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
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author silvandeleemput
 */
public class Log {
    
    private ArrayList<LogEntry> log;
    
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enable)
    {
        this.enabled = enable;
    }
    
    public enum EntryType {
        Restriction1,Restriction2,Restriction3,CameraType,
        X, Z, R,
        PCUp, PCDown, PCLeft, PCRight, PCYRot,
        NewTarget
    };
    
    public class LogEntry
    {
        public EntryType type;
        public long timestamp;
        public float value;
        
        public LogEntry(EntryType type, long timestamp, float value)
        {
            this.type = type;
            this.timestamp = timestamp;
            this.value = value;
        }
        
        @Override
        public String toString()
        {
            return String.format("%d %s %f",timestamp,type.toString(),value);
            //return "" + timestamp + " " + type + " " + value;
        }
    }
        
    public Log(boolean enabled)
    {
        this.enabled = enabled;
        log = new ArrayList<LogEntry>();        
    }
    
    public void log(Loggable logger, int timestamp)
    {
        if (enabled)
            logger.log(this, timestamp);
    }
    
    public ArrayList<LogEntry> getEntries(long timestamp, int offset)
    {
        ArrayList<LogEntry> ret = new ArrayList<LogEntry>();
        while(offset < log.size() && log.get(offset).timestamp == timestamp)
        {
            ret.add(log.get(offset));
            offset++;
        }    
        return ret;
    }
    
    public final void addEntry(EntryType type, long timestamp, float value)
    {
        log.add(new LogEntry(type,timestamp,value));
    }
    
    public void loadFromFile(String filename)
    {
        log.clear();
        File f = new File(filename);
        try
        {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine())!=null)
            {
                int pos = line.indexOf(" ");
                String tstamp = line.substring(0, pos);
                line = line.substring(pos+1, line.length());
                pos = line.indexOf(" ");
                // Damn you System Locale!!!
                line = line.replace(",", ".");
                addEntry(EntryType.valueOf(line.substring(0, pos)),
                             Integer.decode(tstamp).intValue(),
                             Float.valueOf(line.substring(pos+1, line.length())).floatValue()
                            );
            }
            br.close();
            fr.close();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    public void writeToFile(String filename)
    {
        File f = new File(filename);
        try
        {
            FileWriter fr = new FileWriter(f);
            BufferedWriter br = new BufferedWriter(fr);
            for (LogEntry entry : log)
            {
                br.write(entry.toString());
                br.newLine();
            }
            br.close();
            fr.close();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    public static String getDateTimeString()
    {
        Date d = new Date();
        int day = d.getDay(),
            month = d.getMonth(),
            year = d.getYear()-100,
            hour = d.getHours(),
            minutes = d.getMinutes(),
            seconds = d.getSeconds();
        return String.format("%02d%02d%02d_%02d%02d%02d", 
                day, month, year, hour, minutes, seconds);
    }

    
}
