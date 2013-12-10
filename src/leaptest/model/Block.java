/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.model;

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
