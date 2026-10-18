package org.firstinspires.ftc.teamcode.TeleOp.StateMachines;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class BerthaIntakeSMV2 {
    public enum MotorState{
        IDLE,
        OUTTAKE,
        INTAKE
    }
    private DcMotor intakeMotor;
    private final double intakePower = 1.0;
    private final double outtakePower = -1.0;
    private BerthaIntakeSMV2.MotorState currentState = BerthaIntakeSMV2.MotorState.IDLE;
    public void init(HardwareMap hardwareMap){
        intakeMotor = hardwareMap.get(DcMotor.class,"intake");

        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public BerthaIntakeSMV2.MotorState getState(){return currentState;}

    public void setState(BerthaIntakeSMV2.MotorState newState){currentState=newState;}

    public void stop(){

        intakeMotor.setPower(0);

        currentState= BerthaIntakeSMV2.MotorState.IDLE;
    }

    public void update(){
        switch (currentState){
            case INTAKE:
                intakeMotor.setPower(intakePower);
                break;
            case OUTTAKE:
                intakeMotor.setPower(outtakePower);
                break;
            case IDLE:
                intakeMotor.setPower(0);
                break;
        }
    }

}
