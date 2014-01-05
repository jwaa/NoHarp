/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import leaptest.model.LeapCalibrator;

/**
 *
 * @author silvandeleemput
 */
public class KeyboardLeapCalibratorControl implements Updatable {

    private LeapCalibrator calib;
    
    private boolean scale;
    

    
    public KeyboardLeapCalibratorControl(InputManager inputManager, LeapCalibrator calib)
    {
        this.calib = calib;
        scale = false;
        mapInputs(inputManager);
    }
    
    private void mapInputs(InputManager inputManager)
    {
        inputManager.addMapping("LessX", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("MoreX", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("LessY", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addMapping("MoreY", new KeyTrigger(KeyInput.KEY_5));
        inputManager.addMapping("LessZ", new KeyTrigger(KeyInput.KEY_7));
        inputManager.addMapping("MoreZ", new KeyTrigger(KeyInput.KEY_8));
        inputManager.addMapping("ToggleMode", new KeyTrigger(KeyInput.KEY_MINUS));
        inputManager.addMapping("SaveCalib", new KeyTrigger(KeyInput.KEY_F8));  
        inputManager.addListener(analog, new String[]{"LessX","MoreX","LessY","MoreY","LessZ","MoreZ"}); 
        inputManager.addListener(action, new String[]{"ToggleMode","SaveCalib"});
    }
    
    
    private AnalogListener analog = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            Vector3f delta;
            if (scale)
                delta = calib.getScale();
            else
                delta = calib.getOffset();
            if (name.equals("LessX"))
                delta.x -= value * tpf;
            else if (name.equals("MoreX"))
                delta.x += value * tpf;
            else if (name.equals("LessY"))
                delta.y -= value * tpf;
            else if (name.equals("MoreY"))
                delta.y += value * tpf;   
            else if (name.equals("LessZ"))
                delta.z -= value * tpf;
            else if (name.equals("MoreZ"))
                delta.z += value * tpf;  
            if (scale)
                calib.setScale(delta);
            else
                calib.setOffset(delta);  
            System.out.println((scale? "scale" : "offset") +" set to: " + delta.toString() );
        }
    };
    private ActionListener action = new ActionListener() {
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
        }
    };

    public void update(float tpf) {}
    
}
