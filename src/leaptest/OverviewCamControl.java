/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest;



import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author silvandeleemput
 */
public class OverviewCamControl implements Control {

    private Camera cam;
    private Vector3f pos, target;
    
    
    public OverviewCamControl(Camera cam, Vector3f pos, Vector3f target)
    {
        this.target = target;
        this.cam = cam;
        this.pos = pos;

    }
    
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public void update(float tpf) {
        // Update camera
        cam.setLocation(pos);
        cam.lookAt(target, Vector3f.UNIT_Y); 
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
