package example.mycompany.myapp;

import java.io.IOException;
import java.io.PrintWriter;

import com.aleax.blight.TemplateException;
import com.aleax.blight.TemplateManager;

import example.mycompany.myapp.templates.MyTemplate;

/**
 * The simplest "Hello World" bLight application.
 *
 * @author Yiannis Paschalidis
 */
public class MyApp
{
    /**
     * Application entry point.
     * 
     * @param args command-line arguments, ignored.
     * @throws TemplateException if template execution fails.
     * @throws IOException if there is an error writing template output.
     */
    public static void main(final String[] args) throws TemplateException, IOException
    {
        // Create a new template
        MyTemplate template = new MyTemplate();
        
        // Configure the template to send its output to System.out
        PrintWriter out = new PrintWriter(System.out);
        template.setOutput(out);

        // Compile and execute the template
        TemplateManager.execute(template);

        // Ensure the output is written before the app terminates
        out.flush();
    }
}
