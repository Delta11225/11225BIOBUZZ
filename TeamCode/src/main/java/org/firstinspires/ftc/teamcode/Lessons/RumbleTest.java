package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
@Disabled
@TeleOp
public class RumbleTest extends OpMode {

    ColorSensor sensorColor;

    DistanceSensor sensorDistance;
    private ElapsedTime matchtime = new ElapsedTime();


    @Override
    public void init() {
        sensorColor = hardwareMap.get(ColorSensor.class, "colorV3");
        sensorDistance = hardwareMap.get(DistanceSensor.class, "colorV3");
    }

    @Override
    public void start() {
        matchtime.reset();
    }

    @Override
    public void loop() {
        if (sensorDistance.getDistance(DistanceUnit.CM) < 3){
            gamepad1.rumble(100);
        }
        else {
            gamepad1.rumble(0);
        }
    }
}
