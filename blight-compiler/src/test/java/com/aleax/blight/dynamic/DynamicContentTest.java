package com.aleax.blight.dynamic;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.aleax.blight.TemplateManager;
import com.aleax.blight.dynamic.templates.DynamicContent;

/**
 * Test mark-up with dynamic content.
 * 
 * @author Yiannis Paschalidis
 */
public class DynamicContentTest
{
    @Test
    public void runTest() throws Exception
    {
        DynamicContent template = new DynamicContent();
        template.setName("World");
        template.setNumbers(Arrays.asList(1, 2, 3));

        StringWriter writer = new StringWriter();
        template.setOutput(writer);
        TemplateManager.execute(template);

        String output = writer.toString();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        builder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));

        Document doc = builder.parse(new InputSource(new StringReader(output)));

        Assert.assertEquals("Incorrect name", "Hello World!",
                            XPathFactory.newInstance().newXPath().evaluate("/html/body/p/text()", doc));

        Assert.assertEquals("Incorrect number of numbers", "3",
                            XPathFactory.newInstance().newXPath().evaluate("count(/html/body/ul/li)", doc));

        Assert.assertEquals("Incorrect first number", "1",
                            XPathFactory.newInstance().newXPath().evaluate("/html/body/ul/li[1]/text()", doc));

        Assert.assertEquals("Incorrect second number", "2",
                            XPathFactory.newInstance().newXPath().evaluate("/html/body/ul/li[2]/text()", doc));

        Assert.assertEquals("Incorrect third number", "3",
                            XPathFactory.newInstance().newXPath().evaluate("/html/body/ul/li[3]/text()", doc));
    }
}
