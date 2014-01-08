package leaptest.controller;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import leaptest.model.LeapCalibrator;

public abstract class LeapControl implements Updatable {

    private Controller controller;
    private LeapListener leap;
    protected LeapCalibrator calib;
    
    public LeapControl(LeapCalibrator calib)
    {
        this.calib = calib;
        controller = calib.getLeapController();
        leap = new LeapListener(this);
        controller.addListener(leap);   
    }

    public abstract void update(float tpf);

    protected void onInit(Controller leap) {}
    
    protected abstract void onFrame(Controller leap);
    
    protected void onExit(Controller leap) {}
 
    protected void onConnect(Controller leap) {}
 
    protected void onDisconnect(Controller leap) {}   
    
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
            control.onConnect(arg0);
        }

        @Override
        public void onDisconnect(Controller arg0)
        {
            control.onDisconnect(arg0);
        }

        @Override
        public void onExit(Controller arg0)
        {
            control.onExit(arg0);
        }	

        @Override
        public void onFrame(Controller controller)
        {
            control.onFrame(controller);
        }	
    }
}
