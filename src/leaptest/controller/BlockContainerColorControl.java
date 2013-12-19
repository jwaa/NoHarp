/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.view.MaterialManager;


/**
 *
 * @author silvandeleemput
 */
public class BlockContainerColorControl implements Updatable {

    private BlockContainer bc;
    private Block shadow_fix;
    
    public BlockContainerColorControl(BlockContainer bc)
    {
        this.bc = bc;
        
        // ugly hack to solve lingering shadow problem
        // It adds a small cube off-stage that Casts a shadow 
        // Seems that dynamic change of shadow modes does not update when there is no 
        // shadow casting geometry attached
        shadow_fix = new Block(MaterialManager.normal,new Vector3f(1000,0,0),Vector3f.UNIT_XYZ); 
        shadow_fix.setShadowMode(ShadowMode.Cast);
        bc.attachChild(shadow_fix);
    }
    
    public void update(float tpf) {
        for (Block b : bc.getBlocks())
        {
            if (!b.isDissolving())
            {
                if (b.isLifted())
                {
                    b.setMaterial(MaterialManager.lifted);
                    b.setShadowMode(ShadowMode.CastAndReceive);
                }
                else
                {
                    if (!b.isFalling())
                    {
                        b.setMaterial(MaterialManager.normal);
                        b.setShadowMode(RenderQueue.ShadowMode.Receive);
                    }
                }
            } 
            else if (b.getShadowMode() == ShadowMode.CastAndReceive)
            {
                b.setShadowMode(RenderQueue.ShadowMode.Cast);
            }
        }
    }
    
}