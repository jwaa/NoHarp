/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Torus;

/**
 *
 * @author silvandeleemput
 */
public class GridCircleOutline extends Geometry {
    
    private static final float rimProportion = 0.05f;
    private static final int circleDefinition = 40;
    
    public GridCircleOutline(AssetManager assetManager, float radius)
    {
        super("GridCircleOutline", new Torus(circleDefinition, 2, radius * rimProportion, radius));
        rotate(-FastMath.PI*0.5f, 0f, 0f);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        setMaterial(mat);
    }  
    
}
