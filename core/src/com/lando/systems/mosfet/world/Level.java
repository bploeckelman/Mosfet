package com.lando.systems.mosfet.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Array;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.gameobjects.GameObjectProps;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Brian Ploeckelman created on 8/10/2015.
 */
public class Level {

    public static final int CELL_WIDTH  = Config.tileSize;
    public static final int CELL_HEIGHT = Config.tileSize;

    int     width;
    int     height;
    int     numCells;
    int[]   cells;
    boolean hasSpawn;
    boolean hasExit;
    public int levelIndex;
    public String introText;

    // Json reader requires no-arg ctor
    public Level() {
        introText = "From empty constructor";
    }

    public Level(int width, int height) {
        this.width    = width;
        this.height   = height;
        this.numCells = width * height;
        this.cells    = new int[numCells];
        this.hasSpawn = false;
        this.hasExit  = false;
        this.introText = "Something Placeholder no Objects";

    }

    public Level(int width, int height, Array<GameObjectProps> objectPropsArray) {
        this.width    = width;
        this.height   = height;
        this.numCells = width * height;
        this.cells    = new int[numCells];
        this.hasSpawn = false;
        this.hasExit  = false;
        this.introText = "Something Placeholder with objects";
        int i = 0;
        for (GameObjectProps objectProps : objectPropsArray) {
            cells[i] = objectProps.getBits();
            ++i;
        }
    }

    public int getCellAt(int x, int y) {
        if (x >= 0 && x < width & y >= 0 && y < height) {
            return cells[y * width + x];
        }
        return -1;
    }

    public int getCellAt(int index) {
        if (index >= 0 && index < cells.length) {
            return cells[index];
        }
        return -1;
    }

    public void render(SpriteBatch batch) {
        TextureRegion texture;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                texture = Entity.Type.getRegionForValue(getCellAt(x, y));
                batch.draw(texture, x, y, 1, 1);
            }
        }
    }

    public int getSpawnCellIndex() {
        for (int i = 0; i < cells.length; ++i) {
            if (cells[i] == Entity.Type.SPAWN.getValue()) {
                return i;
            }
        }
        return -1;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
