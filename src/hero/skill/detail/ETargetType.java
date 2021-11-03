// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.detail;

public enum ETargetType {
    MYSELF("MYSELF", 0, (byte) 0, "\u81ea\u8eab"),
    ENEMY("ENEMY", 1, (byte) 1, "\u654c\u65b9"),
    FRIEND("FRIEND", 2, (byte) 2, "\u53cb\u65b9"),
    DIER("DIER", 3, (byte) 3, "\u4ea1\u8005"),
    TARGET("TARGET", 4, (byte) 4, "\u76ee\u6807"),
    OWNER("OWNER", 5, (byte) 5, "\u4e3b\u4eba");

    byte value;
    String desc;

    private ETargetType(final String name, final int ordinal, final byte _value, final String _desc) {
        this.value = _value;
        this.desc = _desc;
    }

    public static ETargetType get(final String _typeDesc) {
        ETargetType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            ETargetType type = values[i];
            if (type.desc.equals(_typeDesc)) {
                return type;
            }
        }
        return null;
    }

    public byte value() {
        return this.value;
    }
}
