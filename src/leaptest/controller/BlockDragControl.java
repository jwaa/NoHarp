/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.model.Grid;
import leaptest.view.MaterialManager;

/**
 *
 * @author silvandeleemput
 */
public abstract class BlockDragControl implements Updatable {
    // Linked data
    protected BlockContainer world;
    protected Grid grid;
    protected Block dragging, creationblock;
    
    public BlockDragControl(BlockContainer world, Grid grid, Block selected, Block creationblock)
    {
        this.creationblock = creationblock;
        this.dragging = selected;
        this.grid = grid;
        this.world = world;
    }
   
    protected void liftBlock(Block block)
    {
        dragging = block;
        if (dragging != null)
        {
           if (grid.containsBlock(dragging))
           {
               world.addBlock(dragging);
               grid.removeFromGrid(dragging);
           } else if (!world.containsBlock(dragging))
               world.addBlock(dragging);
           dragging.setLifted(true);
        }       
    }

    protected void releaseBlock()
    {
        dragging.setLifted(false);
        dragging.setFalling(true);
        if (grid.withinGrid(dragging.getPosition()))
        {       
            grid.snapToGrid(dragging);
            dragging.setPosition(grid.world2grid(dragging.getPosition()));
            dragging.setRotation(0f);
            world.removeBlock(dragging);
            grid.addBlock(dragging);   
        }
        else
            dragging.setDissolving(true);
        dragging = null;    
    }    
    
    protected void moveBlock(Vector3f position)
    {
        dragging.setPosition(position);
    }
    
    protected Block getBlockAt(Vector3f pos)
    {
        Block result;
        // If is creation block get new block 
        if (creationblock.isInside(pos))
            return new Block(MaterialManager.normal,creationblock.getPosition(),Vector3f.UNIT_XYZ.mult(creationblock.getDimensions().x));
        
        // Check if block is in the world or in the grid
        result = world.getBlockAt(pos);
        if (result != null)
            return result;  
        return grid.getBlockAt(pos);    
    }
    
    protected Block getBlockCollideWith(Collidable ray)
    {
        CollisionResults results = new CollisionResults();
        
        // New block creation intersection
        creationblock.collideWith(ray, results);
        if (results.size() > 0)
            return new Block(MaterialManager.normal,creationblock.getPosition(),Vector3f.UNIT_XYZ.mult(creationblock.getDimensions().x));
        
        // Collect intersections between ray and all nodes in results list
        world.collideWith(ray, results);
        grid.collideWith(ray, results);
        
        // Find nearest available geometry and return it as a block
        if (results.size() > 0)
        {
            Geometry g = results.getClosestCollision().getGeometry();
            if (g instanceof Block && !((Block) g).isDissolving())
                return (Block) g;
        }
        return null;
    }
    
    public abstract void update(float tpf);
    
}
