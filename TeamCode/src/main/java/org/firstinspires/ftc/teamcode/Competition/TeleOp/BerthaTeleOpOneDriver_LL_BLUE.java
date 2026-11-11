package org.firstinspires.ftc.teamcode.Competition.TeleOp;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.TeleOp.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.TeleOp.StateMachines.BerthaIntakeSMV2;
import org.firstinspires.ftc.teamcode.TeleOp.StateMachines.BerthaLauncherSMPIDF;
import org.firstinspires.ftc.teamcode.TeleOp.StateMachines.BerthaWindmillSM;

@TeleOp
public class BerthaTeleOpOneDriver_LL_BLUE extends OpMode {
    public DcMotor rearLeft = null;
    public DcMotor rearRight = null;
    public DcMotor frontLeft = null;
    public DcMotor frontRight = null;

    private final BerthaIntakeSMV2 intake = new BerthaIntakeSMV2();
    private final BerthaWindmillSM windmill = new BerthaWindmillSM();
    private final BerthaLauncherSMPIDF launcher = new BerthaLauncherSMPIDF();

    GoBildaPinpointDriver odo; // Declare OpMode member for the Odometry Computer


    private ElapsedTime runtime = new ElapsedTime();

    double frontLeftV;
    double rearLeftV;
    double frontRightV;
    double rearRightV;

    double forward;
    double right;
    double clockwise;

    double powerMultiplier = 0.7;

    double denominator;
    double temp;
    double side;

    double currentAngle;

    double baselauncherRPM = 0;


    private Limelight3A limelight;
    private static final int PIPELINE = 2;
    private static final double ALLIANCE = 0.0;
    private static final double CAMERA_PITCH_DEG = 35;
    private static final int CLUSTER_ID = 0;
    private static final int NUM_TAGS = 1;
    private static final int RANGE = 2;
    private static final int BEARING = 3;
    private static final int HEIGHT = 4;
    private static final int ELEVATION = 5;
    private static final int STICKER_PITCH = 6;
    private static final int ROLL = 7;

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

    @Override
    public void init() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.

        odo = hardwareMap.get(GoBildaPinpointDriver.class,"pinpoint");

        /*
        Set the odometry pod positions relative to the point that the odometry computer tracks around.
        The X pod offset refers to how far sideways from the tracking point the
        X (forward) odometry pod is. Left of the center is a positive number,
        right of center is a negative number. the Y pod offset refers to how far forwards from
        the tracking point the Y (strafe) odometry pod is. forward of center is a positive number,
        backwards is a negative number.
         */
        odo.setOffsets(32.5, -37.5, DistanceUnit.MM); //these are tuned for 3110-0002-0001 Product Insight #1

        /*
        Set the kind of pods used by your robot. If you're using goBILDA odometry pods, select either
        the goBILDA_SWINGARM_POD, or the goBILDA_4_BAR_POD.
        If you're using another kind of odometry pod, uncomment setEnc.mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        //odo.setEncoderResolution(13.26291192, DistanceUnit.MM);


        /*
        Set the direction that each of the two odometry pods count. The X (forward) pod should
        increase when you move the robot forward. And the Y (strafe) pod should increase when
        you move the robot to the left.
         */
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);


        /*
        Before running the robot, recalibrate the IMU. This needs to happen when the robot is stationary
        The IMU will automatically calibrate when first powered on, but recalibrating before running
        the robot is a good idea to ensure that the calibration is "good".
        resetPosAndIMU will reset the position to 0,0,0 and also recalibrate the IMU.
        This is recommended before you run your autonomous, as a bad initial calibration can cause
        an incorrect starting value for x, y, and heading.
         */
        //odo.recalibrateIMU();
        odo.resetPosAndIMU();

        limelight = hardwareMap.get(Limelight3A.class, "Limelight");

        limelight.pipelineSwitch(PIPELINE);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("X offset", odo.getXOffset(DistanceUnit.MM));
        telemetry.addData("Y offset", odo.getYOffset(DistanceUnit.MM));
        telemetry.addData("Device Version Number:", odo.getDeviceVersion());
        telemetry.addData("Heading Scalar", odo.getYawScalar());
        telemetry.addLine("Limelight found");
        telemetry.addData("Pipeline", PIPELINE);
        telemetry.update();

        telemetry.addData("Status", "Initialized");
        telemetry.addData("X offset", odo.getXOffset(DistanceUnit.MM));
        telemetry.addData("Y offset", odo.getYOffset(DistanceUnit.MM));
        telemetry.addData("Device Version Number:", odo.getDeviceVersion());
        telemetry.addData("Heading Scalar", odo.getYawScalar());
        telemetry.update();

        //robot = new HardwareITD(hardwareMap);
        rearLeft = hardwareMap.dcMotor.get("lr");
        rearLeft.setDirection(DcMotor.Direction.REVERSE);

        frontLeft = hardwareMap.dcMotor.get("lf");
        frontLeft.setDirection(DcMotor.Direction.REVERSE);

        frontRight = hardwareMap.dcMotor.get("rf");
        frontRight.setDirection(DcMotor.Direction.FORWARD);

        rearRight = hardwareMap.dcMotor.get("rr");
        rearRight.setDirection(DcMotor.Direction.FORWARD);

        rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //initialize drive motors
        frontLeft.setPower(0);
        frontRight.setPower(0);
        rearLeft.setPower(0);
        rearRight.setPower(0);

        intake.init(hardwareMap);
        windmill.init(hardwareMap);
        launcher.init(hardwareMap);

    }
    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        boolean switched = limelight.pipelineSwitch(PIPELINE);

        telemetry.addData("Pipeline switch accepted", switched);
        telemetry.addData("Expected pipeline slot", PIPELINE);
        telemetry.update();

        limelight.updatePythonInputs(new double[] {
                0.0, CAMERA_PITCH_DEG,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        });

        limelight.start();
        limelight.updatePythonInputs(new double[]{ALLIANCE, CAMERA_PITCH_DEG});

        currentAngle = 0;
        runtime.reset();
        odo.resetPosAndIMU();
    }

    @Override
    public void loop() {
        odo.update(GoBildaPinpointDriver.ReadData.ONLY_UPDATE_HEADING);


        Pose2D pos = odo.getPosition();
        telemetry.addData("Heading", pos.getHeading(AngleUnit.DEGREES));

        currentAngle =pos.getHeading(AngleUnit.DEGREES);
        move();
        peripheral();


        LLResult result = limelight.getLatestResult();

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

                ////////////////////// DETERMINE OFFSET //////////////////////
                telemetry.addData("HIVE Opening Offset", "%.2f degrees", openingBearing);

            }

            else {
                // No recognized cluster is visible.
                telemetry.addLine("No Cluster Detected");
            }
        }

        else {
            // No valid Python output.
            telemetry.addLine("No output");
        }



        ////////////////////////////////// TELEMETRY //////////////////////////////////////

        telemetry.addData("Pipeline", PIPELINE);
        telemetry.addData("Cluster ID", clusterID);
        telemetry.addData("Tags Seen", tagCount);

        telemetry.addData("Cluster Bearing", "%.2f degrees", clusterBearing);
        telemetry.addData("HIVE Opening Bearing", "%.2f degrees", openingBearing);

        telemetry.addData("Range to Cluster", "%.2f inches", range);

        telemetry.update();
    }

    /////////////////////////move sequence//////////////////////////
    public void move() {

        double theta = Math.toRadians(currentAngle);

        telemetry.addData("CurrentAngle", currentAngle);
        telemetry.addData("Theta", theta);

        //Orientation set for robot facing away from driver
        forward = -gamepad1.left_stick_y; //left joystick up
        right = gamepad1.left_stick_x * 1.1; //left joystick right, adjusting for strafe
        //clockwise = gamepad1.right_stick_x; //right joystick right (up on FTC Dashboard)
        //Checking to see if correct april tag for alliance color is visible
        boolean targetVisible = clusterID >= 0 && tagCount > 0;
        boolean autoAlignActive = gamepad1.a && targetVisible;

        //code to auto align is active based on boolean settings above
        if (autoAlignActive) {

            telemetry.addData("Horizontal Offset (Yaw)", openingBearing);

            double kP_turn = 0.025; //what rate ur turning
            clockwise = clamp(-kP_turn*openingBearing, -0.725,0.725);

        }
        else{
            //manual rotation contol (a button NOT pressed OR no april tag detected)
            clockwise = gamepad1.right_stick_x;
        }


        temp = (forward * Math.cos(theta) - right * Math.sin(theta));
        side = (forward * Math.sin(theta) + right * Math.cos(theta));

        forward = temp;
        right = side;

        denominator = Math.max(Math.abs(forward) + Math.abs(right) + Math.abs(clockwise), 1);
        frontLeftV = (forward + right + clockwise) / denominator;
        rearLeftV = (forward - right + clockwise) / denominator;
        rearRightV = (forward + right - clockwise) / denominator;
        frontRightV = (forward - right - clockwise) / denominator;

        // Handle speed control
        frontLeft.setPower(frontLeftV * powerMultiplier);
        frontRight.setPower(frontRightV * powerMultiplier);
        rearLeft.setPower(rearLeftV * powerMultiplier);
        rearRight.setPower(rearRightV * powerMultiplier);

        //add speed control here

        if (gamepad1.cross) {
            powerMultiplier = .8;//FAST MODE
        } else if (gamepad1.circle) {
            powerMultiplier = .4; //SLOW MODE
        } else {
            powerMultiplier = 1; //NORMAL MODE
        }


    }/////end of move////////
    public void peripheral() {
        boolean launcherRPM = gamepad1.x;
        boolean launcherOff = gamepad1.b;

        if(gamepad1.right_bumper) {
            intake.setState(BerthaIntakeSMV2.MotorState.INTAKE);
            launcher.setLauncherState(BerthaLauncherSMPIDF.LauncherState.IDLE);
            windmill.setState(BerthaWindmillSM.windmillState.IDLE);
        }
        else if(gamepad1.left_bumper){
            intake.setState(BerthaIntakeSMV2.MotorState.OUTTAKE);
            launcher.setLauncherState(BerthaLauncherSMPIDF.LauncherState.IDLE);
            windmill.setState(BerthaWindmillSM.windmillState.IDLE);
        }
        else{
            intake.setState(BerthaIntakeSMV2.MotorState.IDLE);
        }

        if(launcherRPM){
            baselauncherRPM = 2450;
            launcher.setLauncherState(BerthaLauncherSMPIDF.LauncherState.REVVING_UP);

        }
        else if (launcherOff) {
            launcher.setLauncherState(BerthaLauncherSMPIDF.LauncherState.IDLE);
        }

        if(gamepad1.right_trigger_pressed && launcher.getLaunchState() == BerthaLauncherSMPIDF.LauncherState.READY_TO_LAUNCH){
            windmill.setState(BerthaWindmillSM.windmillState.FORWARD);
        }
        else if (gamepad1.dpad_down){
            windmill.setState(BerthaWindmillSM.windmillState.BACKWARDS);
        }
        else{
            windmill.setState(BerthaWindmillSM.windmillState.IDLE);

        }

        intake.update();
        windmill.update();
        launcher.update(baselauncherRPM);
    }/////end of peripheral move////////

    public void stop(){
        intake.stop();
        launcher.stop();
        windmill.stop();
        limelight.stop();
    }
    //Helper clamp method for rotation correction
    private double clamp (double value, double min, double max){
        return Math.max(min, Math.min(max, value));
    }

}
