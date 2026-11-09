package org.firstinspires.ftc.teamcode.Competition.Auto;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
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

import org.firstinspires.ftc.teamcode.TeleOp.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Launcher;


@Autonomous(preselectTeleOp = "BerthaTeleOpOneDriver")
public class BerthaOneSideWAIT extends OpMode {
    private Follower follower;
    private Intake intake;
    private Launcher launcher;
    private final PoseFactory p = PoseFactory.degrees();
        private final Pose startPose = p.of(58, 136, 90);
        private final Pose CP1 = p.of(62.14, 126.29,90);
        private final Pose flowerDropPose = p.of(47.5, 132, 125);
        private final Pose CP2 = p.of(50.83, 115.74,90);
        private final Pose readyIntakePose = p.of(33, 135, 180);
        private final Pose finalIntakePose = p.of(9.4, 137.5, 180);
        private final Pose angledLaunchPose = p.of(34, 125, 125);
        private final Pose parkPose = p.of(15, 99, 0);
        private final Pose CP3 = p.of(46, 102, 0);



    private Path toFlower(){
        return curve(startPose, CP1, flowerDropPose).linear(startPose, flowerDropPose)
                .with(
                    Constants.foresightConfig.maxPathSpeed.at(0.4)
                );
    }
    private Path toReadyIntake(){
        return curve(flowerDropPose, CP2, readyIntakePose).linear(flowerDropPose, readyIntakePose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toIntakeFlowerPollen(){
        return line(readyIntakePose, finalIntakePose).constant(readyIntakePose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(0.5)
                );
    }
    private Path toSecondLaunch(){
        return line(finalIntakePose, angledLaunchPose).linear(finalIntakePose, angledLaunchPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toPark(){
        return curve(angledLaunchPose, CP3, parkPose).linear(angledLaunchPose, parkPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }

    private Command dropFlowerPollen(){
        return sequential(
            follow(follower, toFlower()),
            intake.intakeSeconds(2)
        );
    }
    private Command readyIntake(){
        return sequential(
                follow(follower, toReadyIntake())
        );
    }
    private Command intakeFlowerPollen(){
        return parallel(
                follow(follower, toIntakeFlowerPollen()),
                intake.intakeSeconds(3)
        );
    }
    private Command secondLaunch(){
        return sequential(
                follow(follower, toSecondLaunch()),
                launcher.launchSeconds(4, 2480)
        );
    }
    private Command park(){
        return sequential(
                follow(follower, toPark())
        );
    }


    private Command autoRoutine(){
        return sequential(
            launcher.launchSeconds(4, 2450),
            dropFlowerPollen(),
            readyIntake(),
            intakeFlowerPollen(),
            secondLaunch(),
            park()


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
