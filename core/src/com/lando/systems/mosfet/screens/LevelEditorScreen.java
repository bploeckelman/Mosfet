package com.lando.systems.mosfet.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.MosfetGame;
import com.lando.systems.mosfet.gameobjects.GameObjectProps;
import com.lando.systems.mosfet.utils.camera.LevelEditorController;
import com.lando.systems.mosfet.utils.camera.OrthoCamController;
import com.lando.systems.mosfet.utils.ui.ButtonInputListenerAdapter;
import com.lando.systems.mosfet.utils.ui.InfoDialog;
import com.lando.systems.mosfet.utils.ui.editor.LoadLevelDialog;
import com.lando.systems.mosfet.utils.ui.editor.NewLevelDialog;
import com.lando.systems.mosfet.utils.ui.editor.SaveLevelDialog;
import com.lando.systems.mosfet.world.Entity;
import com.lando.systems.mosfet.world.Level;

/**
 * Brian Ploeckelman created on 8/9/2015.
 */
public class LevelEditorScreen extends GameScreen {

    FrameBuffer            sceneFrameBuffer;
    TextureRegion          sceneRegion;
    Skin                   skin;
    Stage                  stage;
//    Window                 windowMenu;
    Table                  windowMenu;
    Window                 windowToolbar;
    InfoDialog             infoDialog;
    OrthographicCamera     uiCamera;
    Entity.Type            selectedEntityType;
    Array<GameObjectProps> objectProps;
    int                    levelWidth;
    int                    levelHeight;
    boolean                eraseMode;
    boolean                linkMode;
    int                    linkageValue;
    Label                  linkageLabel;

    public LevelEditorScreen(MosfetGame game) {
        super(game);

        sceneFrameBuffer = new FrameBuffer(Format.RGBA8888, Config.width, Config.height, false);
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);

        objectProps = new Array<GameObjectProps>();
        levelWidth = -1;
        levelHeight = -1;
        linkMode = false;
        linkageValue = 1;

        initializeUserInterface();
        enableInput();
    }

    public LevelEditorScreen(MosfetGame game, Level level) {
        this(game);
        setLevel(level);
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
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Draw the level
            batch.begin();
            batch.setProjectionMatrix(camera.combined);
            int i = 0;
            for (GameObjectProps props : objectProps) {
                float x = (i % levelWidth) * Level.CELL_WIDTH;
                float y = (i / levelWidth) * Level.CELL_HEIGHT;
                batch.draw(props.getType().getRegion(), x, y, Level.CELL_WIDTH, Level.CELL_HEIGHT);
                ++i;
            }
            batch.end();

            // Draw the user interface
//            stage.setDebugAll(true);
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

    public Level generateLevel() {
        return new Level(levelWidth, levelHeight, objectProps);
    }

    /**
     * Generate GameObjectProps array from Level object
     * @param level Level object to use a template for GameObjectProps array
     */
    public void setLevel(Level level) {
        levelWidth = level.getWidth();
        levelHeight = level.getHeight();
        objectProps.clear();
        int numCells = level.getWidth() * level.getHeight();
        for (int i = 0; i < numCells; ++i) {
            final int cellValue = level.getCellAt(i);
            final Entity.Type entityType = Entity.Type.getTypeForValue(cellValue);
            objectProps.add(new GameObjectProps(entityType));
        }

        camera.position.set((levelWidth  * Level.CELL_WIDTH  * camera.zoom) / 2f,
                            (levelHeight * Level.CELL_HEIGHT * camera.zoom) / 2f, 0);
        camera.update();
    }

    public Entity.Type getSelectedEntityType() {
        return selectedEntityType;
    }

    public boolean isEraseMode() {
        return eraseMode;
    }

    // ------------------------------------------------------------------------
    // Private Implementation
    // ------------------------------------------------------------------------

    private void initializeUserInterface() {
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Config.width, Config.height);
        uiCamera.update(true);
        stage = new Stage(new StretchViewport(Config.width, Config.height));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

//        windowMenu = new Window("LevelEd - Main Menu", skin);
//        windowMenu.setMovable(false);
//        windowMenu.setResizable(false);
        windowMenu = new Table(skin);
        windowMenu.setSize(uiCamera.viewportWidth, 60f);
        windowMenu.setPosition(0f, uiCamera.viewportHeight - 60f);

        windowToolbar = new Window("Toolbar", skin);
        windowToolbar.setMovable(false);
        windowToolbar.setResizable(false);
        windowToolbar.setSize(uiCamera.viewportWidth, 60f);
        windowToolbar.setPosition(0f, 0f);

        infoDialog = new InfoDialog("Info", skin);

        final TextButton playButton   = new TextButton("Play!", skin);
        final TextButton newLevelBtn  = new TextButton("New", skin);
        final TextButton saveLevelBtn = new TextButton("Save", skin);
        final TextButton loadLevelBtn = new TextButton("Load", skin);

        playButton.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (objectProps.size == 0) {
                    infoDialog.resetText("Can't play without a level, silly!", stage);
                    return;
                }
                game.setScreen(new GamePlayScreen(game, generateLevel()));
            }
        });
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

        final CheckBox eraseModeToggle = new CheckBox("Erase Mode", skin);
        eraseModeToggle.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                eraseMode = eraseModeToggle.isChecked();
            }
        });
        eraseModeToggle.setChecked(false);
        eraseMode = false;

        linkageLabel = new Label("#" + linkageValue, skin);
        linkageLabel.setColor(Color.DARK_GRAY);

        final CheckBox linkModeToggle = new CheckBox("Link Mode", skin);
        linkModeToggle.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                linkMode = linkModeToggle.isChecked();
                linkageLabel.setColor((linkMode) ? Color.YELLOW : Color.DARK_GRAY);
                linkageLabel.setText("#" + linkageValue);
            }
        });
        linkModeToggle.setChecked(false);
        linkMode = false;

        final SelectBox<Entity.Type> entityTypeSelect = new SelectBox<Entity.Type>(skin);
        entityTypeSelect.setItems(Entity.Type.values());
        entityTypeSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedEntityType = entityTypeSelect.getSelected();
                eraseModeToggle.setChecked(false);
                eraseMode = false;
            }
        });
        entityTypeSelect.setSelected(Entity.Type.BLANK);
        selectedEntityType = Entity.Type.BLANK;

        windowMenu.left().add(playButton).fillX().expandX().padRight(15f);
        windowMenu.left().add(newLevelBtn);
        windowMenu.left().add(saveLevelBtn);
        windowMenu.left().add(loadLevelBtn);
        windowMenu.row();

        windowToolbar.left().add(entityTypeSelect).fillX().expandX().padRight(15f);
        windowToolbar.left().add(eraseModeToggle).padRight(15f);
        windowToolbar.left().add(linkModeToggle).padRight(5f);
        windowToolbar.left().add(linkageLabel).padRight(5f);
        windowToolbar.row();

        stage.addActor(windowMenu);
        stage.addActor(windowToolbar);
    }

    public void newLevel(int width, int height) {
        infoDialog.resetText("Instantiated new level of size: " + width + "x" + height);
        infoDialog.show(stage);

        levelWidth = width;
        levelHeight = height;
        objectProps.clear();
        for (int i = 0; i < width * height; ++i) {
            objectProps.add(new GameObjectProps(Entity.Type.BLANK));
        }
    }

    public void setPropsAt(int cellX, int cellY, Entity.Type entityType) {
        if (cellX < 0 || cellX >= levelWidth || cellY < 0 || cellY >= levelHeight) {
            return;
        }

        int index = cellY * levelWidth + cellX;
        objectProps.set(index, new GameObjectProps(entityType));
    }

}
