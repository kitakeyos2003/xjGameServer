// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet;

public enum FeedType {
    NORMAL("NORMAL", 0, (short) 1, "\u666e\u901a\u9972\u6599"),
    HERBIVORE("HERBIVORE", 1, (short) 2, "\u8349\u98df\u6210\u957f\u9972\u6599"),
    CARNIVORE("CARNIVORE", 2, (short) 3, "\u8089\u98df\u6210\u957f\u9972\u6599"),
    DADIJH("DADIJH", 3, (short) 4, "\u5927\u5730\u7cbe\u534e"),
    LYCZ("LYCZ", 4, (short) 5, "\u9f99\u6d8e\u8349\u6c41");

    private short typeID;
    private String typeName;

    private FeedType(final String name, final int ordinal, final short typeID, final String typeName) {
        this.typeID = typeID;
        this.typeName = typeName;
    }

    public short getTypeID() {
        return this.typeID;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static FeedType getFeedType(final String name) {
        FeedType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            FeedType type = values[i];
            if (type.getTypeName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static FeedType getFeedType(final short typeID) {
        FeedType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            FeedType type = values[i];
            if (type.getTypeID() == typeID) {
                return type;
            }
        }
        return null;
    }
}
