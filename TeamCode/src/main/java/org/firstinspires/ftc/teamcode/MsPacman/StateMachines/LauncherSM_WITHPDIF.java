package org.firstinspires.ftc.teamcode.MsPacman.StateMachines;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class LauncherSM_WITHPDIF {

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

    public double F = 12.623;
    public double P = 11;

    boolean lastDpadRightPressed = false;
    boolean lastDpadLeftPressed = false;

    // --- 3. Constants (adjust as needed) ---
    public final double TICKS_PER_MOTOR_REV = 28;//@SOPHIA This was set to the wrong value
    double RPMTolerance = 200;
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

        leftFlyWheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightFlyWheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        leftFlyWheel.setDirection(DcMotorEx.Direction.REVERSE);

        PIDFCoefficients PIDF = new PIDFCoefficients(
                P, //P - Corrects based on how far the actual velocity is from the target velocity
                0, //I - not nessecary for launch motors
                0, //D - not nessecary fro launch motors
                F //F - Baseline velocity coefficient (usually 1) Ex: (1 x 3300)
        );

        leftFlyWheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, PIDF);
        rightFlyWheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, PIDF);

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

        PIDFCoefficients PIDF = new PIDFCoefficients(
                P, //P - Corrects based on how far the actual velocity is from the target velocity
                0, //I - not nessecary for launch motors
                0, //D - not nessecary fro launch motors
                F //F - Baseline velocity coefficient (usually 1) Ex: (1 x 3300)
        );

        leftFlyWheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, PIDF);
        rightFlyWheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, PIDF);

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

