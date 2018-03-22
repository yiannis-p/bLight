package com.aleax.blight.compiler.dynamic;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import com.aleax.blight.TemplateException;

/**
 * The dynamic compiler uses the JavaCompiler with custom implementations of a
 * JavaFileManager and JavaFileObject to compile a Java Source from a String to
 * Bytecode.
 *
 * @see InMemoryClassManager
 * @see JavaSourceFromString
 * @see JavaMemoryObject
 * 
 * @author Hugo Picado
 * @author Yiannis Paschalidis
 */
public class InMemoryCompiler implements com.aleax.blight.JavaCompiler
{
    /** {@inheritDoc} */
    @Override
    public List<Class<?>> compile(final String className, final String code) throws TemplateException
    {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<JavaFileObject>();
        InMemoryClassManager manager = new InMemoryClassManager(compiler.getStandardFileManager(null, null, null));

        String classpath = loadClasspath();

        // Add classpath to options
        List<String> options = Arrays.asList("-classpath", classpath);

        // Java source from string
        List<JavaSourceFromString> strFiles = Arrays.asList(new JavaSourceFromString(className, code));

        // Compile
        CompilationTask task = compiler.getTask(null, manager, collector, options, null, strFiles);
        boolean compilationSuccessful = task.call();

        if (compilationSuccessful)
        {
            try
            {
                return manager.loadAllClasses();
            }
            catch (ClassNotFoundException e)
            {
                throw new TemplateException("Failed to load class: " + e.getMessage());
            }
        }
        else
        {
            String compilationReport = buildCompilationReport(collector, options);
            throw new TemplateException(compilationReport);
        }
    }

    /**
     * Builds a readable report of compilation errors.
     *
     * @param collector the errors.
     * @param options the compiler options which were used.
     * @return the error report.
     */
    private String buildCompilationReport(final DiagnosticCollector<JavaFileObject> collector, final List<String> options)
    {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = collector.getDiagnostics();
        
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("Compilation error\n")
                     .append(diagnostics.size())
                     .append(" class(es) failed to compile\n");

        // group diagnostic by class
        for (Diagnostic<?> diagnostic : diagnostics)
        {
            if (diagnostic.getSource() instanceof JavaSourceFromString)
            {
                JavaSourceFromString javaSource = (JavaSourceFromString) diagnostic.getSource();
                
                resultBuilder.append(javaSource.getCharContent(false)).append("\n")
                    .append("Compiler options: ").append(options).append("\n\n")
                    .append(diagnostic.getKind()).append("|").append(diagnostic.getCode()).append("\n")
                    .append("LINE:COLUMN ")
                    .append(diagnostic.getLineNumber())
                    .append(":")
                    .append(diagnostic.getColumnNumber())
                    .append("\n")
                    .append(diagnostic.getMessage(null))
                    .append("\n\n");
            }
        }

        return resultBuilder.toString();
    }

    /**
     * Builds a class path string from the current classpath.
     * 
     * @return the class path string.
     */
    private String loadClasspath()
    {
        String pathSeparator = System.getProperty("path.separator");
        String classPath = System.getProperty("java.class.path", "");

        Set<String> paths = new HashSet<String>();
        paths.addAll(Arrays.asList(classPath.split(pathSeparator)));

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        if (loader instanceof URLClassLoader)
        {
            for (URL url : ((URLClassLoader) loader).getURLs())
            {
                paths.add(url.getFile());
            }
        }

        StringBuilder sb = new StringBuilder();

        for (String path : paths)
        {
            sb.append(path).append(System.getProperty("path.separator"));
        }

        return sb.toString();
    }
}
