package org.firstinspires.ftc.teamcode.MsPacman.StateMachines;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class LauncherSM {

    // --- 1. Define all possible states ---
    public enum LauncherState {
        IDLE,
        REVVING_UP,
        READY_TO_LAUNCH,
        LAUNCHING
    }

    // --- 2. Hardware members ---
    public DcMotorEx leftFlyWheel;//facing the intake
    private DcMotorEx rightFlyWheel;//facing the intake

    // --- 3. Constants (adjust as needed) ---
    public final double TICKS_PER_MOTOR_REV = 28;//@SOPHIA This was set to the wrong value
    double RPMTolerance = 100;
    public double flywheelTargetRPM = 0;
    //double flywheelTargetTPS = (flywheelTargetRPM/60.0)*TICKS_PER_MOTOR_REV;


    // --- 4. State variables ---
    private LauncherState launcherState = LauncherState.IDLE;


    // --- 5. Initialization ---
    /**
     * Call this in TeleOp init() to initialize hardware.
     */
    public void init(HardwareMap hardwareMap) {
        leftFlyWheel = hardwareMap.get(DcMotorEx.class, "left_launcher");
        rightFlyWheel = hardwareMap.get(DcMotorEx.class, "right_launcher");

        leftFlyWheel.setDirection(DcMotorEx.Direction.REVERSE);//@Sophia & Lillian, motor directions were flipped

        leftFlyWheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightFlyWheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        leftFlyWheel.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightFlyWheel.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
    }

    // --- 6. Getter & Setter Methods for state ---
   //getter gets the state
    public LauncherState getLaunchState() {

        return launcherState;
    }
    //setter sets the state
    public void setLauncherState(LauncherState newState){

        launcherState = newState;
    }

    //@Sophia & Lillian we need to write a method to set flywheelTargetRPM
    //this will allow us to change targetRPM to different numbers in teleOp


    // --- 7. Optional: stop() method for TeleOp end ---
    public void stop() {
        leftFlyWheel.setVelocity(0);
        rightFlyWheel.setVelocity(0);
        launcherState = LauncherState.IDLE;
    }


    // --- 8. Update logic ---
    /**
     * Call this every loop() to handle state transitions and motor control
     */


    public void update(double flywheelTargetRPM) {
        // --- 6a. Determine state based on inputs and current state ---

        double flywheelTargetTPS = (flywheelTargetRPM/60.0)*TICKS_PER_MOTOR_REV;
        double leftFlyWheelRPM = (leftFlyWheel.getVelocity()*60/TICKS_PER_MOTOR_REV);////@SOPHIA Since this uses .getVelocity it needed to be in the update loop


        // --- 6b. Act based on current state ---
        switch (launcherState) {
            case IDLE:
                leftFlyWheel.setVelocity(0);
                rightFlyWheel.setVelocity(0);
                break;
            case REVVING_UP:
                leftFlyWheel.setVelocity(flywheelTargetTPS);
                rightFlyWheel.setVelocity(flywheelTargetTPS);
                if(Math.abs(leftFlyWheelRPM-flywheelTargetRPM)<RPMTolerance){
                    setLauncherState(LauncherState.READY_TO_LAUNCH);
                }
                break;
            case READY_TO_LAUNCH:
                leftFlyWheel.setVelocity(flywheelTargetTPS);
                rightFlyWheel.setVelocity(flywheelTargetTPS);
                break;
            case LAUNCHING:
                leftFlyWheel.setVelocity(flywheelTargetTPS);
                rightFlyWheel.setVelocity(flywheelTargetTPS);
                break;
        }
    }



}

