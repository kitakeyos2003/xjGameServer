// 
// Decompiled by Procyon v0.5.36
// 
package hero.item;

import hero.share.EVocation;
import hero.share.MagicFastnessList;
import hero.share.AccessorialOriginalAttribute;
import hero.share.EMagic;
import java.util.ArrayList;
import hero.share.service.MagicDamage;
import java.util.Random;
import org.apache.log4j.Logger;

public class Weapon extends Equipment {

    private static Logger log;
    private static final Random RANDOM;
    private EWeaponType weaponType;
    private short attackDistance;
    private int minPhysicsAttack;
    private int maxPhysicsAttack;
    private MagicDamage magicMamage;
    private ArrayList<Integer> accessorialSkillList;
    private int pveEnhanceID;
    private int pvpEnhanceID;
    private short weaponLight;
    private short lightAnimation;

    static {
        Weapon.log = Logger.getLogger((Class) Weapon.class);
        RANDOM = new Random();
    }

    public Weapon() {
        this.durabilityConvertRate = 60;
    }

    public short getLightAnimation() {
        return this.lightAnimation;
    }

    public void setLightAnimation(final short _lightAnimation) {
        this.lightAnimation = _lightAnimation;
    }

    public short getLightID() {
        return this.weaponLight;
    }

    public void setLightID(final short _lightID) {
        this.weaponLight = _lightID;
    }

    public int getPveEnhanceID() {
        return this.pveEnhanceID;
    }

    public void setPveEnhanceID(final int _enhanceID) {
        this.pveEnhanceID = _enhanceID;
    }

    public int getPvpEnhanceID() {
        return this.pvpEnhanceID;
    }

    public void setPvpEnhanceID(final int _enhanceID) {
        this.pvpEnhanceID = _enhanceID;
    }

    public void setMinPhysicsAttack(final int _minPhysicsAttack) {
        this.minPhysicsAttack = _minPhysicsAttack;
    }

    public int getMinPhysicsAttack() {
        return this.minPhysicsAttack;
    }

    public void setMaxPhysicsAttack(final int _maxPhysicsAttack) {
        this.maxPhysicsAttack = _maxPhysicsAttack;
    }

    public int getMaxPhysicsAttack() {
        return this.maxPhysicsAttack;
    }

    public int getPhysicsAttack() {
        return this.minPhysicsAttack + Weapon.RANDOM.nextInt(this.maxPhysicsAttack - this.minPhysicsAttack + 1);
    }

    public void setMagicDamage(final EMagic _magic, final int _minValue, final int _maxValue) {
        if (_minValue < 0 || _maxValue <= 0) {
            return;
        }
        if (this.magicMamage == null) {
            this.magicMamage = new MagicDamage();
        }
        if (_magic == null) {
            Weapon.log.info((Object) this.getName());
        }
        this.magicMamage.magic = _magic;
        this.magicMamage.minDamageValue = _minValue;
        this.magicMamage.maxDamageValue = _maxValue;
    }

    public MagicDamage getMagicDamage() {
        return this.magicMamage;
    }

    @Override
    public int getEquipmentType() {
        return 1;
    }

    public EWeaponType getWeaponType() {
        return this.weaponType;
    }

    public void setWeaponType(final String _desc) {
        EWeaponType[] values;
        for (int length = (values = EWeaponType.values()).length, i = 0; i < length; ++i) {
            EWeaponType type = values[i];
            if (_desc.equals(type.getDesc())) {
                this.weaponType = type;
            }
        }
    }

    public short getAttackDistance() {
        return this.attackDistance;
    }

    public void setAttackDistance(final short _distance) {
        this.attackDistance = _distance;
        if (_distance < 2) {
            this.attackDistance = 2;
        }
    }

    public ArrayList<Integer> getAccessorialSkillList() {
        return this.accessorialSkillList;
    }

    public void addAccessorialSkill(final int _skillID) {
        if (_skillID <= 0) {
            return;
        }
        if (this.accessorialSkillList == null) {
            this.accessorialSkillList = new ArrayList<Integer>();
        }
        this.accessorialSkillList.add(_skillID);
    }

    @Override
    public void initDescription() {
        StringBuffer buff = new StringBuffer();
        buff.append("\u6b66\u5668").append("\u3000").append(this.getWeaponType().getDesc());
        buff.append("\n").append(this.getNeedLevel()).append("\u7ea7").append("\u3000\u3000").append(this.getTrait().getDesc());
        if (this.bindType == 2) {
            buff.append("\n").append("\u88c5\u5907\u540e\u7ed1\u5b9a");
        } else if (this.bindType == 3) {
            buff.append("\n").append("\u62fe\u53d6\u540e\u7ed1\u5b9a");
        }
        AccessorialOriginalAttribute atr = this.atribute;
        if (atr.stamina > 0) {
            buff.append("\n").append("+").append(atr.stamina).append(" \u8010\u529b");
        }
        if (atr.inte > 0) {
            buff.append("\n").append("+").append(atr.inte).append(" \u667a\u529b");
        }
        if (atr.strength > 0) {
            buff.append("\n").append("+").append(atr.strength).append(" \u529b\u91cf");
        }
        if (atr.spirit > 0) {
            buff.append("\n").append("+").append(atr.spirit).append(" \u7cbe\u795e");
        }
        if (atr.agility > 0) {
            buff.append("\n").append("+").append(atr.agility).append(" \u654f\u6377");
        }
        if (atr.lucky > 0) {
            buff.append("\n").append("+").append(atr.lucky).append(" \u5e78\u8fd0");
        }
        if (atr.hp > 0) {
            buff.append("\n").append("+").append(atr.hp).append(" \u751f\u547d");
        }
        if (atr.mp > 0) {
            buff.append("\n").append("+").append(atr.mp).append(" \u9b54\u6cd5");
        }
        if (atr.physicsDeathblowLevel > 0) {
            buff.append("\n").append("+").append(atr.physicsDeathblowLevel).append(" \u7269\u7406\u7206\u51fb\u7b49\u7ea7");
        }
        if (atr.magicDeathblowLevel > 0) {
            buff.append("\n").append("+").append(atr.magicDeathblowLevel).append(" \u9b54\u6cd5\u7206\u51fb\u7b49\u7ea7");
        }
        if (atr.hitLevel > 0) {
            buff.append("\n").append("+").append(atr.hitLevel).append(" \u547d\u4e2d\u7b49\u7ea7");
        }
        if (atr.duckLevel > 0) {
            buff.append("\n").append("+").append(atr.duckLevel).append(" \u95ea\u907f\u7b49\u7ea7");
        }
        buff.append("\n").append("\u653b\u51fb\u529b").append("\u3000").append("\uff1a").append(this.getMinPhysicsAttack()).append("\uff0d").append(this.getMaxPhysicsAttack());
        if (this.magicMamage != null) {
            buff.append("\n").append(this.magicMamage.magic.getName()).append("\u9b54\u6cd5\u4f24\u5bb3\uff1a").append(this.magicMamage.minDamageValue).append("-").append(this.magicMamage.maxDamageValue);
        }
        if (atr.defense > 0) {
            buff.append("\n").append("\u9632\u5fa1").append("\u3000\u3000").append("\uff1a").append(atr.defense);
        }
        buff.append("\n").append("\u653b\u51fb\u95f4\u9694\uff1a").append(this.getImmobilityTime());
        buff.append("\n").append("\u653b\u51fb\u8ddd\u79bb\uff1a").append(this.getAttackDistance());
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
        switch (this.weaponType) {
            case TYPE_BISHOU: {
                return EVocation.CHI_HOU == _vocation || EVocation.GUI_YI_CHI_HOU == _vocation || EVocation.XIE_REN_CHI_HOU == _vocation || EVocation.SHEN_JIAN_CHI_HOU == _vocation || EVocation.LI_JIAN_CHI_HOU == _vocation;
            }
            case TYPE_CHUI: {
                return EVocation.LI_SHI == _vocation || EVocation.JIN_GANG_LI_SHI == _vocation || EVocation.QING_TIAN_LI_SHI == _vocation || EVocation.XIU_LUO_LI_SHI == _vocation || EVocation.LUO_CHA_LI_SHI == _vocation || EVocation.WU_YI == _vocation || EVocation.MIAO_SHOU_WU_YI == _vocation || EVocation.LING_QUAN_WU_YI == _vocation || EVocation.XIE_JI_WU_YI == _vocation || EVocation.YIN_YANG_WU_YI == _vocation;
            }
            case TTYPE_ZHANG: {
                return EVocation.FA_SHI == _vocation || EVocation.YU_HUO_FA_SHI == _vocation || EVocation.TIAN_JI_FA_SHI == _vocation || EVocation.YAN_MO_FA_SHI == _vocation || EVocation.XUAN_MING_FA_SHI == _vocation || EVocation.WU_YI == _vocation || EVocation.MIAO_SHOU_WU_YI == _vocation || EVocation.LING_QUAN_WU_YI == _vocation || EVocation.XIE_JI_WU_YI == _vocation || EVocation.YIN_YANG_WU_YI == _vocation;
            }
            case TYPE_GONG: {
                return EVocation.CHI_HOU == _vocation || EVocation.GUI_YI_CHI_HOU == _vocation || EVocation.XIE_REN_CHI_HOU == _vocation || EVocation.SHEN_JIAN_CHI_HOU == _vocation || EVocation.LI_JIAN_CHI_HOU == _vocation;
            }
            case TYPE_JIAN: {
                return EVocation.LI_SHI == _vocation || EVocation.JIN_GANG_LI_SHI == _vocation || EVocation.QING_TIAN_LI_SHI == _vocation || EVocation.LUO_CHA_LI_SHI == _vocation || EVocation.XIU_LUO_LI_SHI == _vocation || EVocation.FA_SHI == _vocation || EVocation.YU_HUO_FA_SHI == _vocation || EVocation.TIAN_JI_FA_SHI == _vocation || EVocation.YAN_MO_FA_SHI == _vocation || EVocation.XUAN_MING_FA_SHI == _vocation;
            }
            default: {
                return true;
            }
        }
    }

    public enum EWeaponType {
        TYPE_BISHOU("TYPE_BISHOU", 0, "\u5315\u9996", 601),
        TYPE_CHUI("TYPE_CHUI", 1, "\u9524", 602),
        TTYPE_ZHANG("TTYPE_ZHANG", 2, "\u6756", 603),
        TYPE_GONG("TYPE_GONG", 3, "\u5f13", 604),
        TYPE_JIAN("TYPE_JIAN", 4, "\u5251", 605),
        TYPE_SHU("TYPE_SHU", 5, "\u5377\u8f74", 606);

        private int id;
        private String description;

        private EWeaponType(final String name, final int ordinal, final String _desc, final int _id) {
            this.description = _desc;
            this.id = _id;
        }

        public String getDesc() {
            return this.description;
        }

        public int getID() {
            return this.id;
        }

        public static EWeaponType getType(final String _desc) {
            EWeaponType[] values;
            for (int length = (values = values()).length, i = 0; i < length; ++i) {
                EWeaponType type = values[i];
                if (type.description.equals(_desc)) {
                    return type;
                }
            }
            return null;
        }

        public static ArrayList<EWeaponType> getTypes(final String[] _desc) {
            ArrayList<EWeaponType> types = new ArrayList<EWeaponType>();
            EWeaponType[] values;
            for (int length = (values = values()).length, j = 0; j < length; ++j) {
                EWeaponType type = values[j];
                for (int i = 0; i < _desc.length; ++i) {
                    if (type.description.equals(_desc[i])) {
                        types.add(type);
                    }
                }
            }
            return types;
        }
    }
}
