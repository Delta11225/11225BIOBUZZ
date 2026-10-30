package org.firstinspires.ftc.teamcode.TeleOp.Testing.AprilTags;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;


@TeleOp(name = "BIOBUZZ Motor Tracker w/ Telemetry", group = "Vision")
public class BioBuzzMotorTrackerTelemetry extends OpMode {

    // Limelight camera used to detect the AprilTag clusters.
    private Limelight3A limelight;

    // DC motor attached to the camera mount. This motor rotates the Limelight left and right.
    private DcMotor cameraMotor;

    //////////////////////////////////LIMELIGHT SETTINGS//////////////////////////////

    // Pipeline 0 = Blue alliance
    // Pipeline 1 = Red alliance
    private static final int PIPELINE = 0;

    // Tells the Python SnapScript which alliance's AprilTag clusters to look for.
    private static final double ALLIANCE = 0.0; // 0.0 = Blue alliance     1.0 = Red alliance

    // Camera pitch angle in degrees. Use 0.0 if the camera is level.
    // This is the angle the camera is tilted upward or downward. The Python script uses this value when calculating the distance and position of the AprilTag cluster.
    private static final double CAMERA_PITCH_DEG = 0.0;


    ///////////////////////////////MOTOR ROTATION SETTINGS/////////////////////////////-

    // Proportional gain: Determines how strongly the motor responds to the camera's measured bearing error.
    // A larger kP makes the motor turn faster for the same error. A smaller kP makes the motor turn more slowly.
    private double kP = 0.010;

    // Minimum motor power:
    // DC motors may not move reliably at very low power. If the calculated motor power is smaller than this value, the motor is given at least this much power.
    private double minPower = 0.05;

    // Maximum motor power:
    // Limits how quickly the camera can rotate. This helps prevent the camera from spinning too quickly or overshooting the target.
    private double maxPower = 0.15;

    // Deadband:
    // If the AprilTag cluster is within this many degrees of the center of the camera's view, the motor stops.
    // This prevents the motor from constantly making tiny adjustments when the camera is already aimed at the target.
    private double deadband = 2.0;

    // Motor direction:
    // Changes the direction in which the motor responds to the bearing measurement.
    // Use -1.0 or 1.0 depending on how the motor is mounted.
    // If the camera turns AWAY from the target instead of toward it, switch this value.
    private double MOTOR_DIRECTION = -1.0;


    /////////////////////////////APRILTAG CLUSTER INFORMATION///////////////////////////////

    // These values are received from the Limelight's Python script. They describe the AprilTag cluster currently being detected.

    // Base ID of the detected cluster. A value of -1 means no recognized cluster was detected.
    private double clusterID = -1;

    // Number of AprilTags currently visible in the cluster.
    private double tagCount = 0;

    // Horizontal bearing to the center of the cluster, in degrees.
    // Positive bearing = cluster is to the LEFT of the camera center.
    // Negative bearing = cluster is to the RIGHT of the camera center.
    // Zero bearing = cluster is centered in the camera's view.
    private double bearing = 0;

    // Horizontal floor distance from the camera to the center of the AprilTag cluster, measured in inches.
    private double range = 0;


    ////////////////////////////////// MOTOR POWER /////////////////////////////////////

    // Power that will be sent to the camera motor each loop.
    // Positive and negative values determine the direction of rotation. The actual direction depends on the motor mounting and the MOTOR_DIRECTION setting.
    private double motorPower = 0;


    ///////////////////////////////// INITIALIZATION ////////////////////////////////////

    @Override
    public void init() {

        // hardware initialization
        limelight = hardwareMap.get(Limelight3A.class, "Limelight");
        cameraMotor = hardwareMap.get(DcMotor.class, "cameraMotor");

        // Settings for Camera Motor
        cameraMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); // hard brake so motor doesnt coast
        cameraMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // not using encoder, just setting power
        cameraMotor.setPower(0); // makes sure camera motor is stopped at the start

        // Switch the Limelight to the selected pipeline. (pipeline set in variables at the top)
        limelight.pipelineSwitch(PIPELINE);

        // Display a message to confirm that the OpMode initialized.
        telemetry.addLine("BIOBUZZ Motor Tracker Initialized");
        telemetry.update();
    }


    /////////////////////////////////// START /////////////////////////////////////////////////

    @Override
    public void start() {

        // Start the Limelight so it begins processing camera images.
        limelight.start();

        // Send the alliance and camera pitch information to the custom Python SnapScript.
        // The Python script uses these values to determine which AprilTag clusters to detect and to calculate their position.
        limelight.updatePythonInputs(new double[]{ALLIANCE, CAMERA_PITCH_DEG});

    }


    ///////////////////////////////////// LOOP /////////////////////////////////////////////////

    @Override
    public void loop() {

        // Get the most recent result from the Limelight. This includes the data calculated by the Python SnapScript.
        LLResult result = limelight.getLatestResult();

        // Reset motor power at the beginning of every loop.
        // The motor will only receive power if a valid target is detected and the target needs to be centered.
        motorPower = 0;


        // Check that:
        // 1. A Limelight result exists.
        // 2. Python output exists.
        // 3. The Python output contains at least 8 values (Our SnapScript returns 8 values describing the detected AprilTag cluster)
        if (result != null && result.getPythonOutput() != null
                && result.getPythonOutput().length >= 8) {

            // Store the Python output in an array so we can access the individual pieces of cluster information.
            double[] python = result.getPythonOutput();


            // Read the first four values returned by the Python script.
            clusterID = python[0]; // python[0] = Cluster base ID
            tagCount = python[1]; // python[1] = Number of tags detected
            range = python[2]; // python[2] = Range to cluster center (inches)
            bearing = python[3]; // python[3] = Bearing to cluster center (degrees)


            // A target is considered visible if:
            // 1. The cluster ID is not -1.
            // 2. At least one AprilTag is visible.
            // If either condition is false, the camera motor stays stopped.
            boolean targetVisible = clusterID >= 0 && tagCount > 0;


            if (targetVisible) {

                // DETERMINE HOW MUCH THE CAMERA NEEDS TO ROTATE

                // If the bearing is within the deadband, the cluster is close enough to the center of the camera view.
                // Stop the motor instead of making tiny adjustments.
                if (Math.abs(bearing) < deadband) {
                    motorPower = 0;
                }

                else {

                    // Calculate motor power using proportional control.
                    // The farther the cluster is from the center, the larger the bearing value and the stronger the motor response.
                    // Example: If bearing = 10 degrees and kP = 0.025 then motorPower = 0.25 before the maximum power limit.
                    motorPower = kP * bearing;


                    // Make sure the motor receives enough power to move.
                    // If the calculated power is too small, increase it to minPower while keeping its original direction.
                    // Math.signum() returns: 1.0 for a positive value, -1.0 for a negative value
                    if (Math.abs(motorPower) < minPower) {
                        motorPower = Math.signum(motorPower) * minPower;
                    }
                }

                // APPLY MOTOR DIRECTION AND POWER LIMITS

                // Adjust the direction based on how the motor is mounted. This makes the camera rotate toward the AprilTag cluster.
                motorPower *= MOTOR_DIRECTION;

                // Keep motor power between -maxPower and +maxPower. This prevents the camera from rotating too quickly.
                //Range.clip works by taking first value and clipping it between the next two values. If motor power is greater than maxPower, motorPower = maxPower
                motorPower = Range.clip(motorPower, -maxPower, maxPower);
            }

            else {

                // No recognized AprilTag cluster is visible.Stop the motor rather than continuing to rotate.
                motorPower = 0;
            }
        }


        // SEND POWER TO THE CAMERA MOTOR
        // Apply the calculated power to the motor. This happens every loop, allowing the camera to continually adjust its position as the robot or target moves.
        cameraMotor.setPower(motorPower);



        ////////////////////////////////////// TELEMETRY /////////////////////////////////////////

        // Display the information received from the Python script and the power being sent to the motor.
        telemetry.addData("Cluster ID", clusterID);
        telemetry.addData("Tags Seen", tagCount);
        telemetry.addData("Bearing", "%.2f degrees", bearing);
        telemetry.addData("Range", "%.2f inches", range);
        telemetry.addData("Motor Power", "%.3f", motorPower);

        telemetry.update();
    }

    ///////////////////////////////////// STOP /////////////////////////////////////////////////
    @Override
    public void stop() {

        // Stop the camera motor when the OpMode ends.
        cameraMotor.setPower(0);

        // Stop the Limelight.
        limelight.stop();
    }
}