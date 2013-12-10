/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;



import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;
import leaptest.model.Grid;

/**
 *
 * @author silvandeleemput
 */
public class GridControl implements Control {

    private Grid grid;
    private Node gridnode;
    
    
    public GridControl(Grid grid, Node gridnode)
    {
        this.grid = grid;
        this.gridnode = gridnode;
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public void update(float tpf) {
        Quaternion q = new Quaternion();
        q.fromAngles(0f, grid.getRotation(), 0f);
        gridnode.setLocalRotation(q);
        
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
