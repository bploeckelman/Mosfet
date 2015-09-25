package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/24/2015.
 */
public class Teleport extends BaseGameObject {

    Teleport otherEnd;

    public Teleport(Vector2 p) {
        super(p);
        walkable = true;
        interactable = true;
        tex = Assets.teleportRegion;
    }

    public void linkObject(BaseGameObject other){
        if (other instanceof Teleport)
            otherEnd = (Teleport)other;
    }

    public void interactWith(BaseGameObject obj, GamePlayScreen screen){
        if (obj == null) return;
        if (usedThisTurn) return;


        //TODO remove this?
        if (otherEnd == null) {
            throw new NullPointerException("Teleporter is unlinked");
        }

        BaseGameObject otherObject = screen.getObjectAt(otherEnd.pos);
        if (otherObject == null){
            // TODO make this move or something?
            usedThisTurn = true;
            otherEnd.usedThisTurn = true;
            obj.pos = otherEnd.pos.cpy();
            obj.renderPos = otherEnd.pos.cpy();
            if (obj.moveTween != null) obj.moveTween.kill();
        }
    }
}
