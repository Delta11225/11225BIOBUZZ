package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeSMJYMJJN {


    public enum MotorState{
        IDLE,
        INTAKE,
        OUTTAKE
    }
    private DcMotor intake;
    private final double ForwardPower = 1.0;
    private final double BackwardPower = -1.0;
    private IntakeSMJYMJJN.MotorState currentState = IntakeSMJYMJJN.MotorState.IDLE;

    public void init(HardwareMap hardwareMap){
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE );
    }
    public IntakeSMJYMJJN.MotorState getState(){
        return currentState;
    }

    public void setState(IntakeSMJYMJJN.MotorState newState) {
        currentState = newState;
    }

    public void stop(){
        intake.setPower(0);
        currentState = IntakeSMJYMJJN.MotorState.IDLE;
    }
    public void update(){

        switch (currentState){
            case INTAKE:
                intake.setPower(ForwardPower);
                break;
            case OUTTAKE:
                intake.setPower(BackwardPower);
                break;
            case IDLE:
                intake.setPower(0);
                break;
        }
    }
}
