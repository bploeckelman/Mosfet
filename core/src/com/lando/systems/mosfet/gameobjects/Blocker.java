package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Doug on 9/24/2015.
 */
public class Blocker extends BaseGameObject {
    public Blocker(Vector2 p) {
        super(p);
        stationary = false;
    }

    public void bePushed(DIR dir){
        moveDir(dir);
    }

    public void bePulled(DIR dir){
        moveDir(dir);
    }
}
