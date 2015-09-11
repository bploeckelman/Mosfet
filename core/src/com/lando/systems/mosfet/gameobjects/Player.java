package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/10/2015.
 */
public class Player extends BaseGameObject {
    public Player(Vector2 p) {
        super(p);
        tex = new TextureRegion(Assets.testTexture);
        stationary = false;
    }


}
