package com.lando.systems.mosfet.gameobjects;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Doug on 9/24/2015.
 */
public class DumbRobot extends BaseGameObject {
    public DumbRobot(Vector2 p, DIR d){
        super(p);
        stationary = false;
        canRotate = true;
        direction = d;
        rotationAngleDeg = new MutableFloat(getRotationFromDir());
    }

    public DumbRobot(Vector2 p) {
        this(p, DIR.UP);
    }

    public void move(boolean forward){
        moveDir(forward ? direction : invertDir(direction));
    }
}
