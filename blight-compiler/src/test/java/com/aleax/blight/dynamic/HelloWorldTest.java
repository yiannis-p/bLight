package com.aleax.blight.dynamic;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import com.aleax.blight.TemplateManager;
import com.aleax.blight.dynamic.templates.HelloWorld;

/**
 * Tests a simple "Hello World" template.
 * 
 * @author Yiannis Paschalidis
 */
public class HelloWorldTest
{
   @Test
   public void testHelloWorld() throws Exception
   {
      HelloWorld template = new HelloWorld();

      StringWriter writer = new StringWriter();
      template.setOutput(writer);
      
      TemplateManager.execute(template);

      String output = writer.toString();

      Assert.assertEquals("Incorrect output", "Hello World!", output);
   }
}
