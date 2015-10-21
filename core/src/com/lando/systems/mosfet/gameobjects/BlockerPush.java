package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/24/2015.
 */
public class BlockerPush extends Blocker {
    public BlockerPush(Vector2 p, GameObjectProps props) {
        super(p, props);
        tex = Assets.blockerPushRegion;
    }

    public void bePulled(DIR dir){

    }}
