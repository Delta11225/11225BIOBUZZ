package org.firstinspires.ftc.teamcode.MsPacman.StateMachines;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * TEMPLATE: FTC State Machine Class
 *
 * This class demonstrates the structure for writing a subsystem
 * using a state machine. It can be adapted for motors, servos, or
 * other components (e.g., flywheel launcher, intake, feeder, ramp).
 *
 * Key ideas:
 * 1. Define an enum for all possible states.
 * 2. Track the current state.
 * 3. Encapsulate hardware in the class.
 * 4. Provide an init() method to initialize hardware.
 * 5. Provide an update() method called every loop() to handle logic.
 * 6. Keep TeleOp code clean: just call update() with inputs.
 */
public class ArtifactDetectionSM {

    // --- 1. Define all possible states ---
    public enum ArtifactDetectionState {
        IDLE,           // motor/servo stopped
        ONE_ARTIFACT_COLLECTED,
        TWO_ARTIFACTS_COLLECTED,
        THREE_ARTIFACTS_COLLECTED,
        ZERO_ARTIFACTS_COLLECTED

    }
    // --- 2. Hardware members ---
    private DistanceSensor distanceSensorTop;
    private DistanceSensor distanceSensorMid;
    private DistanceSensor distanceSensorLow;

    private ColorSensor colorSensorTop;
    private ColorSensor colorSensorMid;
    private ColorSensor colorSensorLow;
    public Servo lightTop =null;
    public Servo lightMid =null;
    public Servo lightLow =null;

    // --- 4. State variables ---
    private ArtifactDetectionState artifactDetectionState = ArtifactDetectionState.IDLE;

    // --- 5. Initialization ---
    /**
     * Call this in TeleOp init() to initialize hardware.
     */
    public void init(HardwareMap hardwareMap) {
        distanceSensorTop = hardwareMap.get(DistanceSensor.class, "color_sensor_top");
        distanceSensorMid = hardwareMap.get(DistanceSensor.class, "color_sensor_mid");
        distanceSensorLow = hardwareMap.get(DistanceSensor.class, "color_sensor_low");

        colorSensorTop = hardwareMap.get(ColorSensor.class, "color_sensor_top");
        colorSensorMid = hardwareMap.get(ColorSensor.class, "color_sensor_mid");
        colorSensorLow = hardwareMap.get(ColorSensor.class, "color_sensor_low");

        lightTop = hardwareMap.get(Servo.class, "light_top");
        lightMid = hardwareMap.get(Servo.class, "light_mid");
        lightLow = hardwareMap.get(Servo.class, "light_low");
    }

    // --- 6. Getter for current state ---
    public ArtifactDetectionState getArtifactState() {
        return artifactDetectionState;
    }

    // --- 6. Setter for current state ---
    public void setArtifactDetectionState(ArtifactDetectionState newState){
        artifactDetectionState = newState;
    }
    // Intake safety lookout --> So the intake will NOT run when 3 artifacts are detected to prtoect servos
    public boolean isIntakeAllowed () {
        return artifactDetectionState !=ArtifactDetectionState.THREE_ARTIFACTS_COLLECTED; //will return true when it is NOT in 3 artifacts detected state
    }

    // --- 7. Optional: stop() method for TeleOp end ---
    public void stop() {
        artifactDetectionState = ArtifactDetectionState.IDLE;
    }


    // --- 8. Update logic ---
    /**
     * Call this every loop() to handle state transitions and motor control.
     *
     */
    public void update() {

        double topDist = distanceSensorTop.getDistance(DistanceUnit.CM);
        double midDist = distanceSensorMid.getDistance(DistanceUnit.CM);
        double lowDist = distanceSensorLow.getDistance(DistanceUnit.CM);

        boolean top = topDist < 2.8;
        boolean mid = midDist < 2.8;
        boolean low = lowDist < 2.8;


        if (low && mid && top){
            artifactDetectionState = ArtifactDetectionState.THREE_ARTIFACTS_COLLECTED;
        } else if (mid && top){
            artifactDetectionState = ArtifactDetectionState.TWO_ARTIFACTS_COLLECTED;
        } else if (top) {
            artifactDetectionState = ArtifactDetectionState.ONE_ARTIFACT_COLLECTED;
        } else{
            artifactDetectionState = ArtifactDetectionState.ZERO_ARTIFACTS_COLLECTED;
        }

        // --- 6b. Act based on current state ---
        switch (artifactDetectionState) {
            case THREE_ARTIFACTS_COLLECTED:
                if(distanceSensorTop.getDistance(DistanceUnit.CM)<2.8) {
                    if (colorSensorTop.red() < colorSensorTop.blue() && colorSensorTop.blue() < colorSensorTop.green()) {
                        lightTop.setPosition(0.500);
                    } else if (colorSensorTop.red() < colorSensorTop.green() && colorSensorTop.green() < colorSensorTop.blue()) {
                        lightTop.setPosition(0.722);
                    } else{
                        lightTop.setPosition(0.000);
                    }
                }
                else{
                    lightTop.setPosition(0.000);
                }
                if(distanceSensorMid.getDistance(DistanceUnit.CM)<2.8) {
                    if (colorSensorMid.red() < colorSensorMid.blue() && colorSensorMid.blue() < colorSensorMid.green()) {
                        lightMid.setPosition(0.500);
                    } else if (colorSensorMid.red() < colorSensorMid.green() && colorSensorMid.green() < colorSensorMid.blue()) {
                        lightMid.setPosition(0.722);
                    } else{
                        lightMid.setPosition(0.000);
                    }
                }
                else{
                    lightMid.setPosition(0.000);
                }
                if(distanceSensorLow.getDistance(DistanceUnit.CM)<2.8) {
                    if (colorSensorLow.red() < colorSensorLow.blue() && colorSensorLow.blue() < colorSensorLow.green()) {
                        lightLow.setPosition(0.500);
                    } else if (colorSensorLow.red() < colorSensorLow.green() && colorSensorLow.green() < colorSensorLow.blue()) {
                        lightLow.setPosition(0.722);
                    } else{
                        lightLow.setPosition(0.000);
                    }
                }
                else{
                    lightLow.setPosition(0.000);
                }
                break;
            case TWO_ARTIFACTS_COLLECTED:
                if(distanceSensorTop.getDistance(DistanceUnit.CM)<2.8) {
                    if (colorSensorTop.red() < colorSensorTop.blue() && colorSensorTop.blue() < colorSensorTop.green()) {
                        lightTop.setPosition(0.500);
                    } else if (colorSensorTop.red() < colorSensorTop.green() && colorSensorTop.green() < colorSensorTop.blue()) {
                        lightTop.setPosition(0.722);
                    } else{
                        lightTop.setPosition(0.000);
                    }
                }
                else{
                    lightTop.setPosition(0.000);
                }
                if(distanceSensorMid.getDistance(DistanceUnit.CM)<2.8) {
                    if (colorSensorMid.red() < colorSensorMid.blue() && colorSensorMid.blue() < colorSensorMid.green()) {
                        lightMid.setPosition(0.500);
                    } else if (colorSensorMid.red() < colorSensorMid.green() && colorSensorMid.green() < colorSensorMid.blue()) {
                        lightMid.setPosition(0.722);
                    } else{
                        lightMid.setPosition(0.000);
                    }
                }
                else{
                    lightMid.setPosition(0.000);
                }
                break;
            case ONE_ARTIFACT_COLLECTED:
                if(distanceSensorTop.getDistance(DistanceUnit.CM)<2.8) {
                    if (colorSensorTop.red() < colorSensorTop.blue() && colorSensorTop.blue() < colorSensorTop.green()) {
                        lightTop.setPosition(0.500);
                    } else if (colorSensorTop.red() < colorSensorTop.green() && colorSensorTop.green() < colorSensorTop.blue()) {
                        lightTop.setPosition(0.722);
                    } else{
                        lightTop.setPosition(0.000);
                    }
                }
                else{
                    lightTop.setPosition(0.000);
                }
                break;
            case ZERO_ARTIFACTS_COLLECTED:
                lightTop.setPosition(0.000);
                lightMid.setPosition(0.000);
                lightLow.setPosition(0.000);
                break;
            case IDLE:
                break;
        }
    }


}
