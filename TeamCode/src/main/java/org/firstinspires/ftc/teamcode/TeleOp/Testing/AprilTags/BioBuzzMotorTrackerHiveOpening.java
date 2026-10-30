package org.firstinspires.ftc.teamcode.TeleOp.Testing.AprilTags;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

@TeleOp(name = "BIOBUZZ Motor Tracker HIVE OPENING", group = "Vision")
public class BioBuzzMotorTrackerHiveOpening extends OpMode {

    // Limelight camera used to detect the AprilTag clusters.
    private Limelight3A limelight;

    // DC motor attached to the camera mount.
    private DcMotor cameraMotor;


    ///////////////////////////////// LIMELIGHT SETTINGS /////////////////////////////////

    // Pipeline 2 = Blue HIVE opening tracking.
    // Pipeline 3 = Red HIVE opening tracking.
    private static final int PIPELINE = 2;

    // Alliance input sent to the Python SnapScript.
    // 0.0 = Blue, 1.0 = Red.
    // The separate red script uses its own hardcoded alliance selection.
    private static final double ALLIANCE = 0.0;

    // Camera pitch angle in degrees.
    // Use 0.0 if the camera is level.
    private static final double CAMERA_PITCH_DEG = 0.0;


    ///////////////////////////////// MOTOR ROTATION SETTINGS /////////////////////////////////

    // Proportional gain.
    private double kP = 0.015;

    // Minimum motor power.
    private double minPower = 0.08;

    // Maximum motor power.
    private double maxPower = 0.20;

    // If the HIVE opening is within this many degrees of center, stop the motor.
    private double deadband = 1.5;

    // Motor direction.
    // Change to 1.0 if the camera turns away from the target.
    private double MOTOR_DIRECTION = -1.0;


    /////////////////////////////// APRILTAG CLUSTER INFORMATION ///////////////////////////////

    // Base ID of the detected cluster. -1 means no cluster detected.
    private double clusterID = -1;

    // Number of AprilTags currently visible.
    private double tagCount = 0;

    // Bearing to the AprilTag cluster center.
    private double clusterBearing = 0;

    // Bearing to the predicted HIVE opening center.
    // This is the value used to control the camera motor.
    private double openingBearing = 0;

    // Horizontal floor distance to the AprilTag cluster center, in inches.
    private double range = 0;


    ////////////////////////////////// MOTOR POWER //////////////////////////////////////

    // Power sent to the camera motor each loop.
    private double motorPower = 0;


    ///////////////////////////////// INITIALIZATION ////////////////////////////////////

    @Override
    public void init() {

        // Hardware initialization.
        limelight = hardwareMap.get(Limelight3A.class, "Limelight");
        cameraMotor = hardwareMap.get(DcMotor.class, "cameraMotor");

        // Camera motor settings.
        cameraMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        cameraMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        cameraMotor.setPower(0);

        // Switch to the selected Limelight pipeline.
        limelight.pipelineSwitch(PIPELINE);

        telemetry.addLine("BIOBUZZ HIVE Opening Tracker Initialized");
        telemetry.addData("Pipeline", PIPELINE);
        telemetry.update();
    }


    /////////////////////////////////// START //////////////////////////////////////////////

    @Override
    public void start() {

        // Start the Limelight.
        limelight.start();

        // Send alliance and camera pitch information to the Python SnapScript.
        limelight.updatePythonInputs(new double[]{ALLIANCE, CAMERA_PITCH_DEG});
    }


    ///////////////////////////////////// LOOP //////////////////////////////////////////////

    @Override
    public void loop() {

        // Get the most recent Limelight result.
        LLResult result = limelight.getLatestResult();

        // Reset motor power and target information at the beginning of every loop.
        motorPower = 0;
        clusterID = -1;
        tagCount = 0;
        clusterBearing = 0;
        openingBearing = 0;
        range = 0;

        // Check that a result exists and Python returned at least nine values.
        if (result != null && result.getPythonOutput() != null
                && result.getPythonOutput().length >= 9) {

            // Store the Python output.
            double[] python = result.getPythonOutput();

            // Read the cluster information.
            clusterID = python[0];
            tagCount = python[1];
            range = python[2];

            // Original bearing to the AprilTag cluster center.
            clusterBearing = python[3];

            // New bearing to the predicted HIVE opening center.
            // This is the bearing we want the camera to track.
            openingBearing = python[8];

            // A target is visible if a valid cluster ID and at least one tag are detected.
            boolean targetVisible = clusterID >= 0 && tagCount > 0;

            if (targetVisible) {

                ////////////////////// DETERMINE CAMERA ROTATION //////////////////////

                // If the HIVE opening is within the deadband, stop the motor.
                if (Math.abs(openingBearing) < deadband) {
                    motorPower = 0;
                }

                else {

                    // Calculate motor power using proportional control.
                    // The farther the HIVE opening is from the center,
                    // the stronger the motor response.
                    motorPower = kP * openingBearing;

                    // Make sure the motor receives enough power to move.
                    if (Math.abs(motorPower) < minPower) {
                        motorPower = Math.signum(motorPower) * minPower;
                    }
                }

                ////////////////////// APPLY MOTOR DIRECTION //////////////////////

                // Adjust direction based on how the motor is mounted.
                motorPower *= MOTOR_DIRECTION;

                // Limit motor power.
                motorPower = Range.clip(motorPower, -maxPower, maxPower);
            }

            else {
                // No recognized cluster is visible.
                motorPower = 0;
            }
        }

        else {
            // No valid Python output.
            motorPower = 0;
        }


        ////////////////////////////// SEND POWER TO MOTOR //////////////////////////////

        cameraMotor.setPower(motorPower);


        ////////////////////////////////// TELEMETRY //////////////////////////////////////

        telemetry.addData("Pipeline", PIPELINE);
        telemetry.addData("Cluster ID", clusterID);
        telemetry.addData("Tags Seen", tagCount);

        telemetry.addData("Cluster Bearing", "%.2f degrees", clusterBearing);
        telemetry.addData("HIVE Opening Bearing", "%.2f degrees", openingBearing);

        telemetry.addData("Range to Cluster", "%.2f inches", range);

        telemetry.addData("Motor Power", "%.3f", motorPower);

        telemetry.update();
    }


    ///////////////////////////////////// STOP //////////////////////////////////////////////

    @Override
    public void stop() {

        // Stop the camera motor when the OpMode ends.
        cameraMotor.setPower(0);

        // Stop the Limelight.
        limelight.stop();
    }
}
