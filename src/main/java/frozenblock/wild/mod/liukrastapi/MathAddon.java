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

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double distancex;
        double distancey;
        double distancez;

        if(x1 > x2) {distancex = x1 - x2;} else {distancex = x2 -x1;}
        if(y1 > y2) {distancey = y1 - y2;} else {distancey = y2 -y1;}
        if(z1 > z2) {distancez = z1 - z2;} else {distancez = z2 -z1;}

        return Math.sqrt(Math.pow(distancex, 2) + Math.pow(distancey, 2) + Math.pow(distancez, 2));
    }
}
