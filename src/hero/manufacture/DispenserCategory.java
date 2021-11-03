// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture;

public enum DispenserCategory {
    YS("YS", 0, (byte) 0, "\u836f\u6c34"),
    JZ("JZ", 1, (byte) 1, "\u5377\u8f74"),
    CL("CL", 2, (byte) 2, "\u6750\u6599");

    byte id;
    String name;
    static String[] categorys;

    static {
        DispenserCategory.categorys = new String[]{DispenserCategory.YS.name, DispenserCategory.JZ.name, DispenserCategory.CL.name};
    }

    private DispenserCategory(final String name, final int ordinal, final byte _id, final String _name) {
        this.id = _id;
        this.name = _name;
    }

    public static DispenserCategory getCategory(final String _name) {
        if (DispenserCategory.YS.name.equals(_name)) {
            return DispenserCategory.YS;
        }
        if (DispenserCategory.JZ.name.equals(_name)) {
            return DispenserCategory.JZ;
        }
        return DispenserCategory.CL;
    }

    public byte getId() {
        return this.id;
    }

    public static String[] getCategorys() {
        return DispenserCategory.categorys;
    }
}
