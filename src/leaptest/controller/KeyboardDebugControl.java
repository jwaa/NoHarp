/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.system.AppSettings;
import leaptest.Main;

/**
 *
 * @author srw-install
 */
public class KeyboardDebugControl implements ActionListener, Updatable {

    private Main game;
    private AppSettings settings;
    
    public KeyboardDebugControl(Main game, AppSettings settings)
    {
        this.game = game;
        this.settings = settings;
        mapInputs();
    }
    
    private void mapInputs()
    {
        InputManager inputManager = game.getInputManager();
        inputManager.addMapping("Quit game", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("ScreenSettingsDebug", new KeyTrigger(KeyInput.KEY_F1));
        
        inputManager.addListener(this, new String[]{"Quit game","ScreenSettingsDebug"});       
    }
    
    public void onAction(String name, boolean isPressed, float tpf) 
    {   
        if (name.equals("Quit game"))
            game.setShutDown(true);
        else if (name.equals("ScreenSettingsDebug"))
            System.out.println(settings);
        
    }

    public void update(float tpf) {}
    
}
