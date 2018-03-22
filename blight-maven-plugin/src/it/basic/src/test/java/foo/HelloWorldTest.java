package foo; 

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import foo.HelloWorld;

/**
 * Simple "Hello World" test to ensure that the Maven plugin has compiled the code correctly.
 * 
 * @author Yiannis Paschalidis
*/
public class HelloWorldTest
{
    @Test
    public void test() throws Exception
    {
        HelloWorld helloWorld = new HelloWorld();
       
        Assert.assertTrue("Should be compiled", helloWorld.isCompiled());
        
        StringWriter sw = new StringWriter();
        helloWorld.setOutput(sw);
        helloWorld.execute();
        
        Assert.assertEquals("Incorrect output", "Hello World!", sw.toString());
    }
}
