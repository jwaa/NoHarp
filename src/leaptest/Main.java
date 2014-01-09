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
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.leapmotion.leap.Controller;
import java.util.ArrayList;
import leaptest.controller.GridGravityControl;
import leaptest.controller.BlockContainerColorControl;
import leaptest.controller.BlockContainerDissolveControl;
import leaptest.controller.BlockContainerShadowControl;
import leaptest.controller.BlockDragControl;
import leaptest.controller.BlockTargetHelperControl;
import leaptest.controller.GridCamControl;
import leaptest.controller.GridRingColorControl;
import leaptest.controller.KeyboardGridCamControl;
import leaptest.controller.KeyboardGridControl;
import leaptest.controller.KeyboardGridSaveControl;
import leaptest.controller.KeyboardTweakerControl;
import leaptest.controller.MouseBlockControl;
import leaptest.controller.Updatable;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.model.BlockModel;
import leaptest.model.Grid;
import leaptest.model.GridCam;
import leaptest.model.LeapCalibrator;
import leaptest.utils.ConfigSettings;
import leaptest.utils.DefaultAppSettings;
import leaptest.utils.Tweaker;
import leaptest.view.BlockCap;
import leaptest.view.MaterialManager;
import leaptest.view.GridLines;
import leaptest.view.GridRing;
import leaptest.view.ModelDisplay;

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
        ConfigSettings config = new ConfigSettings("config.txt"); 
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
        // Init MaterialManager
        MaterialManager.init(assetManager);
        
        // MODELS
        // Model settings...
        int griddim = 7;
        float cameradistance = 100f, cameraangle = FastMath.PI/4f;
        Vector3f blockdims = Vector3f.UNIT_XYZ.mult(6);
            
        // Add models
        BlockContainer world = new BlockContainer();
        GridCam camera = new GridCam(cameradistance, cameraangle, Vector3f.ZERO);
        Grid grid = new Grid(griddim,griddim,griddim, blockdims);
        Block creationblock = new Block(MaterialManager.creationblock, new Vector3f(-grid.getRadius()-2*blockdims.x,blockdims.y/2,0f), blockdims);
        Tweaker tweaker = new Tweaker();
        
        // Populate grid with stored model
        //grid.rotate(0.5f);
        BlockModel bm = new BlockModel(config.getValue("ModelFile"));
        bm.populateGrid(MaterialManager.normal, grid);
        
        // VIEWS
        // Set viewports
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        
        if (config.isSet("ShowModelImage"))
        {
            ModelDisplay modelpicture = new ModelDisplay(assetManager,settings,1);
            guiNode.attachChild(modelpicture);
        }
        // Build scene from view models
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
        BlockCap blockcap = new BlockCap(blockdims);
        BlockCap cblockcap = (BlockCap) blockcap.clone();
        cblockcap.move(creationblock.getLocalTranslation());
        cblockcap.rotate(-FastMath.PI*0.5f, 0, 0);
        cblockcap.setMaterial(creationblock.getMaterial());
        cblockcap.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(cblockcap);
        
        // Add lights
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White.mult(0.2f));
        sun.setDirection(Vector3f.UNIT_Y.negate());
        rootNode.addLight(sun);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.8f));
        rootNode.addLight(al);
        
        // FILTERS aka post-processors (order matters!!)
        // Add shadows
        final int SHADOWMAP_SIZE=1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setEnabledStabilization(false);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
        
        
        // CONTROLS
        // Set-up looping controllers (order matters!!)
        controllers = new ArrayList<Updatable>();
  
        // Create a Leap Motion interface and put it within the calibrator
        leap = new Controller();
        LeapCalibrator calib = new LeapCalibrator(leap);
        //calib.loadFromFile(config.getValue("CalibFile"));

        if (config.isSet("Leap"))
        {
            controllers.add(new LeapHandControl(calib, handmodel));
        }

        // Add keyboard control
        controllers.add(new KeyboardControl(this)); 
        if (config.isSet("Debug"))
        {
            tweaker.registerTweakable(calib);
            controllers.add(new KeyboardTweakerControl(inputManager,tweaker,config.getValue("SetFolder"),config.getValue("SetExtension")));
            controllers.add(new KeyboardGridSaveControl(inputManager,grid,config.getValue("ModelFile")));
        }
        
        BlockDragControl bdc = null;
        if (config.isSet("MouseAndKeyboard"))
        {
            // Add keyboard control
            controllers.add(new KeyboardGridControl(inputManager,grid));
            controllers.add(new KeyboardGridCamControl(inputManager,camera));

            // Add mouse control
            bdc = new MouseBlockControl(inputManager,cam,world,grid,creationblock);
            controllers.add(bdc);
        }

        // Add model effectors
        controllers.add(new GridCamControl(cam,camera));
        controllers.add(new GridGravityControl(grid,world));
        controllers.add(new BlockContainerDissolveControl(world));     
       
        // Add visual effectors
        if (bdc != null)
            controllers.add(new BlockTargetHelperControl(bdc, rootNode, blockdims));
        controllers.add(new BlockContainerColorControl(grid));
        controllers.add(new BlockContainerColorControl(world)); 
        controllers.add(new GridRingColorControl(grid,gridring));
        controllers.add(new BlockContainerShadowControl(grid,blockdims,blockcap));
        controllers.add(new BlockContainerShadowControl(world,blockdims,blockcap));
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