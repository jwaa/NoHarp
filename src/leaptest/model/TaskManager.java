/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * TODO
 * export eruit halen in map
 * @author Janne Weijkamp
 */
public class TaskManager {
    
    private ArrayList <BlockModel> bm;
    private int counter;
    
    public TaskManager(String path)
    {
        bm = new ArrayList<BlockModel>();
        
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        
        Arrays.sort(listOfFiles);
        String[] listOfFileNames = new String [listOfFiles.length];
        
        for(int i=0;i<listOfFiles.length;i++)
        {   
            listOfFileNames[i] = listOfFiles[i].getName();
        }
        Arrays.sort(listOfFileNames, new LengthCompare());
        
        for(int i=0;i<listOfFiles.length; i++)
        {
            bm.add(new BlockModel(path + listOfFileNames[i]));
            System.out.println(path + listOfFileNames[i]);
        }
    }

    /**
     * Start next task
     */
    public void nextTask()
    {
        counter++;
    }
    
    /**
     * Return number of current task
     * @return 
     */
    public int getTask() 
    {
        return counter;
    }
 
    private static class LengthCompare implements Comparator<String>
    {
        public int compare(String s1, String s2)
        {
            return (s1.length() - s2.length());
        }
    }
}
