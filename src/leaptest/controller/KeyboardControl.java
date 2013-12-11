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
public class KeyboardControl implements ActionListener, Updatable {

    private Main game;
    private boolean quit;
    
    public KeyboardControl(Main game)
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
            quit = true;
    }

    public void update(float tpf) {
        if (quit)
        {
            game.getInputManager().removeListener(this);
            game.shutDown();            
        }
    }
    
}
