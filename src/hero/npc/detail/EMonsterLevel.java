// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.detail;

public enum EMonsterLevel {
    NORMAL("NORMAL", 0, 1, "\u666e\u901a"),
    BOSS("BOSS", 1, 2, "\u9996\u9886");

    private int value;
    private String desc;

    private EMonsterLevel(final String name, final int ordinal, final int _value, final String _desc) {
        this.value = _value;
        this.desc = _desc;
    }

    public int value() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    public static EMonsterLevel getMonsterLevel(final String _desc) {
        EMonsterLevel[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EMonsterLevel type = values[i];
            if (type.getDesc().equals(_desc)) {
                return type;
            }
        }
        return null;
    }
}
