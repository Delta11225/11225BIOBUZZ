package org.firstinspires.ftc.teamcode.TeleOp.StateMachines;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class BerthaLauncherSMPIDF {
    // --- 1. Define all possible states ---
    public enum LauncherState {
        IDLE,
        REVVING_UP,
        READY_TO_LAUNCH,
        LAUNCHING
    }

    // --- 2. Hardware members ---
    private DcMotorEx launcher;

    public double F = 12.5;
    public double P = 50;
    public double D = 0.03;

    boolean lastDpadRightPressed = false;
    boolean lastDpadLeftPressed = false;

    // --- 3. Constants (adjust as needed) ---
    public final double TICKS_PER_MOTOR_REV = 28;
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

        launcher = hardwareMap.get(DcMotorEx.class, "launcher");


        launcher.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients PIDF = new PIDFCoefficients(
                P, //P - Corrects based on how far the actual velocity is from the target velocity
                0, //I - not nessecary for launch motors
                D,
                F //F - Baseline velocity coefficient (usually 1) Ex: (1 x 3300)
        );

        launcher.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, PIDF);

        launcher.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
    }

    // --- 6. Getter & Setter Methods for state ---
    //getter gets the state
    public LauncherState getLaunchState() {

        return launcherState;
    }
    //setter sets the state
    public void setLauncherState(BerthaLauncherSMPIDF.LauncherState newState){

        launcherState = newState;
    }

    //@Sophia & Lillian we need to write a method to set flywheelTargetRPM
    //this will allow us to change targetRPM to different numbers in teleOp


    // --- 7. Optional: stop() method for TeleOp end ---
    public void stop() {
        launcher.setVelocity(0);
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

        launcher.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, PIDF);

        double flywheelTargetTPS = (flywheelTargetRPM/60.0)*TICKS_PER_MOTOR_REV;
        double leftFlyWheelRPM = (launcher.getVelocity()*60/TICKS_PER_MOTOR_REV);////@SOPHIA Since this uses .getVelocity it needed to be in the update loop



        // --- 6b. Act based on current state ---
        switch (launcherState) {
            case IDLE:
                launcher.setVelocity(0);
                break;
            case REVVING_UP:
                launcher.setVelocity(flywheelTargetTPS);
                if(Math.abs(leftFlyWheelRPM-flywheelTargetRPM)<RPMTolerance){
                    setLauncherState(LauncherState.READY_TO_LAUNCH);
                }
                break;
            case READY_TO_LAUNCH:
                launcher.setVelocity(flywheelTargetTPS);
                break;
            case LAUNCHING:
                launcher.setVelocity(flywheelTargetTPS);
                break;
        }
    }

}
