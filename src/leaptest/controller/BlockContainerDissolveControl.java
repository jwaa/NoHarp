/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.texture.Texture;
import java.util.Iterator;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.view.MaterialManager;

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
                if (pos.y <= b.getDimensions().y/2)
                    pos.y = b.getDimensions().y/2;
                b.setPosition(pos);
            }
            if (b.isDissolving())
            {
                // If not in the Transparent bucket clone material and color 
                // and move it to the Transparent bucket
                if (b.getQueueBucket() != RenderQueue.Bucket.Transparent)
                {
                    b.setMaterial(b.getMaterial().clone());
                    b.getMaterial().setColor("Diffuse",((ColorRGBA) (b.getMaterial().getParam("Diffuse").getValue())).clone());
                    b.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                    b.setQueueBucket(RenderQueue.Bucket.Transparent);
                }
                // reduce alpha on each cycle and finally remove block
                ColorRGBA col = ((ColorRGBA) (b.getMaterial().getParam("Diffuse").getValue()));
                col.a -= 0.05;
                b.getMaterial().setColor("Diffuse", col);
                if (col.a <= 0)
                {
                    iter.remove();
                    b.removeFromParent();
                }
            }
        }
    }
    
}
