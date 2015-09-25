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
        EXIT(3, Assets.exitRegion),
        BLOCKER_PULL(4, Assets.blockerPullRegion),
        BLOCKER_PUSH(5, Assets.blockerPushRegion),
        DOOR(6, Assets.doorClosedRegion),
        DUMB_ROBOT(7, Assets.aiRegion),
        SPINNER(8, Assets.spinnerRegion),
        SWITCH(9, Assets.switchRegion),
        TELEPORT(10, Assets.teleportRegion);

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

        public static TextureRegion getRegionForValue(int value) {
            switch (value) {
                case 0:  return Entity.Type.BLANK.getRegion();
                case 1:  return Entity.Type.SPAWN.getRegion();
                case 2:  return Entity.Type.WALL.getRegion();
                case 3:  return Entity.Type.EXIT.getRegion();
                case 4:  return Entity.Type.BLOCKER_PULL.getRegion();
                case 5:  return Entity.Type.BLOCKER_PUSH.getRegion();
                case 6:  return Entity.Type.DOOR.getRegion();
                case 7:  return Entity.Type.DUMB_ROBOT.getRegion();
                case 8:  return Entity.Type.SPINNER.getRegion();
                case 9:  return Entity.Type.SWITCH.getRegion();
                case 10: return Entity.Type.TELEPORT.getRegion();
                default: return Entity.Type.BLANK.getRegion();
            }
        }

        public static Entity.Type getTypeForValue(int value) {
            switch (value) {
                case 0:  return Entity.Type.BLANK;
                case 1:  return Entity.Type.SPAWN;
                case 2:  return Entity.Type.WALL;
                case 3:  return Entity.Type.EXIT;
                case 4:  return Entity.Type.BLOCKER_PULL;
                case 5:  return Entity.Type.BLOCKER_PUSH;
                case 6:  return Entity.Type.DOOR;
                case 7:  return Entity.Type.DUMB_ROBOT;
                case 8:  return Entity.Type.SPINNER;
                case 9:  return Entity.Type.SWITCH;
                case 10: return Entity.Type.TELEPORT;
                default: return Entity.Type.BLANK;
            }
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
