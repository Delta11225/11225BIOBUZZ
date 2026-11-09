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


@Autonomous(preselectTeleOp = "BerthaTeleOpOneDriver")
public class BerthaTwoSide extends OpMode {
    private Follower follower;
    private Intake intake;
    private Launcher launcher;
    private final PoseFactory p = PoseFactory.degrees();
        private final Pose startPose = p.of(55, 8, 270);
        private final Pose CP1 = p.of(40, 18, 270);
        private final Pose collectGardenPose = p.of(26, 10, 200);
        private final Pose midGardenPose = p.of(15, 10, 200);
        private final Pose endGardenPose = p.of(11, 10, 180);
        private final Pose CP2 = p.of(36, 80, 180);
        private final Pose oppositelaunchPose = p.of(34, 125, 120);
        private final Pose CP3 = p.of(76, 113, 120);
        private final Pose flowerDropPose = p.of(50.4, 124.3, 125);
        private final Pose CP4 = p.of(41, 117,90);
        private final Pose readyIntakePose = p.of(33, 132, 180);
        private final Pose finalIntakePose = p.of(12, 133, 180);
        private final Pose parkPose = p.of(15, 99, 0);
        private final Pose CP5 = p.of(46, 102, 0);

    private Path toGarden(){
        return curve(startPose, CP1, collectGardenPose).linear(startPose, collectGardenPose)
                .with(
                    Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }

    private Path garden(){
        Path inGarden = line(collectGardenPose, midGardenPose).constant(midGardenPose)
            .with(
                Constants.foresightConfig.maxPathSpeed.at(0.33)
            );

        Path squareUp = line(midGardenPose, endGardenPose).linear(midGardenPose, endGardenPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(0.4)
                );

        return Paths.path(inGarden, squareUp);
    }
    private Path toOppositeSide(){
        return curve(endGardenPose, CP2,oppositelaunchPose).linear(endGardenPose, oppositelaunchPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toFlower(){
        return curve(oppositelaunchPose, CP3, flowerDropPose).linear(oppositelaunchPose, flowerDropPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(0.45)
                );
    }
    private Path toReadyIntake(){
        return curve(flowerDropPose, CP4, readyIntakePose).linear(flowerDropPose, readyIntakePose)
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
    private Path toThirdLaunch(){
        return line(finalIntakePose, oppositelaunchPose).linear(finalIntakePose, oppositelaunchPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toPark(){
        return curve(oppositelaunchPose, CP5, parkPose).linear(oppositelaunchPose, parkPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Command goToGarden(){
        return sequential(
            follow(follower, toGarden())
        );
    }
    private Command gardenIntake(){
        return parallel(
                follow(follower, garden()),
                intake.intakeSeconds(4)
        );
    }
    private Command goToOppositeSideandLaunch(){
        return sequential(
                follow(follower, toOppositeSide()),
                launcher.launchSeconds(4, 2480)
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
    private Command thirdLaunch(){
        return sequential(
                follow(follower, toThirdLaunch()),
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
            goToGarden(),
            gardenIntake(),
            goToOppositeSideandLaunch(),
            dropFlowerPollen(),
            readyIntake(),
            intakeFlowerPollen(),
            thirdLaunch(),
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
        //@Pierson - added to help inherit start pose for teleop
        //@Pierson - make sure these lines are at the end of every auto

        //Save the robot's final position when Auto ends
        OpModeStorage.autonomousEndPose = follower.pose();

        //Indicate that a valid Auto position was saved
        OpModeStorage.autoPoseSaved = true;

    }
}
