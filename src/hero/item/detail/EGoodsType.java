// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.detail;

public enum EGoodsType {
    EQUIPMENT("EQUIPMENT", 0, "\u88c5\u5907", 1),
    MEDICAMENT("MEDICAMENT", 1, "\u836f\u6c34", 2),
    MATERIAL("MATERIAL", 2, "\u6750\u6599", 3),
    TASK_TOOL("TASK_TOOL", 3, "\u4efb\u52a1\u7269\u54c1", 4),
    SPECIAL_GOODS("SPECIAL_GOODS", 4, "\u7279\u6b8a\u7269\u54c1", 5),
    PET_EQUIQ_GOODS("PET_EQUIQ_GOODS", 5, "\u5ba0\u7269\u88c5\u5907", 7),
    PET("PET", 6, "\u5ba0\u7269", 8),
    PET_GOODS("PET_GOODS", 7, "\u5ba0\u7269\u7269\u54c1", 9);

    private String description;
    private byte value;

    private EGoodsType(final String name, final int ordinal, final String _desc, final int _value) {
        this.description = _desc;
        this.value = (byte) _value;
    }

    public String getDescription() {
        return this.description;
    }

    public byte value() {
        return this.value;
    }

    public static EGoodsType getGoodsType(final byte _value) {
        EGoodsType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EGoodsType goodsType = values[i];
            if (goodsType.value == _value) {
                return goodsType;
            }
        }
        return null;
    }
}
