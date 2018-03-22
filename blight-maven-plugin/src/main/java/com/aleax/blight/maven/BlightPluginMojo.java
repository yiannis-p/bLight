package com.aleax.blight.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.compiler.CompilationFailureException;
import org.apache.maven.plugin.compiler.CompilerMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.aleax.blight.CompilerSettings;
import com.aleax.blight.TemplateException;
import com.aleax.blight.compiler.BatchCompiler;
import com.aleax.blight.compiler.Logger;

/**
 * bLight MOJO for pre-compiling templates.
 * 
 * @author Yiannis Paschalidis
 */
@Mojo(name = "compileTemplates", requiresDependencyResolution = ResolutionScope.COMPILE, defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class BlightPluginMojo extends CompilerMojo
{
    /** The template packages to parse. */
    @Parameter(required = true)
    private String[] packageNames;

    /** The output directory for the compiled template sources. */
    @Parameter(defaultValue = "${project.build.directory}/compiled-templates")
    private File compiledTemplateDir;

    /** The Maven project. */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject templateProject; // Must not be called "project" otherwise injection into superclass fails.
    
    /** The name of the method to modify. */
    @Parameter
    private String templateMethod;

    /** Prefix to insert before each comment replacement. */
    @Parameter
    private String commentReplacementPrefix;

    /** Suffix to insert after each comment replacement. */
    @Parameter
    private String commentReplacementSuffix;

    /** Sets the extra code to insert in the template. */
    @Parameter
    private String extraCode;
    
    /**
     * Executes the plugin.
     * 
     * @throws MojoExecutionException if there is an error compiling the template files.
     * @throws CompilationFailureException if the compiled templates fail to compile to bytecode.
     */
    @Override
    public void execute() throws MojoExecutionException, CompilationFailureException
    {
        List<String> sourceDirList = templateProject.getCompileSourceRoots();
        File[] sourceDirs = new File[sourceDirList.size()];

        for (int i = 0; i < sourceDirs.length; i++)
        {
           sourceDirs[i] = new File(sourceDirList.get(i));
        }

        CompilerSettings compilerSettings = new CompilerSettings();

        if (templateMethod != null)
        {
           compilerSettings.setTemplateMethod(templateMethod);
        }

        if (commentReplacementPrefix != null)
        {
           compilerSettings.setCommentReplacementPrefix(commentReplacementPrefix);
        }

        if (commentReplacementSuffix != null) 
        {
           compilerSettings.setCommentReplacementSuffix(commentReplacementSuffix);
        }

        if (extraCode != null)
        {
           compilerSettings.setExtraCode(extraCode);
        }
        
        BatchCompiler compiler = new BatchCompiler(compilerSettings, sourceDirs, compiledTemplateDir, packageNames);

        compiler.setLogger(new Logger()
        {
            /** {@inheritDoc} */
            @Override
            public void debug(final String message)
            {
                getLog().debug(message);
            }

            /** {@inheritDoc} */
            @Override
            public void info(final String message)
            {
                getLog().info(message);
            }

            /** {@inheritDoc} */
            @Override
            public void warn(final String message)
            {
                getLog().warn(message);
            }

            /** {@inheritDoc} */
            @Override
            public void error(final String message, final Throwable cause)
            {
                getLog().error(message, cause);
            }
        });
        
        // Run the template compiler 
        try
        {
            compiler.execute();
        }
        catch (IOException | TemplateException e)
        {
            throw new MojoExecutionException("Template compilation failed", e);
        }
        
        // Run the Java compiler
        super.execute();
    }
    
    /** {@inheritDoc} */
    @Override
    protected List<String> getCompileSourceRoots()
    {
        return new ArrayList<String>(Arrays.asList(compiledTemplateDir.getPath()));
    }    
}
