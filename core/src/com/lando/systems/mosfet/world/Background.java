package com.lando.systems.mosfet.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 10/21/2015.
 */
public class Background {

    ModelInstance backgroundFloor;
    Environment backgroundEnviornment;

    public Background(){
        backgroundFloor = new ModelInstance(Assets.backgroundModel);
        backgroundEnviornment = new Environment();
        backgroundEnviornment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));
        final PointLight pointLight = new PointLight().set(new Color(1f, 1f, 1f, 1f), 0, 0, 1f, 200f);
        backgroundEnviornment.add(pointLight);

    }

    public void update(float dt){

    }

    public void render(ModelBatch modelBatch){
        modelBatch.render(backgroundFloor, backgroundEnviornment);
    }
}
