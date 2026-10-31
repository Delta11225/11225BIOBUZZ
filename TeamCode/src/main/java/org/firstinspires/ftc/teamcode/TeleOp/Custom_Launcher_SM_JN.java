package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Custom_Launcher_SM_JN {
    //- - - 1. define all possible states - - -
    public enum LauncherState {
        IDLE,
        REVVING_UP,
        READY_TO_LAUNCH,
        LAUNCHING
    }

    // - - - 2. Hardware/Values - - -
    private DcMotorEx launcher;
    private Servo gate = null;
    private final ElapsedTime launchTimer = new ElapsedTime();

    public static final double F = 25.10;
    public static final double P = 173.02;
    public static final double D = 0.03;

    // - - - 3. Constants - - -

    public static final double TICKS_PER_MOTOR_REV = 28;
    public static final double LAUNCH_TIME_MS = 2500;
    public static final double RPM_TOLERANCE = 60;
    public static final double TARGET_RPM = 2190;
    public static final double GATE_CLOSED = 0.1;
    public static final double GATE_OPEN = 0.5;

    // - - - 4. State Variables - - -
    private LauncherState launcherState = LauncherState.IDLE;

    // - - - 5. Initialization - - -

        public void init (HardwareMap hardwareMap) {
            launcher = hardwareMap.get(DcMotorEx.class, "launcher");
            gate = hardwareMap.get(Servo.class, "gate");

            launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            PIDFCoefficients PIDF = new PIDFCoefficients(
                    P,
                    0,
                    D,
                    F
            );
            launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, PIDF);
            launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

    // - - - 6. Getter and button methods for state - - -
        public LauncherState getLaunchState()   {

        return launcherState;
        }
        public double getRPM() {

        return launcher.getVelocity() * 60 / TICKS_PER_MOTOR_REV;
        }
        public void revPressed () {
            if (launcherState == LauncherState.IDLE) {
                launcherState = LauncherState.REVVING_UP;
            }
            else if (launcherState == LauncherState.REVVING_UP || launcherState == LauncherState.READY_TO_LAUNCH) {
                launcherState = LauncherState.IDLE;
            }
        }

        public void launchPressed () {
            if(launcherState == LauncherState.READY_TO_LAUNCH) {
                launchTimer.reset();
                launcherState = LauncherState.LAUNCHING;
            }
        }


    // - - - 7. stop() method for TeleOp end - - -
    public void stop() {
        launcher.setVelocity(0);
        gate.setPosition(GATE_CLOSED);
        launcherState = LauncherState.IDLE;
    }


    public void update(double flywheelTargetRPM) {

        double flywheelTargetTPS = (flywheelTargetRPM/60.0)*TICKS_PER_MOTOR_REV;
        double flywheelRPM = getRPM();

        switch (launcherState) {
            case IDLE:
                launcher.setPower(0);
                gate.setPosition(GATE_CLOSED);
                //Goes to REVVING_UP when rev button is pressed (button)
                //Outputs: flywheel is off and gate is closed
                break;
            case REVVING_UP:
                launcher.setVelocity(flywheelTargetTPS);
                if(Math.abs(flywheelRPM-flywheelTargetRPM)< RPM_TOLERANCE){
                    launcherState = LauncherState.READY_TO_LAUNCH;
                }
                gate.setPosition(GATE_CLOSED);
                //Goes to READY_TO_LAUNCH when target RPM is reached (automatic)
                //Goes to IDLE when rev button is pressed (button)
                //Outputs: flywheel is going to target speed and gate is closed
                break;
            case READY_TO_LAUNCH:
                launcher.setVelocity(flywheelTargetTPS);
                gate.setPosition(GATE_CLOSED);
                //Goes to LAUNCHING when launch button is pressed (button)
                //Goes to IDLE when rev button is pressed (button)
                //Outputs: flywheel is holding target speed and gate is closed
                break;
            case LAUNCHING:
                launcher.setVelocity(flywheelTargetTPS);
                gate.setPosition(GATE_OPEN);
                if (launchTimer.milliseconds() >= LAUNCH_TIME_MS) {
                    launcherState = LauncherState.IDLE;
                }
                //Goes to IDLE once launch timer is up (automatic)
                //Rev press is ignored during LAUNCHING state
                //Outputs: flywheel is holding target speed and gate is open
                break;
        }
    }
}
