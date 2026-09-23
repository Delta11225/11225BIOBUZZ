package org.firstinspires.ftc.teamcode.pedroPathing.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Intake {

    private DcMotor intake;
    public ElapsedTime stateTimer = new ElapsedTime();
    public ElapsedTime intakeTime = new ElapsedTime();

    private enum IntakeState{
        IDLE,
        INTAKING,
        OUTTAKING
    }

    private IntakeState intakeState;

    private boolean intaking = false;
    private boolean outtaking = false;

    public void init(HardwareMap hardwareMap){
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setDirection(DcMotor.Direction.REVERSE);

        intakeState = IntakeState.IDLE;
        intake.setPower(0);
    }

    public void update(){
        switch(intakeState){
            case IDLE:
                if(intaking){
                    intakeTime.reset();
                    intakeState = IntakeState.INTAKING;

                } else if (outtaking) {
                    intakeTime.reset();
                    intakeState = IntakeState.OUTTAKING;
                }
                break;

            case INTAKING:
                intake.setPower(1);
                //if(intakeTime > )
        }
    }
    public void intakeSeconds(double intakeDuration){
        if(intakeState == IntakeState.IDLE){
            //intakeTime = intakeDuration;
        }
    }
}


