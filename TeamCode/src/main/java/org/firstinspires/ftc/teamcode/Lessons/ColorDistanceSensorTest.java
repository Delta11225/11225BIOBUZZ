package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp
public class ColorDistanceSensorTest extends OpMode {
    ColorSensor sensorColor;
    DistanceSensor sensorDistance;

    @Override
    public void init() {
        sensorColor = hardwareMap.get(ColorSensor.class, "colorV3");
        sensorDistance = hardwareMap.get(DistanceSensor.class, "colorV3");
    }


    @Override
    public void loop() {
    telemetry.addData("Color vals, r", sensorColor.red());
    telemetry.addData("Color vals, g", sensorColor.green());
    telemetry.addData("Color vals, b", sensorColor.blue());
    telemetry.addData("Distance(cm)", sensorDistance.getDistance(DistanceUnit.CM));

    }
}
