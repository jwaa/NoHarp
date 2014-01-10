/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import leaptest.Main;

/**
 *
 * @author srw-install
 */
public class KeyboardDebugControl implements ActionListener, Updatable {

    private Main game;
    
    public KeyboardDebugControl(Main game)
    {
        this.game = game;
        mapInputs();
    }
    
    private void mapInputs()
    {
        InputManager inputManager = game.getInputManager();
        inputManager.addMapping("Quit game", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(this, new String[]{"Quit game"});       
    }
    
    public void onAction(String name, boolean isPressed, float tpf) 
    {   
        if (name.equals("Quit game"))
            game.setShutDown(true);
    }

    public void update(float tpf) {}
    
}
