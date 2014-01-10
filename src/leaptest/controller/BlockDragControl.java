/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.model.BlockModel;
import leaptest.model.Grid;
import leaptest.model.TaskManager;
import leaptest.view.MaterialManager;

/**
 *
 * @author silvandeleemput
 */
public class BlockDragControl {
    // Linked data
    private BlockContainer world;
    private Grid grid;
    private Block dragging, creationblock;
    private TaskManager taskmanager;
    
    private Vector3f target;
    
    public BlockDragControl(BlockContainer world, Grid grid, Block creationblock, TaskManager taskmanager)
    {
        this.taskmanager = taskmanager;
        this.creationblock = creationblock;
        this.grid = grid;
        this.world = world;
    }
   
    public void liftBlock(Block block)
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
           target = dragging.getPosition().clone();
        }       
    }

    public void releaseBlock()
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
            if (isTaskComplete())
                taskmanager.nextTask();
        }
        else
            dragging.setDissolving(true);
        dragging = null;    
    }    
    
    private boolean isTaskComplete()
    {
        BlockModel ct = taskmanager.getTask();
        if (ct.getElements() == grid.getBlocks().size())
        {
            BlockModel cg = new BlockModel(grid);
            return cg.equals(ct);
        }
        return false;
    }
    
    public void moveBlock(Vector3f position)
    {
        target = position;
        
        // Every block above the old position of the dragged block switches 
        // to falling state
        CollisionResults cr = new CollisionResults();
        grid.collideAboveBlock(dragging, cr);
        for (CollisionResult c : cr)
            ((Block) c.getGeometry()).setFalling(true);
        
        // Make sure block does not sink into floor
        if (target.y <= dragging.getDimensions().y/2)
            target.y = dragging.getDimensions().y/2;
        
        // Snap target vector 2 grid
        if (grid.withinGrid(target)) 
            target=grid.snapToGrid(target);
        
        // Calculate direction and distance between current position and target
        Vector3f cpos = dragging.getPosition();
        Vector3f dir = target.subtract(cpos).normalize().mult(1.5f);
        float dist = cpos.distance(target);

        // Detect collisions between current position and target
        CollisionResults results = new CollisionResults();
        grid.collideWith(new Ray(cpos,dir), results);
        
        // If collissions occur within range
        if (results.size() > 0 && results.getClosestCollision().getDistance() < dist)
        {
            // Calculate closest collision position and new position of block
            dragging.setPosition(results.getClosestCollision().getContactPoint().subtract(dir));
        }
        else
            dragging.setPosition(target);      
        
        // Snap dragging block to grid
        if (grid.withinGrid(dragging.getPosition())) 
            grid.snapToGrid(dragging);  
    }
    
    public Block getBlockAt(Vector3f pos)
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
    
    public Block getBlockCollideWith(Collidable ray)
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
    
    public Vector3f getTarget()
    {
        return target;
    }
    
    public Block getSelected()
    {
        return dragging;
    }
    
}
