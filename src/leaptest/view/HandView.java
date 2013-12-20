/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author silvandeleemput
 */
public class HandView extends Node {
    
    public Geometry []fingers;
    public Geometry []hands;
    public Geometry []spheres;
    
    private final int maxhands = 4;
    private final int maxfingers = 40;
    
    public HandView(AssetManager assetManager)
    {
        setShadowMode(ShadowMode.Off);
        Material mat = MaterialManager.leaphand;
        fingers = new Geometry[maxfingers];
        hands = new Geometry[maxhands];
        spheres = new Geometry[maxhands];
        for (int i=0; i<fingers.length; i++)
        {
            Sphere b = new Sphere(15,15,1.0f);
            fingers[i] = new Geometry("FingerTip", b);
            fingers[i].setMaterial(mat);
            fingers[i].setQueueBucket(Bucket.Transparent);
        }
        for (int i=0; i<hands.length; i++)
        {
            Box b = new Box(3.0f,1.0f,3.0f);
            hands[i] = new Geometry("Hand", b);
            hands[i].setMaterial(mat);
            hands[i].setQueueBucket(Bucket.Transparent);
        }        
        for (int i=0; i<spheres.length; i++)
        {
            Sphere b = new Sphere(15,15,1.0f);
            spheres[i] = new Geometry("HandSphere", b);
            spheres[i].setMaterial(mat);
            spheres[i].setQueueBucket(Bucket.Transparent);
        }
    }   
    
}
