/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.math.Vector3f;
import com.leapmotion.leap.*;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.model.Grid;
import leaptest.model.LeapCalibrator;

/**
 *
 * @author Annet
 */
public class GestureGrabControl extends LeapControl
{

    //Thresholds
    private final static int GETTING_SMALLER_THRESHOLD = 5;
    private final static int GETTING_BIGGER_THRESHOLD = 2;
    private final static int STAYING_THE_SAME_THRESHOLD = 2;
    private final static double GRABBING_THRESHOLD = 0.98;
    private final static double RELEASE_THRESHOLD = 1.01;
    
    //Transelations of the coordinates
    private final static float Y_TRANSELATION = -4.5f;
    private Vector3f LEAPSCALE;
    
    //Marges in which to look for a block
    private final static float Y_MARGE = 1.0f;
    private final static float X_MARGE = 1.0f;
    private final static float Z_MARGE = 1.0f;
    private final static float MARGE_STEPS = 0.5f;
    
    //Attributes to detect grabbing and releasing
    private int gettingSmaller, gettingBigger, stayingTheSame;
    
    //Attribute to set right or left handiness
    private boolean isRightHanded = true;
    
    //Other attributes
    private Frame frame;
    private Frame previousFrame;
    private BlockDragControl bdc;

    public GestureGrabControl(LeapCalibrator calib, BlockContainer world, Grid grid, Block selected, Block creationblock)
    {//world2Grid uit Grid
        super(calib);
        this.bdc = new BlockDragControl(world, grid, creationblock)
        {
            @Override
            public void update(float tpf)
            {
                this.update(tpf);
            }
        };
        this.gettingSmaller = 0;
        this.gettingBigger = 0;
        this.stayingTheSame = 0;
        bdc.dragging = selected;
        bdc.creationblock = creationblock;
        bdc.world = world;
        bdc.grid = grid;
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
            if (hand.scaleFactor(previousFrame) < GRABBING_THRESHOLD)
            {
                this.gettingSmaller++;
                this.stayingTheSame = 0;
            }
            if (hand.scaleFactor(previousFrame) >= 1.0)
                this.stayingTheSame++;
            if (this.stayingTheSame > STAYING_THE_SAME_THRESHOLD)
            {
                this.gettingSmaller = 0;
                this.stayingTheSame = 0;
            }
            if (this.gettingSmaller > GETTING_SMALLER_THRESHOLD)
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
            if (hand.scaleFactor(previousFrame) > RELEASE_THRESHOLD)
            {
                this.gettingBigger++;
                this.stayingTheSame = 0;
            }
            if (hand.scaleFactor(previousFrame) <= 1.0)
                this.stayingTheSame++;
            if (this.stayingTheSame > STAYING_THE_SAME_THRESHOLD)
            {
                this.gettingBigger = 0;
                this.stayingTheSame = 0;
            }
            if (this.gettingBigger > GETTING_BIGGER_THRESHOLD)
            {
                System.out.println("Release");
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
        for (int x = 0; x <= (int) (X_MARGE / MARGE_STEPS); x++)
        {
            for (int z = 0; z <= (int) (Z_MARGE / MARGE_STEPS); z++)
            {
                for (int y = 0; y <= (int) (Y_MARGE / MARGE_STEPS); y++)
                {
                    Vector3f margeCoordinates = coordinates.clone();
                    margeCoordinates.y += (y * MARGE_STEPS);
                    margeCoordinates.z += (z * MARGE_STEPS);
                    margeCoordinates.x += (x * MARGE_STEPS);
                    found = bdc.getBlockAt(margeCoordinates);
                    if (found != null)
                        break;
                }
                if (found != null)
                    break;
            }
            if (found != null)
                break;
        }

        //Looks at the more negative numbers.
        if (found == null)
            for (int x = 1; x <= (int) (X_MARGE / MARGE_STEPS); x++)
            {
                for (int z = 1; z <= (int) (Z_MARGE / MARGE_STEPS); z++)
                {
                    for (int y = 1; y <= (int) (Y_MARGE / MARGE_STEPS); y++)
                    {
                        Vector3f margeCoordinates = coordinates.clone();
                        margeCoordinates.y -= (y * MARGE_STEPS);
                        margeCoordinates.z -= (z * MARGE_STEPS);
                        margeCoordinates.x -= (x * MARGE_STEPS);
                        found = bdc.getBlockAt(margeCoordinates);
                        if (found != null)
                            break;
                    }
                    if (found != null)
                        break;
                }
                if (found != null)
                    break;
            }
        return found;
    }
}
