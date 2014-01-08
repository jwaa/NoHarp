/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        {
            //System.out.println("-" + entry.getKey() + "-");
            if (entry.getKey().equals(key))
            {
                //System.out.println("-" + entry.getValue()+ "-");
                return entry.getValue().equals("1");
            }
        }
        return ret;
    }

    public String getValue(String key) {
        String ret = null;
        for (Entry<String,String> entry : settings)
            if (entry.getKey().equals(key))
                return entry.getValue();
        return ret;
    }
    
}
