/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

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
import com.jme3.scene.Node;
import leaptest.model.Block;
import leaptest.view.BlockView;
import com.jme3.math.Plane;
import leaptest.model.BlockContainer;
import leaptest.model.Grid;
/**
 *
 * @author silvandeleemput
 */
public class MouseBlockControl implements AnalogListener {
    
    private InputManager inputManager;
    private Block dragging;
    private Camera cam;
    private BlockContainer world;
    private Grid grid;
    
    public MouseBlockControl(InputManager inputManager, Camera cam, BlockContainer world, Grid grid)
    {
        this.grid = grid;
        this.world = world;
        this.cam = cam;
        this.inputManager = inputManager;
        configureInputs(inputManager);
    }
    
    private void configureInputs(InputManager inputManager)
    {
        inputManager.addMapping("Dragging", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Mouse Move", 
                new MouseAxisTrigger(MouseInput.AXIS_X, true), 
                new MouseAxisTrigger(MouseInput.AXIS_X, false), 
                new MouseAxisTrigger(MouseInput.AXIS_Y, true), 
                new MouseAxisTrigger(MouseInput.AXIS_Y, false));        
        inputManager.addListener(this, new String[]{"Mouse Move"});       
        inputManager.addListener(actionListener, new String[]{"Dragging"});
    }

    public void onAnalog(String name, float value, float tpf) 
    {
        if (dragging != null && name.equals("Mouse Move"))
        {
            //System.out.println(name + " " + value + " " + tpf);
            Vector2f click2d = inputManager.getCursorPosition();
            Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
            Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
            // Aim the ray from the clicked spot forwards.
            Ray ray = new Ray(click3d, dir);
            
            if (ray.intersectsWherePlane(new Plane(Vector3f.UNIT_Y,0f), dir))
            {
                dir.y += dragging.getDimensions().y;
                dragging.setPosition(dir);
                if (grid.withinGrid(dir)) 
                    grid.snapToGrid(dragging);
            }
        }
    }
    
    private Block detectBlock()
    {
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        world.collideWith(ray, results);
        grid.collideWith(ray, results);

        Block b = grid.getCollisionBlock(results);
        if (b == null)
            b = world.getCollisionBlock(results);
        return b;
    }
    
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {
            //System.out.println(name + " " + (isPressed ? "Y" : "N") + " " + tpf);
            if (name.equals("Dragging")) 
            {
                if (isPressed)
                {
                    dragging = detectBlock();
                    if (dragging != null)
                    {
                        if (grid.containsBlock(dragging))
                        {
                            //dragging.setPosition(grid.grid2world(dragging.getPosition()));
                            world.addBlock(dragging);
                            grid.removeFromGrid(dragging);
                            
                            // TODO correct position
                        }
                        dragging.setLifted(true);
                    }
                } else if (dragging != null) {
                    dragging.setLifted(false);
                    if (grid.withinGrid(dragging.getPosition()))
                    {
                        grid.snapToGrid(dragging);
                        dragging.setPosition(grid.world2grid(dragging.getPosition()));
                        dragging.setRotation(0f);
                        world.removeBlock(dragging);
                        grid.addBlock(dragging);                    
                    }
                    
                    dragging = null;
                }
            }
        }
        
    };
    
}
