/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;



import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;
import leaptest.model.GridCam;

/**
 *
 * @author silvandeleemput
 */
public class GridCamControl implements Control {

    private Camera cam;
    private GridCam gc;
    
    
    public GridCamControl(Camera cam, GridCam gc)
    {
        this.cam = cam;
        this.gc = gc;
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public void update(float tpf) {
        // Update camera
        cam.setLocation(gc.getPosition());
        cam.lookAt(gc.getFocusPoint(), Vector3f.UNIT_Y); 
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
