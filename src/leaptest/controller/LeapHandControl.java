/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import leaptest.view.HandView;

/**
 *
 * @author silvandeleemput
 */
public class LeapHandControl extends LeapControl {
    
    private HandView hand;
    private Frame frame;
    
    private Vector3f scalar;
    
    private boolean showHands = true, 
                  showFingers = true, 
                  showSpheres = false;
    
    public LeapHandControl(Controller control, Vector3f scalar)
    {
        super(control);
        this.scalar = scalar;
    }
    
    public void setSpatial(Spatial spatial)
    {
        if (spatial instanceof HandView)
            this.hand = (HandView) spatial;
        else
            throw new UnsupportedOperationException("Not supported yet."); 
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
                        hand.fingers[i].setLocalTranslation(f.tipPosition().getX()*scalar.x, f.tipPosition().getY()*scalar.y, f.tipPosition().getZ()*scalar.z);
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
                        hand.hands[i].setLocalTranslation(h.palmPosition().getX()*scalar.x, h.palmPosition().getY()*scalar.y, h.palmPosition().getZ()*scalar.z);
                        hand.attachChild(hand.hands[i]);
                    }
                    if (showSpheres)
                    {
                        hand.spheres[i].setLocalTranslation(h.sphereCenter().getX()*scalar.x, h.sphereCenter().getY()*scalar.y, h.sphereCenter().getZ()*scalar.z);
                        hand.spheres[i].setLocalScale(h.sphereRadius()*scalar.x);
                        hand.attachChild(hand.spheres[i]);
                    }
                }
                i++;
            }

//            if (frame.pointables().count()>0)
//            {
//                x = frame.pointables().frontmost().tipPosition().getX(); 
//                y = frame.pointables().frontmost().tipPosition().getY();
//                z = frame.pointables().frontmost().tipPosition().getZ();
//            }
//            hand.setLocalTranslation(x, y/50, z/20);
        }
    }

    protected void onInit(Controller leap)
    {
        System.out.println("Init");
    }
    
    protected void onFrame(Controller leap)
    {
        frame = controller.frame();
    }
}
