package com.aleax.blight;

/**
 * Compiles a template containing a mix of Java &amp; mark-up in comments to plain Java code.
 *
 * @author Yiannis Paschalidis
 */
public interface TemplateCompiler
{
    /**
     * Sets the compiler settings.
     * 
     * @param settings the compiler settings.
     */
    void setCompilerSettings(CompilerSettings settings);

    /**
     * Compiles a template.
     *
     * @param source the template source.
     * @return the compiled source.
     */
    String compileTemplate(String source);
}
