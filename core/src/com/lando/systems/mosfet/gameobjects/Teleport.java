package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Doug on 9/24/2015.
 */
public class Teleport extends BaseGameObject {

    Teleport otherEnd;

    public Teleport(Vector2 p) {
        super(p);
        walkable = true;
        interactable = true;
    }

    public void linkObject(BaseGameObject other){
        if (other instanceof Teleport) otherEnd = (Teleport)other;
    }

    public void interactWith(BaseGameObject obj){

    }
}
