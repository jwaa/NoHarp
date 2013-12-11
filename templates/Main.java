package leaptest;

import leaptest.view.HandView;
import leaptest.view.Floor;
import leaptest.controller.KeyboardControl;
import leaptest.controller.LeapHandControl;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.leapmotion.leap.Controller;
import java.util.ArrayList;
import leaptest.controller.BlockContainerControl;
import leaptest.controller.GridCamControl;
import leaptest.controller.GridControl;
import leaptest.controller.KeyboardGridCamControl;
import leaptest.controller.KeyboardGridControl;
import leaptest.controller.MouseBlockControl;
import leaptest.controller.Updatable;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.model.Grid;
import leaptest.model.GridCam;
import leaptest.view.GridLines;

/**
 * Main class executes a simple JMonkeyEngine Application
 * It loads application settings from "settings.txt"
 * @author sil
 */
public class Main extends SimpleApplication {

    private ConfigSettings config;
    
    private Controller leap;

    private ArrayList<Updatable> controllers;
    
    /**
     * Loads application config settings from file
     * applies settings and fires up application
     * @param args - ignored
     */
    public static void main(String[] args) {
        ConfigSettings config = new ConfigSettings("settings.txt"); 
        Main app = new Main(config);
        DefaultAppSettings.apply(app,config);         
        app.start();
    }

    /**
     * Constructor for application overrides basic AppState
     * and makes configuration locally available
     * @param config ConfigSettings for application
     */
    public Main(ConfigSettings config)
    {
        super(new StatsAppState());
        this.config = config;     
    }
    
    @Override
    public void simpleInitApp() {
        // MODELS
        // Model settings...
        float blocksize = 6f;
        int griddim = 7;
        float cameradistance = 100f, cameraangle = FastMath.PI/4f;
        
        // Add models
        BlockContainer world = new BlockContainer();
        GridCam camera = new GridCam(cameradistance,cameraangle, Vector3f.ZERO);
        Grid grid = new Grid(griddim,griddim,griddim, Vector3f.UNIT_XYZ.mult(blocksize));
        
        // Do some random stuff with the models for testing...
        grid.rotate(0.5f);
        grid.addBlock(new Block(new Vector3f(0f,blocksize/2,0f),Vector3f.UNIT_XYZ.mult(blocksize)));
        world.addBlock(new Block(new Vector3f(-17f,blocksize/2,0f),Vector3f.UNIT_XYZ.mult(blocksize)));
        world.addBlock(new Block(new Vector3f(-17f+blocksize,blocksize/2,0f),Vector3f.UNIT_XYZ.mult(blocksize)));
        
        // VIEWS
        // Add filters for edge coloring
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        fpp.addFilter(new CartoonEdgeFilter());
        viewPort.addProcessor(fpp);
        
        // Add views         
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        Node gridnode = new Node(), gridblocknode = new Node(), worldblocknode = new Node();
        HandView handmodel = new HandView(assetManager);
        Floor floor = new Floor(assetManager,300);
        GridLines gridlines = new GridLines(assetManager,grid.getDimensions()[0],grid.getCellDimensions().x);
        rootNode.attachChild(gridnode);
        gridnode.attachChild(gridlines);
        gridnode.attachChild(gridblocknode);
        rootNode.attachChild(handmodel);
        rootNode.attachChild(floor);
        rootNode.attachChild(worldblocknode);
        
        // CONTROLS
        // Set-up looping controllers (order matters!!)
        controllers = new ArrayList<Updatable>();
        
        // Create a Leap Motion controller
        leap = new Controller();
        controllers.add(new LeapHandControl(leap, handmodel, new Vector3f(0.1f,0.1f,0.1f)));

        // Add keyboard control
        controllers.add(new KeyboardControl(this));  
        controllers.add(new KeyboardGridControl(inputManager,grid));
        controllers.add(new KeyboardGridCamControl(inputManager,camera));
        
        // Add mouse control
        controllers.add(new MouseBlockControl(inputManager,cam,world,grid));

        // Adds basic effectors
        controllers.add(new GridCamControl(cam,camera));
        controllers.add(new GridControl(grid,gridnode));
        controllers.add(new BlockContainerControl(world,worldblocknode,assetManager));
        controllers.add(new BlockContainerControl(grid,gridblocknode,assetManager));
    }
    
    /**
     * Gracefully shutdowns the application and Leap
     */
    public void shutDown()
    {
        leap.delete();
        stop();
    }
    
    @Override
    public void simpleUpdate(float tpf) 
    {
        for (Updatable c : controllers)
            c.update(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {}
    
}