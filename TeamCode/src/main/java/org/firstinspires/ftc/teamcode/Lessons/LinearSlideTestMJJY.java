package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class LinearSlideTestMJJY extends OpMode {
    private DcMotor linearSlide;
    @Override
    public void init(){
        linearSlide = hardwareMap.get(DcMotor.class, "linear_slide");
        linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop(){
        if ((gamepad1.dpad_up) && (linearSlide.getCurrentPosition() < 1842)){
            linearSlide.setPower(0.5);
        }
        else if ((gamepad1.dpad_down) && (linearSlide.getCurrentPosition() > 0)){
            linearSlide.setPower(-0.5);
        }
        else{
            linearSlide.setPower(0.0);
        }
        telemetry.addData("encoder", linearSlide.getCurrentPosition());
    }
}
