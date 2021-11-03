// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.detail;

public enum EHarmType {
    SANCTITY("SANCTITY", 0, "\u795e\u5723"),
    UMBRA("UMBRA", 1, "\u6697\u5f71"),
    WATER("WATER", 2, "\u51b0"),
    FIRE("FIRE", 3, "\u706b"),
    SOIL("SOIL", 4, "\u81ea\u7136"),
    ALL_MAGIC("ALL_MAGIC", 5, "\u9b54\u6cd5"),
    DISTANCE_PHYSICS("DISTANCE_PHYSICS", 6, "\u8fdc\u7a0b\u7269\u7406"),
    NEAR_PHYSICS("NEAR_PHYSICS", 7, "\u8fd1\u7a0b\u7269\u7406"),
    PHYSICS("PHYSICS", 8, "\u7269\u7406"),
    ALL("ALL", 9, "\u4e00\u5207");

    String desc;

    private EHarmType(final String name, final int ordinal, final String _desc) {
        this.desc = _desc;
    }

    public static EHarmType get(final String _desc) {
        EHarmType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EHarmType harmType = values[i];
            if (harmType.desc.equals(_desc)) {
                return harmType;
            }
        }
        return null;
    }

    public String getDesc() {
        return this.desc;
    }
}
