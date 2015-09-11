package com.lando.systems.mosfet.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.MosfetGame;
import com.lando.systems.mosfet.gameobjects.*;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.world.Level;

/**
 * Brian Ploeckelman created on 9/10/2015.
 */
public class GamePlayScreen extends GameScreen {

    final float             MOVE_DELAY = .1f; // TODO make this match the animation tween

    FrameBuffer             sceneFrameBuffer;
    TextureRegion           sceneRegion;
    Level                   level;
    Array<BaseGameObject>   gameObjects;
    Player                  player;
    float                   movementDelay;

    public GamePlayScreen(MosfetGame game, Level level) {
        super(game);

        if (level == null) {
            throw new GdxRuntimeException("GamePlayScreen requires a valid level");
        }
        this.level = level;
        int levelWidth = level.getWidth();
        int levelHeight = level.getHeight();

        Gdx.gl.glClearColor(0f, 191f / 255f, 1f, 1f);

        // Show a tile portion of the map
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

        gameObjects = new Array<BaseGameObject>();
        for (int y = 0; y < levelHeight; y++){
            for (int x = 0; x < levelWidth; x++){
                gameObjects.add(new Floor(new Vector2(x, y)));
            }
        }

        // TODO; load this in
        player = new Player(new Vector2(3, 3));
        gameObjects.add(player);
        for (int y = 0; y < levelHeight; y++){
            gameObjects.add(new Wall(new Vector2(0, y)));
            gameObjects.add(new Wall(new Vector2(levelWidth -1, y)));
        }
        for (int x = 0; x < levelWidth; x++){
            gameObjects.add(new Wall(new Vector2(x, 0)));
            gameObjects.add(new Wall(new Vector2(x, levelHeight -1)));
        }

        sceneFrameBuffer = new FrameBuffer(Format.RGBA8888, Config.width, Config.height, false);
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);
        movementDelay = 0;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            LevelEditorScreen levelEditorScreen = new LevelEditorScreen(game);
            levelEditorScreen.setLevel(level);
            game.setScreen(levelEditorScreen);
        }

        movementDelay -= delta;
        if (movementDelay <= 0){
            // TODO also handle click on button in UI
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)){
                for (BaseGameObject obj : gameObjects){
                    obj.move(true);
                }
                resolveCollisions();
                processInteractions();
                movementDelay = MOVE_DELAY;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
                for (BaseGameObject obj : gameObjects){
                    obj.move(false);
                }
                resolveCollisions();
                processInteractions();
                movementDelay = MOVE_DELAY;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
                for (BaseGameObject obj : gameObjects){
                    obj.rotate(false);
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
                for (BaseGameObject obj : gameObjects){
                    obj.rotate(true);
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        sceneFrameBuffer.begin();
        {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Draw the world

            batch.begin();
            batch.setProjectionMatrix(camera.combined);
            level.render(batch);
            for (BaseGameObject obj : gameObjects){
                obj.render(batch);
            }
            batch.end();

            // Draw user interface stuff
            batch.begin();
            batch.setProjectionMatrix(uiCamera.combined);
            Assets.font.draw(batch, "This... is... MOSFET!", 10, uiCamera.viewportHeight - 10);
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

    // ------------------------------------------------------------------------
    // Private Implementation
    // ------------------------------------------------------------------------

    private void resolveCollisions(){
        boolean isValid = false;
        while (!isValid) {
            isValid = true;
            for (int i = 0; i < gameObjects.size; i++) {
                BaseGameObject objA = gameObjects.get(i);
                if (objA.stationary) continue; // It didn't move no need to check
                for (int j = 0; j < gameObjects.size; j++) {
                    BaseGameObject objB = gameObjects.get(j);
                    if (objA == objB) continue; // Don't check self
                    if (objA.collides(objB)) {
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
                continue;
            }
        }
    }

    private void processInteractions(){

    }

    @Override
    protected void enableInput() {
        final InputMultiplexer mux = new InputMultiplexer();
        // TODO: add any other input processors here as needed
        Gdx.input.setInputProcessor(mux);
    }

}
