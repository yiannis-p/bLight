package com.aleax.blight;

import java.io.IOException;
import java.io.Writer;

/**
 * Abstract template implementations. All templates must extend this class.
 * 
 * <p>Template instances are not thread-safe.</p> 
 * 
 * @author Yiannis Paschalidis
 */
public abstract class AbstractTemplate
{
    /** The writer which will receive the generated content. */
    private Writer writer;

    /** Indicates whether the template has been compiled. */
    private boolean compiled;

    /**
     * Sets the writer which will receive the generated content.
     * 
     * @param writer the writer to set.
     */
    public void setOutput(final Writer writer)
    {
        this.writer = writer;
    }

    /** @return the output stream which will receive the generated content. */
    public Writer getOutput()
    {
        return writer;
    }

    /**
     * Writes the given text to the output. Null safe.
     * 
     * @param text the text to write.
     * @throws IOException if there is an error writing the text.
     */
    protected void write(final String text) throws IOException
    {
        if (text != null)
        {
            writer.write(text);
        }
    }

    /**
     * Writes the String value of the given object to the output. Null safe.
     * 
     * @param obj the objet to write.
     * @throws IOException if there is an error writing the object.
     */
    protected void write(final Object obj) throws IOException
    {
        if (obj != null)
        {
            writer.write(String.valueOf(obj));
        }
    }

    /**
     * Sets the compiled flag. Not intended for external use.
     * 
     * @param compiled the flag to set.
     */
    protected void setCompiled(final boolean compiled)
    {
        this.compiled = compiled;
    }
    
    /**
     * Executes another template and includes its output.
     * @param template the template to include.
     * @throws TemplateException if the template fails to compile.
     * @throws IOException if the template fails to write output.
     */
    protected void include(final AbstractTemplate template) throws TemplateException, IOException
    {
        if (template != null)
        {
            template.setOutput(getOutput());
            TemplateManager.execute(template);
        }
    }

    /**
     * Indicates whether the template has been compiled.
     * 
     * @return true if the template has been compiled, false otherwise.
     */
    public final boolean isCompiled()
    {
        return compiled;
    }

    /**
     * Executes the template.
     * 
     * @throws TemplateException if the template fails to execute.
     * @throws IOException if there was an error writing to the output.
     */
    public abstract void execute() throws TemplateException, IOException;
}
