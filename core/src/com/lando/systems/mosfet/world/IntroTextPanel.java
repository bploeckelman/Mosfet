package com.lando.systems.mosfet.world;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.utils.accessors.RectangleAccessor;
import com.lando.systems.mosfet.utils.accessors.Vector2Accessor;

/**
 * Created by Doug on 10/22/2015.
 */
public class IntroTextPanel {

    public String text;
    public boolean fullScreen;
    public Rectangle bounds;
    public Vector2 textPosition;
    private float textHeight;
    private GlyphLayout layout;

    public IntroTextPanel(String text, OrthographicCamera cam){
        this.text = text;
        fullScreen = true;
        bounds = new Rectangle(40, 40, cam.viewportWidth - 80, cam.viewportHeight - 80);
        layout = new GlyphLayout();
        setLayout();
        textHeight = layout.height;
        textPosition = new Vector2((cam.viewportWidth - layout.width) /2f, (cam.viewportHeight + layout.height )/ 2f);
    }

    public boolean update(float dt){
        if (!fullScreen) return false;
        if (Gdx.input.justTouched()){
            fullScreen = false;
            Tween.to(bounds, RectangleAccessor.Y, 1f)
                    .target(bounds.y - bounds.height + textHeight + 40)
                    .start(Assets.tween);
            Tween.to(textPosition, Vector2Accessor.Y, 1f)
                    .target(30 + textHeight)
                    .start(Assets.tween);
        }
        return true;
    }

    public void render(SpriteBatch batch)
    {
        setLayout();
        batch.draw(Assets.blankRegion, bounds.x, bounds.y, bounds.width, bounds.height);
        Assets.font.draw(batch, layout, textPosition.x, textPosition.y);
    }

    private void setLayout(){
        layout.setText(Assets.font, text, Color.WHITE, bounds.width - 80, Align.left, true);
    }

}
