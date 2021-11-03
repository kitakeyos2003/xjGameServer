// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.service;

public class GoodsIDLimitDefine {

    public static final int[] MATERIAL_ID_LIMIT;
    public static final int[] MEDICAMENT_ID_LIMIT;
    public static final int[] TASK_GOODS_ID_LIMIT;
    public static final int[] WEAPON_ID_LIMIT;
    public static final int[] ARMOR_ID_LIMIT;
    public static final int[] SPECIAL_GOODS_LIMIT;
    public static final int[] PET_SKILL_BOOK;
    public static final int[] PET_FEED_GOODS_LIMIT;
    public static final int[] PET_REVIVE_LIMIT;
    public static final int[] PET_DICARD_LIMIT;
    public static final int[] PET_EQUIP_ARMOT_LIMIT;
    public static final int[] PET_EQUIP_WEAPON_LIMIT;
    public static final int[] PET_LIMIT;

    static {
        MATERIAL_ID_LIMIT = new int[]{320000, 329999};
        MEDICAMENT_ID_LIMIT = new int[]{310000, 319999};
        TASK_GOODS_ID_LIMIT = new int[]{330000, 339999};
        WEAPON_ID_LIMIT = new int[]{1108000, 1112999};
        ARMOR_ID_LIMIT = new int[]{1200000, 1499999};
        SPECIAL_GOODS_LIMIT = new int[]{340000, 349999};
        PET_SKILL_BOOK = new int[]{351000, 354999};
        PET_FEED_GOODS_LIMIT = new int[]{355000, 355999};
        PET_REVIVE_LIMIT = new int[]{356000, 356999};
        PET_DICARD_LIMIT = new int[]{357000, 357999};
        PET_EQUIP_ARMOT_LIMIT = new int[]{1514000, 1516999};
        PET_EQUIP_WEAPON_LIMIT = new int[]{1513000, 1513999};
        PET_LIMIT = new int[]{9200, 9299};
    }
}
