package com.lando.systems.mosfet.android;

import android.os.Bundle;

import android.view.WindowManager;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.lando.systems.mosfet.MosfetGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useWakelock = true;
		config.r = 8;
		config.g = 8;
		config.b = 8;
		config.numSamples = 4;
		WindowManager.LayoutParams layout = getWindow().getAttributes();
		layout.screenBrightness = 1f;
		getWindow().setAttributes(layout);
		initialize(new MosfetGame(), config);
	}
}
