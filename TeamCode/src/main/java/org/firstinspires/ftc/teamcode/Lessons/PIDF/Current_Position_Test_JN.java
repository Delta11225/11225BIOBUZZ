package org.firstinspires.ftc.teamcode.Lessons.PIDF;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@TeleOp
public class Current_Position_Test_JN extends OpMode {
    public DcMotor motor1 = null;
    @Override
    public void init() {
        motor1 = hardwareMap.get(DcMotor.class, "motor1");
        motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {
        telemetry.addData("Position (ticks)", motor1.getCurrentPosition());
    }
}
