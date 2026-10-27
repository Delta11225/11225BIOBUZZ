package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class PIDFTuner extends OpMode {
        public DcMotorEx FlyWheel;
        public Servo gate;
        double firstLaunchRPM = 2250;
        double flyWheelRPM = 2250;
        double pChange = 0;
        double fChange = 0;
        double dChange = 0;

        boolean lastDpadRightPressed = false;
        boolean lastDpadLeftPressed = false;
        boolean lastDpadUpPressed = false;
        boolean lastDpadDownPressed = false;
        boolean lastLeftBumperPressed = false;
        boolean lastRightBumperPressed = false;
        boolean lastXButtonPressed = false;

        public double F = 13.7;
        public double P = 100;
        public double D = 0.03;

        @Override
        public void init() {
            FlyWheel = hardwareMap.get(DcMotorEx.class, "flywheel");
            gate = hardwareMap.get(Servo.class, "gate");

            FlyWheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

            FlyWheel.setDirection(DcMotorEx.Direction.FORWARD);

            PIDFCoefficients PIDF = new PIDFCoefficients(
                    P, //P - Corrects based on how far the actual velocity is from the target velocity
                    0, //I - not nessecary for launch motors
                    D, //D - not nessecary fro launch motors
                    F //F - Baseline velocity coefficient (usually 1) Ex: (1 x 3300)
            );

            FlyWheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, PIDF);
            gate.setPosition(0.1);



        }

        @Override
        public void loop() {


            //flywheel PIDF code
            PIDFCoefficients PIDF = new PIDFCoefficients(
                    P, //P - Corrects based on how far the actual velocity is from the target velocity
                    0, //I - not nessecary for launch motors
                    D, //D - not nessecary fro launch motors
                    F //F - Baseline velocity coefficient (usually 1) Ex: (1 x 3300)
            );

            FlyWheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, PIDF);

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
                if(gamepad1.x&&!lastXButtonPressed){
                    dChange +=0.01;
                }
                lastDpadRightPressed = gamepad1.dpad_right;
                lastDpadLeftPressed = gamepad1.dpad_left;
                lastDpadUpPressed = gamepad1.dpad_up;
                lastDpadDownPressed = gamepad1.dpad_down;
                lastLeftBumperPressed=gamepad1.left_bumper;
                lastRightBumperPressed=gamepad1.right_bumper;
                lastXButtonPressed =gamepad1.x;

                // Set target RPM
                if (gamepad1.y) {
                    flyWheelRPM = firstLaunchRPM;
                    P=pChange;
                    F=fChange;
                    D=dChange;
                } else if (gamepad1.b) {
                    flyWheelRPM = 0;
                }
                if(gamepad1.a){
                    gate.setPosition(0.5);
                }
                else{
                    gate.setPosition(0.1);
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
                telemetry.addData("pChange",pChange);
                telemetry.addData("P actual", P);
                telemetry.addData("fChange",fChange);
                telemetry.addData("F actual", F);
                telemetry.addData("D Actual", D);
                telemetry.addData("dChange", dChange);
                telemetry.addData("Current RPM", (FlyWheel.getVelocity()*60/TICKS_PER_MOTOR_REV));

                telemetry.update();
            }

        }


