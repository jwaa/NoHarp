package leaptest;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.control.Control;
import com.leapmotion.leap.Controller;
import java.util.ArrayList;

/**
 * test
 * @author sil
 */
public class Main extends SimpleApplication {

    private ConfigSettings config;
    
    private Controller leap;
    private LeapControl leapControl;
    private KeyboardControl keyboardControl;
    private HandModel handmodel;
    private Grid grid;
    private ArrayList<Control> controllers;
    
    public static void main(String[] args) {
        // Load Game config settings from file
        ConfigSettings config = new ConfigSettings("settings.txt"); 
        Main app = new Main(config);
        DefaultAppSettings.apply(app,config);         
        app.start();
    }

    public Main(ConfigSettings config)
    {
        super(new StatsAppState());
        this.config = config;        
    }
    
    @Override
    public void simpleInitApp() {
      
        
        // Add models
        handmodel = new HandModel(assetManager);
        grid = new Grid(assetManager,50);
        
        // Add models to view
        rootNode.attachChild(handmodel);
        rootNode.attachChild(grid);
        
        // Set-up controllers (order matters!!)
        controllers = new ArrayList<Control>();
        
        // Create a Leap Motion controller
        leap = new Controller();
        System.out.println(leap.devices().count());
        handmodel.addControl(new LeapHandControl(leap, new Vector3f(0.1f,0.1f,0.1f)));

        
        // Add keyboard control
        keyboardControl = new KeyboardControl(this);  
        
        controllers.add(new OverviewCamControl(cam,new Vector3f(0,95,50), new Vector3f(0,0,-15)));
    }
    
    public void shutDown()
    {
        leap.delete();
        stop();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        for (Control c : controllers)
            c.update(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}