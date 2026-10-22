package org.firstinspires.ftc.teamcode.pedro.TestAutos;

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

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Launcher;


@Autonomous
public class BerthaTwoSide extends OpMode {
    private Follower follower;
    private Intake intake;
    private Launcher launcher;
    private final PoseFactory p = PoseFactory.degrees();
        private final Pose startPose = p.of(55, 8, 270);
        private final Pose CP1 = p.of(40, 18, 270);
        private final Pose collectGardenPose = p.of(26, 10, 200);
        private final Pose midGardenPose = p.of(18, 10, 200);
        private final Pose endGardenPose = p.of(11, 10, 180);
        private final Pose CP2 = p.of(36, 80, 180);
        private final Pose oppositelaunchPose = p.of(34, 125, 120);

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
                launcher.launchSeconds(3, 2360)
        );
    }


    private Command autoRoutine(){
        return sequential(
            launcher.launchSeconds(3, 2300),
            goToGarden(),
            gardenIntake(),
            goToOppositeSideandLaunch()

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
}
