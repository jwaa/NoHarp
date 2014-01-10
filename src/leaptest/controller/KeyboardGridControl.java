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
import leaptest.utils.Log;
import leaptest.utils.Loggable;

/**
 *
 * @author srw-install
 */
public class KeyboardGridControl implements AnalogListener, Updatable, Loggable {

    private Grid grid;
    
    private final float delta = FastMath.PI/1.5f;
    
    private float inc;
    
    // Log data
    private boolean isKeyPressLeft;
    private boolean isKeyPressRight;
    
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
        {
            isKeyPressLeft = true;
            inc += delta * tpf;
        }
        else if (name.equals("Rotate Grid Right"))
        {
            isKeyPressRight = true;
            inc -= delta * tpf;
        }
    }

    public void update(float tpf) {
        grid.rotate(inc);
        inc = 0;
    }

    public void log(Log log) {
        if(isKeyPressLeft)
            log.addEntry(Log.EntryType.KeyPressLeft, Boolean.toString(isKeyPressLeft));
        if(isKeyPressRight)
            log.addEntry(Log.EntryType.KeyPressRight, Boolean.toString(isKeyPressRight));
        
        isKeyPressLeft = false;
        isKeyPressRight = false;
    }
    
}
