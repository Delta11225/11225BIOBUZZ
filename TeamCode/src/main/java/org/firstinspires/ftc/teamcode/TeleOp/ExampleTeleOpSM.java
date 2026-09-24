package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class ExampleTeleOpSM extends OpMode {

    private ExampleSM motor = new ExampleSM();

    @Override
    public void init() {
        motor.init(hardwareMap);
    }

    @Override
    public void loop() {
        //Handle your controller logic using the setter methods we created in the state machine
        if(gamepad1.right_bumper){
            motor.setState(ExampleSM.MotorState.FORWARD);
        }
        else if (gamepad1.left_bumper) {
            motor.setState(ExampleSM.MotorState.BACKWARD);
        }
        else{
            motor.setState(ExampleSM.MotorState.IDLE);
        }

        // Update state machine each loop
        motor.update();

        //Optional Telemetry
        telemetry.addData("Current Motor State", motor.getState());
        telemetry.update();
    }

    @Override
    public void stop() {
        motor.stop();
    }
}
