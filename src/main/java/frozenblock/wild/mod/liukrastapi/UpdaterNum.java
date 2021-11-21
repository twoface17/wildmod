package frozenblock.wild.mod.liukrastapi;

public class UpdaterNum {
    public static double time;

    public static double cuttedCos(double value, double multiplier, double cutpoint, boolean impactcut) {
        double equation = Math.cos(value)*multiplier;
        if(equation > cutpoint) {
            if(!impactcut) {
                return equation;
            } else {
                return equation - cutpoint;
            }
        } else {
            return cutpoint;
        }
    }

    public static double acuttedCos(double value, double multiplier, double cutpoint, boolean impactcut) {
        double equation = -Math.cos(value)*multiplier;
        if(equation > cutpoint) {
            if(!impactcut) {
                return equation;
            } else {
                return equation - cutpoint;
            }
        } else {
            return cutpoint;
        }
    }

    public static double cuttedSin(double value, double multiplier, double cutpoint, boolean impactcut) {
        double equation = Math.sin(value)*multiplier;
        if(equation > cutpoint) {
            if(!impactcut) {
                return equation;
            } else {
                return equation - cutpoint;
            }
        } else {
            return cutpoint;
        }
    }

    public static double acuttedSin(double value, double multiplier, double cutpoint, boolean impactcut) {
        double equation = -Math.sin(value)*multiplier;
        if(equation > cutpoint) {
            if(!impactcut) {
                return equation;
            } else {
                return equation - cutpoint;
            }
        } else {
            return cutpoint;
        }
    }
}
