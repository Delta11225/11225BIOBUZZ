package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp
public class ColorDistanceSensorTest extends OpMode {
    ColorSensor sensorColor;
    DistanceSensor sensorDistance;
    public Servo servo1 = null;
    public Servo servo2 = null;

    @Override
    public void init() {
        sensorColor = hardwareMap.get(ColorSensor.class, "colorV3");
        sensorDistance = hardwareMap.get(DistanceSensor.class, "colorV3");
        servo1 = hardwareMap.get(Servo.class, "servo_1");
        servo2 = hardwareMap.get(Servo.class, "servo_2");
    }


    @Override
    public void loop() {
        telemetry.addData("Color vals, r", sensorColor.red());
        telemetry.addData("Color vals, g", sensorColor.green());
        telemetry.addData("Color vals, b", sensorColor.blue());
        telemetry.addData("Distance(cm)", sensorDistance.getDistance(DistanceUnit.CM));

        if (sensorDistance.getDistance(DistanceUnit.CM) > 5) {
            servo1.setPosition(0.5);
        servo2.setPosition(0); }
        else {

            if (sensorColor.red() > 2000) {
                servo1.setPosition(0);
                servo2.setPosition(0);
            } else if (sensorColor.blue() > 2000) {
                servo1.setPosition(0);
                servo2.setPosition(0);
            } else {
                servo1.setPosition(0.825);
                servo2.setPosition(0.825);
            }
        }
    }

}