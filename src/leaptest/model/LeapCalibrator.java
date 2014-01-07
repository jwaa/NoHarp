/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.model;

import com.jme3.math.Vector3f;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Vector;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import leaptest.utils.TweakSet;
import leaptest.utils.TweakVariable;
import leaptest.utils.Tweakable;

/**
 * LeapCalibrator data class. Holds the data to convert leap coordinates to 
 * the coordinates of the world. It is designed to be changed at runtime and 
 * can also  save/load the settings to a file.
 * @author Sil van de Leemput
 */
public class LeapCalibrator implements Tweakable {
        
    private Vector3f scale, offset;

    private Controller leapcontroller;
    
    public LeapCalibrator(Controller leapcontroller) 
    {
        this(leapcontroller,Vector3f.UNIT_XYZ,Vector3f.ZERO);
    }    
    
    public LeapCalibrator(Controller leapcontroller, Vector3f scale, Vector3f offset) 
    {
        this.leapcontroller = leapcontroller;
        this.scale = scale;
        this.offset = offset;
    }
    
    public Controller getLeapController()
    {
        return leapcontroller;
    }
    
    public Vector3f getScale()
    {
        return scale;
    }

    public Vector3f getOffset()
    {
        return offset;
    }
    
    public void setScale(Vector3f scale)
    {
        this.scale = scale;
    }

    public void setOffset(Vector3f offset)
    {
        this.offset = offset;
    }

    public Vector3f leap2world(Vector leap)
    {
        Vector3f world = new Vector3f(leap.getX(),leap.getY(),leap.getZ());
        return (world.mult(scale)).add(offset);
    }

    public TweakSet initTweakables() 
    {
        TweakSet set = new TweakSet("LeapCalibrator",this);
        set.add(new TweakVariable <Vector3f>("scale",scale));
        set.add(new TweakVariable <Vector3f>("offset",offset));
        return set;
    }

    public void setVariable(TweakVariable variable) 
    {
        boolean isScale = variable.getName().equals("scale");
        boolean isVector3f = isScale || variable.getName().equals("offset");
        if (isVector3f)
        {
            Vector3f vec = (Vector3f) variable.getValue();
            if (isScale)
                scale = vec;
            else
                offset = vec;
        }
    }
    
    @Override
    public String toString()
    {
        return scale + "\n" + offset;
    }
}
