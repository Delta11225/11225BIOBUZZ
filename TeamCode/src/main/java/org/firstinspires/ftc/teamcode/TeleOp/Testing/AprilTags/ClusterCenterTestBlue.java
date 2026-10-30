package org.firstinspires.ftc.teamcode.TeleOp.Testing.AprilTags;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Arrays;

@TeleOp(name = "Cluster Center Test BLUE", group = "Vision")
public class ClusterCenterTestBlue extends OpMode {

    private static final int CLUSTER_PIPELINE = 0;// Pipeline 0 = Blue, Pipeline 1 = Red

    // Change this to your camera's actual upward angle
    //camera facing straight out is 0
    //angle measured from vertical /|
    private static final double CAMERA_PITCH_DEG = 18.0;

    // Python output indexes
    private static final int CLUSTER_ID = 0;
    private static final int NUM_TAGS = 1;
    private static final int RANGE = 2;
    private static final int BEARING = 3;
    private static final int HEIGHT = 4;
    private static final int ELEVATION = 5;
    private static final int STICKER_PITCH = 6;
    private static final int ROLL = 7;

    private Limelight3A limelight;

    @Override
    public void init() {
        telemetry.addLine("BLUE TEST: INIT WORKS");
        telemetry.update();

        limelight = hardwareMap.get(
                Limelight3A.class, "Limelight");

        telemetry.addLine("Limelight found");
        telemetry.update();

        limelight.pipelineSwitch(CLUSTER_PIPELINE);
    }

    @Override
    public void start() {
        boolean switched = limelight.pipelineSwitch(CLUSTER_PIPELINE);

        telemetry.addData("Pipeline switch accepted", switched);
        telemetry.addData("Expected pipeline slot", CLUSTER_PIPELINE);
        telemetry.update();

        limelight.updatePythonInputs(new double[] {
                0.0, CAMERA_PITCH_DEG,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        });

        limelight.start();
    }

    @Override
    public void loop() {
        telemetry.addLine("LOOP WORKS");
        telemetry.addData("Connected", limelight.isConnected());
        telemetry.addData("Running", limelight.isRunning());

        LLResult result = limelight.getLatestResult();
        telemetry.addData("Result null", result == null);

        if (result != null) {
            double[] py = result.getPythonOutput();

            telemetry.addData("Python null", py == null);
            telemetry.addData("Output length",
                    py == null ? 0 : py.length);

            if (py != null && py.length >= 8) {
                telemetry.addData("Cluster ID", py[0]);
                telemetry.addData("Tag count", py[1]);
                telemetry.addData("Range", py[2]);
                telemetry.addData("Bearing", py[3]);
            }
        }

        telemetry.update();
    }


    @Override
    public void stop() {
        if (limelight != null) {
            limelight.stop();
        }
    }
}