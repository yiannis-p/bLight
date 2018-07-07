/* Generated code - Do not edit. */package templates;

import java.io.IOException;

import com.aleax.blight.AbstractTemplate;

/**
 * A template with no embedded mark-up. 
 * The compiler should still mark the template compiled.
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

	{ setCompiled(true); }
}