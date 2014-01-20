/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import leaptest.model.Block;
import com.jme3.math.Plane;
import leaptest.utils.Log;
import leaptest.utils.Loggable;

/**
 *
 * @author silvandeleemput
 */
public class MouseBlockControl implements AnalogListener, Updatable, Loggable {
    // Linked data
    private InputManager inputManager;
    private Camera cam;
    
    // Process data
    private boolean clickinit, clickrelease;
    private float liftdelta;
    
    //Log data
    private Vector2f prevMouseLoc = new Vector2f();
    private Vector2f mouseDelta = new Vector2f();
    private boolean isClicked;
    private boolean isReleased;
    
    private BlockDragControl bdc;
    
    public MouseBlockControl(InputManager inputManager, Camera cam, BlockDragControl bdc)
    {
        this.bdc = bdc;
        this.cam = cam;
        this.inputManager = inputManager;
        configureInputs(inputManager);
    }
    
    private void configureInputs(InputManager inputManager)
    {
        inputManager.addMapping("Dragging", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));       
        inputManager.addMapping("LiftBlockUp", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("LiftBlockDown", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addListener(this, new String[]{"LiftBlockUp", "LiftBlockDown"});       
        inputManager.addListener(actionListener, new String[]{"Dragging"});
    }

    public void onAnalog(String name, float value, float tpf) 
    {
        if (name.equals("LiftBlockUp"))
            liftdelta += value;
        else if (name.equals("LiftBlockDown"))
            liftdelta -= value;
    }
    
    private Block detectBlock()
    {
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        
        // Aim the ray from the clicked spot forwards
        Ray ray = new Ray(click3d, dir);
        
        return bdc.getBlockCollideWith(ray);
    }
    
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Dragging")) 
            {
                if (isPressed)
                {
                    clickinit = true;
                } else {
                    clickrelease = true;
                    clickinit = false;
                }
            }
        }
        
    };

    private void resetStates()
    {
        clickinit = false;
        clickrelease = false;
        liftdelta = 0;
    }
    
    private void updateBlock()
    {        
        Vector3f target = bdc.getTarget();
        
        target.y += liftdelta; // set y
        
        // Calculate position clicked on the screen to 3D coordinates
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

        // Aim the ray from the clicked spot forwards
        Ray ray = new Ray(click3d, dir);

        if (ray.intersectsWherePlane(new Plane(Vector3f.UNIT_Y,0f), dir))
        {
            dir.y = target.y;
            target = dir; // set x and z
        }
        // try to move the block to position
        bdc.moveBlock(target);       
    }
    
    public void update(float tpf) 
    {  
        // See if there is a block under the mouse
        Block undermouse = detectBlock(); 
        if (bdc.getSelected() == null && undermouse != null)
            undermouse.setOver(true);
        // On a new click start dragging
        if (clickinit)
        {
            isClicked = true;
            bdc.liftBlock(undermouse);
        } 
        // On button release drop block if dragging
        else if (clickrelease && bdc.getSelected() != null) 
        {
            isReleased = true;
            bdc.releaseBlock();
        }     
        // While dragging update position of block
        if (bdc.getSelected() != null)
        {
            updateBlock();
        }
        // Save mouse location data
        if(!inputManager.getCursorPosition().clone().equals(prevMouseLoc))
        {
            mouseDelta = inputManager.getCursorPosition().subtract(prevMouseLoc);
            prevMouseLoc = inputManager.getCursorPosition().clone();
        }
        // Reset click and delta states for next cycle
        resetStates();
       
    }

    public void log(Log log) 
    {
        if(!mouseDelta.equals(new Vector2f()))
        {
            String delta = Float.toString(mouseDelta.x)+", "+Float.toString(mouseDelta.y);
            log.addEntry(Log.EntryType.MouseLocDelta, delta);
        }
        if(liftdelta != 0.0f)
            log.addEntry(Log.EntryType.ScrollDelta,  Float.toString(liftdelta));
        if(isClicked)
            log.addEntry(Log.EntryType.MouseClick, Boolean.toString(isClicked));
        if(isReleased)
            log.addEntry(Log.EntryType.MouseReleased, Boolean.toString(isReleased));
        mouseDelta = new Vector2f();
        isClicked = false;
        isReleased = false;
    }
    
}
