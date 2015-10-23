package com.lando.systems.mosfet.utils;

/**
 * Created by Doug on 10/23/2015.
 */
public class Utils {

    public static float wrapValue(float val, float min, float max){
        float range = max - min;
        if (val > max) val -= range;
        if (val < min) val += range;
        return val;
    }
}
