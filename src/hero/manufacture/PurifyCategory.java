// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture;

public enum PurifyCategory {
    WQ("WQ", 0, (byte) 0, "\u6b66\u5668"),
    KJ("KJ", 1, (byte) 1, "\u94e0\u7532"),
    CL("CL", 2, (byte) 2, "\u6750\u6599"),
    BS("BS", 3, (byte) 3, "\u5b9d\u77f3");

    byte id;
    String name;
    static String[] categorys;

    static {
        PurifyCategory.categorys = new String[]{PurifyCategory.WQ.name, PurifyCategory.KJ.name, PurifyCategory.CL.name, PurifyCategory.BS.name};
    }

    private PurifyCategory(final String name2, final int ordinal, final byte id, final String name) {
        this.id = id;
        this.name = name;
    }

    public byte getId() {
        return this.id;
    }

    public static PurifyCategory getPurifyCategory(final String _name) {
        PurifyCategory[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            PurifyCategory category = values[i];
            if (category.name.equals(_name)) {
                return category;
            }
        }
        return PurifyCategory.WQ;
    }
}
