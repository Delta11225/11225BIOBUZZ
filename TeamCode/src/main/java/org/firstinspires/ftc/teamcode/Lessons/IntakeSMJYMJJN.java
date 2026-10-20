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
    private CRServo intakeSL;
    private CRServo intakeSR;




    private final double ForwardPower = 1.0;
    private final double BackwardPower = -1.0;


    private IntakeSMJYMJJN.MotorState currentState = IntakeSMJYMJJN.MotorState.IDLE;

    public void init(HardwareMap hardwareMap){
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE );

        intakeSL = hardwareMap.get(CRServo.class, "l_intake_wheel");
        intakeSR = hardwareMap.get(CRServo.class, "r_intake_wheel");
    }
    public IntakeSMJYMJJN.MotorState getState(){
        return currentState;
    }

    public void setState(IntakeSMJYMJJN.MotorState newState) {
        currentState = newState;
    }

    public void stop(){
        intake.setPower(0);
        intakeSL.setPower(0);
        intakeSR.setPower(0);

        currentState = IntakeSMJYMJJN.MotorState.IDLE;
    }
    public void update(){

        switch (currentState){
            case INTAKE:
                intake.setPower(ForwardPower);
                intakeSL.setPower(BackwardPower);
                intakeSR.setPower(ForwardPower);
                break;
            case OUTTAKE:
                intake.setPower(BackwardPower);
                intakeSL.setPower(ForwardPower);
                intakeSR.setPower(BackwardPower);
                break;
            case IDLE:
                intake.setPower(0);
                intakeSL.setPower(0);
                intakeSR.setPower(0);
                break;
        }
    }
}
