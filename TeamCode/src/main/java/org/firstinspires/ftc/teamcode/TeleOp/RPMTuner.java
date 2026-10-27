package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
public class RPMTuner extends OpMode {
        public DcMotorEx FlyWheel;
        double firstLaunchRPM = 2250;
        double flyWheelRPM = 2250;
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

            FlyWheel.setDirection(DcMotorEx.Direction.FORWARD);
        }

        @Override
        public void loop() {


            //flywheel PIDF code

                // Adjust target RPM with D-pad
                if (gamepad1.dpad_right && !lastDpadRightPressed) {
                    firstLaunchRPM += 10;
                }

                if (gamepad1.dpad_left && !lastDpadLeftPressed) {
                    firstLaunchRPM -= 10;
                }

                if(gamepad1.dpad_up && !lastDpadUpPressed){

                }
                if(gamepad1.dpad_down && !lastDpadDownPressed){

                }
                if(gamepad1.left_bumper&&!lastLeftBumperPressed){

                }
                if(gamepad1.right_bumper&&!lastRightBumperPressed){

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


