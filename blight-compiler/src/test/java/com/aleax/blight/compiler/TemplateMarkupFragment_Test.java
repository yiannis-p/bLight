package com.aleax.blight.compiler;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests for {@link TemplateMarkupFragment}.
 * 
 * @author Yiannis Paschalidis
 */
public class TemplateMarkupFragment_Test
{
    @Test
    public void getId()
    {
        String id = "TemplateMarkupFragment_Test__ID";
        Assert.assertEquals("Incorrect id", id, new TemplateMarkupFragment(id, "x").getId());
    }

    @Test
    public void testGetLineCount()
    {
        Assert.assertEquals("Incorrect line count", 1, new TemplateMarkupFragment("x", "line1").getLineCount());
        Assert.assertEquals("Incorrect line count", 2, new TemplateMarkupFragment("x", "\nline2").getLineCount());
        Assert.assertEquals("Incorrect line count", 2, new TemplateMarkupFragment("x", "line1\n").getLineCount());
        Assert.assertEquals("Incorrect line count", 2, new TemplateMarkupFragment("x", "line2\nline2").getLineCount());
        Assert.assertEquals("Incorrect line count", 2, new TemplateMarkupFragment("x", "line2\r\nline2").getLineCount());
        Assert.assertEquals("Incorrect line count", 3, new TemplateMarkupFragment("x", "line1\nline2\n").getLineCount());
    }

    @Test
    public void testGetLiteral()
    {
        String markup = "line1\r\nline2";
        String literal = "\"line1\\r\\nline2\"";
        Assert.assertEquals("Incorrect id", literal, new TemplateMarkupFragment("x", markup).getLiteral());
    }
}
