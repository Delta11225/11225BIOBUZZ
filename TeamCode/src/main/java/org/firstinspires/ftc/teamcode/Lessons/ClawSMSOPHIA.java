package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ClawSMSOPHIA {
    public enum MotorState{
        OPEN,
        CLOSED
    }
    private Servo servoLeft;
    private Servo servoRight;
    private final double openLeft = 0.8;
    private final double openRight = 0.05;
    private final double closedLeft = 0.45;
    private final double closedRight = 0.4;
    private ClawSMSOPHIA.MotorState currentState = ClawSMSOPHIA.MotorState.CLOSED;
    public void init(HardwareMap hardwareMap){
        servoRight = hardwareMap.get(Servo.class,"servo_Right");
        servoLeft=hardwareMap.get(Servo.class,"servo_Left");
    }

    public ClawSMSOPHIA.MotorState getState(){return currentState;}
    public void stop(){

        servoLeft.setPosition(closedLeft);
        servoRight.setPosition(closedRight);

        currentState= ClawSMSOPHIA .MotorState.CLOSED;
    }
    public void setState(ClawSMSOPHIA.MotorState newState){currentState=newState;}


    public void update(){
        switch (currentState){
            case OPEN:
                servoLeft.setPosition(openLeft);
                servoRight.setPosition(openRight);
                break;
            case CLOSED:
                servoLeft.setPosition(closedLeft);
                servoRight.setPosition(closedRight);
                break;
        }
    }
}
