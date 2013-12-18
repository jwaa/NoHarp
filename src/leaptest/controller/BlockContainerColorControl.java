/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.view.MaterialManager;


/**
 *
 * @author silvandeleemput
 */
public class BlockContainerColorControl implements Updatable {

    private BlockContainer bc;
    private DirectionalLightShadowRenderer dlsr;
    private ViewPort view;
    
    public BlockContainerColorControl(BlockContainer bc, DirectionalLightShadowRenderer dlsr, ViewPort view)
    {
        this.view = view;
        this.bc = bc;
        this.dlsr = dlsr;
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
                //else if (b.isFalling())
                //    b.setMaterial(MaterialManager.falling);
                }
                else
                {
                    b.setMaterial(MaterialManager.normal);
                    b.setShadowMode(ShadowMode.Receive);
                    b.setShadowMode(ShadowMode.CastAndReceive);
                    b.setShadowMode(ShadowMode.Receive);
                    //view.removeProcessor(dlsr);
                    //view.addProcessor(dlsr);
                    //dlsr.cleanup();
                }
            }
        }
    }
    
}