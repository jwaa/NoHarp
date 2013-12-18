/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
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
    private final static int GETTING_BIGGER_THRESHOLD = 5;
    private final static int STAYING_THE_SAME_THRESHOLD = 2;
    private Frame frame;
    private Frame previousFrame;
    private Controller leap;
    private boolean isRightHanded = true;
    private int gettingSmaller, gettingBigger;
    private int stayingTheSame;
    private Block dragging, creationBlock;
    private BlockContainer world;
    private Grid grid;

    public GestureGrabControl(Controller leap, BlockContainer world, Grid grid, Block selected, Block creationblock) {//world2Grid uit Grid
        super(leap);
        this.leap = leap;
        this.gettingSmaller = 0;
        this.gettingBigger = 0;
        this.stayingTheSame = 0;
        this.dragging = selected;
        this.creationBlock = creationblock;
        this.world = world;
        this.grid = grid;
    }

    @Override
    public void update(float tpf) {
        if (frame != null) {
            if (dragging == null) {
                grab();
            }
            if (dragging!= null) {
                if (!release())
                {}
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
            if (hand.scaleFactor(previousFrame) < 1.0) {
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
            }
        }
    }
    
    private void drag()
    {
           
    }
    
    private boolean release()
    {
        HandList hands = frame.hands();
        Hand hand = getGrabHand(hands);
        if (previousFrame != null) {
            if (hand == null) {
                return false;
            }
            if (hand.scaleFactor(previousFrame) < 1.0) {
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
            }
        }
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
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector coordinates = hand.palmPosition();
        // Aim the ray from the clicked spot forwards.
        return getBlockAtPoint(coordinates);
        // New block creation intersection
        //creationBlock.collideWith(ray, results);
        //if (results.size() > 0)
//        {
//            return new Block(MaterialManager.normal,creationBlock.getPosition(),Vector3f.UNIT_XYZ.mult(creationBlock.getDimensions().x));
//        }
//        
//        // Collect intersections between ray and all nodes in results list.
//        world.collideWith(ray, results);
//        grid.collideWith(ray, results);
//        
//        if (results.size() > 0)
//        {
//            Geometry g = results.getClosestCollision().getGeometry();
//            if (g instanceof Block && !((Block) g).isDissolving())
//            {
//                return (Block) g;
//            }
//        }
//        return null;
    }
}
