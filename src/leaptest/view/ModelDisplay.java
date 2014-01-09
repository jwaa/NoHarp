/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import leaptest.controller.Updatable;
import leaptest.model.Grid;
import leaptest.model.TaskManager;

/**
 *
 * @author silvandeleemput
 */
public class ModelDisplay extends Node implements Updatable {
    
    private AssetManager assetManager;
    private final static float imagesize = 0.25f;
    
    private Picture pic;
    private TaskManager taskman;
    private Grid grid;
    private String imgbasestr;
    
    private int prevtaskid;
    private float prevrota;
    
    public ModelDisplay(AssetManager assetManager, AppSettings settings, TaskManager taskman, Grid grid, String path)
    {
        super("PictureNode");
        pic = new Picture("ModelPicture");
        this.taskman = taskman;
        this.assetManager = assetManager;
        this.grid = grid;
        this.imgbasestr = path;
        pic.setWidth(settings.getWidth()*imagesize);
        pic.setHeight(settings.getHeight()*imagesize);
        pic.setPosition(settings.getWidth()*(1f-imagesize), settings.getHeight()*(1f-imagesize));
        this.attachChild(pic);
        prevtaskid = taskman.getTaskId();
        prevrota = grid.getRotation();
        //setModel(taskman.getTaskId());
    }
    
    private void newModel()
    {
        pic.setImage(assetManager, imgbasestr + taskman.getTaskId() + ".jpg", true);
    }
    
    private void updateModel()
    {
        
    }
    


    public void update(float tpf) 
    {
        if (prevtaskid != taskman.getTaskId())
        {
            newModel();
            updateModel();
        } 
        else if (prevrota != grid.getRotation())
        {
            updateModel();
        }
        prevtaskid = taskman.getTaskId();
        prevrota = grid.getRotation();      
    }
    
}
