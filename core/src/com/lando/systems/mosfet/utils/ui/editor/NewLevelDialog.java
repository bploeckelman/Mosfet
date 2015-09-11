package com.lando.systems.mosfet.utils.ui.editor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.lando.systems.mosfet.screens.LevelEditorScreen;

/**
 * Brian Ploeckelman created on 8/11/2015.
 */
public class NewLevelDialog extends Dialog {

    private static final int MAX_WIDTH = 20;
    private static final int MAX_HEIGHT = 20;

    private LevelEditorScreen levelEditorScreen;

    public NewLevelDialog (String title, Skin skin, LevelEditorScreen levelEditorScreen) {
        super(title, skin);
        this.levelEditorScreen = levelEditorScreen;
        initLayout();
        setZIndex(9999);
    }

    public NewLevelDialog (String title, Skin skin, String windowStyleName, LevelEditorScreen levelEditorScreen) {
        super(title, skin, windowStyleName);
        this.levelEditorScreen = levelEditorScreen;
        initLayout();
        setZIndex(9999);
    }

    public NewLevelDialog (String title, WindowStyle windowStyle, LevelEditorScreen levelEditorScreen) {
        super(title, windowStyle);
        this.levelEditorScreen = levelEditorScreen;
        initLayout();
        setZIndex(9999);
    }

    protected void initLayout() {
        final NewLevelDialog dlg = this;

        final TextField widthField = new TextField("", this.getSkin());
        final TextField heightField = new TextField("", this.getSkin());
        final TextButton okButton = new TextButton("Ok", this.getSkin());
        final TextButton cancelButton = new TextButton("Cancel", this.getSkin());

        okButton.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                String widthStr  = widthField.getText();
                String heightStr = heightField.getText();
                try {
                    int width  = Integer.parseInt(widthStr);
                    int height = Integer.parseInt(heightStr);
                    if (width < 0 || height < 0) throw new NumberFormatException();
                    if (width > MAX_WIDTH || height > MAX_HEIGHT) throw new NumberFormatException();

                    levelEditorScreen.newLevel(width, height);
                    dlg.hide();
                } catch (NumberFormatException e) {
                    widthField.clear();
                    heightField.clear();
                    String errorText = "Invalid value for width or height.\n"
                                     + "Must be integers > 0 and < " + MAX_WIDTH + "x" + MAX_HEIGHT;
                    levelEditorScreen.getInfoDialog().resetText(errorText, levelEditorScreen.getStage());
                }
            }
        });
        cancelButton.addListener(new ButtonInputListenerAdapter() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dlg.hide();
            }
        });

        final float TEXTFIELD_WIDTH = 50f;

        dlg.getContentTable().add("Width");
        dlg.getContentTable().add(widthField).width(TEXTFIELD_WIDTH);
        dlg.getContentTable().row();
        dlg.getContentTable().add("Height");
        dlg.getContentTable().add(heightField).width(TEXTFIELD_WIDTH);
        dlg.getContentTable().row();
        dlg.button(okButton);
        dlg.button(cancelButton);
        dlg.show(levelEditorScreen.getStage());
    }

}
