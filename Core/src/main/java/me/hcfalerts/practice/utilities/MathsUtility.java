package me.hcfalerts.practice.utilities;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class MathsUtility {

    private static final int SIN_BITS, SIN_MASK, SIN_COUNT;
    private static final double radFull, radToIndex;
    private static final double degFull, degToIndex;
    private static final double[] sin, cos;

    static {
        SIN_BITS = 12;
        SIN_MASK = ~(-1 << SIN_BITS);
        SIN_COUNT = SIN_MASK + 1;

        radFull = Math.PI * 2.0;
        degFull = 360.0;
        radToIndex = SIN_COUNT / radFull;
        degToIndex = SIN_COUNT / degFull;

        sin = new double[SIN_COUNT];
        cos = new double[SIN_COUNT];

        for (int i = 0; i < SIN_COUNT; i++) {
            sin[i] = Math.sin((i + 0.5f) / SIN_COUNT * radFull);
            cos[i] = Math.cos((i + 0.5f) / SIN_COUNT * radFull);
        }

        for (int i = 0; i < 360; i += 90) {
            sin[(int) (i * degToIndex) & SIN_MASK] = Math.sin(i * Math.PI / 180.0);
            cos[(int) (i * degToIndex) & SIN_MASK] = Math.cos(i * Math.PI / 180.0);
        }
    }

    public static double sin(double rad) {
        return sin[(int) (rad * radToIndex) & SIN_MASK];
    }

    public static double cos(double rad) {
        return cos[(int) (rad * radToIndex) & SIN_MASK];
    }

    public static List<Location> getCircle(Location center, float radius, int amount) {
        List<Location> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double a = 2 * Math.PI / amount * i;
            double x = Math.cos(a) * radius;
            double z = Math.sin(a) * radius;
            list.add(center.clone().add(x, 0, z));
        }
        return list;
    }

    /**
     * Check if a value is between two other values
     */
    public static Boolean isBetween(Integer input, Integer min, Integer max){
        return input >= min && input <= max;
    }

    /**
     * Convert a value to a positive value
     */
    public static int convertToPositive(int input){
        return Math.abs(input);
    }

    /**
     * Get Proper string for number
     */
    public static String getCorrectPronumeral(String text, int input) {
        return text + (input == 1 ? "" : "s");
    }

}