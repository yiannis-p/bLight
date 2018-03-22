package com.aleax.blight;

/**
 * Thrown to indicate an error during template processing.
 *
 * @author Yiannis Paschalidis
 */
public class TemplateException extends Exception
{
    /**
     * Creates a TemplateException.
     *
     * @param message a message describing the error.
     */
    public TemplateException(final String message)
    {
        super(message);
    }

    /**
     * Creates a TemplateException.
     *
     * @param message a message describing the error.
     * @param cause the underlying cause of the error.
     */
    public TemplateException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
