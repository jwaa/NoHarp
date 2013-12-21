/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Torus;

/**
 *
 * @author silvandeleemput
 */
public class GridRing extends Geometry {
    
    private static final float rimProportion = 0.05f;
    private static final int circleDefinition = 40;
    
    public GridRing(float radius)
    {
        super("GridCircleOutline", new Torus(circleDefinition, 2, radius * rimProportion, radius));
        rotate(-FastMath.PI*0.5f, 0f, 0f);
        setMaterial(MaterialManager.gridnormal);
        setShadowMode(RenderQueue.ShadowMode.Receive);
    }  
    
}
