/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.utils;

import com.jme3.math.Vector3f;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author silvandeleemput
 */
public class TweakSet extends ArrayList<TweakVariable <?>> {
    
    private String name;
    private Tweakable tweak;
    
    public TweakSet(String name, Tweakable tweak)
    {
        this.name = name;
        this.tweak = tweak;
    }

    public boolean updateVariable(TweakVariable <?> var)
    {
        if (this.contains(var))
        {
            this.set(this.indexOf(var),var);
            tweak.setVariable(var);
            return true;
        }
        return false;
    }
    
    public String getName()
    {
        return name;
    }
    
    public boolean load(String filename)
    {
        File f = new File(filename);
        if (!f.exists())
            return false;
        try
        {
            Scanner scan = new Scanner(f);
            for (TweakVariable<?> var : this)
            {
                String []s = scan.nextLine().split(" ", 3);
                if (s[1].equals("Vector3f"))
                {
                    String []s2 = s[2].replace("(", "").replace(")","").split(",");
                    var.setValue(new Vector3f((float) Float.parseFloat(s2[0]),(float) Float.parseFloat(s2[1]),(float) Float.parseFloat(s2[2])));
                } else if (s[1].equals("Float")) {
                    var.setValue(Float.valueOf(s[2]));
                } else if (s[1].equals("Integer")) {
                    var.setValue(Integer.decode(s[2]));
                } else if (s[1].equals("Boolean")) {
                    var.setValue(Boolean.parseBoolean(s[2]));
                }
                tweak.setVariable(var);
            }
            scan.close();

        } catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }  
    
    public void save(String filename)
    {
        File f = new File(filename);
        try
        {
            FileWriter fr = new FileWriter(f);
            BufferedWriter br = new BufferedWriter(fr);
            
            for (TweakVariable<?> var : this)
            {
                br.write(var.toString());
                br.newLine();
            }
            br.close();
            fr.close();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }    
}
