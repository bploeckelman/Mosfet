package com.lando.systems.mosfet.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.MosfetGame;
import com.lando.systems.mosfet.gameobjects.GameObjectProps;
import com.lando.systems.mosfet.utils.Assets;
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
public class LevelEditorScreen extends GameScreen implements InputProcessor {

    private static final float UI_MARGIN = 15f;

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
    GlyphLayout            glyphLayout;

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
        glyphLayout = new GlyphLayout();

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
            float hw = Level.CELL_WIDTH / 2f;
            float hh = Level.CELL_HEIGHT / 2f;
            int i = 0;
            for (GameObjectProps props : objectProps) {
                float x = (i % levelWidth) * Level.CELL_WIDTH;
                float y = (i / levelWidth) * Level.CELL_HEIGHT;
                if (linkMode) batch.setColor(0.4f, 0.4f, 0.4f, 1f);
                else          batch.setColor(Color.WHITE);
                batch.draw(props.getType().getRegion(), x, y, Level.CELL_WIDTH, Level.CELL_HEIGHT);

                int linkId = props.getLinkages();
                if (linkMode && linkId != 0) {
                    final String linkIdStr = "" + linkId;
                    glyphLayout.setText(Assets.font, linkIdStr);
                    // TODO: set color based on linkId
                    batch.setColor(Color.WHITE);
                    Assets.font.draw(batch, linkIdStr, x + hw - glyphLayout.width / 2f, y + hh + glyphLayout.height / 2f);
                }
                ++i;
            }
            batch.end();

            // Draw the user interface
//            stage.setDebugAll(true);
            batch.setColor(Color.WHITE);
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
        mux.addProcessor(this);
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
            objectProps.add(new GameObjectProps(level.getCellAt(i)));
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

        final ImageButton playButton = new ImageButton(new TextureRegionDrawable(Assets.uiPlayButtonRegion),
                                                       new TextureRegionDrawable(Assets.uiPlayButtonDownRegion));
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

        final ImageButton newButton = new ImageButton(new TextureRegionDrawable(Assets.uiNewButtonRegion),
                                                      new TextureRegionDrawable(Assets.uiNewButtonDownRegion));
        newButton.addListener(new TextTooltip("New Level", skin));
        newButton.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                new NewLevelDialog("New Level", skin, LevelEditorScreen.this).show(stage);
            }
        });

        final ImageButton saveButton = new ImageButton(new TextureRegionDrawable(Assets.uiSaveButtonRegion),
                                                       new TextureRegionDrawable(Assets.uiSaveButtonDownRegion));
        saveButton.addListener(new TextTooltip("Save Level", skin));
        saveButton.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                new SaveLevelDialog("Save Level", skin, LevelEditorScreen.this).show(stage);
            }
        });

        final ImageButton loadButton = new ImageButton(new TextureRegionDrawable(Assets.uiLoadButtonRegion),
                                                       new TextureRegionDrawable(Assets.uiLoadButtonDownRegion));
        loadButton.addListener(new TextTooltip("Load Level", skin));
        loadButton.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                new LoadLevelDialog("Load Level", skin, LevelEditorScreen.this).show(stage);
            }
        });

        final ImageButton eraseModeButton = new ImageButton(new TextureRegionDrawable(Assets.uiEraseButtonRegion),
                                                            new TextureRegionDrawable(Assets.uiEraseButtonDownRegion),
                                                            new TextureRegionDrawable(Assets.uiEraseButtonCheckedRegion));
        eraseModeButton.addListener(new TextTooltip("Erase Mode", skin));
        eraseModeButton.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                eraseMode = eraseModeButton.isChecked();
            }
        });
        eraseMode = false;

        linkageLabel = new Label("#" + linkageValue, skin);
        linkageLabel.setColor(Color.DARK_GRAY);
        // TODO: handle changing link id more effectively
        linkageLabel.addListener(new ClickListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                // Increment link id, but keep within 8 bits
                if (++linkageValue > 255) {
                    linkageValue -= 255;
                }
                linkageLabel.setColor((linkMode) ? Color.YELLOW : Color.DARK_GRAY);
                linkageLabel.setText("#" + linkageValue);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        final ImageButton linkModeButton = new ImageButton(new TextureRegionDrawable(Assets.uiLinkButtonRegion),
                                                           new TextureRegionDrawable(Assets.uiLinkButtonDownRegion),
                                                           new TextureRegionDrawable(Assets.uiLinkButtonCheckedRegion));
        linkModeButton.addListener(new TextTooltip("Link Mode", skin));
        linkModeButton.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                linkMode = linkModeButton.isChecked();
                linkageLabel.setColor((linkMode) ? Color.YELLOW : Color.DARK_GRAY);
                linkageLabel.setText("#" + linkageValue);
            }
        });
        linkMode = false;

        final SelectBox<Entity.Type> entityTypeSelect = new SelectBox<Entity.Type>(skin);
        entityTypeSelect.setItems(Entity.Type.values());
        entityTypeSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedEntityType = entityTypeSelect.getSelected();
                eraseModeButton.setChecked(false);
                eraseMode = false;
            }
        });
        entityTypeSelect.setSelected(Entity.Type.BLANK);
        selectedEntityType = Entity.Type.BLANK;

        windowMenu.left().padLeft(UI_MARGIN).add(playButton).expandX();
        windowMenu.left().add(newButton).expandX();
        windowMenu.left().add(saveButton).expandX();
        windowMenu.left().add(loadButton).expandX().padRight(UI_MARGIN);
        windowMenu.row();

        windowToolbar.left().add(entityTypeSelect).fillX().expandX().padRight(15f);
        windowToolbar.left().add(eraseModeButton).padRight(15f);
        windowToolbar.left().add(linkModeButton).padRight(5f);
        windowToolbar.left().add(linkageLabel).padRight(5f);
        windowToolbar.row();

        stage.addActor(windowMenu);
        stage.addActor(windowToolbar);
    }

    // ------------------------------------------------------------------------
    // Utility Methods
    // ------------------------------------------------------------------------

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

    // ------------------------------------------------------------------------
    // InputProcessor Interface
    // ------------------------------------------------------------------------

    private boolean leftButtonDown = false;
    private int lastCellClickedX = -1;
    private int lastCellClickedY = -1;

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            leftButtonDown = true;
            int cellX = (int) (getMouseWorldPos().x / Level.CELL_WIDTH);
            int cellY = (int) (getMouseWorldPos().y / Level.CELL_HEIGHT);

            if (linkMode) {
                if (cellX >= 0 && cellX < Level.CELL_WIDTH && cellY >= 0 && cellY < Level.CELL_HEIGHT) {
                    int index = cellY * levelWidth + cellX;
                    final GameObjectProps props = objectProps.get(index);
                    props.linkages = linkageValue;
                    props.updateBits();
                }
            } else {
                updateLevelWithClickAt(cellX, cellY);
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (leftButtonDown && button == 0) {
            leftButtonDown = false;
            lastCellClickedX = -1;
            lastCellClickedY = -1;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (leftButtonDown && !linkMode) {
            int cellX = (int) (getMouseWorldPos().x / Level.CELL_WIDTH);
            int cellY = (int) (getMouseWorldPos().y / Level.CELL_HEIGHT);
            if (cellX != lastCellClickedX || cellY != lastCellClickedY) {
                updateLevelWithClickAt(cellX, cellY);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    // ------------------------------------------------------------------------
    // Private Implementation
    // ------------------------------------------------------------------------

    private void updateLevelWithClickAt(int cellX, int cellY) {
        // TODO: spawn validation (during editing or on play/save?)
        setPropsAt(cellX, cellY, (isEraseMode()) ? Entity.Type.BLANK : getSelectedEntityType());
        lastCellClickedX = cellX;
        lastCellClickedY = cellY;
    }

}
