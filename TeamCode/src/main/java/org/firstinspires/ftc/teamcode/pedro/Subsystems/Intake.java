package org.firstinspires.ftc.teamcode.pedro.Subsystems;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.CommandBuilder;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Intake {

    private final DcMotor intake;
    private final CRServo LIntakeWheel;
    private final CRServo RIntakeWheel;
    public ElapsedTime intakeTime = new ElapsedTime();

    public Intake(HardwareMap hardwareMap){
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setDirection(DcMotor.Direction.FORWARD);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        LIntakeWheel = hardwareMap.get(CRServo.class, "l_intake_wheel");
        RIntakeWheel = hardwareMap.get(CRServo.class, "r_intake_wheel");
    }

    public Command intakeSeconds(double intakeDuration){
        return new CommandBuilder()
                .requiring(intake, LIntakeWheel, RIntakeWheel)
                .setStart(() -> {
                    intakeTime.reset();
                })
                .setExecute(() -> {
                    intake.setPower(1);
                    LIntakeWheel.setPower(-1);
                    RIntakeWheel.setPower(1);
                })
                .setDone(() -> {
                    return intakeTime.seconds() >= intakeDuration;
                })
                .setEnd((endCondition) -> {
                    intake.setPower(0);
                    LIntakeWheel.setPower(0);
                    RIntakeWheel.setPower(0);
                });
    }
}
