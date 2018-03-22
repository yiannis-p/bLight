package com.aleax.blight.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * General utility methods.
 *
 * @author Yiannis Paschalidis
 */
public final class Util
{
    /** Prevent instantiation of this utility class. */
    private Util()
    {
    }

    /**
     * Reads a file into a byte array.
     * 
     * @param path the path to the file.
     * @return the file contents.
     * @throws IOException if there is an error reading the file.
     */
    public static byte[] readFile(final String path) throws IOException
    {
        File file = new File(path);
        
        if (!file.exists())
        {
            throw new IOException("File not found: " + path);
        }
        
        if (!file.canRead())
        {
            throw new IOException("Can not read file: " + path);
        }
        
        return read(new FileInputStream(file));
    }

    /**
     * Reads a resource into a byte array.
     * 
     * @param path the resource path.
     * @return the resource contents.
     * @throws IOException if there is an error reading the resource.
     */
    public static byte[] read(final String path) throws IOException
    {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        
        if (stream == null)
        {
            throw new IOException("Resource not found: " + path);
        }
        
        return read(stream);
    }

    /**
     * Completely reads a stream into an array. Closes the stream on completion.
     * 
     * @param in the input stream.
     * @return the read content.
     * @throws IOException if there is an error reading data.
     */
    public static byte[] read(final InputStream in) throws IOException
    {
        byte[] buf = new byte[4096];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try
        {
            for (int len = in.read(buf); len != -1; len = in.read(buf))
            {
                baos.write(buf, 0, len);
            }
        }
        finally
        {
            safeClose(in);
        }

        return baos.toByteArray();
    }

    /**
     * Null-safe version of equals.
     *
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return true if o1 == o2, or o1.equals(o2)
     */
    public static boolean equals(final Object o1, final Object o2)
    {
        if (o1 == o2)
        {
            return true;
        }
        else if (o1 != null)
        {
            return o1.equals(o2);
        }

        return false;
    }

    /**
     * Checks whether the given String is blank.
     * 
     * @param string the String to check.
     * @return true if the string is blank.
     */
    public static boolean isBlank(final String string)
    {
        if (string != null)
        {
            final int len = string.length();

            for (int i = 0; i < len; i++)
            {
                if (string.charAt(i) > 32)
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks wheter the given String is null or empty.
     * 
     * @param string the String to check.
     * @return true if the string is empty.
     */
    public static boolean isEmpty(final String string)
    {
        return string == null || string.length() == 0;
    }

    /**
     * Closes a resource, ignoring any exceptions.
     * 
     * @param closeable the closeable to close. Null safe.
     */
    public static void safeClose(final Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch (IOException ignored)
            {
            }
        }
    }

    /**
     * Return a String representation of a stack-trace.
     * 
     * @param t the throwable to get the stack-trace of.
     * @return the throwable's stack-trace.
     */
    public static String stackTraceToString(final Throwable t)
    {
        if (t == null)
        {
            return "";
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);

        pw.flush();
        String trace = sw.toString();

        safeClose(pw);
        safeClose(sw);

        return trace;
    }

    /**
     * Obtains the end of line character sequence for the given text.
     * 
     * @param text the text to get the marker for.
     * @return the EOL marker
     */
    public static final String getEOLMarker(final String text)
    {
        if (isEmpty(text) || text.indexOf('\n') == -1)
        {
            return System.getProperty("line.separator"); // best guess - just use OS encoding
        }
        else if (text.indexOf("\r\n") != -1)
        {
            return "\r\n"; // MSDOS
        }
        else
        {
            return "\n"; // Unix
        }
    }
}
