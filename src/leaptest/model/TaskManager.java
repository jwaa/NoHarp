package leaptest.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import leaptest.utils.Log;
import leaptest.utils.Loggable;

/**
 * Gaat er vanuit dat eerste deel van de models easy zijn, 
 * tweede deel normal en derde deel hard
 * TODO: export uit map halen
 * @author Janne Weijkamp
 */
public class TaskManager implements Loggable{
    
    /*
     * bm contains a list of 9 random models: 3 easy, 3 normal, 3 hard (in that order)
     */
    private final static int number_of_tasks = 4;
    
    private ArrayList <BlockModel> bm;
    private String [] all_tasks = new String [number_of_tasks];
    private int counter;
    
    
    // Log data
    private int newTaskID = -1;
    
    public TaskManager(String path)
    {
        counter = -1;
        bm = new ArrayList<BlockModel>();
        
        // Collect a list of filenames of all model files within path
        File folder = new File(path);
        File[] listOfFilesTemp = folder.listFiles();
        ArrayList<File> listOfFiles2 = new ArrayList<File>();
        for(int i=0; i<listOfFilesTemp.length; i++)
        {
            String s = listOfFilesTemp[i].getName();
            if (s.startsWith("model") && s.endsWith(".model"))
                listOfFiles2.add(listOfFilesTemp[i]);
        }
        File[] listOfFiles = new File[listOfFiles2.size()];
        for (int i=0; i<listOfFiles.length; i++)
            listOfFiles[i] = listOfFiles2.get(i);
        String[] listOfFileNames = new String [listOfFiles.length];
        for(int i=0;i<listOfFiles.length;i++)
            listOfFileNames[i] = listOfFiles[i].getName();
        
        Arrays.sort(listOfFileNames, new LengthCompare());

        String[] allEasyModels = new String [listOfFileNames.length/2];
        String[] allNormalModels = new String [listOfFileNames.length/2];
        
        for(int i=0;i<listOfFiles.length/2;i++)
        {   
            allEasyModels[i] = listOfFileNames[i];
            allNormalModels[i] = listOfFileNames[i+listOfFiles.length/2];
        }
        shuffleArray(allEasyModels); 
        shuffleArray(allNormalModels);
      
        for(int i=0;i<number_of_tasks/2; i++)
        {   
            all_tasks[i] = allEasyModels[i];
            bm.add(new BlockModel(path + allEasyModels[i]));
            
        }
        for(int i=0;i<number_of_tasks/2; i++)
        {
            all_tasks[i+(number_of_tasks/2)] = allNormalModels[i];
            bm.add(new BlockModel(path + allNormalModels[i]));
        }
      
        nextTask();
    }

    /**
     * Start next task if one is available
     * @return returns true if nextTask is available
     */
    public boolean nextTask()
    {
        if (!hasNextTask())
           return false;
        counter++;
        newTaskID = getTaskId();
        return true;
    }
    
    /**
     * Gets the number of the current task
     * @return number of current task
     */
    public int getTaskId() 
    {
        return Integer.parseInt(all_tasks[counter].replaceAll("\\D+",""));
    }
 
    /**
     * Checks if there is a task available
     * @return true if a tasks is available
     */
    public boolean hasNextTask()
    {
        return counter+1 < number_of_tasks;
    }
    
    
    /**
     * Gets current task
     * @return current task
     */
    public BlockModel getTask()
    {
        return bm.get(counter);
    }

    public void log(Log log) {
        if(newTaskID != -1)
            log.addEntry(Log.EntryType.NewTask, Integer.toString(newTaskID));
        newTaskID = -1;
    }
    
    private static class LengthCompare implements Comparator<String>
    {
        public int compare(String s1, String s2)
        {
            return (s1.length() - s2.length());
        }
    }
    
    static void shuffleArray(String[] ar)
    {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}

