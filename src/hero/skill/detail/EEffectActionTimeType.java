// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.detail;

public enum EEffectActionTimeType {
    PER_THREE_SECOND("PER_THREE_SECOND", 0, "\u4e09\u79d2"),
    AT_END("AT_END", 1, "\u672b\u7aef"),
    RANDOM("RANDOM", 2, "\u968f\u673a");

    String desc;

    private EEffectActionTimeType(final String name, final int ordinal, final String _desc) {
        this.desc = _desc;
    }

    public static EEffectActionTimeType get(final String _desc) {
        EEffectActionTimeType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EEffectActionTimeType specialStatus = values[i];
            if (specialStatus.desc.equals(_desc)) {
                return specialStatus;
            }
        }
        return null;
    }
}
