package com.aleax.blight.compiler;

import java.io.File;

/**
  * Stores information used for compiling a template.
 * 
 * @author Yiannis Paschalidis
  */
public final class TemplateFile
{
    /** The source directory which the template was found. */
    private final File sourceDir;

    /** The template package name. */
    private final String packageName;

    /** The template file. */
    private final File sourceFile;

    /**
     * Creates a TemplateFile.
     * 
     * @param sourceDir the source directory where the template was found.
     * @param packageName the template package name.
     * @param sourceFile the template file.
     */
    public TemplateFile(final File sourceDir, final String packageName, final File sourceFile)
    {
        this.sourceDir = sourceDir;
        this.packageName = packageName;
        this.sourceFile = sourceFile;
    }

    /** @return the source directory where the template was found. */
    public File getSourceDir()
    {
        return sourceDir;
    }

    /** @return the template package name. */
    public String getPackageName()
    {
        return packageName;
    }

    /** @return the template file. */
    public File getSourceFile()
    {
        return sourceFile;
    }

    /**
     * @return the relative path to the name of the compiled template file.
     */
    public String getRelativeCompiledTemplatePath()
    {
        return packageName.replace('.', File.separatorChar) + File.separatorChar + sourceFile.getName();
    }
}
