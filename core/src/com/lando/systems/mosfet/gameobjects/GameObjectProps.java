package com.lando.systems.mosfet.gameobjects;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lando.systems.mosfet.world.Entity;

/**
 * Brian Ploeckelman created on 9/25/2015.
 */
public class GameObjectProps {

    private static final int NUM_BITS_LINKAGES  = 8;
    private static final int NUM_BITS_DIRECTION = 2;
    private static final int NUM_BITS_TYPE      = 6;

    /**
     * -------- -------- -------- --------
     * |----reserved---| |-links| |d|type|
     *
     * 0-5 game object type
     * 6,7 direction [NESW - 00, 01, 10, 11]
     * 8-15 linkages (objects w/same link bit set are linked)
     * 16-31 reserved (lol)
     */
    private int bits;

    public Entity.Type        entityType;
    public BaseGameObject.DIR direction;
    public int                linkages;

    public GameObjectProps(int bits) {
        this.bits = bits;
    }

    public GameObjectProps(Entity.Type type) {
        this(type, BaseGameObject.DIR.UP, 0);
    }

    public GameObjectProps(Entity.Type type, BaseGameObject.DIR direction, int linkages) {
        this.entityType = type;
        this.direction  = direction;
        this.linkages   = linkages;
        this.bits       = getBits();
    }

    public int getBits() {
        bits = 0;

        bits <<= NUM_BITS_LINKAGES;
        bits = linkages;

        bits <<= NUM_BITS_DIRECTION;
        switch (direction) {
            case UP:    bits |= 0; break;
            case RIGHT: bits |= 1; break;
            case DOWN:  bits |= 2; break;
            case LEFT:  bits |= 3; break;
        }

        bits <<= NUM_BITS_TYPE;
        bits |= entityType.getValue();

        return bits;
    }

    public Entity.Type getType() {
        final int BITMASK = 63;
        int typeValue = bits & BITMASK;
        return Entity.Type.getTypeForValue(typeValue);
    }

    public BaseGameObject.DIR getDir() {
        final int BITMASK = 3;
        int dirValue = (bits >> NUM_BITS_TYPE) & BITMASK;
        switch (dirValue) {
            case 0: return BaseGameObject.DIR.UP;
            case 1: return BaseGameObject.DIR.RIGHT;
            case 2: return BaseGameObject.DIR.DOWN;
            case 3: return BaseGameObject.DIR.LEFT;
            default: throw new GdxRuntimeException("Invalid direction value: '" + dirValue + "', expected [0,3]");
        }
    }

    public int getLinkages() {
        final int BITMASK = 0xFF;
        int linkageValue = (bits >> (NUM_BITS_TYPE + NUM_BITS_DIRECTION)) & BITMASK;
        return linkageValue;
    }

}
