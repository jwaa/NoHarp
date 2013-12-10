/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;



import com.jme3.asset.AssetManager;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.view.BlockView;

/**
 *
 * @author silvandeleemput
 */
public class BlockContainerControl implements Control {

    private BlockContainer bc;
    private Node node;
    private AssetManager am;
    
    public BlockContainerControl(BlockContainer bc, Node node, AssetManager am)
    {
        this.bc = bc;
        this.node = node;
        this.am = am;
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public void update(float tpf) {
        // Update blocks TODO keep track for performance
        node.detachAllChildren();
        for (Block b : bc.getBlocks())
        {
            node.attachChild(new BlockView(am,b));
        }
    }

    public void render(RenderManager rm, ViewPort vp) {
    }

    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
