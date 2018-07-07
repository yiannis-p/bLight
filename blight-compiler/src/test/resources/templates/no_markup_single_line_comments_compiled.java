/* Generated code - Do not edit. */package templates;

import java.io.IOException;

import com.aleax.blight.AbstractTemplate;

/**
 * A template with no embedded mark-up but single-line comments.
 * The single-line comments should be ignored and the compiler 
 * should return the template unmodified.
 * 
 * @author Yiannis Paschalidis
 */
public class NoMarkup extends AbstractTemplate
{
   @Override
   public void execute() throws IOException
   {
      // Single line comments should not get compiled
      // /* Single line comments should not get compiled */
      write("Hello World!");
   }

	{ setCompiled(true); }
}