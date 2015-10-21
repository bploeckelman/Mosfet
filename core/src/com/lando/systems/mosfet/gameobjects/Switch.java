package com.lando.systems.mosfet.gameobjects;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Created by Doug on 9/24/2015.
 */
public class Switch extends  BaseGameObject {
    Array<Door> doors;
    Material material;
    TextureAttribute activeDiffuseTexture;
    TextureAttribute inactiveDiffuseTexture;
    BaseGameObject interactingObject;
    MutableFloat timer;

    public Switch(Vector2 p, GameObjectProps props) {
        super(p, props);
        tex = Assets.switchRegion;
        modelInstance = new ModelInstance(Assets.switchModel);
        modelInstance.transform.setToTranslation(p.x, p.y, 0);
        material = modelInstance.materials.get(0);
        inactiveDiffuseTexture = (TextureAttribute) material.get(TextureAttribute.Diffuse);
        activeDiffuseTexture = TextureAttribute.createDiffuse(Assets.switchActiveTexture);
        doors = new Array<Door>();
        walkable = true;
        interactable = true;
        interactingObject = null;
        timer = new MutableFloat(0f);
    }

    public void linkObject(BaseGameObject obj){
        if (obj instanceof Door) doors.add((Door)obj);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (interactingObject != null) {
            if (interactingObject.pos.x != pos.x || interactingObject.pos.y != pos.y) {
                interactingObject = null;
                timer.setValue(0f);
                Tween.to(timer, -1, Assets.MOVE_DELAY)
                     .target(1f)
                     .setCallback(new TweenCallback() {
                         @Override
                         public void onEvent(int i, BaseTween<?> baseTween) {
                             material.set(inactiveDiffuseTexture);
                         }
                     })
                     .start(Assets.tween);
            }
        }
    }

    public void interactWith(BaseGameObject obj, GamePlayScreen screen){
        if (obj != null){
            for (Door d : doors){
                d.trigger();
                d.usedThisTurn = true;
            }

            if (obj instanceof Player || obj instanceof DumbRobot) {
                interactingObject = obj;
                timer.setValue(0f);
                Tween.to(timer, -1, Assets.MOVE_DELAY/2)
                     .target(1f)
                     .setCallback(new TweenCallback() {
                         @Override
                         public void onEvent(int i, BaseTween<?> baseTween) {
                             material.set(activeDiffuseTexture);
                         }
                     })
                     .start(Assets.tween);
            }
        }
    }
}
