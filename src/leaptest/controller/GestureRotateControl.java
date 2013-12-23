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
    private final double ROTATE_DURATION_SENSITIVITY = 0.25;
    private final double ROTATE_TIME_BETWEEN_SENSITIVITY = 1;
    private final double ROTATE_PART_SENSITIVITY = 0.5;
    private final double ROTATE_MIN_ACCEPT_DURATION = 75000;
    
    private final double CAMERA_Z_SENSITIVITY = 0.9;
    private final double CAMERA_DURATION_SENSITIVITY = 0.75;
    private final double CAMERA_TIME_BETWEEN_SENSITIVITY = 0.5;
    private final double CAMERA_PART_SENSITIVITY = 0.5;
    private final double CAMERA_MIN_ACCEPT_DURATION = 75000;
    /*
     * Variables that get adjusted over time for a user.
     * Change the initial values to match more the initial preferences of the 
     * user.
    */
    private double averageZPartialRotateSwipe = 0.0;
    private double stdevZPartialRotateSwipe = 60;
    private double averagePartialDurationRotate = 10000;
    private double stdevPartialDurationRotate = 1000;
    private double averageDurationRotateSwipe = 200000;
    private double stdevDurationRotateSwipe = 20000;
    
    private double averageZPartialCameraSwipe = 0.0;
    private double stdevZPartialCameraSwipe = 80;
    private double averagePartialDurationCamera = 20000;
    private double stdevPartialDurationCamera = 2000;
    private double averageDurationCameraSwipe = 200000;
    private double stdevDurationCameraSwipe = 100000;
    
    /*
     * Variables to fine tune the movement of the grid.
    */
    private double ROTATE_SPEED_INCREASE = 100000;
    private double CAMERA_SPEED_INCREASE = 70000;
    private double ROTATE_DECAY_CONSTANT = 0.85;
    private double CAMERA_DECAY_CONSTANT = 0.65;
    private double MAX_VELOCITY_ROTATE = 0.25;
    private double MIN_VELOCITY_ROTATE = 0.001;
    private double MAX_VELOCITY_CAMERA = 0.025;
    private double MIN_VELOCITY_CAMERA = 0.0001;
    private boolean IS_RIGHT_HANDED = false;
    private boolean INVERT_Y_AXIS_FOR_CAMERA = false;
    
    
    
    private Frame frame;
    private Grid grid;
    private SwipeGesture rotateSwipe;
    private SwipeGesture cameraSwipe;
    private GridCam camera;
    private final double ROTATION_DELTA = Math.PI;
    
    private boolean isRotateSwipe;
    private Vector lastDirectionRotate;
    private double rotateVelocity;
    private double summedPartialRotateSwipes;
    private int nrPartialRotateSwipes;
    private long timeBetweenRotateSwipes;
    private double minimalTimeBetweenRotateSwipes;
    private int nrIntendedRotateSwipes;
    
    private boolean isCameraSwipe;
    private Vector lastDirectionCamera;
    private double cameraVelocity;
    private double summedPartialCameraSwipes;
    private int nrPartialCameraSwipes;
    private long timeBetweenCameraSwipes;
    private double minimalTimeBetweenCameraSwipes;    
    private int nrIntendedCameraSwipes;
    
    private long timePreviousIntendedRotateSwipe;
    private int previousRotateID;
    private long timePreviousIntendedCameraSwipe;
    private int previousCameraID;
    
    private final double ROTATE_Z_SENS_VALUE;
    private final double ROTATE_DURATION_SENS_VALUE;
    private final double ROTATE_PART_DURATION_SENS_VALUE;
    private final double CAMERA_Z_SENS_VALUE;
    private final double CAMERA_DURATION_SENS_VALUE;
    private final double CAMERA_PART_DURATION_SENS_VALUE;
    
            
    
    private final boolean isShowWhyRejected = true;
    private final boolean isShowSwipeData = true;
    private final boolean isShowUserVariables = true;
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
        nrPartialRotateSwipes = 0;
        nrPartialCameraSwipes = 0;
        timeBetweenRotateSwipes = 0;
        timePreviousIntendedRotateSwipe = Long.MAX_VALUE;
        timeBetweenCameraSwipes = 0;
        timePreviousIntendedCameraSwipe = Long.MAX_VALUE;
        minimalTimeBetweenRotateSwipes = calculateMinimalTimeBetweenGestures(averageDurationRotateSwipe, 
                stdevDurationRotateSwipe, ROTATE_TIME_BETWEEN_SENSITIVITY);
        minimalTimeBetweenCameraSwipes = calculateMinimalTimeBetweenGestures(averageDurationCameraSwipe, 
                stdevDurationCameraSwipe, CAMERA_TIME_BETWEEN_SENSITIVITY);
        
        ROTATE_Z_SENS_VALUE = calculateSensitivyValue(ROTATE_Z_SENSITIVITY);
        ROTATE_DURATION_SENS_VALUE = calculateSensitivyValue(ROTATE_DURATION_SENSITIVITY);
        CAMERA_Z_SENS_VALUE = calculateSensitivyValue(CAMERA_Z_SENSITIVITY);
        CAMERA_DURATION_SENS_VALUE = calculateSensitivyValue(CAMERA_DURATION_SENSITIVITY);
        ROTATE_PART_DURATION_SENS_VALUE = calculateSensitivyValue(ROTATE_PART_SENSITIVITY);
        CAMERA_PART_DURATION_SENS_VALUE = calculateSensitivyValue(CAMERA_PART_SENSITIVITY);
    }

    @Override
    public void update(float tpf) 
    {
        if (frame != null)
        {
            rotateVelocity = decayVelocity(rotateVelocity);
            cameraVelocity = decayVelocity(cameraVelocity);
            double rotateSwipeSpeed = 0.0, cameraSwipeSpeed = 0.0;
            boolean isIntended = false;
            if(isSwiped())
            { 
                if(isRotateSwipe)
                {
                    lastDirectionRotate = rotateSwipe.direction();
                    if(isIntendedAsRotateSwipe(rotateSwipe))
                    {
                        isIntended = true;
                        timePreviousIntendedRotateSwipe = rotateSwipe.frame().timestamp();
                        rotateSwipeSpeed = rotateSwipe.speed();
                    }
                    else
                    {
                        timeBetweenRotateSwipes = Math.abs(timePreviousIntendedRotateSwipe - rotateSwipe.frame().timestamp());
                    }
                    printDebugData("rotate");
                    boolean isNewAndIntended = isIntended && rotateSwipe.id() != previousRotateID;
                    calculateUserVariablesForRotate(rotateSwipe, isNewAndIntended);
                    previousRotateID = rotateSwipe.id();
                }
                else
                {
                    lastDirectionCamera = cameraSwipe.direction();
                    if(isIntendedAsCameraSwipe(cameraSwipe))
                    {
                        isIntended = true;
                        timePreviousIntendedCameraSwipe = cameraSwipe.frame().timestamp();
                        cameraSwipeSpeed = cameraSwipe.speed();
                    }
                    else
                    {
                        timeBetweenCameraSwipes = Math.abs(timePreviousIntendedCameraSwipe - cameraSwipe.frame().timestamp());
                    }
                    printDebugData("camera");
                    boolean isNewAndIntended = isIntended && cameraSwipe.id() != previousCameraID;
                    calculateUserVariablesForCamera(cameraSwipe, isNewAndIntended);
                    previousCameraID = cameraSwipe.id();
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
                if(isCorrectSwipe(s))
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

    private boolean isCorrectSwipe(SwipeGesture s) 
    {                        
        boolean isCorrectRotateDirection = (Math.abs(s.direction().getX())>Math.abs(s.direction().getY())
                && Math.abs(s.direction().getX())>Math.abs(s.direction().getZ()));
        
        boolean isCorrectCameraDirection = (Math.abs(s.direction().getY())>Math.abs(s.direction().getZ())
                && Math.abs(s.direction().getY())>Math.abs(s.direction().getX()));
        
        if(isCorrectRotateDirection)
        {
            isRotateSwipe = true;
            isCameraSwipe = false;
            if( isDirectionChanged(s) )
                timeBetweenRotateSwipes = Math.abs(timePreviousIntendedRotateSwipe - s.frame().timestamp());
        }
        else if(isCorrectCameraDirection)
        {
            isCameraSwipe = true;
            isRotateSwipe = false;
            if( isDirectionChanged(s) )
                timeBetweenCameraSwipes = Math.abs(timePreviousIntendedCameraSwipe - s.frame().timestamp());
        }
        
        /*boolean isMostRightOrLeftSwipe = (s2 == null) || 
                ((IS_RIGHT_HANDED 
                && s1.startPosition().getX() < s2.startPosition().getX())
                || 
                (!IS_RIGHT_HANDED 
                && s1.startPosition().getX() > s2.startPosition().getX()));
        */
        boolean isCorrectHand = false;
        if(!s.hands().isEmpty())
            isCorrectHand = (s.hands().get(0).equals(getRotateHand()));
        
        if(!"".equals(rejectedOn))
            rejectedOn = "";
        if(!isCorrectRotateDirection && !isCorrectCameraDirection)
            rejectedOn = rejectedOn + " + swipe direction ";
        if(!isCorrectHand)
            rejectedOn = rejectedOn + " + left/right hand ";
       
        return (isCorrectRotateDirection || isCorrectCameraDirection) 
                && isCorrectHand; //&&is MostRightOrLeftSwipe;
    }
    
    private boolean isIntendedAsRotateSwipe(SwipeGesture s)
    {
        boolean isAtRegularSwipePosition = (s.position().getZ() >= (averageZPartialRotateSwipe-ROTATE_Z_SENS_VALUE*stdevZPartialRotateSwipe) &&
        s.position().getZ() <= (averageZPartialRotateSwipe+ROTATE_Z_SENS_VALUE*stdevZPartialRotateSwipe));

        boolean isOffRegularSwipeDuration = (s.duration() == 0) 
                || 
                (s.duration() >= (averagePartialDurationRotate-ROTATE_PART_DURATION_SENS_VALUE*stdevPartialDurationRotate) 
                && s.duration() <= (averagePartialDurationRotate+ROTATE_PART_DURATION_SENS_VALUE*stdevPartialDurationRotate));
        
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
        boolean isAtRegularSwipePosition = (s.position().getZ() >= (averageZPartialCameraSwipe-CAMERA_Z_SENS_VALUE*stdevZPartialCameraSwipe) &&
        s.position().getZ() <= (averageZPartialCameraSwipe+CAMERA_Z_SENS_VALUE*stdevZPartialCameraSwipe));

        boolean isOffRegularSwipeDuration = (s.duration() == 0)
                ||
                (s.duration() >= (averagePartialDurationCamera-CAMERA_PART_DURATION_SENS_VALUE*stdevPartialDurationCamera) 
        && s.duration() <= (averagePartialDurationCamera+CAMERA_PART_DURATION_SENS_VALUE*stdevPartialDurationCamera));
        
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
                velocity = rotateVelocity + ((1/frame.currentFramesPerSecond())+(speed/ROTATE_SPEED_INCREASE));  
            else if(lastDirectionRotate.getX()<0)
                velocity = rotateVelocity - ((1/frame.currentFramesPerSecond())+(speed/ROTATE_SPEED_INCREASE));
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
                velocity = cameraVelocity + ((1/frame.currentFramesPerSecond())+(speed/CAMERA_SPEED_INCREASE));  
            else if(lastDirectionCamera.getY()<0)
                velocity = cameraVelocity - ((1/frame.currentFramesPerSecond())+(speed/CAMERA_SPEED_INCREASE));
            
            
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
    
    private void calculateUserVariablesForRotate(SwipeGesture rotateSwipe, boolean isNewAndIntended)
    {
        nrPartialRotateSwipes++;
        double previousAverage = averageZPartialRotateSwipe, 
                zCoordinate = rotateSwipe.position().getZ(),
                duration = rotateSwipe.duration();
        
        averageZPartialRotateSwipe = calculateAverage(averageZPartialRotateSwipe, zCoordinate, nrPartialRotateSwipes);
        if(nrPartialRotateSwipes > 1)
            stdevZPartialRotateSwipe = calculateStdev(stdevZPartialRotateSwipe, previousAverage, averageZPartialRotateSwipe, zCoordinate);
        
        previousAverage = averagePartialDurationRotate;
        averagePartialDurationRotate = calculateAverage(averagePartialDurationRotate, duration, nrPartialRotateSwipes);
        if(nrPartialRotateSwipes > 1)
            stdevPartialDurationRotate = calculateStdev(stdevPartialDurationRotate, previousAverage, averagePartialDurationRotate, duration);
        
        
        summedPartialRotateSwipes += duration;
        if(isNewAndIntended)
        {
            nrIntendedRotateSwipes++;
            previousAverage = averageDurationRotateSwipe;
            averageDurationRotateSwipe = calculateAverage(averageDurationRotateSwipe, summedPartialRotateSwipes, nrIntendedRotateSwipes);
            if(nrIntendedRotateSwipes > 1)
            {
                stdevDurationRotateSwipe = calculateStdev(stdevDurationRotateSwipe, previousAverage, averageDurationRotateSwipe, summedPartialRotateSwipes);
            }
            if(averageDurationRotateSwipe < (ROTATE_MIN_ACCEPT_DURATION-ROTATE_DURATION_SENS_VALUE*stdevDurationRotateSwipe))
                averageDurationRotateSwipe =  ROTATE_MIN_ACCEPT_DURATION;
            summedPartialRotateSwipes = 0.0;
            minimalTimeBetweenRotateSwipes = calculateMinimalTimeBetweenGestures(averageDurationRotateSwipe, 
                stdevDurationRotateSwipe, ROTATE_TIME_BETWEEN_SENSITIVITY);
        }
    }
    
    private void calculateUserVariablesForCamera(SwipeGesture cameraSwipe, boolean isNewAndIntended)
    {
        nrPartialCameraSwipes++;
        double previousAverage = averageZPartialCameraSwipe, 
               zCoordinate = cameraSwipe.position().getZ(),
               duration = cameraSwipe.duration();
        
        averageZPartialCameraSwipe = calculateAverage(averageZPartialCameraSwipe, zCoordinate, nrPartialCameraSwipes);
        if(nrPartialCameraSwipes > 1)
            stdevZPartialCameraSwipe = calculateStdev(stdevZPartialCameraSwipe, previousAverage, averageZPartialCameraSwipe, zCoordinate);
        
        previousAverage = averagePartialDurationCamera;
        averagePartialDurationCamera = calculateAverage(averagePartialDurationCamera, duration, nrPartialCameraSwipes);
        if(nrPartialCameraSwipes > 1)
            stdevPartialDurationCamera = calculateStdev(stdevPartialDurationCamera, previousAverage, averagePartialDurationCamera, duration);
        
        summedPartialCameraSwipes += duration;
        if(isNewAndIntended)
        {
            nrIntendedCameraSwipes++;
            previousAverage = averageDurationCameraSwipe;
            averageDurationCameraSwipe = calculateAverage(averageDurationCameraSwipe, summedPartialCameraSwipes, nrIntendedCameraSwipes);
            if(nrIntendedCameraSwipes > 1)
            {
                stdevDurationCameraSwipe = calculateStdev(stdevDurationCameraSwipe, previousAverage, averageDurationCameraSwipe, summedPartialCameraSwipes);
            }
            if(averageDurationCameraSwipe < (CAMERA_MIN_ACCEPT_DURATION-CAMERA_DURATION_SENS_VALUE*stdevDurationCameraSwipe))
                averageDurationCameraSwipe =  CAMERA_MIN_ACCEPT_DURATION;
            summedPartialCameraSwipes = 0.0;
            minimalTimeBetweenCameraSwipes = calculateMinimalTimeBetweenGestures(averageDurationCameraSwipe, 
                stdevDurationCameraSwipe, CAMERA_TIME_BETWEEN_SENSITIVITY);
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
                System.out.println("\tPart. Duration:\t\t" + averagePartialDurationRotate + " +/- " + stdevPartialDurationRotate*ROTATE_PART_DURATION_SENS_VALUE + "\t\tsensValue = "+ROTATE_PART_DURATION_SENS_VALUE);
                System.out.println("\tPart. Z coordinate:\t" + averageZPartialRotateSwipe + " +/- " + stdevZPartialRotateSwipe*ROTATE_Z_SENS_VALUE + "\t\tsensValue = "+ROTATE_Z_SENS_VALUE);
                System.out.println("\tSwipe Duration:\t\t" + averageDurationRotateSwipe + " +/- " + stdevDurationRotateSwipe*ROTATE_DURATION_SENS_VALUE + "\t\tsensValue = "+ROTATE_DURATION_SENS_VALUE);
                System.out.println("\tMin. Time between:\t" + minimalTimeBetweenRotateSwipes);
            }
            else if("camera".equals(swipeType))
            {
                System.out.println("User variables of a " + swipeType + " swipe:");
                System.out.println("\tPart. Duration:\t\t" + averagePartialDurationCamera + " +/- " + stdevPartialDurationCamera*CAMERA_PART_DURATION_SENS_VALUE + "\t\tsensValue = "+ CAMERA_PART_DURATION_SENS_VALUE);
                System.out.println("\tPart. Z coordinate:\t" + averageZPartialCameraSwipe + " +/- " + stdevZPartialCameraSwipe*CAMERA_Z_SENS_VALUE + "\t\tsensValue = "+ CAMERA_Z_SENS_VALUE);
                System.out.println("\tSwipe Duration:\t\t" + averageDurationCameraSwipe + " +/- " + stdevDurationCameraSwipe*CAMERA_DURATION_SENS_VALUE + "\t\tsensValue = "+ CAMERA_DURATION_SENS_VALUE);
                System.out.println("\tMin. Time between:\t" + minimalTimeBetweenCameraSwipes);
            }
        }
        if(isShowSwipeData && ((rotateSwipe != null && isRotateSwipe) || (cameraSwipe != null && isCameraSwipe)))
        {
            if("rotate".equals(swipeType))
            {
                System.out.println(swipeType + " Swipe data (id:"+ rotateSwipe.id() +"):");
                System.out.println("State:\t" + rotateSwipe.state().toString());
                System.out.println("\tDirection:\t" + rotateSwipe.direction());
                System.out.println("\tDuration:\t" + rotateSwipe.duration());
                System.out.println("\tSpeed:\t\t" + rotateSwipe.speed());
                System.out.println("\tZ coordinate:\t" + rotateSwipe.position().getZ());
                System.out.println("\tTime between:\t" + timeBetweenRotateSwipes);
            }
            else if("camera".equals(swipeType))
            {
                System.out.println(swipeType + " Swipe data (id:"+ cameraSwipe.id() +"):");
                System.out.println("State:\t" + cameraSwipe.state().toString());
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
        //double relativeStdev = stdev/(stdev+average);
        //return average*relativeStdev*sensitivityValue;
        return sensitivityValue * (average + stdev);
    }

    private boolean isDirectionChanged(SwipeGesture s) 
    {
        if(isRotateSwipe)
        {
            if(lastDirectionRotate == null)
                return true;
            return Math.signum(lastDirectionRotate.getX()) != Math.signum(s.direction().getX());
        }
        else if(isCameraSwipe)
        {
            if(lastDirectionCamera == null)
                return true;
            return Math.signum(lastDirectionCamera.getY()) != Math.signum(s.direction().getY());
        }
        else
            return false;
    }
}
