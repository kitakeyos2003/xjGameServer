// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.detail;

public enum EBodyPartOfEquipment implements BodyPartOfEquipment {
    HEAD("HEAD", 0, 0, "\u5934\u90e8"),
    BOSOM("BOSOM", 1, 1, "\u80f8\u90e8"),
    HAND("HAND", 2, 2, "\u624b\u90e8"),
    FOOT("FOOT", 3, 3, "\u811a\u90e8"),
    WEAPON("WEAPON", 4, 4, "\u6b66\u5668"),
    FINGER("FINGER", 5, 5, "\u9888\u90e8"),
    WAIST("WAIST", 6, 6, "\u8170\u90e8"),
    ADORM("ADORM", 7, 7, "\u624b\u6307"),
    WRIST("WRIST", 8, 8, "\u624b\u8155"),
    PET_F("PET_F", 9, 9, "\u8ddf\u968f\u5ba0\u7269"),
    PET_S("PET_S", 10, 10, "\u5750\u9a91\u5ba0\u7269"),
    EXTEND("EXTEND", 11, 11, "\u6269\u5c55");

    private int bodyPartValue;
    private String desc;

    private EBodyPartOfEquipment(final String name, final int ordinal, final int _bodyPartValue, final String _desc) {
        this.bodyPartValue = _bodyPartValue;
        this.desc = _desc;
    }

    @Override
    public int value() {
        return this.bodyPartValue;
    }

    public String getDesc() {
        return this.desc;
    }

    public static final EBodyPartOfEquipment getBodyPart(final int _bodyPartValue) {
        EBodyPartOfEquipment[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EBodyPartOfEquipment part = values[i];
            if (part.value() == _bodyPartValue) {
                return part;
            }
        }
        return null;
    }

    public static final EBodyPartOfEquipment getBodyPart(final String _desc) {
        EBodyPartOfEquipment[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EBodyPartOfEquipment part = values[i];
            if (part.getDesc().equals(_desc)) {
                return part;
            }
        }
        return null;
    }
}
