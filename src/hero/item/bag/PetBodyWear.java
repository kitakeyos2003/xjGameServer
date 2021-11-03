// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.bag;

import hero.item.detail.EPetBodyPartOfEquipment;
import hero.item.PetEquipment;
import hero.item.EquipmentInstance;

public class PetBodyWear extends EquipmentContainer {

    private static final short BODY_BAG_GRID_NUMBER = 4;

    public PetBodyWear() {
        super((short) 4);
    }

    @Override
    public byte getContainerType() {
        return 4;
    }

    public EquipmentInstance wear(final EquipmentInstance _equipmentInstance) {
        if (_equipmentInstance != null && _equipmentInstance.getOwnerType() == 2) {
            int bodyPart = ((PetEquipment) _equipmentInstance.getArchetype()).getWearBodyPart().value();
            EquipmentInstance equipmentIns = this.equipmentList[bodyPart];
            this.equipmentList[bodyPart] = _equipmentInstance;
            if (equipmentIns == null) {
                --this.emptyGridNumber;
            }
            return equipmentIns;
        }
        return null;
    }

    public EquipmentInstance getPetEqWeapon() {
        return this.equipmentList[EPetBodyPartOfEquipment.CLAW.value()];
    }

    public EquipmentInstance getPetEqHead() {
        return this.equipmentList[EPetBodyPartOfEquipment.HEAD.value()];
    }

    public EquipmentInstance getPetEqBody() {
        return this.equipmentList[EPetBodyPartOfEquipment.BODY.value()];
    }

    public EquipmentInstance getPetEqTail() {
        return this.equipmentList[EPetBodyPartOfEquipment.TAIL.value()];
    }
}
