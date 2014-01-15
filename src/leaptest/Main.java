package leaptest;

import leaptest.view.HandView;
import leaptest.view.Floor;
import leaptest.controller.KeyboardDebugControl;
import leaptest.controller.LeapHandControl;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
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
import leaptest.controller.GestureGrabControl;
import leaptest.controller.GestureRotateControl;
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
import leaptest.model.Grid;
import leaptest.model.GridCam;
import leaptest.model.LeapCalibrator;
import leaptest.model.TaskManager;
import leaptest.utils.ConfigSettings;
import leaptest.utils.DefaultAppSettings;
import leaptest.utils.Log;
import leaptest.utils.Tweaker;
import leaptest.view.BlockCap;
import leaptest.view.MaterialManager;
import leaptest.view.GridLines;
import leaptest.view.GridRing;
import leaptest.view.ModelDisplay;

/**
 * Main class executes a simple JMonkeyEngine Application It loads application
 * settings from "settings.txt"
 *
 * @author sil
 */
public class Main extends SimpleApplication
{

    private ConfigSettings config;
    private Controller leap;
    private ArrayList<Updatable> controllers;
    private Log log;
    private boolean stopping;
    private BlockDragControl blockDragControl;
    private LeapHandControl leapHandControl;
    private GestureGrabControl gestureGrabControl;
    private GestureRotateControl gestureRotateControl;
    private MouseBlockControl mouseBlockControl;
    private KeyboardGridControl kbGridControl;
    private KeyboardGridCamControl kbGridCamControl;
    
    /**
     * Loads application config settings from file applies settings and fires up
     * application
     *
     * @param args - ignored
     */
    public static void main(String[] args)
    {
        ConfigSettings config = new ConfigSettings("config.txt");
        Main app = new Main(config);
        DefaultAppSettings.apply(app, config);
        app.start();
    }

    /**
     * Constructor for application overrides basic AppState and makes
     * configuration locally available
     *
     * @param config ConfigSettings for application
     */
    public Main(ConfigSettings config)
    {
        super(new StatsAppState());
        this.config = config;
    }

    @Override
    public void simpleInitApp()
    {
        // Init MaterialManager
        MaterialManager.init(assetManager);

        // MODELS
        // Model settings...
        int griddim = Integer.parseInt(config.getValue("GridSize"));
        float cameradistance = Float.parseFloat(config.getValue("CamDist")), 
                 cameraangle = Float.parseFloat(config.getValue("CamAngle"));
        Vector3f blockdims = Vector3f.UNIT_XYZ.mult(Float.parseFloat(config.getValue("BlockSize")));
        
        // Add models
        log = new Log(config.isSet("Log"));
        BlockContainer world = new BlockContainer();
        GridCam camera = new GridCam(cameradistance, cameraangle, Vector3f.ZERO);
        Grid grid = new Grid(griddim, griddim, griddim, blockdims);
        float creationblockstartpos = (config.isSet("Righthanded") ? 1f : -1f);
        Block creationblock = new Block(MaterialManager.creationblock, new Vector3f(creationblockstartpos*(grid.getRadius() + 2 * blockdims.x), blockdims.y / 2, 0f), blockdims);
        TaskManager taskmanager = (config.isSet("TaskManager") ? new TaskManager(config.getValue("ModelFolder")) : null);
        Tweaker tweaker = new Tweaker();
        
        // VIEWS
        // Set viewports
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);

        // Build scene from view models
        GridRing gridring = new GridRing(grid.getRadius());
        HandView handmodel = new HandView(assetManager);
        Floor floor = new Floor(300);
        GridLines gridlines = new GridLines(grid.getDimensions()[0] + 2, grid.getCellDimensions().x, grid.getRadius());
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
        cblockcap.rotate(-FastMath.PI * 0.5f, 0, 0);
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
        final int SHADOWMAP_SIZE = 1024;
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
        if (config.isSet("Leap"))
        {
            leapHandControl = new LeapHandControl(calib, handmodel);
            blockDragControl = new BlockDragControl(world, grid, creationblock, taskmanager);
            gestureGrabControl = new GestureGrabControl(calib, blockDragControl, config.isSet("Righthanded"));
            gestureRotateControl = new GestureRotateControl(calib, grid, camera);
            
            controllers.add(leapHandControl);
            controllers.add(gestureGrabControl);
            controllers.add(gestureRotateControl);
            controllers.add(new BlockTargetHelperControl(blockDragControl, rootNode, blockdims));
            if (config.isSet("Debug"))
            {
                tweaker.registerTweakable(gestureGrabControl);
                tweaker.registerTweakable(gestureRotateControl);
            }
        }

        // Add keyboard control
        controllers.add(new KeyboardDebugControl(this));
        if (config.isSet("Debug"))
        {
            tweaker.registerTweakable(calib);
            controllers.add(new KeyboardTweakerControl(inputManager, tweaker, config.getValue("SetFolder"), config.getValue("SetExtension")));
            if (config.isSet("DebugGridSaver"))
                controllers.add(new KeyboardGridSaveControl(inputManager, grid, config.getValue("ModelFolder") + config.getValue("ModelFile")));
        }


        if (config.isSet("MouseAndKeyboard"))
        {
            // Add keyboard control
            kbGridControl = new KeyboardGridControl(inputManager, grid);
            kbGridCamControl = new KeyboardGridCamControl(inputManager, camera);
            controllers.add(kbGridControl);
            controllers.add(kbGridCamControl);

            // Add mouse control
            blockDragControl = new BlockDragControl(world, grid, creationblock, taskmanager);
            mouseBlockControl = new MouseBlockControl(inputManager, cam, blockDragControl);
            controllers.add(mouseBlockControl);
            controllers.add(new BlockTargetHelperControl(blockDragControl, rootNode, blockdims));
        }

        // Add model effectors
        controllers.add(new GridCamControl(cam, camera));
        controllers.add(new GridGravityControl(grid, world));
        controllers.add(new BlockContainerDissolveControl(world));

        // Add visual effectors
        if (config.isSet("ShowModelImage") && config.isSet("TaskManager"))
        {
            ModelDisplay modeldisplay = new ModelDisplay(assetManager, settings, taskmanager, grid, config.getValue("ModelImgBase"));
            guiNode.attachChild(modeldisplay);
            controllers.add(modeldisplay);
        }
        controllers.add(new BlockContainerColorControl(grid));
        controllers.add(new BlockContainerColorControl(world));
        controllers.add(new GridRingColorControl(grid, gridring));
        controllers.add(new BlockContainerShadowControl(grid, blockdims, blockcap));
        controllers.add(new BlockContainerShadowControl(world, blockdims, blockcap));
        
        // Add loggables to log (order matters for the log order in log.txt)
        if (config.isSet("Log"))
        {
            if(config.isSet("Leap"))
            {
                log.addLoggable(leapHandControl);
                log.addLoggable(gestureGrabControl);
                log.addLoggable(gestureRotateControl);
            }
            else if (config.isSet("MouseAndKeyboard"))
            {
                log.addLoggable(mouseBlockControl);
                log.addLoggable(kbGridControl);
                log.addLoggable(kbGridCamControl);
            }
            log.addLoggable(blockDragControl);
            log.addLoggable(camera);
            log.addLoggable(grid);
            log.addLoggable(taskmanager);
        }
    }

    /**
     * Set shutdown flag
     * @param shutdown true to shutdown on next update
     */
    public void setShutDown(boolean shutdown)
    {
        stopping = shutdown;
    }
    
    @Override
    public void simpleUpdate(float tpf)
    {
        log.log();
        for (Updatable c : controllers)
            c.update(tpf);
        if (stopping)
            shutDown();
    }

    @Override
    public void simpleRender(RenderManager rm)
    {
    }
    
    /**
     * Gracefully shutdowns the application and Leap and stores log to file
     */
    private void shutDown()
    {
        System.out.println("Shutting down...");
        leap.delete();
        if (log.isEnabled())
        {
            String logfile = config.getValue("LogFolder") + config.getValue("UserNumber") + ".log";
            log.save(logfile);
            System.out.println("Saved log to: " + logfile);
        }
        stop();
    }
}
