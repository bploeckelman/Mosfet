package com.lando.systems.mosfet.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lando.systems.mosfet.Config;

import java.util.Arrays;

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
        this.width = width;
        this.height = height;
        numCells = width * height;
        cells = new int[numCells];
        Arrays.fill(cells, 0);
        hasSpawn = false;
        hasExit = false;
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

    public void setCellAt(int x, int y, int value) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }

        int index = y * width + x;

        if      (value == Entity.Type.SPAWN.getValue()) hasSpawn = true;
        else if (value == Entity.Type.EXIT.getValue())  hasExit = true;
        else {
            int currentValue = cells[index];
            if      (currentValue == Entity.Type.SPAWN.getValue()) hasSpawn = false;
            else if (currentValue == Entity.Type.EXIT.getValue())  hasExit = false;
        }

        cells[index] = value;
    }

    public void setCellAt(int index, int value) {
        if (index < 0 || index >= cells.length) {
            return;
        }

        if      (value == Entity.Type.SPAWN.getValue()) hasSpawn = true;
        else if (value == Entity.Type.EXIT.getValue())  hasExit = true;
        else {
            int currentValue = cells[index];
            if      (currentValue == Entity.Type.SPAWN.getValue()) hasSpawn = false;
            else if (currentValue == Entity.Type.EXIT.getValue())  hasExit = false;
        }

        cells[index] = value;
    }

    public void render(SpriteBatch batch) {
        TextureRegion texture = Assets.blankRegion;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int value = getCellAt(x, y);
                switch (value) {
                    case 0: texture = Entity.Type.BLANK.getRegion(); break;
                    case 1: texture = Entity.Type.SPAWN.getRegion(); break;
                    case 2: texture = Entity.Type.WALL.getRegion(); break;
                    case 3: texture = Entity.Type.EXIT.getRegion(); break;
                }
                batch.draw(texture, x * CELL_WIDTH, y * CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT);
            }
        }
    }

    public boolean hasSpawn() {
        return hasSpawn;
    }

    public boolean hasExit() {
        return hasExit;
    }

    public int getSpawnCellIndex() {
        if (!hasSpawn) return -1;
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

    public float getCellWidth() {
        return CELL_WIDTH;
    }

    public float getCellHeight() {
        return CELL_HEIGHT;
    }

}
