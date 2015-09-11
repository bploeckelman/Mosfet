package com.lando.systems.mosfet;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.utils.Assets;
import com.lando.systems.mosfet.world.Level;

public class MosfetGame extends Game {

	@Override
	public void create () {
		Assets.load();
		setScreen(new GamePlayScreen(this, new Level(Config.tilesWide, Config.tilesHigh)));
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		delta = Math.min(delta, 1 / 30f);
		Assets.tween.update(delta);
		super.render();
	}

	@Override
	public void dispose() {
		Assets.dispose();
	}

}
