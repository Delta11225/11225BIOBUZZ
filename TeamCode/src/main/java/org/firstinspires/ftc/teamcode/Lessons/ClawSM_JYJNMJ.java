package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ClawSM_JYJNMJ {


    public enum MotorState{
        OPEN,
        CLOSED
    }
    private Servo servo1 = null;
    private Servo servo2 = null;
    private ClawSM_JYJNMJ.MotorState currentState = ClawSM_JYJNMJ.MotorState.OPEN;

    public void init(HardwareMap hardwareMap){
        servo1 = hardwareMap.get(Servo.class, "servo_1");
        servo2 = hardwareMap.get(Servo.class, "servo_2");

    }
    public ClawSM_JYJNMJ.MotorState getState(){
        return currentState;
    }

    public void setState(ClawSM_JYJNMJ.MotorState newState) {
        currentState = newState;
    }

    public void stop(){
        servo1.setPosition(0.45);
        servo2.setPosition(0.4);
        currentState = MotorState.OPEN;
    }
    public void update(){

        switch (currentState){
            case CLOSED:
                servo1.setPosition(0.8);
                servo2.setPosition(0.05);
                break;

            case OPEN:
                servo1.setPosition(0.45);
                servo2.setPosition(0.4);
                break;
        }
    }
}
