/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;
import java.util.ArrayList;
import leaptest.model.Grid;

/**
 *
 * @author Jasper van der Waa & Annet Meijers
 */
public class GestureRotateControl extends LeapControl
{
    /*
     * Variables to fine tune what swipe for horizontal rotation is accepted
    */
    private double zCoordinateSensitivity = 1.5;
    private double spinDurationSensitivity = 1;
    private double timeBetweenSpinSwipesSensitivity = 1;
    private double minimalDurationOfSpinSwipe = 75000;
    //Variables that get adjusted over time for a user 
    private double averageZSpinSwipe = 0.0;
    private double stdevZSpinSwipe = 30;
    private double averageDurationSpinSwipe = 100000;
    private double stdevDurationSpinSwipe = 10000;
    private double mimimalTimeBetweenSpinSwipes;
    
    
    /*
     * Variables to fine tune the horizontal rotation movement of the grid
    */
    private double naturalSpeedIncrease = 2500;
    private double decayConstant = 0.85;
    private double maxVelocity = 0.05;
    private double minVelocity = 0.001;
    private boolean isRightHanded = true;
    
    private final double ROTATION_DELTA = Math.PI;
    private Frame frame;
    
    private SwipeGesture swipe;
    private Grid grid;
    private double spinVelocity;
    private Vector lastDirection;
    private long timeBetweenSpinSwipes;
    private long timePreviousSpinSwipe;
    private int nrSpinSwipes;
    private String rejectedOn = "";
    

    
    public GestureRotateControl(Controller leap, Grid grid)
    {
        super(leap);
        this.grid = grid;
        spinVelocity = 0;
        maxVelocity = 0.05;
        nrSpinSwipes = 0;
        timeBetweenSpinSwipes = 0;
        timePreviousSpinSwipe = Long.MAX_VALUE;
        mimimalTimeBetweenSpinSwipes = averageDurationSpinSwipe * ((stdevDurationSpinSwipe/(averageDurationSpinSwipe+stdevDurationSpinSwipe))*timeBetweenSpinSwipesSensitivity);
    }

    @Override
    public void update(float tpf) 
    {
        if (frame != null)
        {
            spinVelocity = decayVelocity(spinVelocity);
            double swipeSpeed = 0.0;
            if(isSwiped())
            { 
                if(swipe.state().equals(Gesture.State.STATE_STOP))
                {
                    //System.out.println("\tCurrent time = " + swipe.frame().timestamp());
                    //System.out.println("\tTime between = " + timeBetweenSpinSwipes);
                    timeBetweenSpinSwipes = Math.abs(timePreviousSpinSwipe - swipe.frame().timestamp());
                }
                
                if (isIntendedAsSpinSwipe(swipe))
                {
                    timePreviousSpinSwipe = swipe.frame().timestamp();
                    swipeSpeed = swipe.speed();
                    lastDirection = swipe.direction();
                }
                calculateUserVariablesForSpin(swipe);
                //System.out.println("Z:\t\t" + averageZSpinSwipe + " +/- " + zCoordinateSensitivity*stdevZSpinSwipe);
                //System.out.println("Duration:\t" + averageDurationSpinSwipe + " +/- " + spinDurationSensitivity*stdevDurationSpinSwipe);
            }
            spinVelocity = calculateVelocity(swipeSpeed);
            rejectedOn = "";
            rotate();
        }
    }

    @Override
    protected void onFrame(Controller leap) 
    {
        frame = controller.frame();
    }
    
    @Override
    protected void onConnect(Controller leap) 
    {
        leap.enableGesture(Gesture.Type.TYPE_SWIPE);
    }
    
    @Override
    protected void onInit(Controller leap) 
    {
        leap.enableGesture(Gesture.Type.TYPE_SWIPE);
    }
    
    private boolean isSwiped()
    {
        swipe = getSwipe();
        if (swipe == null)
        {
            return false;
        }
        return true;
    }
    
    private void rotate()
    {
        grid.rotate((float) (ROTATION_DELTA*spinVelocity));
    }
    
    private ArrayList<SwipeGesture> getSwipes()
    {
        GestureList allGestures = frame.gestures();
        ArrayList<SwipeGesture> swipes = new ArrayList();
        for(Gesture g : allGestures)
        {
            if(g.type() == Gesture.Type.TYPE_SWIPE)
            {
                SwipeGesture oneSwipe = new SwipeGesture(g);
                swipes.add(oneSwipe);
            }
        }
        return swipes;
    }

    private SwipeGesture getSwipe() 
    {
        ArrayList<SwipeGesture> allSwipes = getSwipes();
        SwipeGesture aSwipe = null;
        if(allSwipes.isEmpty())
            return null;
        else
        {
            for(SwipeGesture s : allSwipes)
            {
                if(isCorrectSwipe(s, aSwipe))
                {
                    aSwipe = s;
                }
            }
        }
        return aSwipe;
    }

    private boolean isCorrectSwipe(SwipeGesture s1, SwipeGesture s2) 
    {                        
        boolean isCorrectDirection = (Math.abs(s1.direction().getX())>Math.abs(s1.direction().getY())
                && Math.abs(s1.direction().getX())>Math.abs(s1.direction().getZ()));
        
        boolean isMostRightOrLeftSwipe = (s2 == null) || ((isRightHanded 
                && s1.position().getX() < s2.position().getX())
                || 
                (!isRightHanded 
                && s1.position().getX() > s2.position().getX()));

        boolean isCorrectHand = false;
        if(!s1.hands().isEmpty())
            isCorrectHand = (s1.hands().get(0).equals(getRotateHand()));
       
        //System.out.println(isCorrectDirection + " " + isMostRightOrLeftSwipe 
        //        + " " + isCorrectHand);
        return isCorrectDirection && isMostRightOrLeftSwipe && isCorrectHand;
    }
    
    private boolean isIntendedAsSpinSwipe(SwipeGesture s)
    {
        boolean isAtRegularSwipePosition = (s.startPosition().getZ() >= (averageZSpinSwipe-(1-1/zCoordinateSensitivity)*stdevZSpinSwipe) &&
        s.startPosition().getZ() <= (averageZSpinSwipe+(1-1/zCoordinateSensitivity)*stdevZSpinSwipe));

        boolean isOffRegularSwipeDuration = (s.duration() >= (averageDurationSpinSwipe-(1-1/spinDurationSensitivity)*stdevDurationSpinSwipe) 
        && s.duration() <= (averageDurationSpinSwipe+(1-1/spinDurationSensitivity)*stdevDurationSpinSwipe));
        
        boolean isFakeSwipe = timeBetweenSpinSwipes < mimimalTimeBetweenSpinSwipes;

        if(!isAtRegularSwipePosition)
            rejectedOn = rejectedOn + "+ Regular Swipe Position ";
        if(!isOffRegularSwipeDuration)
            rejectedOn = rejectedOn + "+ swipe duration";
        if(isFakeSwipe)
            rejectedOn = rejectedOn + "+ time between swipes";
        
        //if(!"".equals(rejectedOn))
        //    System.out.println("Swipe rejected on: " + rejectedOn);
       
        
        return isAtRegularSwipePosition && isOffRegularSwipeDuration && !isFakeSwipe;
    }
    
    private Hand getRotateHand() 
    {
        if(isRightHanded)
            return frame.hands().leftmost();
        else
            return frame.hands().rightmost();
    }

    private double calculateVelocity(double speed) 
    {
        double velocity;        
        if(lastDirection == null || speed == 0.0)
            velocity = spinVelocity;
        else if(lastDirection.getX()>0)
            velocity = spinVelocity + (1/(frame.currentFramesPerSecond()*(speed/naturalSpeedIncrease)));  
        else if(lastDirection.getX()<0)
            velocity = spinVelocity - (1/(frame.currentFramesPerSecond()*(speed/naturalSpeedIncrease)));
        else
            velocity = 0.0;
        
        if(Math.abs(velocity) > maxVelocity )
            return Math.signum(velocity)*maxVelocity;
        if(Math.abs(velocity) < minVelocity )
            return 0.0;
        
        return velocity;
    }
    
    private double decayVelocity(double velocity)
    {
        return velocity*decayConstant; 
    }
    
    private void calculateUserVariablesForSpin(SwipeGesture spinSwipe)
    {
        nrSpinSwipes++;
        
        double previousAverage = averageZSpinSwipe, zCoordinate = spinSwipe.position().getZ();
        averageZSpinSwipe = (zCoordinate+(nrSpinSwipes-1)*averageZSpinSwipe)/nrSpinSwipes;
        if(nrSpinSwipes > 1)
            stdevZSpinSwipe = Math.sqrt(stdevZSpinSwipe+(zCoordinate-previousAverage)*(zCoordinate-averageZSpinSwipe));
        
        
        if(spinSwipe.state().equals(Gesture.State.STATE_STOP) && spinSwipe.duration() != 0.0)
        {
            previousAverage = averageDurationSpinSwipe;
            averageDurationSpinSwipe = (spinSwipe.duration()+(nrSpinSwipes-1)*averageDurationSpinSwipe)/nrSpinSwipes;
            if(nrSpinSwipes > 1)
            {
                stdevDurationSpinSwipe = Math.sqrt(stdevDurationSpinSwipe+(spinSwipe.duration()-previousAverage)*(spinSwipe.duration()-averageDurationSpinSwipe));
                mimimalTimeBetweenSpinSwipes = averageDurationSpinSwipe * ((stdevDurationSpinSwipe/(averageDurationSpinSwipe+stdevDurationSpinSwipe))*timeBetweenSpinSwipesSensitivity);
            }
            if(averageDurationSpinSwipe < minimalDurationOfSpinSwipe)
                averageDurationSpinSwipe =  minimalDurationOfSpinSwipe + spinDurationSensitivity*stdevDurationSpinSwipe;
        }
    }
}
