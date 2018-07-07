package com.aleax.blight.dynamic;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import com.aleax.blight.TemplateManager;
import com.aleax.blight.dynamic.templates.AnonInnerClass;

/**
 * Tests a template with an anonymouse inner class.
 * 
 * @author Yiannis Paschalidis
 */
public class AnonInnerClassTest
{
   @Test
   public void testHelloWorld() throws Exception
   {
      AnonInnerClass template = new AnonInnerClass();

      StringWriter writer = new StringWriter();
      template.setOutput(writer);
      
      TemplateManager.execute(template);

      String output = writer.toString();

      Assert.assertEquals("Incorrect output", "<options><option>1</option><option>2</option><option>3</option></options>", output);
   }
}
