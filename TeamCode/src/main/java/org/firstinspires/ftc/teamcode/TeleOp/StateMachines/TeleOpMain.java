package org.firstinspires.ftc.teamcode.TeleOp.StateMachines;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="TeleOp Main")
public class TeleOpMain extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    // Instantiate your state machines here
    private MechDriveSM drive = new MechDriveSM();
    // private LiftStateMachine lift = new LiftStateMachine(); // Example for future state machines
    // private IntakeStateMachine intake = new IntakeStateMachine();

    @Override
    public void init() {
        // Initialize the drive system
        drive.init(hardwareMap, telemetry);

        // Initialize other subsystems here
        // lift.init(hardwareMap);
        // intake.init(hardwareMap);

        telemetry.addData("Status", "Central TeleOp Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        runtime.reset();
        drive.resetOdo();
    }

    @Override
    public void loop() {
        // Run the drive state machine
        drive.update(gamepad1, telemetry);

        // Drop your future subsystem state machine updates right below
        // lift.update(gamepad1, telemetry);
        // intake.update(gamepad1, telemetry);

        // Central global telemetry data
        telemetry.addData("Runtime", runtime.toString());
        telemetry.update();
    }

    @Override
    public void stop() {
        drive.stopMotors();
    }
}
