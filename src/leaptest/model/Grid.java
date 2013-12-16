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
public class Grid extends BlockContainer {
    
    private int dimensions[];
    private Vector3f cellDimensions;
    boolean rotating;
    float rotation;
    float radius;
    
    public Grid(int x, int y, int z, Vector3f cellDimensions)
    {
        this.dimensions = new int[] {x,y,z};
        this.cellDimensions = cellDimensions;
        float width = x * (cellDimensions.x + 1), height = z * (cellDimensions.z + 1);
        radius = FastMath.sqrt(width * width + height * height)/2;
    }
    
    public boolean withinGrid(Vector3f pos)
    {
        return (radius > FastMath.sqrt(pos.x * pos.x + pos.z * pos.z));
    }
    
    public void rotate(float delta)
    {
        this.rotation += delta;
        this.rotate(0, delta, 0);
    }
    
    public float getRotation()
    {
        return rotation;
    }
    
    public void setRotating(boolean rota)
    {
        this.rotating = rota;
    }
    
    public boolean isRotating()
    {
        return rotating;
    }
    
    public int[] getDimensions()
    {
        return dimensions;
    }
    
    public Vector3f getCellDimensions()
    {
        return cellDimensions;
    }
    
    public Vector3f world2grid(Vector3f world)
    {
        return new Vector3f(
                world.x*FastMath.cos(rotation) - world.z*FastMath.sin(rotation), 
                world.y, 
                world.x*FastMath.sin(rotation) + world.z*FastMath.cos(rotation));
    }
 
    public Vector3f grid2world(Vector3f world)
    {
        return new Vector3f(
                world.x*FastMath.cos(-rotation) - world.z*FastMath.sin(-rotation), 
                world.y, 
                world.x*FastMath.sin(-rotation) + world.z*FastMath.cos(-rotation));
    }    
    
    public void removeFromGrid(Block b)
    {
        blocks.remove(b);
        b.setPosition(grid2world(b.getPosition()));
        b.setRotation(rotation);        
    }
    
    public void snapToGrid(Block b)
    {
        Vector3f pos = world2grid(b.getPosition());
        pos.x = Math.round(pos.x/cellDimensions.x)*cellDimensions.x;
        pos.z = Math.round(pos.z/cellDimensions.z)*cellDimensions.z;
        b.setPosition(grid2world(pos));
        b.setRotation(rotation);
    }
    
}