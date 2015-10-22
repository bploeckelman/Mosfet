package com.lando.systems.mosfet.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.lando.systems.mosfet.MosfetGame;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.utils.ui.LevelSelectButton;

/**
 * Created by Doug on 10/8/2015.
 */
public class LevelSelectScreen extends GameScreen {

    Array<LevelSelectButton> levelBtns;
    int LEVELS_PER_ROW = 4;
    float LEVEL_BUTTON_MARGIN = 20;
    float SCREEN_TOP_MARGIN = 160;

    public LevelSelectScreen(MosfetGame game) {
        super(game);
        levelBtns = new Array<LevelSelectButton>();
        float btnWidth = (uiCamera.viewportWidth - (LEVEL_BUTTON_MARGIN  * (LEVELS_PER_ROW + 1))) / LEVELS_PER_ROW;
        for (int i = 0; i < Assets.levels.size; i++){
            float x = LEVEL_BUTTON_MARGIN + ((i % LEVELS_PER_ROW) * (LEVEL_BUTTON_MARGIN + btnWidth));
            float y = uiCamera.viewportHeight - SCREEN_TOP_MARGIN - ( (i / LEVELS_PER_ROW) * (btnWidth + LEVEL_BUTTON_MARGIN));
            levelBtns.add( new LevelSelectButton(Assets.levels.get(i), new Rectangle(x, y, btnWidth, btnWidth)));
        }
    }

    public void update(float dt){
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            Gdx.app.exit();
        }
        if (Gdx.input.justTouched()){
            Vector3 camTouchPointV3 = uiCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            Vector2 camTouchPoint = new Vector2(camTouchPointV3.x, camTouchPointV3.y);
            for (LevelSelectButton btn : levelBtns){
                if (btn.bounds.contains(camTouchPoint)){
                    if (btn.editBounds.contains(camTouchPoint)) {
                        game.setScreen(new LevelEditorScreen(game, btn.level));
                    } else {
                        game.setScreen(new GamePlayScreen(game, btn.level));
                    }
                }
            }
        }
    }

    public void render(float dt){
        Gdx.gl.glClearColor(1f, 0.2f, 0.6f, 1f);

        update(dt);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin();
        batch.setProjectionMatrix(uiCamera.combined);
        for (LevelSelectButton btn : levelBtns){
            btn.render(batch);
        }
        batch.end();
    }
}
