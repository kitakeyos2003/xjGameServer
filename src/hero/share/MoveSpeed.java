// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

public class MoveSpeed {

    public static final byte SLOWEST = 1;
    public static final byte SLOWER = 2;
    public static final byte GENERIC = 3;
    public static final byte FASTER = 4;
    public static final byte FASTEST = 5;
    public static final byte SPEED_LESSEN_STATE = 0;
    public static final byte SPEED_GENERIC_STATE = 1;
    public static final byte SPEED_ADD_STATE = 2;
    private static final byte[] ADD_SPEED_LIST;
    private static final byte[] LESSEN_SPEED_LIST;
    public static final byte NORMAL_MONSTER_ATTACK_ADD = 1;
    public static final byte BOSS_MONSTER_ATTACK_ADD = 2;

    static {
        ADD_SPEED_LIST = new byte[]{3, 4, 5};
        LESSEN_SPEED_LIST = new byte[]{3, 2, 1};
    }

    public static byte getNowSpeed(final byte _speed, final int _speedLevel) {
        byte speed = 3;
        if (_speed == 0) {
            speed = MoveSpeed.LESSEN_SPEED_LIST[_speedLevel];
        } else if (_speed == 1) {
            speed = 3;
        } else if (_speed == 2) {
            speed = MoveSpeed.ADD_SPEED_LIST[_speedLevel];
        }
        return speed;
    }
}
