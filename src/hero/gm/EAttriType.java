// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm;

public enum EAttriType {
    LEVEL("LEVEL", 0, "\u7ea7\u522b"),
    MONEY("MONEY", 1, "\u91d1\u94b1"),
    VOCATION("VOCATION", 2, "\u804c\u4e1a"),
    EXP("EXP", 3, "\u7ecf\u9a8c");

    private String desc;

    private EAttriType(final String name, final int ordinal, final String _name) {
        this.desc = _name;
    }

    public String getDesc() {
        return this.desc;
    }

    public static EAttriType getEType(final String _name) {
        EAttriType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EAttriType et = values[i];
            if (et.getDesc().equals(_name)) {
                return et;
            }
        }
        return null;
    }
}
