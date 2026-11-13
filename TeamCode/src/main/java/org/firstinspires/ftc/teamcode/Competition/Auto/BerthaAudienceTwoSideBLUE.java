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


@Autonomous(preselectTeleOp = "BerthaTeleOpOneDriver_LL_BLUE")
public class BerthaAudienceTwoSideBLUE extends OpMode {
    private Follower follower;
    private Intake intake;
    private Launcher launcher;
    private FlowerArm flowerArm;

    ////////////////////////POSES////////////////////////
    private final PoseFactory p = PoseFactory.degrees();
        private final Pose startPose = p.of(55, 8, 270);
        private final Pose giveSpacePose = p.of(55, 15, 270);
        private final Pose preFlower1Pose = p.of(24, 45, 180);
        private final Pose intakeFlower1Pose = p.of(15.9, 45, 180);
        private final Pose backOutofFlower1Pose = p.of(30, 45, 180);
        private final Pose jiggleFlower1Pose = p.of(26, 45, 180);
        private final Pose CP1 = p.of(40, 72, 120);
        private final Pose oppositelaunchPose = p.of(34, 125, 120);
        private final Pose parkPose = p.of(15, 90, 0);
        private final Pose CP3 = p.of(37, 14, 0);

    ////////////////////////PATHS////////////////////////
    private Path toFlower1(){
        Path toGiveSpace = Paths.line(startPose, giveSpacePose).constant(startPose);
        Path toPreFLower1 = Paths.line(giveSpacePose, preFlower1Pose).linear(giveSpacePose, preFlower1Pose);
        return Paths.path(toGiveSpace, toPreFLower1)
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
    private Path toPark(){
        return curve(oppositelaunchPose, CP3, parkPose).linear(oppositelaunchPose, parkPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }

    ////////////////////////COMMANDS////////////////////////
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
                    intake.intakeSeconds(1)
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
            park()
        );
    }

    ////////////////////////RUN METHODS////////////////////////

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