package org.firstinspires.ftc.teamcode.Competition.Auto;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.waitMs;
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


@Autonomous(preselectTeleOp = "BerthaTeleOpOneDriver")
public class BerthaSoloTwoTip extends OpMode {
    private Follower follower;
    private Intake intake;
    private Launcher launcher;
    private FlowerArm flowerArm;
    private final PoseFactory p = PoseFactory.degrees();
        private final Pose startPose = p.of(55, 8, 270);
        private final Pose preFlower1Pose = p.of(24, 45, 180);
        private final Pose intakeFlower1Pose = p.of(15.9, 45, 180);
        private final Pose backOutofFlower1Pose = p.of(30, 45, 180);
        private final Pose jiggleFlower1Pose = p.of(26, 45, 180);
        private final Pose CP1 = p.of(40, 72, 120);
        private final Pose oppositelaunchPose = p.of(34, 125, 120);
        private final Pose CP2 = p.of(33, 103,90);
        private final Pose preFlower2Pose = p.of(48, 120, 90);
        private final Pose intakeFlower2Pose = p.of(48, 123, 90);
        private final Pose backOutOfFlower2Pose = p.of(48, 115, 90);
        private final Pose jiggleFlower2Pose = p.of(48, 119, 90);
        private final Pose parkPose = p.of(8, 95, 0);
        private final Pose CP3 = p.of(37, 14, 0);

    private Path toFlower1(){
        return line(startPose, preFlower1Pose).linear(startPose, preFlower1Pose)
                .with(
                    Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toIntakeFlower1(){
        return line(preFlower1Pose, intakeFlower1Pose).constant(preFlower1Pose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(0.45)
                );
    }
    private Path toBackOutOfFlower1(){
        return line(intakeFlower1Pose, backOutofFlower1Pose).constant(intakeFlower1Pose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path jiggleFlower1(){
        Path jiggleForward1 = Paths.line(backOutofFlower1Pose, jiggleFlower1Pose).constant(backOutofFlower1Pose);
        Path jiggleBackward1 = Paths.line(jiggleFlower1Pose, backOutofFlower1Pose).constant(backOutofFlower1Pose);
        return Paths.path(jiggleForward1, jiggleBackward1)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }

    private Path toOppositeSide(){
        return curve(backOutofFlower1Pose, CP1, oppositelaunchPose).linear(backOutofFlower1Pose, oppositelaunchPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toFlower2(){
        return curve(oppositelaunchPose, CP2, preFlower2Pose).linear(oppositelaunchPose, preFlower2Pose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toIntakeFlower2(){
        return line(preFlower2Pose, intakeFlower2Pose).constant(preFlower2Pose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(0.45)
                );
    }
    private Path toBackOutOfFlower2(){
        return line(intakeFlower2Pose, backOutOfFlower2Pose).constant(intakeFlower2Pose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path jiggleFlower2(){
        Path jiggleForward2 = Paths.line(backOutOfFlower2Pose, jiggleFlower2Pose).constant(backOutOfFlower2Pose);
        Path jiggleBackward2 = Paths.line(jiggleFlower2Pose, backOutOfFlower2Pose).constant(backOutOfFlower2Pose);
        return Paths.path(jiggleForward2, jiggleBackward2)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toThirdLaunch(){
        return line(backOutOfFlower2Pose, oppositelaunchPose).linear(backOutOfFlower2Pose, oppositelaunchPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Path toPark(){
        return curve(oppositelaunchPose, CP3, parkPose).linear(oppositelaunchPose, parkPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }
    private Command goToFlower1(){
        return parallel(
            follow(follower, toFlower1()),
            flowerArm.flowerArmDown()
        );
    }
    private Command intakeFlower1(){
        return parallel(
            race(
                follow(follower, toIntakeFlower1()),
                waitMs(3000)
            ),
            intake.intakeSeconds(1.75)
        );
    }
    private Command backOutOfFlower1(){
        return sequential(
            follow(follower, toBackOutOfFlower1()),
                parallel(
                    follow(follower, jiggleFlower1()),
                    intake.intakeSeconds(2)
                ),
            flowerArm.flowerArmUp()
        );
    }
    private Command goToOppositeSideAndLaunch(){
        return sequential(
            follow(follower, toOppositeSide()),
            launcher.launchSeconds(3, 2470)
        );
    }
    private Command goToFlower2(){
        return parallel(
            follow(follower, toFlower2()),
            flowerArm.flowerArmDown()
        );
    }
    private Command intakeFlower2(){
        return parallel(
            race(
                follow(follower, toIntakeFlower2()),
                waitMs(3000)
            ),
            intake.intakeSeconds(1.75)
        );
    }
    private Command backOutOfFlower2(){
        return sequential(
            follow(follower, toBackOutOfFlower2()),
                parallel(
                    follow(follower, jiggleFlower2()),
                    intake.intakeSeconds(2)
                ),
            flowerArm.flowerArmUp()
        );
    }
    private Command thirdLaunch(){
        return sequential(
            follow(follower, toThirdLaunch()),
            launcher.launchSeconds(3, 2470)
        );
    }
    private Command park(){
        return sequential(
            follow(follower, toPark())
        );
    }


    private Command autoRoutine(){
        return sequential(
            launcher.launchSeconds(3, 2450),
            goToFlower1(),
            intakeFlower1(),
            backOutOfFlower1(),
            goToOppositeSideAndLaunch(),
            goToFlower2(),
            intakeFlower2(),
            backOutOfFlower2(),
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