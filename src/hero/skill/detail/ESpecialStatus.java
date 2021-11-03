// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.detail;

public enum ESpecialStatus {
    HIDE("HIDE", 0, "\u6f5c\u884c"),
    DUMB("DUMB", 1, "\u6c89\u9ed8"),
    FAINT("FAINT", 2, "\u6655\u53a5"),
    SLEEP("SLEEP", 3, "\u660f\u7761"),
    MOVE_FAST("MOVE_FAST", 4, "\u52a0\u901f"),
    MOVE_SLOWLY("MOVE_SLOWLY", 5, "\u51cf\u901f"),
    LAUGH("LAUGH", 6, "\u5632\u8bbd"),
    PHY_BOOM("PHY_BOOM", 7, "\u7269\u7406\u66b4\u51fb"),
    MAG_BOOM("MAG_BOOM", 8, "\u9b54\u6cd5\u66b4\u51fb"),
    STOP("STOP", 9, "\u5b9a\u8eab");

    String desc;

    private ESpecialStatus(final String name, final int ordinal, final String _desc) {
        this.desc = _desc;
    }

    public static ESpecialStatus get(final String _desc) {
        ESpecialStatus[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            ESpecialStatus specialStatus = values[i];
            if (specialStatus.desc.equals(_desc)) {
                return specialStatus;
            }
        }
        return null;
    }
}
