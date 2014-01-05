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

/**
 * LeapCalibrator data class. Holds the data to convert leap coordinates to 
 * the coordinates of the world. It is designed to be changed at runtime and 
 * can also  save/load the settings to a file.
 * @author Sil van de Leemput
 */
public class LeapCalibrator {
        
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
    
    public boolean loadFromFile(String filename)
    {
        File f = new File(filename);
        if (!f.exists())
            return false;
        try
        {
            Scanner scan = new Scanner(f);
            float []data = new float[6]; 
            int i=0;
            while (scan.hasNextLine())
            {
                String []s2 = scan.nextLine().replace("(", "").replace(")","").split(",");
                for (int j=0; j<s2.length; j++)
                {
                    data[i] = (float) Float.parseFloat(s2[j]);
                    i++;
                }
            }
            scan.close();
            scale = new Vector3f(data[0],data[1],data[2]);
            offset = new Vector3f(data[3],data[4],data[5]);
        } catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public void writeToFile(String filename)
    {
        File f = new File(filename);
        try
        {
            FileWriter fr = new FileWriter(f);
            BufferedWriter br = new BufferedWriter(fr);
            br.write(scale.toString());
            br.newLine();
            br.write(offset.toString());
            br.close();
            fr.close();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
}
