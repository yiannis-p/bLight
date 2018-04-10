package com.aleax.blight;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aleax.blight.util.Factory;
import com.aleax.blight.util.Util;

/**
 * <p>Provides methods to allow templates to be run either pre- or dynamically compiled.</p>
 *
 * <p>Dynamic compilation support has the following requirements/limitations:</p>
 * <ul>
 *    <li>The template source code be on the class path.</li>
 *    <li>The code must be run on a JDK rather than JRE.</li>
 *    <li>Permission must be granted to use the reflection API to access private/final fields.</li>
 *    <li>Template classes must have a public no-args constructor.</li>
 *    <li>Inner classes are not supported.</li>
 *    <li>The security manager must allow the TemplateManager to modify fields within the template via reflection.</li>
 * </ul>
 *
 * <p>
 * <b>Note:</b> dynamic template execution is slower than running a pre-compiled template.
 * It is strongly recommended to pre-compile templates when running in a production environment.
 * This can be achieved by using the various build-tool extensions (e.g. Apache Ant/Maven) or by
 * running the stand-alone BatchCompiler commandline tool.
 * </p>
 *
 * @author Yiannis Paschalidis
 */
public final class TemplateManager
{
    /** A cache of dynamically compiled classes. */
    private static final Map<Class<? extends AbstractTemplate>, CompiledTemplate> TEMPLATE_CACHE = new HashMap<>();

    /** Indicates whether template caching is enabled. */
    private static boolean cachingEnabled = true;

    /** Prevent instantiation of this utility class. */
    private TemplateManager()
    {
    }

    /**
     * Executes the given template. Un-compiled templates will be compiled first.
     * 
     * @param template the template to execute.
     * 
     * @throws TemplateException if the template failed to compile
     * @throws IOException if the template failed to execute.
     */
    public static void execute(final AbstractTemplate template) throws TemplateException, IOException
    {
        if (template != null)
        {
            if (template.isCompiled())
            {
                template.execute();
            }
            else
            {
                CompiledTemplate compiledTemplate = null;
                
                if (cachingEnabled)
                {
                    synchronized (TEMPLATE_CACHE)
                    {
                        compiledTemplate = TEMPLATE_CACHE.get(template.getClass());
                        
                        if (compiledTemplate == null || compiledTemplate.isExpired())
                        {
                            compiledTemplate = compile(template);
                            TEMPLATE_CACHE.put(template.getClass(), compiledTemplate);
                        }
                    }
                }
                else
                {
                    compiledTemplate = compile(template);
                }
                
                AbstractTemplate compiledTemplateInstance;
                
                try
                {
                    compiledTemplateInstance = compiledTemplate.compiledClass.newInstance();
                    compiledTemplate.copyFields(template, compiledTemplateInstance);
                }
                catch (InstantiationException e)
                {
                    throw new TemplateException("Failed to instantiate compiled template", e);
                }
                catch (IllegalAccessException e)
                {
                    throw new TemplateException("Failed to populate compiled template", e);
                }

                // Reset the compiled flag which we've just accidentally cleared above
                compiledTemplateInstance.setCompiled(true);
                compiledTemplateInstance.execute();
            }
        }
    }

    /**
     * Compiles the given template.
     *
     * @param template the template to compile.
     * @return the compiled template.
     * @throws TemplateException if compilation fails.
     */
    private static CompiledTemplate compile(final AbstractTemplate template) throws TemplateException
    {
        try
        {
            Class<? extends AbstractTemplate> templateClass = template.getClass();
            String packageName = templateClass.getPackage().getName();
            String path = templateClass.getName().replace('.', '/') + ".java";
            String source = new String(Util.read(path), StandardCharsets.UTF_8);
            
            TemplateCompiler templateCompiler = Factory.create(TemplateCompiler.class);
            
            if (templateCompiler == null)
            {
                throw new TemplateException("No template compiler found");
            }
            
            CompilerSettings settings = new CompilerSettings();
            templateCompiler.setCompilerSettings(settings);
            String compiledTemplate = templateCompiler.compileTemplate(source);
            
            JavaCompiler javaCompiler = Factory.create(JavaCompiler.class);
            
            if (javaCompiler == null)
            {
                throw new TemplateException("No java compiler found");
            }
            
            List<Class<?>> classes = javaCompiler.compile(packageName + '.' + templateClass.getSimpleName(), compiledTemplate);
            
            return new CompiledTemplate(path, templateClass, (Class<? extends AbstractTemplate>) classes.get(0));
        }
        catch (Exception e)
        {
            throw new TemplateException("Failed to compile template: " + template, e);
        }
    }

    /**
     * Sets whether template caching is enabled. Caching is enabled by default.
     * 
     * @param cachingEnabled true to enable caching, false to disable.
     */
    public static void setCachingEnabled(final boolean cachingEnabled)
    {
        TemplateManager.cachingEnabled = cachingEnabled;
    }

    /**
     * Indicates whether template caching is enabled.
     * 
     * @return true if caching is enabled, false otherwise.
     */
    public static boolean isCachingEnabled()
    {
        return cachingEnabled;
    }

    /**
     * Cache entry for compiled templates.
     */
    private static final class CompiledTemplate
    {
        /** The original template class. */
        private final Class<? extends AbstractTemplate>  originalClass;
        
        /** The original template class fields. */
        private final Field[] originalFields;
        
        /** The compiled template class. */
        private final Class<? extends AbstractTemplate>  compiledClass;
        
        /** The compiled template class fields. */
        private final Field[] compiledFields;
        
        /** The file or jar archive containing the template source. */
        private final File source;
        
        /** The timestamp for when this cache entry was created. */
        private final long createTime = System.currentTimeMillis();

        /**
         * Creates a CompiledTemplate.
         * 
         * @param path the resource path for the source code.
         * @param originalClass the original class.
         * @param compiledClass the compiled class.
         * 
         * @throws TemplateException if the cached entry could not be created.
         */
        CompiledTemplate(final String path, final Class<? extends AbstractTemplate> originalClass, final Class<? extends AbstractTemplate> compiledClass) throws TemplateException
        {
            this.originalClass = originalClass;
            this.compiledClass = compiledClass;
            
            // Find template source
            try
            {
                URL resourceLocation = Thread.currentThread().getContextClassLoader().getResource(path);
                File file = null;

                if ("jar".equals(resourceLocation.getProtocol()))
                {
                    resourceLocation = ((JarURLConnection) resourceLocation.openConnection()).getJarFileURL();
                }
                
                if ("file".equals(resourceLocation.getProtocol()))
                {
                    file = new File(resourceLocation.toURI());
                }
                
                source = file;
            }
            catch (Exception e)
            {
                throw new TemplateException("Failed to determine template file path for: " + originalClass.getName(), e);
            }
            
            // Map fields. Have to loop to get fields from superclass as well.
            final List<Field> sourceList = new ArrayList<Field>();
            final List<Field> destList = new ArrayList<Field>();
            
            TemplateException mappingResult = AccessController.doPrivileged(new PrivilegedAction<TemplateException>()
            {
                @Override
                public TemplateException run()
                {
                    try
                    {
                        Class<?> sourceClass = originalClass;
                        Class<?> destClass = compiledClass;
                        
                        while (sourceClass != null)
                        {
                            Field[] sourceFields = sourceClass.getDeclaredFields();
                            Field[] destFields = destClass.getDeclaredFields();

                            for (Field sourceField : sourceFields)
                            {
                                int mods = sourceField.getModifiers();
                                
                                if (!Modifier.isStatic(mods) && !Modifier.isFinal(mods))
                                {
                                    for (Field destField : destFields)
                                    {
                                        if (sourceField.getName().equals(destField.getName()))
                                        {
                                            sourceField.setAccessible(true);
                                            destField.setAccessible(true);
                                            sourceList.add(sourceField);
                                            destList.add(destField);
                                            break;
                                        }
                                    }
                                }
                            }

                            sourceClass = sourceClass.getSuperclass();
                            destClass = destClass.getSuperclass();
                        }
                    }
                    catch (Exception e)
                    {
                        return new TemplateException("Failed to compile template", e);
                    }

                    return null;
                }
            });

            if (mappingResult != null)
            {
                throw mappingResult;
            }
            
            originalFields = sourceList.toArray(new Field[sourceList.size()]);
            compiledFields = destList.toArray(new Field[destList.size()]);
        }
        
        /**
         * Copies fields from one template to another.
         * 
         * @param source the source template.
         * @param dest the destination template.
         * @throws IllegalAccessException if there are any errors copying fields.
         */
        void copyFields(final Object source, final Object dest) throws IllegalAccessException
        {
            for (int i = 0; i < originalFields.length; i++)
            {
                compiledFields[i].set(dest, originalFields[i].get(source));
            }
        }

        /**
         * Indicates whether the cached template has expired due to the source file changing.
         * @return true if the cache entry has expired, false otherwise.
         */
        boolean isExpired()
        {
            return source != null && source.lastModified() > createTime;
        }
    }
}
