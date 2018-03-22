package example.mycompany.myapp;

import java.io.File;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * The test harness allows running of the web application directly from an IDE
 * without having to package up the WAR and deploy it.
 *
 * @author Yiannis Paschalidis
 */
public final class TestHarness
{
    /** Prevent external instantiation of this class. */
    private TestHarness()
    {
    }

    /**
     * Runs the server.
     */
    public static void main(final String[] args) throws Exception
    {
        File webAppDir = new File("src/main/webapp".replace('/', File.separatorChar));

        if (!webAppDir.canRead())
        {
            System.err.println("Failed to start server - webapp directory not found."
                               + "\nMake sure the working directory is blight-example-app");
        }

        // Use Jetty to run the servlet
        Server server = new Server();

        SocketConnector connector = new SocketConnector();
        connector.setMaxIdleTime(0);
        connector.setPort(8080);
        server.addConnector(connector);

        server.setHandler(new WebAppContext(webAppDir.getPath(), "/"));
        server.start();

        System.out.println("Example is running on http://localhost:8080/example");
    }
}
