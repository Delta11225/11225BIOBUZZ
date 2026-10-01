package org.firstinspires.ftc.teamcode.pedroPathing.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Launcher {

    private DcMotorEx leftLauncher;
    private DcMotorEx rightLauncher;

    //launch time tracks time since we last triggered the intake
    public ElapsedTime launchTime = new ElapsedTime();

    //launch Duration keeps track of how long we want the intake to run during this action
    private double launchDuration = 0;

    //these booleans keep track of if we have triggered the launching action
    //"areWeLaunching" from the state machine flowchart
    // set to false on initialization so it doesn't run until we tell it to
    private boolean areWeLaunching = false;

    //we want the enums to be public so we can access them in teleop
    public enum LaunchState{
        IDLE,
        LAUNCHING

    }
    // this helps us keep track of current state. Set starting state as idle.
    private LaunchState launchState = LaunchState.IDLE;

////////////////INIT STUFF/////////////////////////////

    public void init(HardwareMap hardwareMap){
        leftLauncher = hardwareMap.get(DcMotorEx.class, "left_launcher");
        rightLauncher = hardwareMap.get(DcMotorEx.class, "rightLauncher");
        leftLauncher.setDirection(DcMotor.Direction.REVERSE);
        //redundant, but setting intake to idle again just to make sure.
        launchState = LaunchState.IDLE;
        leftLauncher.setPower(0);
        leftLauncher.setPower(0);
        
    }


    // this is the main update loop. We must call this method (launcher.update();) in our autonomous so that it checks continuously.
    public void update(){
        switch(launchState){
            case IDLE:
                if(areWeLaunching) {
                    launchTime.reset();
                    launchState = LaunchState.LAUNCHING;
                }
                break;//you must have break at the end of each case

            case LAUNCHING:
                leftLauncher.setPower(1);
                rightLauncher.setPower(1);
                //check the timer to see if we need to stay in the intaking state
                if (launchTime.seconds() >= launchDuration){
                    leftLauncher.setPower(0);
                    rightLauncher.setPower(0);
                    areWeLaunching = false;
                    launchState = LaunchState.IDLE;
                }
                break;
        }
    }
    public Runnable launchSeconds(double targetDuration) {
        return () -> {
            if (launchState == LaunchState.IDLE) {
                launchDuration = targetDuration;
                areWeLaunching = true;
            }
        };
    }
}


