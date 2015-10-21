package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/11/2015.
 */
public class Wall extends BaseGameObject {
    public Wall(Vector2 p, GameObjectProps props) {
        super(p, props);
        tex = new TextureRegion(Assets.wallRegion);
        modelInstance = new ModelInstance(Assets.cubeModel);
        modelInstance.transform.setToTranslation(p.x, p.y, 0);
        stationary = true;
    }
}
