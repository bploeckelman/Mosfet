package com.lando.systems.mosfet.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.utils.accessors.*;
import com.lando.systems.mosfet.world.Level;

/**
 * Brian Ploeckelman created on 9/10/2015.
 */
public class Assets {
    public static DefaultShader.Config perfragLightConfig;
    public static final float MOVE_DELAY = .5f;

    public static TweenManager tween;
    public static SpriteBatch  batch;
    public static ModelBatch   modelBatch;
    public static BitmapFont   font;
    public static BitmapFont   resistorFont32;
    public static BitmapFont   resistorFont64;

    private static final String PREFS_FILE = "mostfet-prefs";
    public static Preferences prefs;

    public static Texture testTexture;
    public static Texture circleTexture;
    public static Texture floorTexture;
    public static Texture gearTexture;
    public static Texture switchActiveTexture;
    public static Texture uiSpritesheetTexture;
    public static Texture spritesheetTexturePlaceholder;
    public static Texture upArrow;
    public static Texture downArrow;
    public static Texture rightArrow;
    public static Texture leftArrow;

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

    public static TextureRegion[][] uiSpritesheetRegions;
    public static TextureRegion uiPlayButtonRegion;
    public static TextureRegion uiPlayButtonDownRegion;
    public static TextureRegion uiEraseButtonRegion;
    public static TextureRegion uiEraseButtonDownRegion;
    public static TextureRegion uiEraseButtonCheckedRegion;
    public static TextureRegion uiLinkButtonRegion;
    public static TextureRegion uiLinkButtonDownRegion;
    public static TextureRegion uiLinkButtonCheckedRegion;
    public static TextureRegion uiNewButtonRegion;
    public static TextureRegion uiNewButtonDownRegion;
    public static TextureRegion uiSaveButtonRegion;
    public static TextureRegion uiSaveButtonDownRegion;
    public static TextureRegion uiLoadButtonRegion;
    public static TextureRegion uiLoadButtonDownRegion;
    public static TextureRegion uiCameraButtonRegion;
    public static TextureRegion uiCameraButtonDownRegion;
    public static TextureRegion uiCameraButtonCheckedRegion;

    public static Model         cubeModel;
    public static Model         robotModel;
    public static Model         floorModel;
    public static Model         backgroundModel;
    public static Model         coordModel;
    public static Model         ladderModel;
    public static Model         crateModel;
    public static Model         switchModel;
    public static ModelInstance floorModelInstance;
    public static ModelInstance backgroundModelInstance;
    public static ModelInstance coordModelInstance;
    public static Environment   environment;

    static        AssetManager assetManager;
    public static Array<Level> levels;

    public static void load() {

        perfragLightConfig = new DefaultShader.Config();
        perfragLightConfig.vertexShader = Gdx.files.internal("shaders/backgroundvertex.glsl").readString();
        perfragLightConfig.fragmentShader = Gdx.files.internal("shaders/backgroundfrag.glsl").readString();

        if (tween == null) {
            tween = new TweenManager();
            Tween.setCombinedAttributesLimit(4);
            Tween.registerAccessor(Color.class, new ColorAccessor());
            Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
            Tween.registerAccessor(Vector2.class, new Vector2Accessor());
            Tween.registerAccessor(Vector3.class, new Vector3Accessor());
            Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
        }

        batch = new SpriteBatch();
        modelBatch = new ModelBatch();
        font = new BitmapFont();
        resistorFont32 = new BitmapFont(Gdx.files.internal("fonts/resistors-32pt"));
        resistorFont64 = new BitmapFont(Gdx.files.internal("fonts/resistors-64pt"));

        prefs = Gdx.app.getPreferences(PREFS_FILE);

        testTexture = new Texture("badlogic.jpg");
        circleTexture = new Texture("circle.png");
        floorTexture = new Texture("floor-diffuse.png");
        floorTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        gearTexture = new Texture("gear.png");

        upArrow = new Texture("up-arrow.png");
        downArrow = new Texture("down-arrow.png");
        rightArrow = new Texture("right-arrow.png");
        leftArrow = new Texture("left-arrow.png");
        uiSpritesheetTexture = new Texture("spritesheet.png");
        spritesheetTexturePlaceholder = new Texture("spritesheet-placeholders.png");
        switchActiveTexture = new Texture("switch1-active-diffuse.png");

        spritePlaceholderRegions = TextureRegion.split(spritesheetTexturePlaceholder, Config.tileSize, Config.tileSize);
        blankRegion = spritePlaceholderRegions[7][7];
        spawnRegion = spritePlaceholderRegions[0][0];
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

        uiSpritesheetRegions = TextureRegion.split(uiSpritesheetTexture, 32, 32);
        uiPlayButtonRegion         = uiSpritesheetRegions[0][0];
        uiPlayButtonDownRegion     = uiSpritesheetRegions[1][0];
        uiEraseButtonRegion        = uiSpritesheetRegions[0][1];
        uiEraseButtonDownRegion    = uiSpritesheetRegions[1][1];
        uiEraseButtonCheckedRegion = uiSpritesheetRegions[2][1];
        uiLinkButtonRegion         = uiSpritesheetRegions[0][2];
        uiLinkButtonDownRegion     = uiSpritesheetRegions[1][2];
        uiLinkButtonCheckedRegion  = uiSpritesheetRegions[2][2];
        uiNewButtonRegion          = uiSpritesheetRegions[0][3];
        uiNewButtonDownRegion      = uiSpritesheetRegions[1][3];
        uiSaveButtonRegion         = uiSpritesheetRegions[0][4];
        uiSaveButtonDownRegion     = uiSpritesheetRegions[1][4];
        uiLoadButtonRegion         = uiSpritesheetRegions[0][5];
        uiLoadButtonDownRegion     = uiSpritesheetRegions[1][5];
        uiCameraButtonRegion       = uiSpritesheetRegions[0][6];
        uiCameraButtonDownRegion   = uiSpritesheetRegions[1][6];
        uiCameraButtonCheckedRegion= uiSpritesheetRegions[2][6];

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1f));

        final ModelBuilder modelBuilder = new ModelBuilder();
        final Material floorMaterial = new Material(TextureAttribute.createDiffuse(floorTexture));
        final long floorAttribs = VertexAttributes.Usage.Position
                                | VertexAttributes.Usage.Normal
                                | VertexAttributes.Usage.TextureCoordinates;
        floorModel = modelBuilder.createRect(
                -0.5f, -0.5f, 0f,
                 0.5f, -0.5f, 0f,
                 0.5f,  0.5f, 0f,
                -0.5f,  0.5f, 0f,
                   0f,    0f, 1f,
                floorMaterial,
                floorAttribs
        );
        floorModelInstance = new ModelInstance(floorModel);

        final Material backgroundMaterial = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        final long backgroundAttribs = VertexAttributes.Usage.Position
                | VertexAttributes.Usage.Normal
                | VertexAttributes.Usage.TextureCoordinates;
        backgroundModel = modelBuilder.createRect(
                -100.5f, -100.5f, -1f,
                100.5f, -100.5f, -1f,
                100.5f,  100.5f, -1f,
                -100.5f,  100.5f, -1f,
                0f,    0f, 1f,
                backgroundMaterial,
                backgroundAttribs
        );
        backgroundModelInstance = new ModelInstance(backgroundModel);

        final Material coordMaterial = new Material(ColorAttribute.createDiffuse(1f, 1f, 1f, 1f));
        final long coordAttribs = VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked;
        coordModel = modelBuilder.createXYZCoordinates(1f, coordMaterial, coordAttribs);
        coordModelInstance = new ModelInstance(coordModel);

        ModelLoader.ModelParameters modelParam = new ModelLoader.ModelParameters();
        modelParam.textureParameter.magFilter = Texture.TextureFilter.MipMapLinearNearest;
        modelParam.textureParameter.minFilter = Texture.TextureFilter.MipMapLinearNearest;
        modelParam.textureParameter.genMipMaps = true;
        assetManager = new AssetManager();
        assetManager.load("models/wall.g3dj", Model.class, modelParam);
        assetManager.load("models/robot-head.g3dj", Model.class, modelParam);
        assetManager.load("models/ladder.g3dj", Model.class, modelParam);
        assetManager.load("models/crate.g3dj", Model.class, modelParam);
        assetManager.load("models/switch1.g3dj", Model.class, modelParam);
        assetManager.finishLoading();
        cubeModel = assetManager.get("models/wall.g3dj", Model.class);
        robotModel = assetManager.get("models/robot-head.g3dj", Model.class);
        ladderModel = assetManager.get("models/ladder.g3dj", Model.class);
        crateModel = assetManager.get("models/crate.g3dj", Model.class);
        switchModel = assetManager.get("models/switch1.g3dj", Model.class);

        // Lets load up some levels, here is a comment cause Brain says I don't comment anything
        levels = new Array<Level>();
        FileHandle levelList = Gdx.files.internal("levels/level_list.txt");
        String[] levelNames = levelList.readString().split("\n");
        for (int i = 0; i < levelNames.length; i++){
            FileHandle levelFile = Gdx.files.internal("levels/"+ levelNames[i].trim());
            Level l = (new Json()).fromJson(Level.class, levelFile);
            l.levelIndex = i;
            levels.add(l);
        }
    }

    public static void dispose() {
        batch.dispose();
        modelBatch.dispose();
        font.dispose();
        resistorFont32.dispose();
        resistorFont64.dispose();
        testTexture.dispose();
        circleTexture.dispose();
        floorTexture.dispose();
        gearTexture.dispose();
        switchActiveTexture.dispose();
        uiSpritesheetTexture.dispose();
        spritesheetTexturePlaceholder.dispose();
        assetManager.dispose();
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
