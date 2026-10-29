package org.firstinspires.ftc.teamcode.Lessons;



import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
@Disabled
@TeleOp
public class ClawChallengeMJ extends OpMode {
    public Servo servo1 = null;
    public Servo servo2 = null;

    @Override
    public void init() {
        servo1 = hardwareMap.get(Servo.class, "servo_1");
        servo2 = hardwareMap.get(Servo.class, "servo_2");

    }

    @Override
    public void loop() {
        if (gamepad1.x) {
            servo1.setPosition(0.85);
            servo2.setPosition(0.14);
        }
        else if (gamepad1.b) {
            servo1.setPosition(0.5);
            servo2.setPosition(0.3);
        }
    }
}