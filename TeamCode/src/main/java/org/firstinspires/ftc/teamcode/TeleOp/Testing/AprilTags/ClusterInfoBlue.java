package org.firstinspires.ftc.teamcode.TeleOp.Testing.AprilTags;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Cluster Info BLUE", group = "Vision")
public class ClusterInfoBlue extends OpMode {

    private static final int BLUE_PIPELINE = 0;

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
        limelight = hardwareMap.get(
                Limelight3A.class, "Limelight");

        limelight.pipelineSwitch(BLUE_PIPELINE);

        telemetry.addLine("BLUE CLUSTER INFO");
        telemetry.addData("Pipeline requested", BLUE_PIPELINE);
        telemetry.update();
    }

    @Override
    public void start() {
        // Alliance input is not used by the hardcoded blue script.
        limelight.updatePythonInputs(new double[] {
                0.0, 18.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        });

        limelight.start();
    }

    @Override
    public void loop() {
        LLResult result = limelight.getLatestResult();

        telemetry.addLine("=== LIMELIGHT STATUS ===");
        telemetry.addData("Connected", limelight.isConnected());
        telemetry.addData("Running", limelight.isRunning());

        if (result == null) {
            telemetry.addLine("Waiting for result...");
            telemetry.update();
            return;
        }

        telemetry.addData("Active pipeline", result.getPipelineIndex());
        telemetry.addData("Pipeline type", result.getPipelineType());
        telemetry.addData("Result valid", result.isValid());

        // Standard Limelight measurements
        telemetry.addLine("--- STANDARD TARGET DATA ---");
        telemetry.addData("tx (horizontal deg)",
                "%.2f", result.getTx());
        telemetry.addData("ty (vertical deg)",
                "%.2f", result.getTy());
        telemetry.addData("ta (target area %)",
                "%.2f", result.getTa());
        telemetry.addData("tl (targeting latency ms)",
                "%.2f", result.getTargetingLatency());

        // Python SnapScript cluster measurements
        double[] py = result.getPythonOutput();

        telemetry.addLine("--- PYTHON CLUSTER DATA ---");

        if (py == null || py.length < 8) {
            telemetry.addLine("Python output unavailable");
        } else if (py[CLUSTER_ID] < 0) {
            telemetry.addLine("No BLUE cluster detected");
            telemetry.addData("Python output length", py.length);
        } else {
            telemetry.addData("Cluster", (int) py[CLUSTER_ID]);
            telemetry.addData("Number of tags", (int) py[NUM_TAGS]);
            telemetry.addData("Range (in)", "%.2f", py[RANGE]);
            telemetry.addData("Height (in)", "%.2f", py[HEIGHT]);
            telemetry.addData("Bearing (deg, + left)",
                    "%.2f", py[BEARING]);
            telemetry.addData("Elevation (deg)",
                    "%.2f", py[ELEVATION]);
            telemetry.addData("Sticker pitch (deg)",
                    "%.2f", py[STICKER_PITCH]);
            telemetry.addData("Roll (deg)", "%.2f", py[ROLL]);
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