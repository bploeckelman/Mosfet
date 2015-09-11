package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/11/2015.
 */
public class Wall extends BaseGameObject {
    public Wall(Vector2 p) {
        super(p);
        tex = new TextureRegion(Assets.wallRegion);
        stationary = true;
    }
}
