/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 *
 * @author silvandeleemput
 */
public class ConfigSettings {
    
    private ArrayList<Entry<String,String>> settings;
    
    public ConfigSettings(String filename)
    {
        settings = new ArrayList<Entry<String,String>>();
        File f = new File(filename);
        try
        {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine())!=null)
            {
                int pos = -1;
                if ((pos = line.indexOf(" "))!=-1 && !line.contains("%"))
                {
                    AbstractMap.SimpleEntry<String,String> entry = 
                            new AbstractMap.SimpleEntry<String,String>(
                                line.substring(0, pos),
                                line.substring(pos+1, line.length())
                            );
                    settings.add(entry);
                }
            }
            br.close();
            fr.close();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    public boolean isSet(String key)
    {
        Boolean ret = false;
        for (Entry<String,String> entry : settings)
            if (entry.getKey().equals(key))
                return entry.getValue().equals("1");
        return ret;
    }

    public String getValue(String key) {
        String ret = null;
        for (Entry<String,String> entry : settings)
            if (entry.getKey().equals(key))
                return entry.getValue();
        return ret;
    }
    
    public void setValue(String key, String value)
    {
         for (Entry<String,String> entry : settings)
            if (entry.getKey().equals(key))
               entry.setValue(value);
    }
    
    public boolean save(String filename)
    {
        File f = new File(filename);
        if (!f.exists())
            return false;
        try
        {
            FileWriter fr = new FileWriter(f);
            BufferedWriter br = new BufferedWriter(fr);
            for (Entry<String,String> entry : settings)
            {
                br.append(entry.getKey());
                br.append(" ");
                br.append(entry.getValue());
                br.append("\n");
            }
            br.close();
            fr.close();
            return true;
        } catch (Exception e) 
        {
            e.printStackTrace();
        }        
        return false;
    }
    
}
