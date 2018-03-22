package com.aleax.blight.compiler.dynamic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

/**
 * The standard JavaFileManager uses a simple implementation of type
 * JavaFileObject to read/write bytecode into class files. This class extends
 * the standard JavaFileManager to read/write bytecode into memory using a
 * custom implementation of the JavaFileObject.
 * 
 * @see JavaMemoryObject
 * 
 * @author Hugo Picado
 * @author Yiannis Paschalidis
 */
public class InMemoryClassManager extends ForwardingJavaFileManager<JavaFileManager>
{
    private final List<JavaMemoryObject> memory = new ArrayList<JavaMemoryObject>();

    public InMemoryClassManager(final JavaFileManager fileManager)
    {
        super(fileManager);
    }

    /** {@inheritDoc} */
    @Override
    public FileObject getFileForInput(final JavaFileManager.Location location, final String packageName,
                                      final String relativeName) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public JavaFileObject getJavaFileForInput(final Location location, final String className,
                                              final JavaFileObject.Kind kind) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public JavaFileObject getJavaFileForOutput(final Location location, final String name, final Kind kind,
                                               final FileObject sibling) throws IOException
    {
        JavaMemoryObject co = new JavaMemoryObject(name, kind);
        memory.add(co);
        return co;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSameFile(final FileObject a, final FileObject b)
    {
        return false;
    }

    /**
     * Gets the bytecode as a list of compiled classes. If the source code generates inner classes, these classes will
     * be placed in front of the returned list and the class associated to the source file will be the last element in
     * the list.
     * 
     * @return List of compiled classes
     * @throws ClassNotFoundException if the class was not found.
     */
    public List<Class<?>> loadAllClasses() throws ClassNotFoundException
    {
        Map<String, byte[]> classes = new HashMap<String, byte[]>(memory.size());

        for (JavaMemoryObject unit : memory)
        {
            classes.put(unit.getClassName(), unit.getClassBytes());
        }

        ByteArrayClassLoader bacl = ByteArrayClassLoader.newInstance(classes);
        List<Class<?>> loadedClasses = new ArrayList<>();

        for (JavaMemoryObject unit : memory)
        {
            Class<?> cls = bacl.loadClass(unit.getClassName());
            loadedClasses.add(cls);
        }

        return loadedClasses;
    }
}
