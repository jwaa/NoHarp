/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.leapmotion.leap.*;

/**
 *
 * @author Annet
 */
public class GestureGrabControl extends LeapControl {
    
    private final static int HAND_PALM_THRESHOLD = 1;
    private Frame frame;
    private Hand previousHand;
    private Controller leap;
    private boolean isRightHanded = true;

    public GestureGrabControl(Controller leap)
    {
        super(leap);
        this.leap = leap;
    }
    
    @Override
    public void update(float tpf) {
        if (frame != null)
        {
            HandList hands = frame.hands();
            Hand hand = getGrabHand(hands);
            if(previousHand != null)
            {
                if(hand == null)
                    return;
                if(Math.abs(hand.palmPosition().getX()-previousHand.palmPosition().getX())<HAND_PALM_THRESHOLD &&
                        Math.abs(hand.palmPosition().getY()-previousHand.palmPosition().getY())<HAND_PALM_THRESHOLD &&
                        Math.abs(hand.palmPosition().getZ()-previousHand.palmPosition().getZ())<HAND_PALM_THRESHOLD)
                {
                    FingerList fingers = hand.fingers();
                    //for (Finger f : fingers)
                }
            }
            previousHand = hand;
        }
    }

    @Override
    protected void onFrame(Controller leap) {
        frame = controller.frame();
    }
    
    private Hand getGrabHand(HandList hands)
    {
        if (isRightHanded)
            return hands.rightmost();
        return hands.leftmost();
    }
    
}
