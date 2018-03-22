package com.aleax.blight.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests for {@link Util}.
 * 
 * @author Yiannis Paschalidis
 */
public class Util_Test
{
    @Test
    public void testReadFile() throws IOException
    {
        String path = "src.test.resources.".replace('.', File.separatorChar) + "test_resource.txt";
        byte[] data = Util.readFile(path);

        Assert.assertNotNull("data should not be null", data);
        Assert.assertEquals("Incorrect data", "TEST", new String(data, StandardCharsets.UTF_8));
    }

    @Test
    public void testReadPath() throws IOException
    {
        byte[] data = Util.read("test_resource.txt");
        Assert.assertNotNull("data should not be null", data);
        Assert.assertEquals("Incorrect data", "TEST", new String(data, StandardCharsets.UTF_8));
    }

    @Test
    public void testReadStream() throws IOException
    {
        byte[] data = Util.read(getClass().getResourceAsStream("/test_resource.txt"));
        Assert.assertNotNull("data should not be null", data);
        Assert.assertEquals("Incorrect data", "TEST", new String(data, StandardCharsets.UTF_8));
    }

    @Test
    public void testSafeClose()
    {
        // Should not throw an exception
        Util.safeClose(null);
    }

    @Test
    public void testEquals()
    {
        Assert.assertTrue("Null + null", Util.equals(null, null));
        Assert.assertFalse("Null + non-null", Util.equals(null, "X"));
        Assert.assertFalse("Non-null + null", Util.equals("X", null));
        Assert.assertFalse("X + Y", Util.equals("X", "Y"));
        Assert.assertTrue("X + X", Util.equals("X", new String(new char[] { 'X' })));
    }

    @Test
    public void testIsBlank()
    {
        Assert.assertTrue("Null", Util.isBlank(null));
        Assert.assertTrue("Empty", Util.isBlank(""));
        Assert.assertTrue("Space", Util.isBlank(" "));
        Assert.assertTrue("Newline", Util.isBlank("\n"));
        Assert.assertTrue("Tab", Util.isBlank("\t"));
        Assert.assertFalse("Non empty", Util.isBlank("x"));
        Assert.assertFalse("Non empty with spaces", Util.isBlank(" x "));
    }

    @Test
    public void testIsEmpty()
    {
        Assert.assertTrue("Null", Util.isEmpty(null));
        Assert.assertTrue("Empty", Util.isEmpty(""));
        Assert.assertFalse("Space", Util.isEmpty(" "));
    }

    @Test
    public void testStackTraceToString()
    {
        Assert.assertEquals("Null throwable should return empty string", "", Util.stackTraceToString(null));

        String trace = Util.stackTraceToString(new Throwable());
        Assert.assertTrue("Stack trace should contain test method", trace.indexOf("testStackTraceToString") != -1);
    }

    @Test
    public void getEOLMarker()
    {
        String osMarker = System.getProperty("line.separator");
        Assert.assertEquals("Default marker should be os marker", osMarker, Util.getEOLMarker(null));
        Assert.assertEquals("Default marker should be os marker", osMarker, Util.getEOLMarker(""));
        Assert.assertEquals("Default marker should be os marker", osMarker, Util.getEOLMarker("x"));

        Assert.assertEquals("Marker should be \\r\\n", "\r\n", Util.getEOLMarker("x\r\ny"));
        Assert.assertEquals("Marker should be \\n", "\n", Util.getEOLMarker("x\ny"));
    }
}
