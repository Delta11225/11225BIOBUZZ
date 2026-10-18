package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp
public class FlyWheelTESTBREADON extends OpMode {
        public DcMotorEx FlyWheel;
        double firstLaunchRPM = 1400;
        double flyWheelRPM=1400;

        boolean lastDpadRightPressed = false;
        boolean lastDpadLeftPressed = false;

        public double F = 13;
        public double P = 10;

        @Override
        public void init() {
            FlyWheel = hardwareMap.get(DcMotorEx.class, "flywheel");

            FlyWheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

            FlyWheel.setDirection(DcMotorEx.Direction.FORWARD);

            PIDFCoefficients PIDF = new PIDFCoefficients(
                    P, //P - Corrects based on how far the actual velocity is from the target velocity
                    0, //I - not nessecary for launch motors
                    0, //D - not nessecary fro launch motors
                    F //F - Baseline velocity coefficient (usually 1) Ex: (1 x 3300)
            );

            FlyWheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, PIDF);



        }
    @Override
         public void init_loop() {
            FlyWheel.setVelocity(flyWheelRPM);

    }

        @Override
        public void loop() {
/*

            //flywheel PIDF code
            PIDFCoefficients PIDF = new PIDFCoefficients(
                    P, //P - Corrects based on how far the actual velocity is from the target velocity
                    0, //I - not nessecary for launch motors
                    0, //D - not nessecary fro launch motors
                    F //F - Baseline velocity coefficient (usually 1) Ex: (1 x 3300)
            );

            FlyWheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, PIDF);


            ////launcher flywheels
            if (gamepad1.dpad_right&& !lastDpadRightPressed){
                firstLaunchRPM+=100;
            }

            if (gamepad1.dpad_left && !lastDpadLeftPressed){
                firstLaunchRPM-=100;
            }

            lastDpadRightPressed=gamepad1.dpad_right;
            lastDpadLeftPressed = gamepad1.dpad_left;


            if (gamepad2.y) {
                flyWheelRPM= firstLaunchRPM;
                telemetry.addData("motor RPM", firstLaunchRPM);

            } else if (gamepad1.b) {
                flyWheelRPM= 0;
                telemetry.addLine("none");
            }


            double TICKS_PER_MOTOR_REV = 28;
            double FLYWHEEL_TARGET_RPM = flyWheelRPM;
            double FLYWHEEL_TARGET_TPS = (FLYWHEEL_TARGET_RPM/60.0)*TICKS_PER_MOTOR_REV;

            FlyWheel.setVelocity(FLYWHEEL_TARGET_TPS);

            // telemetry.addData("leftFlyWheel TPS", leftFlyWheel.getVelocity());
            // telemetry.addData("Target TPS", FLYWHEEL_TARGET_TPS);
            telemetry.addData("leftFlyWheel RPM", (FlyWheel.getVelocity()*60/TICKS_PER_MOTOR_REV));
            telemetry.addData("current RPM", flyWheelRPM);
*/

                // Adjust target RPM with D-pad
                if (gamepad1.dpad_right && !lastDpadRightPressed) {
                    firstLaunchRPM += 10;
                }

                if (gamepad1.dpad_left && !lastDpadLeftPressed) {
                    firstLaunchRPM -= 10;
                }

                lastDpadRightPressed = gamepad1.dpad_right;
                lastDpadLeftPressed = gamepad1.dpad_left;

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
                telemetry.update();
            }

        }


