package com.lando.systems.mosfet.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Sine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.MosfetGame;
import com.lando.systems.mosfet.gameobjects.*;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.utils.accessors.Vector3Accessor;
import com.lando.systems.mosfet.utils.camera.PerspectiveCameraController;
import com.lando.systems.mosfet.world.*;

/**
 * Brian Ploeckelman created on 9/10/2015.
 */
public class GamePlayScreen extends GameScreen {


    FrameBuffer             sceneFrameBuffer;
    TextureRegion           sceneRegion;
    public Level                   level;
    Array<BaseGameObject>   gameObjects;
    Array<ModelInstance>    floorCellInstances;
    Background              background;
    public Player                  player;
    float                   movementDelay;
    Rectangle               forwardButton;
    Rectangle               backwardButton;
    Rectangle               turnRightButton;
    Rectangle               turnLeftButton;
    Rectangle               rotateCameraLeftButton;
    Rectangle               rotateCameraRightButton;
    Rectangle               resetLevelButton;
    PerspectiveCamera       perspectiveCamera;
    boolean                 renderAs3d;

    PerspectiveCameraController perCamController;
    Vector3                 spawnPosition;
    Vector3                 exitPosition;
    Vector3                 cameraPosition;
    Vector3                 cameraLookAt;
    IntroTextPanel          introText;
    ExitScreenPanel         exitPanel;

    int                     movesTaken;


    public GamePlayScreen(MosfetGame game, Level level) {
        super(game);

        if (level == null) {
            throw new GdxRuntimeException("GamePlayScreen requires a valid level");
        }
        spawnPosition = new Vector3();
        exitPosition = new Vector3();
        this.level = level;
        int levelWidth = level.getWidth();
        int levelHeight = level.getHeight();

        Gdx.gl.glClearColor(0f, 0.2f, 0.6f, 1f);

        // Fit the map while maintaining the crrect aspect ratio
        float aspect = Config.width/(float)Config.height;
        float levelAspect = levelWidth/ (float)levelHeight;
        float cameraWidth;
        float cameraHeight;
        Vector2 cameraOffset = new Vector2();
        if (levelAspect >= aspect){
            cameraWidth = levelWidth;
            cameraHeight = levelWidth / aspect;
            cameraOffset.x = 0;
            cameraOffset.y = - ((cameraHeight - levelHeight)/2);
        } else {
            cameraWidth = levelHeight * aspect;
            cameraHeight = levelHeight;
            cameraOffset.x = - ((cameraWidth - levelWidth)/2);
            cameraOffset.y = 0;
        }
        camera.setToOrtho(false, cameraWidth, cameraHeight);
        camera.translate(cameraOffset);
        camera.update();

        cameraPosition = new Vector3(spawnPosition.x, spawnPosition.y, 5);
        cameraLookAt = new Vector3(spawnPosition);

        perspectiveCamera = new PerspectiveCamera(45, Config.width, Config.height);
        perspectiveCamera.up.set(0,0,1);
        perspectiveCamera.position.set(cameraPosition);
        perspectiveCamera.lookAt(cameraLookAt);

        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 300f;
        perspectiveCamera.update();
        renderAs3d = true;
        perCamController = new PerspectiveCameraController(perspectiveCamera, this);

        background = new Background(new Vector2(levelWidth, levelHeight));
        introText = new IntroTextPanel(level.introText, uiCamera);
        exitPanel = new ExitScreenPanel(this);
        enableInput();

        resetLevel();

        sceneFrameBuffer = new FrameBuffer(Format.RGBA8888, Config.width, Config.height, true);
        sceneFrameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);
        movementDelay = 0;

        forwardButton = new Rectangle(uiCamera.viewportWidth - 125, 150, 50, 50);
        backwardButton = new Rectangle(uiCamera.viewportWidth - 125, 50, 50, 50);
        turnRightButton = new Rectangle(uiCamera.viewportWidth - 100, 100, 50, 50);
        turnLeftButton = new Rectangle(uiCamera.viewportWidth - 150, 100, 50, 50);

        rotateCameraLeftButton = new Rectangle(50, uiCamera.viewportHeight - 100, 50, 50);
        rotateCameraRightButton = new Rectangle(uiCamera.viewportWidth -100, uiCamera.viewportHeight - 100, 50, 50);
        resetLevelButton = new Rectangle(uiCamera.viewportWidth /2 - 50, uiCamera.viewportHeight - 100, 100, 50);


    }

    private void resetLevel(){
        movesTaken = 0;
        introText.reset(uiCamera);
        Assets.environment.clear();
        Assets.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));

        // Regenerate model instances for the level geometry
        floorCellInstances = new Array<ModelInstance>();

        // Create game objects
        gameObjects = new Array<BaseGameObject>();

        final Vector2 pos = new Vector2();

        // Populate game objects based on map layout
        for (int y = 0; y < level.getHeight(); ++y) {
            for (int x = 0; x < level.getWidth(); ++x) {
                pos.set(x, y);

                final GameObjectProps objectProps = new GameObjectProps(level.getCellAt(x, y));
                final Entity.Type entityType = objectProps.getType();
                switch (entityType) {
                    case SPAWN:        gameObjects.add(new Spawn(pos.cpy(),       objectProps)); break;
                    case WALL:         gameObjects.add(new Wall(pos.cpy(),        objectProps)); break;
                    case EXIT:         gameObjects.add(new Exit((pos.cpy()),      objectProps)); break;
                    case BLOCKER_PULL: gameObjects.add(new BlockerPull(pos.cpy(), objectProps)); break;
                    case BLOCKER_PUSH: gameObjects.add(new BlockerPush(pos.cpy(), objectProps)); break;
                    case DOOR:         gameObjects.add(new Door(pos.cpy(),        objectProps)); break;
                    case DUMB_ROBOT:   gameObjects.add(new DumbRobot(pos.cpy(),   objectProps)); break;
                    case SPINNER:      gameObjects.add(new Spinner(pos.cpy(),     objectProps)); break;
                    case SWITCH:       gameObjects.add(new Switch(pos.cpy(),      objectProps)); break;
                    case TELEPORT:     gameObjects.add(new Teleport(pos.cpy(),    objectProps)); break;
                }

                if (entityType != Entity.Type.WALL) {
                    ModelInstance floorCell = new ModelInstance(Assets.floorModelInstance);
                    floorCell.transform.setToTranslation(x, y, 0f);
                    floorCellInstances.add(floorCell);
                }
                if (entityType == Entity.Type.SPAWN) {
                    final PointLight pointLight = new PointLight().set(new Color(1f, 0f, 1f, 1f), pos.x, pos.y, 1f, 2f);
                    Assets.environment.add(pointLight);
                    spawnPosition.set(pos.x, pos.y, 0);
                }
                else if (entityType == Entity.Type.EXIT) {
                    final PointLight pointLight = new PointLight().set(new Color(0f, 1f, 0f, 1f), pos.x, pos.y, 1f, 2f);
                    Assets.environment.add(pointLight);
                    exitPosition.set(pos.x, pos.y, 0);
                }
            }
        }

        // Link game objects
        for (int i = 0; i < gameObjects.size; ++i) {
            final BaseGameObject gameObject = gameObjects.get(i);
            gameObject.update(0); // Call once to set rotations
            for (int j = 0; j < gameObjects.size; ++j) {
                final BaseGameObject otherObject = gameObjects.get(j);
                if (gameObject == otherObject) continue;
                if (gameObject.properties.getLinkages() == otherObject.properties.getLinkages()) {
                    gameObject.linkObject(otherObject);
                }
            }
        }

        // Instantiate player
        int px = level.getSpawnCellIndex() % level.getWidth();
        int py = level.getSpawnCellIndex() / level.getWidth();
        player = new Player(new Vector2(px, py));
        gameObjects.add(player);

        cameraPosition.set(spawnPosition.x, spawnPosition.y -1, 5);
        cameraLookAt.set(spawnPosition);

        perCamController.pause = true;
        perspectiveCamera.position.set(cameraPosition);
        perspectiveCamera.lookAt(cameraLookAt);
        perspectiveCamera.update(true);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new LevelSelectScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            LevelEditorScreen levelEditorScreen = new LevelEditorScreen(game);
            levelEditorScreen.setLevel(level);
            game.setScreen(levelEditorScreen);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            renderAs3d = !renderAs3d;
            if (renderAs3d) {
                Gdx.input.setInputProcessor(perCamController);
            } else {
                Gdx.input.setInputProcessor(null);
            }
        }

        if (renderAs3d) {
            if (perCamController.pause) {
                perspectiveCamera.up.set(0,0,1);
                perspectiveCamera.position.set(cameraPosition);
                perspectiveCamera.lookAt(cameraLookAt);
                perspectiveCamera.update(true);
            }
            perCamController.update();
        }

        if (introText.update(delta, this)) return; // Don't do game things yet

        if (exitPanel.active){
            exitPanel.update(delta);
            for (BaseGameObject obj : gameObjects) {
                obj.update(delta);
            }
            return;
        }

        movementDelay -= delta;
        if (movementDelay <= 0){
            for(BaseGameObject obj : gameObjects){
                obj.setOldPos();
            }
            Vector2 touchPoint = new Vector2(-100, -100);
            if (Gdx.input.justTouched()) {
                Vector3 touchPoint3 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                uiCamera.unproject(touchPoint3);
                touchPoint = new Vector2(touchPoint3.x, touchPoint3.y);
            }
            if (rotateCameraLeftButton.contains(touchPoint)){
                perCamController.rotateLeft();
//                perCamController.addRotation(-90);
            }
            if (rotateCameraRightButton.contains(touchPoint)){
                perCamController.rotateRight();
//                perCamController.addRotation(90);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP) || forwardButton.contains(touchPoint)){
                for (BaseGameObject obj : gameObjects){
                    obj.move(true, this);
                }
                movesTaken++;
                resolveCollisions();
                processInteractions();
                movementDelay = Assets.MOVE_DELAY;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || backwardButton.contains(touchPoint)){
                for (BaseGameObject obj : gameObjects){
                    obj.move(false, this);
                }
                movesTaken++;
                resolveCollisions();
                processInteractions();
                movementDelay = Assets.MOVE_DELAY;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || turnLeftButton.contains(touchPoint)){
                for (BaseGameObject obj : gameObjects){
                    obj.rotate(false);
                }
                movesTaken++;
                movementDelay = Assets.MOVE_DELAY;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || turnRightButton.contains(touchPoint)){
                for (BaseGameObject obj : gameObjects){
                    obj.rotate(true);
                }
                movesTaken++;
                movementDelay = Assets.MOVE_DELAY;
            }
            if (resetLevelButton.contains(touchPoint)){
                resetLevel();
            }


        }
        for (BaseGameObject obj : gameObjects) {
            obj.update(delta);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        sceneFrameBuffer.begin();
        {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            // Draw the world
            if (renderAs3d) {
                Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
                background.render(perspectiveCamera);
                Assets.modelBatch.begin(perspectiveCamera);
                Assets.modelBatch.render(Assets.coordModelInstance);
                Assets.modelBatch.render(floorCellInstances, Assets.environment);
                for (BaseGameObject obj : gameObjects) {
                    obj.render(Assets.modelBatch, Assets.environment);
                }
                Assets.modelBatch.end();
            } else {
                batch.begin();
                batch.setProjectionMatrix(camera.combined);
                for (BaseGameObject obj : gameObjects) {
                    obj.render(batch);
                }
                batch.end();
            }

            // Draw user interface stuff
            batch.begin();
            batch.setProjectionMatrix(uiCamera.combined);
            if (exitPanel.active){
                exitPanel.render(batch);
            } else {
                introText.render(batch);
                if (!introText.fullScreen) {
                    Assets.font.draw(batch, "Moves: " + movesTaken, 10, uiCamera.viewportHeight - 10);

                    batch.draw(Assets.upArrow, forwardButton.x, forwardButton.y, forwardButton.width, forwardButton.height);
                    batch.draw(Assets.downArrow, backwardButton.x, backwardButton.y, backwardButton.width, backwardButton.height);
                    batch.draw(Assets.leftArrow, turnLeftButton.x, turnLeftButton.y, turnLeftButton.width, turnLeftButton.height);
                    batch.draw(Assets.rightArrow, turnRightButton.x, turnRightButton.y, turnRightButton.width, turnRightButton.height);

                    batch.draw(Assets.leftArrow, rotateCameraLeftButton.x, rotateCameraLeftButton.y, rotateCameraLeftButton.width, rotateCameraLeftButton.height);
                    batch.draw(Assets.rightArrow, rotateCameraRightButton.x, rotateCameraRightButton.y, rotateCameraRightButton.width, rotateCameraRightButton.height);

                    batch.draw(Assets.testTexture, resetLevelButton.x, resetLevelButton.y, resetLevelButton.width, resetLevelButton.height);
                }
            }

            batch.end();
        }
        sceneFrameBuffer.end();

        batch.setShader(null);
        batch.begin();
        {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.setProjectionMatrix(uiCamera.combined);
            batch.draw(sceneRegion, 0, 0);
            batch.end();
        }
    }

    // ------------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------------


    public BaseGameObject getObjectAt(Vector2 pos){
        final int objectSize  = gameObjects.size;
        for (int i = 0; i < objectSize; i++){
            BaseGameObject obj = gameObjects.get(i);
            if (obj.walkable) continue;
            if (obj.pos.x == pos.x && obj.pos.y == pos.y) return obj;
        }
        return null;
    }

    public void start(){
        cameraPosition.set(spawnPosition.x, spawnPosition.y -1, 5);
        cameraLookAt.set(spawnPosition);

        perCamController.pause = true;
        perspectiveCamera.position.set(cameraPosition);
        perspectiveCamera.lookAt(cameraLookAt);
        perspectiveCamera.update(true);
        Timeline.createSequence()
                .push(
                        Tween.to(cameraPosition, Vector3Accessor.XYZ, 4*Assets.MOVE_DELAY)
                                .target(spawnPosition.x, spawnPosition.y - 15f, 20f)
                                .ease(Sine.IN)
                )
                .push(
                        Tween.to(cameraPosition, Vector3Accessor.XYZ, 1.5f*Assets.MOVE_DELAY)
                                .target(spawnPosition.x - 15f, spawnPosition.y - 15f, 20f)
                                .ease(Sine.OUT)
                                .setCallback(new TweenCallback() {
                                    @Override
                                    public void onEvent(int i, BaseTween<?> baseTween) {
                                        perCamController.pause = false;
                                    }
                                })
                )
                .start(Assets.tween);
    }

    public void win(){
        // TODO add logic to transition when you get to the exit
        perCamController.pause = true;
        cameraPosition.set(perspectiveCamera.position);
        cameraLookAt.set(perCamController.lookatPosition);
        Timeline.createSequence()
                .push(
                        Tween.to(cameraLookAt, Vector3Accessor.XYZ, 1f)
                        .target(exitPosition.x, exitPosition.y, exitPosition.z)
                )
                .push(
                        Tween.to(cameraPosition, Vector3Accessor.XYZ, 1f)
                                .target(exitPosition.x, exitPosition.y - 15f, 20f)
                                .ease(Sine.IN)
                )
                .push(
                        Tween.to(cameraPosition, Vector3Accessor.XYZ, 1f)
                                .target(exitPosition.x, exitPosition.y - 1f, 5f)
                                .ease(Sine.OUT)
                                .setCallback(new TweenCallback() {
                                    @Override
                                    public void onEvent(int i, BaseTween<?> baseTween) {
                                        perCamController.pause = false;
                                    }
                                })
                )
                .start(Assets.tween);
        exitPanel.activate(3);
//        game.setScreen(new LevelSelectScreen(game));
    }
    // ------------------------------------------------------------------------
    // Private Implementation
    // ------------------------------------------------------------------------

    private void resolveCollisions(){
        boolean isValid = false;
        while (!isValid) {
            isValid = true;
            for (int i = 0; i < gameObjects.size; i++) {
                BaseGameObject objA = gameObjects.get(i);
                objA.usedThisTurn = false;
                if (objA.stationary) continue; // It didn't move no need to check
                for (int j = 0; j < gameObjects.size; j++) {
                    BaseGameObject objB = gameObjects.get(j);
                    if (objA == objB) continue; // Don't check self
                    if (objA.collides(objB) || objA.passedThrough(objB)) {
                        isValid = false;
                        objA.conflict = true;
                        objB.conflict = true;
                    }
                }
                if (objA.pos.x < 0 || objA.pos.x >= level.getWidth() || objA.pos.y < 0 || objA.pos.y >= level.getHeight()) objA.conflict = true;
            }
            if (!isValid){
                for (BaseGameObject obj : gameObjects)
                {
                    if (obj.conflict)
                    {
                        obj.revert();
                        obj.conflict = false;
                    }
                }
            }
        }
    }


    private void processInteractions(){
        final int objectSize  = gameObjects.size;
        for (int i = 0; i < objectSize; i++){
            BaseGameObject obj = gameObjects.get(i);
            if (!obj.interactable) continue;
            obj.interactWith(getObjectAt(obj.pos), this);

        }
    }

    @Override
    protected void enableInput() {
        final InputMultiplexer mux = new InputMultiplexer();
        // TODO: add any other input processors here as needed
        mux.addProcessor(perCamController);
        mux.addProcessor(new GestureDetector(perCamController));
        Gdx.input.setInputProcessor(mux);
    }

}
