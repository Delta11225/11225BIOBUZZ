package org.firstinspires.ftc.teamcode.MsPacman;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.MsPacman.StateMachines.ArtifactDetectionSM;
import org.firstinspires.ftc.teamcode.MsPacman.StateMachines.GateSM;
import org.firstinspires.ftc.teamcode.MsPacman.StateMachines.IntakeSM;
import org.firstinspires.ftc.teamcode.MsPacman.StateMachines.KickstandSM;
import org.firstinspires.ftc.teamcode.MsPacman.StateMachines.LauncherSM_WITHPDIF;
import org.firstinspires.ftc.teamcode.MsPacman.StateMachines.RampSM;
import org.firstinspires.ftc.teamcode.TeleOp.GoBildaPinpointDriver;

@TeleOp(name="MsPacman TeleOp")
public class MsPacmanTeleOpNew extends OpMode {
    private Limelight3A limelight;

    public Servo rampSpiral = null;
    //HardwareITD robot;

    public DcMotor rearLeft = null;
    public DcMotor rearRight = null;
    public DcMotor frontLeft = null;
    public DcMotor frontRight = null;


    GoBildaPinpointDriver odo; // Declare OpMode member for the Odometry Computer


    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime artifactsCollected = new ElapsedTime();


    boolean threeArtifactsCollected = false;


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

    double bluegoalX = -61;
    double bluegoalY = -45;
    double rampSpiralLow = 0;
    double rampSpiralMid = 0.5;
    double rampSpiralHigh= 0.55;

    //importing State Machines
    private IntakeSM intake = new IntakeSM();
    private RampSM ramp = new RampSM();
    private LauncherSM_WITHPDIF launcher = new LauncherSM_WITHPDIF();
    private GateSM gate = new GateSM();
    private ArtifactDetectionSM artifacts = new ArtifactDetectionSM();
    private KickstandSM kickstand = new KickstandSM();

    double baselauncherRPM = 0;
    double launcherRPM = 0;

    /////////////////////PIDF Constants for Auto Align///////////////////////////
    double kP = 0.025; // 0.025
    double kI = 0.003; // 0.0001
    double kD = 0.0015; // 0.0015

    double errorSum = 0;
    double lastError = 0;
    ElapsedTime pidTimer = new ElapsedTime();
    ElapsedTime gateTime = new ElapsedTime();




    @Override
    public void init() {

        intake.init(hardwareMap);
        ramp.init(hardwareMap);
        launcher.init(hardwareMap);
        gate.init(hardwareMap);
        artifacts.init(hardwareMap);
        kickstand.init(hardwareMap);

        //Stuff for limelight
        limelight = hardwareMap.get(Limelight3A.class, "Limelight");
        limelight.pipelineSwitch(0);

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.

        odo = hardwareMap.get(GoBildaPinpointDriver.class,"pinpoint");

        odo.setOffsets(DistanceUnit.MM.fromInches(-3.25), DistanceUnit.MM.fromInches(-4.00)); //these are tuned for 3110-0002-0001 Product Insight #1

        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED, GoBildaPinpointDriver.EncoderDirection.REVERSED);


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






        //robot = new HardwareITD(hardwareMap);
        rearLeft = hardwareMap.dcMotor.get("leftBack");
        rearLeft.setDirection(DcMotor.Direction.REVERSE);

        frontLeft = hardwareMap.dcMotor.get("leftFront");
        frontLeft.setDirection(DcMotor.Direction.REVERSE);

        frontRight = hardwareMap.dcMotor.get("rightFront");
        frontRight.setDirection(DcMotor.Direction.FORWARD);

        rearRight = hardwareMap.dcMotor.get("rightBack");
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

        //initialize rampSpiral
        rampSpiral = hardwareMap.get(Servo.class, "spiral_servo");

        //Initializing rampSpiral
        rampSpiral.setPosition(0);


        telemetry.addLine("Init Complete");
        telemetry.addData("Heading Scalar", odo.getYawScalar());
        telemetry.update();




    }
    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        currentAngle = 0;
        runtime.reset();
        limelight.start();
    }

    @Override
    public void loop() {
        ///////////////////////DRIVE//////////////////////////////

        //getting limelight Apriltag info
        LLStatus status = limelight.getStatus();

        //checking for limelight
        LLResult result = limelight.getLatestResult();


        odo.update(GoBildaPinpointDriver.ReadData.ONLY_UPDATE_HEADING);

        odo.update();
        Pose2D pose = odo.getPosition();

//        //Calculating Robot Position
//        double robotX = -(pose.position.y) + startX;
//        double robotY = (pose.position.x) + startY;
//        double robotHeading = (pose.heading.toDouble())+ startHeading;

        //Calculating Robot Position using PoseStorage inherited start pose @Pierson
        double robotX = pose.getX(DistanceUnit.INCH);
        double robotY = pose.getY(DistanceUnit.INCH);
        double robotHeading = pose.getHeading(AngleUnit.DEGREES);

        //Calculating Robot Distance to goal
        double deltaX = bluegoalX - robotX;
        double deltaY = bluegoalY - robotY;
        double distance2Goal = Math.sqrt(deltaX*deltaX + deltaY*deltaY);

        telemetry.addData("pinpoint Robot X Position: ", robotX);
        telemetry.addData("pinpoint Robot Y Position: ", robotY);
        telemetry.addData("heading",robotHeading);
        telemetry.addData("distance to red goal", distance2Goal);
        telemetry.update();


        Pose2D pos = odo.getPosition();
        telemetry.addData("Heading", pos.getHeading(AngleUnit.DEGREES));

        currentAngle =pos.getHeading(AngleUnit.DEGREES);
        move();
        peripheral();

    }

    /////////////////////////move sequence//////////////////////////
    public void move() {

        double theta = Math.toRadians(currentAngle);

        //Get latest limelight result
        LLResult result = limelight.getLatestResult();



        //setting joystick directions for driver oriented controls
        forward = -gamepad1.left_stick_x;
        right = -gamepad1.left_stick_y * 1.1;
        //right joystick right (up on FTC Dashboard)

        //Checking to see if correct april tag for alliance color is visable
        boolean autoAlignActive = gamepad1.a && result.isValid();
        double telem = 0;

        //code to auto align is active based on boolean settings above
        if (autoAlignActive) {
            // tx is the horizontal offset from the crosshair to the April Tag center
            double tx = (result.getTx());// How far left or right the target is (degrees) //-4.25
            double dt = pidTimer.seconds();
            pidTimer.reset();

            if(baselauncherRPM > 3200){
                tx = (result.getTx() - 4.25);
                telem = 4.25;
            }
            else if (baselauncherRPM <= 3200){
                tx = (result.getTx());
                telem = 0;
            }

            // Proportional: further away means turning faster
            double pOut = kP * tx;

            // Integral: eliminates small degree error- can overpower friction even if robot needs to turn
            // only 1 degree; this error sum builds up over time and the integral tracks error and fixes what
            // proportion cannot
            if (Math.abs(tx) < 5) { // only uses I when close to target (within 3 degrees)
                errorSum += tx * dt;
                // Anti wind-up clamp
                // errorSum = clamp(errorSum,-5,5);
            } else {
                errorSum = 0;  // reset to prevent error buildup
            }
            double iOut = kI * errorSum;

            // Derivative: Measures how fast tx changes
            // if appoaching center quickly, dOut becomes negative to counter change and slow robot down

            double dOut = (kD * (tx - lastError))/dt;
            lastError = tx;

            // add to rotation power
            clockwise = pOut + iOut + dOut;

            //Dealing with static friction -> min power to counter static
            double min_turn = 0.1;
            if (Math.abs(tx) > 0.5) {
                if (Math.abs(clockwise) < min_turn) {
                    clockwise = Math.signum(clockwise) * min_turn;
                }
            } else {
                clockwise = 0;
            }
        }else {
            // If not using auto align, use normal rotation controls
            clockwise = gamepad1.right_stick_x;
            errorSum = 0;
            lastError = 0;
            pidTimer.reset();
        }




        telemetry.addData("Tx align", result.getTx());
        telemetry.addData("offset", telem);
        telemetry.update();

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

        if (gamepad1.left_bumper) {
            powerMultiplier = 1;//FAST MODE //0.85
        } else if (gamepad1.right_bumper) {
            powerMultiplier = .3; //SLOW MODE //0.3
        } else {
            powerMultiplier = .7; //NORMAL MODE //0.7
        }


    }/////end of move////////

    //Helper clamp method for rotation correction
    private double clamp (double value, double min, double max){
        return Math.max(min, Math.min(max, value));
    }
    public void peripheral() {
////////////////////////INTAKE//////////////////////////////

        // Map buttons to state machine inputs
        boolean intakeButton = gamepad2.right_bumper;
        boolean outtakeButton = gamepad2.left_bumper;


        /// ///////////////////////KICKSTAND//////////////////////////////////////

        //Map buttons for kickstand
        boolean downButtons = gamepad1.dpad_up && gamepad1.y;
        boolean upButtons = gamepad1.dpad_down && gamepad1.a;


        ////////////////////////RAMP+GATE+ARTIFACT DETECTION//////////////////////////////

        // Map buttons to state machine inputs
        boolean rampUpButton = gamepad2.dpad_up;
        boolean rampDownButton = gamepad2.dpad_down;

        //reseting gate timer as long as ramp up is not pressed
        if (rampUpButton==false){
            gateTime.reset();
        }

        if (rampUpButton) {
            gate.setGateState(GateSM.GateState.OPENED);
            if(launcher.getLaunchState()==LauncherSM_WITHPDIF.LauncherState.LAUNCHING && gateTime.seconds() > 0.4){
                ramp.setRampState(RampSM.RampState.MANUAL_UP);
            }
            else if(launcher.getLaunchState()==LauncherSM_WITHPDIF.LauncherState.READY_TO_LAUNCH && gateTime.seconds() > 0.4){
                launcher.setLauncherState(LauncherSM_WITHPDIF.LauncherState.LAUNCHING);
                ramp.setRampState(RampSM.RampState.MANUAL_UP);
            }
        } else if (rampDownButton) {
            ramp.setRampState(RampSM.RampState.MANUAL_DOWN);
            gate.setGateState(GateSM.GateState.CLOSED);

        }  else if (intake.getIntakeState() == IntakeSM.IntakeState.INTAKING){
            gate.setGateState(GateSM.GateState.CLOSED);
            if (artifacts.getArtifactState()==ArtifactDetectionSM.ArtifactDetectionState.ONE_ARTIFACT_COLLECTED){
                ramp.setRampState(RampSM.RampState.INTAKING_ONE);
            }else if (artifacts.getArtifactState() == ArtifactDetectionSM.ArtifactDetectionState.TWO_ARTIFACTS_COLLECTED) {
                ramp.setRampState(RampSM.RampState.INTAKING_TWO);
            }else if (artifacts.getArtifactState() == ArtifactDetectionSM.ArtifactDetectionState.THREE_ARTIFACTS_COLLECTED){
                ramp.setRampState(RampSM.RampState.INTAKING_THREE);
                artifactsCollected.reset();
            }else {
                ramp.setRampState(RampSM.RampState.INTAKING_ZERO);
            }

        } else {
            ramp.setRampState(RampSM.RampState.IDLE);
            gate.setGateState(GateSM.GateState.CLOSED);
        }

        if (artifactsCollected.seconds()>0 && artifactsCollected.seconds()<0.5){
            gamepad1.rumble(500);
            gamepad2.rumble(500);
        }



        ///////////////////////////////LAUNCHER////////////////////////////////////
        boolean launcherOnLowRPM = gamepad2.x;
        boolean launcherOnMediumRPM = gamepad2.y;
        boolean launcherOnHighRPM = gamepad2.b;
        boolean launcherOff = gamepad2.a;

        if(launcherOnLowRPM){
            baselauncherRPM = 2950;
            rampSpiral.setPosition(rampSpiralHigh);
            launcher.setLauncherState(LauncherSM_WITHPDIF.LauncherState.REVVING_UP);

        }
        else if (launcherOnMediumRPM){
            baselauncherRPM = 2790;
            rampSpiral.setPosition(rampSpiralHigh);
            launcher.setLauncherState(LauncherSM_WITHPDIF.LauncherState.REVVING_UP);
        }
        else if (launcherOnHighRPM){
            baselauncherRPM = 3600;
            rampSpiral.setPosition(rampSpiralLow);
            launcher.setLauncherState(LauncherSM_WITHPDIF.LauncherState.REVVING_UP);
        }

        else if (launcherOff) {
            launcher.setLauncherState(LauncherSM_WITHPDIF.LauncherState.IDLE);
        }

        //adjusting launcher rpm for artifacts 2 & 3
        if (gateTime.seconds() <=0.8) {
            launcherRPM = baselauncherRPM;
        }

        if (gateTime.seconds() > 0.8 && baselauncherRPM > 0){
            launcherRPM = baselauncherRPM *1; //was 0.9
        }





        /// //////////////////////////////////ARTIFACT RUMBLE////////////////////////////

        ArtifactDetectionSM.ArtifactDetectionState currentState = artifacts.getArtifactState();

        //if currently we had 3 artifacts and last loop we did not have 3 artifacts
        if (currentState == ArtifactDetectionSM.ArtifactDetectionState.THREE_ARTIFACTS_COLLECTED &&
                threeArtifactsCollected == false){

            gamepad1.rumble(500);
            gamepad2.rumble(500);

            threeArtifactsCollected = true;
        }

        //Helper if statement for rumble logic so it resets every time we stop intaking
        if (intake.getIntakeState() == IntakeSM.IntakeState.INTAKING) {
            threeArtifactsCollected = false;
        }

        ///////////////////////////UPDATE STATE MACHINES//////////////////////////////

        // Update state machine each loop
        ramp.update();
        gate.update();
        artifacts.update();
        launcher.update(launcherRPM);
        kickstand.update(upButtons, downButtons);

        /// control intake with artifact # limitation: will not be allowed if 3 artifacts detected
        intake.update(intakeButton, outtakeButton,artifacts.isIntakeAllowed());

        ////////////////////////TELEMETRY//////////////////////////////////////////

        telemetry.addData("Actual RPM", (launcher.leftFlyWheel.getVelocity()*60/launcher.TICKS_PER_MOTOR_REV));
        telemetry.addData("Target RPM", launcherRPM);
        telemetry.addData("detection state",artifacts.getArtifactState());
        telemetry.update();





}/////end of peripheral move////////
    @Override
    public void stop() {
        intake.stop(); // safely stop motors
        ramp.stop();
        launcher.stop();
        kickstand.stop();
    }


} //end of opmode






