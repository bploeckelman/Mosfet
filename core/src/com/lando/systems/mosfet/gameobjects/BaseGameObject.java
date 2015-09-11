package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Karla on 9/10/2015.
 */
public class BaseGameObject {
    public enum DIR {UP, DOWN, LEFT, RIGHT}

    public Vector2 pos;
    public Vector2 oldPos;
    public Vector2 targetPos;
    public TextureRegion tex;
    public DIR direction;
    public boolean stationary;
    public boolean walkable;
    public boolean conflict;


    public BaseGameObject(Vector2 p){
        tex = new TextureRegion(Assets.stoneTexture);
        pos = p;
        oldPos = p.cpy();
        targetPos = p.cpy();
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
        switch (direction){
            case UP:
                targetPos.y = forward ? pos.y + 1 : pos.y -1;
                break;
            case RIGHT:
                targetPos.x = forward ? pos.x + 1 : pos.x -1;
                break;
            case DOWN:
                targetPos.y = forward ? pos.y - 1 : pos.y +1;
                break;
            case LEFT:
                targetPos.x = forward ? pos.x - 1 : pos.x +1;
                break;
        }
        pos = targetPos;
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
        pos = oldPos;
        targetPos = oldPos;
    }
}
