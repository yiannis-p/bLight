package com.aleax.blight.precompiled;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import com.aleax.blight.precompiled.templates.HelloWorld;

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
      template.execute();

      String output = writer.toString();

      Assert.assertEquals("Incorrect output", "Hello World!", output);
   }
}
