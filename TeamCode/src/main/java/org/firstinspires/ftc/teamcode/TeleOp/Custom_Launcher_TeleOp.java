package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Custom_Launcher_TeleOp extends OpMode {
    private final Custom_Launcher_SM_JN launcher = new Custom_Launcher_SM_JN();

    @Override
    public void init() {
        launcher.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad1.xWasPressed()) {
            launcher.revPressed();
        }
        if (gamepad1.yWasPressed()) {
            launcher.launchPressed();
        }

        launcher.update(Custom_Launcher_SM_JN.TARGET_RPM);

        telemetry.addData("State", launcher.getLaunchState());
        telemetry.addData("RPM", launcher.getRPM());


    }

    @Override
    public void stop() {
        launcher.stop();
    }
}
