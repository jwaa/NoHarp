/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
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
import leaptest.model.BlockContainer;
import leaptest.model.Grid;

/**
 *
 * @author silvandeleemput
 */
public class MouseBlockControl extends BlockDragControl implements AnalogListener {
    // Linked data
    private InputManager inputManager;
    private Camera cam;
    
    // Process data
    private boolean clickinit, clickrelease;
    private float liftdelta;
    
    public MouseBlockControl(InputManager inputManager, Camera cam, BlockContainer world, Grid grid, Block creationblock)
    {
        super(world,grid,creationblock);
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
        
        return getBlockCollideWith(ray);
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
        // Every block above the old position of the dragged block switches 
        // to falling state
        CollisionResults cr = new CollisionResults();
        grid.collideAboveBlock(dragging, cr);
        for (CollisionResult c : cr)
            ((Block) c.getGeometry()).setFalling(true);

        if (target.y + liftdelta > dragging.getDimensions().y/2)
            target.y += liftdelta;
        else
            target.y = dragging.getDimensions().y/2;

        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

        // Aim the ray from the clicked spot forwards
        Ray ray = new Ray(click3d, dir);

        if (ray.intersectsWherePlane(new Plane(Vector3f.UNIT_Y,0f), dir))
        {
            dir.y = target.y;
            target = dir;
        }
        if (grid.withinGrid(target)) 
            target=grid.snapToGrid(target);  
        
        moveBlock(target);
        
        if (grid.withinGrid(dragging.getPosition())) 
            grid.snapToGrid(dragging);        
    }
    
    public void update(float tpf) 
    {
        // On a new click start dragging
        if (clickinit)
        {
            liftBlock(detectBlock());
        } 
        // On button release drop block if dragging
        else if (clickrelease && dragging != null) 
        {
            releaseBlock();
        }     
        // While dragging update position of block
        if (dragging != null)
        {
            updateBlock();
        }
        // Reset click and delta states for next cycle
        resetStates();
    }
    
}
