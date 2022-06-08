package util;

public class MathHelpers {
    public static double differenceBetweenAngles(double angle1, double angle2) {
        double diff = Math.abs(angle1 - angle2);
        return diff > 180 ? 360 - diff : diff;
    }
}
