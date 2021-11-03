// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.detail;

public enum EAOERangeType {
    CENTER("CENTER", 0, "\u4e2d\u5fc3\u6a21\u5f0f"),
    FRONT_RECT("FRONT_RECT", 1, "\u524d\u65b9\u77e9\u5f62\u6a21\u5f0f");

    String desc;

    private EAOERangeType(final String name, final int ordinal, final String _desc) {
        this.desc = _desc;
    }

    public static EAOERangeType get(final String _desc) {
        EAOERangeType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EAOERangeType mode = values[i];
            if (mode.desc.equals(_desc)) {
                return mode;
            }
        }
        return null;
    }
}
