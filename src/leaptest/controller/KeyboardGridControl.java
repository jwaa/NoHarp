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
import leaptest.model.Grid;

/**
 *
 * @author srw-install
 */
public class KeyboardGridControl implements AnalogListener {

    private Grid grid;
    
    private final float delta = FastMath.PI/1.5f;
    
    
    public KeyboardGridControl(InputManager inputManager, Grid grid)
    {
        this.grid = grid;
        mapInputs(inputManager);
    }
    
    private void mapInputs(InputManager inputManager)
    {
        inputManager.addMapping("Rotate Grid Left", new KeyTrigger(KeyInput.KEY_LEFT), new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Rotate Grid Right", new KeyTrigger(KeyInput.KEY_RIGHT), new KeyTrigger(KeyInput.KEY_D));
        inputManager.addListener(this, new String[]{"Rotate Grid Left","Rotate Grid Right"});       
    }

    public void onAnalog(String name, float value, float tpf) {
          if (name.equals("Rotate Grid Left"))
            grid.rotate(delta * tpf);
        else if (name.equals("Rotate Grid Right"))
            grid.rotate(-delta * tpf);
    }
    
}
