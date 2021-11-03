// 
// Decompiled by Procyon v0.5.36
// 
package hero.item;

import hero.share.EVocation;
import hero.share.MagicFastnessList;
import hero.share.EMagic;

public class Armor extends Equipment {

    private ArmorType armorType;
    private short suiteID;
    private byte isDistinguish;

    public Armor() {
        this.durabilityConvertRate = 40;
    }

    public ArmorType getArmorType() {
        return this.armorType;
    }

    public void setArmorType(final String _desc) {
        ArmorType[] values;
        for (int length = (values = ArmorType.values()).length, i = 0; i < length; ++i) {
            ArmorType type = values[i];
            if (_desc.equals(type.getDesc())) {
                this.armorType = type;
            }
        }
    }

    public void setSuiteID(final short _suiteID) {
        this.suiteID = _suiteID;
    }

    public short getSuiteID() {
        return this.suiteID;
    }

    @Override
    public int getEquipmentType() {
        return 2;
    }

    @Override
    public void initDescription() {
        StringBuffer buff = new StringBuffer();
        buff.append(this.armorType.getDesc()).append("\u3000").append(this.getWearBodyPart().getDesc());
        buff.append("\n").append(this.getNeedLevel()).append("\u7ea7").append("\u3000\u3000").append(this.getTrait().getDesc());
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
    public boolean canBeUse(final EVocation _vocation) {
        boolean result = false;
        switch (this.armorType) {
            case BU_JIA: {
                if (EVocation.FA_SHI == _vocation || EVocation.WU_YI == _vocation || EVocation.MIAO_SHOU_WU_YI == _vocation || EVocation.LING_QUAN_WU_YI == _vocation || EVocation.XIE_JI_WU_YI == _vocation || EVocation.YIN_YANG_WU_YI == _vocation || EVocation.YU_HUO_FA_SHI == _vocation || EVocation.TIAN_JI_FA_SHI == _vocation || EVocation.YAN_MO_FA_SHI == _vocation || EVocation.XUAN_MING_FA_SHI == _vocation || EVocation.LI_SHI == _vocation || EVocation.JIN_GANG_LI_SHI == _vocation || EVocation.QING_TIAN_LI_SHI == _vocation || EVocation.LUO_CHA_LI_SHI == _vocation || EVocation.XIU_LUO_LI_SHI == _vocation || EVocation.CHI_HOU == _vocation || EVocation.LI_JIAN_CHI_HOU == _vocation || EVocation.SHEN_JIAN_CHI_HOU == _vocation || EVocation.XIE_REN_CHI_HOU == _vocation || EVocation.GUI_YI_CHI_HOU == _vocation) {
                    result = true;
                }
            }
            case QING_JIA: {
                if (EVocation.CHI_HOU == _vocation || EVocation.LI_JIAN_CHI_HOU == _vocation || EVocation.SHEN_JIAN_CHI_HOU == _vocation || EVocation.XIE_REN_CHI_HOU == _vocation || EVocation.GUI_YI_CHI_HOU == _vocation || EVocation.LI_SHI == _vocation || EVocation.JIN_GANG_LI_SHI == _vocation || EVocation.QING_TIAN_LI_SHI == _vocation || EVocation.LUO_CHA_LI_SHI == _vocation || EVocation.XIU_LUO_LI_SHI == _vocation) {
                    result = true;
                }
            }
            case ZHONG_JIA: {
                if (EVocation.LI_SHI == _vocation || EVocation.JIN_GANG_LI_SHI == _vocation || EVocation.QING_TIAN_LI_SHI == _vocation || EVocation.LUO_CHA_LI_SHI == _vocation || EVocation.XIU_LUO_LI_SHI == _vocation) {
                    result = true;
                    break;
                }
                break;
            }
        }
        result = true;
        return result;
    }

    public void setDistinguish(final byte _isDistinguish) {
        this.isDistinguish = _isDistinguish;
    }

    public byte getDistinguish() {
        return this.isDistinguish;
    }

    public enum ArmorType {
        BU_JIA("BU_JIA", 0, 1, "\u5e03\u7532"),
        QING_JIA("QING_JIA", 1, 2, "\u8f7b\u7532"),
        ZHONG_JIA("ZHONG_JIA", 2, 3, "\u91cd\u7532"),
        RING("RING", 3, 4, "\u6212\u6307"),
        NECKLACE("NECKLACE", 4, 5, "\u9879\u94fe"),
        BRACELETE("BRACELETE", 5, 6, "\u8170\u5e26");

        private int typeValue;
        private String desc;

        private ArmorType(final String name, final int ordinal, final int _typeValue, final String _desc) {
            this.typeValue = _typeValue;
            this.desc = _desc;
        }

        public int getTypeValue() {
            return this.typeValue;
        }

        public String getDesc() {
            return this.desc;
        }
    }
}
