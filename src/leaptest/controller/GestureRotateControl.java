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
import leaptest.model.GridCam;

/**
 *
 * @author Jasper van der Waa & Annet Meijers
 */
public class GestureRotateControl extends LeapControl
{
    /*
     * Variables to fine tune what swipe for horizontal rotation is accepted
     * Sensitivities should be values above 1. 
     *      When == 1   :   all swipes rejected
     *      When  > 1   :   some swipes accepted
     * Example: sensitivity=100, then 99% of the preceived SpinSwipes for a 
     * user will be accepted.
    */
    private final double Z_COORDINATE_SENSITIVITY = 100;
    private final double SPIN_DURATION_SENSITIVITY = 100;
    private final double TIME_BETWEEN_SPINSWIPES_SENSITIVITY = 75;
    private final double MINIMAL_ACCEPTED_DURATION_OF_SPINSWIPE = 75000;
    //Variables that get adjusted over time for a user 
    private double averageZSpinSwipe = 0.0;
    private double stdevZSpinSwipe = 30;
    private double averageDurationSpinSwipe = 100000;
    private double stdevDurationSpinSwipe = 10000;
    private double minimalTimeBetweenSpinSwipes;
    
    
    /*
     * Variables to fine tune the horizontal rotation movement of the grid
    */
    private double NATURAL_SPEED_INCREASE = 2500;
    private double DECAY_CONSTANT = 0.85;
    private double MAX_VELOCITY = 0.05;
    private double MIN_VELOCITY = 0.001;
    private boolean IS_RIGHT_HANDED = true;
    
    private Frame frame;
    private Grid grid;
    private SwipeGesture swipe;
    private GridCam camera;
    
    private final double ROTATION_DELTA = Math.PI;
    private double spinVelocity;
    private Vector lastDirection;
    private long timeBetweenSpinSwipes;
    private long timePreviousSpinSwipe;
    private int nrSpinSwipes;
    private final double Z_SENSITIVITY_VALUE;
    private final double DURATION_SENSITIVITY_VALUE;
    private final double TIME_BETWEEN_SENSITIVITY_VALUE;
            
    
    private final boolean isShowWhyRejected = true;
    private final boolean isShowSwipeData = true;
    private final boolean isShowUserVariables = true;
    private String rejectedOn = "";
    
    
    public GestureRotateControl(Controller leap, Grid grid, GridCam camera)
    {
        super(leap);
        this.grid = grid;
        this.camera = camera;
        spinVelocity = 0;
        MAX_VELOCITY = 0.05;
        nrSpinSwipes = 0;
        timeBetweenSpinSwipes = 0;
        timePreviousSpinSwipe = Long.MAX_VALUE;
        minimalTimeBetweenSpinSwipes = calculateMinimalTimeBetweenGestures(averageDurationSpinSwipe, 
                stdevDurationSpinSwipe, TIME_BETWEEN_SPINSWIPES_SENSITIVITY);
        Z_SENSITIVITY_VALUE = calculateSensitivyValue(Z_COORDINATE_SENSITIVITY);
        DURATION_SENSITIVITY_VALUE = calculateSensitivyValue(SPIN_DURATION_SENSITIVITY);
        TIME_BETWEEN_SENSITIVITY_VALUE = calculateSensitivyValue(TIME_BETWEEN_SPINSWIPES_SENSITIVITY);
        
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
                    timeBetweenSpinSwipes = Math.abs(timePreviousSpinSwipe - swipe.frame().timestamp());
                
                if (isIntendedAsSpinSwipe(swipe))
                {
                    timePreviousSpinSwipe = swipe.frame().timestamp();
                    swipeSpeed = swipe.speed();
                    lastDirection = swipe.direction();
                }
                calculateUserVariablesForSpin(swipe);
                printDebugData();
                
            }
            spinVelocity = calculateVelocity(swipeSpeed);
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
        
        boolean isMostRightOrLeftSwipe = (s2 == null) || ((IS_RIGHT_HANDED 
                && s1.position().getX() < s2.position().getX())
                || 
                (!IS_RIGHT_HANDED 
                && s1.position().getX() > s2.position().getX()));

        boolean isCorrectHand = false;
        if(!s1.hands().isEmpty())
            isCorrectHand = (s1.hands().get(0).equals(getRotateHand()));
        
        if(!"".equals(rejectedOn))
            rejectedOn = "";
        if(!isCorrectDirection)
            rejectedOn = rejectedOn + " + swipe direction";
        if(!isMostRightOrLeftSwipe)
            rejectedOn = rejectedOn + " + swipe hand";
        if(!isCorrectHand)
            rejectedOn = rejectedOn + " + left/right hand";
       
        //System.out.println(isCorrectDirection + " " + isMostRightOrLeftSwipe 
        //        + " " + isCorrectHand);
        return isCorrectDirection && isMostRightOrLeftSwipe && isCorrectHand;
    }
    
    private boolean isIntendedAsSpinSwipe(SwipeGesture s)
    {
        boolean isAtRegularSwipePosition = (s.position().getZ() >= (averageZSpinSwipe-Z_SENSITIVITY_VALUE*stdevZSpinSwipe) &&
        s.position().getZ() <= (averageZSpinSwipe+Z_SENSITIVITY_VALUE*stdevZSpinSwipe));

        boolean isOffRegularSwipeDuration = (s.duration() >= (averageDurationSpinSwipe-DURATION_SENSITIVITY_VALUE*stdevDurationSpinSwipe) 
        && s.duration() <= (averageDurationSpinSwipe+DURATION_SENSITIVITY_VALUE*stdevDurationSpinSwipe));
        
        boolean isFakeSwipe = timeBetweenSpinSwipes < minimalTimeBetweenSpinSwipes;

        if(!isAtRegularSwipePosition)
            rejectedOn = rejectedOn + "+ Regular Swipe Position ";
        if(!isOffRegularSwipeDuration)
            rejectedOn = rejectedOn + "+ swipe duration";
        if(isFakeSwipe)
            rejectedOn = rejectedOn + "+ time between swipes";
        
        
       
        
        return isAtRegularSwipePosition && isOffRegularSwipeDuration && !isFakeSwipe;
    }
    
    private Hand getRotateHand() 
    {
        if(IS_RIGHT_HANDED)
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
            velocity = spinVelocity + (1/(frame.currentFramesPerSecond()*(speed/NATURAL_SPEED_INCREASE)));  
        else if(lastDirection.getX()<0)
            velocity = spinVelocity - (1/(frame.currentFramesPerSecond()*(speed/NATURAL_SPEED_INCREASE)));
        else
            velocity = 0.0;
        
        if(Math.abs(velocity) > MAX_VELOCITY )
            return Math.signum(velocity)*MAX_VELOCITY;
        if(Math.abs(velocity) < MIN_VELOCITY )
            return 0.0;
        
        return velocity;
    }
    
    private double decayVelocity(double velocity)
    {
        return velocity*DECAY_CONSTANT; 
    }
    
    private void calculateUserVariablesForSpin(SwipeGesture spinSwipe)
    {
        nrSpinSwipes++;
        double previousAverage = averageZSpinSwipe, 
                zCoordinate = spinSwipe.position().getZ(),
                duration = spinSwipe.duration();
        
        averageZSpinSwipe = calculateAverage(averageZSpinSwipe, zCoordinate, nrSpinSwipes);
        if(nrSpinSwipes > 1)
            stdevZSpinSwipe = calculateStdev(stdevZSpinSwipe, previousAverage, averageZSpinSwipe, zCoordinate);
        
        if(spinSwipe.state().equals(Gesture.State.STATE_STOP) && spinSwipe.duration() != 0.0)
        {
            previousAverage = averageDurationSpinSwipe;
            averageDurationSpinSwipe = calculateAverage(averageDurationSpinSwipe, duration, nrSpinSwipes);
            if(nrSpinSwipes > 1)
            {
                stdevDurationSpinSwipe = calculateStdev(stdevDurationSpinSwipe, previousAverage, averageDurationSpinSwipe, duration);
                minimalTimeBetweenSpinSwipes = calculateMinimalTimeBetweenGestures(averageDurationSpinSwipe, stdevDurationSpinSwipe, TIME_BETWEEN_SENSITIVITY_VALUE);
            }
            if(averageDurationSpinSwipe < (MINIMAL_ACCEPTED_DURATION_OF_SPINSWIPE-DURATION_SENSITIVITY_VALUE*stdevDurationSpinSwipe))
                averageDurationSpinSwipe =  MINIMAL_ACCEPTED_DURATION_OF_SPINSWIPE;
        }
    }
    
    private double calculateMinimalTimeBetweenGestures(double average, double stdev, double sensitivityValue)
    {
        double ratio = (average+stdev)*sensitivityValue;
        return stdev*(average / ratio);
    }

    private void printDebugData() 
    {
        if(isShowWhyRejected || isShowUserVariables || isShowSwipeData)
            System.out.println("\n");
        if(!"".equals(rejectedOn) && isShowWhyRejected)
            System.out.println("Swipe rejected on:\n\t" + rejectedOn);
        if(isShowUserVariables)
        {
            System.out.println("User variables:");
            System.out.println("\tDuration:\t\t" + averageDurationSpinSwipe + " +/- " + stdevDurationSpinSwipe + "\t\tsensValue = "+DURATION_SENSITIVITY_VALUE);
            System.out.println("\tZ coordinate:\t\t" + averageZSpinSwipe + " +/- " + stdevZSpinSwipe + "\t\tsensValue = "+Z_SENSITIVITY_VALUE);
            System.out.println("\tMin. Time between:\t" + minimalTimeBetweenSpinSwipes);
        }
        if(isShowSwipeData && swipe != null)
        {
            System.out.println("Swipe data:");
            System.out.println("\tDirection:\t" + swipe.direction());
            System.out.println("\tDuration:\t" + swipe.duration());
            System.out.println("\tZ coordinate:\t" + swipe.position().getZ());
            System.out.println("\tTime between:\t" + timeBetweenSpinSwipes);
        }
    }

    private double calculateStdev(double stdev, double prevAverage, double average, double x) 
    {
        return Math.sqrt(stdev+(x-prevAverage)*(x-average));
    }

    private double calculateAverage(double average, double x, int N) 
    {
        return (x+(N-1)*average)/N;
    }
    
    private double calculateSensitivyValue(double sensitivity)
    {
        return Math.abs(1-1/sensitivity);
    }
}
