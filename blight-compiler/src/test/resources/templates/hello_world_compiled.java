/* Generated code - Do not edit. */package templates;

import java.io.IOException;

import com.aleax.blight.AbstractTemplate;

/**
 * A basic "Hello world" template.
 *
 * @author Yiannis Paschalidis 
 */
public class HelloWorld extends AbstractTemplate
{
   @Override
   public void execute() throws IOException
   {
      write(MARKUP_LITERAL_1);
   }

	private static final String MARKUP_LITERAL_1 = "Hello World!";
	{ setCompiled(true); }
}