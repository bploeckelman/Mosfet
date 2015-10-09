package com.lando.systems.mosfet.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.MosfetGame;
import com.lando.systems.mosfet.gameobjects.*;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.world.Entity;
import com.lando.systems.mosfet.world.Level;

/**
 * Brian Ploeckelman created on 9/10/2015.
 */
public class GamePlayScreen extends GameScreen {


    FrameBuffer             sceneFrameBuffer;
    TextureRegion           sceneRegion;
    Level                   level;
    Array<BaseGameObject>   gameObjects;
    Array<ModelInstance>    floorCellInstances;
    Player                  player;
    float                   movementDelay;
    Rectangle               forwardButton;
    Rectangle               backwardButton;
    Rectangle               turnRightButton;
    Rectangle               turnLeftButton;
    PerspectiveCamera       perspectiveCamera;
    boolean                 renderAs3d;

    FirstPersonCameraController fpsCamController;


    public GamePlayScreen(MosfetGame game, Level level) {
        super(game);

        if (level == null) {
            throw new GdxRuntimeException("GamePlayScreen requires a valid level");
        }
        this.level = level;
        int levelWidth = level.getWidth();
        int levelHeight = level.getHeight();

        Gdx.gl.glClearColor(0f, 191f / 255f, 1f, 1f);

        perspectiveCamera = new PerspectiveCamera(67f, Config.width, Config.height);
        perspectiveCamera.position.set(levelWidth / 2f, levelHeight / 2f, 20f);
        perspectiveCamera.lookAt(levelWidth / 2f, levelHeight / 2f, 0f);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 300f;
        perspectiveCamera.update();
        renderAs3d = false;
        fpsCamController = new FirstPersonCameraController(perspectiveCamera);

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

        resetLevel();

        sceneFrameBuffer = new FrameBuffer(Format.RGBA8888, Config.width, Config.height, true);
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);
        movementDelay = 0;

        forwardButton = new Rectangle(uiCamera.viewportWidth - 125, 150, 50, 50);
        backwardButton = new Rectangle(uiCamera.viewportWidth - 125, 50, 50, 50);
        turnRightButton = new Rectangle(uiCamera.viewportWidth - 100, 100, 50, 50);
        turnLeftButton = new Rectangle(uiCamera.viewportWidth - 150, 100, 50, 50);
    }

    private void resetLevel(){
        // Regenerate model instances for the level geometry
        floorCellInstances = new Array<ModelInstance>();

        // Create game objects
        gameObjects = new Array<BaseGameObject>();

        final Vector2 pos = new Vector2();

        // Populate game objects based on map layout
        for (int y = 0; y < level.getHeight(); ++y) {
            for (int x = 0; x < level.getWidth(); ++x) {
                pos.set(x, y);

                final Entity.Type entityType = Entity.Type.getTypeForValue(level.getCellAt(x, y));
                switch (entityType) {
                    case SPAWN:        gameObjects.add(new Spawn(pos.cpy())); break;
                    case WALL:         gameObjects.add(new Wall(pos.cpy())); break;
                    case EXIT:         gameObjects.add(new Exit((pos.cpy()))); break;
                    case BLOCKER_PULL: gameObjects.add(new BlockerPull(pos.cpy())); break;
                    case BLOCKER_PUSH: gameObjects.add(new BlockerPush(pos.cpy())); break;
                    case DOOR:         gameObjects.add(new Door(pos.cpy())); break;
                    case DUMB_ROBOT:   gameObjects.add(new DumbRobot(pos.cpy())); break;
                    case SPINNER:      gameObjects.add(new Spinner(pos.cpy())); break;
                    case SWITCH:       gameObjects.add(new Switch(pos.cpy())); break;
                    case TELEPORT:     gameObjects.add(new Teleport(pos.cpy())); break;
                }

                if (entityType != Entity.Type.WALL) {
                    ModelInstance floorCell = new ModelInstance(Assets.floorModelInstance);
                    floorCell.transform.setToTranslation(x, y, 0f);
                    floorCellInstances.add(floorCell);
                }
            }
        }
        gameObjects.add(new DumbRobot(new Vector2(2,2)));
        gameObjects.add(new Blocker(new Vector2(2,3)));


        // Instantiate player
        int px = level.getSpawnCellIndex() % level.getWidth();
        int py = level.getSpawnCellIndex() / level.getWidth();
        player = new Player(new Vector2(px, py));
        gameObjects.add(player);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            LevelEditorScreen levelEditorScreen = new LevelEditorScreen(game);
            levelEditorScreen.setLevel(level);
            game.setScreen(levelEditorScreen);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            renderAs3d = !renderAs3d;
            if (renderAs3d) {
                Gdx.input.setInputProcessor(fpsCamController);
            } else {
                Gdx.input.setInputProcessor(null);
            }
        }
        if (renderAs3d) fpsCamController.update();

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
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP) || forwardButton.contains(touchPoint)){
                for (BaseGameObject obj : gameObjects){
                    obj.move(true, this);
                }
                resolveCollisions();
                processInteractions();
                movementDelay = Assets.MOVE_DELAY;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || backwardButton.contains(touchPoint)){
                for (BaseGameObject obj : gameObjects){
                    obj.move(false, this);
                }
                resolveCollisions();
                processInteractions();
                movementDelay = Assets.MOVE_DELAY;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || turnLeftButton.contains(touchPoint)){
                for (BaseGameObject obj : gameObjects){
                    obj.rotate(false);
                }
                movementDelay = Assets.MOVE_DELAY;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || turnRightButton.contains(touchPoint)){
                for (BaseGameObject obj : gameObjects){
                    obj.rotate(true);
                }
                movementDelay = Assets.MOVE_DELAY;
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
            Assets.font.draw(batch, "This... is... MOSFET!", 10, uiCamera.viewportHeight - 10);

            batch.draw(Assets.upArrow, forwardButton.x, forwardButton.y, forwardButton.width, forwardButton.height);
            batch.draw(Assets.downArrow, backwardButton.x, backwardButton.y, backwardButton.width, backwardButton.height);
            batch.draw(Assets.leftArrow, turnLeftButton.x, turnLeftButton.y, turnLeftButton.width, turnLeftButton.height);
            batch.draw(Assets.rightArrow, turnRightButton.x, turnRightButton.y, turnRightButton.width, turnRightButton.height);

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

    public void win(){
        // TODO add logic to transition when you get to the exit
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
        Gdx.input.setInputProcessor(mux);
    }

}
