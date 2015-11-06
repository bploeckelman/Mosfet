package com.lando.systems.mosfet.world;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.screens.LevelSelectScreen;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.utils.accessors.RectangleAccessor;

/**
 * Created by Doug on 11/5/2015.
 */
public class ExitScreenPanel {
    GamePlayScreen screen;
    public boolean active;
    public Rectangle bounds;

    public ExitScreenPanel (GamePlayScreen screen){
        this.screen = screen;
        reset();
    }

    public void reset(){
        active = false;
        bounds = new Rectangle(40, -screen.uiCamera.viewportHeight, screen.uiCamera.viewportWidth - 80, screen.uiCamera.viewportHeight - 80);

    }

    public void activate(float delay){
        active = true;
        Tween.to(bounds, RectangleAccessor.Y, 1f)
                .target(40)
                .delay(delay)
                .start(Assets.tween);
    }

    public void update(float dt){
        if (active){
            if (Gdx.input.justTouched()){
                screen.game.setScreen(new LevelSelectScreen(screen.game));
            }
        }
    }

    public void render(SpriteBatch batch){
        batch.draw(Assets.blankRegion, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
