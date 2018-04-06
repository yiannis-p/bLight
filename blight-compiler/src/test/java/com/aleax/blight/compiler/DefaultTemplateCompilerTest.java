package com.aleax.blight.compiler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import com.aleax.blight.util.Util;

/**
 * JUnit tests for the {@link DefaultTemplateCompiler}.
 *
 * @author Yiannis Paschalidis
 */
public class DefaultTemplateCompilerTest
{
    /**
     * Blank template with no content. Should return original source.
     */
    @Test
    public void testBlank()
    {
        testTemplate("blank.java", "blank.java");
    }

    /**
     * Blank template with no content. Should strip the comment but not output anything.
     */
    @Test
    public void testEmptyComment()
    {
        testTemplate("empty_comment.java", "empty_comment_compiled.java");
    }

    /**
     * Template with comments containing only whitespace.
     */
    @Test
    public void testWhitespaceComments()
    {
        testTemplate("whitespace_comments.java", "whitespace_comments_compiled.java");
    }
    
    /**
     * Template with content but no embedded markup. Should return original source.
     */
    @Test
    public void testNoMarkup()
    {
        testTemplate("no_markup.java", "no_markup.java");
    }

    /**
     * Not a template. Should return original source.
     */
    @Test
    public void testNoExecuteMethod()
    {
        testTemplate("no_exec_method.java", "no_exec_method.java");
    }

    /**
     * Template with no embedded markup but single-line comments. The comments should be ignored.
     */
    @Test
    public void testNoMarkupSingleLineComments()
    {
        testTemplate("no_markup_single_line_comments.java", "no_markup_single_line_comments.java");
    }

    /** Simple "Hello World" template. */
    @Test
    public void testHelloWorld()
    {
        testTemplate("hello_world.java", "hello_world_compiled.java");
    }

    /** Template with multi-line comments. */
    @Test
    public void testMultiLineComments()
    {
        testTemplate("multi_line_comment.java", "multi_line_comment_compiled.java");
    }

    /** Template with multiple comments. */
    @Test
    public void testMultipleComments()
    {
        testTemplate("multi_comment.java", "multi_comment_compiled.java");
    }

    /**
     * Internal convenience method to test a single template.
     * 
     * @param sourceFile the name of the source template.
     * @param expectedFile the name of the compiled template.
     */
    private void testTemplate(final String sourceFile, final String expectedFile)
    {
        try
        {
            String source = new String(Util.read("templates/" + sourceFile), StandardCharsets.UTF_8);
            String expected = new String(Util.read("templates/" + expectedFile), StandardCharsets.UTF_8);

            DefaultTemplateCompiler compiler = new DefaultTemplateCompiler();
            String compiled = compiler.compileTemplate(source);

            Assert.assertEquals("Incorrect compiler output", expected, compiled);
        }
        catch (IOException e)
        {
            Assert.fail("IOException: " + e.getMessage() + '\n' + Util.stackTraceToString(e));
        }
    }
}
