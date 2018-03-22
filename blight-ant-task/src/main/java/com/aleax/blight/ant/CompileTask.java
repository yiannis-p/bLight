package com.aleax.blight.ant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.aleax.blight.CompilerSettings;
import com.aleax.blight.TemplateException;
import com.aleax.blight.compiler.BatchCompiler;
import com.aleax.blight.compiler.Logger;
import com.aleax.blight.util.Util;

/**
 * Template compile Ant task.
 * 
 * @author Yiannis Paschalidis
 */
public final class CompileTask extends Task
{
    /** The source paths to search for templates in. */
    private final List<String> sourcePaths = new ArrayList<String>();

    /** The packages containing templates. */
    private final List<String> packageNames = new ArrayList<String>();

    /** The template output directory. */
    private File outputDir;

    /** The name of the method to modify. */
    private String templateMethod;

    /** Prefix to insert before each comment replacement. */
    private String commentReplacementPrefix;

    /** Suffix to insert after each comment replacement. */
    private String commentReplacementSuffix;

    /** Sets the extra code to insert in the template. */
    private String extraCode;

    /**
     * Adds the given source directory to the list of sources.
     * 
     * @param dir the directory to add.
     */
    public void addConfiguredSourceDir(final SourceDirType dir)
    {
        sourcePaths.add(getProject().replaceProperties(dir.getPath()));
    }

    /**
     * Adds the given package to the list of packages to compile.
     * 
     * @param pkg the package to add.
     */
    public void addConfiguredPackage(final PackageType pkg)
    {
        packageNames.add(getProject().replaceProperties(pkg.getName()));
    }

    /**
     * Sets the directory where the compiled templates are placed.
     * 
     * @param outputDirectory the directory to set.
     */
    public void setOutputDir(final String outputDirectory)
    {
        this.outputDir = new File(outputDirectory);
    }

    /**
     * Sets the name of the template method. Only this method will have comments replaced.
     *
     * @param methodName the name of the method to modify.
     */
    public void setTemplateMethod(final String methodName)
    {
        this.templateMethod = methodName;
    }

    /**
     * Retrieves the name of the template method. Only this method will have comments replaced.
     *
     * @return the name of the method to modify.
     */
    public String getTemplateMethod()
    {
        return templateMethod;
    }

    /**
     * Sets the string suffix to place before comment replacements.
     *
     * @param commentReplacementPrefix the prefix to insert.
     */
    public void setCommentReplacementPrefix(final String commentReplacementPrefix)
    {
        this.commentReplacementPrefix = commentReplacementPrefix;
    }

    /**
     * Sets the string suffix to place after comment replacements.
     *
     * @param commentReplacementSuffix the suffix to insert.
     */
    public void setCommentReplacementSuffix(final String commentReplacementSuffix)
    {
        this.commentReplacementSuffix = commentReplacementSuffix;
    }

    /**
     * Sets extra code to insert at the end of each compiled template. This is placed in an initialiser block at the end
     * of the class.
     *
     * @param extraCode the extra code to insert.
     */
    public void setExtraCode(final String extraCode)
    {
        this.extraCode = extraCode;
    }

    /**
     * Executes the task.
     * 
     * @throws BuildException if there is an error executing the task.
     */
    @Override
    public void execute() throws BuildException
    {
        String[] packages = packageNames.toArray(new String[packageNames.size()]);
        CompilerSettings compilerSettings = new CompilerSettings();
        File[] sourceDirs = new File[sourcePaths.size()];

        for (int i = 0; i < sourceDirs.length; i++)
        {
            sourceDirs[i] = new File(sourcePaths.get(i));
        }

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

        BatchCompiler compiler = new BatchCompiler(compilerSettings, sourceDirs, outputDir, packages);

        compiler.setLogger(new Logger()
        {
            /** {@inheritDoc} */
            @Override
            public void debug(final String message)
            {
                getProject().log(message, Project.MSG_DEBUG);
            }

            /** {@inheritDoc} */
            @Override
            public void info(final String message)
            {
                getProject().log(message, Project.MSG_INFO);
            }

            /** {@inheritDoc} */
            @Override
            public void warn(final String message)
            {
                getProject().log(message, Project.MSG_WARN);
            }

            /** {@inheritDoc} */
            @Override
            public void error(final String message, final Throwable cause)
            {
                getProject().log(message + '\n' + Util.stackTraceToString(cause), Project.MSG_ERR);
            }
        });

        try
        {
            compiler.execute();
        }
        catch (IOException | TemplateException e)
        {
            throw new BuildException("Template compilation failed", e);
        }
    }
}
