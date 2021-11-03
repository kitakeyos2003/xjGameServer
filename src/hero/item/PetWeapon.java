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

public class PetWeapon extends PetEquipment {

    private static Logger log;
    private static final Random RANDOM;
    private Weapon.EWeaponType weaponType;
    private short attackDistance;
    private int minPhysicsAttack;
    private int maxPhysicsAttack;
    private MagicDamage magicMamage;
    private ArrayList<Integer> accessorialSkillList;

    static {
        PetWeapon.log = Logger.getLogger((Class) PetWeapon.class);
        RANDOM = new Random();
    }

    public PetWeapon() {
        this.durabilityConvertRate = 40;
    }

    @Override
    public int getEquipmentType() {
        return 1;
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
        return this.minPhysicsAttack + PetWeapon.RANDOM.nextInt(this.maxPhysicsAttack - this.minPhysicsAttack + 1);
    }

    public void setMagicDamage(final EMagic _magic, final int _minValue, final int _maxValue) {
        if (_minValue < 0 || _maxValue <= 0) {
            return;
        }
        if (this.magicMamage == null) {
            this.magicMamage = new MagicDamage();
        }
        if (_magic == null) {
            PetWeapon.log.debug((Object) this.getName());
        }
        this.magicMamage.magic = _magic;
        this.magicMamage.minDamageValue = _minValue;
        this.magicMamage.maxDamageValue = _maxValue;
    }

    public MagicDamage getMagicDamage() {
        return this.magicMamage;
    }

    public Weapon.EWeaponType getWeaponType() {
        return this.weaponType;
    }

    public void setWeaponType(final String _desc) {
        Weapon.EWeaponType[] values;
        for (int length = (values = Weapon.EWeaponType.values()).length, i = 0; i < length; ++i) {
            Weapon.EWeaponType type = values[i];
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
        buff.append("\u6b66\u5668").append("\u3000").append(this.getName());
        buff.append("\n").append(this.getNeedLevel()).append("\u7ea7");
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
    public boolean canBeUse(final EVocation evocation) {
        return false;
    }

    @Override
    public short getAnimationID() {
        return -1;
    }
}
