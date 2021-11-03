// 
// Decompiled by Procyon v0.5.36
// 
package hero.item;

import hero.item.detail.EGoodsTrait;
import hero.share.service.IDManager;
import java.util.Random;
import hero.share.service.MagicDamage;
import hero.item.enhance.WeaponBloodyEnhance;
import hero.item.enhance.GenericEnhance;
import org.apache.log4j.Logger;

public class EquipmentInstance {

    private static Logger log;
    private int instanceID;
    private int creatorUserID;
    private int ownerUserID;
    private int currentDurability;
    private boolean existSeal;
    private boolean isBind;
    private GenericEnhance genericEnhance;
    private WeaponBloodyEnhance weaponBloodyEnhance;
    private EqGoods archetype;
    private int weaponMinPhysicsAttack;
    private int weaponMaxPhysicsAttack;
    private MagicDamage weaponMagicMamage;
    private short ownerType;
    private static final Random RANDOM;

    static {
        EquipmentInstance.log = Logger.getLogger((Class) EquipmentInstance.class);
        RANDOM = new Random();
    }

    public static EquipmentInstance init(final EqGoods _e, final int _creatorUserID, final int _ownerUserID) {
        try {
            if (_e instanceof Equipment) {
                EquipmentInstance.log.debug((Object) " \u521d\u59cb\u5316\u88c5\u5907\u5b9e\u4f8b\u7c7b\u578b\u4e3a\u73a9\u5bb6\u6b66\u5668");
                Equipment _equipment = (Equipment) _e;
                return new EquipmentInstance(_equipment, _creatorUserID, _ownerUserID);
            }
            if (_e instanceof PetEquipment) {
                EquipmentInstance.log.debug((Object) " \u521d\u59cb\u5316\u88c5\u5907\u5b9e\u4f8b\u7c7b\u578b\u4e3a\u5ba0\u7269\u6b66\u5668");
                PetEquipment _equipment2 = (PetEquipment) _e;
                return new EquipmentInstance(_equipment2, _creatorUserID, _ownerUserID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public EquipmentInstance(final Equipment _equipment, final int _creatorUserID, final int _ownerUserID) {
        this.instanceID = IDManager.buildEquipmentInsID();
        EquipmentInstance.log.debug((Object) (" \u6784\u9020\uff0c\u4ea7\u751f\u7684\u65b0\u73a9\u5bb6\u88c5\u5907 id = " + this.instanceID));
        this.creatorUserID = _creatorUserID;
        this.ownerUserID = _ownerUserID;
        this.archetype = _equipment;
        this.currentDurability = _equipment.getMaxDurabilityPoint() * _equipment.getDurabilityConvertRate();
        this.existSeal = _equipment.existSeal();
        this.setOwnerType((short) 1);
        if (3 == _equipment.getBindType()) {
            this.isBind = true;
        }
        if (_equipment instanceof Weapon) {
            Weapon weapon = (Weapon) _equipment;
            this.weaponMaxPhysicsAttack = weapon.getMaxPhysicsAttack();
            this.weaponMinPhysicsAttack = weapon.getMinPhysicsAttack();
            if (weapon.getMagicDamage() != null) {
                this.weaponMagicMamage = new MagicDamage();
                this.weaponMagicMamage.magic = weapon.getMagicDamage().magic;
                this.weaponMagicMamage.maxDamageValue = weapon.getMagicDamage().maxDamageValue;
                this.weaponMagicMamage.minDamageValue = weapon.getMagicDamage().minDamageValue;
            }
            if (EGoodsTrait.YU_ZHI == _equipment.getTrait() || EGoodsTrait.SHENG_QI == _equipment.getTrait()) {
                this.weaponBloodyEnhance = new WeaponBloodyEnhance(weapon.getPveEnhanceID(), weapon.getPvpEnhanceID());
            }
        }
        EquipmentInstance.log.debug((Object) (" getOwnerType= " + this.getOwnerType()));
        this.genericEnhance = new GenericEnhance(((Equipment) this.archetype).getEquipmentType());
    }

    public static EquipmentInstance init(final EqGoods _e, final int _creatorUserID, final int _ownerUserID, final int _instanceID, final int _currentDurabilityPoint, final boolean _existSeal, final boolean _isBind) {
        if (_e instanceof Equipment) {
            EquipmentInstance.log.debug((Object) "\u6839\u636e\u6570\u636e\u521b\u5efa\u88c5\u5907\u5b9e\u4f8b\u662f \u73a9\u5bb6\u88c5\u5907");
            Equipment _equipment = (Equipment) _e;
            return new EquipmentInstance(_equipment, _creatorUserID, _ownerUserID, _instanceID, _currentDurabilityPoint, _existSeal, _isBind);
        }
        if (_e instanceof PetEquipment) {
            EquipmentInstance.log.debug((Object) " \u6839\u636e\u6570\u636e\u521b\u5efa\u88c5\u5907\u5b9e\u4f8b\u662f \u5ba0\u7269\u88c5\u5907");
            PetEquipment _equipment2 = (PetEquipment) _e;
            return new EquipmentInstance(_equipment2, _creatorUserID, _ownerUserID, _instanceID, _currentDurabilityPoint, _existSeal, _isBind);
        }
        return null;
    }

    public EquipmentInstance(final Equipment _equipment, final int _creatorUserID, final int _ownerUserID, final int _instanceID, final int _currentDurabilityPoint, final boolean _existSeal, final boolean _isBind) {
        this.archetype = _equipment;
        this.creatorUserID = _creatorUserID;
        this.ownerUserID = _ownerUserID;
        this.instanceID = _instanceID;
        this.currentDurability = _currentDurabilityPoint * _equipment.getDurabilityConvertRate();
        this.existSeal = _existSeal;
        this.isBind = _isBind;
        this.setOwnerType((short) 1);
        if (_equipment instanceof Weapon) {
            Weapon weapon = (Weapon) _equipment;
            this.weaponMaxPhysicsAttack = weapon.getMaxPhysicsAttack();
            this.weaponMinPhysicsAttack = weapon.getMinPhysicsAttack();
            if (weapon.getMagicDamage() != null) {
                this.weaponMagicMamage = new MagicDamage();
                this.weaponMagicMamage.magic = weapon.getMagicDamage().magic;
                this.weaponMagicMamage.maxDamageValue = weapon.getMagicDamage().maxDamageValue;
                this.weaponMagicMamage.minDamageValue = weapon.getMagicDamage().minDamageValue;
            }
            if (EGoodsTrait.YU_ZHI == _equipment.getTrait() || EGoodsTrait.SHENG_QI == _equipment.getTrait()) {
                this.weaponBloodyEnhance = new WeaponBloodyEnhance(((Weapon) _equipment).getPveEnhanceID(), ((Weapon) _equipment).getPvpEnhanceID());
            }
        }
        this.genericEnhance = new GenericEnhance(((Equipment) this.archetype).getEquipmentType());
    }

    public EquipmentInstance(final PetEquipment _equipment, final int _creatorUserID, final int _ownerUserID) {
        this.instanceID = IDManager.buildEquipmentInsID();
        this.creatorUserID = _creatorUserID;
        this.ownerUserID = _ownerUserID;
        this.archetype = _equipment;
        this.currentDurability = _equipment.getMaxDurabilityPoint() * _equipment.getDurabilityConvertRate();
        this.setOwnerType((short) 2);
        if (_equipment instanceof PetWeapon) {
            PetWeapon weapon = (PetWeapon) _equipment;
            this.weaponMaxPhysicsAttack = weapon.getMaxPhysicsAttack();
            this.weaponMinPhysicsAttack = weapon.getMinPhysicsAttack();
            if (weapon.getMagicDamage() != null) {
                this.weaponMagicMamage = new MagicDamage();
                this.weaponMagicMamage.magic = weapon.getMagicDamage().magic;
                this.weaponMagicMamage.maxDamageValue = weapon.getMagicDamage().maxDamageValue;
                this.weaponMagicMamage.minDamageValue = weapon.getMagicDamage().minDamageValue;
            }
        }
    }

    public EquipmentInstance(final PetEquipment _equipment, final int _creatorUserID, final int _ownerUserID, final int _instanceID, final int _currentDurabilityPoint, final boolean _existSeal, final boolean _isBind) {
        this.instanceID = _instanceID;
        this.creatorUserID = _creatorUserID;
        this.ownerUserID = _ownerUserID;
        this.archetype = _equipment;
        this.currentDurability = _equipment.getMaxDurabilityPoint() * _equipment.getDurabilityConvertRate();
        this.setOwnerType((short) 2);
        if (_equipment instanceof PetWeapon) {
            PetWeapon weapon = (PetWeapon) _equipment;
            this.weaponMaxPhysicsAttack = weapon.getMaxPhysicsAttack();
            this.weaponMinPhysicsAttack = weapon.getMinPhysicsAttack();
            if (weapon.getMagicDamage() != null) {
                this.weaponMagicMamage = new MagicDamage();
                this.weaponMagicMamage.magic = weapon.getMagicDamage().magic;
                this.weaponMagicMamage.maxDamageValue = weapon.getMagicDamage().maxDamageValue;
                this.weaponMagicMamage.minDamageValue = weapon.getMagicDamage().minDamageValue;
            }
        }
    }

    public EqGoods getArchetype() {
        if (this.getOwnerType() == 1) {
            return this.archetype;
        }
        return this.archetype;
    }

    public short getOwnerType() {
        return this.ownerType;
    }

    public void setOwnerType(final short ownerType) {
        this.ownerType = ownerType;
    }

    public void changeOwnerUserID(final int _ownerUserID) {
        this.ownerUserID = _ownerUserID;
    }

    public int getCreatorUserID() {
        return this.creatorUserID;
    }

    public int getOwnerUserID() {
        return this.ownerUserID;
    }

    public int getInstanceID() {
        return this.instanceID;
    }

    public GenericEnhance getGeneralEnhance() {
        return this.genericEnhance;
    }

    public void setWeaponBloodyEnhance(final WeaponBloodyEnhance _bloodyEnhance) {
        this.weaponBloodyEnhance = _bloodyEnhance;
    }

    public WeaponBloodyEnhance getWeaponBloodyEnhance() {
        return this.weaponBloodyEnhance;
    }

    public int getCurrentDurabilityPoint() {
        int currentDurabilityPoint = this.currentDurability / this.archetype.getDurabilityConvertRate();
        if (this.currentDurability % this.archetype.getDurabilityConvertRate() > 0) {
            ++currentDurabilityPoint;
        }
        return currentDurabilityPoint;
    }

    public int reduceCurrentDurabilityPoint(final int _point) {
        if (this.currentDurability > 0) {
            this.currentDurability -= _point;
            if (this.currentDurability < 0) {
                this.currentDurability = 0;
            }
        }
        return this.currentDurability;
    }

    public int reduceCurrentDurabilityPercent(final int _percent) {
        if (this.currentDurability > 0) {
            this.currentDurability -= (int) (_percent * this.getArchetype().getMaxDurabilityPoint() * this.archetype.getDurabilityConvertRate() / 100.0f);
            if (this.currentDurability < 0) {
                this.currentDurability = 0;
            }
        }
        return this.currentDurability;
    }

    public void beRepaired() {
        this.currentDurability = this.archetype.getMaxDurabilityPoint() * this.archetype.getDurabilityConvertRate();
    }

    public void setSeal(final boolean _isBeSealed) {
        this.existSeal = _isBeSealed;
    }

    public boolean existSeal() {
        return this.existSeal;
    }

    public void bind() {
        this.isBind = true;
    }

    public boolean isBind() {
        return this.isBind;
    }

    public int getRepairCharge() {
        int currentDurabilityPoint = this.getCurrentDurabilityPoint();
        if (currentDurabilityPoint < this.archetype.getMaxDurabilityPoint()) {
            int cost = (int) (this.archetype.getSellPrice() * 1.0 * (this.archetype.getMaxDurabilityPoint() - currentDurabilityPoint) / (4 * this.archetype.getMaxDurabilityPoint()) + 0.5);
            return (cost < 1) ? 1 : cost;
        }
        return 0;
    }

    public void resetWeaponMinPhysicsAttack(final float _modulus) {
        if (this.archetype instanceof Weapon && _modulus > 0.0f) {
            Weapon weapon = (Weapon) this.archetype;
            this.weaponMinPhysicsAttack = weapon.getMinPhysicsAttack() + (int) ((weapon.getMaxPhysicsAttack() + weapon.getMinPhysicsAttack()) / 2 * _modulus);
        }
        if (this.archetype instanceof PetWeapon && _modulus > 0.0f) {
            PetWeapon weapon2 = (PetWeapon) this.archetype;
            this.weaponMinPhysicsAttack = weapon2.getMinPhysicsAttack() + (int) ((weapon2.getMaxPhysicsAttack() + weapon2.getMinPhysicsAttack()) / 2 * _modulus);
        }
    }

    public int getWeaponMinPhysicsAttack() {
        return this.weaponMinPhysicsAttack;
    }

    public void resetWeaponMaxPhysicsAttack(final float _modulus) {
        if (this.archetype instanceof Weapon && _modulus > 0.0f) {
            Weapon weapon = (Weapon) this.archetype;
            this.weaponMaxPhysicsAttack = weapon.getMaxPhysicsAttack() + (int) ((weapon.getMaxPhysicsAttack() + weapon.getMinPhysicsAttack()) / 2 * _modulus);
        }
        if (this.archetype instanceof PetWeapon && _modulus > 0.0f) {
            PetWeapon weapon2 = (PetWeapon) this.archetype;
            this.weaponMaxPhysicsAttack = weapon2.getMaxPhysicsAttack() + (int) ((weapon2.getMaxPhysicsAttack() + weapon2.getMinPhysicsAttack()) / 2 * _modulus);
        }
    }

    public int getWeaponMaxPhysicsAttack() {
        return this.weaponMaxPhysicsAttack;
    }

    public int getPhysicsAttack() {
        return this.weaponMinPhysicsAttack + EquipmentInstance.RANDOM.nextInt(this.weaponMaxPhysicsAttack - this.weaponMinPhysicsAttack + 1);
    }

    public MagicDamage getMagicDamage() {
        return this.weaponMagicMamage;
    }

    public void resetWeaponMaxMagicAttack(final float _modulus) {
        if (this.archetype instanceof Weapon && this.weaponMagicMamage != null && _modulus > 0.0f) {
            Weapon weapon = (Weapon) this.archetype;
            this.weaponMagicMamage.maxDamageValue = weapon.getMagicDamage().maxDamageValue + (int) (weapon.getMagicDamage().maxDamageValue * _modulus);
        }
        if (this.archetype instanceof PetWeapon && this.weaponMagicMamage != null && _modulus > 0.0f) {
            PetWeapon weapon2 = (PetWeapon) this.archetype;
            this.weaponMagicMamage.maxDamageValue = weapon2.getMagicDamage().maxDamageValue + (int) (weapon2.getMagicDamage().maxDamageValue * _modulus);
        }
    }

    public void resetWeaponMinMagicAttack(final float _modulus) {
        if (this.archetype instanceof Weapon && this.weaponMagicMamage != null && _modulus > 0.0f) {
            Weapon weapon = (Weapon) this.archetype;
            this.weaponMagicMamage.minDamageValue = weapon.getMagicDamage().minDamageValue + (int) (weapon.getMagicDamage().minDamageValue * _modulus);
        }
        if (this.archetype instanceof PetWeapon && this.weaponMagicMamage != null && _modulus > 0.0f) {
            PetWeapon weapon2 = (PetWeapon) this.archetype;
            this.weaponMagicMamage.minDamageValue = weapon2.getMagicDamage().minDamageValue + (int) (weapon2.getMagicDamage().minDamageValue * _modulus);
        }
    }
}
