/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.leapmotion.leap.*;
import leaptest.model.Block;
import leaptest.model.BlockContainer;
import leaptest.model.Grid;
import leaptest.view.MaterialManager;

/**
 *
 * @author Annet
 */
public class GestureGrabControl extends LeapControl {

    //Thresholds
    private final static int GETTING_SMALLER_THRESHOLD = 5;
    private final static int GETTING_BIGGER_THRESHOLD = 3;
    private final static int STAYING_THE_SAME_THRESHOLD = 2;
    private final static double GRABBING_THRESHOLD = 0.98;
    private final static double RELEASE_THRESHOLD = 1.01;
    //Transelations of the coordinates
    private final static float Y_TRANSELATION = -4.5f;
    private Vector3f LEAPSCALE;
    //Attributes to detect grabbing and releasing
    private int gettingSmaller, gettingBigger, stayingTheSame;
    //Attribute to set right or left handiness
    private boolean isRightHanded = true;
    private Frame frame;
    private Frame previousFrame;
    private BlockDragControl bdc;

    public GestureGrabControl(Controller leap, BlockContainer world, Grid grid, Block selected, Block creationblock, Vector3f LEAPSCALE) {//world2Grid uit Grid
        super(leap);
        this.bdc = new BlockDragControl(world, grid, creationblock) {
            @Override
            public void update(float tpf) {
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
        this.LEAPSCALE = LEAPSCALE;
    }

    @Override
    public void update(float tpf) {
        if (frame != null) {
            if (bdc.dragging == null) {
                grab();
            }
            if (bdc.dragging != null) {
                if (!release()) {
                    drag();
                }
            }
            previousFrame = frame;
        }
    }

    /**
     * Checks whether one is trying to grab a block and if so grabs the block.
     */
    private void grab() {
        HandList hands = frame.hands();
        Hand hand = getGrabHand(hands);
        if (previousFrame != null) {
            if (hand == null) {
                return;
            }
            if (hand.scaleFactor(previousFrame) < GRABBING_THRESHOLD) {
                this.gettingSmaller++;
                this.stayingTheSame = 0;
            }
            if (hand.scaleFactor(previousFrame) >= 1.0) {
                this.stayingTheSame++;
            }
            if (this.stayingTheSame > STAYING_THE_SAME_THRESHOLD) {
                this.gettingSmaller = 0;
                this.stayingTheSame = 0;
            }
            if (this.gettingSmaller > GETTING_SMALLER_THRESHOLD) {
                Vector3f coordinates = getTransformedCoordinates(hand);
                bdc.dragging = bdc.getBlockAt(coordinates);
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
    private void drag() {
        HandList hands = frame.hands();
        Hand hand = getGrabHand(hands);
        Vector3f coordinates = getTransformedCoordinates(hand);
        bdc.moveBlock(coordinates);
    }

    /**
     * Checks whether one is releasing the block and if so releases it.
     *
     * @return a boolean which says whether the block is released.
     */
    private boolean release() {
        HandList hands = frame.hands();
        Hand hand = getGrabHand(hands);
        if (previousFrame != null) {
            if (hand == null) {
                return false;
            }
            if (hand.scaleFactor(previousFrame) > RELEASE_THRESHOLD) {
                this.gettingBigger++;
                this.stayingTheSame = 0;
            }
            if (hand.scaleFactor(previousFrame) <= 1.0) {
                this.stayingTheSame++;
            }
            if (this.stayingTheSame > STAYING_THE_SAME_THRESHOLD) {
                this.gettingBigger = 0;
                this.stayingTheSame = 0;
            }
            if (this.gettingBigger > GETTING_BIGGER_THRESHOLD) {
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
    protected void onFrame(Controller leap) {
        frame = controller.frame();
    }

    /**
     * When more hands are visible for the leap the hand which is used for
     * grabbing is selected. This is the right most hand when one is right
     * handed en de left most hand when one is right handed
     *
     * @param hands, list of hands detected by de leap
     * @return the hand which has to grab
     */
    private Hand getGrabHand(HandList hands) {
        if (isRightHanded) {
            return hands.rightmost();
        }
        return hands.leftmost();
    }

    /**
     * Transforms the coordinates of the handpalm so that they fit into the
     * block world.
     *
     * @param hand, the hand of which the coordinates need to be transformed.
     * @return the transformed coordinates.
     */
    private Vector3f getTransformedCoordinates(Hand hand) {
        Vector3f coordinates = new Vector3f(hand.palmPosition().getX(), hand.palmPosition().getY(), hand.palmPosition().getZ());
        coordinates = coordinates.mult(LEAPSCALE);
        coordinates.y = coordinates.y + Y_TRANSELATION;
        return coordinates;
    }
}
