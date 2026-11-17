package org.firstinspires.ftc.teamcode.Competition.Auto;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
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

import org.firstinspires.ftc.teamcode.TeleOp.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.FlowerArm;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Launcher;


@Autonomous(preselectTeleOp = "BerthaTeleOpOneDriver_LL_BLUE")
public class BerthaAudienceOneTipBLUE extends OpMode {
    private Follower follower;
    private Intake intake;
    private Launcher launcher;
    private FlowerArm flowerArm;

    ////////////////////////POSES////////////////////////
    private final PoseFactory p = PoseFactory.degrees();
        private final Pose startPose = p.of(55, 8, 270);
        private final Pose CP1 = p.of(16, 42, 0);
        private final Pose parkPose = p.of(15, 90, 0);


    ////////////////////////PATHS////////////////////////
    private Path toPark(){
        return curve(startPose, CP1, parkPose).linear(startPose, parkPose)
                .with(
                        Constants.foresightConfig.maxPathSpeed.at(1.0)
                );
    }

    ////////////////////////COMMANDS////////////////////////
    private Command park(){
        return sequential(
            follow(follower, toPark())
        );
    }

    private Command autoRoutine(){
        return sequential(
            launcher.launchSeconds(3, 2450),
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