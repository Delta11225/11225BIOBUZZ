package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp
public class Touch_Sensor_Test_JN extends OpMode{
    TouchSensor touch;
    public DcMotor motor1 = null;
    public void init (){
        touch = hardwareMap.get(TouchSensor.class, "touch");
       motor1 = hardwareMap.get(DcMotor.class,"motor_1");
    }
    public void loop (){
        telemetry.addLine("Programming is fun!");
        if (touch.isPressed()) {
            telemetry.addData("Touch Sensor", "PRESSED");
            motor1.setPower(0.75);
        } else{
            telemetry.addData("Touch Sensor", "NOT PRESSED");
            motor1.setPower(0);
        }
    }
}