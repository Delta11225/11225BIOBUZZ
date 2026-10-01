package org.firstinspires.ftc.teamcode.pedroPathing.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Intake {

    private DcMotor intake;

    //intake time tracks time since we last triggered the intake
    public ElapsedTime intakeTime = new ElapsedTime();

    //intakeDuration keeps track of how long we want the intake to run during this action
    private double intakeDuration = 0;

    //these booleans keep track of if we have triggered the intake or outake action
    //"Are we inaking" and "are we outtaking" from the state machine flowchart
    // set both to false on initialization so it doesn't run until we tell it to
    private boolean areWeIntaking = false;
    private boolean areWeOutaking = false;

    //we want the enums to be public so we can access them in teleop
    public enum IntakeState{
        IDLE,
        INTAKING,
        OUTTAKING
    }
    // this helps us keep track of current state. Set starting state as idle.
    private IntakeState intakeState = IntakeState.IDLE;

////////////////INIT STUFF/////////////////////////////

    public void init(HardwareMap hardwareMap){
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setDirection(DcMotor.Direction.REVERSE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //redundant, but setting intake to idle again just to make sure.
        intakeState = IntakeState.IDLE;
        intake.setPower(0);
        
    }


    // this is the main update loop. We must call this method (intake.update();) in our autonomous so that it checks continuously.
    public void update(){
        switch(intakeState){
            case IDLE:
                if(areWeIntaking){
                    intakeTime.reset();
                    intakeState = IntakeState.INTAKING;

                } else if (areWeOutaking) {
                    intakeTime.reset();
                    intakeState = IntakeState.OUTTAKING;
                }
                break;//you must have break at the end of each case

            case INTAKING:
                intake.setPower(1);
                //check the timer to see if we need to stay in the intaking state
                if (intakeTime.seconds() >= intakeDuration){
                    intake.setPower(0);
                    areWeIntaking = false;
                    intakeState = IntakeState.IDLE;
                }
                break;

            case OUTTAKING:
                intake.setPower(-1);
                if(intakeTime.seconds() >= intakeDuration){
                    intake.setPower(0);
                    areWeOutaking = false;
                    intakeState = IntakeState.IDLE;
                }
                break;
        }
    }
    public Runnable intakeSeconds(double targetDuration) {
        return () -> {
            if (intakeState == IntakeState.IDLE) {
                intakeDuration = targetDuration;
                areWeIntaking = true;
            }
        };
    }
    public Runnable outakeSeconds (double targetDuration){
        return () -> {
            if(intakeState == IntakeState.IDLE){
                intakeDuration = targetDuration;
                areWeOutaking = true;
            }
        };
    }
}


