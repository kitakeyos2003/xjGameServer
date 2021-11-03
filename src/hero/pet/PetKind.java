// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet;

public enum PetKind {
    ALL("ALL", 0, (short) 0, "all"),
    DANGKANG("DANGKANG", 1, (short) 1, "\u5f53\u5eb7"),
    GULUNIAO("GULUNIAO", 2, (short) 2, "\u5495\u565c\u9e1f"),
    MENGYANHU("MENGYANHU", 3, (short) 3, "\u68a6\u9b47\u864e"),
    DUJIAOSHOU("DUJIAOSHOU", 4, (short) 4, "\u72ec\u89d2\u517d"),
    XINGMAO("XINGMAO", 5, (short) 5, "\u718a\u732b"),
    QILIN("QILIN", 6, (short) 6, "\u9e92\u9e9f"),
    LINGWENYANG("LINGWENYANG", 7, (short) 7, "\u7075\u7eb9\u7f8a"),
    ZHUHUAI("ZHUHUAI", 8, (short) 8, "\u8bf8\u6000");

    short kindId;
    String name;

    private PetKind(final String name2, final int ordinal, final short kindId, final String name) {
        this.kindId = kindId;
        this.name = name;
    }

    public static PetKind getPetKind(final String kindName) {
        PetKind[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            PetKind kind = values[i];
            if (kind.name.equals(kindName)) {
                return kind;
            }
        }
        return null;
    }

    public static PetKind getPetKind(final short kindId) {
        PetKind[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            PetKind kind = values[i];
            if (kind.kindId == kindId) {
                return kind;
            }
        }
        return null;
    }

    public short getKindID() {
        return this.kindId;
    }
}
