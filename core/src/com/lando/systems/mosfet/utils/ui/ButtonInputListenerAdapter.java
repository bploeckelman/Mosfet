package com.lando.systems.mosfet.utils.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * Brian Ploeckelman created on 8/9/2015.
 *
 * Adapter for InputListener so that Buttons can add anonymous listeners to catch click events
 * and handle them by just implementing touchUp() instead of having to override touchDown() for one line.
 */
public class ButtonInputListenerAdapter extends InputListener {

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        return true;
    }

}
