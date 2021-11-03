// 
// Decompiled by Procyon v0.5.36
// 
package hero.item;

import hero.share.EVocation;
import hero.share.MagicFastnessList;
import hero.share.EMagic;

public class PetArmor extends PetEquipment {

    public PetArmor() {
        this.durabilityConvertRate = 40;
    }

    @Override
    public int getEquipmentType() {
        return 2;
    }

    @Override
    public void initDescription() {
        StringBuffer buff = new StringBuffer();
        buff.append("\u3000").append(this.getWearBodyPart().getDesc());
        buff.append("\n").append(this.getNeedLevel()).append("\u7ea7");
        if (this.bindType == 2) {
            buff.append("\n").append("\u88c5\u5907\u540e\u7ed1\u5b9a");
        } else if (this.bindType == 3) {
            buff.append("\n").append("\u62fe\u53d6\u540e\u7ed1\u5b9a");
        }
        if (this.atribute.stamina > 0) {
            buff.append("\n").append("+").append(this.atribute.stamina).append(" \u8010\u529b");
        }
        if (this.atribute.inte > 0) {
            buff.append("\n").append("+").append(this.atribute.inte).append(" \u667a\u529b");
        }
        if (this.atribute.strength > 0) {
            buff.append("\n").append("+").append(this.atribute.strength).append(" \u529b\u91cf");
        }
        if (this.atribute.spirit > 0) {
            buff.append("\n").append("+").append(this.atribute.spirit).append(" \u7cbe\u795e");
        }
        if (this.atribute.agility > 0) {
            buff.append("\n").append("+").append(this.atribute.agility).append(" \u654f\u6377");
        }
        if (this.atribute.lucky > 0) {
            buff.append("\n").append("+").append(this.atribute.lucky).append(" \u5e78\u8fd0");
        }
        if (this.atribute.hp > 0) {
            buff.append("\n").append("+").append(this.atribute.hp).append(" \u751f\u547d");
        }
        if (this.atribute.mp > 0) {
            buff.append("\n").append("+").append(this.atribute.mp).append(" \u9b54\u6cd5");
        }
        if (this.atribute.physicsDeathblowLevel > 0) {
            buff.append("\n").append("+").append(this.atribute.physicsDeathblowLevel).append(" \u7269\u7406\u7206\u51fb\u7b49\u7ea7");
        }
        if (this.atribute.magicDeathblowLevel > 0) {
            buff.append("\n").append("+").append(this.atribute.magicDeathblowLevel).append(" \u9b54\u6cd5\u7206\u51fb\u7b49\u7ea7");
        }
        if (this.atribute.hitLevel > 0) {
            buff.append("\n").append("+").append(this.atribute.hitLevel).append(" \u547d\u4e2d\u7b49\u7ea7");
        }
        if (this.atribute.duckLevel > 0) {
            buff.append("\n").append("+").append(this.atribute.duckLevel).append(" \u95ea\u907f\u7b49\u7ea7");
        }
        if (this.atribute.defense > 0) {
            buff.append("\n").append("\u9632\u5fa1").append("\u3000\u3000").append("\uff1a").append(this.atribute.defense);
        }
        MagicFastnessList mfl = this.getMagicFastnessList();
        if (mfl != null) {
            int value = mfl.getEMagicFastnessValue(EMagic.SANCTITY);
            if (value > 0) {
                buff.append("\n").append("+").append(value).append(" ").append(EMagic.SANCTITY.getName()).append("\u6297\u6027");
            }
            value = mfl.getEMagicFastnessValue(EMagic.UMBRA);
            if (value > 0) {
                buff.append("\n").append("+").append(value).append(" ").append(EMagic.UMBRA.getName()).append("\u6297\u6027");
            }
            value = mfl.getEMagicFastnessValue(EMagic.FIRE);
            if (value > 0) {
                buff.append("\n").append("+").append(value).append(" ").append(EMagic.FIRE.getName()).append("\u6297\u6027");
            }
            value = mfl.getEMagicFastnessValue(EMagic.WATER);
            if (value > 0) {
                buff.append("\n").append("+").append(value).append(" ").append(EMagic.WATER.getName()).append("\u6297\u6027");
            }
            value = mfl.getEMagicFastnessValue(EMagic.SOIL);
            if (value > 0) {
                buff.append("\n").append("+").append(value).append(" ").append(EMagic.SOIL.getName()).append("\u6297\u6027");
            }
        }
        this.description = buff.toString();
    }

    @Override
    public boolean canBeUse(final EVocation evocation) {
        return false;
    }

    @Override
    public short getAnimationID() {
        return -1;
    }
}
