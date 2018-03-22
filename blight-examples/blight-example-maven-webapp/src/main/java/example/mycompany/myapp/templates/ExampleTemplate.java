package example.mycompany.myapp.templates;

import java.io.IOException;

import com.aleax.blight.AbstractTemplate;
import com.aleax.blight.TemplateException;

/**
 * Example template.
 *
 * @author Yiannis Paschalidis
 */
public class ExampleTemplate extends AbstractTemplate
{
    /** Demonstration of basic dynamic content. */
    private String message;

    /**
     * Sets the message to display.
     * 
     * @param message the message to set.
     */
    public void setMessage(final String message)
    {
        this.message = message;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() throws IOException, TemplateException
    {
        /*<!DOCTYPE html>
       <html>
            <head>
               <link rel="stylesheet" type="text/css" href="/css/example.css"/>
               <title>bLight example</title>
            </head>
            <body>
                <h1>Example bLight template</h1>

                <p>This source for this template is located at
                <code>src/main/com/aleax/blight/example/webapp/templates/ExampleTemplate.java</code></p>

                <p>When running from the TestHarness class, you can edit the
                template file and reload the page to see the changes without
                having to restart the application. Note that changes outside
                of the <code>execute()</code> method will not be picked up.</p>

                <p>The following text is dynamic and is set by the <code>ExampleServlet</code>.<br/>
                */write(message);/*</p>
            </body>
        </html>*/
     }
}
