/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.model;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 *
 * @author silvandeleemput
 */
public class GridCam {
    
    private float distance, rotation;
    private Vector3f focuspoint;
    private Vector3f position;
    
    public GridCam(float distance, float rotation, Vector3f focuspoint)
    {
        this.distance = distance;
        this.rotation = rotation;
        this.focuspoint = focuspoint;
        correctBounds();
    }
    
    public void rotate(float delta)
    {
        rotation += delta;
        correctBounds();
    }
    
    private void correctBounds()
    {
        if (rotation > FastMath.PI*0.5f -0.01f)
            rotation = FastMath.PI*0.5f -0.01f;
        if (rotation < 0.01f)
            rotation = 0.01f;   
        position = new Vector3f(
                0f,
                distance*FastMath.sin(rotation),
                distance*FastMath.cos(rotation)
               );
    }
    
    public Vector3f getFocusPoint()
    {
        return focuspoint;
    }
    
    public Vector3f getPosition()
    {
        return position;
    }
}
