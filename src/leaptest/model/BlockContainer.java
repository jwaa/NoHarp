/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.model;

import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *
 * @author silvandeleemput
 */
public class BlockContainer {
    protected ArrayList<Block> blocks;
    protected Node blocknode;
    private ArrayList<Geometry> blockgeoms;
    
    public BlockContainer()
    {
        blocknode = new Node();
        blocks = new ArrayList<Block>();
        blockgeoms = new ArrayList<Geometry>();
    }

    private void setBlockGeoms()
    {
        blocknode.detachAllChildren();
        blockgeoms.clear();
        for (Block b: blocks)
        {
            Geometry box = new Geometry("Block", new Box(b.getDimensions().x,b.getDimensions().y,b.getDimensions().z));
            box.setLocalTranslation(b.getPosition());
            box.rotate(0, b.getRotation(), 0);
            blockgeoms.add(box);
            blocknode.attachChild(box);
        }
    }

    public int collideWith(Collidable c, CollisionResults results)
    {
        setBlockGeoms();
        return blocknode.collideWith(c, results);
    }
    
    
    public Block getBlockFromGeometry(Geometry target)
    {
        if (target != null)
        {
          int index = blockgeoms.indexOf(target);
          if (index > -1)
            return blocks.get(index);
        }        
        return null;
    }    
    
    public Block getBlockAt(Vector3f pos)
    {
        setBlockGeoms();
        return null;
    }
    
    public ArrayList<Block> getBlocks()
    {
        return blocks;
    }
    
    public void addBlock(Block b)
    {
        blocks.add(b);
    }

    public void removeBlock(Block b)
    {
        blocks.remove(b);
    }
    
    public boolean containsBlock(Block b)
    {
        return blocks.contains(b);
    }
}
