package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp
public class SensorTouchTestMJ extends OpMode {
    TouchSensor touch;
    public DcMotor motor1;

    @Override
    public void init() {
        touch = hardwareMap.get(TouchSensor.class, "touch");
        motor1 = hardwareMap.get(DcMotor.class, "motor_1");
    }

    @Override
    public void loop() {
        if (touch.isPressed()){
            motor1.setPower(0.75);
            telemetry.addLine("Programming is fun!");
        } else {
            motor1.setPower(0);
            telemetry.addLine("Programming is fun!");
        }
    }
}