// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture;

public enum ManufactureType {
    DISPENSER("DISPENSER", 0, (byte) 4, "\u836f\u8349\u5320"),
    JEWELER("JEWELER", 1, (byte) 3, "\u9274\u5b9d\u5320"),
    CRAFTSMAN("CRAFTSMAN", 2, (byte) 2, "\u5de5\u827a\u5320"),
    BLACKSMITH("BLACKSMITH", 3, (byte) 1, "\u94c1\u5320"),
    GRATHER("GRATHER", 4, (byte) 6, "\u91c7\u96c6\u5e08"),
    PURIFY("PURIFY", 5, (byte) 5, "\u63d0\u7eaf\u5e08");

    private byte id;
    private String name;

    private ManufactureType(final String name, final int ordinal, final byte _id, final String _name) {
        this.id = _id;
        this.name = _name;
    }

    public byte getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public static ManufactureType get(final byte _value) {
        ManufactureType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            ManufactureType type = values[i];
            if (type.id == _value) {
                return type;
            }
        }
        return null;
    }

    public static ManufactureType get(final String _typeDesc) {
        ManufactureType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            ManufactureType type = values[i];
            if (type.name.equals(_typeDesc)) {
                return type;
            }
        }
        return null;
    }
}
