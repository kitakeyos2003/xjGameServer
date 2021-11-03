// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

public class Constant {

    public static final short SHORT_MAX_VALUE = 32760;
    public static final int INTEGER_MAX_VALUE = 1000000000;
    public static final short CLIENT_OF_LOW_SIDE = 1;
    public static final short CLIENT_OF_HIGH_SIDE = 3;
    public static final int INTERVAL_OF_RESUME_ENERGY = 3000;
    public static final int DISENGAGE_FIGHT_TIME = 15000;
    public static String CHINESE_ENCODING;
    public static int HEARTBEAT_INTERVAL;
    public static int COLOR_SYSTEM;
    public static int COLOR_WORLD;
    public static int COLOR_CONSORTIA;
    public static int COLOR_PERSIONAL;
    public static int COLOR_TEAM;
    public static int COLOR_MAP;
    public static final short DEFAULT_IMMOBILITY_TIME = 1;
    public static final short DEFAULT_ATTACK_DISTANCE = 3;
    public static final short BALANCE_ATTACK_DISTANCE = 2;
    public static final short MAP_GRID_PIXELS_SIZE = 16;
    public static int SERVER_ID;
    public static int GOODS_ID_BOUNDARY;
    public static int CHARACTER_LIFECYLE;
    public static int FOLLOW_INTERVAL;

    static {
        Constant.FOLLOW_INTERVAL = 1000;
    }

    public static boolean isSkillUnit(final int _skillUnitID) {
        return _skillUnitID > 10000 && _skillUnitID < 99999;
    }

    public static boolean isEffectUnit(final int _effectUnitID) {
        return _effectUnitID > 100000 && _effectUnitID < 999999;
    }
}
