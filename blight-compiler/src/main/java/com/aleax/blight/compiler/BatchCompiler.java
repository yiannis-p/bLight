package com.aleax.blight.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.aleax.blight.CompilerSettings;
import com.aleax.blight.TemplateCompiler;
import com.aleax.blight.TemplateException;
import com.aleax.blight.util.Factory;
import com.aleax.blight.util.Util;

/**
 * <p>Command-line tool to compile a batch of templates.</p>
 * 
 * <p>Can also be invoked programmatically by creating a new instance and calling
 * {@link #execute()}.</p>
 * 
 * @author Yiannis Paschalidis
 */
public class BatchCompiler
{
    /** Modes for reading command-line args. */
    private enum ArgMode
    {
        SOURCE_DIRS("-s", "source dir"), 
        PACKAGE_NAMES("-p", "package name"), 
        OUTPUT_DIR("-o", "output dir"), 
        COMPILER_METHOD_NAME("-method", "method name"), 
        COMPILER_PREFIX("-prefix", "replacement prefix"), 
        COMPILER_SUFFIX("-suffix", "replacement suffix"), 
        COMPILER_EXTRA_CODE("-code", "extra code");

        /** The command-line argument text. */
        private final String desc;

        /** The command-line argument text. */
        private final String switchText;

        /**
         * Creates an ArgMode.
         * 
         * @param switchText the command-line switch.
         * @param desc the short description.
         */
        private ArgMode(final String switchText, final String desc)
        {
            this.switchText = switchText;
            this.desc = desc;
        }

        /**
         * Retrieves an ArgMode by its command-line switch.
         * 
         * @param switchText the command-line switch text.
         * @return the ArgMode for the given text, or null if the text was invalid.
         */
        public static ArgMode get(final String switchText)
        {
            for (ArgMode mode : values())
            {
                if (mode.switchText.equals(switchText))
                {
                    return mode;
                }
            }

            return null;
        }
    }

    /** Java source file filter. */
    private static final FilenameFilter JAVA_FILE_FILTER = (dir, name) -> name.endsWith(".java");

    /** The source directories. */
    private final File[] sourceDirs;

    /** The output directory for the compiled template sources. */
    private final File outputDir;

    /** The template packages to parse. */
    private final String[] packageNames;

    /** The compiler settings. */
    private final CompilerSettings settings;

    /** The logger to use for logging messages. */
    private Logger logger = new DefaultLogger();

    /**
     * Creates a BatchCompiler.
     * 
     * @param settings the compiler settings to use.
     * @param sourceDirs the source directories to search for templates in.
     * @param outputDir the directory to place the compiled templates sources in.
     * @param packageNames the fully qualified names of the packages to compile.
     */
    public BatchCompiler(final CompilerSettings settings, final File[] sourceDirs, final File outputDir, final String[] packageNames)
    {
        this.settings = settings;
        this.sourceDirs = sourceDirs;
        this.outputDir = outputDir;
        this.packageNames = packageNames;
    }

    /**
     * Sets the logger to use.
     * 
     * @param logger the logger to set.
     */
    public void setLogger(final Logger logger)
    {
        this.logger = logger;
    }

    /**
     * Compiles the templates and places them in the output directory tree.
     * 
     * @throws IOException on read/write error
     * @throws TemplateException on compilation error
     */
    public void execute() throws IOException, TemplateException
    {
        logger.info("BatchCompiler.execute()");

        if (packageNames == null || packageNames.length == 0)
        {
            logger.warn("No packages specified - skipping execution");
            return;
        }

        if (sourceDirs == null || sourceDirs.length == 0)
        {
            logger.warn("No source directories specified - skipping execution");
            return;
        }

        if (outputDir == null)
        {
            logger.warn("No output directory specified - skipping execution");
            return;
        }

        // Compile the templates
        compileTemplates(outputDir);
    }

    /**
     * Compiles sources.
     * 
     * @param targetTemplateDir the target directory to place the compiled template source.
     * @throws IOException if there was an error reading/writing template files.
     * @throws TemplateException if there was an error compiling template files.
     */
    private void compileTemplates(final File targetTemplateDir) throws IOException, TemplateException
    {
        List<TemplateFile> sourceFiles = findTemplates();

        if (sourceFiles.isEmpty())
        {
            logger.info("No source files found");
            return;
        }

        logger.info("Compiling " + sourceFiles.size() + " templates to " + targetTemplateDir.getPath());

        if (!targetTemplateDir.exists() && !targetTemplateDir.mkdirs())
        {
            throw new IOException("Failed to create template directory: " + targetTemplateDir.getPath());
        }

        for (TemplateFile file : sourceFiles)
        {
            try
            {
                compileTemplate(file);
            }
            catch (IOException | TemplateException e)
            {
                throw new TemplateException("Failed to compile template " + file.getSourceFile().getPath(), e);
            }
        }
    }

    /**
     * Compiles a single template.
     * 
     * @param file the template to compile.
     * @throws IOException if there was an error reading the template file.
     * @throws TemplateException if there was an error compiling the template.
     */
    private void compileTemplate(final TemplateFile file) throws IOException, TemplateException
    {
        logger.debug("Compiling template " + file.getRelativeCompiledTemplatePath());

        FileInputStream fis = new FileInputStream(file.getSourceFile());
        String source = new String(Util.read(fis), StandardCharsets.UTF_8);
        fis.close();

        TemplateCompiler templateCompiler = Factory.create(TemplateCompiler.class);

        if (templateCompiler == null)
        {
            templateCompiler = new DefaultTemplateCompiler();
        }

        templateCompiler.setCompilerSettings(settings);
        source = templateCompiler.compileTemplate(source);

        File compiledTemplateFile = new File(outputDir, file.getRelativeCompiledTemplatePath());
        File parentDir = compiledTemplateFile.getParentFile();

        if (!parentDir.exists() && !parentDir.mkdirs())
        {
            throw new IOException("Failed to create template directory: " + parentDir.getPath());
        }

        FileOutputStream fos = new FileOutputStream(compiledTemplateFile);
        fos.write(source.getBytes(StandardCharsets.UTF_8));
        fos.close();
    }

    /**
     * Finds all source files for the project.
     * 
     * @return the source files for the project. Will not be null, but may be empty.
     */
    private List<TemplateFile> findTemplates()
    {
        List<TemplateFile> sourceFiles = new ArrayList<TemplateFile>();

        for (File dir : sourceDirs)
        {
            if (dir.canRead())
            {
                findSourceFiles(dir, sourceFiles);
            }
        }

        return sourceFiles;
    }

    /**
     * Files all source files to compile within a single source directory.
     * 
     * @param sourceDir the source directory.
     * @param templateFiles the list to add files to.
     */
    private void findSourceFiles(final File sourceDir, final List<TemplateFile> templateFiles)
    {
        for (String packageName : packageNames)
        {
            File dir = new File(sourceDir, packageName.replace('.', File.separatorChar));

            if (dir.canRead())
            {
                File[] sourceFiles = dir.listFiles(JAVA_FILE_FILTER);

                if (sourceFiles != null)
                {
                    for (File sourceFile : sourceFiles)
                    {
                        if (sourceFile.canRead())
                        {
                            templateFiles.add(new TemplateFile(sourceDir, packageName, sourceFile));
                        }
                    }
                }
            }
        }
    }

    /**
     * Command-line entry point.
     * 
     * @param args the command-line arguments
     */
    public static void main(final String[] args)
    {
        if (args.length == 0)
        {
            usage(null);
        }

        CompilerSettings compilerSettings = new CompilerSettings();
        List<File> sourceDirs = new ArrayList<File>();
        List<String> packages = new ArrayList<String>();
        File destDir = null;
        ArgMode argMode = null;

        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];

            ArgMode newMode = ArgMode.get(arg);

            if (newMode != null)
            {
                argMode = newMode;
            }
            else
            {
                switch (argMode)
                {
                    case SOURCE_DIRS:
                    {
                        sourceDirs.add(new File(arg));
                        break;
                    }
                    case PACKAGE_NAMES:
                    {
                        // Basic sanity checking for the package name
                        if (!Pattern.matches("^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_]$", arg))
                        {
                            usage("Invalid package name: " + arg);
                        }

                        packages.add(arg);
                        break;
                    }
                    case OUTPUT_DIR:
                    {
                        if (destDir == null)
                        {
                            destDir = new File(arg);
                            argMode = null;
                        }
                        else
                        {
                            usage("Only one output directory allowed");
                        }

                        break;
                    }
                    default:
                    {
                        usage("Invalid argument: " + arg);
                    }
                }
            }
        }

        // Check for missing args
        if (argMode != null)
        {
            switch (argMode)
            {
                case COMPILER_EXTRA_CODE:
                case COMPILER_PREFIX:
                case COMPILER_METHOD_NAME:
                case COMPILER_SUFFIX:
                case OUTPUT_DIR:
                    usage("Missing value for " + argMode.desc);
                    break;
            }
        }

        if (sourceDirs.isEmpty())
        {
            usage("No source directories specified");
        }

        if (packages.isEmpty())
        {
            usage("No java package names specified");
        }

        if (destDir == null)
        {
            usage("No destination directory specified");
        }

        File[] sourceDirArray = sourceDirs.toArray(new File[sourceDirs.size()]);
        String[] packageArray = packages.toArray(new String[packages.size()]);
      
        System.out.println("Batch compile templates: " 
                         + "\n   Source dirs: " + sourceDirs
                         + "\n   Packages: " + packages
                         + "\n   Output dir: " + destDir.getPath());

        try
        {
            BatchCompiler compiler = new BatchCompiler(compilerSettings, sourceDirArray, destDir, packageArray);
            compiler.execute();
        }
        catch (IOException | TemplateException e)
        {
            System.err.println("Failed to compile sources: " + e.getMessage());
            e.printStackTrace();
        }
    }
   
    /**
     * Displays the command-line usage message and exists.
     * 
     * @param message an additional message to display.
     */
    private static final void usage(final String message)
    {
        if (message != null)
        {
            System.err.println(message);
        }

        CompilerSettings defaultSettings = new CompilerSettings();

        System.err.println
        (
              "Basic usage:\n"
            + "\n    java " + BatchCompiler.class.getName() + " " 
            + ArgMode.SOURCE_DIRS.switchText + " <source dirs...> "
            + ArgMode.PACKAGE_NAMES.switchText + " <package names...> "
            + ArgMode.OUTPUT_DIR.switchText + " <output dir> "
            + "\n\nOptional compiler settings:"
            + "\n"
            + "\n    " + ArgMode.COMPILER_METHOD_NAME.switchText + " <method name>   -- The template method name, default \"" + defaultSettings.getTemplateMethod() + '"'
            + "\n    " + ArgMode.COMPILER_PREFIX.switchText + " <prefix>        -- replacement prefix, default \"" + defaultSettings.getCommentReplacementPrefix() + '"'
            + "\n    " + ArgMode.COMPILER_SUFFIX.switchText + " <suffix>        -- replacement suffix, default \"" + defaultSettings.getCommentReplacementSuffix() + '"'
            + "\n    " + ArgMode.COMPILER_EXTRA_CODE.switchText + " <code>            -- extra code to insert, default \"" + defaultSettings.getExtraCode() + '"'
        );

        System.exit(1);
    }
}
