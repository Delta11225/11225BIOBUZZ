package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp
public class FlyWheel_RPMTuner_JN extends OpMode {
        public DcMotorEx FlyWheel;
        double firstLaunchRPM = 1400;
        double flyWheelRPM=1400;
        double pChange = 5;
        double fChange = 0;

        boolean lastDpadRightPressed = false;
        boolean lastDpadLeftPressed = false;
        boolean lastDpadUpPressed = false;
        boolean lastDpadDownPressed = false;
        boolean lastLeftBumperPressed = false;
        boolean lastRightBumperPressed = false;


        @Override
        public void init() {
            FlyWheel = hardwareMap.get(DcMotorEx.class, "flywheel");

            FlyWheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

            FlyWheel.setDirection(DcMotorEx.Direction.REVERSE);

        }

        @Override
        public void loop() {

                // Adjust target RPM with D-pad
                if (gamepad1.dpad_right && !lastDpadRightPressed) {
                    firstLaunchRPM += 10;
                }

                if (gamepad1.dpad_left && !lastDpadLeftPressed) {
                    firstLaunchRPM -= 10;
                }

                if(gamepad1.dpad_up && !lastDpadUpPressed){
                    pChange +=0.1;
                }
                if(gamepad1.dpad_down && !lastDpadDownPressed){
                    pChange -=0.1;
                }
                if(gamepad1.left_bumper&&!lastLeftBumperPressed){
                    fChange -= 0.1;
                }
                if(gamepad1.right_bumper&&!lastRightBumperPressed){
                    fChange +=0.1;
                }
                lastDpadRightPressed = gamepad1.dpad_right;
                lastDpadLeftPressed = gamepad1.dpad_left;
                lastDpadUpPressed = gamepad1.dpad_up;
                lastDpadDownPressed = gamepad1.dpad_down;
                lastLeftBumperPressed=gamepad1.left_bumper;
                lastRightBumperPressed=gamepad1.right_bumper;

                // Set target RPM
                if (gamepad1.y) {
                    flyWheelRPM = firstLaunchRPM;
                    //P=pChange;
                   //F=fChange;
                } else if (gamepad1.b) {
                    flyWheelRPM = 0;
                }

                // Convert RPM to encoder ticks per second
                double TICKS_PER_MOTOR_REV = 28;
                double targetTPS = (flyWheelRPM / 60.0) * TICKS_PER_MOTOR_REV;

                //set flywheel velocity to target TPS
                FlyWheel.setVelocity(targetTPS);

                // Read actual motor velocity
                double actualTPS = FlyWheel.getVelocity();
                double actualRPM = actualTPS * 60.0 / TICKS_PER_MOTOR_REV;

                // Always display telemetry
                telemetry.addData("Target RPM Setting", firstLaunchRPM);
                telemetry.addData("Commanded RPM", flyWheelRPM);
                telemetry.addData("Actual RPM", "%.0f", actualRPM);
                telemetry.addData("Current RPM", (FlyWheel.getVelocity()*60/TICKS_PER_MOTOR_REV));

                telemetry.update();
            }

        }


