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
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author silvandeleemput
 */
public class Floor extends Node {
    
    private int scale;
    private Geometry floor;
    
    public Floor(AssetManager assetManager, int scale)
    {
        this.scale = scale;
        Quad b = new Quad(scale,scale);
        
        floor = new Geometry("Floor", b);
        floor.setLocalTranslation(-scale/2, -0.1f , scale/2);
        floor.rotate(-FastMath.PI*0.5f, 0f, 0f);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.3f,0.3f,0.70f,1.0f));
        floor.setMaterial(mat);
        this.attachChild(floor);
    }  
    
}
