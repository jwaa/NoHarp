/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.asset.AssetManager;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

/**
 *
 * @author silvandeleemput
 */
public class GuiModelPicture extends Picture {
    
    AssetManager assetManager;
    private final static float imagesize = 0.25f;
    public GuiModelPicture(AssetManager assetManager, AppSettings settings, int model)
    {
        super("ModelPicture");
        this.assetManager = assetManager;
        setWidth(settings.getWidth()*imagesize);
        setHeight(settings.getHeight()*imagesize);
        setPosition(settings.getWidth()*(1f-imagesize), settings.getHeight()*(1f-imagesize));
        setModel(model);
    }
    
    public void setModel(int model)
    {
        setImage(assetManager, "Textures/CubeTex.png", true);
    }
    
}
