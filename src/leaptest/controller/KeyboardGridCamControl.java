/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import leaptest.model.GridCam;

/**
 *
 * @author srw-install
 */
public class KeyboardGridCamControl implements AnalogListener, Updatable {

    private GridCam gridcam;
    
    private final float delta = FastMath.PI/1.5f;
    
    private float inc;
    
    public KeyboardGridCamControl(InputManager inputManager, GridCam gridcam)
    {
        this.gridcam = gridcam;
        mapInputs(inputManager);
    }
    
    private void mapInputs(InputManager inputManager)
    {
        inputManager.addMapping("Rotate Cam Up", new KeyTrigger(KeyInput.KEY_UP), new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Rotate Cam Down", new KeyTrigger(KeyInput.KEY_DOWN), new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(this, new String[]{"Rotate Cam Up","Rotate Cam Down"});       
    }

    public void onAnalog(String name, float value, float tpf) {
          if (name.equals("Rotate Cam Up"))
            inc += delta * tpf;
        else if (name.equals("Rotate Cam Down"))
            inc -= delta * tpf;
    }

    public void update(float tpf) {
        gridcam.rotate(inc);
        inc = 0;
    }
    
}
