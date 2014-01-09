/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.math.Vector3f;
import com.leapmotion.leap.*;
import leaptest.model.Block;
import leaptest.model.LeapCalibrator;
import leaptest.utils.TweakSet;
import leaptest.utils.TweakVariable;
import leaptest.utils.Tweakable;

/**
 *
 * @author Annet
 */
public class GestureGrabControl extends LeapControl implements Tweakable
{

    //Thresholds
    private int gettingSmallerThreshold;
    private int gettingBiggerThreshold;
    private int stayingTheSameThreshold;
    private float grabbingThreshold;
    private float releaseThreshold;
    //Marges in which to look for a block
    private float yMarge;
    private float xMarge;
    private float zMarge;
    private float margeSteps;
    //Attributes to detect grabbing and releasing
    private int gettingSmaller, gettingBigger, stayingTheSame;
    //Attribute to set right or left handiness
    private boolean isRightHanded = true;
    //Other attributes
    private Frame frame;
    private Frame previousFrame;
    private BlockDragControl bdc;

    public GestureGrabControl(LeapCalibrator calib, BlockDragControl bdc, boolean isRightHanded)
    {
        super(calib);
        this.bdc = bdc;
        this.gettingSmaller = 0;
        this.gettingBigger = 0;
        this.stayingTheSame = 0;
        this.isRightHanded = isRightHanded;
        gettingSmallerThreshold = 5;
        gettingBiggerThreshold = 2;
        stayingTheSameThreshold = 2;
        grabbingThreshold = 0.98f;
        releaseThreshold = 1.01f;
        yMarge = 1.0f;
        xMarge = 1.0f;
        zMarge = 1.0f;
        margeSteps = 0.5f;
    }

    @Override
    public void update(float tpf)
    {
        if (frame != null)
        {
            if (bdc.dragging == null)
                grab();
            if (bdc.dragging != null)
                if (!release())
                    drag();
            previousFrame = frame;
        }
    }

    /**
     * Checks whether one is trying to grab a block and if so grabs the block.
     */
    private void grab()
    {
        HandList hands = frame.hands();
        Hand hand = getGrabHand(hands);
        if (previousFrame != null)
        {
            if (hand == null)
                return;
            if (hand.scaleFactor(previousFrame) < grabbingThreshold)
            {
                this.gettingSmaller++;
                this.stayingTheSame = 0;
            }
            if (hand.scaleFactor(previousFrame) >= 1.0)
                this.stayingTheSame++;
            if (this.stayingTheSame > stayingTheSameThreshold)
            {
                this.gettingSmaller = 0;
                this.stayingTheSame = 0;
            }
            if (this.gettingSmaller > gettingSmallerThreshold)
            {
                Vector3f coordinates = calib.leap2world(hand.palmPosition());
                bdc.dragging = findBlockWithinMarges(coordinates);
                bdc.liftBlock(bdc.dragging);
                this.gettingSmaller = 0;
                this.stayingTheSame = 0;
            }
        }
    }

    /**
     * Moves the block that is currently hold according to the hand movement and
     * the rules present in the environment.
     */
    private void drag()
    {
        HandList hands = frame.hands();
        Hand hand = getGrabHand(hands);
        Vector3f coordinates = calib.leap2world(hand.palmPosition());
        bdc.moveBlock(coordinates);
    }

    /**
     * Checks whether one is releasing the block and if so releases it.
     *
     * @return a boolean which says whether the block is released.
     */
    private boolean release()
    {
        HandList hands = frame.hands();
        Hand hand = getGrabHand(hands);
        if (previousFrame != null)
        {
            if (hand == null)
                return false;
            if (hand.scaleFactor(previousFrame) > releaseThreshold)
            {
                this.gettingBigger++;
                this.stayingTheSame = 0;
            }
            if (hand.scaleFactor(previousFrame) <= 1.0)
                this.stayingTheSame++;
            if (this.stayingTheSame > stayingTheSameThreshold)
            {
                this.gettingBigger = 0;
                this.stayingTheSame = 0;
            }
            if (this.gettingBigger > gettingBiggerThreshold)
            {
                bdc.releaseBlock();
                this.gettingBigger = 0;
                this.stayingTheSame = 0;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onFrame(Controller leap)
    {
        frame = leap.frame();
    }

    /**
     * When more hands are visible for the leap the hand which is used for
     * grabbing is selected. This is the right most hand when one is right
     * handed en de left most hand when one is right handed
     *
     * @param hands, list of hands detected by de leap
     * @return the hand which has to grab
     */
    private Hand getGrabHand(HandList hands)
    {
        if (isRightHanded)
            return hands.rightmost();
        return hands.leftmost();
    }

    private Block findBlockWithinMarges(Vector3f coordinates)
    {
        Block found = null;
        //Begins at zero and start looking at more positive numbers.
        for (int x = 0; x <= (int) (xMarge / margeSteps); x++)
            for (int z = 0; z <= (int) (zMarge / margeSteps); z++)
                for (int y = 0; y <= (int) (yMarge / margeSteps); y++)
                {
                    Vector3f margeCoordinates = coordinates.clone();
                    margeCoordinates.y += (y * margeSteps);
                    margeCoordinates.z += (z * margeSteps);
                    margeCoordinates.x += (x * margeSteps);
                    found = bdc.getBlockAt(margeCoordinates);
                    if (found != null)
                        return found;
                }

        //Looks at the more negative numbers.
        for (int x = 1; x <= (int) (xMarge / margeSteps); x++)
            for (int z = 1; z <= (int) (zMarge / margeSteps); z++)
                for (int y = 1; y <= (int) (yMarge / margeSteps); y++)
                {
                    Vector3f margeCoordinates = coordinates.clone();
                    margeCoordinates.y -= (y * margeSteps);
                    margeCoordinates.z -= (z * margeSteps);
                    margeCoordinates.x -= (x * margeSteps);
                    found = bdc.getBlockAt(margeCoordinates);
                    if (found != null)
                        return found;
                }

        return found;
    }

    public TweakSet initTweakables()
    {
        TweakSet set = new TweakSet("GrabControl", this);
        set.add(new TweakVariable<Integer>("gettingSmallerThreshold", gettingSmallerThreshold));
        set.add(new TweakVariable<Integer>("gettingBiggerThreshold", gettingBiggerThreshold));
        set.add(new TweakVariable<Integer>("stayingTheSameThreshold", stayingTheSameThreshold));
        set.add(new TweakVariable<Float>("grabbingThreshold", grabbingThreshold));
        set.add(new TweakVariable<Float>("releaseThreshold", releaseThreshold));

        set.add(new TweakVariable<Float>("yMarge", yMarge));
        set.add(new TweakVariable<Float>("xMarge", xMarge));
        set.add(new TweakVariable<Float>("zMarge", zMarge));
        set.add(new TweakVariable<Float>("margeSteps", margeSteps));
        return set;
    }

    private enum Variable
    {

        gettingSmallerThreshold, gettingBiggerThreshold, stayingTheSameThreshold,
        grabbingThreshold, releaseThreshold, yMarge, xMarge, zMarge, margeSteps
    };

    public void setVariable(TweakVariable var)
    {
        Variable variable = Variable.valueOf(var.getName());
        switch (variable)
        {
            case gettingSmallerThreshold:
                if (var.getValue() instanceof Integer)
                    gettingSmallerThreshold = (Integer) var.getValue();
                break;
            case gettingBiggerThreshold:
                if (var.getValue() instanceof Integer)
                    gettingBiggerThreshold = (Integer) var.getValue();
                break;
            case stayingTheSameThreshold:
                if (var.getValue() instanceof Integer)
                    stayingTheSameThreshold = (Integer) var.getValue();
                break;
            case grabbingThreshold:
                if (var.getValue() instanceof Float)
                    grabbingThreshold = (Float) var.getValue();
                break;
            case releaseThreshold:
                if (var.getValue() instanceof Float)
                    releaseThreshold = (Float) var.getValue();
                break;
            case yMarge:
                if (var.getValue() instanceof Float)
                    yMarge = (Float) var.getValue();
                break;
            case xMarge:
                if (var.getValue() instanceof Float)
                    xMarge = (Float) var.getValue();
                break;
            case zMarge:
                if (var.getValue() instanceof Float)
                    zMarge = (Float) var.getValue();
                break;
            case margeSteps:
                if (var.getValue() instanceof Float)
                    margeSteps = (Float) var.getValue();
                break;
        }
    }
}
