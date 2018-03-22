package com.aleax.blight.compiler.dynamic;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

/**
 * A byte array class loader extends the standard class loader to transform an
 * array of bytes into a java.lang.Class.
 * 
 * @author Hugo Picado
 * @author Yiannis Paschalidis
 */
public class ByteArrayClassLoader extends ClassLoader
{
    /** The class bytecode keyed by fully qualified class name. */
    private final Map<String, byte[]> classes;
    
    /** The map of loaded classes. */
    private final Map<String, Class<?>> loadedClasses = new HashMap<String, Class<?>>();

    /**
     * Creates a new ByteArrayClassLoader.
     * 
     * @param classes the class bytecode, keyed by fully qualified class name.
     */
    private ByteArrayClassLoader(final Map<String, byte[]> classes)
    {
        super(ByteArrayClassLoader.class.getClassLoader());
        this.classes = classes;
    }
    
    /** {@inheritDoc} */
    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException
    {
        Class<?> cls = loadedClasses.get(name);
        
        if (cls == null)
        {
            try
            {
                byte[] bytecode = classes.get(name);
    
                if (bytecode != null)
                {
                    cls = defineClass(name, bytecode, 0, bytecode.length);
                    loadedClasses.put(name, cls);
                }
                else
                {
                    cls = super.loadClass(name, resolve);
                }
            }
            catch (ClassFormatError ex)
            {
                throw new ClassNotFoundException("Class name: " + name, ex);
            }
        }
        
        return cls;
    }

    /**
     * Creates a new ByteArrayClassLoader.
     * 
     * @param classes the class bytecode keyed by fully qualified name.
     * @return a new ByteArrayClassLoader.
     */
    public static ByteArrayClassLoader newInstance(final Map<String, byte[]> classes)
    {
        return AccessController.doPrivileged(new PrivilegedAction<ByteArrayClassLoader>()
        {
            @Override
            public ByteArrayClassLoader run()
            {
                return new ByteArrayClassLoader(classes);
            }
        });
    }
}
