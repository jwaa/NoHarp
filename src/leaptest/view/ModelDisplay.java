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
import leaptest.model.BlockModel;
import leaptest.model.Grid;
import leaptest.model.TaskManager;

/**
 *
 * @author silvandeleemput
 */
public class ModelDisplay extends Node implements Updatable
{

    private AssetManager assetManager;
    private final static float SCALE = 21f;
    private final static float OFFSET_X = 10f;
    private final static float OFFSET_Y = 10f;
    private Picture pic;
    private TaskManager taskman;
    private Grid grid;
    private String imgbasestr;
    private int prevtaskid;
    private float prevrota;
    private AppSettings settings;

    public ModelDisplay(AssetManager assetManager, AppSettings settings, TaskManager taskman, Grid grid, String path)
    {
        super("PictureNode");
        pic = new Picture("ModelPicture");
        this.taskman = taskman;
        this.assetManager = assetManager;
        this.grid = grid;
        this.imgbasestr = path;
        this.settings = settings;
        this.attachChild(pic);
        prevtaskid = taskman.getTaskId();
        prevrota = grid.getRotation();
        newModel();
        updateModel();
    }

    private void newModel()
    {
        BlockModel bm = taskman.getTask();
        pic.setImage(assetManager, imgbasestr + taskman.getTaskId() + ".jpg", true);
        pic.setWidth(bm.getWidth() * SCALE);
        pic.setHeight(bm.getHeight() * SCALE);
        pic.setPosition(settings.getWidth() - bm.getWidth() * SCALE - OFFSET_X, settings.getHeight() - bm.getHeight() * SCALE - OFFSET_Y);
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
        } else if (prevrota != grid.getRotation())
            updateModel();
        prevtaskid = taskman.getTaskId();
        prevrota = grid.getRotation();
    }
}
