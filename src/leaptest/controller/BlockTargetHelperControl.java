/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import leaptest.model.Block;
import leaptest.view.MaterialManager;

/**
 *
 * @author silvandeleemput
 */
public class BlockTargetHelperControl implements Updatable {

    private Block helper, selected;
    private BlockDragControl bdc;
    private Node node;
    
    public BlockTargetHelperControl(BlockDragControl bdc, Node node, Vector3f dimensions)
    {
        this.node = node;
        this.helper = new Block(MaterialManager.target,Vector3f.ZERO,dimensions);
        this.helper.setQueueBucket(Bucket.Transparent);
        this.bdc = bdc;
    }
    
    public void update(float tpf) 
    {
        selected = bdc.getSelected();
        if (selected!=null && bdc.getTarget()!= selected.getPosition())
        {
            //System.out.println("yay"); //bdc.getTarget().x
            helper.setPosition(bdc.getTarget());
            helper.setLocalRotation(selected.getLocalRotation());
            node.attachChild(helper);
        }
        else 
            helper.removeFromParent();
    }
    
}
