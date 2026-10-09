package org.firstinspires.ftc.teamcode.pedroPathing.MainAutos;


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
public class AutoTwoTipPark extends LinearOpMode {

    //////CALLING EXTERNAL CLASSES//////
    public Follower follower;
    public Intake intake;


    //////LAUNCH_AND_GARDEN CHAIN//////
    public PathChain launchAndGarden;
        private final Pose startPose = new Pose(57, 8, Math.toRadians(90));
        private final Pose beforeGardenPose = new Pose(24, 8, Math.toRadians(180));
        private final Pose afterGardenPose = new Pose(8, 8, Math.toRadians(180));

    //////FAR_CELL_AND_LAUNCH CHAIN//////
    public PathChain farCellAndLaunch;
        private final Pose farCellLaunchPose = new Pose(32, 128, Math.toRadians(300));

    //////FLOWER_COLLECT CHAIN//////
    public PathChain flowerCollectAndLaunch;
        private final Pose flowerIntakePose = new Pose(40, 132, Math.toRadians(26.6));

    //////PARK CHAIN//////
    public PathChain park;
        private final Pose parkPose = new Pose(18, 116, Math.toRadians(300));


    public void buildPaths() {
        launchAndGarden = follower.pathBuilder()
                .setGlobalDeceleration()
                .addPath(new BezierCurve(startPose, new Pose(41.5, 12.7) ,beforeGardenPose))
                    .setLinearHeadingInterpolation(startPose.getHeading(), beforeGardenPose.getHeading())

                .addPath(new BezierLine(beforeGardenPose, afterGardenPose))
                    .setConstantHeadingInterpolation(beforeGardenPose.getHeading())
                .build();


        farCellAndLaunch = follower.pathBuilder()
                .addPath(new BezierCurve(afterGardenPose, new Pose(28.7, 53.6), farCellLaunchPose))
                    .setLinearHeadingInterpolation(afterGardenPose.getHeading(), farCellLaunchPose.getHeading())
                .build();


        flowerCollectAndLaunch = follower.pathBuilder()
                .addPath(new BezierLine(farCellLaunchPose, flowerIntakePose))
                    .setTangentHeadingInterpolation()

                .addPath(new BezierLine(flowerIntakePose, farCellLaunchPose))
                    .setLinearHeadingInterpolation(flowerIntakePose.getHeading(), farCellLaunchPose.getHeading())
                .build();


        park = follower.pathBuilder()
                .addPath(new BezierLine(farCellLaunchPose, parkPose))
                    .setConstantHeadingInterpolation(farCellLaunchPose.getHeading())
                .build();


    }


    //////RUN METHOD//////
    @Override
    public void runOpMode(){

        //initialization
        follower = Constants.createFollower(hardwareMap);
        intake = new Intake();
        intake.init(hardwareMap);

        Scheduler.reset();
        buildPaths();
        follower.setStartingPose(startPose);

        //bridge between init and run
        waitForStart();

        //follow all paths
        follower.followPath(launchAndGarden, 0.7, true);
        follower.followPath(farCellAndLaunch, 0.9, true);
        follower.followPath(flowerCollectAndLaunch, 0.4, true);
        follower.followPath(park, 1.0, true);

        //update all info in real time
        while(opModeIsActive() && follower.isBusy()){
            follower.update();
            intake.update();
            Scheduler.execute();
        }
    }



}
