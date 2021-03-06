/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Log class that allows multiple Loggables to be registered
 * Which logs all class states on the log() method
 * Loggables should call the addEntry() method to add to the Log 
 * @author silvandeleemput
 */
public class Log {
    
    private ArrayList<LogEntry> log;
    
    private ArrayList<Loggable> loggables;
    
    private boolean enabled;

    public Log(boolean enabled)
    {
        this.enabled = enabled;
        log = new ArrayList<LogEntry>();   
        loggables = new ArrayList<Loggable>();
    }    
    
    /**
     * Register a loggable for logging (order counts)
     * @param log loggable to register
     */
    public void addLoggable(Loggable log)
    {
        loggables.add(log);
    }
        
    public enum EntryType {
        ScrollDelta, MouseLocDelta, MouseClick, MouseReleased, KeyPressUp, 
        KeyPressDown, KeyPressLeft, KeyPressRight, CameraRotateDelta, GridRotateDelta,
        NewTask, CreateBlock, DeleteBlock, StartDragBlock, EndDragBlock, MoveBlock,
        SnapBlock, Finger, Hand, Grabbed, SwipedVertical, SwipedHorizontal, Frame
    };
    
    public class LogEntry
    {
        public EntryType type;
        public String value;
        
        public LogEntry(EntryType type, String value)
        {
            this.type = type;
            this.value = value;
        }
        
        @Override
        public String toString()
        {
            //return String.format("%d %s",type, value);
            return type + " " + value;
        }
    }
        
    /**
     * Logs all registered loggables
     */
    public void log()
    {
        if (enabled)
            for (Loggable log : loggables)
                log.log(this);
    }
    
    /**
     * Major method for writing data to the log
     * @param type Type of data
     * @param value Value of data
     */
    public final void addEntry(EntryType type, String value)
    {
        log.add(new LogEntry(type, value));
    }
    
    /**
     * Writes the logged data to a file
     * @param filename filename to write the file to
     */
    public void save(String filename)
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
    
    /**
     * Checks if enabled
     * @return boolean enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Sets enabled state
     * @param enable boolean enabled
     */
    public void setEnabled(boolean enable)
    {
        this.enabled = enable;
    }
}
