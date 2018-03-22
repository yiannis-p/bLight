package com.aleax.blight;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests for {@link TemplateManager}.
 * 
 * @author Yiannis Paschalidis
 */
public class TemplateManager_Test
{
    @Test
    public void testSetCachingEnabled()
    {
        Assert.assertFalse("Caching should be disabled by default", TemplateManager.isCachingEnabled());

        TemplateManager.setCachingEnabled(true);
        Assert.assertTrue("Caching should be enabled", TemplateManager.isCachingEnabled());
    }
}
