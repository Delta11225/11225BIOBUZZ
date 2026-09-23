package org.firstinspires.ftc.teamcode.MsPacman.StateMachines;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeSM {

    // --- 1. Define all possible states ---
    public enum IntakeState {
        IDLE,           // motor/servo stopped
        INTAKING,        // normal operation
        OUTTAKING       // optional reverse mode
    }

    // --- 2. Hardware members ---
    private DcMotor intake;       // replace or add more hardware as needed

    // --- 3. Constants (adjust as needed) ---
    private final double INTAKE_POWER = -1.0;
    private final double OUTTAKE_POWER = 1.0;

    // --- 4. State variables ---
    private IntakeState intakeState = IntakeState.IDLE;

    // --- 5. Initialization ---
    /**
     * Call this in TeleOp init() to initialize hardware.
     */
    public void init(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // --- 6. Getter & Setter Mehods for state ---
   //getter gets the state
    public IntakeState getIntakeState() {
        return intakeState;
    }
    //setter sets the state
    public void setIntakeState(IntakeState newState){
        intakeState = newState;
    }


    // --- 7. Optional: stop() method for TeleOp end ---
    public void stop() {
        intake.setPower(0);
        intakeState = IntakeState.IDLE;
    }


    // --- 8. Update logic ---
    /**
     * Call this every loop() to handle state transitions and motor control.
     *
     * @param intakeButton boolean input for starting the motor
     * @param outtakeButton boolean input for reversing motor (optional)
     */
    public void update(boolean intakeButton, boolean outtakeButton, boolean allowed) {

        if(!allowed){
            intake.setPower(0);
            intakeState = IntakeState.IDLE;
            return;
        }

        // --- 6a. Determine state based on inputs and current state ---
        if (intakeButton && !outtakeButton) {
            intakeState = IntakeState.INTAKING;
        } else if (outtakeButton && !intakeButton) {
            intakeState = IntakeState.OUTTAKING;
        } else {
            intakeState = IntakeState.IDLE;
        }

        // --- 6b. Act based on current state ---
        switch (intakeState) {
            case INTAKING:
                intake.setPower(INTAKE_POWER);
                break;
            case OUTTAKING:
                intake.setPower(OUTTAKE_POWER);
                break;
            case IDLE:
                intake.setPower(0);
                break;
        }
    }



}

