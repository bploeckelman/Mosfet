package com.lando.systems.mosfet;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.lando.systems.mosfet.screens.MainMenuScreen;
import com.lando.systems.mosfet.utils.Assets;

public class MosfetGame extends Game {

	@Override
	public void create () {
		Assets.load();
		Gdx.input.setCatchBackKey(true);
		setScreen(new MainMenuScreen(this));

	}

	@Override
	public void render () {

		float delta = Gdx.graphics.getDeltaTime();
		delta = Math.min(delta, 1 / 30f);
		Assets.tween.update(delta);
		if (screen != null) screen.render(delta); // use our framerate limiter not let the superclass decide
	}

	@Override
	public void dispose() {
		Assets.dispose();
	}

}
