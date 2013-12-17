/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;



import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.model.Grid;

/**
 *
 * @author silvandeleemput
 */
public class GridGravityControl implements Updatable {

    private Grid bc;
    private BlockContainer world;
    private final float dy = -0.15f;
    
    public GridGravityControl(Grid bc, BlockContainer world)
    {
        this.bc = bc;
        this.world = world;
    }
    
    private class IntPair {
      final int x;
      final int y;
      IntPair(int x, int y) {this.x=x;this.y=y;}
    }
    
    public void update(float tpf) {
        // find out for which pillars there are falling blocks
        HashSet<IntPair> pillars = new HashSet<IntPair>();
        for (Block b : bc.getBlocks())
        {
            if (b.isFalling())
            {
                b.setGravity(b.getGravity()+dy);
                pillars.add(new IntPair((int)b.getPosition().x,(int)b.getPosition().z));
            }
        }     
        // for each pillar with falling blocks
        for (IntPair pp : pillars)
        {
            // collect blocks for pillar
            HashSet<Block> blocks = new HashSet<Block>();
            CollisionResults cr = new CollisionResults();
            Ray r = new Ray(bc.grid2world(new Vector3f(pp.x,0,pp.y)),Vector3f.UNIT_Y);
            bc.collideWith(r, cr);
            world.collideWith(r,cr);
            for (CollisionResult c : cr)
                blocks.add(((Block) c.getGeometry()));
            
            // sort elements from ground to air
            ArrayList<Block> blocks2 = new ArrayList<Block>();
            for (Block b : blocks)
            {
                //b.setFalling(true);
                blocks2.add(b);
            }
            Collections.sort(blocks2);
            
            // set blocks to new positions
            for (int i=0; i<blocks2.size(); i++)
            {
                Block b = blocks2.get(i);
                Vector3f pos = b.getPosition();
                // for the first block above the ground
                if (i==0) 
                {
                    // does it hit the floor?
                    if (b.getDeltaY()<b.getDimensions().y)
                    {
                        pos.y=b.getDimensions().y;
                        b.setPosition(pos);
                        b.setFalling(false);
                    }
                } 
                else // for all other blocks
                {
                    // does it hit block below
                    if (b.getDeltaY()<blocks2.get(i-1).getPosition().y+blocks2.get(i-1).getDimensions().y*2)
                    {
                        pos.y=blocks2.get(i-1).getPosition().y+blocks2.get(i-1).getDimensions().y*2;
                        b.setPosition(pos);
                        b.setFalling(false);
                    }             
                }
                // if still falling update position
                if (b.isFalling())
                {
                    pos.y+=b.getGravity();
                    b.setPosition(pos);
                }
            }           
        }
    }
    
}
