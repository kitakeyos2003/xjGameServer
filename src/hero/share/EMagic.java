// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

public enum EMagic {
    ALL("ALL", 0, 0, "\u5168\u90e8"),
    SANCTITY("SANCTITY", 1, 1, "\u795e\u5723"),
    UMBRA("UMBRA", 2, 2, "\u6697\u5f71"),
    WATER("WATER", 3, 3, "\u51b0"),
    FIRE("FIRE", 4, 4, "\u706b"),
    SOIL("SOIL", 5, 5, "\u81ea\u7136");

    private int ID;
    private String name;

    private EMagic(final String name, final int ordinal, final int _id, final String _name) {
        this.ID = _id;
        this.name = _name;
    }

    public int getID() {
        return this.ID;
    }

    public String getName() {
        return this.name;
    }

    public static EMagic getMagic(final int _magicID) {
        switch (_magicID) {
            case 0: {
                return EMagic.ALL;
            }
            case 1: {
                return EMagic.SANCTITY;
            }
            case 2: {
                return EMagic.UMBRA;
            }
            case 3: {
                return EMagic.WATER;
            }
            case 4: {
                return EMagic.FIRE;
            }
            case 5: {
                return EMagic.SOIL;
            }
            default: {
                return null;
            }
        }
    }

    public static EMagic getMagic(final String _magicName) {
        EMagic[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EMagic magic = values[i];
            if (magic.getName().equals(_magicName)) {
                return magic;
            }
        }
        return null;
    }
}
