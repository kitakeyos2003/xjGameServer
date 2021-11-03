// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.detail;

public enum ETargetRangeType {
    SINGLE("SINGLE", 0, "\u5355\u4f53"),
    TEAM("TEAM", 1, "\u56e2\u961f"),
    SOME("SOME", 2, "\u7fa4\u4f53");

    String desc;

    private ETargetRangeType(final String name, final int ordinal, final String _desc) {
        this.desc = _desc;
    }

    public static ETargetRangeType get(final String _desc) {
        ETargetRangeType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            ETargetRangeType rangeType = values[i];
            if (rangeType.desc.equals(_desc)) {
                return rangeType;
            }
        }
        return null;
    }
}
