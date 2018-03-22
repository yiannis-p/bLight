-------------------------------
blight-example-maven-helloworld
-------------------------------

This example demonstrates a minimal "Hello World" application using bLight with Maven.

To compile the example application, run:

    mvn package
	
After compilation, you can execute the example application with:

    mvn exec:java -Dexec.mainClass="example.mycompany.myapp.MyApp"  
    
This should print out "Hello World!".