package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/24/2015.
 */
public class Switch extends  BaseGameObject {
    Array<Door> doors;

    public Switch(Vector2 p) {
        super(p);
        tex = Assets.switchRegion;
        modelInstance = new ModelInstance(Assets.cubeModel);
        modelInstance.transform.setToTranslation(p.x, p.y, 0);
        doors = new Array<Door>();
        walkable = true;
        interactable = true;
    }

    public void linkObject(BaseGameObject obj){
        if (obj instanceof Door) doors.add((Door)obj);
    }

    public void interactWith(BaseGameObject obj, GamePlayScreen screen){
        if (obj != null){
            for (Door d : doors){
                d.trigger();
                d.usedThisTurn = true;
            }
        }
    }
}
