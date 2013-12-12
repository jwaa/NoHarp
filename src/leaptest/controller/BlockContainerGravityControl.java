/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;



import com.jme3.math.Vector3f;
import leaptest.model.Block;
import leaptest.model.BlockContainer;

/**
 *
 * @author silvandeleemput
 */
public class BlockContainerGravityControl implements Updatable {

    private BlockContainer bc;
    private final float dy = -0.25f;
    
    public BlockContainerGravityControl(BlockContainer bc)
    {
        this.bc = bc;
    }

    public void update(float tpf) {
        bc.setBlockGeoms();
        for (Block b : bc.getBlocks())
        {
            pushDown(b);
        }
    }
    
    private void pushDown(Block b)
    {
        Vector3f pos = b.getPosition();
        if (b.getPosition().y > b.getDimensions().y)
        {
            pos.y += dy;
            if (bc.collideWith(b))
            {
                pos.y -= dy;
            }
        } 
    }
    
}
