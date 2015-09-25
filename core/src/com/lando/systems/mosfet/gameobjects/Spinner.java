package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/24/2015.
 */
public class Spinner extends BaseGameObject {
    public Spinner(Vector2 p) {
        super(p);
        walkable = true;
        interactable = true;
        tex = Assets.spinnerRegion;
    }

    public void interactWith(BaseGameObject other, GamePlayScreen screen){
        if (other != null)
            other.rotate(true);
    }
}
