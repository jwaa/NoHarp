/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author silvandeleemput
 */
public class OverviewCamControl implements Updatable {

    private Camera cam;
    private Vector3f pos, target;
    
    
    public OverviewCamControl(Camera cam, Vector3f pos, Vector3f target)
    {
        this.target = target;
        this.cam = cam;
        this.pos = pos;
    }

    public void update(float tpf) {
        // Update camera
        cam.setLocation(pos);
        cam.lookAt(target, Vector3f.UNIT_Y); 
    }
    
}
