package com.lando.systems.mosfet.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.lando.systems.mosfet.Config;
import com.lando.systems.mosfet.MosfetGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Config.width, Config.height);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new MosfetGame();
        }
}