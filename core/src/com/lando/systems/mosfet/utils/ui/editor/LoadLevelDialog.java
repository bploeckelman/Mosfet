package com.lando.systems.mosfet.utils.ui.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.lando.systems.mosfet.screens.LevelEditorScreen;
import com.lando.systems.mosfet.utils.ui.ButtonInputListenerAdapter;
import com.lando.systems.mosfet.world.Level;

/**
 * Brian Ploeckelman created on 8/11/2015.
 */
public class LoadLevelDialog extends Dialog {

    private LevelEditorScreen levelEditorScreen;

    public LoadLevelDialog(String title, Skin skin, LevelEditorScreen levelEditorScreen) {
        super(title, skin);
        this.levelEditorScreen = levelEditorScreen;
        initLayout();
        setZIndex(9999);
    }

    public LoadLevelDialog(String title, Skin skin, String windowStyleName, LevelEditorScreen levelEditorScreen) {
        super(title, skin, windowStyleName);
        this.levelEditorScreen = levelEditorScreen;
        initLayout();
        setZIndex(9999);
    }

    public LoadLevelDialog(String title, WindowStyle windowStyle, LevelEditorScreen levelEditorScreen) {
        super(title, windowStyle);
        this.levelEditorScreen = levelEditorScreen;
        initLayout();
        setZIndex(9999);
    }

    protected void initLayout() {
        final LoadLevelDialog dlg = this;

        FileHandle levelsDir = Gdx.files.local("levels");
        Array<FileHandle> levelFiles = new Array<FileHandle>();
        levelFiles.add(levelsDir);
        levelFiles.addAll(levelsDir.list());

        final SelectBox<FileHandle> fileSelect = new SelectBox<FileHandle>(this.getSkin());
        fileSelect.setItems(levelFiles);
        fileSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final FileHandle selectedFile = fileSelect.getSelected();
                final String input = selectedFile.readString();
                final Json json = new Json();
                final Level level = json.fromJson(Level.class, input);
                if (level == null) {
                    levelEditorScreen.getInfoDialog().resetText(
                            "Unable to load level from file: " + selectedFile.path(),
                            levelEditorScreen.getStage());
                    dlg.hide();
                    return;
                }

                levelEditorScreen.setLevel(level);
                levelEditorScreen.getInfoDialog().resetText(
                        "Loaded level from file: " + selectedFile.path(),
                        levelEditorScreen.getStage());
                dlg.hide();
            }
        });

        final TextButton cancelButton = new TextButton("Cancel", this.getSkin());
        cancelButton.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dlg.hide();
            }
        });

        final float SELECTBOX_WIDTH = 150f;

        dlg.getContentTable().add("Load");
        dlg.getContentTable().add(fileSelect).width(SELECTBOX_WIDTH);
        dlg.getContentTable().row();
        dlg.button(cancelButton);
        dlg.show(levelEditorScreen.getStage());
    }

}
