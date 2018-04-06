package com.aleax.blight.compiler;

/**
 * Represents a fragment of mark-up in a template file.
 * 
 * @author Yiannis Paschalidis
 */
public final class TemplateMarkupFragment
{
    /** The fragment identifier. */
    private final String id;

    /** The mark-up represented as a Java String literal. */
    private final String literal;

    /** The number of lines in the original mark-up. */
    private final int lineCount;

    /**
     * Creates a TemplateMarkupFragment.
     * 
     * @param id the fragment identifier.
     * @param markup the raw mark-up.
     */
    public TemplateMarkupFragment(final String id, final String markup)
    {
        this.id = id;

        final int len = markup.length();
        int lineFeedCount = 0;
        StringBuilder buf = new StringBuilder(markup.length());

        buf.append('"');

        for (int i = 0; i < len; i++)
        {
            char c = markup.charAt(i);

            switch (c)
            {
                case '"':
                    buf.append("\\\"");
                    break;

                case '\t':
                   buf.append("\\t");
                   break;

                case '\r':
                    buf.append("\\r");
                    break;

                case '\n':
                    buf.append("\\n");
                    lineFeedCount++;
                    break;

                case '\\':
                    buf.append("\\\\");
                    break;

                default:
                    buf.append(c);
                    break;
            }
        }

        buf.append("\"");

        literal = buf.toString();
        lineCount = lineFeedCount + 1;
    }

    /** @return the fragment identifier. */
    public String getId()
    {
        return id;
    }

    /** @return the number of lines in the original mark-up. */
    public int getLineCount()
    {
        return lineCount;
    }

    /** @return the mark-up represented as a Java String literal. */
    public String getLiteral()
    {
        return literal;
    }
}
