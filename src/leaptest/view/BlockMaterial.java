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
public class BlockMaterial extends Material {
    
    public static Material normal, lifted;
            
    private BlockMaterial(AssetManager assetManager, boolean lifted)
    {
        super(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //Material mat = new Material(assetManager, "Materials/blockwires.j3m"); 
        //Material mat = new Material();
        //mat.getAdditionalRenderState().setWireframe(true);
        this.setColor("Color", (lifted ? ColorRGBA.Orange : ColorRGBA.Blue));
    }
    
    public static void init(AssetManager assetManager)
    {
        normal = new BlockMaterial(assetManager, false);
        lifted = new BlockMaterial(assetManager, true);
    }
    
}
