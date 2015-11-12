package com.lando.systems.mosfet.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.MosfetGame;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.utils.ui.ButtonInputListenerAdapter;
import com.lando.systems.mosfet.utils.ui.InfoDialog;
import com.lando.systems.mosfet.world.Level;

/**
 * Brian Ploeckelman created on 11/8/2015.
 */
public class MainMenuScreen extends GameScreen {

    FrameBuffer       sceneFrameBuffer;
    TextureRegion     sceneRegion;
    Stage             stage;
    Skin              skin;
    GlyphLayout       glyphLayout1;
    GlyphLayout       glyphLayout2;

    public MainMenuScreen(MosfetGame game) {
        super(game);

        sceneFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Config.width, Config.height, false);
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);

        glyphLayout1 = new GlyphLayout(Assets.resistorFont64, "Parallel");
        glyphLayout2 = new GlyphLayout(Assets.resistorFont64, "Resistor");

        initializeUI();
        enableInput();

        Gdx.gl.glClearColor(0.2f, 0.3f, 0.4f, 1f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            Gdx.app.exit();
        }
        stage.act(delta);
    }

    @Override
    public void render(float delta) {
        update(delta);

        sceneFrameBuffer.begin();
        {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            batch.setProjectionMatrix(camera.combined);
            Assets.resistorFont64.draw(batch,
                                       "Parallel",
                                       camera.viewportWidth / 2f - glyphLayout1.width / 2f - 40f,
                                       camera.viewportHeight - glyphLayout1.height - 10f);
            Assets.resistorFont64.draw(batch,
                                       "Resistor",
                                       camera.viewportWidth / 2f - glyphLayout2.width / 2f + 50f,
                                       camera.viewportHeight - glyphLayout2.height - 10f - glyphLayout1.height - 40f);
            batch.end();

            stage.draw();
        }
        sceneFrameBuffer.end();

        batch.setShader(null);
        batch.begin();
        {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.setProjectionMatrix(uiCamera.combined);
            batch.draw(sceneRegion, 0, 0);
        }
        batch.end();
    }

    @Override
    protected void enableInput() {
        final InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(stage);
        Gdx.input.setInputProcessor(mux);
    }

    // -----------------------------------------------------------------------
    // Private Implementation Details
    // -----------------------------------------------------------------------

    private void initializeUI() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage = new Stage(new StretchViewport(Config.width, Config.height));

        final InfoDialog infoDlg = new InfoDialog("Info", skin);

        final TextButton storyModeBtn = new TextButton("Story Mode", skin);
        storyModeBtn.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                final FileHandle levelFile = Gdx.files.internal("levels/level1.lvl");
                if (!levelFile.exists()) {
                    infoDlg.resetText("Unable to start story mode, missing level 1 map file", stage);
                    return;
                }
                game.setScreen(new GamePlayScreen(game, (new Json()).fromJson(Level.class, levelFile)));
            }
        });

        final TextButton customLevelModeBtn = new TextButton("Custom Level", skin);
        customLevelModeBtn.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // TODO: custom level input screen
                infoDlg.resetText("Custom level screen not yet implemented", stage);
            }
        });

        final TextButton randomLevelModeBtn = new TextButton("Random Level", skin);
        randomLevelModeBtn.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // TODO: random level input screen
                infoDlg.resetText("Random level screen not yet implemented", stage);
            }
        });

        final TextButton levelEditorModeBtn = new TextButton("Level Editor", skin);
        levelEditorModeBtn.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new LevelEditorScreen(game));
            }
        });

        final Window menuWindow = new Window("Main Menu", skin);
        menuWindow.setSize(3f * Config.width / 4f, Config.height / 2f);
        menuWindow.setResizable(false);
        menuWindow.setMovable(false);

        menuWindow.add(storyModeBtn)      .expand().fill().row();
        menuWindow.add(customLevelModeBtn).expand().fill().row();
        menuWindow.add(randomLevelModeBtn).expand().fill().row();
        menuWindow.add(levelEditorModeBtn).expand().fill().row();

        menuWindow.setPosition(Config.width / 2f - menuWindow.getWidth() / 2f,
                               Config.height / 2f - menuWindow.getHeight() / 2f - 100f);

        stage.addActor(menuWindow);
    }

}
