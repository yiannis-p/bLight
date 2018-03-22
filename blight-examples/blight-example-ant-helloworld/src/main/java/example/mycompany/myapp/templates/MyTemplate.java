package example.mycompany.myapp.templates;

import java.io.IOException;

import com.aleax.blight.AbstractTemplate;

/**
 * The simplest "Hello World" template.
 *
 * @author Yiannis Paschalidis
 */
public class MyTemplate extends AbstractTemplate
{
    /** {@inheritDoc} */
    @Override
    public void execute() throws IOException
    {
        /*Hello world!*/
    }
}
