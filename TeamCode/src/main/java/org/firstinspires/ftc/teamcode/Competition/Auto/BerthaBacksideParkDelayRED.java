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


@Autonomous(preselectTeleOp = "BerthaTeleOpOneDriver_LL_RED")
public class BerthaBacksideParkDelayRED extends OpMode {
    private Follower follower;
    private Intake intake;
    private Launcher launcher;
    private FlowerArm flowerArm;

    ////////////////////////POSES////////////////////////
    private final PoseFactory p = PoseFactory.degrees();
        private final Pose startPose = p.of(58, 136, 90);
        private final Pose firstLaunchJiggleForwardPose = p.of(58, 134, 90);
        private final Pose firstLaunchJiggleBackwardPose = p.of(58, 135, 90);
        private final Pose CP1 = p.of(61, 96, 0);
        private final Pose CP2 = p.of(21, 142, 0);
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
    private Path toPark(){
        return curve(firstLaunchJiggleBackwardPose, CP1, CP2, parkPose).linear(firstLaunchJiggleBackwardPose, parkPose)
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
    private Command park(){
        return sequential(
            follow(follower, toPark())
        );
    }

    private Command autoRoutine(){
        return sequential(
            waitMs(20000),
            firstLaunch(),
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
