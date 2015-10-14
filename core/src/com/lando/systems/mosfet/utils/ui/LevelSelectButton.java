package com.lando.systems.mosfet.utils.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.world.Level;

/**
 * Created by Doug on 10/8/2015.
 */
public class LevelSelectButton {
    private static final float accessoryButtonScale = 0.3333f;
    public Level     level;
    public Rectangle bounds;
    public Rectangle editBounds;
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

        final float xOffset = b.width * accessoryButtonScale;
        final float yOffset = b.height * accessoryButtonScale;
        editBounds = new Rectangle(b.x + b.width - xOffset, b.y + b.height - yOffset, xOffset, yOffset);
    }

    public void update(float dt) {

    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.draw(Assets.gearTexture, editBounds.x, editBounds.y, editBounds.width, editBounds.height);
    }
}
