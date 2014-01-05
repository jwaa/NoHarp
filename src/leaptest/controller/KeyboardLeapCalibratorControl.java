/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import leaptest.model.LeapCalibrator;

/**
 *
 * @author silvandeleemput
 */
public class KeyboardLeapCalibratorControl implements Updatable, ActionListener {

    private LeapCalibrator calib;
    
    private boolean scale;
    
    private float delta;
    
    public KeyboardLeapCalibratorControl(InputManager inputManager, LeapCalibrator calib)
    {
        this.calib = calib;
        delta = 0.1f;
        scale = false;
        mapInputs(inputManager);
    }
    
    private void mapInputs(InputManager inputManager)
    {
        inputManager.addMapping("LessX", new KeyTrigger(KeyInput.KEY_1), new KeyTrigger(KeyInput.KEY_NUMPAD1));
        inputManager.addMapping("MoreX", new KeyTrigger(KeyInput.KEY_2), new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addMapping("LessY", new KeyTrigger(KeyInput.KEY_4), new KeyTrigger(KeyInput.KEY_NUMPAD4));
        inputManager.addMapping("MoreY", new KeyTrigger(KeyInput.KEY_5), new KeyTrigger(KeyInput.KEY_NUMPAD5));
        inputManager.addMapping("LessZ", new KeyTrigger(KeyInput.KEY_7), new KeyTrigger(KeyInput.KEY_NUMPAD7));
        inputManager.addMapping("MoreZ", new KeyTrigger(KeyInput.KEY_8), new KeyTrigger(KeyInput.KEY_NUMPAD8));
        inputManager.addMapping("ToggleMode", new KeyTrigger(KeyInput.KEY_0), new KeyTrigger(KeyInput.KEY_NUMPAD0));
        inputManager.addMapping("IncDelta", new KeyTrigger(KeyInput.KEY_ADD));
        inputManager.addMapping("DecDelta", new KeyTrigger(KeyInput.KEY_MINUS), new KeyTrigger(KeyInput.KEY_SUBTRACT));
        inputManager.addMapping("SaveCalib", new KeyTrigger(KeyInput.KEY_F8));  
        inputManager.addListener(this, new String[]{"LessX","MoreX","LessY","MoreY","LessZ","MoreZ"}); 
        inputManager.addListener(this, new String[]{"ToggleMode","SaveCalib","IncDelta","DecDelta"});
    }
    
    
    private void change(String name) {
        Vector3f temp;
        if (scale)
            temp = calib.getScale();
        else
            temp = calib.getOffset();
        if (name.equals("LessX"))
            temp.x -= delta;
        else if (name.equals("MoreX"))
            temp.x += delta;
        else if (name.equals("LessY"))
            temp.y -= delta;
        else if (name.equals("MoreY"))
            temp.y += delta;   
        else if (name.equals("LessZ"))
            temp.z -= delta;
        else if (name.equals("MoreZ"))
            temp.z += delta;  
        if (scale)
            calib.setScale(temp);
        else
            calib.setOffset(temp);  
        System.out.println((scale? "scale" : "offset") +" set to: " + temp.toString() );
    }

    
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("ToggleMode") && isPressed)
        {
            scale = !scale;
            System.out.println("Mode set to: " + (scale? "scale" : "offset"));
        }
        else if (name.equals("SaveCalib") && isPressed)
        {
            calib.writeToFile("leap.calib");
            System.out.println("Calib file saved - scale: " + calib.getScale() + " offset: " + calib.getOffset());
        }
        else if (name.equals("IncDelta") && isPressed)
        {
            delta*=10;
            System.out.println("Delta set to: " + delta);
        }
        else if (name.equals("DecDelta") && isPressed)
        {
            delta/=10;
            System.out.println("Delta set to: " + delta);
        }           
        else if (isPressed)
        {
            change(name);
        }
    }


    public void update(float tpf) {}
    
}
