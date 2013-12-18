/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.model;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author silvandeleemput
 */
public class Block extends Geometry implements Comparable {
    
    private Vector3f dimensions;
    private Vector3f position;
    private boolean lifted, falling, dissolving;
    private float rotation, gravity;
    
    public Block(Material mat, Vector3f position, Vector3f dimensions)
    {
        super("Block", new Box(dimensions.x*0.5f,dimensions.y*0.5f,dimensions.z*0.5f));
        this.setMaterial(mat);
        this.setLocalTranslation(position);       
        this.position = position;
        this.dimensions = dimensions;
    }
    
    public boolean isInside(Vector3f point)
    {
        Vector3f diff = (point.subtract(position));
        return (FastMath.abs(diff.y) < dimensions.y/2 && 
                FastMath.abs(diff.x) < dimensions.x/2 && 
                FastMath.abs(diff.z) < dimensions.z/2);
    }
    
    public boolean isDissolving()
    {
        return dissolving;
    }
    
    public void setDissolving(boolean dissolving)
    {
        this.dissolving = dissolving;
        this.setMaterial(material.clone());
        this.material.setColor("Color",((ColorRGBA) (this.material.getParam("Color").getValue())).clone());
        this.material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        this.setQueueBucket(RenderQueue.Bucket.Transparent);
    }
    
    public float getAlpha()
    {
        return ((ColorRGBA) this.material.getParam("Color").getValue()).a;
    }
    
    public void setAlpha(float alpha)
    {
        if (alpha<=0)
            alpha=0;
        else if (alpha>=1)
            alpha=1;
        ((ColorRGBA) this.material.getParam("Color").getValue()).a = alpha;
    }
    
    public float getDeltaY()
    {
        return position.y + gravity;
    }
    
    public void setFalling(boolean falling)
    {
        this.falling = falling;
        if (!falling)
            gravity = 0;
    }
    
    public float getGravity()
    {
        return gravity;
    }
    
    public void setGravity(float gravity)
    {
        this.gravity = gravity;
    }
    
    public boolean isFalling()
    {
        return falling;
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
        this.setLocalRotation(Quaternion.IDENTITY);
        this.rotate(0, rotation, 0);
        this.rotation = rotation;
    }
    
    public void setPosition(Vector3f position)
    {
        this.setLocalTranslation(position);
        this.position = position;
    }

    public int compareTo(Object o) {
        if (o instanceof Block)
        {
            Block b = (Block) o;
            return (int) (this.position.y - b.position.y);
        }
        return 0;
    }
}
