/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.math.Vector3f;
import com.leapmotion.leap.*;
import leaptest.model.Block;
import leaptest.model.LeapCalibrator;
import leaptest.utils.Log;
import leaptest.utils.Loggable;
import leaptest.utils.TweakSet;
import leaptest.utils.TweakVariable;
import leaptest.utils.Tweakable;

/**
 *
 * @author Annet
 */
public class GestureGrabControl extends LeapControl implements Tweakable, Loggable
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
    // Log data
    private Vector3f whereGrabbed = new Vector3f();

    public GestureGrabControl(LeapCalibrator calib, BlockDragControl bdc, boolean isRightHanded)
    {
        super(calib);
        this.bdc = bdc;
        this.gettingSmaller = 0;
        this.gettingBigger = 0;
        this.stayingTheSame = 0;
        this.isRightHanded = isRightHanded;
        gettingSmallerThreshold = 3;
        gettingBiggerThreshold = 1;
        stayingTheSameThreshold = 2;
        grabbingThreshold = 0.9779999f;
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
            HandList hands = frame.hands();
            Hand hand = getGrabHand(hands);
            if (frame != null)
            {
                if (bdc.getSelected() == null)
                    if (!grab(hand))
                    {
                        Vector3f coordinates = calib.leap2world(hand.palmPosition());
                        Block grabable = findBlockWithinMarges(coordinates);
                        if (grabable != null)
                            grabable.setOver(true);
                    }
                if (bdc.getSelected() != null)
                    if (!release(hand))
                        drag(hand);
                previousFrame = frame;
            }
        }
    }

    /**
     * Checks whether one is trying to grab a block and if so grabs the block.
     */
    private boolean grab(Hand hand)
    {
        if (previousFrame != null)
        {
            if (hand == null)
                return false;
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
                whereGrabbed = coordinates;
                bdc.liftBlock(findBlockWithinMarges(coordinates));
                this.gettingSmaller = 0;
                this.stayingTheSame = 0;
                return true;
            }
        }
        return false;
    }

    /**
     * Moves the block that is currently hold according to the hand movement and
     * the rules present in the environment.
     */
    private void drag(Hand hand)
    {
        Vector3f coordinates = calib.leap2world(hand.palmPosition());
        bdc.moveBlock(coordinates);
    }

    /**
     * Checks whether one is releasing the block and if so releases it.
     *
     * @return a boolean which says whether the block is released.
     */
    private boolean release(Hand hand)
    {
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
        if ((isRightHanded && calib.getScale().x > 0) || (!isRightHanded && calib.getScale().x < 0))
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

    public void log(Log log)
    {
        if (!whereGrabbed.equals(new Vector3f()))
            log.addEntry(Log.EntryType.Grabbed, whereGrabbed.toString());
        whereGrabbed = new Vector3f();
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
