package com.lando.systems.mosfet.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lando.systems.mosfet.utils.Assets;

/**
 * Brian Ploeckelman created on 8/11/2015.
 */
public class Entity {

    public enum Type {
        BLANK(0, Assets.blankRegion),
        SPAWN(1, Assets.spawnRegion),
        WALL(2, Assets.wallRegion),
        EXIT(3, Assets.exitRegion);

        private int           value;
        private TextureRegion region;

        Type(int value, TextureRegion region) {
            this.value = value;
            this.region = region;
        }

        public int getValue() {
            return value;
        }

        public TextureRegion getRegion() {
            return region;
        }
    }

    private Type type;

    public Entity(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public int getTypeValue() { return type.getValue(); }

}
