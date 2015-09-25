package com.lando.systems.mosfet.gameobjects;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/10/2015.
 */
public class Player extends BaseGameObject {



    public Player(Vector2 p) {
        super(p);
        tex = new TextureRegion(Assets.testTexture);
        stationary = false;
        canRotate = true;

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(tex, pos.x, pos.y, 0.5f, 0.5f, 1f, 1f, 1f, 1f, rotationAngleDeg.floatValue());
    }




    public void move(boolean forward){
        moveDir(forward ? direction: invertDir(direction));
    }

}
