package templates;

import java.io.IOException;

import com.aleax.blight.AbstractTemplate;

/**
 * Used to test handling of whitespace.
 *
 * @author Yiannis Paschalidis 
 */
public class EmptyComment extends AbstractTemplate
{
   @Override
   public void execute() throws IOException
   {
      /* */
      /*	*/
      /*
*/
   }
}