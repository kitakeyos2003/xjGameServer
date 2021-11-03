// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture;

public enum CraftsmanCategory {
    WQ("WQ", 0, (byte) 0, "\u6b66\u5668"),
    BJ("BJ", 1, (byte) 1, "\u5e03\u7532"),
    QJ("QJ", 2, (byte) 2, "\u8f7b\u7532"),
    CL("CL", 3, (byte) 3, "\u6750\u6599");

    byte id;
    String name;
    static String[] categorys;

    static {
        CraftsmanCategory.categorys = new String[]{CraftsmanCategory.WQ.name, CraftsmanCategory.BJ.name, CraftsmanCategory.QJ.name, CraftsmanCategory.CL.name};
    }

    private CraftsmanCategory(final String name, final int ordinal, final byte _id, final String _name) {
        this.id = _id;
        this.name = _name;
    }

    public byte getId() {
        return this.id;
    }

    public static CraftsmanCategory getCategory(final String _name) {
        if (CraftsmanCategory.WQ.name.equals(_name)) {
            return CraftsmanCategory.WQ;
        }
        if (CraftsmanCategory.BJ.name.equals(_name)) {
            return CraftsmanCategory.BJ;
        }
        if (CraftsmanCategory.QJ.name.equals(_name)) {
            return CraftsmanCategory.QJ;
        }
        return CraftsmanCategory.CL;
    }

    public static String[] getCategorys() {
        return CraftsmanCategory.categorys;
    }
}
