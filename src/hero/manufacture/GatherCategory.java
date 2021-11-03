// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture;

public enum GatherCategory {
    ORE("ORE", 0, (byte) 0, "\u77ff\u77f3"),
    HERBS("HERBS", 1, (byte) 1, "\u836f\u8349"),
    LEATHER("LEATHER", 2, (byte) 2, "\u76ae\u9769");

    private byte id;
    private String name;
    static String[] categorys;

    static {
        GatherCategory.categorys = new String[]{GatherCategory.ORE.name, GatherCategory.HERBS.name, GatherCategory.LEATHER.name};
    }

    private GatherCategory(final String name2, final int ordinal, final byte id, final String name) {
        this.id = id;
        this.name = name;
    }

    public byte getId() {
        return this.id;
    }

    public static GatherCategory getGatherCategory(final String _name) {
        GatherCategory[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            GatherCategory category = values[i];
            if (category.name.equals(_name)) {
                return category;
            }
        }
        return GatherCategory.ORE;
    }
}
