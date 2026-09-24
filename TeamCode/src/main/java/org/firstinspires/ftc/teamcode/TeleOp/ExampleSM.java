package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ExampleSM {

    //Step 1:Define all possible states
    //use all caps for states
    //enum is special data type to define a list/collection/group of constants
    public enum MotorState{
        IDLE,
        FORWARD,
        BACKWARD
    }

    //Step 2: Declare your hardware that will be used in this state machine
    private DcMotor motor1;

    //Step 3: Define any constants that you will use
    //doing this makes it easy to edit it one place later and
    //not have to find and change multiple values in your code
    private final double ForwardPower = 1.0;
    private final double BackwardPower = -1.0;

    //Step 4: Create a variable to hold your state
    private MotorState currentState = MotorState.IDLE;

    //Step 5: Initialize your hardware for the state machine

    public void init (HardwareMap hardwareMap){
        motor1 = hardwareMap.get(DcMotor.class, "motor_1");
        //Use the next line if you are utilizing encoders
        motor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //Use the next line if you want your motor power of 0 to hold as a hard brake and not idle
        motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    //Step 6: Create "Getter" and "Setter" Methods for the state

    //Getter Method: this method allows you to check what the current state is
    public MotorState getState(){
        return currentState;
    }

    //Setter Method: this method allows you to set the state to a desired state in teleop
    public void setState(ExampleSM.MotorState newState){
        currentState = newState;
    }

    //Step 7: Optional - Create a "Stopper" method to stop the motor
    //We typically use this at the end of teleop in the stop loop just to ensure all motors stop
    public void stop(){
        motor1.setPower(0);
        currentState = MotorState.IDLE;
    }


    //Step 8: Create Update Logic
    //You must call this update method every loop in teleOp so that it can update the states

    public void update(){

        switch (currentState){
            case FORWARD:
                motor1.setPower(ForwardPower);
                break;
            case BACKWARD:
                motor1.setPower(BackwardPower);
                break;
            case IDLE:
                motor1.setPower(0);
                break;
        }
    }
}
