package org.firstinspires.ftc.teamcode.pedro.TestAutos;

import static com.pedropathing.api.Paths.*;
import static com.pedropathing.ivy.Scheduler.schedule;
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


@Autonomous
public class ExampleAuto extends OpMode {
    private Follower follower;
    private final PoseFactory p = PoseFactory.degrees();
        private final Pose startPose = p.of(72, 12, 90);
        private final Pose endLinepose = p.of(72, 72, 270);
        private final Pose CP1 = p.of(72, 24, 90);
        private final Pose endCurvePose = p.of(24, 24, 90);

    private Path goStraight(){
        return line(startPose, endLinepose).linear(startPose, endLinepose);
    }
    private Path curveBack(){
        return curve(endLinepose, CP1, endCurvePose).tangent();
    }

    private Command autoRoutine(){
        return sequential(
                follow(follower, goStraight()),
                //can add actions and subsystems here
                follow(follower, curveBack())
        );
    }

    @Override
    public void init(){
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        follower.update();
    }

    @Override
    public void start(){
        schedule(autoRoutine());
    }

    @Override
    public void loop(){
        follower.update();
        Scheduler.execute();
    }
}
