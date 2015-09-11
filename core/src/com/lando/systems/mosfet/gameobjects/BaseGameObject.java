package com.lando.systems.mosfet.gameobjects;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.utils.accessors.Vector2Accessor;

/**
 * Created by Karla on 9/10/2015.
 */
public class BaseGameObject {
    public enum DIR {UP, DOWN, LEFT, RIGHT}

    public Vector2 pos;
    public Vector2 oldPos;
    public Vector2 targetPos;
    public Vector2 tempPos;
    public TextureRegion tex;
    public DIR direction;
    public boolean stationary;
    public boolean walkable;
    public boolean conflict;
    public BaseTween moveTween;


    public BaseGameObject(Vector2 p){
        tex = new TextureRegion(Assets.stoneTexture);
        pos = p;
        oldPos = p.cpy();
        targetPos = p.cpy();
        tempPos = p.cpy();
        direction = DIR.UP;
        stationary = true;
        walkable = false;
        conflict = false;
    }

    public void update(float dt){

    }

    public void render(SpriteBatch batch){
        batch.draw(tex, pos.x, pos.y, 1, 1);
    }

    public void move(boolean forward){
        if (stationary) return;
        oldPos = pos.cpy();
        tempPos = pos.cpy();
        switch (direction){
            case UP:
                targetPos.y = forward ? pos.y + 1 : pos.y -1;
                tempPos.y = forward ? pos.y + .5f : pos.y -.5f;
                break;
            case RIGHT:
                targetPos.x = forward ? pos.x + 1 : pos.x -1;
                tempPos.x = forward ? pos.x + .5f : pos.x -.5f;
                break;
            case DOWN:
                targetPos.y = forward ? pos.y - 1 : pos.y +1;
                tempPos.y = forward ? pos.y - .5f : pos.y + .5f;
                break;
            case LEFT:
                targetPos.x = forward ? pos.x - 1 : pos.x +1;
                tempPos.x = forward ? pos.x - .5f : pos.x +.5f;
                break;
        }
        moveTween = Tween.to(pos, Vector2Accessor.XY, Assets.MOVE_DELAY)
                    .target(targetPos.x, targetPos.y)
                    .start(Assets.tween);
    }


    public void rotate(boolean clockwise)
    {
        switch (direction){
            case UP:
                direction = clockwise ? DIR.RIGHT : DIR.LEFT;
                break;
            case RIGHT:
                direction = clockwise ? DIR.DOWN : DIR.UP;
                break;
            case DOWN:
                direction = clockwise ? DIR.LEFT : DIR.RIGHT;
                break;
            case LEFT:
                direction = clockwise ? DIR.UP : DIR.DOWN;
                break;
        }
    }

    public boolean collides(BaseGameObject other){
        if (other.walkable) return false;
        if (targetPos.x == other.targetPos.x && targetPos.y == other.targetPos.y) return true;
        return false;
    }

    public void revert(){
        if (moveTween != null) moveTween.kill();
        Timeline.createSequence()
                .push(Tween.to(pos, Vector2Accessor.XY, Assets.MOVE_DELAY/2)
                    .target(tempPos.x, tempPos.y))
                .push(Tween.to(pos, Vector2Accessor.XY, Assets.MOVE_DELAY/2)
                        .target(oldPos.x, oldPos.y))
                .start(Assets.tween);
        targetPos = oldPos.cpy();
    }
}
