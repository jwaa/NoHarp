package leaptest;

import leaptest.view.HandView;
import leaptest.view.Floor;
import leaptest.controller.KeyboardControl;
import leaptest.controller.LeapHandControl;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.leapmotion.leap.Controller;
import java.util.ArrayList;
import leaptest.controller.GridGravityControl;
import leaptest.controller.BlockContainerColorControl;
import leaptest.controller.BlockContainerDissolveControl;
import leaptest.controller.GridCamControl;
import leaptest.controller.GridRingColorControl;
import leaptest.controller.KeyboardGridCamControl;
import leaptest.controller.KeyboardGridControl;
import leaptest.controller.MouseBlockControl;
import leaptest.controller.Updatable;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.model.Grid;
import leaptest.model.GridCam;
import leaptest.view.MaterialManager;
import leaptest.view.GridLines;
import leaptest.view.GridRing;

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
        // Init stuff
        MaterialManager.init(assetManager);
        
        // MODELS
        // Model settings...
        float blocksize = 6f;
        int griddim = 7;
        float cameradistance = 100f, cameraangle = FastMath.PI/4f;
        
        // Add models
        BlockContainer world = new BlockContainer();
        GridCam camera = new GridCam(cameradistance,cameraangle, Vector3f.ZERO);
        Grid grid = new Grid(griddim,griddim,griddim, Vector3f.UNIT_XYZ.mult(blocksize));
        Block creationblock = new Block(MaterialManager.creationblock,new Vector3f(-grid.getRadius()-2*blocksize,blocksize/2,0f),Vector3f.UNIT_XYZ.mult(blocksize)),
              selected = null;
        
        // Do some random stuff with the models for testing...
        grid.rotate(0.5f);
        grid.addBlock(new Block(MaterialManager.normal,new Vector3f(0f,blocksize/2,0f),Vector3f.UNIT_XYZ.mult(blocksize)));
        grid.addBlock(new Block(MaterialManager.normal,new Vector3f(-17f,blocksize/2,0f),Vector3f.UNIT_XYZ.mult(blocksize)));
        grid.addBlock(new Block(MaterialManager.normal,new Vector3f(-17f+blocksize,blocksize/2,0f),Vector3f.UNIT_XYZ.mult(blocksize)));
        
        // VIEWS

        
        // Add views         
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        GridRing gridring = new GridRing(grid.getRadius());
        HandView handmodel = new HandView(assetManager);
        Floor floor = new Floor(300);
        GridLines gridlines = new GridLines(grid.getDimensions()[0]+2,grid.getCellDimensions().x,grid.getRadius());
        rootNode.attachChild(grid);
        grid.attachChild(gridlines);
        grid.attachChild(gridring);        
        rootNode.attachChild(handmodel);
        rootNode.attachChild(floor);
        rootNode.attachChild(world);
        rootNode.attachChild(creationblock);
        
        // Add lights
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(Vector3f.UNIT_Y.negate());
        rootNode.addLight(sun);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
        
        // Cast shadows
        final int SHADOWMAP_SIZE=1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setEnabledStabilization(false);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        dlsr.setLight(sun);
        
        viewPort.addProcessor(dlsr);
        /*DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(sun);
        dlsf.setEnabled(true);
        fpp.addFilter(dlsf);
        viewPort.addProcessor(fpp);
        */
                // Add filters for edge coloring
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        CartoonEdgeFilter cef = new CartoonEdgeFilter();
        cef.setEdgeWidth(0.75f);
        fpp.addFilter(cef);
        viewPort.addProcessor(fpp);
        
        // CONTROLS
        // Set-up looping controllers (order matters!!)
        controllers = new ArrayList<Updatable>();
        
        // Create a Leap Motion controller
        leap = new Controller();
        controllers.add(new LeapHandControl(leap, handmodel, new Vector3f(0.1f,0.1f,0.1f)));
        //controllers.add(new GestureCreateControl(leap,world,blocksize));

        // Add keyboard control
        controllers.add(new KeyboardControl(this));  
        controllers.add(new KeyboardGridControl(inputManager,grid));
        controllers.add(new KeyboardGridCamControl(inputManager,camera));
        
        // Add mouse control
        controllers.add(new MouseBlockControl(inputManager,cam,world,grid,selected,creationblock));

        // Adds basic effectors
        controllers.add(new GridRingColorControl(grid,gridring));
        controllers.add(new GridCamControl(cam,camera));

      
        controllers.add(new GridGravityControl(grid,world));
        controllers.add(new BlockContainerDissolveControl(world));
        
        controllers.add(new BlockContainerColorControl(grid,dlsr,viewPort));
        controllers.add(new BlockContainerColorControl(world,dlsr,viewPort)); 
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