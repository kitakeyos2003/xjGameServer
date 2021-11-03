// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.dictionary;

import hero.item.Goods;
import hero.item.service.GoodsIDLimitDefine;
import hero.item.detail.EGoodsType;
import org.apache.log4j.Logger;

public class GoodsContents {

    private static Logger log;

    static {
        GoodsContents.log = Logger.getLogger((Class) GoodsContents.class);
    }

    private GoodsContents() {
    }

    public static EGoodsType getGoodsType(final int _goodsID) {
        if (GoodsIDLimitDefine.MATERIAL_ID_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.MATERIAL_ID_LIMIT[1] >= _goodsID) {
            return EGoodsType.MATERIAL;
        }
        if (GoodsIDLimitDefine.MEDICAMENT_ID_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.MEDICAMENT_ID_LIMIT[1] >= _goodsID) {
            return EGoodsType.MEDICAMENT;
        }
        if (GoodsIDLimitDefine.TASK_GOODS_ID_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.TASK_GOODS_ID_LIMIT[1] >= _goodsID) {
            return EGoodsType.TASK_TOOL;
        }
        if (GoodsIDLimitDefine.WEAPON_ID_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.WEAPON_ID_LIMIT[1] >= _goodsID) {
            return EGoodsType.EQUIPMENT;
        }
        if (GoodsIDLimitDefine.ARMOR_ID_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.ARMOR_ID_LIMIT[1] >= _goodsID) {
            return EGoodsType.EQUIPMENT;
        }
        if (GoodsIDLimitDefine.SPECIAL_GOODS_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.SPECIAL_GOODS_LIMIT[1] >= _goodsID) {
            return EGoodsType.SPECIAL_GOODS;
        }
        if (GoodsIDLimitDefine.PET_EQUIP_ARMOT_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.PET_EQUIP_ARMOT_LIMIT[1] >= _goodsID) {
            return EGoodsType.PET_EQUIQ_GOODS;
        }
        if (GoodsIDLimitDefine.PET_EQUIP_WEAPON_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.PET_EQUIP_WEAPON_LIMIT[1] >= _goodsID) {
            return EGoodsType.PET_EQUIQ_GOODS;
        }
        if (GoodsIDLimitDefine.PET_SKILL_BOOK[0] <= _goodsID && GoodsIDLimitDefine.PET_SKILL_BOOK[1] >= _goodsID) {
            return EGoodsType.PET_GOODS;
        }
        if (GoodsIDLimitDefine.PET_FEED_GOODS_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.PET_FEED_GOODS_LIMIT[1] >= _goodsID) {
            return EGoodsType.PET_GOODS;
        }
        if (GoodsIDLimitDefine.PET_REVIVE_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.PET_REVIVE_LIMIT[1] >= _goodsID) {
            return EGoodsType.PET_GOODS;
        }
        if (GoodsIDLimitDefine.PET_DICARD_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.PET_DICARD_LIMIT[1] >= _goodsID) {
            return EGoodsType.PET_GOODS;
        }
        if (GoodsIDLimitDefine.PET_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.PET_LIMIT[1] >= _goodsID) {
            return EGoodsType.PET;
        }
        return null;
    }

    public static Goods getGoods(final int _goodsID) {
        if (GoodsIDLimitDefine.MATERIAL_ID_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.MATERIAL_ID_LIMIT[1] >= _goodsID) {
            return MaterialDict.getInstance().getMaterial(_goodsID);
        }
        if (GoodsIDLimitDefine.MEDICAMENT_ID_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.MEDICAMENT_ID_LIMIT[1] >= _goodsID) {
            return MedicamentDict.getInstance().getMedicament(_goodsID);
        }
        if (GoodsIDLimitDefine.TASK_GOODS_ID_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.TASK_GOODS_ID_LIMIT[1] >= _goodsID) {
            return TaskGoodsDict.getInstance().getTaskTool(_goodsID);
        }
        if (GoodsIDLimitDefine.WEAPON_ID_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.WEAPON_ID_LIMIT[1] >= _goodsID) {
            return WeaponDict.getInstance().getWeapon(_goodsID);
        }
        if (GoodsIDLimitDefine.ARMOR_ID_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.ARMOR_ID_LIMIT[1] >= _goodsID) {
            return ArmorDict.getInstance().getArmor(_goodsID);
        }
        if (GoodsIDLimitDefine.SPECIAL_GOODS_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.SPECIAL_GOODS_LIMIT[1] >= _goodsID) {
            return SpecialGoodsDict.getInstance().getSpecailGoods(_goodsID);
        }
        if (GoodsIDLimitDefine.PET_SKILL_BOOK[0] <= _goodsID && GoodsIDLimitDefine.PET_SKILL_BOOK[1] >= _goodsID) {
            return SpecialGoodsDict.getInstance().getSpecailGoods(_goodsID);
        }
        if (GoodsIDLimitDefine.PET_FEED_GOODS_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.PET_FEED_GOODS_LIMIT[1] >= _goodsID) {
            return SpecialGoodsDict.getInstance().getSpecailGoods(_goodsID);
        }
        if (GoodsIDLimitDefine.PET_REVIVE_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.PET_REVIVE_LIMIT[1] >= _goodsID) {
            return SpecialGoodsDict.getInstance().getSpecailGoods(_goodsID);
        }
        if (GoodsIDLimitDefine.PET_DICARD_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.PET_DICARD_LIMIT[1] >= _goodsID) {
            return SpecialGoodsDict.getInstance().getSpecailGoods(_goodsID);
        }
        if (GoodsIDLimitDefine.PET_EQUIP_ARMOT_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.PET_EQUIP_ARMOT_LIMIT[1] >= _goodsID) {
            return PetEquipmentDict.getInstance().getPetArmor(_goodsID);
        }
        if (GoodsIDLimitDefine.PET_EQUIP_WEAPON_LIMIT[0] <= _goodsID && GoodsIDLimitDefine.PET_EQUIP_WEAPON_LIMIT[1] >= _goodsID) {
            return PetEquipmentDict.getInstance().getPetWeapon(_goodsID);
        }
        return null;
    }
}
