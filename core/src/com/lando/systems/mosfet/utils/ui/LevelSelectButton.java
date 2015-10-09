package com.lando.systems.mosfet.utils.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.lando.systems.mosfet.world.Level;

/**
 * Created by Doug on 10/8/2015.
 */
public class LevelSelectButton {
    public Level     level;
    public Rectangle bounds;
    public Texture   texture;

    public LevelSelectButton(Level l, Rectangle b) {
        level = l;
        bounds = b;
        final Pixmap pixmap = new Pixmap(l.getWidth(), l.getHeight(), Pixmap.Format.RGB888);
        for (int y = 0; y < l.getHeight(); ++y) {
            for (int x = 0; x < l.getWidth(); ++x) {
                int value = l.getCellAt(x, y) * 100;
                pixmap.drawPixel(x, l.getHeight() - y - 1, Color.rgb888(value, value, value));
            }
        }
        // TODO: dispose of me
        texture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void update(float dt) {

    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
