/* Generated code - Do not edit. */package templates;

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
      write(MARKUP_LITERAL_1);
      write(MARKUP_LITERAL_2);
   }

	private static final String MARKUP_LITERAL_1 = "Hello";
	private static final String MARKUP_LITERAL_2 = " World!";
	{ setCompiled(true); }
}