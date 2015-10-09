package com.lando.systems.mosfet.utils.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.world.Level;

/**
 * Created by Doug on 10/8/2015.
 */
public class LevelSelectButton {
    public Level level;
    public Rectangle bounds;

    public LevelSelectButton(Level l, Rectangle b){
        level = l;
        bounds = b;
    }

    public void update(float dt){

    }

    public void render(SpriteBatch batch){
        batch.draw(Assets.testTexture, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
