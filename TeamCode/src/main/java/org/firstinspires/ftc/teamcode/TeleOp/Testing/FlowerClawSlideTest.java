package org.firstinspires.ftc.teamcode.TeleOp.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class FlowerClawSlideTest extends OpMode {

    public DcMotor slide;
    public Servo claw;

    @Override
    public void init() {
        slide = hardwareMap.get(DcMotor.class, "slide");
        slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slide.setDirection(DcMotor.Direction.FORWARD);

        claw = hardwareMap.get(Servo.class, "claw");
        claw.setPosition(0.35); //OPEN
    }

    @Override
    public void loop() {
        if(gamepad1.right_bumper){
            claw.setPosition(0.9); //CLOSED
        }
        if(gamepad1.left_bumper){
            claw.setPosition(0.35); //OPEN
        }

        if(gamepad1.x && slide.getCurrentPosition() < 850){
            slide.setPower(0.4);
        }
        else{
            slide.setPower(0);
        }


        telemetry.addData("encoder", slide.getCurrentPosition());
        telemetry.update();
    }
}
