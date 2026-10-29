package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@Disabled
@TeleOp
public class LogicalOperatorsMJ extends OpMode {
public DcMotor motor1 = null;
    @Override
    public void init() {

        motor1 = hardwareMap.get(DcMotor.class, "motor_1");
    }

    @Override
    public void loop() {
        if (gamepad1.a && gamepad1.b){
            telemetry.addLine("A and B are pressed");
            motor1.setPower(1);
        }
        else if (gamepad1.x && gamepad1.y) {
            telemetry.addLine("X and Y are pressed");
            motor1.setPower(-1);
        }
        else{
            telemetry.addLine("No buttons are pressed");
            motor1.setPower(0);
        }
    }
}