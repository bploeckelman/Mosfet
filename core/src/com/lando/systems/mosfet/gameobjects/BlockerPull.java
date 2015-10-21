package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/24/2015.
 */
public class BlockerPull extends Blocker {
    public BlockerPull(Vector2 p, GameObjectProps props) {
        super(p, props);
        tex = Assets.blockerPullRegion;
    }

    public void bePushed(DIR dir){

    }
}
