/* Generated code - Do not edit. */package templates;

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
      write(MARKUP_LITERAL_1);
      write(MARKUP_LITERAL_2);
      write(MARKUP_LITERAL_3);

   }

	private static final String MARKUP_LITERAL_1 = " ";
	private static final String MARKUP_LITERAL_2 = "\t";
	private static final String MARKUP_LITERAL_3 = "\r\n";
	{ setCompiled(true); }
}