// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.bag;

import hero.item.Armor;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.EquipmentInstance;

public class BodyWear extends EquipmentContainer {

    private static final short BODY_BAG_GRID_NUMBER = 8;

    public BodyWear() {
        super((short) 8);
    }

    public EquipmentInstance wear(final EquipmentInstance _equipmentInstance) {
        if (_equipmentInstance != null && _equipmentInstance.getOwnerType() == 1) {
            int bodyPart = _equipmentInstance.getArchetype().getWearBodyPart().value();
            EquipmentInstance equipmentIns = this.equipmentList[bodyPart];
            this.equipmentList[bodyPart] = _equipmentInstance;
            if (equipmentIns == null) {
                --this.emptyGridNumber;
            }
            return equipmentIns;
        }
        return null;
    }

    @Override
    public byte getContainerType() {
        return 2;
    }

    public EquipmentInstance getBosom() {
        return this.equipmentList[EBodyPartOfEquipment.BOSOM.value()];
    }

    public EquipmentInstance getHead() {
        return this.equipmentList[EBodyPartOfEquipment.HEAD.value()];
    }

    public EquipmentInstance getWeapon() {
        return this.equipmentList[EBodyPartOfEquipment.WEAPON.value()];
    }

    public int getSuiteLevel() {
        int level = 0;
        return level;
    }

    public short hasSuite() {
        if (this.equipmentList[EBodyPartOfEquipment.HEAD.value()] != null && this.equipmentList[EBodyPartOfEquipment.BOSOM.value()] != null && this.equipmentList[EBodyPartOfEquipment.HAND.value()] != null && this.equipmentList[EBodyPartOfEquipment.FOOT.value()] != null) {
            short suiteID = ((Armor) this.equipmentList[EBodyPartOfEquipment.HEAD.value()].getArchetype()).getSuiteID();
            if (suiteID != 0 && suiteID == ((Armor) this.equipmentList[EBodyPartOfEquipment.BOSOM.value()].getArchetype()).getSuiteID() && suiteID != 0 && suiteID == ((Armor) this.equipmentList[EBodyPartOfEquipment.HAND.value()].getArchetype()).getSuiteID() && suiteID != 0 && suiteID == ((Armor) this.equipmentList[EBodyPartOfEquipment.HAND.value()].getArchetype()).getSuiteID()) {
                return suiteID;
            }
        }
        return 0;
    }
}
