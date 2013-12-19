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
     * Duration & location sensitivities should be positive non-zero value. 
     * (the time between sensitivity should be between 0 [=no time] and 1 [=the 
     * stdev weighted with the adjusted relative stdev])
     *      When == 1   :   All swipes rejected
     *      When  > 1   :   more swipes between user's extremes are accepted 
     *      When  < 1   :   all swipes between user's extremes are accepted and 
     *                      more (how much more depends on how close to zero the
     *                      sensitivity is. CAREFULL WITH THIS!
     * Example 1: sensitivity=100, then 99% of the preceived RotateSwipes 
     * between the users expected extremes values are accepted. The 1% that is 
     * not accepted are the swipes close to those expected extremes.
     * Example 2: sensitivity=0.9, then all swipes within the user's extremes are
     * accepted. PLUS the swipes that are 11% more extreme than the expected 
     * extreme swipes.
    */
    private final double ROTATE_Z_SENSITIVITY = 0.9;
    private final double ROTATE_DURATION_SENSITIVITY = 0.50;
    private final double ROTATE_TIME_BETWEEN_SENSITIVITY = 0.5;
    private final double ROTATE_MIN_ACCEPT_DURATION = 75000;
    private final double CAMERA_Z_SENSITIVITY = 0.9;
    private final double CAMERA_DURATION_SENSITIVITY = 0.50;
    private final double CAMERA_TIME_BETWEEN_SENSITIVITY = 0.5;
    private final double CAMERA_MIN_ACCEPT_DURATION = 75000;
    /*
     * Variables that get adjusted over time for a user.
     * Change the initial values to match more the initial preferences of the 
     * user.
    */
    private double averageZRotateSwipe = 0.0;
    private double stdevZRotateSwipe = 60;
    private double averageDurationRotateSwipe = 100000;
    private double stdevDurationRotateSwipe = 10000;
    private double minimalTimeBetweenRotateSwipes;
    private double averageZCameraSwipe = 0.0;
    private double stdevZCameraSwipe = 80;
    private double averageDurationCameraSwipe = 200000;
    private double stdevDurationCameraSwipe = 100000;
    private double minimalTimeBetweenCameraSwipes;
    
    /*
     * Variables to fine tune the movement of the grid.
    */
    private double ROTATE_SPEED_INCREASE = 2500;
    private double CAMERA_SPEED_INCREASE = 2000;
    private double ROTATE_DECAY_CONSTANT = 0.85;
    private double CAMERA_DECAY_CONSTANT = 0.85;
    private double MAX_VELOCITY_ROTATE = 0.05;
    private double MIN_VELOCITY_ROTATE = 0.001;
    private double MAX_VELOCITY_CAMERA = 0.01;
    private double MIN_VELOCITY_CAMERA = 0.0001;
    private boolean IS_RIGHT_HANDED = true;
    private boolean INVERT_Y_AXIS_FOR_CAMERA = false;
    
    
    
    private Frame frame;
    private Grid grid;
    private SwipeGesture rotateSwipe;
    private SwipeGesture cameraSwipe;
    private GridCam camera;
    
    private final double ROTATION_DELTA = Math.PI;
    private boolean isRotateSwipe;
    private boolean isCameraSwipe;
    private double rotateVelocity;
    private double cameraVelocity;
    private Vector lastDirectionRotate;
    private Vector lastDirectionCamera;
    private long timeBetweenRotateSwipes;
    private long timeBetweenCameraSwipes;
    private long timePreviousRotateSwipe;
    private long timePreviousCameraSwipe;
    private int nrRotateSwipes;
    private int nrCameraSwipes;
    private final double ROTATE_Z_SENS_VALUE;
    private final double ROTATE_DURATION_SENS_VALUE;
    private final double CAMERA_Z_SENS_VALUE;
    private final double CAMERA_DURATION_SENS_VALUE;
    
            
    
    private final boolean isShowWhyRejected = true;
    private final boolean isShowSwipeData = false;
    private final boolean isShowUserVariables = false;
    private String rejectedOn = "";
    
    
    public GestureRotateControl(Controller leap, Grid grid, GridCam camera)
    {
        super(leap);
        this.grid = grid;
        this.camera = camera;
        isRotateSwipe = false;
        isCameraSwipe = false;
        rotateVelocity = 0;
        cameraVelocity = 0;
        nrRotateSwipes = 0;
        nrCameraSwipes = 0;
        timeBetweenRotateSwipes = 0;
        timePreviousRotateSwipe = Long.MAX_VALUE;
        timeBetweenCameraSwipes = 0;
        timePreviousCameraSwipe = Long.MAX_VALUE;
        minimalTimeBetweenRotateSwipes = calculateMinimalTimeBetweenGestures(averageDurationRotateSwipe, 
                stdevDurationRotateSwipe, ROTATE_TIME_BETWEEN_SENSITIVITY);
        
        ROTATE_Z_SENS_VALUE = calculateSensitivyValue(ROTATE_Z_SENSITIVITY);
        ROTATE_DURATION_SENS_VALUE = calculateSensitivyValue(ROTATE_DURATION_SENSITIVITY);
        CAMERA_Z_SENS_VALUE = calculateSensitivyValue(CAMERA_Z_SENSITIVITY);
        CAMERA_DURATION_SENS_VALUE = calculateSensitivyValue(CAMERA_DURATION_SENSITIVITY);
    }

    @Override
    public void update(float tpf) 
    {
        if (frame != null)
        {
            rotateVelocity = decayVelocity(rotateVelocity);
            cameraVelocity = decayVelocity(cameraVelocity);
            double rotateSwipeSpeed = 0.0, cameraSwipeSpeed = 0.0;
            if(isSwiped())
            { 
                if(isRotateSwipe)
                {
                    if(rotateSwipe.state().equals(Gesture.State.STATE_STOP))
                        timeBetweenRotateSwipes = Math.abs(timePreviousRotateSwipe - rotateSwipe.frame().timestamp());

                    if(isIntendedAsRotateSwipe(rotateSwipe))
                    {
                        timePreviousRotateSwipe = rotateSwipe.frame().timestamp();
                        rotateSwipeSpeed = rotateSwipe.speed();
                        lastDirectionRotate = rotateSwipe.direction();
                    }
                    printDebugData("rotate");
                    calculateUserVariablesForRotate(rotateSwipe);
                }
                else
                {
                    if(cameraSwipe.state().equals(Gesture.State.STATE_STOP))
                        timeBetweenCameraSwipes = Math.abs(timePreviousCameraSwipe - cameraSwipe.frame().timestamp());
                    
                    if(isIntendedAsCameraSwipe(cameraSwipe))
                    {
                        timePreviousCameraSwipe = cameraSwipe.frame().timestamp();
                        cameraSwipeSpeed = cameraSwipe.speed();
                        lastDirectionCamera = cameraSwipe.direction();
                    }
                    printDebugData("camera");
                    calculateUserVariablesForCamera(cameraSwipe);
                }
            }
            rotateVelocity = calculateVelocity(rotateSwipeSpeed);
            cameraVelocity = calculateVelocity(cameraSwipeSpeed);
            adjustGrid();
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
        SwipeGesture s = getSwipe();
        if(isRotateSwipe && s != null)
        {
            rotateSwipe = s;
            return true;
        }
        else if(isCameraSwipe && s != null)
        {
            cameraSwipe = s;
            return true;
        }
        return false;
    }
    
    private void adjustGrid()
    {
        if(isRotateSwipe && rotateVelocity != 0 )
        {
            grid.rotate((float) (ROTATION_DELTA*rotateVelocity));
        }
        else if(isCameraSwipe && cameraVelocity != 0)
        {
            if(!INVERT_Y_AXIS_FOR_CAMERA)
                camera.rotate((float) (ROTATION_DELTA*cameraVelocity));
            else
                camera.rotate((float) (ROTATION_DELTA*cameraVelocity*-1));
        }
        
    }
    
    private SwipeGesture getSwipe() 
    {
        ArrayList<SwipeGesture> allSwipes = getSwipes();
        SwipeGesture aCorrectSwipe = null;
        if(allSwipes.isEmpty())
            return null;
        else
        {
            for(SwipeGesture s : allSwipes)
            {
                if(isCorrectSwipe(s, aCorrectSwipe))
                {
                    aCorrectSwipe = s;
                }
            }
        }
        return aCorrectSwipe;
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

    private boolean isCorrectSwipe(SwipeGesture s1, SwipeGesture s2) 
    {                        
        boolean isCorrectRotateDirection = (Math.abs(s1.direction().getX())>Math.abs(s1.direction().getY())
                && Math.abs(s1.direction().getX())>Math.abs(s1.direction().getZ()));
        
        boolean isCorrectCameraDirection = (Math.abs(s1.direction().getY())>Math.abs(s1.direction().getZ())
                && Math.abs(s1.direction().getY())>Math.abs(s1.direction().getX()));
        
        if(isCorrectRotateDirection)
        {
            isRotateSwipe = true;
            isCameraSwipe = false;
        }
        else if(isCorrectCameraDirection)
        {
            isCameraSwipe = true;
            isRotateSwipe = false;
        }
        
        /*boolean isMostRightOrLeftSwipe = (s2 == null) || 
                ((IS_RIGHT_HANDED 
                && s1.startPosition().getX() < s2.startPosition().getX())
                || 
                (!IS_RIGHT_HANDED 
                && s1.startPosition().getX() > s2.startPosition().getX()));
        */
        boolean isCorrectHand = false;
        if(!s1.hands().isEmpty())
            isCorrectHand = (s1.hands().get(0).equals(getRotateHand()));
        
        if(!"".equals(rejectedOn))
            rejectedOn = "";
        if(!isCorrectRotateDirection && !isCorrectCameraDirection)
            rejectedOn = rejectedOn + " + swipe direction ";
        //if(!isMostRightOrLeftSwipe && s2 != null)
        //    rejectedOn = rejectedOn + " + swipe hand (" + s1.startPosition().getX() + " < " + s2.startPosition().getX();
        if(!isCorrectHand)
            rejectedOn = rejectedOn + " + left/right hand ";
       
        //System.out.println(isCorrectDirection + " " + isMostRightOrLeftSwipe 
        //        + " " + isCorrectHand);
        return (isCorrectRotateDirection || isCorrectCameraDirection) 
                && isCorrectHand; //&&is MostRightOrLeftSwipe;
    }
    
    private boolean isIntendedAsRotateSwipe(SwipeGesture s)
    {
        boolean isAtRegularSwipePosition = (s.position().getZ() >= (averageZRotateSwipe-ROTATE_Z_SENS_VALUE*stdevZRotateSwipe) &&
        s.position().getZ() <= (averageZRotateSwipe+ROTATE_Z_SENS_VALUE*stdevZRotateSwipe));

        boolean isOffRegularSwipeDuration = (s.duration() >= (averageDurationRotateSwipe-ROTATE_DURATION_SENS_VALUE*stdevDurationRotateSwipe) 
        && s.duration() <= (averageDurationRotateSwipe+ROTATE_DURATION_SENS_VALUE*stdevDurationRotateSwipe));
        
        boolean isFakeSwipe = timeBetweenRotateSwipes < minimalTimeBetweenRotateSwipes;

        if(!isAtRegularSwipePosition)
            rejectedOn = rejectedOn + "+ Regular Swipe Position ";
        if(!isOffRegularSwipeDuration)
            rejectedOn = rejectedOn + "+ swipe duration ";
        if(isFakeSwipe)
            rejectedOn = rejectedOn + "+ time between swipes ";
        
        return isAtRegularSwipePosition && isOffRegularSwipeDuration && !isFakeSwipe;
    }
    
    private boolean isIntendedAsCameraSwipe(SwipeGesture s)
    {
        boolean isAtRegularSwipePosition = (s.position().getZ() >= (averageZCameraSwipe-CAMERA_Z_SENS_VALUE*stdevZCameraSwipe) &&
        s.position().getZ() <= (averageZCameraSwipe+CAMERA_Z_SENS_VALUE*stdevZCameraSwipe));

        boolean isOffRegularSwipeDuration = (s.duration() >= (averageDurationCameraSwipe-CAMERA_DURATION_SENS_VALUE*stdevDurationCameraSwipe) 
        && s.duration() <= (averageDurationCameraSwipe+CAMERA_DURATION_SENS_VALUE*stdevDurationCameraSwipe));
        
        boolean isFakeSwipe = timeBetweenCameraSwipes < minimalTimeBetweenCameraSwipes;

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
        double velocity =0.0;      
        if(isRotateSwipe)
        {
            if(lastDirectionRotate == null || speed == 0.0)
                velocity = rotateVelocity;
            else if(lastDirectionRotate.getX()>0)
                velocity = rotateVelocity + (1/(frame.currentFramesPerSecond()*(speed/ROTATE_SPEED_INCREASE)));  
            else if(lastDirectionRotate.getX()<0)
                velocity = rotateVelocity - (1/(frame.currentFramesPerSecond()*(speed/ROTATE_SPEED_INCREASE)));
            
            if(Math.abs(velocity) > MAX_VELOCITY_ROTATE )
                return Math.signum(velocity)*MAX_VELOCITY_ROTATE;
            if(Math.abs(velocity) < MIN_VELOCITY_ROTATE )
                return 0.0;
        }
        else if(isCameraSwipe)
        {
            if(lastDirectionCamera == null || speed == 0.0)
                velocity = cameraVelocity;
            else if(lastDirectionCamera.getY()>0)
                velocity = cameraVelocity + (1/(frame.currentFramesPerSecond()*(speed/CAMERA_SPEED_INCREASE)));  
            else if(lastDirectionCamera.getY()<0)
                velocity = cameraVelocity - (1/(frame.currentFramesPerSecond()*(speed/CAMERA_SPEED_INCREASE)));
            
            
            if(Math.abs(velocity) > MAX_VELOCITY_CAMERA )
                return Math.signum(velocity)*MAX_VELOCITY_CAMERA;
            if(Math.abs(velocity) < MIN_VELOCITY_CAMERA )
                return 0.0;
        }
        return velocity;
    }
    
    private double decayVelocity(double velocity)
    {
        if(isRotateSwipe)
            return velocity*ROTATE_DECAY_CONSTANT; 
        else if(isCameraSwipe)
            return velocity*CAMERA_DECAY_CONSTANT; 
        else
            return 0;
    }
    
    private void calculateUserVariablesForRotate(SwipeGesture rotateSwipe)
    {
        nrRotateSwipes++;
        double previousAverage = averageZRotateSwipe, 
                zCoordinate = rotateSwipe.position().getZ(),
                duration = rotateSwipe.duration();
        
        averageZRotateSwipe = calculateAverage(averageZRotateSwipe, zCoordinate, nrRotateSwipes);
        if(nrRotateSwipes > 1)
            stdevZRotateSwipe = calculateStdev(stdevZRotateSwipe, previousAverage, averageZRotateSwipe, zCoordinate);
        
        if(rotateSwipe.state().equals(Gesture.State.STATE_STOP) && rotateSwipe.duration() != 0.0)
        {
            previousAverage = averageDurationRotateSwipe;
            averageDurationRotateSwipe = calculateAverage(averageDurationRotateSwipe, duration, nrRotateSwipes);
            if(nrRotateSwipes > 1)
            {
                stdevDurationRotateSwipe = calculateStdev(stdevDurationRotateSwipe, previousAverage, averageDurationRotateSwipe, duration);
                minimalTimeBetweenRotateSwipes = calculateMinimalTimeBetweenGestures(averageDurationRotateSwipe, stdevDurationRotateSwipe, ROTATE_TIME_BETWEEN_SENSITIVITY);
            }
            if(averageDurationRotateSwipe < (ROTATE_MIN_ACCEPT_DURATION-ROTATE_DURATION_SENS_VALUE*stdevDurationRotateSwipe))
                averageDurationRotateSwipe =  ROTATE_MIN_ACCEPT_DURATION;
        }
    }
    
    private void calculateUserVariablesForCamera(SwipeGesture cameraSwipe)
    {
        nrCameraSwipes++;
        double previousAverage = averageZCameraSwipe, 
                zCoordinate = cameraSwipe.position().getZ(),
                duration = cameraSwipe.duration();
        
        averageZCameraSwipe = calculateAverage(averageZCameraSwipe, zCoordinate, nrCameraSwipes);
        if(nrCameraSwipes > 1)
            stdevZCameraSwipe = calculateStdev(stdevZCameraSwipe, previousAverage, averageZCameraSwipe, zCoordinate);
        
        if(cameraSwipe.state().equals(Gesture.State.STATE_STOP) && cameraSwipe.duration() != 0.0)
        {
            previousAverage = averageDurationCameraSwipe;
            averageDurationCameraSwipe = calculateAverage(averageDurationCameraSwipe, duration, nrCameraSwipes);
            if(nrCameraSwipes > 1)
            {
                stdevDurationCameraSwipe = calculateStdev(stdevDurationCameraSwipe, previousAverage, averageDurationCameraSwipe, duration);
                minimalTimeBetweenCameraSwipes = calculateMinimalTimeBetweenGestures(averageDurationCameraSwipe, stdevDurationCameraSwipe, CAMERA_TIME_BETWEEN_SENSITIVITY);
            }
            if(averageDurationCameraSwipe < (CAMERA_MIN_ACCEPT_DURATION-CAMERA_DURATION_SENS_VALUE*stdevDurationCameraSwipe))
                averageDurationCameraSwipe =  CAMERA_MIN_ACCEPT_DURATION;
        }
    }
    

    private void printDebugData(String swipeType) 
    {
        if(isShowWhyRejected || isShowUserVariables || isShowSwipeData)
            System.out.println("\n");
        if(!"".equals(rejectedOn) && isShowWhyRejected)
            System.out.println(swipeType + " Swipe rejected on:\n\t" + rejectedOn);
        if(isShowUserVariables)
        {
            if("rotate".equals(swipeType))
            {
                System.out.println("User variables of a " + swipeType + " swipe:");
                System.out.println("\tDuration:\t\t" + averageDurationRotateSwipe + " +/- " + stdevDurationRotateSwipe*ROTATE_DURATION_SENS_VALUE + "\t\tsensValue = "+ROTATE_DURATION_SENS_VALUE);
                System.out.println("\tZ coordinate:\t\t" + averageZRotateSwipe + " +/- " + stdevZRotateSwipe*ROTATE_Z_SENS_VALUE + "\t\tsensValue = "+ROTATE_Z_SENS_VALUE);
                System.out.println("\tMin. Time between:\t" + minimalTimeBetweenRotateSwipes);
            }
            else if("camera".equals(swipeType))
            {
                System.out.println("User variables of a " + swipeType + " swipe:");
                System.out.println("\tDuration:\t\t" + averageDurationCameraSwipe + " +/- " + stdevDurationCameraSwipe*CAMERA_DURATION_SENS_VALUE + "\t\tsensValue = "+ CAMERA_DURATION_SENS_VALUE);
                System.out.println("\tZ coordinate:\t\t" + averageZCameraSwipe + " +/- " + stdevZCameraSwipe*CAMERA_Z_SENS_VALUE + "\t\tsensValue = "+ CAMERA_Z_SENS_VALUE);
                System.out.println("\tMin. Time between:\t" + minimalTimeBetweenCameraSwipes);
            }
        }
        if(isShowSwipeData && ((rotateSwipe != null && isRotateSwipe) || (cameraSwipe != null && isCameraSwipe)))
        {
            if("rotate".equals(swipeType))
            {
                System.out.println(swipeType + " Swipe data:");
                System.out.println("\tDirection:\t" + rotateSwipe.direction());
                System.out.println("\tDuration:\t" + rotateSwipe.duration());
                System.out.println("\tSpeed:\t\t" + rotateSwipe.speed());
                System.out.println("\tZ coordinate:\t" + rotateSwipe.position().getZ());
                System.out.println("\tTime between:\t" + timeBetweenRotateSwipes);
            }
            else if("camera".equals(swipeType))
            {
                System.out.println(swipeType + " Swipe data:");
                System.out.println("\tDirection:\t" + cameraSwipe.direction());
                System.out.println("\tDuration:\t" + cameraSwipe.duration());
                System.out.println("\tSpeed:\t\t" + cameraSwipe.speed());
                System.out.println("\tZ coordinate:\t" + cameraSwipe.position().getZ());
                System.out.println("\tTime between:\t" + timeBetweenCameraSwipes);
            }
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
        if(sensitivity > 1)
            return 1-1/sensitivity;
        else if(sensitivity == 1)
            return 0;
        else if(sensitivity < 1)
            return Math.abs(1/sensitivity);
        else
        {
            System.exit(1);
            System.err.println("ERROR: Sensitivity must be a non-zero positive value.");
        }
        return -1;
    }
    
    private double calculateMinimalTimeBetweenGestures(double average, double stdev, double sensitivityValue)
    {
        double relativeStdev = stdev/(stdev+average);
        return average*relativeStdev*sensitivityValue;
    }
}
