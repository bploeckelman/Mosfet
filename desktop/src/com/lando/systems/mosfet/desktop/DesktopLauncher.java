package com.lando.systems.mosfet.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.MosfetGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = Config.title;
		config.width = Config.width;
		config.height = Config.height;
		config.resizable = Config.resizable;
		new LwjglApplication(new MosfetGame(), config);
	}
}
