/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author silvandeleemput
 */
public class GridLines extends Node {
    
    private float scale;
    private int elements;
    private final float thickness = 0.3f;
    
    public GridLines(AssetManager assetManager, int elements, float scale)
    {
        this.scale = scale;
        this.elements = elements;
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        setupCoordinateLines(assetManager, mat);
    }
    
    private void setupCoordinateLines(AssetManager assetManager, Material mat) {
        for(int i=0; i<=elements; i++){
            Geometry xQuad = new Geometry("x" + i,new Quad(thickness,scale*elements));
            xQuad.setLocalTranslation(new Vector3f(((float)i-elements/2.0f)*scale,0f,scale*elements/2));
            xQuad.rotate(-FastMath.PI*0.5f, 0f, 0f);
            xQuad.setMaterial(mat);
            this.attachChild(xQuad);
            
            Geometry yQuad = new Geometry("z" + i,new Quad(scale*elements,thickness));
            yQuad.setLocalTranslation(new Vector3f(-scale*elements/2,0f,((float)i-elements/2.0f)*scale));
            yQuad.rotate(-FastMath.PI*0.5f, 0f, 0f);
            yQuad.setMaterial(mat);
            this.attachChild(yQuad);
        }
    }    
    
}
