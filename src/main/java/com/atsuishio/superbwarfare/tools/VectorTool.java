package com.atsuishio.superbwarfare.tools;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class VectorTool {
    public static double calculateAngle(Vec3 start, Vec3 end) {
        double startLength = start.length();
        double endLength = end.length();
        if (startLength > 0.0D && endLength > 0.0D) {
            return Math.toDegrees(Math.acos(Mth.clamp(start.dot(end) / (startLength * endLength), -1, 1)));
        } else {
            return 0.0D;
        }
    }
}
