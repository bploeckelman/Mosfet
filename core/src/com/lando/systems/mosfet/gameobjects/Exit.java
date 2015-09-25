package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Brian Ploeckelman created on 9/11/2015.
 */
public class Exit extends BaseGameObject {

    public Exit(Vector2 p) {
        super(p);
        tex = new TextureRegion(Assets.exitRegion);
        stationary = true;
        walkable = true;
        interactable = true;
    }

    public void interactWith(BaseGameObject obj, GamePlayScreen screen){
        if (obj != null && obj instanceof Player){
            screen.win();
        }
    }

}
