/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.model;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Janne Weijkamp
 */
public class TaskManager {
    
    private ArrayList <BlockModel> bm;
    
    public TaskManager(String path)
    {
        bm = new ArrayList<BlockModel>();
        
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        
        for(int i=0;i<listOfFiles.length; i++)
        {
            bm.add(new BlockModel(path + listOfFiles[i].getName()));
            System.out.println(path + listOfFiles[i].getName());
        }
        
        
    
    }
    
}
