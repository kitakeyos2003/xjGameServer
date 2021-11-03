// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge;

public enum FPType {
    CHARGE("CHARGE", 0, 1, "\u5145\u503c"),
    FEE("FEE", 1, 2, "\u8ba1\u8d39");

    private int typeID;
    private String name;

    private FPType(final String name2, final int ordinal, final int typeID, final String name) {
        this.typeID = typeID;
        this.name = name;
    }

    public static FPType getFPTypeByName(final String name) {
        FPType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            FPType fpt = values[i];
            if (fpt.name.equals(name)) {
                return fpt;
            }
        }
        return null;
    }

    public static FPType getFPTypeByTypeID(final int typeID) {
        FPType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            FPType fpt = values[i];
            if (fpt.typeID == typeID) {
                return fpt;
            }
        }
        return null;
    }
}
