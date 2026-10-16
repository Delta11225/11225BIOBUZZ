package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.Intake;

@TeleOp
public class ExampleTeleOPSMSOPHIA extends OpMode {
    private final ExampleSMSOPHIA motor = new ExampleSMSOPHIA();
    private final IntakeSMSOPHIA intake = new IntakeSMSOPHIA();
    private final ClawSMSOPHIA claw = new ClawSMSOPHIA();
    @Override
    public void init() {
        motor.init(hardwareMap);
        intake.init(hardwareMap);
        claw.init(hardwareMap);
    }

    @Override
    public void loop() {
        if(gamepad1.right_bumper){
            motor.setState(ExampleSMSOPHIA.MotorState.FORWARD);
        }
        else if(gamepad1.left_bumper){
            motor.setState(ExampleSMSOPHIA.MotorState.BACKWARD);
        }
        else{
            motor.setState(ExampleSMSOPHIA.MotorState.IDLE);
        }
        if(gamepad1.a){
            intake.setState(IntakeSMSOPHIA.MotorState.INTAKE);
        }
        else if(gamepad1.b){
            intake.setState(IntakeSMSOPHIA.MotorState.OUTTAKE);
        }
        else{
            intake.setState(IntakeSMSOPHIA.MotorState.IDLE);
        }
        if(gamepad1.x){
            claw.setState(ClawSMSOPHIA.MotorState.OPEN);
        }
        else if(gamepad1.y){
            claw.setState(ClawSMSOPHIA.MotorState.CLOSED);
        }
        else{
            claw.setState(ClawSMSOPHIA.MotorState.CLOSED);
        }


        motor.update();
        intake.update();
        claw.update();
        //0.4,0.45
        //0.8,0.05
        telemetry.addData("Current Motor State", motor.getState());
        telemetry.addData("Current Intake State", intake.getState());
        telemetry.addData("Current Intake State", claw.getState());
        telemetry.update();
    }
    @Override
    public void stop(){
        motor.stop();
        intake.stop();
        claw.stop();
    }
}

