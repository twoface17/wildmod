package frozenblock.wild.mod.liukrastapi;

public class AnimationAPI {

    /*
    ANIMATION API BY LIUKRAST - PRIVATE FOR @FROZENBLOCKSTUDIOS MODS ONLY
     */

    public static float easeIn(float start, float w, float h, float time) {
        float exit = 0;
        if(time >= start && time <= w + start) {
            float pow = (time/w)+(start/w);
            exit = pow*pow*h;
        }
        if(time > w + start) {
            exit = h;
        }
        return exit;
    }
    public static float easeOut(float start, float w, float h, float time) {
        float exit = 0;
        if(time >= start && time <= w + start) {
            float pow = (time/w)-1-(start/w);
            exit = -(pow*pow)*h + h;
        }
        if(time > w + start) {
            exit = h;
        }
        return exit;
    }

}
