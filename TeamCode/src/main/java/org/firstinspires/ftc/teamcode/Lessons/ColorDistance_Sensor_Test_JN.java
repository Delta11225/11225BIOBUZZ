package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp
public class ColorDistance_Sensor_Test_JN extends OpMode {
    ColorSensor sensorColor;
    DistanceSensor sensorDistance;
    @Override
    public void init(){
        sensorColor = hardwareMap.get(ColorSensor.class, "colorV3");
        sensorDistance = hardwareMap.get(DistanceSensor.class,"colorV3");
    }
    @Override
    public void loop() {
        telemetry.addData("Color Values, r", sensorColor.red());
        telemetry.addData("Color values, b", sensorColor.blue());
        telemetry.addData("Color values, g", sensorColor.green());
        telemetry.addData("Distance(cm)", sensorDistance.getDistance(DistanceUnit.CM));
        if (sensorColor.red() >= 500 && sensorDistance.getDistance(DistanceUnit.CM) <= 5) {
            telemetry.addLine("Color: Red");
        } else if (sensorColor.blue() >= 500 && sensorDistance.getDistance(DistanceUnit.CM) <= 5) {
            telemetry.addLine("COLOR: Blue");
        } else if (sensorColor.red() <= 500 && sensorColor.blue() <= 500 && sensorDistance.getDistance(DistanceUnit.CM) <= 5) {
            telemetry.addLine("OBJECT DETECTED - Wrong Color");
        } else if (sensorColor.red()<= 500 && sensorColor.blue() <= 500 && sensorDistance.getDistance(DistanceUnit.CM) >=5) {
            telemetry.addLine("NO OBJECT DETECTED");
        }
    }
}
