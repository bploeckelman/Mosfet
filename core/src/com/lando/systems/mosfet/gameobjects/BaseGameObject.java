package com.lando.systems.mosfet.gameobjects;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.utils.accessors.Vector2Accessor;

/**
 * Created by Karla on 9/10/2015.
 */
public class BaseGameObject {
    public enum DIR {UP, DOWN, LEFT, RIGHT}

    public Vector2 pos;
    public Vector2 oldPos;
    public Vector2 renderPos;
    public Vector2 tempPos;
    public TextureRegion tex;
    public DIR direction;
    public boolean stationary;
    public boolean canRotate;
    public boolean walkable;
    public boolean conflict;
    public BaseTween moveTween;
    MutableFloat rotationAngleDeg;
    public boolean interactable;
    public boolean usedThisTurn;



    public BaseGameObject(Vector2 p){
        tex = new TextureRegion(Assets.wallRegion);
        pos = p;
        oldPos = p.cpy();
        renderPos = p.cpy();
        tempPos = p.cpy();
        direction = DIR.UP;
        stationary = true;
        walkable = false;
        conflict = false;
        canRotate = false;
        rotationAngleDeg = new MutableFloat(getRotationFromDir());
        interactable = false;
    }

    public void linkObject(BaseGameObject obj){

    }

    public void update(float dt){

    }

    public void render(SpriteBatch batch){
        batch.draw(tex, renderPos.x, renderPos.y, 1, 1);
    }

    public void move(boolean forward){

    }

    public void moveDir(DIR d){
        oldPos = pos.cpy();
        tempPos = pos.cpy();
        switch (d){
            case UP:
                pos.y = oldPos.y + 1;
                tempPos.y = oldPos.y + .5f;
                break;
            case RIGHT:
                pos.x = oldPos.x + 1;
                tempPos.x = oldPos.x + .5f;
                break;
            case DOWN:
                pos.y = oldPos.y - 1;
                tempPos.y = oldPos.y - .5f;
                break;
            case LEFT:
                pos.x =oldPos.x - 1;
                tempPos.x = oldPos.x - .5f;
                break;
        }
        moveTween = Tween.to(renderPos, Vector2Accessor.XY, Assets.MOVE_DELAY)
                .target(pos.x, pos.y)
                .start(Assets.tween);
    }


    public void rotate(boolean clockwise)
    {
        if (!canRotate) return;
        DIR oldDir = direction;
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



        if (direction != oldDir) {
            float currentAngleDeg = rotationAngleDeg.floatValue();
            float targetAngleDeg = clockwise ? currentAngleDeg - 90f : currentAngleDeg + 90f;
            Tween.to(rotationAngleDeg, -1, Assets.MOVE_DELAY)
                    .target(targetAngleDeg)
                    .start(Assets.tween);
        }
    }

    public void bePushed(DIR dir){

    }

    public void bePulled(DIR dir){

    }

    /**
     * have the object get acted on
     *
     * @param obj if obj is null that means nothing is on it.
     * @param screen
     */
    public void interactWith(BaseGameObject obj, GamePlayScreen screen){

    }

    public boolean collides(BaseGameObject other){
        if (other.walkable) return false;
        return overlaps(other);
    }

    public boolean overlaps(BaseGameObject other){
        return (pos.x == other.pos.x && pos.y == other.pos.y);
    }

    public boolean passedThrough(BaseGameObject obj)
    {
        if (obj.walkable) return false;
        return  pos.x == obj.oldPos.x &&
                pos.y == obj.oldPos.y &&
                oldPos.x == obj.pos.x &&
                oldPos.y == obj.pos.y;
    }

    public void revert(){
        if (moveTween != null) moveTween.kill();
        Timeline.createSequence()
                .push(Tween.to(renderPos, Vector2Accessor.XY, Assets.MOVE_DELAY/2)
                    .target(tempPos.x, tempPos.y))
                .push(Tween.to(renderPos, Vector2Accessor.XY, Assets.MOVE_DELAY/2)
                        .target(oldPos.x, oldPos.y))
                .start(Assets.tween);
        pos = oldPos.cpy();
    }

    public DIR invertDir(DIR d){
        switch (d){
            case UP: return DIR.DOWN;
            case DOWN: return DIR.UP;
            case LEFT: return DIR.RIGHT;
            case RIGHT: return DIR.LEFT;
        }
        return DIR.UP;
    }

    protected float getRotationFromDir() {
        switch (direction) {
            case UP:    return 0f;
            case DOWN:  return 180f;
            case LEFT:  return 90f;
            case RIGHT: return 270f;
            default:    return 0f;
        }
    }
}
