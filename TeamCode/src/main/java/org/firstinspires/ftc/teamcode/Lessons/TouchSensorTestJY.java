package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
@Disabled
@TeleOp
public class TouchSensorTestJY extends OpMode {

    TouchSensor touch;
    public DcMotor motor1 = null;

    @Override
    public void init() {
        touch = hardwareMap.get(TouchSensor.class, "touch");
        motor1 = hardwareMap.get(DcMotor.class, "motor_1");
    }



    @Override
    public void loop() {
        telemetry.addLine("Programming is Fun!");
        if (touch.isPressed()) {
            motor1.setPower(0.75);
        } else {
            motor1.setPower(0);
        }
    }
}
