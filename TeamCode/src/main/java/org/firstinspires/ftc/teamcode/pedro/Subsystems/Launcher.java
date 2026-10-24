package org.firstinspires.ftc.teamcode.pedro.Subsystems;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.CommandBuilder;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Launcher {
    private final DcMotorEx flywheel;
    private final CRServo windmill;
    private final DcMotor intake;
    public ElapsedTime launchTime = new ElapsedTime();
    public final double TICKS_PER_MOTOR_REV = 28;
    public double RPMTolerance = 200;
    public double F = 12.5;
    public double P = 40;

    public Launcher(HardwareMap hardwareMap){
        PIDFCoefficients PIDF = new PIDFCoefficients(
                P, //P - Corrects based on how far the actual velocity is from the target velocity
                0, //I - not necessary for launch motors
                0, //D - not necessary for launch motors
                F //F - Baseline velocity coefficient (usually 1) Ex: (1 x 3300)
        );

        flywheel = hardwareMap.get(DcMotorEx.class, "launcher");
        flywheel.setDirection(DcMotor.Direction.FORWARD);
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, PIDF);
        flywheel.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setDirection(DcMotor.Direction.FORWARD);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        windmill = hardwareMap.get(CRServo.class, "windmill");
    }

    public Command launchSeconds(double launchDuration, double launchRPM){
        return new CommandBuilder()
                .requiring(flywheel, intake, windmill)
                .setStart(() -> {
                    launchTime.reset();
                })
                .setExecute(() -> {
                    double currentFlywheelRPM = (flywheel.getVelocity()*60/TICKS_PER_MOTOR_REV);
                    flywheel.setVelocity((launchRPM/60)*TICKS_PER_MOTOR_REV);

                    if(Math.abs(currentFlywheelRPM-launchRPM)<RPMTolerance){
                        intake.setPower(1);
                        windmill.setPower(0.75);
                    }
                })
                .setDone(() -> {
                    return launchTime.seconds() >= launchDuration;
                })
                .setEnd((endCondition) -> {
                    intake.setPower(0);
                    windmill.setPower(0);
                    flywheel.setVelocity(0);
                });
    }
}
