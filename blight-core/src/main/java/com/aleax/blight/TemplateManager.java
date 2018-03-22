package com.aleax.blight;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
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
// TODO: cache invalidation
public final class TemplateManager
{
    /** A cache of dynamically compiled classes. */
    private static final Map<Class<? extends AbstractTemplate>, Class<? extends AbstractTemplate>> TEMPLATE_CACHE = new HashMap<>();

    /** Indicates whether template caching is enabled. */
    private static boolean cachingEnabled = false;

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
                Class<? extends AbstractTemplate> compiledTemplateClass = null;

                if (cachingEnabled)
                {
                    compiledTemplateClass = TEMPLATE_CACHE.get(template.getClass());

                    if (compiledTemplateClass == null)
                    {
                        compiledTemplateClass = compile(template);
                        TEMPLATE_CACHE.put(template.getClass(), compiledTemplateClass);
                    }
                }
                else
                {
                    compiledTemplateClass = compile(template);
                }

                AbstractTemplate compiledTemplate;
                
                try
                {
                    compiledTemplate = compiledTemplateClass.newInstance();
                }
                catch (InstantiationException | IllegalAccessException e)
                {
                    throw new TemplateException("Failed to instantiate copied template", e);
                }
                
                Exception copyResult = AccessController.doPrivileged(new PrivilegedAction<Exception>()
                {
                    @Override
                    public Exception run()
                    {
                        try
                        {
                            copyFields(template, compiledTemplate);
                        }
                        catch (Exception e)
                        {
                            return e;
                        }

                        return null;
                    }
                });

                if (copyResult != null)
                {
                    throw new TemplateException("Failed to populate template", copyResult);
                }

                // Reset the compiled flag which we've just accidentally cleared above
                compiledTemplate.setCompiled(true);
                compiledTemplate.execute();
            }
        }
    }

    /**
     * Copies fields from one template to another.
     *
     * @param source the source template.
     * @param dest the destination template.
     * @throws Exception if there is any error copying fields.
     */
    private static void copyFields(final AbstractTemplate source, final AbstractTemplate dest) throws Exception
    {
        Class<?> sourceClass = source.getClass();
        Class<?> destClass = dest.getClass();

        // Have to loop to get fields from superclass as well.
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
                            destField.set(dest, sourceField.get(source));
                        }
                    }
                }
            }

            sourceClass = sourceClass.getSuperclass();
            destClass = destClass.getSuperclass();
        }
    }

    /**
     * Compiles the given template.
     *
     * @param template the template to compile.
     * @return the compiled template.
     * @throws TemplateException if compilation fails.
     */
    private static Class<? extends AbstractTemplate> compile(final AbstractTemplate template) throws TemplateException
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
            
            return (Class<? extends AbstractTemplate>) classes.get(0);
        }
        catch (Exception e)
        {
            throw new TemplateException("Failed to compile template: " + template, e);
        }
    }

    /**
     * Sets whether template caching is enabled. Caching is disabled by default, as on-demand compilation is intended
     * for local development only.
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
}
