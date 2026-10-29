package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp
public class LinearSlideAutoTestMJJY extends OpMode {
    private DcMotor linearSlide;
    TouchSensor touch;
    private int linearSlideTarget = 0;
    @Override
    public void init(){
        linearSlide = hardwareMap.get(DcMotor.class, "linear_slide");
        linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        linearSlide.setTargetPosition(linearSlideTarget);
        linearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        touch = hardwareMap.get(TouchSensor.class, "touch");
    }

    @Override
    public void loop(){
        if (gamepad1.y) {
            linearSlideTarget = 1842;
        }
        if (gamepad1.x){
            linearSlideTarget = 921;
        }
        if (gamepad1.a){
            linearSlideTarget = 0;
        }
        if (touch.isPressed()) {
            linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            linearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            linearSlideTarget = 0;

        }

        linearSlide.setTargetPosition(linearSlideTarget);
        linearSlide.setPower(0.3);

        telemetry.addData("Encoder", linearSlide.getCurrentPosition());
        telemetry.addData("velocity", linearSlide.getPower());
    }
}
