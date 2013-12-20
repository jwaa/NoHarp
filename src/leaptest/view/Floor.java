/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author silvandeleemput
 */
public class Floor extends Geometry {
    
    public Floor(int scale)
    {
        super("Floor", new Quad(scale,scale));
        setLocalTranslation(-scale/2, -0.1f , scale/2);
        rotate(-FastMath.PI*0.5f, 0f, 0f);
        setMaterial(MaterialManager.floor);
        setShadowMode(ShadowMode.Receive);
    }  
    
}
