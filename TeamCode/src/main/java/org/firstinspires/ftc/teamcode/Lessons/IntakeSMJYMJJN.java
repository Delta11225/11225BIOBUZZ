package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeSMJYMJJN {


    public enum MotorState{
        IDLE,
        INTAKE,
        OUTTAKE
    }

    //declaring all hardware members
    private DcMotor intake;
    private CRServo intakeSideLeft;
    private CRServo intakeSideRight;




    private final double ForwardPower = 1.0;
    private final double BackwardPower = -1.0;


    private IntakeSMJYMJJN.MotorState currentState = IntakeSMJYMJJN.MotorState.IDLE;

    public void init(HardwareMap hardwareMap){
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE );

        intakeSideLeft = hardwareMap.get(CRServo.class, "intakeSL");
        intakeSideRight = hardwareMap.get(CRServo.class, "intakeSR");
    }
    public IntakeSMJYMJJN.MotorState getState(){
        return currentState;
    }

    public void setState(IntakeSMJYMJJN.MotorState newState) {
        currentState = newState;
    }

    public void stop(){
        intake.setPower(0);
        intakeSideLeft.setPower(0);
        intakeSideRight.setPower(0);

        currentState = IntakeSMJYMJJN.MotorState.IDLE;
    }
    public void update(){

        switch (currentState){
            case INTAKE:
                intake.setPower(ForwardPower);
                intakeSideLeft.setPower(ForwardPower);
                intakeSideRight.setPower(ForwardPower);
                break;
            case OUTTAKE:
                intake.setPower(BackwardPower);
                intakeSideLeft.setPower(BackwardPower);
                intakeSideRight.setPower(BackwardPower);
                break;
            case IDLE:
                intake.setPower(0);
                intakeSideLeft.setPower(0);
                intakeSideRight.setPower(0);
                break;
        }
    }
}
