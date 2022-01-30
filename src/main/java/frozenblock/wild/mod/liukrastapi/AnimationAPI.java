package frozenblock.wild.mod.liukrastapi;

import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public class AnimationAPI {

    private static final String errorprfx = "[AnimationAPI] ";

    /*
    ANIMATION API BY LIUKRAST - PRIVATE FOR @FROZENBLOCKSTUDIOS MODS ONLY
    VERSION 2.2
     */

    public static float easeInSine(float from, float to, float size, float time) {
        float exit = 0;
        float w = to - from;
        float x = time - from;
        if( x >= 0 && x <= w) {
            float eq = (time/w) - 1 - (from/w);
            exit = eq*eq;
        }
        if (x > w) {
            return size;
        }
        if( x >= 0) {
            return (-exit * size) + size;
        } else {
            return 0;
        }
    }

    public static float easeOutSine(float from, float to, float size, float time) {
        float exit = 0;
        float w = to - from;
        float x = time - from;
        if( x >= 0 && x <= w) {
            float eq = (time/w) - (from/w);
            exit = eq*eq;
        }
        if (x > w) {
            return size;
        }
        if( x >= 0) {
            return exit * size;
        } else {
            return 0;
        }
    }

    public static float easeInOutSine(float from, float to, float size, float time) {
        float exit = 0;
        float w = to - from;
        float x = time - from;
        if( x >= 0 && x <= w) {
            exit = (-(float)Math.cos((x/w)*Math.PI)/2)+0.5f;
        }
        if (x > w) {
            return size;
        }
        if( x >= 0) {
            return exit * size;
        } else {
            return 0;
        }
    }

    public static float easeInBack(float from, float to, float size, float time, float multiplier) {
        float exit = 0;
        float w = to - from;
        float x = time - from;
        float c3 = multiplier + 1;
        if( x >= 0 && x <= w) {
            exit = c3*(x/w)*(x/w)*(x/w)- multiplier *(x/w)*(x/w);
        }
        if (x > w) {
            return size;
        }
        if( x >= 0) {
            return exit * size;
        } else {
            return 0;
        }
    }

    public static float easeInBack(float from, float to, float size, float time) {
        return easeInBack(from, to, size, time, 1.5f);
    }

    public static float easeOutBack(float from, float to, float size, float time, float multiplier) {
        float exit = 0;
        float w = to - from;
        float x = time - from;
        float c3 = multiplier + 1;
        if( x >= 0 && x <= w) {
            exit = 1+c3*(float)Math.pow(x/w - 1, 3)+ multiplier *(float)Math.pow(x/w - 1, 2);
        }
        if (x > w) {
            return size;
        }
        if( x >= 0) {
            return exit * size;
        } else {
            return 0;
        }
    }

    public static float easeOutBack(float from, float to, float size, float time) {
        return easeOutBack(from, to, size, time, 1.5f);
    }

    public static float easeInOutBack(float from, float to, float size, float time, float multiplier) {
        float exit = 0;
        float w = to - from;
        float x = time - from;
        float c2 = multiplier * 1.525f;
        if( x >= 0 && x <= w) {
            if(x < 0.5*w) {
                exit = ((float)Math.pow(2*x/w, 2) * ((c2+1)*2*x/w - c2) )/2;
            } else {
                exit = ((float)Math.pow(2 * (x/w) - 2, 2) * ((c2 + 1) * ((x/w) * 2 - 2) + c2) + 2) / 2;
            }
        }
        if (x > w) {
            return size;
        }
        if( x >= 0) {
            return exit * size;
        } else {
            return 0;
        }
    }

    public static float easeInOutBack(float from, float to, float size, float time) {
        return easeInOutBack(from, to, size, time, 1.5f);
    }

    public static float easeInElastic(float from, float to, float size, float time, float amount) {
        float exit = 0;
        float w = to - from;
        if(w < 0) {
        }
        float timelessfrom = time-from;
        if(timelessfrom == 0) return 0; if((timelessfrom/=w)==1) return size;
        float p=w*.3f;
        float s=p/4;
        if(time >= from && time <= from + to) {
            exit = -(amount *(float)Math.pow(2,10*(timelessfrom-=1)) * (float)Math.sin( (timelessfrom*w-s)*(2*(float)Math.PI)/p ));
        }
        if(time > from + to) {
            exit = size;
        }
        return exit;
    }

    public static float easeOutElastic(float from, float to, float size, float time, float amount) {
        float exit = 0;
        float w = to - from;
        if(w < 0) {
        }
        float timelessfrom = time-from;
        if(timelessfrom == 0) return 0; if((timelessfrom/=w)==1) return size;
        float p=w*.3f;
        float s=p/4;
        if(time >= from && time <= from + to) {
            exit = (amount *(float)Math.pow(2,-10*timelessfrom) * (float)Math.sin( (timelessfrom*w-s)*(2*(float)Math.PI)/p ) + size);
        }
        if(time > from + to) {
            exit = size;
        }
        return exit;
    }

    public static float easeInOutElastic(float from, float to, float size, float time, float amount) {
        float exit = 0;
        float w = to - from;
        if(w < 0) {
        }
        float timelessfrom = time-from;
        if(timelessfrom == 0) return 0; if((timelessfrom/=w/2)==2) return size;
        float p=w*(.3f*1.5f);
        float s=p/4;
        if(time >= from && time <= from + to) {
            if(timelessfrom < 1) {
                exit = -.5f*(amount *(float)Math.pow(2,10*(timelessfrom-=1)) * (float)Math.sin( (timelessfrom*w-s)*(2*(float)Math.PI)/p ));
            } else {
                exit = amount *(float)Math.pow(2,-10*(timelessfrom-=1)) * (float)Math.sin( (timelessfrom*w-s)*(2*(float)Math.PI)/p )*.5f + size;
            }
        }
        if(time > from + to) {
            exit = size;
        }
        return exit;
    }

    public static float easeInBounce(float from, float to, float size, float time) {
        float exit = 0;
        float w = to - from;
        if(w < 0) {
        }
        float timelessfrom = time-from;
        if(time >= from && time <= from + to) {
            exit = size - easeOutBounce(0, to, w, w-timelessfrom);
        }
        if(time > from + to) {
            exit = size;
        }
        return exit;
    }

    public static float easeOutBounce(float from, float to, float size, float time) {
        float exit = 0;
        float w = to - from;
        if(w < 0) {
        }
        float timelessfrom = time-from;
        if(time >= from && time <= from + to) {
            if ((timelessfrom/=w) < (1/2.75f)) {
                return size*(7.5625f*timelessfrom*timelessfrom);
            } else if (timelessfrom < (2/2.75f)) {
                return size*(7.5625f*(timelessfrom-=(1.5f/2.75f))*timelessfrom + .75f);
            } else if (timelessfrom < (2.5/2.75)) {
                return size*(7.5625f*(timelessfrom-=(2.25f/2.75f))*timelessfrom + .9375f);
            } else {
                return size*(7.5625f*(timelessfrom-=(2.625f/2.75f))*timelessfrom + .984375f);
            }
        }
        if(time > from + to) {
            exit = size;
        }
        return exit;
    }

    public static float easeInOutBounce(float from, float to, float size, float time) {
        float exit = 0;
        float w = to - from;
        if(w < 0) {
        }
        float timelessfrom = time-from;
        if(time >= from && time <= from + to) {
            if(timelessfrom < w/2) {
                exit = easeInBounce(0, w, size, time*2) * .5f;
            } else {
                exit = easeOutBounce(0, w, size, time*2-w) * size * .5f * .5f;
            }
        }
        if(time > from + to) {
            exit = size;
        }
        return exit;
    }

    public static float linear(float from, float to, float size, float time) {
        float exit = 0;
        float w = to - from;
        if(w < 0) {
        }
        float timelessfrom = time-from;
        if(time >= from && time <= from + to) {
            exit = timelessfrom*(size/w);
        }
        if(time > from + to) {
            exit = size;
        }
        return exit;
    }

}
