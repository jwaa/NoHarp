package leaptest;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import java.io.IOException;

public abstract class LeapControl implements Control {

    protected Controller controller;
    private LeapListener leap;
    
    public LeapControl(Controller control)
    {
        this.controller = control;
        leap = new LeapListener(this);
        controller.addListener(leap);   
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public abstract void setSpatial(Spatial spatial);

    public abstract void update(float tpf);

    protected abstract void onInit(Controller leap);
    
    protected abstract void onFrame(Controller leap);
    
    public void render(RenderManager rm, ViewPort vp) {}

    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }      
    
    private class LeapListener extends Listener {

        private LeapControl control;
        
        public LeapListener(LeapControl control)
        {
            this.control = control;
        }
        
        @Override
        public void onInit(Controller arg0)
        {
            control.onInit(arg0);
            //    System.out.println("Init");
        }

        @Override
        public void onConnect(Controller arg0)
        {
                System.out.println("Connect");
        }

        @Override
        public void onDisconnect(Controller arg0)
        {
                System.out.println("Disconnect");
        }

        @Override
        public void onExit(Controller arg0)
        {
                System.out.println("Exit");
        }	

        @Override
        public void onFrame(Controller controller)
        {
            control.onFrame(controller);
        }	
    }
}
