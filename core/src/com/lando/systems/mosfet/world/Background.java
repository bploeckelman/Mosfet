package com.lando.systems.mosfet.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 10/21/2015.
 */
public class Background {

    ModelInstance backgroundFloor;
    ModelBatch  modelBatch;

    public Background(Vector2 size){

        modelBatch = new ModelBatch(new DefaultShaderProvider(Assets.perfragLightConfig));
        backgroundFloor = new ModelInstance(Assets.backgroundModel);
        backgroundFloor.transform.setTranslation(size.x /2, size.y/2 , -1);

    }

    public void update(float dt){

    }

    public void render(PerspectiveCamera camera){
        modelBatch.begin(camera);
        modelBatch.render(backgroundFloor);
        modelBatch.end();
    }
}
