// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.detail;

public enum EAOERangeBaseLine {
    RELEASER("RELEASER", 0, "\u65bd\u653e\u8005"),
    TARGET("TARGET", 1, "\u88ab\u65bd\u653e\u8005");

    String desc;

    private EAOERangeBaseLine(final String name, final int ordinal, final String _desc) {
        this.desc = _desc;
    }

    public static EAOERangeBaseLine get(final String _desc) {
        EAOERangeBaseLine[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EAOERangeBaseLine base = values[i];
            if (base.desc.equals(_desc)) {
                return base;
            }
        }
        return null;
    }
}
