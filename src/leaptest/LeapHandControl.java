/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;

/**
 *
 * @author silvandeleemput
 */
public class LeapHandControl extends LeapControl {
    
    private HandModel hand;
    private Frame frame;
    
    private Vector3f scalar;
    
    public LeapHandControl(Controller control, Vector3f scalar)
    {
        super(control);
        this.scalar = scalar;
    }
    
    public void setSpatial(Spatial spatial)
    {
        if (spatial instanceof HandModel)
            this.hand = (HandModel) spatial;
        else
            throw new UnsupportedOperationException("Not supported yet."); 
    }

    public void update(float tpf)
    {
        hand.detachAllChildren();
        if (frame != null)
        {
            int i=0;
            for (Finger f : frame.fingers())
            {
                if (i<hand.fingers.length)
                {
                    hand.fingers[i].setLocalTranslation(f.tipPosition().getX()*scalar.x, f.tipPosition().getY()*scalar.y, f.tipPosition().getZ()*scalar.z);
                    hand.attachChild(hand.fingers[i]);
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
