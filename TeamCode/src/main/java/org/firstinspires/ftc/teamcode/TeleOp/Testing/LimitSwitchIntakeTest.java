package org.firstinspires.ftc.teamcode.TeleOp.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp(name = "Limit Switch Stop Test", group = "Testing")
public class LimitSwitchIntakeTest extends OpMode {
    private final IntakeSMJYMJJN intake = new IntakeSMJYMJJN();

    private TouchSensor magnet;

    private int magnetCount = 0;

    boolean magnetStateLastLoop = false;


    @Override
    public void init() {
        magnet = hardwareMap.get(TouchSensor.class, "limit_switch");
        intake.init(hardwareMap);


    }

    @Override
    public void loop() {
        boolean magnetStateRightNow = magnet.isPressed();

        if (magnetStateRightNow == true && magnetStateLastLoop == false) {
            magnetCount += 1;
        }
        magnetStateLastLoop = magnetStateRightNow;


        if (gamepad1.right_bumper) {

            if (magnetCount < 4) {
                intake.setState(IntakeSMJYMJJN.MotorState.INTAKE);
            } else if (magnetCount >= 4) {
                intake.setState(IntakeSMJYMJJN.MotorState.IDLE);
                telemetry.addLine("Maximum amount of balls (Intake Stops)");
                telemetry.update();
            } else {
                intake.setState(IntakeSMJYMJJN.MotorState.IDLE);

            }

        }
            if (gamepad1.a) {
                magnetCount = 0;
                telemetry.addLine("Magnet Count Reset (Intake Starts)");
                telemetry.update();
            }

            //update state machines
            intake.update();

            telemetry.addData("Magnet Count", magnetCount);
            telemetry.update();

        }
    }

