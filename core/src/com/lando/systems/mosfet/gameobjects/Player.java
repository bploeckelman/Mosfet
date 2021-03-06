package com.lando.systems.mosfet.gameobjects;


import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.world.Entity;

/**
 * Created by Doug on 9/10/2015.
 */
public class Player extends BaseGameObject {

    public Player(Vector2 p, DIR d){
        super(p, new GameObjectProps(Entity.Type.BLANK, d, 0));
        tex = new TextureRegion(Assets.playerRegion);
        stationary = false;
        canRotate = true;
        direction = d;
        rotationAngleDeg = new MutableFloat(getRotationFromDir());

        modelInstance = new ModelInstance(Assets.robotModel);
        modelInstance.transform.setToTranslation(p.x, p.y, 0);
        modelInstance.transform.rotate(0f, 0f, 1f, 180f);
    }

    public Player(Vector2 p) {
        this(p, DIR.UP);
    }


    public void update(float dt) {
        super.update(dt);
    }

    public void move(boolean forward, GamePlayScreen screen){
        moveDir(forward ? direction: invertDir(direction));
    }

}
