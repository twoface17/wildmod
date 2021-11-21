package frozenblock.wild.mod.liukrastapi;

public class UpdaterNum {
    public static double time;

    public static double cuttedCos(double value, double multiplier, double frequency) {
        double equation = Math.cos(value*frequency)*multiplier;
        if(equation > 0) {
            return equation;
        } else {
            return 0;
        }
    }

    public static double acuttedCos(double value, double multiplier, double frequency) {
        double equation = -Math.cos(value*frequency)*multiplier;
        if(equation > 0) {
            return equation;
        } else {
            return 0;
        }
    }

    public static double cuttedSin(double value, double multiplier, double frequency) {
        double equation = Math.sin(value*frequency)*multiplier;
        if(equation > 0) {
            return equation;
        } else {
            return 0;
        }
    }

    public static double acuttedSin(double value, double multiplier, double frequency) {
        double equation = -Math.sin(value*frequency)*multiplier;
        if(equation > 0) {
            return equation;
        } else {
            return 0;
        }
    }
}
