package templates;

import java.io.IOException;

import com.aleax.blight.AbstractTemplate;

/**
 * A template with multiple comments.
 *
 * @author Yiannis Paschalidis 
 */
public class HelloWorld extends AbstractTemplate
{
   @Override
   public void execute() throws IOException
   {
      /*Hello*/
      /* World!*/
   }
}