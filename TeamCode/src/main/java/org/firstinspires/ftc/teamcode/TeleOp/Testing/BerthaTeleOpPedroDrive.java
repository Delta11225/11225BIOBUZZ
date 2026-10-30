package org.firstinspires.ftc.teamcode.TeleOp.Testing;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.TeleOp.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.Constants;


/*
 * This TeleOp uses Pedro Pathing to:
 *
 * 1. Control the mecanum drivetrain.
 * 2. Read the robot's position from the goBILDA Pinpoint.
 * 3. Display the robot's position and heading.
 *
 * Pedro handles communication with the drivetrain and Pinpoint.
 * We should NOT separately initialize the Pinpoint or write directly
 * to the drive motors in this OpMode.
 */

@TeleOp(name = "Bertha Pedro Drive Test", group = "Bertha")
public class BerthaTeleOpPedroDrive extends OpMode {

    // The Follower is Pedro's main object.
    // It connects the drivetrain, localizer (Pinpoint), and movement controllers.
    private Follower follower;

    // This variable scales the joystick power.
    // A value of 1.0 means full power.
    private double powerMultiplier = 1.0;


    @Override
    public void init() {

    //init Pedro, drivetrain, and pinpoint through pedropathing
        follower = Constants.create(hardwareMap);

        telemetry.addLine("Pedro initialized");
        telemetry.addLine("Press INIT, then PLAY to begin.");
        telemetry.update();
    }

    @Override
    public void start() {

        //check whether auto saved a position
        if (OpModeStorage.autoPoseSaved == true){
            //Starting pose of teleop = ending pose of auto
            follower.setPose(OpModeStorage.autonomousEndPose);

            telemetry.addLine("Using Auto Ending Position");
        }
        else{
            //if no auto position was saved, start at field origin (audience left corner)
            follower.setPose(new Pose(0,0,0));

            telemetry.addLine("No Auto position found. Starting at 0,0,0");
        }
        telemetry.update();
    }

    @Override
    public void loop() {

        //Handling Power Multipliers - Speed Modes
        if (gamepad1.left_bumper) {//fast mode
            powerMultiplier = 1.0;
        }
        else if (gamepad1.right_bumper) {//slow mode
            powerMultiplier = 0.4;
        }
        else {
            powerMultiplier = 0.8;//normal
        }

        //Getting joystick readings (Gamepad 1)
        double forward = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x * 1.1; //1.1 multiplier on lateral movement for strafe adjustment
        double turn = gamepad1.right_stick_x;


        //Applying speed multipliers
        forward *= powerMultiplier;
        lateral *= powerMultiplier;
        turn *= powerMultiplier;



        // Utilize Pedro's ManualDrive.fieldCentric()
        // ManualDrive.fieldCentric() uses the robot's current heading to convert our joystick commands into the correct drivetrain commands.
        // follower.pose().heading() gives Pedro's current heading, which comes from the Pinpoint localization system.

        DrivePowers powers = ManualDrive.fieldCentric(
                forward,
                lateral,
                turn,
                follower.pose().heading()
        );


        // Send Drive Commands to Pedro. Pedro calculates the appropriate drivetrain output
        // This tells Pedro what we want the robot to do.It does not directly set individual motor powers.
        follower.manual(powers);


        // UPDATE PEDRO: Each loop, update() allows Pedro to:
        // 1. Read the Pinpoint
        // 2. Update the robot's estimated position
        // 3. Update its heading
        // 4. Update drivetrain control

        follower.update();


        //Read Robot's current Pose (x, y, heading)
        // We can use this information later to tell the robot to automatically drive to a specific location.
        Pose robotPose = follower.pose();


///////////////////////////// Telemetry ///////////////////////////////

        // Display X and Y coordinates in inches
        telemetry.addData("X Position", "%.2f inches", robotPose.x());
        telemetry.addData("Y Position", "%.2f inches", robotPose.y());

        // Pedro stores heading in radians.
        // Convert to degrees for easier reading.
        telemetry.addData("Heading", "%.1f degrees", Math.toDegrees(robotPose.heading()));

        telemetry.addData("Drive Speed", "%.0f%%", powerMultiplier * 100);

        telemetry.update();
    }


    @Override
    public void stop() {

        if (follower != null) {
            follower.stop();
        }
    }
}
