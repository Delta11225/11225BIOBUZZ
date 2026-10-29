package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
@Disabled
@TeleOp
public class Rumble_Test_JN extends OpMode {
    DistanceSensor sensorDistance;

    @Override
    public void init() {
        sensorDistance = hardwareMap.get(DistanceSensor.class, "distanceV3");
    }

    @Override
    public void loop() {
        if (sensorDistance.getDistance(DistanceUnit.CM) <= 3){
            gamepad1.rumble(100);
        }
        else if (sensorDistance.getDistance(DistanceUnit.CM) > 3){
            gamepad1.rumble(0);
        }
    }
}
