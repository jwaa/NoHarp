/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.math.Quaternion;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import leaptest.model.LeapCalibrator;
import leaptest.view.HandView;

/**
 *
 * @author silvandeleemput
 */
public class LeapHandControl extends LeapControl {
    
    private HandView hand;
    private Frame frame;
    
    private boolean showHands = true, 
                  showFingers = true, 
                  showSpheres = false;
    
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
}
