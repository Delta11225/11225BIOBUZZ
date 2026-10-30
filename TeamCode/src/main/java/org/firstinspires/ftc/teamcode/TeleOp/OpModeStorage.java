package org.firstinspires.ftc.teamcode.TeleOp;

import com.pedropathing.math.Pose;

public class OpModeStorage {

    //"Static" important! It allows the info to remain accessible when the DS switches from one OpMode to another.

    //stores the ending position from autonomous
    //defaults to (0,0,0) if no Auto has saved a position.
    public static Pose autonomousEndPose = new Pose(0,0,0);

    //Tracks whether an Autonomous OpMode has saved a pose.
    public static boolean autoPoseSaved = false;
}
