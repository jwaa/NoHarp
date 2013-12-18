/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author silvandeleemput
 */
public class MaterialManager {
    
    public static Material normal, lifted, falling, gridnormal, ringrotate, creationblock, floor, leaphand;
     
    private MaterialManager() {}
    
    public static Material generateMaterial(AssetManager assetManager, ColorRGBA color)
    {
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat.setColor("Color", color);        
        //mat.getAdditionalRenderState().setWireframe(true);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);    
        mat.setColor("Diffuse",color); 
        mat.setColor("Ambient", color.mult(0.4f));
        return mat;
    }
    
    public static void init(AssetManager assetManager)
    {
        normal = generateMaterial(assetManager, ColorRGBA.Blue);
        lifted = generateMaterial(assetManager, ColorRGBA.Orange);
        falling = generateMaterial(assetManager, ColorRGBA.White);
        gridnormal = generateMaterial(assetManager, ColorRGBA.Red);
        ringrotate = generateMaterial(assetManager, ColorRGBA.Orange);  
        creationblock = generateMaterial(assetManager, ColorRGBA.Blue);
        floor = generateMaterial(assetManager, new ColorRGBA(0.3f,0.3f,0.70f,1.0f));
        leaphand = generateMaterial(assetManager, new ColorRGBA(0f,1.0f,0.f,0.3f));
        leaphand.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
    }
    
}
