/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.model;

import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author silvandeleemput
 */
public class BlockContainer extends Node {
    protected ArrayList<Block> blocks;
    protected Node blocknode;
    
    public BlockContainer()
    {
        blocknode = new Node();
        this.attachChild(blocknode);
        blocks = new ArrayList<Block>();
    }  
    
    public int collideAboveBlock(Block b, CollisionResults r)
    {
        Vector3f pos = b.getPosition().subtract(0, b.getDimensions().y, 0);
        return collideAbovePosition(pos,r);
    }
    
    public int collideAbovePosition(Vector3f pos, CollisionResults r)
    {
        return collideWith(new Ray(pos,Vector3f.UNIT_Y),r);        
    }
    
    @Override
    public int collideWith(Collidable c, CollisionResults r)
    {
        return blocknode.collideWith(c,r);
    }
    
    public boolean collideWith(Block b)
    {
        Vector3f pos = b.getPosition().clone();
        pos.y -= b.getDimensions().y * 2;
        return (getBlockAt(pos,b) != null);
    }
    
    public Block getBlockAt(Vector3f pos, Block nb)
    {
        for (Block b : blocks)
            if (!b.equals(nb) && b.collidesWith(pos))
                return b;
        return null;
    }
    
    public ArrayList<Block> getBlocks()
    {
        return blocks;
    }
    
    public void addBlock(Block b)
    {
        blocknode.attachChild(b);
        blocks.add(b);
    }

    public void removeBlock(Block b)
    {
        blocknode.detachChild(b);
        blocks.remove(b);
    }
    
    public boolean containsBlock(Block b)
    {
        return blocks.contains(b);
    }
}
