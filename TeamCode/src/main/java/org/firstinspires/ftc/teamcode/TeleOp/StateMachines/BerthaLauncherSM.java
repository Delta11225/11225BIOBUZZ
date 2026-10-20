package org.firstinspires.ftc.teamcode.TeleOp.StateMachines;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class BerthaLauncherSM {
    public enum launchMotorStates{
        IDLE,
        LAUNCHING,

    }
    private DcMotor launcher;
    private final double launching = 0.25;
    private BerthaLauncherSM.launchMotorStates currentState= BerthaLauncherSM.launchMotorStates.IDLE;
    public void init(HardwareMap hardwareMap){
        launcher = hardwareMap.get(DcMotor.class,"launcher");

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public BerthaLauncherSM.launchMotorStates getState(){return currentState;}

    public void setState(BerthaLauncherSM.launchMotorStates newState){currentState=newState;}

    public void stop(){

        launcher.setPower(0);

        currentState= BerthaLauncherSM.launchMotorStates.IDLE;
    }
    public void update(){
        switch (currentState){
            case LAUNCHING:
                launcher.setPower(launching);
                break;
            case IDLE:
                launcher.setPower(0);
                break;
        }
    }

}
