package org.firstinspires.ftc.teamcode.pedro.Subsystems;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.CommandBuilder;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Intake {

    private DcMotor intake;

    public ElapsedTime intakeTime = new ElapsedTime();

    public Intake(HardwareMap hardwareMap){
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setDirection(DcMotor.Direction.FORWARD);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public Command intakeSeconds(double intakeDuration){
        return new CommandBuilder()
                .requiring(intake)
                .setStart(() -> {
                    intakeTime.reset();
                })
                .setExecute(() -> {
                    intake.setPower(1);
                })
                .setDone(() -> {
                    return intakeTime.seconds() >= intakeDuration;
                })
                .setEnd((endCondition) -> {
                    intake.setPower(0);
                });
    }
}
