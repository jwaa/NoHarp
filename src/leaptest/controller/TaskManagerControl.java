/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import leaptest.Main;
import leaptest.model.BlockModel;
import leaptest.model.Grid;
import leaptest.model.TaskManager;

/**
 *
 * @author silvandeleemput
 */
public class TaskManagerControl implements Updatable {

    private TaskManager taskmanager;
    private Grid grid;
    private Main main;
    
    private long timeout;
    
    public TaskManagerControl(TaskManager taskmanager, Grid grid, Main main, long timeout)
    {
        this.taskmanager = taskmanager;
        this.grid = grid;
        this.main = main;
        this.timeout = timeout;
    }

    public void completionCheck()
    {
        if (isTaskComplete())
            tryNextTask();
    }
    
    private boolean isTaskComplete()
    {
        if (taskmanager == null)
            return false;
        BlockModel ct = taskmanager.getTask();
        if (ct.getElements() == grid.getBlocks().size())
            return ct.equals(grid);
        return false;
    }
    
    private void tryNextTask()
    {
        if (taskmanager.nextTask())
            grid.removeAllBlocks();    
        else
            main.setShutDown(true);
    }
    
    public void update(float tpf) {
        if (taskmanager.getTimeSinceTaskStart() > timeout)
            tryNextTask();
    }
    
}
