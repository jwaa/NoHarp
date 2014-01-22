/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.view.BlockCap;
import leaptest.view.MaterialManager;

/**
 *
 * @author silvandeleemput
 */
public class BlockContainerShadowControl implements Updatable {

    private Geometry cap;
    private BlockContainer bc;
    private Node node;
    private Vector3f offset;
    private Block shadow_fix;
    
    public BlockContainerShadowControl(BlockContainer bc, Vector3f dimensions)
    {
        this.cap = new BlockCap(dimensions);

        offset = new Vector3f(-dimensions.x/2,dimensions.y/2+0.01f,dimensions.z/2);
        node = new Node();
        bc.attachChild(node);
        this.bc = bc;
        
        // ugly hack to solve lingering shadow problem
        // It adds a small cube off-stage that Casts a shadow 
        // Seems that dynamic change of shadow modes does not update when there is no 
        // shadow casting geometry attached
        shadow_fix = new Block(MaterialManager.normal,new Vector3f(1000,0,0),Vector3f.UNIT_XYZ); 
        shadow_fix.setShadowMode(ShadowMode.Cast);
        bc.attachChild(shadow_fix);        
    }
    
    public void update(float tpf) 
    {
        node.detachAllChildren();
        for (Block b : bc.getBlocks())
        {
            Geometry c = cap.clone();
            float rotation = b.getRotation();
            c.rotate(-FastMath.PI*0.5f,rotation,0); 
            Vector3f addition = new Vector3f(
                FastMath.cos(rotation) - FastMath.sin(rotation), 
                1f, 
                FastMath.sin(rotation) + FastMath.cos(rotation));   
            c.setLocalTranslation(b.getPosition().add(offset.mult(addition)));
            c.setMaterial(b.getMaterial());
            if (!b.isDissolving())
            {
                node.attachChild(c);
                b.setShadowMode(ShadowMode.Off);
            } else {
                b.setShadowMode(ShadowMode.Cast);
            }
        }
    }
    
}
