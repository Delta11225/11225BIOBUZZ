package org.firstinspires.ftc.teamcode.TeleOp.Testing.AprilTags;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;


@TeleOp(name = "BIOBUZZ Motor Tracker", group = "Vision")
public class BioBuzzMotorTracker extends OpMode {

    //Hardware
    private Limelight3A limelight;
    private DcMotor cameraMotor;

    //Select Pipeline & Alliance
    private static final int PIPELINE = 0; //0 = Blue, 1 = Red
    private static final double ALLIANCE = 0.0; // 0.0 = Blue alliance     1.0 = Red alliance

    // Camera pitch angle in degrees. Use 0.0 if the camera is level.
    private static final double CAMERA_PITCH_DEG = 0.0;

    //MOTOR ROTATION SETTINGS
    private double kP = 0.015;
    private double minPower = 0.08;
    private double maxPower = 0.20;
    private double deadband = 1.5;
    private double MOTOR_DIRECTION = -1.0;

    //APRILTAG CLUSTER INFORMATION
    private double clusterID = -1; // -1 means no recognized cluster was detected.
    private double tagCount = 0; // Number of AprilTags currently visible in the cluster.

    // Horizontal bearing to the center of the cluster, in degrees.
    private double bearing = 0; // + means cluser LEFT of camer, - means cluster RIGHT of camera

    // Horizontal floor distance from the camera to the center of the AprilTag cluster, measured in inches.
    private double range = 0;

    private double motorPower = 0;


    @Override
    public void init() {

        // hardware initialization
        limelight = hardwareMap.get(Limelight3A.class, "Limelight");
        cameraMotor = hardwareMap.get(DcMotor.class, "cameraMotor");

        // Settings for Camera Motor
        cameraMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); // hard brake so motor doesnt coast
        cameraMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // not using encoder, just setting power
        cameraMotor.setPower(0); // makes sure camera motor is stopped at the start

        limelight.pipelineSwitch(PIPELINE);

        telemetry.addLine("BIOBUZZ Motor Tracker Initialized");
        telemetry.update();
    }


    @Override
    public void start() {

        // Start the Limelight so it begins processing camera images.
        limelight.start();

        // Send the alliance and camera pitch information to the custom Python SnapScript.
        limelight.updatePythonInputs(new double[]{ALLIANCE, CAMERA_PITCH_DEG});

    }

    @Override
    public void loop() {

        // Get the most recent result from the Limelight. This includes the data calculated by the Python SnapScript.
        LLResult result = limelight.getLatestResult();

        motorPower = 0;



        if (result != null && result.getPythonOutput() != null && result.getPythonOutput().length >= 8) {

            // Store the Python output in an array so we can access the individual pieces of cluster information.
            double[] python = result.getPythonOutput();

            // Read the first four values returned by the Python script.
            clusterID = python[0]; // python[0] = Cluster base ID
            tagCount = python[1]; // python[1] = Number of tags detected
            range = python[2]; // python[2] = Range to cluster center (inches)
            bearing = python[3]; // python[3] = Bearing to cluster center (degrees)

            boolean targetVisible = clusterID >= 0 && tagCount > 0;


            if (targetVisible) {

                if (Math.abs(bearing) < deadband) {
                    motorPower = 0;
                }

                else {

                    motorPower = kP * bearing;

                    if (Math.abs(motorPower) < minPower) {
                        motorPower = Math.signum(motorPower) * minPower;
                    }
                }

                motorPower *= MOTOR_DIRECTION;

                motorPower = Range.clip(motorPower, -maxPower, maxPower);
            }

            else {

                motorPower = 0;
            }
        }

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


    @Override
    public void stop() {
        cameraMotor.setPower(0);
        limelight.stop();
    }

}