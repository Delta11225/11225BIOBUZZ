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
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.TeleOp.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.pedro.Subsystems.Launcher;


@Autonomous(preselectTeleOp = "BerthaTeleOpOneDriver")
public class BerthaOneSideWAIT extends OpMode {
    private Follower follower;
    private Intake intake;
    private Launcher launcher;
    private Limelight3A limelight;
    public ElapsedTime autoTime = new ElapsedTime();
    private boolean routineScheduled = false;
    private int consecutiveTagFrames = 0;
    private static final int REQUIRED_FRAMES = 3;
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

    private boolean isTargetHiveCellId(int id) {
        return id == 11 || id == 12 || id == 13 || id == 14; //CHANGE TO PROPER ID
    }
    private boolean isTargetTagConfirmed() {
        LLResult result = limelight.getLatestResult();
        boolean tagSeenThisFrame = false;

        if (result != null && result.isValid()) {
            for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
                if (isTargetHiveCellId(tag.getFiducialId())) {
                    tagSeenThisFrame = true;
                    break;
                }
            }
        }
        if (tagSeenThisFrame) {
            consecutiveTagFrames++;
        } else {
            consecutiveTagFrames = 0;
        }

        return consecutiveTagFrames >= REQUIRED_FRAMES;
    }

    @Override
    public void init(){
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        intake = new Intake(hardwareMap);
        launcher = new Launcher(hardwareMap);

        follower.setPose(startPose);
        follower.update();
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0); // Set to AprilTag pipeline index
        limelight.start();

        routineScheduled = false;
        consecutiveTagFrames = 0;
    }

    @Override
    public void start(){
        routineScheduled = false;
        consecutiveTagFrames = 0;
    }

    @Override
    public void loop(){
        follower.update();

        if (!routineScheduled && isTargetTagConfirmed()) {
            schedule(autoRoutine());
            routineScheduled = true;
        } //NEED TO ADD AN EMERGENCY PARK USING ELASPED TIME AND AN ELSE STATEMENT

        Scheduler.execute();

        telemetry.addData("Routine Triggered", routineScheduled);
        telemetry.addData("Consecutive Tag Frames", consecutiveTagFrames);
        telemetry.addData("Robot Pose", follower.pose());
        telemetry.update();
    }

    @Override
    public void stop() {

        //Save the robot's final position when Auto ends
        OpModeStorage.autonomousEndPose = follower.pose();

        //Indicate that a valid Auto position was saved
        OpModeStorage.autoPoseSaved = true;

        if (limelight != null) {
            limelight.stop();
        }

    }
}
