package com.lando.systems.mosfet.gameobjects;


import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/10/2015.
 */
public class Player extends BaseGameObject {

    public Player(Vector2 p, DIR d){
        super(p);
        tex = new TextureRegion(Assets.playerRegion);
        stationary = false;
        canRotate = true;
        direction = d;
        rotationAngleDeg = new MutableFloat(getRotationFromDir());

        modelInstance.transform.rotate(0f, 0f, 1f, 180f);
    }

    public Player(Vector2 p) {
        this(p, DIR.UP);
    }


    public void update(float dt) {
        float degrees;
        switch (direction) {
            default:
            case RIGHT: degrees = 0f;   break;
            case UP:    degrees = 90f;  break;
            case LEFT:  degrees = 180f; break;
            case DOWN:  degrees = 270f; break;
        }
        modelInstance.transform.setToRotation(0f, 0f, 1f, degrees);
        super.update(dt);
    }

    public void move(boolean forward, GamePlayScreen screen){
        moveDir(forward ? direction: invertDir(direction));
    }

}
