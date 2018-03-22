package com.aleax.blight.compiler;

/**
 * Simple logging interface, to allow logging to various implementations.
 * 
 * @author Yiannis Paschalidis
 */
public interface Logger
{
    /**
     * Logs a debugging message.
     * 
     * @param message the message to log.
     */
    void debug(String message);

    /**
     * Logs an informational message.
     * 
     * @param message the message to log.
     */
    void info(String message);

    /**
     * Logs a warning message.
     * 
     * @param message the message to log.
     */
    void warn(String message);

    /**
     * Logs an error message.
     * 
     * @param message the message to log.
     * @param cause the cause of the error.
     */
    void error(String message, Throwable cause);
}
