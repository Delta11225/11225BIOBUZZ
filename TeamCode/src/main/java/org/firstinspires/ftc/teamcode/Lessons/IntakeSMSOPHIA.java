package org.firstinspires.ftc.teamcode.Lessons;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeSMSOPHIA {
    public enum MotorState{
        IDLE,
        OUTTAKE,
        INTAKE
    }
    private DcMotor intakeMotor;
    private final double intakePower = 1.0;
    private final double outtakePower = -1.0;
    private IntakeSMSOPHIA.MotorState currentState = IntakeSMSOPHIA.MotorState.IDLE;
    public void init(HardwareMap hardwareMap){
        intakeMotor = hardwareMap.get(DcMotor.class,"motor_1");

        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public IntakeSMSOPHIA.MotorState getState(){return currentState;}

    public void setState(IntakeSMSOPHIA.MotorState newState){currentState=newState;}

    public void stop(){

        intakeMotor.setPower(0);

        currentState= IntakeSMSOPHIA.MotorState.IDLE;
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
