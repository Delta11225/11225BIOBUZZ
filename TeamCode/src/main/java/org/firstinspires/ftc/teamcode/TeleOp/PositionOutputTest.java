package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@TeleOp
public class PositionOutputTest extends OpMode {
    GoBildaPinpointDriver odo;
    double currentAngle;
    @Override
    public void init (){
        odo = hardwareMap.get(GoBildaPinpointDriver.class,"pinpoint");//ending position (15,99,0)
        odo.setOffsets(32.5, -37.5, DistanceUnit.MM);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        odo.resetPosAndIMU();
    }
    public void loop(){
        odo.update(GoBildaPinpointDriver.ReadData.ONLY_UPDATE_HEADING);

        Pose2D pos = odo.getPosition();
        telemetry.addData("Heading", pos.getHeading(AngleUnit.DEGREES));

        currentAngle =pos.getHeading(AngleUnit.DEGREES);

    }
}
