package com.aleax.blight;

import java.util.List;

/**
 * Used to compile Java templates to Java classes.
 *
 * @author Yiannis Paschalidis
 */
public interface JavaCompiler
{
    /**
     * Compiles a single class.
     *
     * @param className the fully qualified class name
     * @param code the code to compile
     * @return The compiled class, including any inner classes
     * @throws TemplateException Thrown when a compiler exception occurs
     */
    List<Class<?>> compile(String className, String code) throws TemplateException;
}
