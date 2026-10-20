package org.firstinspires.ftc.teamcode.TeleOp.StateMachines;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class BerthaWindmillSM {
    public enum windmillState{
        FORWARD,
        BACKWARDS,
        IDLE
    }
    private CRServo windmill;
    private final double forwardPower = 0.5;
    private final double backwardPower = -0.5;
    private BerthaWindmillSM.windmillState currentState= BerthaWindmillSM.windmillState.IDLE;
    public void init(HardwareMap hardwareMap){
        windmill = hardwareMap.get(CRServo.class,"windmill");
    }
    public BerthaWindmillSM.windmillState getState(){return currentState;}

    public void setState(BerthaWindmillSM.windmillState newState){currentState=newState;}

    public void stop(){
        windmill.setPower(0);
        currentState= BerthaWindmillSM.windmillState.IDLE;
    }
    public void update(){
        switch (currentState){
            case FORWARD:
                windmill.setPower(forwardPower);
                break;
            case BACKWARDS:
                windmill.setPower(backwardPower);
            case IDLE:
                windmill.setPower(0);
                break;
        }
    }

}
