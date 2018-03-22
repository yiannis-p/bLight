package com.aleax.blight.compiler.dynamic;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import com.aleax.blight.util.Util;

/**
 * Stores Java source sourceCode from a String into a JavaFileObject.
 * 
 * This class is responsible for creating a Java File Object from a String
 * containing the Java source sourceCode.
 * 
 * @author Hugo Picado
 * @author Yiannis Paschalidis
 */
public class JavaSourceFromString extends SimpleJavaFileObject
{
   /** The source code. */
    private final String sourceCode;

    /** The fully qualified class name. */
    private final String className;

    /**
     * Creates a JavaSourceFromString.
     * 
     * @param className the fully qualified class name.
     * @param sourceCode the source code.
     */
    public JavaSourceFromString(final String className, final String sourceCode)
    {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.className = className;
        this.sourceCode = sourceCode;
    }

    /** {@inheritDoc} */
    @Override
    public final CharSequence getCharContent(final boolean ignoreEncodingErrors)
    {
        return sourceCode;
    }

    /** {@inheritDoc} */
    @Override
    public final String getName()
    {
        return className;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o)
    {
        return o instanceof JavaSourceFromString 
                && Util.equals(className, ((JavaSourceFromString) o).className)
                && Util.equals(sourceCode, ((JavaSourceFromString) o).sourceCode);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return className == null ? 0 : className.hashCode();
    }
}
