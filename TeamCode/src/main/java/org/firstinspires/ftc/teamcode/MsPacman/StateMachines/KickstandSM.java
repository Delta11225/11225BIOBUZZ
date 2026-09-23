package org.firstinspires.ftc.teamcode.MsPacman.StateMachines;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class KickstandSM {

    // --- 1. Define all possible states ---
    public enum KickstandState {
        IDLE,           // motor/servo stopped
        DOWN,        // normal operation
        UP       // reverse mode
    }

    // --- 2. Hardware members ---
    private DcMotor kickstand;       // replace or add more hardware as needed

    // --- 3. Constants (adjust as needed) ---
    private final double KICKSTAND_POWER = 0.2;

    // --- 4. State variables ---
    private KickstandState kickstandState = KickstandState.IDLE;

    // --- 5. Initialization ---
    /**
     * Call this in TeleOp init() to initialize hardware.
     */
    public void init(HardwareMap hardwareMap) {
        kickstand = hardwareMap.get(DcMotor.class, "kickstand");
        kickstand.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        kickstand.setTargetPosition(0);
        kickstand.setDirection(DcMotor.Direction.REVERSE);
        kickstand.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        kickstand.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    // --- 6. Getter & Setter Mehods for state ---
   //getter gets the state
    public KickstandState getKickstandState() {
        return kickstandState;
    }
    //setter sets the state
    public void setKickstandState(KickstandState newState){
        kickstandState = newState;
    }


    // --- 7. Optional: stop() method for TeleOp end ---
    public void stop() {
        kickstand.setPower(0);
        kickstandState = kickstandState.IDLE;
    }


    // --- 8. Update logic ---
    /**
     * Call this every loop() to handle state transitions and motor control.
     *
     * @param downButtons boolean input for starting the motor
     * @param upButtons boolean input for reversing motor (optional)
     */
    public void update(boolean downButtons, boolean upButtons) {

        // --- 6a. Determine state based on inputs and current state ---
        if (downButtons) {
            kickstandState = kickstandState.DOWN;
            kickstand.setTargetPosition(-1500);
            kickstand.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        } else if (upButtons) {
            kickstandState = kickstandState.UP;
            kickstand.setTargetPosition(0);
            kickstand.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        }
//        else {
//            kickstandState = kickstandState.IDLE;
//        }

        // --- 6b. Act based on current state ---
        switch (kickstandState) {
            case DOWN:
                kickstand.setPower(KICKSTAND_POWER);
                break;
            case UP:
                kickstand.setPower(KICKSTAND_POWER);
                break;
            case IDLE:
                kickstand.setPower(0);
                break;
        }
    }



}

