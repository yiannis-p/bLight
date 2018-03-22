package com.aleax.blight.compiler.dynamic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

import com.aleax.blight.util.Util;

/**
 * Wraps bytecode into memory.
 * 
 * The JavaCompiler uses the StandardFileManager to perform read/write bytecode
 * on files of type JavaFileObject. This class extends this functionality by
 * reading/writing bytecode into memory instead of files.
 * 
 * @author Hugo Picado
 * @author Yiannis Paschalidis
 */
public class JavaMemoryObject implements JavaFileObject
{
    private final ByteArrayOutputStream bos;
    private final String className;
    private final URI uri;
    private final Kind kind;

    /**
     * Creates a JavaMemoryObject.
     * 
     * @param className the fully qualified class name.
     * @param fileKind the file kind.
     */
    public JavaMemoryObject(final String className, final Kind fileKind)
    {
        this.className = className;
        this.uri = URI.create("string:///" + className.replace('.', '/') + fileKind.extension);
        this.kind = fileKind;
        bos = new ByteArrayOutputStream();
    }

    /** @return the compiled bytecode. */
    public byte[] getClassBytes()
    {
        return bos.toByteArray();
    }

    /** {@inheritDoc} */
    @Override
    public final URI toUri()
    {
        return uri;
    }

    /** {@inheritDoc} */
    @Override
    public final String getName()
    {
        return uri.getPath();
    }

    /** @return the fully qualified clas name. */
    public String getClassName()
    {
        return className;
    }

    /** {@inheritDoc} */
    @Override
    public final InputStream openInputStream() throws IOException
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public final OutputStream openOutputStream() throws IOException
    {
        return bos;
    }

    /** {@inheritDoc} */
    @Override
    public final Reader openReader(final boolean ignoreEncodingErrors) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public final CharSequence getCharContent(final boolean ignoreEncodingErrors) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public final Writer openWriter() throws IOException
    {
        return new OutputStreamWriter(openOutputStream());
    }

    /** {@inheritDoc} */
    @Override
    public final long getLastModified()
    {
        return 0L;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean delete()
    {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public final Kind getKind()
    {
        return kind;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isNameCompatible(final String simpleName, final Kind fileKind)
    {
        String baseName = simpleName + kind.extension;
        return fileKind.equals(getKind())
               && (baseName.equals(toUri().getPath()) || toUri().getPath().endsWith("/" + baseName));
    }

    /** {@inheritDoc} */
    @Override
    public final NestingKind getNestingKind()
    {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public final Modifier getAccessLevel()
    {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public final String toString()
    {
        return getClass().getName() + "[" + toUri() + "]";
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o)
    {
        return o instanceof JavaMemoryObject
                && Util.equals(uri, ((JavaMemoryObject) o).uri)
                && Util.equals(kind, ((JavaMemoryObject) o).kind);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return uri == null ? 0 : uri.hashCode();
    }
}
