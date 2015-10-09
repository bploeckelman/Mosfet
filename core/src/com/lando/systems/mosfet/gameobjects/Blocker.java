package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/24/2015.
 */
public class Blocker extends BaseGameObject {
    public Blocker(Vector2 p) {
        super(p);
        modelInstance = new ModelInstance(Assets.cubeModel);
        modelInstance.transform.setToTranslation(p.x, p.y, 0);
        stationary = false;
    }

    public void bePushed(DIR dir){
        moveDir(dir);
    }

    public void bePulled(DIR dir){
        moveDir(dir);
    }
}
