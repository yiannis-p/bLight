package com.aleax.blight.util;

import org.junit.Assert;
import org.junit.Test;

import com.aleax.blight.TemplateException;

/**
 * JUnit tests for {@link Factory}.
 * 
 * @author Yiannis Paschalidis
 */
public class Factory_Test
{
    @Test
    public void testCreateServiceLoader() throws TemplateException
    {
        A a = Factory.create(A.class);
        Assert.assertTrue("Incorrect impl", a instanceof B);
    }

    @Test
    public void testCreateProgrammatic() throws TemplateException
    {
        Factory.setImplementation(A.class, C.class);

        A a = Factory.create(A.class);
        Assert.assertTrue("Incorrect impl", a instanceof C);

        // Restore back to default
        Factory.setImplementation(A.class, null);

        a = Factory.create(A.class);
        Assert.assertFalse("Incorrect impl", a instanceof C);
    }

    @Test
    public void testMissingImpl() throws TemplateException
    {
        Assert.assertNull("Missing impl should return null", Factory.create(Factory.class));
    }

    /**
     * Dummy interface for testing.
     */
    public interface A
    {
    }

    /**
     * Dummy interface for testing.
     */
    public static class B implements A
    {
    }

    /**
     * Dummy interface for testing.
     */
    public static class C implements A
    {
    }
}
