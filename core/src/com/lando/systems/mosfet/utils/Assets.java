package com.lando.systems.mosfet.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.utils.accessors.*;

/**
 * Brian Ploeckelman created on 9/10/2015.
 */
public class Assets {

    public static final float MOVE_DELAY = .25f;

    public static TweenManager tween;
    public static SpriteBatch  batch;
    public static ModelBatch   modelBatch;
    public static BitmapFont   font;

    private static final String PREFS_FILE = "mostfet-prefs";
    public static Preferences prefs;

    public static Texture testTexture;
    public static Texture circleTexture;
    public static Texture stoneTexture;
    public static Texture spritesheetTexturePlaceholder;

    public static TextureRegion[][] spritePlaceholderRegions;
    public static TextureRegion     blankRegion;
    public static TextureRegion     spawnRegion;
    public static TextureRegion     wallRegion;
    public static TextureRegion     exitRegion;
    public static TextureRegion     doorClosedRegion;
    public static TextureRegion     doorOpenRegion;
    public static TextureRegion     blockerPushPullRegion;
    public static TextureRegion     blockerPushRegion;
    public static TextureRegion     blockerPullRegion;
    public static TextureRegion     playerRegion;
    public static TextureRegion     aiRegion;
    public static TextureRegion     spinnerRegion;
    public static TextureRegion     switchRegion;
    public static TextureRegion     teleportRegion;


    public static void load() {
        if (tween == null) {
            tween = new TweenManager();
            Tween.registerAccessor(Color.class, new ColorAccessor());
            Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
            Tween.registerAccessor(Vector2.class, new Vector2Accessor());
            Tween.registerAccessor(Vector3.class, new Vector3Accessor());
            Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
        }

        batch = new SpriteBatch();
        modelBatch = new ModelBatch();
        font = new BitmapFont();

        prefs = Gdx.app.getPreferences(PREFS_FILE);

        testTexture = new Texture("badlogic.jpg");
        circleTexture = new Texture("circle.png");
        stoneTexture = new Texture("stone.png");
        spritesheetTexturePlaceholder = new Texture("spritesheet-placeholders.png");

        spritePlaceholderRegions = TextureRegion.split(spritesheetTexturePlaceholder, Config.tileSize, Config.tileSize);
        blankRegion           = spritePlaceholderRegions[7][7];
        spawnRegion           = spritePlaceholderRegions[0][0];
        wallRegion            = spritePlaceholderRegions[0][1];
        exitRegion            = spritePlaceholderRegions[0][2];
        doorClosedRegion      = spritePlaceholderRegions[0][3];
        doorOpenRegion        = spritePlaceholderRegions[0][4];
        blockerPushPullRegion = spritePlaceholderRegions[0][5];
        blockerPushRegion     = spritePlaceholderRegions[0][6];
        blockerPullRegion     = spritePlaceholderRegions[0][7];
        playerRegion          = spritePlaceholderRegions[1][1];
        aiRegion              = spritePlaceholderRegions[1][0];
        spinnerRegion         = spritePlaceholderRegions[1][2];
        switchRegion          = spritePlaceholderRegions[1][3];
        teleportRegion        = spritePlaceholderRegions[1][4];
    }

    public static void dispose() {
        batch.dispose();
        modelBatch.dispose();
        font.dispose();
        testTexture.dispose();
        circleTexture.dispose();
        stoneTexture.dispose();
        spritesheetTexturePlaceholder.dispose();
    }

    private static ShaderProgram compileShaderProgram(FileHandle vertSource, FileHandle fragSource) {
        ShaderProgram.pedantic = false;
        final ShaderProgram shader = new ShaderProgram(vertSource, fragSource);
        if (!shader.isCompiled()) {
            throw new GdxRuntimeException("Failed to compile shader program:\n" + shader.getLog());
        }
        else if (shader.getLog().length() > 0) {
            Gdx.app.debug("SHADER", "ShaderProgram compilation log:\n" + shader.getLog());
        }
        return shader;
    }

}
