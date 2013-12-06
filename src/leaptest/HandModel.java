/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author silvandeleemput
 */
public class HandModel extends Node {
    
    public Geometry []fingers;
    
    public HandModel(AssetManager assetManager)
    {
        fingers = new Geometry[100];
        for (int i=0; i<100; i++)
        {
            Sphere b = new Sphere(15,15,1.0f);
            fingers[i] = new Geometry("FingerTip", b);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Blue);
            fingers[i].setMaterial(mat);
        }
    }   
    
}
