package org.firstinspires.ftc.teamcode.TeleOp.StateMachines;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class MechDriveSM {
    // Hardware elements
    private DcMotor rearLeft, rearRight, frontLeft, frontRight;
    private GoBildaPinpointDriver odo;

    // State Machine definition
    public enum DriveState {
        NORMAL,
        SLOW,
        FAST
    }
    private DriveState currentState = DriveState.NORMAL;
    private double powerMultiplier = 0.6;

    // Initialization
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        // Initialize Pinpoint Odometry Computer
        odo = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        odo.setOffsets(-84.0, -168.0, DistanceUnit.MM);
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        odo.resetPosAndIMU();

        // Initialize Drive Motors
        rearLeft = hardwareMap.dcMotor.get("leftBack");
        frontLeft = hardwareMap.dcMotor.get("leftFront");
        frontRight = hardwareMap.dcMotor.get("rightFront");
        rearRight = hardwareMap.dcMotor.get("rightBack");

        rearLeft.setDirection(DcMotor.Direction.REVERSE);
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        rearRight.setDirection(DcMotor.Direction.FORWARD);

        rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        stopMotors();
    }

    public void resetOdo() {
        odo.resetPosAndIMU();
    }

    // Main periodic update method (Call this in your TeleOp loop)
    public void update(Gamepad gamepad, Telemetry telemetry) {
        // 1. Update hardware heading
        odo.update(GoBildaPinpointDriver.ReadData.ONLY_UPDATE_HEADING);
        Pose2D pos = odo.getPosition();
        double currentAngle = pos.getHeading(AngleUnit.DEGREES);
        double theta = Math.toRadians(currentAngle);

        // 2. State Machine: Determine Speed State based on inputs
        if (gamepad.left_bumper) {
            currentState = DriveState.FAST;
            powerMultiplier = 0.8;
        } else if (gamepad.right_bumper) {
            currentState = DriveState.SLOW;
            powerMultiplier = 0.3;
        } else {
            currentState = DriveState.NORMAL;
            powerMultiplier = 0.6;
        }

        // 3. Vector math for field centric driving
        double forward = gamepad.left_stick_y;
        double right = -gamepad.left_stick_x * 1.1;
        double clockwise = gamepad.right_stick_x;

        double temp = (forward * Math.cos(theta) - right * Math.sin(theta));
        double side = (forward * Math.sin(theta) + right * Math.cos(theta));
        forward = temp;
        right = side;

        double denominator = Math.max(Math.abs(forward) + Math.abs(right) + Math.abs(clockwise), 1);
        double frontLeftV = (forward + right + clockwise) / denominator;
        double rearLeftV = (forward - right + clockwise) / denominator;
        double rearRightV = (forward + right - clockwise) / denominator;
        double frontRightV = (forward - right - clockwise) / denominator;

        // 4. Set powers using the newly updated multiplier
        frontLeft.setPower(frontLeftV * powerMultiplier);
        frontRight.setPower(frontRightV * powerMultiplier);
        rearLeft.setPower(rearLeftV * powerMultiplier);
        rearRight.setPower(rearRightV * powerMultiplier);

        // 5. Output localized Telemetry
        telemetry.addData("Drive State", currentState);
        telemetry.addData("Heading", currentAngle);
    }

    public void stopMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        rearLeft.setPower(0);
        rearRight.setPower(0);
    }
}
