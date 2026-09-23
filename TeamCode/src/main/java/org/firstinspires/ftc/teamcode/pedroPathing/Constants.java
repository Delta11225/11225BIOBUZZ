package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PredictiveBrakingCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;



import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(7.711)
            .centripetalScaling(0)
            .useSecondaryHeadingPIDF(true)
            .headingPIDFCoefficients(new PIDFCoefficients(0.55, 0, 0.05, 0.1))
            .secondaryHeadingPIDFCoefficients(new PIDFCoefficients(1, 0, 0.05, 0.02))
            .predictiveBrakingCoefficients(new PredictiveBrakingCoefficients(0.04, 0.11629, 0.00053889))
            ;


    public static PathConstraints pathConstraints = new PathConstraints(
            0.99, 100, 1, 1);

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .xVelocity(61.68600)
            .yVelocity(48.90537)
            .rightFrontMotorName("rf") //CONFIG
            .rightRearMotorName("rr") //CONFIG
            .leftRearMotorName("lr") //CONFIG
            .leftFrontMotorName("lf") //CONFIG
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE) //true for Ms Pac-Man
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE) //true for Ms Pac-Man
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            ;

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(3.25)
            .strafePodX(0.5)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint") //CONFIG
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            ;

    //builds all the variables and values tuned above
    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localizerConstants)
                .build();
    }
}
