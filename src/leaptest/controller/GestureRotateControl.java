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
import leaptest.model.LeapCalibrator;
import leaptest.utils.TweakSet;
import leaptest.utils.TweakVariable;
import leaptest.utils.Tweakable;

/**
 *
 * @author Jasper van der Waa & Annet Meijers
 */
public class GestureRotateControl extends LeapControl implements Tweakable
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
    private float Rotate_Z_Sensitivity = 0.9f;
    private float Rotate_Duration_Sensitivity = 0.25f;
    private float Rotate_Time_Between_Sensitivity = 0.6f;
    private float Rotate_Part_Sensitivity = 0.5f;
    private float Rotate_Min_Accept_Duration = 75000f;
    
    private float Camera_Z_Sensitivity = 0.9f;
    private float Camera_Duration_Sensitivity = 0.75f;
    private float Camera_Time_Between_Sensitivity = 0.5f;
    private float Camera_Part_Sensitivity = 0.5f;
    private float Camera_Min_Accept_Duration = 75000f;
    /*
     * Variables that get adjusted over time for a user.
     * Change the initial values to match more the initial preferences of the 
     * user.
    */
    private float averageZPartialRotateSwipe = 0.0f;
    private float stdevZPartialRotateSwipe = 60f;
    private float averagePartialDurationRotate = 10000f;
    private float stdevPartialDurationRotate = 1000f;
    private float averageDurationRotateSwipe = 200000f;
    private float stdevDurationRotateSwipe = 20000f;
    
    private float averageZPartialCameraSwipe = 0.0f;
    private float stdevZPartialCameraSwipe = 80f;
    private float averagePartialDurationCamera = 20000f;
    private float stdevPartialDurationCamera = 2000f;
    private float averageDurationCameraSwipe = 200000f;
    private float stdevDurationCameraSwipe = 100000f;
    
    /*
     * Variables to fine tune the movement of the grid.
    */
    private float Rotate_Speed_Increase = 100000f;
    private float Camera_Speed_Increase = 70000f;
    private float Rotate_Decay_Constant = 0.85f;
    private float Camera_Decay_Constant = 0.65f;
    private float Max_Velocity_Rotate = 0.25f;
    private float Min_Velocity_Rotate = 0.001f;
    private float Max_Velocity_Camera = 0.025f;
    private float Min_Velocity_Camera = 0.0001f;
    private boolean IS_RIGHT_HANDED = true;
    private boolean INVERT_Y_AXIS_FOR_CAMERA = false;
    
    
    
    private Frame frame;
    private Grid grid;
    private SwipeGesture rotateSwipe;
    private SwipeGesture cameraSwipe;
    private GridCam camera;
    private final double ROTATION_DELTA = Math.PI;
    
    private boolean isRotateSwipe;
    private Vector lastDirectionRotate;
    private float rotateVelocity;
    private float summedPartialRotateSwipes;
    private int nrPartialRotateSwipes;
    private long timeBetweenRotateSwipes;
    private float minimalTimeBetweenRotateSwipes;
    private int nrIntendedRotateSwipes;
    
    private boolean isCameraSwipe;
    private Vector lastDirectionCamera;
    private float cameraVelocity;
    private float summedPartialCameraSwipes;
    private int nrPartialCameraSwipes;
    private long timeBetweenCameraSwipes;
    private float minimalTimeBetweenCameraSwipes;    
    private int nrIntendedCameraSwipes;
    
    private long timePreviousIntendedRotateSwipe;
    private int previousRotateID;
    private long timePreviousIntendedCameraSwipe;
    private int previousCameraID;
    
    private final float ROTATE_Z_SENS_VALUE;
    private final float ROTATE_DURATION_SENS_VALUE;
    private final float ROTATE_PART_DURATION_SENS_VALUE;
    private final float CAMERA_Z_SENS_VALUE;
    private final float CAMERA_DURATION_SENS_VALUE;
    private final float CAMERA_PART_DURATION_SENS_VALUE;
    
            
    
    private final boolean isShowWhyRejected = true;
    private final boolean isShowSwipeData = true;
    private final boolean isShowUserVariables = true;
    private String rejectedOn = "";
    
    
    public GestureRotateControl(LeapCalibrator leap, Grid grid, GridCam camera)
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
                stdevDurationRotateSwipe, Rotate_Time_Between_Sensitivity);
        minimalTimeBetweenCameraSwipes = calculateMinimalTimeBetweenGestures(averageDurationCameraSwipe, 
                stdevDurationCameraSwipe, Camera_Time_Between_Sensitivity);
        
        ROTATE_Z_SENS_VALUE = calculateSensitivyValue(Rotate_Z_Sensitivity);
        ROTATE_DURATION_SENS_VALUE = calculateSensitivyValue(Rotate_Duration_Sensitivity);
        CAMERA_Z_SENS_VALUE = calculateSensitivyValue(Camera_Z_Sensitivity);
        CAMERA_DURATION_SENS_VALUE = calculateSensitivyValue(Camera_Duration_Sensitivity);
        ROTATE_PART_DURATION_SENS_VALUE = calculateSensitivyValue(Rotate_Part_Sensitivity);
        CAMERA_PART_DURATION_SENS_VALUE = calculateSensitivyValue(Camera_Part_Sensitivity);
    }

    @Override
    public void update(float tpf) 
    {
        if (frame != null)
        {
            rotateVelocity = decayVelocity(rotateVelocity);
            cameraVelocity = decayVelocity(cameraVelocity);
            float rotateSwipeSpeed = 0.0f, cameraSwipeSpeed = 0.0f;
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
        frame = leap.frame();
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

    private float calculateVelocity(float speed) 
    {
        float velocity =0.0f;      
        if(isRotateSwipe)
        {
            if(lastDirectionRotate == null || speed == 0.0)
                velocity = rotateVelocity;
            else if(lastDirectionRotate.getX()>0)
                velocity = rotateVelocity + ((1/frame.currentFramesPerSecond())+(speed/Rotate_Speed_Increase));  
            else if(lastDirectionRotate.getX()<0)
                velocity = rotateVelocity - ((1/frame.currentFramesPerSecond())+(speed/Rotate_Speed_Increase));
            if(Math.abs(velocity) > Max_Velocity_Rotate )
                return Math.signum(velocity)*Max_Velocity_Rotate;
            if(Math.abs(velocity) < Min_Velocity_Rotate )
                return 0.0f;
        }
        else if(isCameraSwipe)
        {
            if(lastDirectionCamera == null || speed == 0.0)
                velocity = cameraVelocity;
            else if(lastDirectionCamera.getY()>0)
                velocity = cameraVelocity + ((1/frame.currentFramesPerSecond())+(speed/Camera_Speed_Increase));  
            else if(lastDirectionCamera.getY()<0)
                velocity = cameraVelocity - ((1/frame.currentFramesPerSecond())+(speed/Camera_Speed_Increase));
            
            
            if(Math.abs(velocity) > Max_Velocity_Camera )
                return Math.signum(velocity)*Max_Velocity_Camera;
            if(Math.abs(velocity) < Min_Velocity_Camera )
                return 0.0f;
        }
        return velocity;
    }
    
    private float decayVelocity(float velocity)
    {
        if(isRotateSwipe)
            return velocity*Rotate_Decay_Constant; 
        else if(isCameraSwipe)
            return velocity*Camera_Decay_Constant; 
        else
            return 0;
    }
    
    private void calculateUserVariablesForRotate(SwipeGesture rotateSwipe, boolean isNewAndIntended)
    {
        nrPartialRotateSwipes++;
        float previousAverage = averageZPartialRotateSwipe, 
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
            if(averageDurationRotateSwipe < (Rotate_Min_Accept_Duration-ROTATE_DURATION_SENS_VALUE*stdevDurationRotateSwipe))
                averageDurationRotateSwipe =  Rotate_Min_Accept_Duration;
            summedPartialRotateSwipes = 0.0f;
            minimalTimeBetweenRotateSwipes = calculateMinimalTimeBetweenGestures(averageDurationRotateSwipe, 
                stdevDurationRotateSwipe, Rotate_Time_Between_Sensitivity);
        }
    }
    
    private void calculateUserVariablesForCamera(SwipeGesture cameraSwipe, boolean isNewAndIntended)
    {
        nrPartialCameraSwipes++;
        float previousAverage = averageZPartialCameraSwipe, 
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
            if(averageDurationCameraSwipe < (Camera_Min_Accept_Duration-CAMERA_DURATION_SENS_VALUE*stdevDurationCameraSwipe))
                averageDurationCameraSwipe =  Camera_Min_Accept_Duration;
            summedPartialCameraSwipes = 0.0f;
            minimalTimeBetweenCameraSwipes = calculateMinimalTimeBetweenGestures(averageDurationCameraSwipe, 
                stdevDurationCameraSwipe, Camera_Time_Between_Sensitivity);
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

    private float calculateStdev(float stdev, float prevAverage, float average, float x) 
    {
        return (float) Math.sqrt(stdev+(x-prevAverage)*(x-average));
    }

    private float calculateAverage(float average, float x, int N) 
    {
        return (x+(N-1)*average)/N;
    }
    
    private float calculateSensitivyValue(float sensitivity)
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
    
    private float calculateMinimalTimeBetweenGestures(float average, float stdev, float sensitivityValue)
    {
        //double relativeStdev = stdev/(stdev+average);
        //return average*relativeStdev*sensitivityValue;
        return (float) sensitivityValue * (average + stdev);
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

    public TweakSet initTweakables() 
    {   
        TweakSet set = new TweakSet("GestureRotateControl",this);
        set.add(new TweakVariable <Float>("Rotate_Z_Sensitivity",Rotate_Z_Sensitivity));
        set.add(new TweakVariable <Float>("Rotate_Duration_Sensitivity",Rotate_Duration_Sensitivity));
        set.add(new TweakVariable <Float>("Rotate_Time_Between_Swipes_Sensitivity",Rotate_Time_Between_Sensitivity));
        set.add(new TweakVariable <Float>("Rotate_Partial_Sensitivity",Rotate_Part_Sensitivity));
        set.add(new TweakVariable <Float>("Rotate_Min_acceptance_duration",Rotate_Min_Accept_Duration));
        
        set.add(new TweakVariable <Float>("Camera_Z_Sensitivity",Camera_Z_Sensitivity));
        set.add(new TweakVariable <Float>("Camera_Duration_Sensitivity",Camera_Duration_Sensitivity));
        set.add(new TweakVariable <Float>("Camera_Time_Between_Swipes_Sensitivity",Camera_Time_Between_Sensitivity));
        set.add(new TweakVariable <Float>("Camera_Partial_Sensitivity",Camera_Part_Sensitivity));
        set.add(new TweakVariable <Float>("Camera_Min_acceptance_duration",Camera_Min_Accept_Duration));
    
        set.add(new TweakVariable <Float>("Rotate_speed_increase",Rotate_Speed_Increase));
        set.add(new TweakVariable <Float>("Camera_speed_increase",Camera_Speed_Increase));
        set.add(new TweakVariable <Float>("Rotate_decay_constant",Rotate_Decay_Constant));
        set.add(new TweakVariable <Float>("Camera_decay_constant",Camera_Decay_Constant));
        set.add(new TweakVariable <Float>("Max_velocity_rotate",Max_Velocity_Rotate));
        set.add(new TweakVariable <Float>("Min_velocity_rotate",Min_Velocity_Rotate));
        set.add(new TweakVariable <Float>("Max_velocity_camera",Max_Velocity_Camera));
        set.add(new TweakVariable <Float>("Min_velocity_camera",Min_Velocity_Camera));
        
        return set;
    }

    private enum Variables {Rotate_Z_Sensitivity, Rotate_Duration_Sensitivity, Rotate_Time_Between_Swipes_Sensitivity,
    Rotate_Partial_Sensitivity, Rotate_Min_acceptance_duration, Camera_Z_Sensitivity, Camera_Duration_Sensitivity, 
    Camera_Time_Between_Swipes_Sensitivity, Camera_Partial_Sensitivity, Camera_Min_acceptance_duration, Rotate_speed_increase,
    Camera_speed_increase, Rotate_decay_constant, Camera_decay_constant, Max_velocity_rotate, Max_velocity_camera, Min_velocity_camera}
    
    public void setVariable(TweakVariable var) 
    {
        Variables name = Variables.valueOf(var.getName());
        float varFloat = (Float) var.getValue();
        switch (name) 
        {
            case Rotate_Z_Sensitivity:
                Rotate_Z_Sensitivity = varFloat;
                break;
            case Rotate_Duration_Sensitivity:
                Rotate_Duration_Sensitivity = varFloat;
                break;
            case Rotate_Time_Between_Swipes_Sensitivity:
                Rotate_Time_Between_Sensitivity = varFloat;
                break;
            case Rotate_Partial_Sensitivity:
                Rotate_Part_Sensitivity = varFloat;
                break;
            case Rotate_Min_acceptance_duration:
                Rotate_Min_Accept_Duration = varFloat;
                break;
            case Camera_Z_Sensitivity:
                Camera_Z_Sensitivity = varFloat;
                break;
            case Camera_Duration_Sensitivity:
                Camera_Duration_Sensitivity = varFloat;
                break;
            case Camera_Time_Between_Swipes_Sensitivity:
                Camera_Time_Between_Sensitivity = varFloat;
                break;
            case Camera_Partial_Sensitivity:
                Camera_Part_Sensitivity = varFloat;
                break;
            case Camera_Min_acceptance_duration:
                Camera_Min_Accept_Duration = varFloat;
                break;
            case Rotate_speed_increase:
                Rotate_Speed_Increase = varFloat;
                break;
            case Camera_speed_increase:
                Camera_Speed_Increase = varFloat;
                break;
            case Rotate_decay_constant:
                Rotate_Decay_Constant = varFloat;
                break;
            case Camera_decay_constant:
                Camera_Decay_Constant = varFloat;
                break;
            case Max_velocity_rotate:
                Max_Velocity_Rotate = varFloat;
                break;
            case Max_velocity_camera:
                Max_Velocity_Camera = varFloat;
                break;
            case Min_velocity_camera:
                Min_Velocity_Camera = varFloat;
                break;
            default :
                System.err.println("Variable not found");
                break;
        }
    }
}
