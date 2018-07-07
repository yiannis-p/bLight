package com.aleax.blight.dynamic.templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.aleax.blight.AbstractTemplate;

/**
 * Test mark-up with an anonymouse inner class.
 * 
 * @author Yiannis Paschalidis
 */
public class AnonInnerClass extends AbstractTemplate
{
   /** {@inheritDoc} */
   @Override
   public void execute() throws IOException
   {
      List<String> letters = new ArrayList<String>(Arrays.asList("1", "3", "2"));
      
      Collections.sort(letters, new Comparator<String>()
      {
         @Override
         public int compare(final String o1, final String o2)
         {
            return o1.compareTo(o2);
         }
      });
      
      /*<options>*/
         for (String letter : letters)
         {
            /*<option>*/write(letter);/*</option>*/
         }
      /*</options>*/
   }
}
