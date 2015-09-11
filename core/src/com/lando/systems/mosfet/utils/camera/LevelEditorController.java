package com.lando.systems.mosfet.utils.camera;

import com.badlogic.gdx.InputAdapter;
import com.lando.systems.mosfet.screens.LevelEditorScreen;
import com.lando.systems.mosfet.world.Entity;
import com.lando.systems.mosfet.world.Level;

/**
 * Brian Ploeckelman created on 8/11/2015.
 */
public class LevelEditorController extends InputAdapter {

    private final LevelEditorScreen levelEditorScreen;
    private       boolean           leftButtonDown;
    private       int               lastCellClickedX;
    private       int               lastCellClickedY;

    public LevelEditorController(LevelEditorScreen levelEditorScreen) {
        this.levelEditorScreen = levelEditorScreen;
        leftButtonDown = false;
        lastCellClickedX = -1;
        lastCellClickedY = -1;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (levelEditorScreen.getLevel() != null && button == 0) {
            leftButtonDown = true;
            int cellX = (int) (levelEditorScreen.getMouseWorldPos().x / Level.CELL_WIDTH);
            int cellY = (int) (levelEditorScreen.getMouseWorldPos().y / Level.CELL_HEIGHT);
            updateLevelWithClickAt(cellX, cellY);
            return true;
        }

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (leftButtonDown && button == 0) {
            leftButtonDown = false;
            lastCellClickedX = -1;
            lastCellClickedY = -1;
            return true;
        }
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (leftButtonDown) {
            int cellX = (int) (levelEditorScreen.getMouseWorldPos().x / Level.CELL_WIDTH);
            int cellY = (int) (levelEditorScreen.getMouseWorldPos().y / Level.CELL_HEIGHT);
            if (cellX != lastCellClickedX || cellY != lastCellClickedY) {
                updateLevelWithClickAt(cellX, cellY);
            }
            return true;
        }
        return super.touchDragged(screenX, screenY, pointer);
    }

    // ------------------------------------------------------------------------
    // Private Implementation
    // ------------------------------------------------------------------------

    private void updateLevelWithClickAt(int cellX, int cellY) {
        int cellValue = levelEditorScreen.getSelectedEntityType().getValue();
        if (levelEditorScreen.isRemovalMode()) {
            levelEditorScreen.getLevel().setCellAt(cellX, cellY, Entity.Type.BLANK.getValue());
        } else {
            final Level level = levelEditorScreen.getLevel();
            if (cellValue == Entity.Type.SPAWN.getValue() && level.hasSpawn()) {
                level.setCellAt(level.getSpawnCellIndex(), Entity.Type.BLANK.getValue());
            }
            levelEditorScreen.getLevel().setCellAt(cellX, cellY, cellValue);
        }
        lastCellClickedX = cellX;
        lastCellClickedY = cellY;
    }

}
