package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ServoTestMJ extends OpMode {
    public Servo servo1 = null;
    @Override
    public void init() {
        servo1 = hardwareMap.get(Servo.class, "servo_1");
    }
    @Override
    public void loop() {
        if (gamepad1.x){
            servo1.setPosition(0);
            telemetry.addLine("0 degrees");
        }
        else if(gamepad1.y) {
            servo1.setPosition(0.5);
            telemetry.addLine("90 degrees");
        }
        else if(gamepad1.b){
            servo1.setPosition(1);
            telemetry.addLine("180 degrees");
}
    }
}