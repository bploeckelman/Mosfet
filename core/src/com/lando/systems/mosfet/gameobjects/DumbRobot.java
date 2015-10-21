package com.lando.systems.mosfet.gameobjects;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/24/2015.
 */
public class DumbRobot extends BaseGameObject {

    public DumbRobot(Vector2 p, GameObjectProps props) {
        this(p, props, DIR.UP);
    }

    public DumbRobot(Vector2 p, GameObjectProps props, DIR d){
        super(p, props);
        modelInstance = new ModelInstance(Assets.robotModel);
        modelInstance.transform.setToTranslation(p.x, p.y, 0);
        stationary = false;
        canRotate = true;
        direction = d;
        rotationAngleDeg = new MutableFloat(getRotationFromDir());
        tex = Assets.aiRegion;
    }

    public void move(boolean forward, GamePlayScreen screen){
        if (forward){
            BaseGameObject obj = screen.getObjectAt(getFront());
            if (obj != null) obj.bePushed(direction);
            obj = screen.getObjectAt(getBehind());
            if (obj != null) obj.bePulled(direction);
        } else {
            BaseGameObject obj = screen.getObjectAt(getFront());
            if (obj != null) obj.bePulled(invertDir(direction));
            obj = screen.getObjectAt(getBehind());
            if (obj != null) obj.bePushed(invertDir(direction));
        }
        moveDir(forward ? direction : invertDir(direction));
    }
}
