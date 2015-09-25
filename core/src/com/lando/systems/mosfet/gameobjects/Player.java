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

    }

    public Player(Vector2 p) {
        this(p, DIR.UP);
    }



    public void move(boolean forward, GamePlayScreen screen){
        moveDir(forward ? direction: invertDir(direction));
    }

}
