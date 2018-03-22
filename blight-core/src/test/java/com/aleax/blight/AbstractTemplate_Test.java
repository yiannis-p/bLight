package com.aleax.blight;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests for {@link AbstractTemplate}.
 * 
 * @author Yiannis Paschalidis
 */
public class AbstractTemplate_Test
{
    @Test
    public void testSetOutput()
    {
        AbstractTemplate template = new TestTemplate();

        Assert.assertNull("Outuput should be null by default", template.getOutput());

        Writer output = new StringWriter();
        template.setOutput(output);

        Assert.assertSame("Incorrect output", output, template.getOutput());

        Assert.assertEquals("Output should be empty without call to write", "", output.toString());
    }

    @Test
    public void testWriteString() throws IOException
    {
        AbstractTemplate template = new TestTemplate();
        StringWriter output = new StringWriter();
        template.setOutput(output);

        String x = UUID.randomUUID().toString();
        template.write(x);
        output.flush();

        Assert.assertEquals("Incorrect output", x, output.toString());
    }

    @Test
    public void testWriteObj() throws IOException
    {
        AbstractTemplate template = new TestTemplate();
        StringWriter output = new StringWriter();
        template.setOutput(output);

        Object x = UUID.randomUUID();
        template.write(x);
        output.flush();

        Assert.assertEquals("Incorrect output", x.toString(), output.toString());
    }

    @Test
    public void testSetCompiled()
    {
        AbstractTemplate template = new TestTemplate();
        Assert.assertFalse("Should not be compiled by default", template.isCompiled());

        template.setCompiled(true);
        Assert.assertTrue("Should be compiled after setCompiled(true)", template.isCompiled());
    }

    /**
     * Trivial template implementation.
     */
    private static final class TestTemplate extends AbstractTemplate
    {
        @Override
        public void execute() throws IOException
        {
            // Do nothing
        }
    }
}
