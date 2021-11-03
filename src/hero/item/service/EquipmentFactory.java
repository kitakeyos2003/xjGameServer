// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.service;

import hero.item.dictionary.PetEquipmentDict;
import hero.item.dictionary.WeaponDict;
import hero.item.dictionary.ArmorDict;
import hero.share.service.LogWriter;
import hero.item.EquipmentInstance;
import hero.item.EqGoods;
import org.apache.log4j.Logger;

public class EquipmentFactory {

    private static Logger log;
    private static EquipmentFactory instance;

    static {
        EquipmentFactory.log = Logger.getLogger((Class) EquipmentFactory.class);
    }

    public static EquipmentFactory getInstance() {
        if (EquipmentFactory.instance == null) {
            EquipmentFactory.instance = new EquipmentFactory();
        }
        return EquipmentFactory.instance;
    }

    private EquipmentFactory() {
    }

    public EquipmentInstance build(final int _creatorUserID, final int _ownerUserID, final EqGoods _equipment) {
        EquipmentInstance ei = EquipmentInstance.init(_equipment, _creatorUserID, _ownerUserID);
        EquipmentFactory.log.debug((Object) (" 66: \u6839\u636e\u62e5\u6709\u8005userID\u548c\u88c5\u5907\u6a21\u677f\u5bf9\u8c61\u521b\u5efa\u88c5\u5907\u5b9e\u4f8b id=" + ei.getInstanceID() + " ownertype = " + ei.getOwnerType()));
        return ei;
    }

    public EquipmentInstance build(final int _creatorUserID, final int _ownerUserID, final int _equipmentID) {
        EquipmentFactory.log.debug((Object) (" \u6839\u636e\u521b\u5efa\u8005userID\u548c\u88c5\u5907\u6a21\u677f\u5bf9\u8c61\u521b\u5efa\u88c5\u5907\u5b9e\u4f8b eqid = " + _equipmentID));
        EqGoods e = this.getEquipmentArchetype(_equipmentID);
        EquipmentFactory.log.debug((Object) (" eqGoods = " + e));
        if (e != null) {
            return EquipmentInstance.init(e, _creatorUserID, _ownerUserID);
        }
        LogWriter.println("\u4e0d\u5b58\u5728\u7684\u88c5\u5907\u7f16\u53f7\uff1a" + _equipmentID);
        return null;
    }

    public EquipmentInstance buildFromDB(final int _creatorUserID, final int _ownerUserID, final int _instanceID, final int _equipmentID, final int _currentDurabilityPoint, final byte _existSeal, final byte _isBind) {
        EqGoods e = this.getEquipmentArchetype(_equipmentID);
        if (e != null) {
            EquipmentFactory.log.debug((Object) (" \u6839\u636e\u6570\u636e\u521b\u5efa\u88c5\u5907\u5b9e\u4f8b goods id = " + e.getID()));
            return EquipmentInstance.init(e, _creatorUserID, _ownerUserID, _instanceID, _currentDurabilityPoint, _existSeal == 1, _isBind == 1);
        }
        LogWriter.println("\u4e0d\u5b58\u5728\u7684\u88c5\u5907\u7f16\u53f7\uff1a" + _equipmentID);
        return null;
    }

    public EqGoods getEquipmentArchetype(final int _equipmentID) {
        if (_equipmentID >= GoodsIDLimitDefine.ARMOR_ID_LIMIT[0] && _equipmentID <= GoodsIDLimitDefine.ARMOR_ID_LIMIT[1]) {
            return ArmorDict.getInstance().getArmor(_equipmentID);
        }
        if (_equipmentID >= GoodsIDLimitDefine.WEAPON_ID_LIMIT[0] && _equipmentID <= GoodsIDLimitDefine.WEAPON_ID_LIMIT[1]) {
            return WeaponDict.getInstance().getWeapon(_equipmentID);
        }
        if (_equipmentID >= GoodsIDLimitDefine.PET_EQUIP_ARMOT_LIMIT[0] && _equipmentID <= GoodsIDLimitDefine.PET_EQUIP_ARMOT_LIMIT[1]) {
            return PetEquipmentDict.getInstance().getPetArmor(_equipmentID);
        }
        if (_equipmentID >= GoodsIDLimitDefine.PET_EQUIP_WEAPON_LIMIT[0] && _equipmentID <= GoodsIDLimitDefine.PET_EQUIP_WEAPON_LIMIT[1]) {
            return PetEquipmentDict.getInstance().getPetWeapon(_equipmentID);
        }
        return null;
    }
}
