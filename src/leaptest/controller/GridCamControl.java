/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;



import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import leaptest.model.GridCam;

/**
 *
 * @author silvandeleemput
 */
public class GridCamControl implements Updatable {

    private Camera cam;
    private GridCam gc;
    
    
    public GridCamControl(Camera cam, GridCam gc)
    {
        this.cam = cam;
        this.gc = gc;
    }

    public void update(float tpf) {
        // Update camera
        cam.setLocation(gc.getPosition());
        cam.lookAt(gc.getFocusPoint(), Vector3f.UNIT_Y); 
    }

}
