package leaptest.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Gaat er vanuit dat eerste deel van de models easy zijn, 
 * tweede deel normal en derde deel hard
 * TODO: export uit map halen
 * @author Janne Weijkamp
 */
public class TaskManager {
    
    /*
     * bm contains a list of 9 random models: 3 easy, 3 normal, 3 hard (in that order)
     */
    private ArrayList <BlockModel> bm;
    String [] all_tasks = new String [9];
    private int counter;
    private int number_of_tasks = 9;
    
    public TaskManager(String path)
    {
        counter = 0;
        bm = new ArrayList<BlockModel>();
        
        File folder = new File(path);
        File[] listOfFilesTemp = folder.listFiles();
        Arrays.sort(listOfFilesTemp);
        
        ArrayList<File> listOfFiles2 = new ArrayList<File>();
        for(int i =0;i<listOfFilesTemp.length;i++)
        {
            if(!listOfFilesTemp[i].getName().equals("export.model"));
            {
                listOfFiles2.add(listOfFilesTemp[i]);
            }
        }
        File[] listOfFiles = listOfFiles2.toArray(new File[listOfFiles2.size()]);;
        
        String[] listOfFileNames = new String [listOfFiles.length];
        for(int i=0;i<listOfFiles.length;i++)
        {
            listOfFileNames[i] = listOfFiles[i].getName();
        }
        Arrays.sort(listOfFileNames, new LengthCompare());
        
        String[] allEasyModels = new String [listOfFileNames.length/3];
        String[] allNormalModels = new String [listOfFileNames.length/3];
        String[] allHardModels = new String [listOfFileNames.length/3];
        
        for(int i=0;i<listOfFiles.length/3;i++)
        {   
            allEasyModels[i] = listOfFileNames[i];
            allNormalModels[i] = listOfFileNames[i+listOfFiles.length/3];
            allHardModels[i] = listOfFileNames[i+ 2*(listOfFiles.length/3)];
        }
        shuffleArray(allEasyModels); 
        shuffleArray(allNormalModels);
        shuffleArray(allHardModels);
      
        for(int i=0;i<number_of_tasks/3; i++)
        {   
            all_tasks[i] = allEasyModels[i];
            bm.add(new BlockModel(path + allEasyModels[i]));
            //System.out.println(path + allEasyModels[i]);
        }
        for(int i=0;i<number_of_tasks/3; i++)
        {
            all_tasks[i+(number_of_tasks/3)] = allNormalModels[i];
            bm.add(new BlockModel(path + allNormalModels[i]));
            //System.out.println(path + allNormalModels[i]);
        }
        for(int i=0;i<3; i++)
        {
            all_tasks[i+2*(number_of_tasks/3)] = allHardModels[i];
            bm.add(new BlockModel(path + allHardModels[i]));
            //System.out.println(path + allHardModels[i]);
        }
        
        //for(int i=0;i<9; i++)
        //{
        //    System.out.println(Integer.parseInt(all_tasks[i].replaceAll("\\D+","")));
        //}
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
        return Integer.parseInt(all_tasks[counter].replaceAll("\\D+",""));
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

