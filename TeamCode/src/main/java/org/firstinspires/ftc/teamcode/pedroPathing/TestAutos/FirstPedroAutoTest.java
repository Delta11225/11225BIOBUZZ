package org.firstinspires.ftc.teamcode.pedroPathing.TestAutos;

import com.pedropathing.drivetrain.Drivetrain;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.sql.Driver;

@Autonomous
public class FirstPedroAutoTest extends LinearOpMode {

    public Follower follower;


    //////ALL POSES FOR ALL PATHS//////
    private final Pose startPose = new Pose(76, 76, Math.toRadians(180));
    private final Pose midCurvePose = new Pose(48, 96, Math.toRadians(90));
    private final Pose endchain1Pose = new Pose(48, 120, Math.toRadians(90));
    private final Pose endLinePose = new Pose(72, 120, Math.toRadians(270));
    private final Pose halfwayCurvePose = new Pose(96, 96, Math.toRadians(0));
    private final Pose endPose = new Pose(120, 72, Math.toRadians(270));



    //////ALL PATHS///////
    public PathChain chain1;// Define chains of paths that are all grouped together under chains
    // Hierarchy: chain -> path -> Bézier curve -> pose
    public PathChain chain2;
    public void buildPaths() {
        chain1 = follower.pathBuilder()
                .setGlobalDeceleration() //sets deceleration/braking globally so that the entire chain is treated as one continuous path, makes transitions smooth!
                .addPath(new BezierCurve(startPose, new Pose(48, 72), midCurvePose))
                .setTangentHeadingInterpolation()

                .addPath(new BezierLine(midCurvePose, endchain1Pose))
                .setConstantHeadingInterpolation(endchain1Pose.getHeading())//uses whatever heading is specified in the endPose pose
                .build();

        chain2 = follower.pathBuilder()
                .setGlobalDeceleration()
                .addPath(new BezierLine(endchain1Pose, endLinePose))
                .setLinearHeadingInterpolation(endchain1Pose.getHeading(), endLinePose.getHeading())

                .addPath(new BezierCurve(endLinePose, new Pose(72, 96), halfwayCurvePose))
                .setTangentHeadingInterpolation()

                .addPath(new BezierCurve(halfwayCurvePose, new Pose(120, 96), endPose))
                .setTangentHeadingInterpolation()
                .build();
    }

    ////////RUN METHOD////////
    @Override
    public void runOpMode(){
        follower = Constants.createFollower(hardwareMap);
        Scheduler.reset();
        buildPaths();
        follower.setStartingPose(startPose);

        waitForStart();//Bridge between INIT and RUN


        follower.followPath(chain1, 0.5, false); ///max power is 80in/sec, set a max from 0-1 -> holdEnd is only used for the LAST PATH IN A SEQUENCE
        while (opModeIsActive() && follower.isBusy()){
            follower.update();
            Scheduler.execute();
        }

        follower.followPath(chain2, 0.7, true);
        while (opModeIsActive() && follower.isBusy()){
            follower.update();
            Scheduler.execute();
        }
    }
}
