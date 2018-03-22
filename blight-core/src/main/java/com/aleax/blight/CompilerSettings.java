package com.aleax.blight;

/**
 * Stores compiler settings and defaults.
 * The default settings are as follows:
 * 
 * <ul>
 *    <li>template method = "<code>execute</code>"</li>
 *    <li>replacement prefix = "<code>write(</code>"</li>
 *    <li>replacement suffix = "<code>);</code>"</li>
 *    <li>extra code = "<code>setCompiled(true);</code>"</li>
 *    <li>target class name suffix = <code>null</code></li>
 * </ul>
 * 
 * @author Yiannis Paschalidis
 */
public class CompilerSettings
{
    /** The name of the method to modify. */
    private String templateMethod = "execute";

    /** Prefix to insert before each comment replacement. */
    private String commentReplacementPrefix = "write(";

    /** Suffix to insert after each comment replacement. */
    private String commentReplacementSuffix = ");";

    /** Sets the extra code to insert in the template. */
    private String extraCode = "setCompiled(true);";

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
     * Retrieves the string suffix to place before comment replacements.
     *
     * @return the prefix to insert.
     */
    public String getCommentReplacementPrefix()
    {
        return commentReplacementPrefix;
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
     * Retrieves the string suffix to place after comment replacements.
     *
     * @return the suffix to insert.
     */
    public String getCommentReplacementSuffix()
    {
        return commentReplacementSuffix;
    }

    /**
     * Sets extra code to insert at the end of each compiled template. 
     * This is placed in an initialiser block at the end of the class.
     *
     * @param extraCode the extra code to insert.
     */
    public void setExtraCode(final String extraCode)
    {
        this.extraCode = extraCode;
    }

    /**
     * Retrieves the extra code to insert at the end of each compiled template. 
     * This is placed in an initialiser block at the end of the class.
     *
     * @return extraCode the extra code to insert.
     */
    public String getExtraCode()
    {
        return extraCode;
    }
}
