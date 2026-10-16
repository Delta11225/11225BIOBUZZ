package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.TeleOp.ExampleSM;

@TeleOp
public class ExampleTeleOpSMMJJ extends OpMode {

    private final EXAMPLE_SM_MJJ motor = new EXAMPLE_SM_MJJ();

    @Override
    public void init() {
        motor.init(hardwareMap);
    }

    @Override
    public void loop() {
        if(gamepad1.right_bumper){
            motor.setState(EXAMPLE_SM_MJJ.MotorState.FORWARD);
        }
        else if (gamepad1.left_bumper){
            motor.setState(EXAMPLE_SM_MJJ.MotorState.BACKWARD);
        }
        else {
            motor.setState(EXAMPLE_SM_MJJ.MotorState.IDLE);
        }
        motor.update();
    }
}
