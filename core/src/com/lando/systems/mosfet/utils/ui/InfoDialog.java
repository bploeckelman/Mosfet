package com.lando.systems.mosfet.utils.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Brian Ploeckelman created on 8/9/2015.
 */
public class InfoDialog extends Dialog {

    public InfoDialog (String title, Skin skin) {
        super(title, skin);
        initButton();
        setZIndex(9999);
    }

    public InfoDialog (String title, Skin skin, String windowStyleName) {
        super(title, skin, windowStyleName);
        initButton();
        setZIndex(9999);
    }

    public InfoDialog (String title, WindowStyle windowStyle) {
        super(title, windowStyle);
        initButton();
        setZIndex(9999);
    }

    protected void initButton() {
        final TextButton okButton = new TextButton("Ok", this.getSkin());
        okButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                InfoDialog.this.hide();
            }
        });
        this.button(okButton);
    }

    public void resetText(String text) {
        this.getContentTable().clear();
        this.text(text);
    }

    public void resetText(String text, Stage stage) {
        resetText(text);
        this.show(stage);
    }

}
