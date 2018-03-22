package com.aleax.blight.ant;

/**
 * Package type. Required by Ant for nested elements.
 * 
 * @author Yiannis Paschalidis
 */
public final class PackageType
{
    /** The fully qualified package name. */
    private String name;

    /**
     * Called by Ant to set the package name.
     * 
     * @param name the fully qualified package name.
     */
    public void addText(final String name)
    {
        this.setName(name);
    }

    /**
     * @return the package name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the package name.
     * 
     * @param name the name to set.
     */
    public void setName(final String name)
    {
        this.name = name;
    }
}
