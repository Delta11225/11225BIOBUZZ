package org.firstinspires.ftc.teamcode.TeleOp.StateMachines;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="TeleOp Main")
public class TeleOpMain extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    // Instantiate your state machines here
        // Instantiate = creating an instance (object) from a class.
        // MechDriveSM is the class/blueprint.
        // new MechDriveSM() creates an instance of that class.
        // drive is the object refers to that newly created instance.
        // Notice we will use drive. to access things from MechDriveSM
    private MechDriveSM drive = new MechDriveSM();
    //add other State Machines here as you create them

    @Override
    public void init() {
        // Initialize the drive system
        drive.init(hardwareMap, telemetry);
        // Initialize other subsystems here

        telemetry.addData("Status", "TeleOp Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {
        //put things here that you need constantly updating in init (like a camera stream)
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

        // Put subsystem state machine updates here


        // Central global telemetry data
        telemetry.addData("Runtime", runtime.toString());
        telemetry.update();
    }

    @Override
    public void stop() {
        drive.stopMotors();
    }
}
