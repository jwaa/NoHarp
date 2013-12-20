/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.view.MaterialManager;


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
            if (!b.isDissolving())
            {
                if (b.isLifted())
                    b.setMaterial(MaterialManager.lifted);
                else
                    b.setMaterial(MaterialManager.normal);
            } 
        }
    }
    
}