package com.aleax.blight.dynamic.templates;

import java.io.IOException;
import java.util.List;

import com.aleax.blight.AbstractTemplate;
import com.aleax.blight.util.Util;

/**
 * Test mark-up with dynamic content.
 * 
 * @author Yiannis Paschalidis
 */
public class DynamicContent extends AbstractTemplate
{
   /** The name to display. */
   private String name;
   
   /** The numbers to display. */
   private List<Integer> numbers;
   
   /**
    * Sets the name to display.
    * @param name the name to display.
    */
   public void setName(final String name)
   {
      this.name = name;
   }
   
   /**
    * Sets the numbers to display. 
    * @param numbers the numbers to display.
    */
   public void setNumbers(final List<Integer> numbers)
   {
      this.numbers = numbers;
   }

   /** {@inheritDoc} */
   @Override
   public void execute() throws IOException
   {/*
      <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

      <html xmlns="http://www.w3.org/1999/xhtml">
         <body>
             <p>Hello */writeEscaped(name);/*!</p>

             <ul>*/
             
                 for (Integer i : numbers)
                 {
                     /*<li>*/write(i);/*</li>*/
                 }
                 
             /*</ul>
                 
         </body>
     </html>
   */}
   
   /**
    * Writes the given String to the output, html-escaped.
    * 
    * @param s the string to write.
    * @throws IOException if there is an error writing to the output.
    */
   private void writeEscaped(final String s) throws IOException
   {
       if (!Util.isEmpty(s))
       {
           final int len = s.length();
           int lastPos = 0; // position of unwritten content.
           String replacement = null;

           for (int i = 0; i < len; i++)
           {
               switch (s.charAt(i))
               {
                   case '&':
                       replacement = "&amp;";
                       break;

                   case '<':
                       replacement = "&lt;";
                       break;

                   case '>':
                       replacement = "&gt;";
                       break;
               }

               // Write any unwritten content and the replacement
               if (replacement != null)
               {
                   if (lastPos < i)
                   {
                       super.write(s.substring(lastPos, i));
                   }

                   super.write(replacement);
                   lastPos = i + 1;

                   replacement = null;
               }
           }

           // Write any unwritten content
           if (lastPos < len)
           {
               super.write(s.substring(lastPos, len));
           }
       }
   }   
}
