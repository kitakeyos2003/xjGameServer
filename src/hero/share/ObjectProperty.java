// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

import hero.share.service.MagicDamage;
import hero.item.EquipmentInstance;
import hero.npc.Monster;
import hero.item.Weapon;
import hero.player.HeroPlayer;
import hero.expressions.service.CEService;
import java.util.Random;

public class ObjectProperty {

    private static Random random;
    private ME2GameObject host;
    private int maxHp;
    private int maxMp;
    private int defense;
    private int strength;
    private int agility;
    private int stamina;
    private int inte;
    private int spirit;
    private int lucky;
    private short physicsDeathblowLevel;
    private short magicDeathblowLevel;
    private short hitLevel;
    private short physicsDuckLevel;
    private float physicsDeathblowOdds;
    private float magicDeathblowOdds;
    private float physicsHitOdds;
    private float magicHitOdds;
    private float physicsDuckOdds;
    private int minPhysicsAttack;
    private int maxPhysicsAttack;
    private MagicHarmList baseMagicHarmList;
    private MagicFastnessList magicFastnessList;
    private int additionalPhysicsAttackHarmValue;
    private int additionalPhysicsAttackHarmScale;
    private int additionalHarmValueBePhysicsAttack;
    private int additionalHarmScaleBePhysicsAttack;
    private MagicHarmList additionalMagicHarmList;
    private MagicHarmList additionalMagicHarmScaleList;
    private MagicHarmList additionalMagicHarmBeAttackList;
    private MagicHarmList additionalMagicHarmScaleBeAttackList;

    static {
        ObjectProperty.random = new Random();
    }

    public ObjectProperty(final ME2GameObject _host) {
        this.host = _host;
        this.baseMagicHarmList = new MagicHarmList();
        this.magicFastnessList = new MagicFastnessList();
        this.additionalMagicHarmList = new MagicHarmList();
        this.additionalMagicHarmScaleList = new MagicHarmList();
        this.additionalMagicHarmBeAttackList = new MagicHarmList();
        this.additionalMagicHarmScaleBeAttackList = new MagicHarmList();
    }

    public void setHpMax(final int _hpMax) {
        this.maxHp = _hpMax;
    }

    public void addHpMax(final int _hpMax) {
        this.maxHp += _hpMax;
    }

    public int getHpMax() {
        if (this.maxHp < 0) {
            return 0;
        }
        return this.maxHp;
    }

    public void setMpMax(final int _mpMax) {
        this.maxMp = _mpMax;
    }

    public void addMpMax(final int _mpMax) {
        this.maxMp += _mpMax;
    }

    public int getMpMax() {
        if (this.maxMp < 0) {
            return 0;
        }
        return this.maxMp;
    }

    public void setPhysicsDeathblowLevel(final short _physicsDeathblowLevel) {
        this.physicsDeathblowLevel = _physicsDeathblowLevel;
        this.physicsDeathblowOdds = CEService.basePhysicsDeathblowOdds(this.getPhysicsDeathblowLevel(), this.host.getLevel());
    }

    public void addPhysicsDeathblowLevel(final short _physicsDeathblowLevel) {
        this.physicsDeathblowLevel += _physicsDeathblowLevel;
        this.physicsDeathblowOdds = CEService.basePhysicsDeathblowOdds(this.getPhysicsDeathblowLevel(), this.host.getLevel());
    }

    public short getPhysicsDeathblowLevel() {
        if (this.physicsDeathblowLevel < 0) {
            return 0;
        }
        return this.physicsDeathblowLevel;
    }

    public float getPhysicsDeathblowOdds() {
        if (this.physicsDeathblowOdds < 0.0f) {
            return 0.0f;
        }
        return this.physicsDeathblowOdds;
    }

    public void setMagicDeathblowLevel(final short _magicDeathblowLevel) {
        this.magicDeathblowLevel = _magicDeathblowLevel;
        this.magicDeathblowOdds = CEService.baseMagicDeathblowOdds(this.getMagicDeathblowLevel(), this.host.getLevel());
    }

    public void addMagicDeathblowLevel(final short _magicDeathblowLevel) {
        this.magicDeathblowLevel += _magicDeathblowLevel;
        this.magicDeathblowOdds = CEService.baseMagicDeathblowOdds(this.getMagicDeathblowLevel(), this.host.getLevel());
    }

    public short getMagicDeathblowLevel() {
        if (this.magicDeathblowLevel < 0) {
            return 0;
        }
        return this.magicDeathblowLevel;
    }

    public float getMagicDeathblowOdds() {
        if (this.magicDeathblowOdds < 0.0f) {
            return 0.0f;
        }
        return this.magicDeathblowOdds;
    }

    public void setMagicDeathblowOdds(final float _magicDeathblowOdds) {
        this.magicDeathblowOdds = _magicDeathblowOdds;
    }

    public void addMagicDeathblowOdds(final float _magicDeathblowOdds) {
        this.magicDeathblowOdds += _magicDeathblowOdds;
    }

    public void setHitLevel(final short _hitLevel) {
        this.hitLevel = _hitLevel;
        this.physicsHitOdds = CEService.basePhysicsHitOdds(this.getLucky(), this.getHitLevel(), this.host.getLevel());
        this.magicHitOdds = CEService.baseMagicHitOdds(this.getLucky(), this.getHitLevel(), this.host.getLevel());
    }

    public void addHitLevel(final short _hitLevel) {
        this.hitLevel += _hitLevel;
        this.physicsHitOdds = CEService.basePhysicsHitOdds(this.getLucky(), this.getHitLevel(), this.host.getLevel());
        this.magicHitOdds = CEService.baseMagicHitOdds(this.getLucky(), this.getHitLevel(), this.host.getLevel());
    }

    public short getHitLevel() {
        if (this.hitLevel < 0) {
            return 0;
        }
        return this.hitLevel;
    }

    public float getPhysicsHitOdds() {
        if (this.physicsHitOdds < 0.0f) {
            return 0.0f;
        }
        return this.physicsHitOdds;
    }

    public void setPhysicsHitOdds(final float _physicsHitOdds) {
        this.physicsHitOdds = _physicsHitOdds;
    }

    public float getMagicHitOdds() {
        if (this.magicHitOdds < 0.0f) {
            return 0.0f;
        }
        return this.magicHitOdds;
    }

    public void setMagicHitOdds(final float _magicHitOdds) {
        this.magicHitOdds = _magicHitOdds;
    }

    public void setPhysicsDuckLevel(final short _physicsDuckLevel) {
        this.physicsDuckLevel = _physicsDuckLevel;
        this.physicsDuckOdds = CEService.basePhysicsDuckOdds(this.getPhysicsDuckLevel(), this.host.getLevel());
    }

    public void addPhysicsDuckLevel(final short _physicsDuckLevel) {
        this.physicsDuckLevel += _physicsDuckLevel;
        this.physicsDuckOdds = CEService.basePhysicsDuckOdds(this.getPhysicsDuckLevel(), this.host.getLevel());
    }

    public short getPhysicsDuckLevel() {
        if (this.physicsDuckLevel < 0) {
            return 0;
        }
        return this.physicsDuckLevel;
    }

    public float getPhysicsDuckOdds() {
        if (this.physicsDuckOdds < 0.0f) {
            return 0.0f;
        }
        return this.physicsDuckOdds;
    }

    public void setPhysicsDuckOdds(final float _physicsDuckOdds) {
        this.physicsDuckOdds = _physicsDuckOdds;
    }

    public void setDefense(final int _defense) {
        this.defense = _defense;
    }

    public void addDefense(final int _defense) {
        this.defense += _defense;
    }

    public int getDefense() {
        if (this.defense < 0) {
            return 0;
        }
        return this.defense;
    }

    public void setStrength(final int _strength) {
        this.strength = _strength;
    }

    public void addStrength(final int _strength) {
        this.strength += _strength;
        EVocation vocation = this.host.getVocation();
        int weaponMinPhysicsAttack = 0;
        int weaponMaxPhysicsAttack = 0;
        int weaponMinMagicHarm = 0;
        int weaponMaxMagicHarm = 0;
        float weaponImmobilityTime = 1.0f;
        if (EObjectType.PLAYER == this.host.getObjectType()) {
            EquipmentInstance ei = ((HeroPlayer) this.host).getBodyWear().getWeapon();
            if (ei != null) {
                weaponMinPhysicsAttack = ei.getWeaponMinPhysicsAttack();
                weaponMaxPhysicsAttack = ei.getWeaponMaxPhysicsAttack();
                weaponImmobilityTime = ((Weapon) ei.getArchetype()).getImmobilityTime();
                if (ei.getMagicDamage() != null) {
                    weaponMaxMagicHarm = ei.getMagicDamage().maxDamageValue;
                    weaponMinMagicHarm = ei.getMagicDamage().minDamageValue;
                }
            }
        } else {
            weaponImmobilityTime = ((Monster) this.host).getActualAttackImmobilityTime() / 1000.0f;
            weaponMinPhysicsAttack = ((Monster) this.host).getBaseProperty().getMinPhysicsAttack();
            weaponMaxPhysicsAttack = ((Monster) this.host).getBaseProperty().getMaxPhysicsAttack();
        }
        this.maxPhysicsAttack = CEService.maxPhysicsAttack(this.getStrength(), this.getAgility(), vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), weaponMaxPhysicsAttack, weaponMaxMagicHarm, weaponImmobilityTime, this.host.getObjectLevel().getPhysicsAttckCalPara());
        this.minPhysicsAttack = CEService.minPhysicsAttack(this.getStrength(), this.getAgility(), vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), weaponMinPhysicsAttack, weaponMinMagicHarm, weaponImmobilityTime, this.host.getObjectLevel().getPhysicsAttckCalPara());
    }

    public int getStrength() {
        if (this.strength < 0) {
            return 0;
        }
        return this.strength;
    }

    public void setSpirit(final int _spirit) {
        this.spirit = _spirit;
    }

    public void addSpirit(final int _spirit) {
        this.defense -= CEService.defenseBySpirit(this.getSpirit(), this.host.getVocation().getPhysicsDefenceSpiritPara());
        this.spirit += _spirit;
        this.defense += CEService.defenseBySpirit(this.getSpirit(), this.host.getVocation().getPhysicsDefenceSpiritPara());
        if (EObjectType.PLAYER == this.host.getObjectType()) {
            ((HeroPlayer) this.host).resetHpResumeValue(CEService.hpResumeAuto(this.host.getLevel(), this.getSpirit(), this.host.getVocation().getStaminaCalPara()));
            ((HeroPlayer) this.host).resetMpResumeValue(CEService.mpResumeAuto(this.host.getLevel(), this.getSpirit(), this.host.getVocation().getInteCalcPara()));
        }
    }

    public int getSpirit() {
        if (this.spirit < 0) {
            return 0;
        }
        return this.spirit;
    }

    public void setInte(final int _inte) {
        this.inte = _inte;
    }

    public void addInte(final int _inte) {
        this.magicDeathblowLevel -= CEService.magicDeathblowLevel(this.inte, this.lucky);
        this.inte += _inte;
        this.magicDeathblowLevel += CEService.magicDeathblowLevel(this.inte, this.lucky);
        this.maxMp = CEService.mpByInte(this.inte, this.host.getLevel(), EObjectLevel.NORMAL.getMpCalPara());
        int baseMagicHarmByInte = CEService.magicHarmByInte(this.getInte());
        this.baseMagicHarmList.resetByInte(EMagic.SANCTITY, (float) baseMagicHarmByInte);
        this.baseMagicHarmList.resetByInte(EMagic.UMBRA, (float) baseMagicHarmByInte);
        this.baseMagicHarmList.resetByInte(EMagic.FIRE, (float) baseMagicHarmByInte);
        this.baseMagicHarmList.resetByInte(EMagic.WATER, (float) baseMagicHarmByInte);
        this.baseMagicHarmList.resetByInte(EMagic.SOIL, (float) baseMagicHarmByInte);
        if (EObjectType.PLAYER == this.host.getObjectType()) {
            EquipmentInstance ei = ((HeroPlayer) this.host).getBodyWear().getWeapon();
            if (ei != null) {
                MagicDamage weaponMagicDamage = ei.getMagicDamage();
                if (weaponMagicDamage != null) {
                    int magicHarmValue = (weaponMagicDamage.minDamageValue + weaponMagicDamage.maxDamageValue) / 2;
                    this.baseMagicHarmList.reset(weaponMagicDamage.magic, (float) magicHarmValue);
                }
            }
        }
    }

    public int getInte() {
        if (this.inte < 0) {
            return 0;
        }
        return this.inte;
    }

    public void setStamina(final int _stamina) {
        this.stamina = _stamina;
    }

    public void addStamina(final int _stamina) {
        this.stamina += _stamina;
        this.maxHp = CEService.hpByStamina(this.getStamina(), this.host.getLevel(), this.host.getObjectLevel().getHpCalPara());
    }

    public int getStamina() {
        if (this.stamina < 0) {
            return 0;
        }
        return this.stamina;
    }

    public void setAgility(final int _agility) {
        this.agility = _agility;
    }

    public void addAgility(final int _agility) {
        this.physicsDeathblowLevel -= CEService.physicsDeathblowLevel(this.agility, this.lucky);
        this.physicsDuckLevel -= CEService.duckLevel(this.agility, this.lucky);
        this.agility += _agility;
        this.physicsDeathblowLevel += CEService.physicsDeathblowLevel(this.agility, this.lucky);
        this.physicsDuckLevel += CEService.duckLevel(this.agility, this.lucky);
        EVocation vocation = this.host.getVocation();
        int weaponMinPhysicsAttack = 0;
        int weaponMaxPhysicsAttack = 0;
        int weaponMinMagicHarm = 0;
        int weaponMaxMagicHarm = 0;
        float weaponImmobilityTime = 1.0f;
        if (EObjectType.PLAYER == this.host.getObjectType()) {
            EquipmentInstance ei = ((HeroPlayer) this.host).getBodyWear().getWeapon();
            if (ei != null) {
                weaponMinPhysicsAttack = ei.getWeaponMinPhysicsAttack();
                weaponMaxPhysicsAttack = ei.getWeaponMaxPhysicsAttack();
                weaponImmobilityTime = ((Weapon) ei.getArchetype()).getImmobilityTime();
                if (ei.getMagicDamage() != null) {
                    weaponMaxMagicHarm = ei.getMagicDamage().maxDamageValue;
                    weaponMinMagicHarm = ei.getMagicDamage().minDamageValue;
                }
            }
        } else {
            weaponImmobilityTime = ((Monster) this.host).getActualAttackImmobilityTime() / 1000.0f;
            weaponMinPhysicsAttack = ((Monster) this.host).getBaseProperty().getMinPhysicsAttack();
            weaponMaxPhysicsAttack = ((Monster) this.host).getBaseProperty().getMaxPhysicsAttack();
        }
        this.maxPhysicsAttack = CEService.maxPhysicsAttack(this.getStrength(), this.getAgility(), vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), weaponMaxPhysicsAttack, weaponMaxMagicHarm, weaponImmobilityTime, this.host.getObjectLevel().getPhysicsAttckCalPara());
        this.minPhysicsAttack = CEService.minPhysicsAttack(this.getStrength(), this.getAgility(), vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), weaponMinPhysicsAttack, weaponMinMagicHarm, weaponImmobilityTime, this.host.getObjectLevel().getPhysicsAttckCalPara());
        this.physicsDuckOdds = CEService.basePhysicsDuckOdds(this.getPhysicsDuckLevel(), this.host.getLevel());
        this.physicsDeathblowOdds = CEService.basePhysicsDeathblowOdds(this.getPhysicsDeathblowLevel(), this.host.getLevel());
    }

    public int getAgility() {
        if (this.agility < 0) {
            return 0;
        }
        return this.agility;
    }

    public int getLucky() {
        if (this.lucky < 0) {
            return 0;
        }
        return this.lucky;
    }

    public void setLucky(final int _lucky) {
        this.lucky = _lucky;
    }

    public void addLucky(final int _lucky) {
        this.physicsDeathblowLevel -= CEService.physicsDeathblowLevel(this.agility, this.lucky);
        this.magicDeathblowLevel -= CEService.magicDeathblowLevel(this.inte, this.lucky);
        this.hitLevel -= CEService.hitLevel(this.lucky);
        this.physicsDuckLevel -= CEService.duckLevel(this.agility, this.lucky);
        this.lucky += _lucky;
        this.physicsDeathblowLevel += CEService.physicsDeathblowLevel(this.agility, this.lucky);
        this.magicDeathblowLevel += CEService.magicDeathblowLevel(this.inte, this.lucky);
        this.hitLevel += CEService.hitLevel(this.lucky);
        this.physicsDuckLevel += CEService.duckLevel(this.agility, this.lucky);
        this.physicsDuckOdds = CEService.basePhysicsDuckOdds(this.getPhysicsDuckLevel(), this.host.getLevel());
        this.physicsHitOdds = CEService.basePhysicsHitOdds(this.getLucky(), this.getHitLevel(), this.host.getLevel());
        this.magicHitOdds = CEService.baseMagicHitOdds(this.getLucky(), this.getHitLevel(), this.host.getLevel());
        this.magicDeathblowOdds = CEService.baseMagicDeathblowOdds(this.getMagicDeathblowLevel(), this.host.getLevel());
        this.physicsDuckOdds = CEService.basePhysicsDuckOdds(this.getPhysicsDuckLevel(), this.host.getLevel());
    }

    public MagicFastnessList getMagicFastnessList() {
        return this.magicFastnessList;
    }

    public MagicHarmList getBaseMagicHarmList() {
        return this.baseMagicHarmList;
    }

    public int getActualPhysicsAttack() {
        return this.minPhysicsAttack + ObjectProperty.random.nextInt(this.maxPhysicsAttack - this.minPhysicsAttack + 1);
    }

    public void setMaxPhysicsAttack(final int _maxPhysicsAttack) {
        this.maxPhysicsAttack = _maxPhysicsAttack;
    }

    public int getMaxPhysicsAttack() {
        if (this.maxPhysicsAttack < 0) {
            return 0;
        }
        return this.maxPhysicsAttack;
    }

    public void setMinPhysicsAttack(final int _minPhysicsAttack) {
        this.minPhysicsAttack = _minPhysicsAttack;
    }

    public int getMinPhysicsAttack() {
        if (this.minPhysicsAttack < 0) {
            return 0;
        }
        return this.minPhysicsAttack;
    }

    public int getAdditionalPhysicsAttackHarmValue() {
        return this.additionalPhysicsAttackHarmValue;
    }

    public int addAdditionalPhysicsAttackHarmValue(final int _value) {
        return this.additionalPhysicsAttackHarmValue += _value;
    }

    public float getAdditionalPhysicsAttackHarmScale() {
        return (float) this.additionalPhysicsAttackHarmScale;
    }

    public float addAdditionalPhysicsAttackHarmScale(final float _scale) {
        int additionalPhysicsAttackHarmScale = (int) (this.additionalPhysicsAttackHarmScale + _scale);
        this.additionalPhysicsAttackHarmScale = additionalPhysicsAttackHarmScale;
        return (float) additionalPhysicsAttackHarmScale;
    }

    public int getAdditionalHarmValueBePhysicsAttack() {
        return this.additionalHarmValueBePhysicsAttack;
    }

    public int addAdditionalHarmValueBePhysicsAttack(final int _value) {
        return this.additionalHarmValueBePhysicsAttack += _value;
    }

    public float getAdditionalHarmScaleBePhysicsAttack() {
        return (float) this.additionalHarmScaleBePhysicsAttack;
    }

    public float addAdditionalHarmScaleBePhysicsAttack(final float _scale) {
        int additionalHarmScaleBePhysicsAttack = (int) (this.additionalHarmScaleBePhysicsAttack + _scale);
        this.additionalHarmScaleBePhysicsAttack = additionalHarmScaleBePhysicsAttack;
        return (float) additionalHarmScaleBePhysicsAttack;
    }

    public float getAdditionalMagicHarm(final EMagic _magic) {
        return this.additionalMagicHarmList.getEMagicHarmValue(_magic);
    }

    public float addAdditionalMagicHarm(final EMagic _magic, final float _value) {
        return this.additionalMagicHarmList.add(_magic, _value);
    }

    public void addAdditionalMagicHarm(final MagicHarmList _magicHarmList) {
        this.additionalMagicHarmList.add(_magicHarmList);
    }

    public float getAdditionalMagicHarmScale(final EMagic _magic) {
        return this.additionalMagicHarmScaleList.getEMagicHarmValue(_magic);
    }

    public float addAdditionalMagicHarmScale(final EMagic _magic, final float _scale) {
        return this.additionalMagicHarmScaleList.add(_magic, _scale);
    }

    public void addAdditionalMagicHarmScale(final MagicHarmList _magicHarmList) {
        this.additionalMagicHarmScaleList.add(_magicHarmList);
    }

    public float getAdditionalMagicHarmBeAttack(final EMagic _magic) {
        return this.additionalMagicHarmBeAttackList.getEMagicHarmValue(_magic);
    }

    public float addAdditionalMagicHarmBeAttack(final EMagic _magic, final float _value) {
        return this.additionalMagicHarmBeAttackList.add(_magic, _value);
    }

    public void addAdditionalMagicHarmBeAttack(final MagicHarmList _magicHarmList) {
        this.additionalMagicHarmBeAttackList.add(_magicHarmList);
    }

    public float getAdditionalMagicHarmScaleBeAttack(final EMagic _magic) {
        return this.additionalMagicHarmScaleBeAttackList.getEMagicHarmValue(_magic);
    }

    public float addAdditionalMagicHarmScaleBeAttack(final EMagic _magic, final float _value) {
        return this.additionalMagicHarmScaleBeAttackList.add(_magic, _value);
    }

    public void addAdditionalMagicHarmScaleBeAttack(final MagicHarmList _magicHarmList) {
        this.additionalMagicHarmScaleBeAttackList.add(_magicHarmList);
    }

    public void clearNoneBaseProperty() {
        this.additionalPhysicsAttackHarmValue = 0;
        this.additionalPhysicsAttackHarmScale = 0;
        this.additionalHarmValueBePhysicsAttack = 0;
        this.additionalHarmScaleBePhysicsAttack = 0;
        this.additionalMagicHarmList.clear();
        this.additionalMagicHarmScaleList.clear();
        this.additionalMagicHarmBeAttackList.clear();
        this.additionalMagicHarmScaleBeAttackList.clear();
    }
}
