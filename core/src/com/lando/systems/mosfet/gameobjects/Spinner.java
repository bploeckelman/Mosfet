package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Doug on 9/24/2015.
 */
public class Spinner extends BaseGameObject {
    public Spinner(Vector2 p) {
        super(p);
        walkable = true;
        interactable = true;
    }

    public void interactWith(BaseGameObject other){
        if (other != null)
            other.rotate(true);
    }
}
