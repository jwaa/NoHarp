/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;



import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.view.BlockView;

/**
 *
 * @author silvandeleemput
 */
public class BlockContainerControl implements Updatable {

    private BlockContainer bc;
    private Node node;
    private AssetManager am;
    
    public BlockContainerControl(BlockContainer bc, Node node, AssetManager am)
    {
        this.bc = bc;
        this.node = node;
        this.am = am;
    }

    public void update(float tpf) {
        // Update blocks TODO keep track for performance
        node.detachAllChildren();
        for (Block b : bc.getBlocks())
        {
            node.attachChild(new BlockView(am,b));
        }
    }
    
}
