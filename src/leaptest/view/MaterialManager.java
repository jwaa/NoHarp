/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author silvandeleemput
 */
public class MaterialManager extends Material {
    
    public static Material normal, lifted, falling, gridnormal, ringrotate;
     
    private MaterialManager(AssetManager assetManager, int col)
    {
        super(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //Material mat = new Material(assetManager, "Materials/blockwires.j3m"); 
        //Material mat = new Material();
        //mat.getAdditionalRenderState().setWireframe(true);
        
        ColorRGBA color;
        switch (col)
        {
            case 1: 
            case 4:                
                color = ColorRGBA.Orange;
                break;
            case 2:
                color = ColorRGBA.White;
                break;
            case 3: 
                color = ColorRGBA.Red;
                break;               
            default:    
                color = ColorRGBA.Blue;
        }
        this.setColor("Color", color);
    }
    
    public static void init(AssetManager assetManager)
    {
        normal = new MaterialManager(assetManager, 0);
        lifted = new MaterialManager(assetManager, 1);
        falling = new MaterialManager(assetManager, 2);
        gridnormal = new MaterialManager(assetManager, 3);
        ringrotate = new MaterialManager(assetManager, 4);        
    }
    
}
