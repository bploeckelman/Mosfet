package com.lando.systems.mosfet.gameobjects;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/10/2015.
 */
public class Player extends BaseGameObject {

    MutableFloat rotationAngleDeg;
    boolean rotationTweenRunning;

    public Player(Vector2 p) {
        super(p);
        tex = new TextureRegion(Assets.testTexture);
        stationary = false;
        rotationAngleDeg = new MutableFloat(getRotationFromDir());
        rotationTweenRunning = false;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(tex, pos.x, pos.y, 0.5f, 0.5f, 1f, 1f, 1f, 1f, rotationAngleDeg.floatValue());
    }

    @Override
    public void rotate(boolean clockwise) {
        if (rotationTweenRunning) return;

        DIR oldDir = direction;
        super.rotate(clockwise);
        if (direction != oldDir) {
            float currentAngleDeg = rotationAngleDeg.floatValue();
            float targetAngleDeg = clockwise ? currentAngleDeg - 90f : currentAngleDeg + 90f;
            Tween.to(rotationAngleDeg, -1, Assets.MOVE_DELAY)
                 .target(targetAngleDeg)
                 .setCallback(new TweenCallback() {
                     @Override
                     public void onEvent(int i, BaseTween<?> baseTween) {
                         rotationTweenRunning = false;
                     }
                 })
                 .start(Assets.tween);
            rotationTweenRunning = true;
        }
    }

    private float getRotationFromDir() {
        switch (direction) {
            case UP:    return 0f;
            case DOWN:  return 180f;
            case LEFT:  return 90f;
            case RIGHT: return 270f;
            default:    return 0f;
        }
    }

}
