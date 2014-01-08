/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import leaptest.model.BlockModel;
import leaptest.model.Grid;

/**
 *
 * @author srw-install
 */
public class KeyboardGridSaveControl implements ActionListener, Updatable {

    private final String exportfile;
    private Grid grid;
    
    public KeyboardGridSaveControl(InputManager inputManager, Grid grid, String exportfile)
    {
        this.exportfile = exportfile;
        this.grid = grid;
        mapInputs(inputManager);
    }
    
    private void mapInputs(InputManager inputManager)
    {
        inputManager.addMapping("SaveGrid", new KeyTrigger(KeyInput.KEY_F7));
        inputManager.addListener(this, new String[]{"SaveGrid"});       
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("SaveGrid") && isPressed)
        {
            BlockModel save = new BlockModel(grid);
            save.save(exportfile);
            System.out.print(save);
            System.out.println("Saved grid to: " + exportfile);
        }
    }

    public void update(float tpf) {}
    
}
