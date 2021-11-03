// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.bag;

public enum EBagType {
    BODY_WEAR("BODY_WEAR", 0, 10, "\u8eab\u4e0a"),
    MEDICAMENT_BAG("MEDICAMENT_BAG", 1, 36, "\u836f\u6c34\u80cc\u5305"),
    MATERIAL_BAG("MATERIAL_BAG", 2, 37, "\u6750\u6599\u80cc\u5305"),
    TASK_TOOL_BAG("TASK_TOOL_BAG", 3, 38, "\u4efb\u52a1\u7269\u54c1\u80cc\u5305"),
    SPECIAL_GOODS_BAG("SPECIAL_GOODS_BAG", 4, 39, "\u7279\u6b8a\u7269\u54c1"),
    EQUIPMENT_BAG("EQUIPMENT_BAG", 5, 56, "\u88c5\u5907\u80cc\u5305"),
    PET_EQUIPMENT_BAG("PET_EQUIPMENT_BAG", 6, 57, "\u5ba0\u7269\u88c5\u5907\u80cc\u5305"),
    PET_BODY_WEAR("PET_BODY_WEAR", 7, 58, "\u5ba0\u7269\u8eab\u4e0a"),
    PET_GOODS_BAG("PET_GOODS_BAG", 8, 59, "\u5ba0\u7269\u7269\u54c1\u80cc\u5305"),
    PET_BAG("PET_BAG", 9, 60, "\u5ba0\u7269\u80cc\u5305"),
    STORAGE_BAG("STORAGE_BAG", 10, 99, "\u4ed3\u5e93");

    private byte typeValue;
    private String description;

    private EBagType(final String name, final int ordinal, final int _typeValue, final String _description) {
        this.typeValue = (byte) _typeValue;
        this.description = _description;
    }

    public byte getTypeValue() {
        return this.typeValue;
    }

    public String getDescription() {
        return this.description;
    }

    public static EBagType getBagType(final int _typeValue) {
        EBagType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EBagType type = values[i];
            if (type.typeValue == _typeValue) {
                return type;
            }
        }
        return null;
    }
}
