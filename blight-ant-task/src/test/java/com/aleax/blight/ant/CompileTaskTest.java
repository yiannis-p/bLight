package com.aleax.blight.ant;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Test;

import com.aleax.blight.util.Util;

/**
 * Junit Tests for the Ant {@link CompileTask}.
 * 
 * @author Yiannis Paschalidis
 */
public class CompileTaskTest
{
    /** The test template package name. */
    private static final String TEMPLATE_PACKAGE_NAME = "com.aleax.blight.ant.templates";

    /** The relative path to the template. */
    private static final String RELATIVE_TEMPLATE_PATH = TEMPLATE_PACKAGE_NAME.replace('.', File.separatorChar) + File.separatorChar + "HelloWorldAnt.java";

    /**
     * Test direct invocation of the task using Java.
     * 
     * @throws Exception on error.
     */
    @Test
    public void testDirectInvocation() throws Exception
    {
        Project project = new Project();
        project.setName("Test project");

        // Create a temporary output directory to place the compiled template.
        File outputDir = Files.createTempDirectory("blightTest").toFile();
        outputDir.deleteOnExit();

        PackageType pkg = new PackageType();
        pkg.addText(TEMPLATE_PACKAGE_NAME);

        SourceDirType sourceDir = new SourceDirType();
        sourceDir.addText("src.test.java".replace('.', File.separatorChar));

        CompileTask task = new CompileTask();
        task.setProject(project);
        task.setOutputDir(outputDir.getPath());
        task.addConfiguredPackage(pkg);
        task.addConfiguredSourceDir(sourceDir);

        task.execute();

        File outputFile = new File(outputDir, RELATIVE_TEMPLATE_PATH);
        Assert.assertTrue("File not found", outputFile.canRead());

        String contents = new String(Util.readFile(outputFile.getPath()), StandardCharsets.UTF_8);
        Assert.assertTrue("Template should have been compiled", contents.startsWith("/* Generated code - Do not edit. */"));
    }

    /**
     * <p>
     * Test invocation of the task using Ant invocation. This tests both the Java code and the XML definition of the
     * task in the "template-ant.xml".
     * </p>
     * <p>
     * The Ant task is invoked during a Maven build for this module. See the call to the maven-antrun-plugin in the
     * pom.xml.
     * </p>
     * 
     * @throws Exception on error.
     */
    @Test
    public void testAntInvocation() throws Exception
    {
        File outputDir = new File("target.compiled-templates".replace('.', File.separatorChar));
        File outputFile = new File(outputDir, RELATIVE_TEMPLATE_PATH);

        Assert.assertTrue("File not found", outputFile.canRead());

        String contents = new String(Util.readFile(outputFile.getPath()), StandardCharsets.UTF_8);
        Assert.assertTrue("Template should have been compiled", contents.startsWith("/* Generated code - Do not edit. */"));
    }
}
