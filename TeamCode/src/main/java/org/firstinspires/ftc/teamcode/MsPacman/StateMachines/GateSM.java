package org.firstinspires.ftc.teamcode.MsPacman.StateMachines;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class GateSM {

    // --- 1. Define all possible states ---
    public enum GateState {
        CLOSED,
        OPENED
    }

    // --- 2. Hardware members ---
    private Servo gate;
    // --- 3. Constants (adjust as needed) ---
    private final double OPENED_POSITION = 0.1; //when gate is opened

    private final double CLOSED_POSITION = 0.35; //When gate is closed

    // --- 4. State variables ---
    private GateState gateState = GateState.CLOSED;

    // --- 5. Initialization ---
    /**
     * Call this in TeleOp init() to initialize hardware.
     */
    public void init(HardwareMap hardwareMap) {
        gate = hardwareMap.get(Servo.class, "launch_gate");
    }

    // --- 6. Getter & Setter Methods for state ---
   //getter gets the state
    public GateState getGateState() {

        return gateState;
    }
    //setter sets the state
    public void setGateState(GateState newState){

        gateState = newState;
    }


    // --- 7. Optional: stop() method for TeleOp end ---
    public void stop() {
        gate.setPosition(CLOSED_POSITION);
    }


    // --- 8. Update logic ---
    /**
     * Call this every loop() to handle state transitions and motor control
     */


    public void update() {
        // --- 6a. Determine state based on inputs and current state ---

        // --- 6b. Act based on current state ---
        switch (gateState) {
            case OPENED:
                gate.setPosition(OPENED_POSITION);
                break;
            case CLOSED:
                gate.setPosition(CLOSED_POSITION);
                break;
        }
    }



}

