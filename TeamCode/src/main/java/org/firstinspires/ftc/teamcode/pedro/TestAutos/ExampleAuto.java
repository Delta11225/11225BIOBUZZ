package org.firstinspires.ftc.teamcode.pedro.TestAutos;

import static com.pedropathing.api.Paths.*;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Launcher;


@Autonomous
public class ExampleAuto extends OpMode {
    //import external classes
    private Follower follower;
    private Intake intake;
    private Launcher launcher;
    private final PoseFactory p = PoseFactory.degrees(); //set all poses
        private final Pose startPose = p.of(72, 12, 90);
        private final Pose endLinepose = p.of(72, 72, 270);
        private final Pose CP1 = p.of(72, 24, 90);
        private final Pose endCurvePose = p.of(24, 24, 90);


    //define paths (ONLY drivetrain movement!!)
    private Path goStraight(){
        return line(startPose, endLinepose).linear(startPose, endLinepose)
                .with(
                    Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path curveBack(){
        return curve(endLinepose, CP1, endCurvePose).tangent()
                .with(
                    Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }

    //define commands (movement AND subsystem action)
    private Command forward(){
        return sequential(
            follow(follower, goStraight())
        );
    }
    private Command curveAndIntake(){
        return parallel(
            follow(follower, curveBack()),
            intake.intakeSeconds(4)
        );
    }


    //condense everything together into one singular executable command
    private Command autoRoutine(){
        return sequential(
            launcher.launchSeconds(3, 2520),
            forward(),
            curveAndIntake()
        );
    }

    @Override
    public void init(){
        Scheduler.reset(); //reset any previously scheduled commands on init

        //init all hardware for drivetrain + subsystems
        follower = Constants.create(hardwareMap);
        intake = new Intake(hardwareMap);
        launcher = new Launcher(hardwareMap);

        //set the start pose and update the follower to include that start pose
        follower.setPose(startPose);
        follower.update();
    }


    //schedule any commands you want to run in your auto
    @Override
    public void start(){
        schedule(
            autoRoutine()
        );
    }

    //continuously update the follower and execute your commands as the auto runs
    @Override
    public void loop(){
        follower.update();
        Scheduler.execute();
    }
}
