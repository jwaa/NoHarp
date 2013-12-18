/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author silvandeleemput
 */
public class ShadowPlane extends Geometry {
    
    public ShadowPlane(AssetManager assetManager, float size)
    {
        super("Floor", new Quad(size,size));
        setLocalTranslation(-size/2, -0.1f , size/2);
        rotate(-FastMath.PI*0.5f, 0f, 0f);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.0f,0.0f,0.0f,0.4f));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        setMaterial(mat);
        setQueueBucket(RenderQueue.Bucket.Transparent);      
    }
    
}
