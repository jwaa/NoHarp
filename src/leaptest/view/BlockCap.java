/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author silvandeleemput
 */
public class BlockCap extends Geometry {
    
    public BlockCap(Vector3f dimensions)
    {
        super("BlockCap",new Quad(dimensions.x, dimensions.z));
        setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        //rotate(-FastMath.PI*0.5f,0,0);
        move(-dimensions.x/2,dimensions.y/2+0.01f,dimensions.z/2);
    }
    
}
