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

    //private final static int HAND_PALM_THRESHOLD = 1;
    private final static int GETTING_SMALLER_THRESHOLD = 5;
    private final static int GETTING_BIGGER_THRESHOLD = 3;
    private final static int STAYING_THE_SAME_THRESHOLD = 2;
    private final static double GRABBING_THRESHOLD = 0.98;
    private final static double RELEASE_THRESHOLD = 1.01;
    private final static float Y_TRANSELATION = -3.0f;
    private Frame frame;
    private Frame previousFrame;
    private Controller leap;
    private boolean isRightHanded = true;
    private int gettingSmaller, gettingBigger;
    private int stayingTheSame;
    private Block dragging, creationBlock;
    private BlockContainer world;
    private Grid grid;
    private Vector3f LEAPSCALE;

    public GestureGrabControl(Controller leap, BlockContainer world, Grid grid, Block selected, Block creationblock, Vector3f LEAPSCALE) {//world2Grid uit Grid
        super(leap);
        this.leap = leap;
        this.gettingSmaller = 0;
        this.gettingBigger = 0;
        this.stayingTheSame = 0;
        this.dragging = selected;
        this.creationBlock = creationblock;
        this.world = world;
        this.grid = grid;
        this.LEAPSCALE = LEAPSCALE;
    }

    @Override
    public void update(float tpf) {
        if (frame != null) {
            if (dragging == null) {
                grab();
            }
            if (dragging != null) {
                if (!release()) {
                    drag();
                }
            }
            previousFrame = frame;
        }
    }

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
                dragging = detectBlock(hand);
                this.gettingSmaller = 0;
                this.stayingTheSame = 0;
                System.out.print(Math.random());
                System.out.println("Ik grab!");
            }
        }
    }

    private void drag() {
        if (grid.containsBlock(dragging)) {
            //dragging.setPosition(grid.grid2world(dragging.getPosition()));
            world.addBlock(dragging);
            grid.removeFromGrid(dragging);

            // TODO correct position
        } else if (!world.containsBlock(dragging)) {
            world.addBlock(dragging);
        }
        dragging.setLifted(true);
        CollisionResults cr = new CollisionResults();
        grid.collideAboveBlock(dragging, cr);
        for (CollisionResult c : cr) {
            ((Block) c.getGeometry()).setFalling(true);
        }
        
        HandList hands = frame.hands();
        Hand hand = getGrabHand(hands);
        Vector3f pos = new Vector3f(hand.palmPosition().getX(),hand.palmPosition().getY(),hand.palmPosition().getZ());
        pos = pos.mult(LEAPSCALE);
        dragging.setPosition(pos);

        if (grid.withinGrid(dragging.getPosition())) {
            grid.snapToGrid(dragging);
        }
    }

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
                dragging.setLifted(false);
                dragging.setFalling(true);
                if (grid.withinGrid(dragging.getPosition())) {
                    grid.snapToGrid(dragging);
                    dragging.setPosition(grid.world2grid(dragging.getPosition()));
                    dragging.setRotation(0f);
                    world.removeBlock(dragging);
                    grid.addBlock(dragging);
                } else {
                    dragging.setDissolving(true);
                }

                dragging = null;
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

    private Hand getGrabHand(HandList hands) {
        if (isRightHanded) {
            return hands.rightmost();
        }
        return hands.leftmost();
    }

    private Block detectBlock(Hand hand) {
        Vector3f coordinates = new Vector3f(hand.palmPosition().getX(), hand.palmPosition().getY(), hand.palmPosition().getZ());
        coordinates = coordinates.mult(LEAPSCALE);
        coordinates.y = coordinates.y + Y_TRANSELATION;
        //coordinates.x = coordinates.x*LEAPSCALE.x;
        //coordinates.y = coordinates.y*LEAPSCALE.y;
        //coordinates.z = coordinates.z*LEAPSCALE.z;
        System.out.print(coordinates.x);
        System.out.print(",");
        System.out.print(coordinates.y);
        System.out.print(",");
        System.out.println(coordinates.z);
        Block result = grid.getBlockAt(coordinates);
        if (result != null) {
            System.out.println("HOI");
        }
        return grid.getBlockAt(coordinates);
    }
}
