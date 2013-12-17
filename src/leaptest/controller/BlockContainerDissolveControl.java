/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.math.Vector3f;
import java.util.Iterator;
import leaptest.model.Block;
import leaptest.model.BlockContainer;

/**
 *
 * @author silvandeleemput
 */
public class BlockContainerDissolveControl implements Updatable {

    BlockContainer bc;
    
    public BlockContainerDissolveControl(BlockContainer bc)
    {
        this.bc = bc;
    }
    
    public void update(float tpf) {
        Iterator<Block> iter = bc.getBlocks().iterator();
        while (iter.hasNext())
        {
            Block b = iter.next();
            if (!b.isLifted())
            {
                b.setFalling(true);
                b.setGravity(b.getGravity()-0.2f);
                Vector3f pos = b.getPosition().add(0, b.getGravity(), 0);
                if (pos.y <= b.getDimensions().y)
                    pos.y = b.getDimensions().y;
                b.setPosition(pos);
            }
            if (b.isDissolving())
            {
                b.setAlpha(b.getAlpha()-0.05f);
                if (b.getAlpha()<= 0)
                {
                    bc.detachChild(b);
                    iter.remove();
                }
            }
        }
    }
    
}
