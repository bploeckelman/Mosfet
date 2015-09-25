package com.lando.systems.mosfet.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.MosfetGame;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.utils.camera.LevelEditorController;
import com.lando.systems.mosfet.utils.camera.OrthoCamController;
import com.lando.systems.mosfet.utils.ui.ButtonInputListenerAdapter;
import com.lando.systems.mosfet.utils.ui.InfoDialog;
import com.lando.systems.mosfet.utils.ui.editor.EntityLinkDialog;
import com.lando.systems.mosfet.utils.ui.editor.LoadLevelDialog;
import com.lando.systems.mosfet.utils.ui.editor.NewLevelDialog;
import com.lando.systems.mosfet.utils.ui.editor.SaveLevelDialog;
import com.lando.systems.mosfet.world.Entity;
import com.lando.systems.mosfet.world.Level;

/**
 * Brian Ploeckelman created on 8/9/2015.
 */
public class LevelEditorScreen extends GameScreen {

    FrameBuffer        sceneFrameBuffer;
    TextureRegion      sceneRegion;
    Skin               skin;
    Stage              stage;
    Window             window;
    InfoDialog         infoDialog;
    OrthographicCamera uiCamera;
    Level              level;
    Entity.Type        selectedEntityType;
    boolean            removalMode;

    public LevelEditorScreen(MosfetGame game) {
        super(game);

        sceneFrameBuffer = new FrameBuffer(Format.RGBA8888, Config.width, Config.height, false);
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);

        initializeUserInterface();
        enableInput();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stage.act(delta);
    }

    @Override
    public void render(float delta) {
        update(delta);

        sceneFrameBuffer.begin();
        {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Draw the level
            batch.begin();
            batch.setProjectionMatrix(camera.combined);
            if (level != null) {
                level.render(batch);
            }
            batch.end();

            // Draw the user interface
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
        mux.addProcessor(new OrthoCamController(camera));
        mux.addProcessor(new LevelEditorController(this));
        Gdx.input.setInputProcessor(mux);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public InfoDialog getInfoDialog() {
        return infoDialog;
    }

    public Stage getStage() {
        return stage;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Entity.Type getSelectedEntityType() {
        return selectedEntityType;
    }

    public boolean isRemovalMode() {
        return removalMode;
    }

    // ------------------------------------------------------------------------
    // Private Implementation
    // ------------------------------------------------------------------------

    private void initializeUserInterface() {
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Config.width, Config.height);
        stage = new Stage(new ScreenViewport());
        stage.getViewport().setCamera(uiCamera);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        window = new Window("LevelEd", skin);
        window.setMovable(false);
        window.setResizable(false);
        window.setSize(camera.viewportWidth, 60f);
        window.setPosition(0f, camera.viewportHeight - 60f);
        window.setZIndex(0);

        infoDialog = new InfoDialog("Info", skin);

        final TextButton newLevelBtn  = new TextButton("New", skin);
        final TextButton saveLevelBtn = new TextButton("Save", skin);
        final TextButton loadLevelBtn = new TextButton("Load", skin);

        newLevelBtn.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                new NewLevelDialog("New Level", skin, LevelEditorScreen.this).show(stage);
            }
        });
        saveLevelBtn.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                new SaveLevelDialog("Save Level", skin, LevelEditorScreen.this).show(stage);
            }
        });
        loadLevelBtn.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                new LoadLevelDialog("Load Level", skin, LevelEditorScreen.this).show(stage);
            }
        });

        final CheckBox removalModeCheckBox = new CheckBox("Remove", skin);
        removalModeCheckBox.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                removalMode = removalModeCheckBox.isChecked();
            }
        });
        removalModeCheckBox.setChecked(false);
        removalMode = false;

        final SelectBox<Entity.Type> entityTypeSelect = new SelectBox<Entity.Type>(skin);
        entityTypeSelect.setItems(Entity.Type.values());
        entityTypeSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedEntityType = entityTypeSelect.getSelected();
                removalModeCheckBox.setChecked(false);
                removalMode = false;
            }
        });
        entityTypeSelect.setSelected(Entity.Type.BLANK);
        selectedEntityType = Entity.Type.BLANK;

        final TextButton entityLinkButton = new TextButton("Link", skin);
        entityLinkButton.addListener(new ButtonInputListenerAdapter() {
             @Override
             public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                 new EntityLinkDialog("Link Entities", skin, LevelEditorScreen.this).show(stage);
             }
        });

        TextButton playButton = new TextButton("Play!", skin);
        playButton.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (level == null) {
                    infoDialog.resetText("Can't play without a level, silly!", stage);
                    return;
                }

                game.setScreen(new GamePlayScreen(game, level));
            }
        });

//        window.top().left().add("Entity Type").padRight(15f);
        window.top().left().add(playButton).padRight(15f).fillX().expandX();
        window.top().left().add(entityTypeSelect).width(100f).fillX().padRight(5f);
        window.top().left().add(entityLinkButton).fillX().padRight(15f);
        window.top().right().add(removalModeCheckBox).padRight(15f);
        window.top().right().add(newLevelBtn);
        window.top().right().add(saveLevelBtn);
        window.top().right().add(loadLevelBtn);
        window.row();
        final float titleHeight = window.getTitleLabel().getHeight();
        window.padTop(titleHeight);

        stage.addActor(window);
    }

    public void newLevel(int width, int height) {
        infoDialog.resetText("Instantiated new level of size: " + width + "x" + height);
        infoDialog.show(stage);
        level = new Level(width, height);
    }

}
