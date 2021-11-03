// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather;

public enum RefinedCategory {
    WZ("WZ", 0, (byte) 0, "\u7269\u8d28"),
    JT("JT", 1, (byte) 1, "\u6676\u4f53");

    byte id;
    String name;
    static String[] categorys;

    static {
        RefinedCategory.categorys = new String[]{RefinedCategory.WZ.name, RefinedCategory.JT.name};
    }

    private RefinedCategory(final String name, final int ordinal, final byte _id, final String _name) {
        this.id = _id;
        this.name = _name;
    }

    public byte getId() {
        return this.id;
    }

    public static RefinedCategory getCategory(final String _name) {
        if (RefinedCategory.WZ.name.equals(_name)) {
            return RefinedCategory.WZ;
        }
        return RefinedCategory.JT;
    }

    public static String[] getCategorys() {
        return RefinedCategory.categorys;
    }
}
