/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import leaptest.controller.Updatable;

/**
 *
 * @author silvandeleemput
 */
public class ModelDisplay extends Node implements Updatable {
    
    AssetManager assetManager;
    private final static float imagesize = 0.25f;
    
    Picture pic;
    
    public ModelDisplay(AssetManager assetManager, AppSettings settings, int model)
    {
        super("PictureNode");
        pic = new Picture("ModelPicture");
        this.assetManager = assetManager;
        pic.setWidth(settings.getWidth()*imagesize);
        pic.setHeight(settings.getHeight()*imagesize);
        pic.setPosition(settings.getWidth()*(1f-imagesize), settings.getHeight()*(1f-imagesize));
        this.attachChild(pic);
        setModel(model);
    }
    
    public void setModel(int model)
    {
        pic.setImage(assetManager, "Textures/CubeTex.png", true);
    }

    public void update(float tpf) {
        
    }
    
}
