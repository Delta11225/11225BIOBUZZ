package org.firstinspires.ftc.teamcode.TeleOp.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp
public class Nectar_launcher_servo_tester extends OpMode {
    public Servo nectar_gate = null;
    @Override
    public void init() {
        nectar_gate = hardwareMap.get(Servo.class, "NectarGate");
    }

    @Override
    public void loop() {
        if (gamepad1.b) {
            nectar_gate.setPosition(0);
        }
        else if (gamepad1.a) {
            nectar_gate.setPosition(0.25);
        }
        else if (gamepad1.x) {
            nectar_gate.setPosition(0.455);
        }
        telemetry.addData("Position", nectar_gate.getPosition());

    }
}
