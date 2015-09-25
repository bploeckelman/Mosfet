package com.lando.systems.mosfet.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.gameobjects.GameObjectProps;

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

    // Json reader requires no-arg ctor
    public Level() {
    }

    public Level(int width, int height) {
        this.width    = width;
        this.height   = height;
        this.numCells = width * height;
        this.cells    = new int[numCells];
        this.hasSpawn = false;
        this.hasExit  = false;
    }

    public Level(int width, int height, Array<GameObjectProps> objectPropsArray) {
        this.width    = width;
        this.height   = height;
        this.numCells = width * height;
        this.cells    = new int[numCells];
        this.hasSpawn = false;
        this.hasExit  = false;

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
