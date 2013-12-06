/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author silvandeleemput
 */
public class Grid extends Node {
    
    private int scale;
    private Geometry grid;
    
    public Grid(AssetManager assetManager, int scale)
    {
        this.scale = scale;
        Quad b = new Quad(scale,scale);
        
        grid = new Geometry("Surface", b);
        grid.setLocalTranslation(0f, 0f , 0f);
        grid.rotate(-FastMath.PI*0.5f, 0f, 0f);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        grid.setMaterial(mat);
        //this.attachChild(grid);

        setupCoordinateLines(assetManager, mat);
        //this.rotate(0f, -FastMath.PI*0.5f, 0f);
    }
    
    private void setupCoordinateLines(AssetManager assetManager,Material mat) {
        float thickness=0.5f;
        for(int i=-10; i<=10; i++){
            Geometry xQuad = new Geometry("x" + i,new Quad(thickness,scale));
            xQuad.setLocalTranslation(new Vector3f(i*scale/20f,0f,scale/2));
            xQuad.rotate(-FastMath.PI*0.5f, 0f, 0f);
            xQuad.setMaterial(mat);
            this.attachChild(xQuad);
            
            Geometry yQuad = new Geometry("z" + i,new Quad(scale,thickness));
            yQuad.setLocalTranslation(new Vector3f(-scale/2,0f,i*scale/20f));
            yQuad.rotate(-FastMath.PI*0.5f, 0f, 0f);
            yQuad.setMaterial(mat);
            this.attachChild(yQuad);
        }
    }    
    
}
