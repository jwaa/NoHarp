/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.model;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 *
 * @author silvandeleemput
 */
public class Block {
    
    private Vector3f dimensions;
    private Vector3f position;
    private boolean lifted;
    private float rotation;
    
    public Block(Vector3f position, Vector3f dimensions)
    {
        this.position = position;
        this.dimensions = dimensions.mult(0.5f);
    }
    
     /**
     * Calculates Block point intersection
     * Assumes point is in same coordinate system as block 
     * Assumes that there is only rotation around the y-axis
     * @param p point
     * @return if the point is contained by the block
     */
    public boolean collidesWith(Vector3f p)
    {
        Vector3f diff = position.subtract(p);
        if (FastMath.abs(diff.y) < dimensions.y/2 
                && FastMath.sqrt(diff.x*diff.x + diff.z*diff.z) < dimensions.x * 1.5)
        {
            diff = new Vector3f(
                diff.x*FastMath.cos(rotation) - diff.z*FastMath.sin(rotation), 
                diff.y, 
                diff.x*FastMath.sin(rotation) + diff.z*FastMath.cos(rotation));
            return (FastMath.abs(diff.x) < dimensions.x/2 && FastMath.abs(diff.z) < dimensions.z/2);
        }
        return false;
    }
    
    public void setLifted(boolean lifted)
    {
        this.lifted = lifted;
    }
    
    public boolean isLifted()
    {
        return lifted;
    }
    
    public Vector3f getPosition()
    {
        return position;
    }
    
    public Vector3f getDimensions()
    {
        return dimensions;
    }
    
    public float getRotation()
    {
        return rotation;
    }
    
    public void setRotation(float rotation)
    {
        this.rotation = rotation;
    }
    
    public void setPosition(Vector3f position)
    {
        this.position = position;
    }
}
