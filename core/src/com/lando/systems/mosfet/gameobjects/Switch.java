package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/24/2015.
 */
public class Switch extends  BaseGameObject {
    public Switch(Vector2 p) {
        super(p);
        tex = Assets.switchRegion;
    }
}
