package org.firstinspires.ftc.teamcode.Competition.Auto;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.TeleOp.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Launcher;


@Autonomous
public class BerthaOneSideWAIT extends OpMode {
    private Follower follower;
    private Intake intake;
    private Launcher launcher;
    private final PoseFactory p = PoseFactory.degrees();
        private final Pose startPose = p.of(58, 136, 90);
        private final Pose CP1 = p.of(62.14, 126.29,90);
        private final Pose flowerIntakePose = p.of(48.5, 129, 125);


    private Path toFlower(){
        return curve(startPose, CP1, flowerIntakePose).linear(startPose, flowerIntakePose)
                .with(
                    Constants.foresightConfig.maxPathSpeed.at(0.4)
                );
    }

    private Command intakeFlowerPollen(){
        return sequential(
            follow(follower, toFlower()),
            intake.intakeSeconds(5)
        );
    }

    private Command autoRoutine(){
        return sequential(
                //WAIT HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//////////////////
            launcher.launchSeconds(4, 2450)

        );
    }

    @Override
    public void init(){
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        intake = new Intake(hardwareMap);
        launcher = new Launcher(hardwareMap);

        follower.setPose(startPose);
        follower.update();
    }

    @Override
    public void start(){
        schedule(
            autoRoutine()
        );
    }

    @Override
    public void loop(){
        follower.update();
        Scheduler.execute();
    }

    @Override
    public void stop() {

        //Save the robot's final position when Auto ends
        OpModeStorage.autonomousEndPose = follower.pose();

        //Indicate that a valid Auto position was saved
        OpModeStorage.autoPoseSaved = true;

    }
}
