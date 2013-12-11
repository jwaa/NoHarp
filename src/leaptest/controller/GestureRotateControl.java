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
import leaptest.model.Grid;

/**
 *
 * @author Jasper van der Waa & Annet Meijers
 */
public class GestureRotateControl extends LeapControl
{
    private Frame frame;
    private boolean isRightHanded = true;
    private SwipeGesture swipe;
    
    public GestureRotateControl(Controller leap, Grid grid)
    {
        super(leap);
    }

    @Override
    public void update(float tpf) 
    {
        if(isSwiped())
        {
            thenRotate;
        }
        
    }

    @Override
    protected void onFrame(Controller leap) 
    {
        frame = controller.frame();
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
        
    }
    
    private GestureList getSwipes()
    {
        GestureList allGestures = frame.gestures();
        GestureList swipes = new GestureList();
        for(Gesture g : allGestures)
        {
            if(g.type() instanceof SwipeGesture)
            {
                swipes.append(g.);
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
