/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author silvandeleemput
 */
public class GridLines extends Node {
    
    private float scale, radius;
    private int elements;
    private final float thickness = 0.3f;
    
    
    public GridLines(int elements, float scale, float radius)
    {
        this.radius = radius;
        this.scale = scale;
        this.elements = elements;
        setupCoordinateLines(MaterialManager.gridnormal);
    }
    
    private void setupCoordinateLines(Material mat) {
        for(int i=0; i<=elements; i++){
            float pos = ((float)i-elements/2.0f)*scale;
            if (FastMath.abs(pos) < radius)
            {
                float length = FastMath.sqrt(radius*radius - pos * pos)*2; //scale*elements
                float offset = radius - (radius*2 - length)/2; //-scale*elements/2

                Geometry xQuad = new Geometry("x" + i,new Quad(thickness,length));
                xQuad.setLocalTranslation(new Vector3f(pos,-0.05f,offset));
                xQuad.rotate(-FastMath.PI*0.5f, 0f, 0f);
                xQuad.setMaterial(mat);
                xQuad.setShadowMode(RenderQueue.ShadowMode.Off);
                this.attachChild(xQuad);

                Geometry yQuad = new Geometry("z" + i,new Quad(length,thickness));
                yQuad.setLocalTranslation(new Vector3f(-offset,-0.05f,pos));
                yQuad.rotate(-FastMath.PI*0.5f, 0f, 0f);
                yQuad.setMaterial(mat);
                yQuad.setShadowMode(RenderQueue.ShadowMode.Off);
                this.attachChild(yQuad);
            }
        }
    }    
    
}
