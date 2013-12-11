/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.SwipeGesture;
import java.util.ArrayList;
import leaptest.model.Grid;

/**
 *
 * @author Jasper van der Waa & Annet Meijers
 */
public class GestureRotateControl extends LeapControl
{
    private final double ROTATION_DELTA = Math.PI;
    private Frame frame;
    private boolean isRightHanded = true;
    private SwipeGesture swipe;
    private Grid grid;
    
    public GestureRotateControl(Controller leap, Grid grid)
    {
        super(leap);
        this.grid = grid;
    }

    @Override
    public void update(float tpf) 
    {
        
    }

    @Override
    protected void onFrame(Controller leap) 
    {
        frame = controller.frame();
        if (frame != null)
        {
            if(isSwiped())
            {
                System.out.println(swipe.direction());
                System.out.println(swipe.direction().getX());
                rotate(1/frame.currentFramesPerSecond());
            }
        }
    }
    
    @Override
    protected void onConnect(Controller leap) 
    {
        leap.enableGesture(Gesture.Type.TYPE_SWIPE);
    }
    
    @Override
    protected void onInit(Controller leap) 
    {
        leap.enableGesture(Gesture.Type.TYPE_SWIPE);
    }
    
    private boolean isSwiped()
    {
        swipe = null;
        ArrayList<SwipeGesture> swipes = getSwipes();
        if (swipes.isEmpty())
        {
            return false;
        }
        for(SwipeGesture s : swipes)
        {
            //if (swipe==null || swipe.position().getX() < s.position().getX())
              //  swipe = s;
            if(!(Math.abs(s.direction().getX())>Math.abs(s.direction().getY())&&Math.abs(s.direction().getX())>Math.abs(s.direction().getZ())))
                swipes.remove(s);
        }
        if (swipes.isEmpty())
            return false;
        for(SwipeGesture s : swipes)
        {
            if(swipe==null || (isRightHanded && s.position().getX() < swipe.position().getX())||(!isRightHanded && s.position().getX() > swipe.position().getX()))
                swipe = s;
        }
        return true;
    }
    
    private void rotate(float tpf)
    {
        if(swipe.direction().getX()>0)
            grid.rotate((float) (ROTATION_DELTA*tpf));
        else if(swipe.direction().getX()<0)
            grid.rotate((float) (-ROTATION_DELTA*tpf));
    }
    
    private ArrayList<SwipeGesture> getSwipes()
    {
        GestureList allGestures = frame.gestures();
        ArrayList<SwipeGesture> swipes = new ArrayList();
        for(Gesture g : allGestures)
        {
            if(g.type() == Gesture.Type.TYPE_SWIPE)
            {
                SwipeGesture oneSwipe = new SwipeGesture(g);
                swipes.add(oneSwipe);
            }
        }
        return swipes;
    }

    private Hand getRotateHand() 
    {
        if(isRightHanded)
        {
            return frame.hands().rightmost();
        }
        else
        {
            return frame.hands().leftmost();
        }
    }
    
}
