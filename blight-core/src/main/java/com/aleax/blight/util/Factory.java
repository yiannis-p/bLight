package com.aleax.blight.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import com.aleax.blight.TemplateException;

/**
 * Arbitrary class Factory, allowing programmatic configuration
 * in addition to using Java's {@link ServiceLoader} utility. 
 * 
 * @author Yiannis Paschalidis
 */
public final class Factory
{
    /** A map of service loaders by class. */
    private static final Map<Class<?>, ServiceLoader<?>> SERVICE_LOADERS = new HashMap<>();

    /** A map of class implementations by class. */
    private static final Map<Class<?>, Class<?>> IMPLEMENTATIONS = new HashMap<>();

    /** Prevent instantiation of this utility class. */
    private Factory()
    {
    }

    /**
     * Programmatically configures the implementation for a particular class/interface.
     * This overrides any previous configuration for the given target. To restore
     * {@link ServiceLoader} behaviour, call this method with <code>impl</code> set to <code>null</code>.
     * 
     * @param target the target class or interface.
     * @param impl the implementation class.
     */
    public static <T> void setImplementation(final Class<T> target, final Class<? extends T> impl)
    {
        IMPLEMENTATIONS.put(target, impl);
    }

    /**
     * Creates a concrete instance of a class given a class/interface. 
     * Uses either the Java {@link ServiceLoader} utility, or programmatic 
     * configuration using {@link #setImplementation(Class, Class)}.
     * 
     * @param clazz the class/interface to find a concrete instance of.
     * @return the loaded class, or null if not found.
     * @throws TemplateException if the class could not be instantiated.
     */
    public static <T> T create(final Class<T> clazz) throws TemplateException
    {
        Class<T> classImpl = (Class<T>) IMPLEMENTATIONS.get(clazz);

        if (classImpl != null)
        {
            try
            {
                return classImpl.newInstance();
            }
            catch (Exception e)
            {
                throw new TemplateException("Failed to initialise class: " + classImpl);
            }
        }

        ServiceLoader<T> loader = (ServiceLoader<T>) SERVICE_LOADERS.get(clazz);

        if (loader == null)
        {
            loader = ServiceLoader.load(clazz);
            SERVICE_LOADERS.put(clazz, loader);
        }

        for (T obj : loader)
        {
            // Just grab the first one found
            return obj;
        }

        return null;
    }
}
