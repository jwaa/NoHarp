/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.view.BlockMaterial;

/**
 *
 * @author silvandeleemput
 */
public class BlockContainerColorControl implements Updatable {

    private BlockContainer bc;
    
    public BlockContainerColorControl(BlockContainer bc)
    {
        this.bc = bc;
    }
    
    public void update(float tpf) {
        for (Block b : bc.getBlocks())
        {
            if (b.isLifted())
                b.setMaterial(BlockMaterial.lifted);
            else if (b.isFalling())
                b.setMaterial(BlockMaterial.falling);
            else
                b.setMaterial(BlockMaterial.normal);
        }
    }
    
}