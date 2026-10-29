package org.firstinspires.ftc.teamcode.TeleOp.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class IntakeTeleOpSMJYMJJN extends OpMode {

    private final IntakeSMJYMJJN intake = new IntakeSMJYMJJN();

    @Override
    public void init() {
        intake.init(hardwareMap);
    }

    @Override
    public void loop() {
        if(gamepad2.right_bumper){
            intake.setState(IntakeSMJYMJJN.MotorState.INTAKE);
        }
        else if (gamepad2.left_bumper){
            intake.setState(IntakeSMJYMJJN.MotorState.OUTTAKE);
        }
        else {
            intake.setState(IntakeSMJYMJJN.MotorState.IDLE);
        }


        intake.update();

        telemetry.addData("Current Intake Motor State", intake.getState());
        telemetry.update();
    }

    @Override
    public void stop() {
        intake.stop();
    }
}