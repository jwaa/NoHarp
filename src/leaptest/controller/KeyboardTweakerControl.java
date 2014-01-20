/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import leaptest.utils.TweakSet;
import leaptest.utils.TweakVariable;
import leaptest.utils.Tweaker;

/**
 *
 * @author silvandeleemput
 */
public class KeyboardTweakerControl implements Updatable, ActionListener {

    private Tweaker tweak;
    String pre, post;
    
    public KeyboardTweakerControl(InputManager inputManager, Tweaker tweak, String pre, String post)
    {
        this.pre = pre;
        this.post = post;
        this.tweak = tweak;
        this.tweak.loadTweakSets(pre,post);
        this.tweak.nextSet();
        mapInputs(inputManager);
    }
    
    private void mapInputs(InputManager inputManager)
    {
        inputManager.addMapping("LessX", new KeyTrigger(KeyInput.KEY_1), new KeyTrigger(KeyInput.KEY_NUMPAD1));
        inputManager.addMapping("MoreX", new KeyTrigger(KeyInput.KEY_7), new KeyTrigger(KeyInput.KEY_NUMPAD7));
        inputManager.addMapping("LessY", new KeyTrigger(KeyInput.KEY_2), new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addMapping("MoreY", new KeyTrigger(KeyInput.KEY_8), new KeyTrigger(KeyInput.KEY_NUMPAD8));
        inputManager.addMapping("LessZ", new KeyTrigger(KeyInput.KEY_3), new KeyTrigger(KeyInput.KEY_NUMPAD3));
        inputManager.addMapping("MoreZ", new KeyTrigger(KeyInput.KEY_9), new KeyTrigger(KeyInput.KEY_NUMPAD9));
        inputManager.addMapping("ToggleSet", new KeyTrigger(KeyInput.KEY_5), new KeyTrigger(KeyInput.KEY_NUMPAD5));
        inputManager.addMapping("NextVar", new KeyTrigger(KeyInput.KEY_4), new KeyTrigger(KeyInput.KEY_NUMPAD4));
        inputManager.addMapping("PrevVar", new KeyTrigger(KeyInput.KEY_6), new KeyTrigger(KeyInput.KEY_NUMPAD6));        
        inputManager.addMapping("IncDelta", new KeyTrigger(KeyInput.KEY_ADD));
        inputManager.addMapping("DecDelta", new KeyTrigger(KeyInput.KEY_MINUS), new KeyTrigger(KeyInput.KEY_SUBTRACT));
        inputManager.addMapping("SaveCalib", new KeyTrigger(KeyInput.KEY_F6));  
        inputManager.addMapping("LoadCalib", new KeyTrigger(KeyInput.KEY_F5));  
        inputManager.addListener(this, new String[]{"LessX","MoreX","LessY","MoreY","LessZ","MoreZ"}); 
        inputManager.addListener(this, new String[]{"ToggleSet","NextVar","PrevVar","SaveCalib","LoadCalib","IncDelta","DecDelta"});
    }
    
    private void change(String name) 
    {
        TweakVariable o = null;
        if (name.equals("LessX"))
            o = tweak.setVarValue(0,false);
        else if (name.equals("MoreX"))
            o = tweak.setVarValue(0,true);
        else if (name.equals("LessY"))
            o = tweak.setVarValue(1,false);
        else if (name.equals("MoreY"))
            o = tweak.setVarValue(1,true);  
        else if (name.equals("LessZ"))
            o = tweak.setVarValue(2,false);
        else if (name.equals("MoreZ"))
            o = tweak.setVarValue(2,true);  
        if (o != null)
            System.out.println("variable" + o.getName()  +" set to: " + o.getValue() );
    }

    
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed)
            if (name.equals("ToggleSet"))
            {
                TweakSet ts = tweak.nextSet();
                System.out.println("Tweaking Set: " + ts.getName());
            }
            else if (name.equals("NextVar"))
            {
                TweakVariable tv = tweak.nextVar();
                System.out.println("Tweaking Var: " + tv.getName());
            }
            else if (name.equals("PrevVar"))
            {
                TweakVariable tv = tweak.prevVar();
                System.out.println("Tweaking Var: " + tv.getName());
            }        
            else if (name.equals("SaveCalib"))
            {
                TweakSet ts = tweak.getSelectedSet();
                String filename = pre + ts.getName() + post;
                ts.save(filename);
                System.out.println("Tweaking Set: " + ts.getName() + " file saved to: " + filename);
            }
            else if (name.equals("LoadCalib"))
            {
                TweakSet ts = tweak.getSelectedSet();
                String filename = pre + ts.getName() + post;
                ts.load(filename);
                System.out.println("Tweaking Set: " + ts.getName() + " file loaded from: " + filename);
            }
            else if (name.equals("IncDelta"))
            {
                Object delta = tweak.incDelta();
                System.out.println("Delta set to: " + delta);
            }
            else if (name.equals("DecDelta"))
            {
                Object delta = tweak.decDelta();
                System.out.println("Delta set to: " + delta);
            }           
            else 
            {
                change(name);
            }
    }

    public void update(float tpf) {}
    
}
