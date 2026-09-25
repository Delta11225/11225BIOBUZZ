package org.firstinspires.ftc.teamcode.pedroPathing.TestAutos;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.Intake;

@Autonomous
public class FirstSubsystemAutoTest extends LinearOpMode {

    public Follower follower;
    public Intake intake;


    //////ALL POSES FOR ALL PATHS//////
    private final Pose startPose = new Pose(72, 24, Math.toRadians(90));
    private final Pose endPose = new Pose(72, 128, Math.toRadians(90));



    //////ALL PATHS///////
    public PathChain chain1;// Define chains of paths that are all grouped together under chains
    // Hierarchy: chain -> path -> Bézier curve -> pose
    public void buildPaths() {
        chain1 = follower.pathBuilder()
                .setGlobalDeceleration() //sets deceleration/braking globally so that the entire chain is treated as one continuous path, makes transitions smooth!

                .addPath(new BezierLine(startPose, endPose))
                .setConstantHeadingInterpolation(startPose.getHeading())//uses whatever heading is specified in the endPose pose
                .addParametricCallback(0.2, intake.intakeSeconds(1.5))
                .build();

    }

    ////////RUN METHOD////////
    @Override
    public void runOpMode(){
        follower = Constants.createFollower(hardwareMap);
        intake = new Intake();
        Scheduler.reset();
        buildPaths();
        follower.setStartingPose(startPose);

        waitForStart();//Bridge between INIT and RUN

        follower.followPath(chain1, 0.4, true); ///max power is 80in/sec, set a max from 0-1 -> holdEnd is only used for the LAST PATH IN A SEQUENCE
        while (opModeIsActive() && follower.isBusy()){
            follower.update();
            Scheduler.execute();
        }

    }
}

