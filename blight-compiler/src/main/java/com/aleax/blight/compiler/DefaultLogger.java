package com.aleax.blight.compiler;

/**
 * Default Logger implementation. Just sends the messages to stdout.
 * 
 * @author Yiannis Paschalidis.
 */
public class DefaultLogger implements Logger
{
    /** {@inheritDoc} */
    @Override
    public void debug(final String message)
    {
        System.out.println("DEBUG: " + message);
    }

    /** {@inheritDoc} */
    @Override
    public void info(final String message)
    {
        System.out.println("INFO: " + message);
    }

    /** {@inheritDoc} */
    @Override
    public void warn(final String message)
    {
        System.out.println("WARN: " + message);
    }

    /** {@inheritDoc} */
    @Override
    public void error(final String message, final Throwable cause)
    {
        System.out.println("ERROR: " + message);

        if (cause != null)
        {
            cause.printStackTrace(System.out);
        }
    }
}
