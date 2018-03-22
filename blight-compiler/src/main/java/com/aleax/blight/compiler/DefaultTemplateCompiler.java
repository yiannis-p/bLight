package com.aleax.blight.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.aleax.blight.CompilerSettings;
import com.aleax.blight.TemplateCompiler;
import com.aleax.blight.util.Util;

/**
 * The default {@link TemplateCompiler} implementation for bLight.
 *
 * The default settings are as follows:
 * 
 * <ul>
 *    <li>template method = "<code>execute</code>"</li>
 *    <li>replacement prefix = "<code>write(</code>"</li>
 *    <li>replacement suffix = "<code>);</code>"</li>
 *    <li>target class name = original class name</li>
 *    <li>extra code = "<code>setCompiled(true);</code>"</li>
 * </ul>
 *
 * @author Yiannis Paschalidis
 */
public class DefaultTemplateCompiler implements TemplateCompiler
{
    /** The compiler settings. */
    private CompilerSettings settings = new CompilerSettings();

    /** {@inheritDoc} */
    @Override
    public void setCompilerSettings(final CompilerSettings settings)
    {
        this.settings = settings;
    }

    /** {@inheritDoc} */
    @Override
    public String compileTemplate(final String source)
    {
        CompilationUnit cu = parse(source);

        // Find the target method
        MethodDeclaration methodNode = findTargetMethod(cu);

        if (methodNode == null)
        {
            // No template method found
            return source;
        }

        final int methodStart = methodNode.getStartPosition();
        final int methodEnd = methodStart + methodNode.getLength();
        final int[] classEnd = new int[1];

        // Find the end of the class enclosing that method.
        for (ASTNode parent = methodNode.getParent(); parent != null; parent = parent.getParent())
        {
            if (parent instanceof TypeDeclaration)
            {
                classEnd[0] = parent.getStartPosition() + parent.getLength();
                break;
            }
        }

        final List<BlockComment> comments = new ArrayList<BlockComment>();

        for (Comment comment : (List<Comment>) cu.getCommentList())
        {
            comment.accept(new ASTVisitor()
            {
                @Override
                public boolean visit(final BlockComment node)
                {
                    // Only accept comments within the target method
                    if (node.getStartPosition() > methodStart && node.getStartPosition() < methodEnd)
                    {
                        comments.add(node);
                        node.getAlternateRoot().delete();
                    }

                    return super.visit(node);
                }
            });
        }

        // Sort comments in the order that they appear in the source.
        Collections.sort(comments, (o1, o2) -> o1.getStartPosition() - o2.getStartPosition());

        // Create constants
        List<TemplateMarkupFragment> fragments = new ArrayList<TemplateMarkupFragment>();
        Map<BlockComment, TemplateMarkupFragment> fragmentsByComment = new HashMap<BlockComment, TemplateMarkupFragment>();
        Map<String, TemplateMarkupFragment> fragmentsByCommentText = new HashMap<String, TemplateMarkupFragment>();

        for (BlockComment comment : comments)
        {
            int start = comment.getStartPosition();
            int end = start + comment.getLength();

            String commentText = source.substring(start + 2, end - 2);
            TemplateMarkupFragment fragment = fragmentsByCommentText.get(commentText);

            if (fragment == null)
            {
                String commentId = "MARKUP_LITERAL_" + (fragments.size() + 1);
                fragment = new TemplateMarkupFragment(commentId, commentText);

                fragments.add(fragment);
                fragmentsByCommentText.put(commentText, fragment);
            }

            fragmentsByComment.put(comment, fragment);
        }

        if (comments.isEmpty())
        {
            // Nothing to do
            return source;
        }

        String eol = Util.getEOLMarker(source);

        StringBuilder buf = new StringBuilder(source.length());
        buf.append("/* Generated code - Do not edit. */");

        int lastCommentEnd = 0;

        for (BlockComment comment : comments)
        {
            int start = comment.getStartPosition();
            int end = comment.getStartPosition() + comment.getLength();
            TemplateMarkupFragment fragment = fragmentsByComment.get(comment);

            buf.append(source.substring(lastCommentEnd, start))
               .append(settings.getCommentReplacementPrefix())
               .append(fragment.getId())
               .append(settings.getCommentReplacementSuffix());

            // Insert blank lines in multi-line comments to make sure line numbers still match up
            if (fragment.getLineCount() > 1)
            {
                for (int i = 1; i < fragment.getLineCount(); i++)
                {
                    buf.append(eol);
                }
            }

            lastCommentEnd = end;
        }

        buf.append(source.substring(lastCommentEnd, classEnd[0] - 1));

        // Extra code is inserted at the bottom of the class as a best effort to not affect line numbers.

        // Define constants
        for (TemplateMarkupFragment fragment : fragments)
        {
            buf.append(eol);

            buf.append("\tprivate static final String ")
               .append(fragment.getId())
               .append(" = ")
               .append(fragment.getLiteral())
               .append(';');
        }

        if (!Util.isBlank(settings.getExtraCode()))
        {
            buf.append(eol).append("\t{ ").append(settings.getExtraCode()).append(" }");
        }

        buf.append(eol);
        buf.append(source.substring(classEnd[0] - 1));

        return buf.toString();
    }

    /**
     * Parses the given source into an AST.
     * 
     * @param source the source to parse.
     * @return the parsed AST.
     */
    private CompilationUnit parse(final String source)
    {
        org.eclipse.jface.text.Document document = new org.eclipse.jface.text.Document(source);
        ASTParser parser = ASTParser.newParser(AST.JLS4);
        parser.setSource(document.get().toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        return (CompilationUnit) parser.createAST(null);
    }

    /**
     * Finds the target template method.
     * 
     * @param cu the compilation unit to search for the method.
     * @return the method delcaration for the method, or null if not found.
     */
    private MethodDeclaration findTargetMethod(final CompilationUnit cu)
    {
        final MethodDeclaration[] methodNodes = new MethodDeclaration[1];

        cu.accept(new ASTVisitor()
        {
            @Override
            public boolean visit(final MethodDeclaration node)
            {
                String name = node.getName().getFullyQualifiedName();

                if (settings.getTemplateMethod().equals(name))
                {
                    methodNodes[0] = node;
                }

                // Don't visit the method body
                return false;
            }
        });

        return methodNodes[0];
    }
}
