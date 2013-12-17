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
    
    public static Material normal, lifted, falling;
            
    private BlockMaterial(AssetManager assetManager, int col)
    {
        super(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //Material mat = new Material(assetManager, "Materials/blockwires.j3m"); 
        //Material mat = new Material();
        //mat.getAdditionalRenderState().setWireframe(true);
        this.setColor("Color", (col==1 ? ColorRGBA.Orange : (col==2 ? ColorRGBA.White : ColorRGBA.Blue)));
    }
    
    public static void init(AssetManager assetManager)
    {
        normal = new BlockMaterial(assetManager, 0);
        lifted = new BlockMaterial(assetManager, 1);
        falling = new BlockMaterial(assetManager, 2);
    }
    
}
