package frozenblock.wild.mod.liukrastapi;

public class MathAddon {
    public static double time;

    public static double cutCos(double value, double offset, boolean inverse) {
        double equation = Math.cos(value);
        if(!inverse) {
            return Math.max(equation, offset);
        } else {
            return Math.max(-equation, offset);
        }
    }

    public static double cutSin(double value, double offset, boolean inverse) {
        double equation = Math.sin(value);
        if(!inverse) {
            return Math.max(equation, offset);
        } else {
            return Math.max(-equation, offset);
        }
    }
}
