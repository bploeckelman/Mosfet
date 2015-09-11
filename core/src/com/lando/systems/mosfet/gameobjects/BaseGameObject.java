package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Karla on 9/10/2015.
 */
public class BaseGameObject {

    public Vector2 pos;
    public TextureRegion tex;

    public BaseGameObject(Vector2 p){
        tex = new TextureRegion(Assets.circleTexture);
        pos = p;
    }

    public void update(float dt){

    }

    public void render(SpriteBatch batch){
        batch.draw(tex, pos.x, pos.y, 1, 1);
    }
}
