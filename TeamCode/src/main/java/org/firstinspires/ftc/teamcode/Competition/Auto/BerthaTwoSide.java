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
import org.firstinspires.ftc.teamcode.pedro.Subsystems.FlowerArm;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Launcher;


@Autonomous(preselectTeleOp = "BerthaTeleOpOneDriver")
public class BerthaTwoSide extends OpMode {
    private Follower follower;
    private Intake intake;
    private Launcher launcher;
    private FlowerArm flowerArm;
    private final PoseFactory p = PoseFactory.degrees();
        private final Pose startPose = p.of(55, 8, 270);
        private final Pose preFlower1Pose = p.of(24, 45, 180);
        private final Pose intakeFlower1Pose = p.of(15.9, 45, 180);
        private final Pose backOutofFlower1Pose = p.of(30, 45, 180);
        private final Pose CP1 = p.of(40, 72, 120);
        private final Pose oppositelaunchPose = p.of(34, 125, 120);
        private final Pose flowerDropPose = p.of(50.4, 124.3, 125);
        private final Pose CP4 = p.of(41, 117,90);
        private final Pose readyIntakePose = p.of(33, 132, 180);
        private final Pose finalIntakePose = p.of(12, 133, 180);
        private final Pose parkPose = p.of(15, 99, 0);
        private final Pose CP5 = p.of(46, 102, 0);

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
    private Path toOppositeSide(){
        return curve(backOutofFlower1Pose, CP1, oppositelaunchPose).linear(backOutofFlower1Pose, oppositelaunchPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
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
    private Command goToFlower1(){
        return parallel(
            follow(follower, toFlower1()),
                flowerArm.flowerArmDown()
        );
    }
    private Command intakeFlower1(){
        return parallel(
                follow(follower, toIntakeFlower1()),
                intake.intakeSeconds(4)
        );
    }
    private Command backOutOfFlower1(){
        return sequential(
                follow(follower, toBackOutOfFlower1()),
                flowerArm.flowerArmUp()
        );
    }
    private Command goToOppositeSideandLaunch(){
        return sequential(
                follow(follower, toOppositeSide()),
                launcher.launchSeconds(2, 2460)
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
            goToFlower1(),
            intakeFlower1(),
            backOutOfFlower1(),
            goToOppositeSideandLaunch()


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
