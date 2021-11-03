// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture;

public enum BlacksmithCategory {
    WQ("WQ", 0, (byte) 0, "\u6b66\u5668"),
    KJ("KJ", 1, (byte) 1, "\u94e0\u7532"),
    CL("CL", 2, (byte) 2, "\u6750\u6599");

    byte id;
    String name;
    static String[] categorys;

    static {
        BlacksmithCategory.categorys = new String[]{BlacksmithCategory.WQ.name, BlacksmithCategory.KJ.name, BlacksmithCategory.CL.name};
    }

    private BlacksmithCategory(final String name, final int ordinal, final byte _id, final String _name) {
        this.id = _id;
        this.name = _name;
    }

    public byte getId() {
        return this.id;
    }

    public static BlacksmithCategory getCategory(final String _name) {
        if (BlacksmithCategory.WQ.name.equals(_name)) {
            return BlacksmithCategory.WQ;
        }
        if (BlacksmithCategory.KJ.name.equals(_name)) {
            return BlacksmithCategory.KJ;
        }
        return BlacksmithCategory.CL;
    }
}
