/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import leaptest.model.BlockContainer;

/**
 *
 * @author silvandeleemput
 */
public class BlockContainerShadowControl implements Updatable {

    BlockContainer bc;
    
    public BlockContainerShadowControl(BlockContainer bc)
    {
        this.bc = bc;
    }
    
    public void update(float tpf) 
    {
        
    }
    
}
