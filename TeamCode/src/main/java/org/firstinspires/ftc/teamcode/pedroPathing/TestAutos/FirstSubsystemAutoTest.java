package org.firstinspires.ftc.teamcode.pedroPathing.TestAutos;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.Launcher;

@Autonomous
public class FirstSubsystemAutoTest extends LinearOpMode {

    public Follower follower;
    public Intake intake;
    public Launcher launcher;


    //////ALL POSES FOR ALL PATHS//////
    private final Pose startPose1 = new Pose(48, 12, Math.toRadians(90));
    private final Pose endPose1 = new Pose(48, 96, Math.toRadians(90));
    private final Pose startPose2 = new Pose(96, 96, Math.toRadians(270));
    private final Pose endPose2 = new Pose(96, 36, Math.toRadians(270));



    //////ALL PATHS///////
    public PathChain chain1;


    // Define chains of paths that are all grouped together under chains
    // Hierarchy: chain -> path -> Bézier curve -> pose
    public void buildPaths() {
        chain1 = follower.pathBuilder()
                //.setGlobalDeceleration() //sets deceleration/braking globally so that the entire chain is treated as one continuous path, makes transitions smooth!
                .addPath(new BezierLine(startPose1, endPose1))
                .setConstantHeadingInterpolation(startPose1.getHeading())
                .addParametricCallback(0.4, intake.intakeSeconds(1))

                .addPath(new BezierLine(endPose1, startPose2))
                .setLinearHeadingInterpolation(endPose1.getHeading(), startPose2.getHeading())

                .addPath(new BezierLine(startPose2, endPose2))
                .setConstantHeadingInterpolation(startPose2.getHeading())
                .addParametricCallback(0.4, intake.outakeSeconds(0.7))

                .build();
    }

    ////////RUN METHOD////////
    @Override
    public void runOpMode(){

        follower = Constants.createFollower(hardwareMap);
        intake = new Intake();
        intake.init(hardwareMap);
        launcher = new Launcher();
        launcher.init(hardwareMap);

        Scheduler.reset();
        buildPaths();
        follower.setStartingPose(startPose1);

        waitForStart();//Bridge between INIT and RUN

        follower.followPath(chain1, 0.4, true); ///max power is 80in/sec, set a max from 0-1 -> holdEnd is only used for the LAST PATH IN A SEQUENCE
        while (opModeIsActive() && follower.isBusy()){
            follower.update();
            intake.update();
            launcher.update();
            Scheduler.execute();
        }

    }
}

