package org.firstinspires.ftc.teamcode.Competition.Auto;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.race;
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
import org.firstinspires.ftc.teamcode.pedro.Subsystems.FlowerArm;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Launcher;


@Autonomous(preselectTeleOp = "BerthaTeleOpOneDriver_LL_BLUE")
public class BerthaBacksideTipDelayBLUE extends OpMode {
    private Follower follower;
    private Intake intake;
    private Launcher launcher;
    private FlowerArm flowerArm;

    ////////////////////////POSES////////////////////////
    private final PoseFactory p = PoseFactory.degrees();
        private final Pose startPose = p.of(58, 136, 90);
        private final Pose firstLaunchJiggleForwardPose = p.of(58, 134, 90);
        private final Pose firstLaunchJiggleBackwardPose = p.of(58, 135, 90);
        private final Pose giveSpacePose = p.of(58, 124, 90);
        private final Pose CP1 = p.of(58, 116,90);
        private final Pose preFlowerPose = p.of(47, 118, 90);
        private final Pose intakeFlowerPose = p.of(47, 130.8, 90);
        private final Pose backOutOfFlowerPose = p.of(47, 117, 90);
        private final Pose jiggleFlowerPose = p.of(47, 121, 90);
        private final Pose oppositeLaunchPose = p.of(34, 125, 125);
        private final Pose jiggleOppositelaunchPose = p.of(35, 124, 125);
        private final Pose parkPose = p.of(14, 125, 0);

    ////////////////////////PATHS////////////////////////
    private Path jiggleFirstLaunch(){
        Path jiggleLaunchForward1 = Paths.line(startPose, firstLaunchJiggleForwardPose).constant(startPose);
        Path jiggleLaunchBackward1 = Paths.line(firstLaunchJiggleForwardPose, firstLaunchJiggleBackwardPose).constant(startPose);
        return Paths.path(jiggleLaunchForward1, jiggleLaunchBackward1)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toFlower(){
        Path toGiveSpace = Paths.line(startPose, giveSpacePose).constant(startPose);
        Path toPreFlower = Paths.curve(giveSpacePose, CP1, preFlowerPose).constant(giveSpacePose);
        return Paths.path(toGiveSpace, toPreFlower)
                .with(
                    Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toIntakeFlower(){
        return line(preFlowerPose, intakeFlowerPose).constant(preFlowerPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(0.45)
                );
    }
    private Path toBackOutOfFlower(){
        return line(intakeFlowerPose, backOutOfFlowerPose).constant(intakeFlowerPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path jiggleFlower(){
        Path jiggleForward1 = Paths.line(backOutOfFlowerPose, jiggleFlowerPose).constant(backOutOfFlowerPose);
        Path jiggleBackward1 = Paths.line(jiggleFlowerPose, backOutOfFlowerPose).constant(backOutOfFlowerPose);
        return Paths.path(jiggleForward1, jiggleBackward1)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toSecondLaunch(){
        return line(backOutOfFlowerPose, oppositeLaunchPose).linear(backOutOfFlowerPose, oppositeLaunchPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path jiggleLaunchOppositeSide(){
        Path jiggleLaunchForward2 = Paths.line(oppositeLaunchPose, jiggleOppositelaunchPose).constant(oppositeLaunchPose);
        Path jiggleLaunchBackward2 = Paths.line(jiggleOppositelaunchPose, oppositeLaunchPose).constant(oppositeLaunchPose);
        return Paths.path(jiggleLaunchForward2, jiggleLaunchBackward2)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toPark(){
        return line(oppositeLaunchPose, parkPose).linear(oppositeLaunchPose, parkPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }

    ////////////////////////COMMANDS////////////////////////
    private Command firstLaunch(){
        return parallel(
            launcher.launchSeconds(3, 2450),
            sequential(
                waitMs(2000),
                follow(follower, jiggleFirstLaunch())
            )
        );
    }
    private Command goToFlower(){
        return parallel(
            follow(follower, toFlower()),
            sequential(
                waitUntil(()-> follower.pathIndex() >= 1),
                flowerArm.flowerArmDown()
            )
        );
    }
    private Command intakeFlower(){
        return parallel(
            race(
                follow(follower, toIntakeFlower()),
                waitMs(2000)
            ),
            intake.intakeSeconds(2)
        );
    }
    private Command backOutOfFlower(){
        return sequential(
            follow(follower, toBackOutOfFlower()),
            parallel(
                follow(follower, jiggleFlower()),
                intake.intakeSeconds(1)
            ),
            flowerArm.flowerArmUp()
        );
    }
    private Command secondLaunch(){
        return sequential(
            follow(follower, toSecondLaunch()),
            parallel(
                launcher.launchSeconds(3, 2470),
                sequential(
                    waitMs(2000),
                    follow(follower, jiggleLaunchOppositeSide())
                )
            )
        );
    }
    private Command park(){
        return sequential(
            follow(follower, toPark())
        );
    }

    private Command autoRoutine(){
        return sequential(
            waitMs(14000),
            firstLaunch(),
            goToFlower(),
            intakeFlower(),
            backOutOfFlower(),
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
        flowerArm = new FlowerArm(hardwareMap);

        follower.setPose(startPose);
        follower.update();

        flowerArm.initFlower();
    }

    @Override
    public void start(){
        schedule(
            autoRoutine()
        );
    }

    @Override
    public void loop() {
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
