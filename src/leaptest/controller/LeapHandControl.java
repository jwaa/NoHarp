/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.math.Quaternion;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import leaptest.model.LeapCalibrator;
import leaptest.utils.Log;
import leaptest.utils.Loggable;
import leaptest.view.HandView;

/**
 *
 * @author silvandeleemput
 */
public class LeapHandControl extends LeapControl implements Loggable {
    
    private HandView hand;
    private Frame frame;
        
    private boolean showHands = true, 
                  showFingers = true, 
                  showSpheres = false;
    
    // Log data 
    private FingerList logFingers =  new FingerList();
    private HandList logHands = new HandList();
    
    public LeapHandControl(LeapCalibrator calib, HandView hand)
    {
        super(calib);
        this.hand = hand;
    }

    public void update(float tpf)
    {
        hand.detachAllChildren();
        if (frame != null)
        {            
            int i=0;
            if (showFingers)
            {
                logFingers = frame.fingers();
                for (Finger f : frame.fingers())
                {
                    if (i<hand.fingers.length)
                    {
                        hand.fingers[i].setLocalTranslation(calib.leap2world(f.tipPosition()));
                        hand.attachChild(hand.fingers[i]);
                    }
                    i++;
                }
            }  
            i=0;
            logHands = frame.hands();
            for (Hand h : frame.hands())
            {
                if (i<hand.hands.length)
                {
                    if (showHands)
                    {
                        Quaternion q = new Quaternion();
                        q.fromAngles(h.direction().pitch(), -h.direction().yaw(), h.palmNormal().roll());
                        hand.hands[i].setLocalRotation(q);
                        hand.hands[i].setLocalTranslation(calib.leap2world(h.palmPosition()));
                        hand.attachChild(hand.hands[i]);
                    }
                    if (showSpheres)
                    {
                        hand.spheres[i].setLocalTranslation(calib.leap2world(h.sphereCenter()));
                        hand.spheres[i].setLocalScale(h.sphereRadius()*calib.getScale().x);
                        hand.attachChild(hand.spheres[i]);
                    }
                }
                i++;
            }
        }
    }

    @Override
    protected void onInit(Controller leap)
    {
        System.out.println("Init");
    }
    
    protected void onFrame(Controller leap)
    {
        frame = leap.frame();
    }

    public void log(Log log) {
        for( int f=0; f<logFingers.count(); f++)
        {
            String str = logFingers.get(f).id() + " " + logFingers.get(f).tipPosition().toString();
            log.addEntry(Log.EntryType.Finger, str);
        }
        for( int h=0; h<logHands.count(); h++)
        {
            String str = logHands.get(h).id() + " " + logHands.get(h).palmPosition().toString() 
                    + " " + getOrientation(logHands.get(h), logHands);
            log.addEntry(Log.EntryType.Hand, str);
        }
    }

    private String getOrientation(Hand hand, HandList logHands) {
        String orientation = "mid";
        if(logHands.leftmost().equals(hand))
            orientation = "left";
        else if(logHands.rightmost().equals(hand))
            orientation = "right";
        return orientation;
    }
}
