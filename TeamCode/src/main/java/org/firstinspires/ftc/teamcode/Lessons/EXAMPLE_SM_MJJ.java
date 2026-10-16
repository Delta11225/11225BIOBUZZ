package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class EXAMPLE_SM_MJJ {

    public enum MotorState{
        IDLE,
        FORWARD,
        BACKWARD
    }
    private DcMotor motor1;
    private final double ForwardPower = 1.0;
    private final double BackwardPower = -1.0;
    private MotorState currentState = MotorState.IDLE;

    public void init(HardwareMap hardwareMap){
        motor1 = hardwareMap.get(DcMotor.class, "motor_1");
        motor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE );
    }
    public MotorState getState(){
        return currentState;
    }

    public void setState(EXAMPLE_SM_MJJ.MotorState newState) {
        currentState = newState;
    }

    public void stop(){
        motor1.setPower(0);
        currentState = MotorState.IDLE;
    }
    public void update(){

        switch (currentState){
            case FORWARD:
                motor1.setPower(ForwardPower);
                break;
            case BACKWARD:
                motor1.setPower(BackwardPower);
                break;
            case IDLE:
                motor1.setPower(0);
                break;
        }
    }
}//Continue at Step 6 Part 2 Lesson 12
