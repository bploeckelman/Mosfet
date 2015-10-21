package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/24/2015.
 */
public class Door extends BaseGameObject {


    public Door(Vector2 p, GameObjectProps props) {
        super(p, props);
        tex = Assets.doorClosedRegion;
        modelInstance = new ModelInstance(Assets.cubeModel);
        modelInstance.transform.setToTranslation(p.x, p.y, 0);
        interactable = true;
    }

    public void trigger(){
        walkable = true;
        tex = Assets.doorOpenRegion;
    }

    public void interactWith(BaseGameObject obj, GamePlayScreen screen){
        if (usedThisTurn) return;
        if (obj == null){
            walkable = false;
            tex = Assets.doorClosedRegion;
        }
    }
}
