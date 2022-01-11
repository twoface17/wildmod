package frozenblock.wild.mod.liukrastapi;

import net.minecraft.util.math.MathHelper;

public class AnimationAPI {

    /*
    ANIMATION API BY LIUKRAST - PRIVATE FOR @FROZENBLOCKSTUDIOS MODS ONLY
     */

    public static float easeInSine(float start, float w, float h, float time) {
        float exit = 0;
        if(time >= start && time <= w + start) {
            float value_10982 = (time/w)+(start/w);
            exit = value_10982*value_10982*h;
        }
        if(time > w + start) {
            exit = h;
        }
        return exit;
    }
    public static float easeOutSine(float start, float w, float h, float time) {
        float exit = 0;
        if(time >= start && time <= w + start) {
            float value_10982 = (time/w)-1-(start/w);
            exit = -(value_10982*value_10982)*h + h;
        }
        if(time > w + start) {
            exit = h;
        }
        return exit;
    }
    public static float easeInOutSine(float start, float w, float h, float time) {
        float exit = 0;
        if(time >= start && time <= w + start) {
            exit = ((MathHelper.cos((float)Math.PI*((time-start)/w))-1)/2) * h;
        }
        return exit;
    }
    public static float easeOutElastic(float start, float w, float h, float freq, float divider, float time) {
        float exit = 0;
        if(time >= start) {
            float divider2 = (time/divider - start/divider)*(time/divider - start/divider);
            float value_19872b = MathHelper.cos((float) ((10*(float)Math.PI*(time-start))/(w*1.5) * freq));
            exit = (value_19872b*h)/divider2 + h;
        }
        return exit;
    }
    public static float easeOutBounce(float start, float w, float h, float freq, float divider, float time) {
        float exit = 0;
        if(time >= start) {
            float divider2 = (time/divider - start/divider)*(time/divider - start/divider);
            float value_19872b = MathHelper.abs(MathHelper.cos((float) ((10*(float)Math.PI*(time-start))/(w*1.5) * freq)));
            exit = (value_19872b*h)/divider2 + h;
        }
        return exit;
    }
    public static float easeInBack(float start, float w, float h, float a, float time) {
        float b = a+1;
        float exit = 0;
        if(time>=start && time <= w+start) {
            float eq = (time-start)/w;
            exit = (b*eq*eq*eq - a*eq*eq)*h;
        }
        return exit;
    }
    public static float easeOutBack(float start, float w, float h, float a, float time) {
        float b = a+1;
        float exit = 0;
        if(time>=start && time <= w+start) {
            float eq = ((time-start)/w) - 1;
            exit = (1 + b*eq*eq*eq - a*eq*eq)*h;
        }
        return exit;
    }
    public static float linear(float start, float w, float h, float time) {
        float exit = 0;
        if(time >= start && time <= w +start) {
            exit = time/w * h;
        }
        return exit;
    }

}
