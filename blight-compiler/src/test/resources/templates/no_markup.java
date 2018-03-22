package templates;

import java.io.IOException;

import com.aleax.blight.AbstractTemplate;

/**
 * A template with no embedded mark-up. 
 * The compiler should return the template unmodified.
 * 
 * @author Yiannis Paschalidis
 */
public class NoMarkup extends AbstractTemplate
{
   @Override
   public void execute() throws IOException
   {
      write("Hello World!");
   }
}