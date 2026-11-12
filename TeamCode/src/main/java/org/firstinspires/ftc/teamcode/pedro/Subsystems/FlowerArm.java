package org.firstinspires.ftc.teamcode.pedro.Subsystems;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.CommandBuilder;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class FlowerArm {
    private final Servo flowerArm;
    double FLOWER_ARM_UP = 0.6;
    double FLOWER_ARM_DOWN = 0.042;

    public FlowerArm(HardwareMap hardwareMap){
        flowerArm = hardwareMap.get(Servo.class, "flower_arm");
    }

    public Command flowerArmDown(){
        return new CommandBuilder()
                .requiring(flowerArm)

                .setExecute(() -> {
                    flowerArm.setPosition(FLOWER_ARM_DOWN);
                })
                .setDone(()->{
                    return flowerArm.getPosition() == FLOWER_ARM_DOWN;
                        });
    }
    public Command flowerArmUp(){
        return new CommandBuilder()
                .requiring(flowerArm)

                .setExecute(() -> {
                    flowerArm.setPosition(FLOWER_ARM_UP);
                })
                .setDone(()->{
                    return flowerArm.getPosition() == FLOWER_ARM_UP;
                        });
    }
    public void initFlower(){
        flowerArm.setPosition(FLOWER_ARM_UP);
    }
}
