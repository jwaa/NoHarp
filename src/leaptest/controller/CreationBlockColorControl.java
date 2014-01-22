/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import leaptest.model.Block;
import leaptest.view.BlockCap;
import leaptest.view.MaterialManager;

/**
 *
 * @author silvandeleemput
 */
public class CreationBlockColorControl implements Updatable {
    
    private Block cblock;
    private BlockCap cap;
    
    public CreationBlockColorControl(Block cblock)
    {
        this.cblock = cblock;
        BlockCap cblockcap = new BlockCap(cblock.getDimensions());
        cblockcap.move(cblock.getLocalTranslation());
        cblockcap.rotate(-FastMath.PI * 0.5f, 0, 0);
        cblockcap.setMaterial(cblock.getMaterial());
        cblockcap.setShadowMode(RenderQueue.ShadowMode.Receive);
        cblock.getParent().attachChild(cblockcap);
        this.cap = cblockcap;
    }

    public void update(float tpf) {
        if (cblock.isOver())
        {
            cblock.setMaterial(MaterialManager.creationblockover);
            cblock.setOver(false);
        }
        else
            cblock.setMaterial(MaterialManager.creationblock);
        cap.setMaterial(cblock.getMaterial());
    }
    
}