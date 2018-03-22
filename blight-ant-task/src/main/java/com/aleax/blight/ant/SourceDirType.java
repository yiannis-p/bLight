package com.aleax.blight.ant;

/**
 * SourceDir type. Required by Ant for nested elements.
 * 
 * @author Yiannis Paschalidis
 */
public final class SourceDirType
{
    /** The path to the source directory. */
    private String path;

    /**
     * Called by Ant to set the source directory.
     * 
     * @param path the path to the source directory.
     */
    public void addText(final String path)
    {
        this.setPath(path);
    }

    /**
     * @return the source directory path.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sets the source directory path.
     * 
     * @param path the path to set.
     */
    public void setPath(final String path)
    {
        this.path = path;
    }
}
