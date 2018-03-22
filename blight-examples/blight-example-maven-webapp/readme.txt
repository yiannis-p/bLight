---------------------------
blight-example-maven-webapp
---------------------------

This example demonstrates a minimal web application using bLight with Maven.

To compile the example application, run:

    mvn package
	
After compilation the WAR file can be deployed to a web container.

Alternatively, the TestHarness class can be run within an IDE. It runs the
application in an embedded Jetty container. The harness will print out
the application URL when run, for example:

    Example is running on http://localhost:8080/example