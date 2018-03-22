package example.mycompany.myapp;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aleax.blight.TemplateManager;

import example.mycompany.myapp.templates.ExampleTemplate;

/**
 * A simple example servlet which just runs a single template.
 *
 * @author Yiannis Paschalidis
 */
public class ExampleServlet extends HttpServlet
{
    /** {@inheritDoc} */
    @Override
    public void init() throws ServletException
    {
        super.init();

        // Disable caching to demonstrate ability to
        // edit templates in the IDE without requiring a restart
        // This will negatively impact application performance.
        TemplateManager.setCachingEnabled(false);
    }

    /** {@inheritDoc} */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException
    {
        try
        {
            // Configure the HTTP response
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);

            // Create a new template
            ExampleTemplate template = new ExampleTemplate();

            // Configure the template to send its output to the browser
            template.setOutput(resp.getWriter());

            // Configure some dynamic text to display.
            template.setMessage("The current server time is " + new Date());

            // Compile and execute the template
            TemplateManager.execute(template);
        }
        catch (Exception e)
        {
            // Error compiling or executing the template.
            // In a real application, you should log this properly.
            e.printStackTrace();

            // Display a basic error response
            // In a real application, you should display something more appropriate.
            // Note that we haven't buffered the template output in the above code.
            // It is therefore possible that content may already have been written
            // to the HTTP response if the failure occurs within the template's
            // execute method.
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Internal server error");
        }
    }
}
