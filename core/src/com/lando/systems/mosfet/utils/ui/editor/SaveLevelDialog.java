package com.lando.systems.mosfet.utils.ui.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Json;
import com.lando.systems.mosfet.screens.LevelEditorScreen;
import com.lando.systems.mosfet.utils.ui.ButtonInputListenerAdapter;
import com.lando.systems.mosfet.world.Level;

import java.io.IOException;

/**
 * Brian Ploeckelman created on 8/11/2015.
 */
public class SaveLevelDialog extends Dialog {

    private LevelEditorScreen levelEditorScreen;

    public SaveLevelDialog(String title, Skin skin, LevelEditorScreen levelEditorScreen) {
        super(title, skin);
        this.levelEditorScreen = levelEditorScreen;
        initLayout();
        setZIndex(9999);
    }

    public SaveLevelDialog(String title, Skin skin, String windowStyleName, LevelEditorScreen levelEditorScreen) {
        super(title, skin, windowStyleName);
        this.levelEditorScreen = levelEditorScreen;
        initLayout();
        setZIndex(9999);
    }

    public SaveLevelDialog(String title, WindowStyle windowStyle, LevelEditorScreen levelEditorScreen) {
        super(title, windowStyle);
        this.levelEditorScreen = levelEditorScreen;
        initLayout();
        setZIndex(9999);
    }

    protected void initLayout() {
        final SaveLevelDialog dlg = this;

        final TextField nameField = new TextField("", this.getSkin());
        final TextButton okButton = new TextButton("Ok", this.getSkin());
        final TextButton cancelButton = new TextButton("Cancel", this.getSkin());

        okButton.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // Fetch and validate the name to save the level as
                String nameStr = nameField.getText();
                if (nameStr == null || nameStr.isEmpty()) {
                    levelEditorScreen.getInfoDialog().resetText(
                            "Unable to save level\nInvalid name",
                            levelEditorScreen.getStage());
                    dlg.hide();
                    return;
                }

                // Fetch and validate the level to save
                Level level = levelEditorScreen.getLevel();
                if (level == null) {
                    levelEditorScreen.getInfoDialog().resetText(
                            "Unable to save level\nNo level loaded",
                            levelEditorScreen.getStage());
                    dlg.hide();
                    return;
                }

                // Serialize level as JSON
                Json json = new Json();
                String output = json.toJson(level, Level.class);
                //Gdx.app.log("json", json.prettyPrint(output));

                // Write serialized JSON as nameStr text file
                FileHandle file = Gdx.files.local("levels/" + nameStr);
                try {
                    boolean exists = file.exists();
                    if (!exists) {
                        exists = file.file().createNewFile();
                    }
                    if (!exists) {
                        throw new IOException("Unable to create file: levels/" + nameStr);
                    }
                    file.writeString(output, false);
                } catch (IOException e) {
                    levelEditorScreen.getInfoDialog().resetText(e.getMessage(), levelEditorScreen.getStage());
                }

                levelEditorScreen.getInfoDialog().resetText("Level saved as: levels/" + nameStr, levelEditorScreen.getStage());
            }
        });
        cancelButton.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dlg.hide();
            }
        });

        final float TEXTFIELD_WIDTH = 150f;

        dlg.getContentTable().add("Save As");
        dlg.getContentTable().add(nameField).width(TEXTFIELD_WIDTH);
        dlg.getContentTable().row();
        dlg.button(okButton);
        dlg.button(cancelButton);
        dlg.show(levelEditorScreen.getStage());
    }

}
