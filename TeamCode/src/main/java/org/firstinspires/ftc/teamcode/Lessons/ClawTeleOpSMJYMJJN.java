package org.firstinspires.ftc.teamcode.Lessons;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class ClawTeleOpSMJYMJJN extends OpMode {

    private final ClawSM_JYJNMJ servo1 = new ClawSM_JYJNMJ();
    private final ClawSM_JYJNMJ servo2 = new ClawSM_JYJNMJ();

    @Override
    public void init() {
        servo1.init(hardwareMap);
        servo2.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad1.x) {
            servo1.setState(ClawSM_JYJNMJ.MotorState.OPEN);
            servo2.setState(ClawSM_JYJNMJ.MotorState.OPEN);
        } else if (gamepad1.y) {
            servo1.setState(ClawSM_JYJNMJ.MotorState.CLOSED);
            servo2.setState(ClawSM_JYJNMJ.MotorState.CLOSED);
        } else {
            servo1.setState(ClawSM_JYJNMJ.MotorState.OPEN);
            servo2.setState(ClawSM_JYJNMJ.MotorState.OPEN);
        }


        servo1.update();
        servo2.update();

        telemetry.addData("Current Motor State", servo1.getState());
        telemetry.addData("Current Motor State", servo2.getState());
        telemetry.update();
    }

    @Override
    public void stop() {
        servo1.stop();
        servo2.stop();
    }
}