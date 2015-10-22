package com.lando.systems.mosfet.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Sine;
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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.MosfetGame;
import com.lando.systems.mosfet.gameobjects.GameObjectProps;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.utils.accessors.RectangleAccessor;
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

    private static final float UI_MARGIN              = 15f;
    private static final float UI_PICKER_MARGIN       = 10f;
    private static final float TWEEN_TIME_PICKER_HIDE = 0.3f;
    private static final float TWEEN_TIME_PICKER_SHOW = 0.15f;

    FrameBuffer            sceneFrameBuffer;
    TextureRegion          sceneRegion;
    Skin                   skin;
    Stage                  stage;
    Table                  headerTable;
    Table                  footerTable;
    ScrollPane             tilePickerPane;
    ImageButton            brushButton;
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
    Rectangle              tilePickerBounds;
    Rectangle              tilePickerShowBounds;

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
        tilePickerPane.setPosition(tilePickerBounds.x, tilePickerBounds.y);
        tilePickerPane.setSize(tilePickerBounds.width, tilePickerBounds.height);
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
                else batch.setColor(Color.WHITE);
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
        infoDialog = new InfoDialog("Info", skin);

        // Initialize header table --------------------------------------------

        headerTable = new Table(skin);
        headerTable.setSize(uiCamera.viewportWidth, 60f);
        headerTable.setPosition(0f, uiCamera.viewportHeight - 60f);

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

        headerTable.left().padLeft(UI_MARGIN);
        headerTable.left().add(playButton).expandX();
        headerTable.left().add(newButton).expandX();
        headerTable.left().add(saveButton).expandX();
        headerTable.left().add(loadButton).expandX();
        headerTable.left().padRight(UI_MARGIN);
        headerTable.row();

        // Initialize footer table --------------------------------------------

        footerTable = new Table(skin);
        footerTable.setSize(uiCamera.viewportWidth, 60f);
        footerTable.setPosition(0f, 0f);

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

        // Initialize tile picker ---------------------------------------------

        selectedEntityType = Entity.Type.BLANK;
        brushButton = new ImageButton(new TextureRegionDrawable(Entity.Type.BLANK.getRegion()));
        brushButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                tilePickerPane.setVisible(true);

                Tween.to(tilePickerBounds, RectangleAccessor.XYWH, TWEEN_TIME_PICKER_SHOW)
                     .target(tilePickerShowBounds.x,
                             tilePickerShowBounds.y,
                             tilePickerShowBounds.width,
                             tilePickerShowBounds.height)
                     .ease(Sine.OUT)
                     .start(Assets.tween);

                return super.touchDown(event, x, y, pointer, button);
            }
        });
        brushButton.setSize(64, 64);

        final Table tilePickerTable = new Table(skin);
        tilePickerTable.pad(UI_PICKER_MARGIN);
        int cell = 0;
        for (final Entity.Type entityType : Entity.Type.values()) {
            final ImageButton typeButton = new ImageButton(new TextureRegionDrawable(entityType.getRegion()));
            typeButton.getImage().setFillParent(true);
            typeButton.addListener(new TextTooltip(entityType.toString(), skin));
            typeButton.addListener(new ClickListener() {
                @Override
                public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    selectedEntityType = entityType;

                    final ImageButton.ImageButtonStyle style = brushButton.getStyle();
                    style.imageUp = new TextureRegionDrawable(entityType.getRegion());
                    brushButton.setStyle(style);

                    Tween.to(tilePickerBounds, RectangleAccessor.XYWH, TWEEN_TIME_PICKER_HIDE)
                         .target(brushButton.getX(), brushButton.getY(), brushButton.getWidth(), brushButton.getHeight())
                         .ease(Sine.OUT)
                         .setCallback(new TweenCallback() {
                             @Override
                             public void onEvent(int i, BaseTween<?> baseTween) {
                                 tilePickerPane.setVisible(false);
                             }
                         })
                         .start(Assets.tween);
                }
            });
            tilePickerTable.left().add(typeButton).width(35f).height(35f).expand().fill().pad(5f);

            if (++cell % 2 == 0) {
                tilePickerTable.row();
                tilePickerTable.pad(UI_PICKER_MARGIN);
            }
        }

        tilePickerBounds = new Rectangle(uiCamera.viewportWidth  / 2f - uiCamera.viewportWidth  / 6f,
                                         uiCamera.viewportHeight / 2f - uiCamera.viewportHeight / 6f,
                                         uiCamera.viewportWidth  / 3f,
                                         uiCamera.viewportHeight / 3f);
        tilePickerShowBounds = new Rectangle(tilePickerBounds);

        tilePickerPane = new ScrollPane(tilePickerTable, skin);
        tilePickerPane.setVisible(false);
        tilePickerPane.setScrollingDisabled(true, false);
        tilePickerPane.setPosition(tilePickerBounds.x, tilePickerBounds.y);
        tilePickerPane.setSize(tilePickerBounds.width, tilePickerBounds.height);

        footerTable.left().padLeft(UI_MARGIN);
        footerTable.left().add(brushButton).expandX();
        footerTable.left().add(eraseModeButton).expandX();
        footerTable.left().add(linkModeButton).expandX();
        footerTable.left().add(linkageLabel);
        footerTable.left().padRight(UI_MARGIN);
        footerTable.row();

        // Add widgets to stage -----------------------------------------------

        stage.addActor(headerTable);
        stage.addActor(tilePickerPane);
        stage.addActor(footerTable);
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
                    props.linkages = (eraseMode ? 0 : linkageValue);
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
