// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture;

public enum JewelerCategory {
    SS("SS", 0, (byte) 0, "\u9996\u9970"),
    CL("CL", 1, (byte) 1, "\u6750\u6599");

    byte id;
    String name;
    static String[] categorys;

    static {
        JewelerCategory.categorys = new String[]{JewelerCategory.SS.name, JewelerCategory.CL.name};
    }

    private JewelerCategory(final String name, final int ordinal, final byte _id, final String _name) {
        this.id = _id;
        this.name = _name;
    }

    public byte getId() {
        return this.id;
    }

    public static JewelerCategory getCategory(final String _name) {
        if (JewelerCategory.SS.name.equals(_name)) {
            return JewelerCategory.SS;
        }
        return JewelerCategory.CL;
    }

    public static String[] getCategorys() {
        return JewelerCategory.categorys;
    }
}
