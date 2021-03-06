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
import java.util.Iterator;

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
    
    public Block getBlockAt(Vector3f point)
    {
        for (Block b : blocks)
            if (b.isInside(point))
                return b;
        return null;
    }
    
    public int collideAboveBlock(Block b, CollisionResults r)
    {
        Vector3f pos = b.getPosition().subtract(0, b.getDimensions().y/2, 0);
        return collideWith(new Ray(pos,Vector3f.UNIT_Y),r);
    }
    
    @Override
    public int collideWith(Collidable c, CollisionResults r)
    {
        return blocknode.collideWith(c,r);
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
        b.removeFromParent();
        blocks.remove(b);
    }
    
    public void removeAllBlocks()
    {
        Iterator<Block> iter = blocks.iterator();
        while (iter.hasNext())
        {
            Block b = iter.next();
            b.removeFromParent();
            iter.remove();
        }
    }
    
    public boolean containsBlock(Block b)
    {
        return blocks.contains(b);
    }
}
