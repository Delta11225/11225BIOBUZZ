package org.firstinspires.ftc.teamcode.TeleOp.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp
public class LimitSwitchIntakeTest extends OpMode {

    private TouchSensor magnet;
    private int magnetCount = 0;

    boolean magnetStateLastLoop = false;


    @Override
    public void init(){
        magnet = hardwareMap.get(TouchSensor.class, "limit_switch");

    }
    @Override
    public void loop(){
        telemetry.addData("Presses", magnetCount);
        boolean magnetStateRightNow = magnet.isPressed();

        if (magnetStateRightNow == true && magnetStateLastLoop == false){
            magnetCount += 1;
        }
        magnetStateLastLoop = magnetStateRightNow;

        telemetry.addData("Total Magnet Passes", magnetCount);
        telemetry.addData("Is Magnet Present Now?", magnetStateRightNow);
        telemetry.update();
    }
}
