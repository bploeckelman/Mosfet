package com.lando.systems.mosfet;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.screens.LevelSelectScreen;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.world.Level;

public class MosfetGame extends Game {

	@Override
	public void create () {
		Assets.load();

//		final FileHandle levelFile = Gdx.files.internal("levels/level1.lvl");
//		setScreen(new GamePlayScreen(this, (new Json()).fromJson(Level.class, levelFile)));
		setScreen(new LevelSelectScreen(this));
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
