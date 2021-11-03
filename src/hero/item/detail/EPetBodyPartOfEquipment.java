// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.detail;

public enum EPetBodyPartOfEquipment implements BodyPartOfEquipment {
    HEAD("HEAD", 0, 0, "\u5934\u90e8"),
    BODY("BODY", 1, 1, "\u8eab\u8eaf"),
    CLAW("CLAW", 2, 2, "\u722a\u90e8"),
    TAIL("TAIL", 3, 3, "\u5c3e\u90e8");

    private int bodyPartValue;
    private String desc;

    private EPetBodyPartOfEquipment(final String name, final int ordinal, final int bodyPartValue, final String desc) {
        this.bodyPartValue = bodyPartValue;
        this.desc = desc;
    }

    public static EPetBodyPartOfEquipment getBodyPart(final int bodyPartValue) {
        EPetBodyPartOfEquipment[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EPetBodyPartOfEquipment part = values[i];
            if (part.bodyPartValue == bodyPartValue) {
                return part;
            }
        }
        return null;
    }

    public static EPetBodyPartOfEquipment getBodyPart(final String desc) {
        EPetBodyPartOfEquipment[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EPetBodyPartOfEquipment part = values[i];
            if (part.desc.equals(desc)) {
                return part;
            }
        }
        return null;
    }

    @Override
    public int value() {
        return this.bodyPartValue;
    }

    public String getDesc() {
        return this.desc;
    }
}
