package org.firstinspires.ftc.teamcode.MsPacman.StateMachines;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RampSM {

    // --- 1. Define all possible states ---
    public enum RampState {
        IDLE,           // motor/servo stopped
        INTAKING_ZERO,      /// zero artifacts collected
        INTAKING_ONE,        ///  one artifact collected at top
        INTAKING_TWO,   // two artifacts collected middle/top
        INTAKING_THREE,  //three artifacts collected
        MANUAL_UP, /// manually moving ramp servos up
        MANUAL_DOWN, ///  manually moving ramp servos down
    }

    // --- 2. Hardware members ---

    /// ///ramp/////
    private CRServo topRampServo; //top of the ramp
    private CRServo middleTopRampServo;
    private CRServo middleBottomRampServo;
    private CRServo bottomRampServo; //bottom of the ramp      // replace or add more hardware as needed

    /// ///sensor detection////////



    // --- 3. Constants (adjust as needed) ---
    private final double RAMP_UP_POWER = -1.0; //negative power moves wheels up

    private final double RAMP_DOWN_POWER = 1.0; //positive power moves wheels down

    // --- 4. State variables ---
    private RampState rampState = RampState.IDLE;

    // --- 5. Initialization ---
    /**
     * Call this in TeleOp init() to initialize hardware.
     */
    public void init(HardwareMap hardwareMap) {
        //ramp//
        topRampServo = hardwareMap.get(CRServo.class, "fourth_ramp_servo");
        middleTopRampServo = hardwareMap.get(CRServo.class, "third_ramp_servo");
        middleBottomRampServo = hardwareMap.get(CRServo.class, "second_ramp_servo");
        bottomRampServo = hardwareMap.get(CRServo.class, "first_ramp_servo");


    }

    // --- 6. Getter & Setter Methods for state ---
   //getter gets the state
    public RampState getRampState() {

        return rampState;
    }
    //setter sets the state
    public void setRampState(RampState newState){

        rampState = newState;
    }


    // --- 7. Optional: stop() method for TeleOp end ---
    public void stop() {
        topRampServo.setPower(0);
        middleTopRampServo.setPower(0);
        middleBottomRampServo.setPower(0);
        bottomRampServo.setPower(0);
        rampState = RampState.IDLE;
    }


    // --- 8. Update logic ---
    /**
     * Call this every loop() to handle state transitions and motor control
     */


    public void update() {
        // --- 6a. Determine state based on inputs and current state ---


        // --- 6b. Act based on current state ---
        switch (rampState) {
            case INTAKING_ZERO:
                topRampServo.setPower(RAMP_UP_POWER);
                middleTopRampServo.setPower(RAMP_UP_POWER);
                middleBottomRampServo.setPower(RAMP_UP_POWER);
                bottomRampServo.setPower(RAMP_UP_POWER);
                break;
            case INTAKING_ONE:
                topRampServo.setPower(0);
                middleTopRampServo.setPower(0);
                middleBottomRampServo.setPower(RAMP_UP_POWER);
                bottomRampServo.setPower(RAMP_UP_POWER);
                break;
            case INTAKING_TWO:
                topRampServo.setPower(0);
                middleTopRampServo.setPower(0);
                middleBottomRampServo.setPower(0);
                bottomRampServo.setPower(RAMP_UP_POWER);
                break;
            case INTAKING_THREE:
                topRampServo.setPower(0);
                middleTopRampServo.setPower(0);
                middleBottomRampServo.setPower(0);
                bottomRampServo.setPower(0);
                break;
            case MANUAL_UP:
                topRampServo.setPower(RAMP_UP_POWER);
                middleTopRampServo.setPower(RAMP_UP_POWER);
                middleBottomRampServo.setPower(RAMP_UP_POWER);
                bottomRampServo.setPower(RAMP_UP_POWER);
                break;
            case MANUAL_DOWN:
                topRampServo.setPower(RAMP_DOWN_POWER);
                middleTopRampServo.setPower(RAMP_DOWN_POWER);
                middleBottomRampServo.setPower(RAMP_DOWN_POWER);
                bottomRampServo.setPower(RAMP_DOWN_POWER);
                break;
            case IDLE:
                topRampServo.setPower(0);
                middleTopRampServo.setPower(0);
                middleBottomRampServo.setPower(0);
                bottomRampServo.setPower(0);
                break;
        }
    }



}

