package com.lando.systems.mosfet.utils.ui.editor;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Json;
import com.lando.systems.mosfet.screens.LevelEditorScreen;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.utils.ui.ButtonInputListenerAdapter;
import com.lando.systems.mosfet.world.Level;

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
                String levelName = nameField.getText();
                if (levelName == null || levelName.isEmpty()) {
                    levelEditorScreen.getInfoDialog().resetText(
                            "Unable to save level\nInvalid name",
                            levelEditorScreen.getStage());
                    dlg.hide();
                    return;
                }

                // Fetch and validate the level to save
                final Level level = levelEditorScreen.generateLevel();
                if (level == null) {
                    levelEditorScreen.getInfoDialog().resetText(
                            "Unable to save level\nNo level loaded",
                            levelEditorScreen.getStage());
                    dlg.hide();
                    return;
                }

                // Serialize level as JSON
                final Json json = new Json();
                final String levelData = json.prettyPrint(json.toJson(level, Level.class));

                // Can't write to filesystem in HTML build, so use shared prefs instead
                Assets.prefs.putString("levels/" + levelName, levelData);
                Assets.prefs.flush();

                // Write out to the file system too, if we are on desktop
                if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
                    Gdx.files.local("levels/" + levelName).writeString(levelData, false);
                }

                levelEditorScreen.getInfoDialog().resetText("Level saved as: levels/" + levelName, levelEditorScreen.getStage());
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
