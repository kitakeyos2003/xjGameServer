// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.dict;

public enum PickType {
    PICK("PICK", 0, "\u62fe\u53d6"),
    GATHER("GATHER", 1, "\u91c7\u96c6");

    private String type;

    private PickType(final String name, final int ordinal, final String type) {
        this.type = type;
    }

    static PickType getPickType(final String typeName) {
        PickType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            PickType pickType = values[i];
            if (pickType.type.equals(typeName)) {
                return pickType;
            }
        }
        return null;
    }
}
