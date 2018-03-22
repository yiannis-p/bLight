package com.aleax.blight.dynamic.templates;

import java.io.IOException;

import com.aleax.blight.AbstractTemplate;

/**
 * Tests the simplest "Hello World" template.
 * 
 * @author Yiannis Paschalidis
 */
public class HelloWorld extends AbstractTemplate
{
   /** Should print "Hello World!" when compiled and executed. */
   @Override
   public void execute() throws IOException
   {
      // Single line comments should be ignored      
      ///* Single line comments should be ignored */
      
      /*Hello World!*/
   }
}