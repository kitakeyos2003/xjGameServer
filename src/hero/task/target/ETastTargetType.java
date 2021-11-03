// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.target;

public enum ETastTargetType {
    GOODS("GOODS", 0, "\u7269\u54c1"),
    FOUND_A_PATH("FOUND_A_PATH", 1, "\u63a2\u8def"),
    ESCORT_NPC("ESCORT_NPC", 2, "\u62a4\u9001"),
    OPEN_GEAR("OPEN_GEAR", 3, "\u673a\u5173"),
    KILL_MONSTER("KILL_MONSTER", 4, "\u6740\u602a");

    private String typeDesc;

    private ETastTargetType(final String name, final int ordinal, final String _typeDesc) {
        this.typeDesc = _typeDesc;
    }

    public static ETastTargetType getTastTargetTypeByDesc(final String _desc) {
        if (_desc == null) {
            return null;
        }
        ETastTargetType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            ETastTargetType type = values[i];
            if (type.typeDesc.equals(_desc)) {
                return type;
            }
        }
        return null;
    }
}
