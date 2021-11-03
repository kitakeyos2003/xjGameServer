// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.service;

import hero.skill.unit.SkillUnit;
import hero.effect.detail.TouchEffect;
import hero.effect.detail.DynamicEffect;
import hero.skill.unit.TouchUnit;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.player.message.ShortcutKeyListNotify;
import hero.novice.service.NoviceServiceImpl;
import hero.item.dictionary.GoodsContents;
import hero.item.special.RinseSkill;
import hero.skill.message.SkillUpgradeNotify;
import hero.share.message.RefreshObjectViewValue;
import hero.skill.message.LearnedSkillListNotify;
import hero.skill.detail.ESkillType;
import hero.effect.Effect;
import hero.item.Weapon;
import hero.npc.dict.MonsterImageConfDict;
import hero.duel.service.DuelServiceImpl;
import hero.share.message.Warning;
import hero.skill.unit.PassiveSkillUnit;
import hero.effect.detail.StaticEffect;
import hero.skill.detail.AdditionalActionUnit;
import hero.item.EquipmentInstance;
import hero.share.Constant;
import hero.npc.Monster;
import hero.skill.detail.ETouchType;
import hero.effect.service.EffectServiceImpl;
import hero.expressions.service.CEService;
import hero.fight.service.FightServiceImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.skill.message.SkillAnimationNotify;
import hero.map.service.MapServiceImpl;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.PetActiveSkill;
import hero.skill.message.ReviveConfirm;
import hero.skill.detail.EActiveSkillType;
import hero.skill.detail.ETargetRangeType;
import hero.share.ME2GameObject;
import hero.skill.detail.ETargetType;
import hero.skill.PetPassiveSkill;
import hero.pet.Pet;
import hero.skill.message.UpdateActiveSkillNotify;
import yoyo.core.packet.AbsResponseMessage;
import hero.skill.message.RefreshSkillTime;
import yoyo.core.queue.ResponseMessageQueue;
import hero.share.ResistOddsList;
import hero.skill.detail.ESpecialStatus;
import hero.skill.unit.ActiveSkillUnit;
import hero.share.EObjectType;
import hero.share.EMagic;
import hero.share.MagicHarmList;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.unit.ChangePropertyUnit;
import hero.skill.Skill;
import java.util.Iterator;
import hero.skill.unit.EnhanceSkillUnit;
import hero.skill.PassiveSkill;
import hero.skill.ActiveSkill;
import java.util.ArrayList;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import yoyo.service.base.session.Session;
import hero.skill.dict.MonsterSkillDict;
import hero.skill.dict.SkillDict;
import hero.skill.dict.SkillUnitDict;
import java.util.Random;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class SkillServiceImpl extends AbsServiceAdaptor<SkillConfig> {

    private static Logger log;
    private static SkillServiceImpl instance;
    private static final Random RANDOM;
    public static final int VALIDATE_CD_TIME = 300;

    static {
        SkillServiceImpl.log = Logger.getLogger((Class) SkillServiceImpl.class);
        RANDOM = new Random();
    }

    private SkillServiceImpl() {
        this.config = new SkillConfig();
    }

    public static SkillServiceImpl getInstance() {
        if (SkillServiceImpl.instance == null) {
            SkillServiceImpl.instance = new SkillServiceImpl();
        }
        return SkillServiceImpl.instance;
    }

    @Override
    protected void start() {
        SkillUnitDict.getInstance().load((SkillConfig) this.config);
        SkillDict.getInstance().load((SkillConfig) this.config);
        MonsterSkillDict.getInstance().load((SkillConfig) this.config);
    }

    @Override
    public void createSession(final Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
        if (player != null) {
            this.initSkillList(player, SkillDAO.loadPlayerSkill(_session.userID));
        }
    }

    @Override
    public void sessionFree(final Session _session) {
    }

    @Override
    public void clean(final int _userID) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
        if (player != null) {
            SkillDAO.updateSkillTraceCD(player);
        }
    }

    private void initSkillList(final HeroPlayer _player, final ArrayList<int[]> _skillInfoList) {
        _player.activeSkillList.clear();
        _player.activeSkillTable.clear();
        _player.passiveSkillList.clear();
        for (final int[] skillInfo : _skillInfoList) {
            Skill skill = getInstance().getSkillIns(skillInfo[0]);
            if (skill != null) {
                if (skill instanceof ActiveSkill) {
                    ((ActiveSkill) skill).reduceCoolDownTime = skillInfo[1];
                    _player.activeSkillList.add((ActiveSkill) skill);
                    _player.activeSkillTable.put(skill.id, (ActiveSkill) skill);
                } else {
                    _player.passiveSkillList.add((PassiveSkill) skill);
                }
            }
        }
        for (final PassiveSkill passiveSkill : _player.passiveSkillList) {
            if (passiveSkill.skillUnit instanceof EnhanceSkillUnit) {
                this.enhanceSkill((EnhanceSkillUnit) passiveSkill.skillUnit, _player, true, false);
            }
        }
    }

    private void enhanceSkill(final EnhanceSkillUnit _enhanceSkillPassiveSkill, final HeroPlayer _player, final boolean _isBuild, final boolean _needNotifyClient) {
        boolean enhanced = false;
        EnhanceSkillUnit.EnhanceUnit[] enhanceUnitList;
        for (int length = (enhanceUnitList = _enhanceSkillPassiveSkill.enhanceUnitList).length, i = 0; i < length; ++i) {
            EnhanceSkillUnit.EnhanceUnit enhanceUnit = enhanceUnitList[i];
            for (final ActiveSkill activeSkill : _player.activeSkillList) {
                if (this.unitEnhance(enhanceUnit, activeSkill, _isBuild, _needNotifyClient, _player)) {
                    enhanced = true;
                    break;
                }
            }
            if (!enhanced) {
                for (final PassiveSkill passiveSkill : _player.passiveSkillList) {
                    if (this.unitEnhance(enhanceUnit, passiveSkill, _isBuild, _needNotifyClient, _player)) {
                        break;
                    }
                }
            }
        }
    }

    private void enhanceProperty(final ChangePropertyUnit _propertyPassiveSkill, final HeroPlayer _host, final boolean _isInit) {
        float value = 0.0f;
        EMathCaluOperator operator = _propertyPassiveSkill.caluOperator;
        if (_propertyPassiveSkill.strength > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getStrength(), _propertyPassiveSkill.strength, operator);
            _host.getBaseProperty().addStrength((int) value);
        }
        if (_propertyPassiveSkill.agility > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getAgility(), _propertyPassiveSkill.agility, operator);
            _host.getBaseProperty().addAgility((int) value);
        }
        if (_propertyPassiveSkill.stamina > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getStamina(), _propertyPassiveSkill.stamina, operator);
            _host.getBaseProperty().addStamina((int) value);
        }
        if (_propertyPassiveSkill.inte > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getInte(), _propertyPassiveSkill.inte, operator);
            _host.getBaseProperty().addInte((int) value);
        }
        if (_propertyPassiveSkill.spirit > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getSpirit(), _propertyPassiveSkill.spirit, operator);
            _host.getBaseProperty().addSpirit((int) value);
        }
        if (_propertyPassiveSkill.lucky > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getLucky(), _propertyPassiveSkill.lucky, operator);
            _host.getBaseProperty().addLucky((int) value);
        }
        if (_propertyPassiveSkill.defense > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getDefense(), _propertyPassiveSkill.defense, operator);
            _host.getBaseProperty().addDefense((int) value);
        }
        if (_propertyPassiveSkill.maxHp > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getHpMax(), _propertyPassiveSkill.maxHp, operator);
            _host.getBaseProperty().addHpMax((int) value);
        }
        if (_propertyPassiveSkill.maxMp > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getMpMax(), _propertyPassiveSkill.maxMp, operator);
            _host.getBaseProperty().addMpMax((int) value);
        }
        if (_propertyPassiveSkill.hitLevel > 0.0f) {
            value = this.valueChanged(_host.getBaseProperty().getHitLevel(), _propertyPassiveSkill.hitLevel, operator);
            _host.getBaseProperty().addHitLevel((short) value);
        }
        if (_propertyPassiveSkill.physicsDuckLevel > 0.0f) {
            value = this.valueChanged(_host.getBaseProperty().getPhysicsDuckLevel(), _propertyPassiveSkill.physicsDuckLevel, operator);
            _host.getBaseProperty().addPhysicsDuckLevel((short) value);
        }
        if (_propertyPassiveSkill.physicsDeathblowLevel > 0.0f) {
            value = this.valueChanged(_host.getBaseProperty().getPhysicsDeathblowLevel(), _propertyPassiveSkill.physicsDeathblowLevel, operator);
            _host.getBaseProperty().addPhysicsDeathblowLevel((short) value);
        }
        if (_propertyPassiveSkill.magicDeathblowLevel > 0.0f) {
            value = this.valueChanged(_host.getBaseProperty().getMagicDeathblowLevel(), _propertyPassiveSkill.magicDeathblowLevel, operator);
            _host.getBaseProperty().addMagicDeathblowLevel((short) value);
        }
        if (_propertyPassiveSkill.physicsAttackHarmValue > 0.0f && _isInit) {
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                value = this.valueChanged(0.0f, _propertyPassiveSkill.physicsAttackHarmValue, operator);
                _host.getActualProperty().addAdditionalPhysicsAttackHarmValue((int) value);
            } else {
                value = this.valueChanged(1.0f, _propertyPassiveSkill.physicsAttackHarmValue, operator);
                _host.getActualProperty().addAdditionalPhysicsAttackHarmScale(value);
            }
        }
        if (_propertyPassiveSkill.bePhysicsHarmValue > 0.0f && _isInit) {
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                value = this.valueChanged(0.0f, _propertyPassiveSkill.bePhysicsHarmValue, operator);
                _host.getActualProperty().addAdditionalHarmValueBePhysicsAttack((int) value);
            } else {
                value = this.valueChanged(1.0f, _propertyPassiveSkill.bePhysicsHarmValue, operator);
                _host.getActualProperty().addAdditionalHarmScaleBePhysicsAttack(value);
            }
        }
        if (_propertyPassiveSkill.magicHarmValue != 0.0f && _isInit) {
            value = this.valueChanged(1.0f, _propertyPassiveSkill.magicHarmValue, operator);
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                if (_propertyPassiveSkill.magicHarmType != null) {
                    _host.getActualProperty().addAdditionalMagicHarm(_propertyPassiveSkill.magicHarmType, value);
                } else {
                    MagicHarmList list = new MagicHarmList(value);
                    _host.getActualProperty().addAdditionalMagicHarm(list);
                }
            } else if (_propertyPassiveSkill.magicHarmType != null) {
                _host.getActualProperty().addAdditionalMagicHarmScale(_propertyPassiveSkill.magicHarmType, value);
            } else {
                MagicHarmList list = new MagicHarmList(value);
                _host.getActualProperty().addAdditionalMagicHarmScale(list);
            }
        }
        if (_propertyPassiveSkill.magicHarmValueBeAttack != 0.0f && _isInit) {
            value = this.valueChanged(1.0f, _propertyPassiveSkill.magicHarmValueBeAttack, operator);
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                if (_propertyPassiveSkill.magicHarmTypeBeAttack != null) {
                    _host.getActualProperty().addAdditionalMagicHarmBeAttack(_propertyPassiveSkill.magicHarmTypeBeAttack, value);
                } else {
                    MagicHarmList list = new MagicHarmList(value);
                    _host.getActualProperty().addAdditionalMagicHarmBeAttack(list);
                }
            } else if (_propertyPassiveSkill.magicHarmTypeBeAttack != null) {
                _host.getActualProperty().addAdditionalMagicHarmScaleBeAttack(_propertyPassiveSkill.magicHarmTypeBeAttack, value);
            } else {
                MagicHarmList list = new MagicHarmList(value);
                _host.getActualProperty().addAdditionalMagicHarmScaleBeAttack(list);
            }
        }
        if (_propertyPassiveSkill.magicFastnessValue != 0.0f) {
            value = this.valueChanged(0.0f, _propertyPassiveSkill.magicFastnessValue, operator);
            if (_propertyPassiveSkill.magicFastnessType != null) {
                _host.getBaseProperty().getMagicFastnessList().add(_propertyPassiveSkill.magicFastnessType, (int) value);
            } else {
                EMagic[] magic = EMagic.values();
                for (int i = 0; i < magic.length; ++i) {
                    _host.getBaseProperty().getMagicFastnessList().add(magic[i], (int) value);
                }
            }
        }
        if (_propertyPassiveSkill.hate > 0.0f && _isInit && _host.getObjectType() == EObjectType.PLAYER) {
            value = this.valueChanged(1.0f, _propertyPassiveSkill.hate, operator);
            _host.changeHatredModulus(value);
        }
        if (_propertyPassiveSkill.physicsAttackInterval > 0.0f) {
            value = this.valueChanged((float) _host.getBaseAttackImmobilityTime(), _propertyPassiveSkill.physicsAttackInterval, operator);
            _host.addActualAttackImmobilityTime((int) value);
        }
        if (_propertyPassiveSkill.allSkillReleaseTime > 0.0f && _isInit) {
            ArrayList<ActiveSkill> list2 = _host.activeSkillList;
            for (int j = 0; j < list2.size(); ++j) {
                ActiveSkill activeSkill = list2.get(j);
                value = this.valueChanged(((ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(activeSkill.skillUnit.id)).releaseTime, _propertyPassiveSkill.allSkillReleaseTime, operator);
                ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) activeSkill.skillUnit;
                activeSkillUnit.releaseTime += value;
            }
        }
        if (_propertyPassiveSkill.specialSkillReleaseTimeIDList != null && _isInit) {
            for (int i = 0; i < _propertyPassiveSkill.specialSkillReleaseTimeIDList.size(); ++i) {
                for (int k = 0; k < _host.activeSkillList.size(); ++k) {
                    ActiveSkill activeSkill2 = _host.activeSkillList.get(k);
                    if (activeSkill2.id == _propertyPassiveSkill.specialSkillReleaseTimeIDList.get(i)) {
                        value = this.valueChanged(((ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(activeSkill2.skillUnit.id)).releaseTime, _propertyPassiveSkill.specialSkillReleaseTime, operator);
                        ActiveSkillUnit activeSkillUnit2 = (ActiveSkillUnit) activeSkill2.skillUnit;
                        activeSkillUnit2.releaseTime += value;
                        break;
                    }
                }
            }
        }
        if (_propertyPassiveSkill.resistSpecialStatus != null && _isInit) {
            value = this.valueChanged(0.0f, _propertyPassiveSkill.resistSpecialStatusOdds, operator);
            value = (_isInit ? value : (-value));
            if (_propertyPassiveSkill.resistSpecialStatus == ESpecialStatus.DUMB) {
                ResistOddsList resistOddsList = _host.getResistOddsList();
                resistOddsList.forbidSpellOdds += value;
            } else if (_propertyPassiveSkill.resistSpecialStatus == ESpecialStatus.FAINT) {
                ResistOddsList resistOddsList2 = _host.getResistOddsList();
                resistOddsList2.insensibleOdds += value;
            } else if (_propertyPassiveSkill.resistSpecialStatus == ESpecialStatus.SLEEP) {
                ResistOddsList resistOddsList3 = _host.getResistOddsList();
                resistOddsList3.sleepingOdds += value;
            } else if (_propertyPassiveSkill.resistSpecialStatus == ESpecialStatus.PHY_BOOM) {
                ResistOddsList resistOddsList4 = _host.getResistOddsList();
                resistOddsList4.physicsDeathblowOdds += value;
            } else if (_propertyPassiveSkill.resistSpecialStatus == ESpecialStatus.MAG_BOOM) {
                ResistOddsList resistOddsList5 = _host.getResistOddsList();
                resistOddsList5.magicDeathblowOdds += value;
            } else if (_propertyPassiveSkill.resistSpecialStatus == ESpecialStatus.STOP) {
                ResistOddsList resistOddsList6 = _host.getResistOddsList();
                resistOddsList6.fixBodyOdds += value;
            }
        }
    }

    private boolean enhancePropertySkillUpgrade(final ChangePropertyUnit _lowLevelSkill, final ChangePropertyUnit _highLevelSkill, final HeroPlayer _host) {
        EMathCaluOperator operator = _lowLevelSkill.caluOperator;
        boolean basePropertyChanged = false;
        if (_highLevelSkill.strength > 0.0f || _highLevelSkill.agility > 0.0f || _highLevelSkill.stamina > 0.0f || _highLevelSkill.inte > 0.0f || _highLevelSkill.spirit > 0.0f || _highLevelSkill.lucky > 0.0f || _highLevelSkill.defense > 0.0f || _highLevelSkill.maxHp > 0.0f || _highLevelSkill.maxMp > 0.0f || _highLevelSkill.hitLevel > 0.0f || _highLevelSkill.physicsDuckLevel > 0.0f || _highLevelSkill.physicsDeathblowLevel > 0.0f || _highLevelSkill.magicDeathblowLevel > 0.0f || _highLevelSkill.physicsAttackInterval > 0.0f || _highLevelSkill.physicsAttackHarmValue > 0.0f || _highLevelSkill.bePhysicsHarmValue > 0.0f || _highLevelSkill.magicHarmValue != 0.0f || _highLevelSkill.magicHarmValueBeAttack != 0.0f || _highLevelSkill.hate > 0.0f || _highLevelSkill.resistSpecialStatus != null || _highLevelSkill.allSkillReleaseTime > 0.0f || _highLevelSkill.specialSkillReleaseTimeIDList != null) {
            basePropertyChanged = true;
        }
        float value = 0.0f;
        if (_highLevelSkill.strength > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getStrength(), _highLevelSkill.strength, operator);
            _host.getBaseProperty().addStrength((int) value);
        }
        if (_highLevelSkill.agility > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getAgility(), _highLevelSkill.agility, operator);
            _host.getBaseProperty().addAgility((int) value);
        }
        if (_highLevelSkill.stamina > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getStamina(), _highLevelSkill.stamina, operator);
            _host.getBaseProperty().addStamina((int) value);
        }
        if (_highLevelSkill.inte > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getInte(), _highLevelSkill.inte, operator);
            _host.getBaseProperty().addInte((int) value);
        }
        if (_highLevelSkill.spirit > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getSpirit(), _highLevelSkill.spirit, operator);
            _host.getBaseProperty().addSpirit((int) value);
        }
        if (_highLevelSkill.lucky > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getLucky(), _highLevelSkill.lucky, operator);
            _host.getBaseProperty().addLucky((int) value);
        }
        if (_highLevelSkill.defense > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getDefense(), _highLevelSkill.defense, operator);
            _host.getBaseProperty().addDefense((int) value);
        }
        if (_highLevelSkill.maxHp > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getHpMax(), _highLevelSkill.maxHp, operator);
            _host.getBaseProperty().addHpMax((int) value);
        }
        if (_highLevelSkill.maxMp > 0.0f) {
            value = this.valueChanged((float) _host.getBaseProperty().getMpMax(), _highLevelSkill.maxMp, operator);
            _host.getBaseProperty().addMpMax((int) value);
        }
        if (_highLevelSkill.hitLevel > 0.0f) {
            value = this.valueChanged(_host.getBaseProperty().getHitLevel(), _highLevelSkill.hitLevel, operator);
            _host.getBaseProperty().addHitLevel((short) value);
        }
        if (_highLevelSkill.physicsDuckLevel > 0.0f) {
            value = this.valueChanged(_host.getBaseProperty().getPhysicsDuckLevel(), _highLevelSkill.physicsDuckLevel, operator);
            _host.getBaseProperty().addPhysicsDuckLevel((short) value);
        }
        _host.getActualProperty().getPhysicsDeathblowOdds();
        _host.getBaseProperty().getPhysicsDeathblowLevel();
        if (_highLevelSkill.physicsDeathblowLevel > 0.0f) {
            value = this.valueChanged(_host.getBaseProperty().getPhysicsDeathblowLevel(), _highLevelSkill.physicsDeathblowLevel, operator);
            _host.getBaseProperty().addPhysicsDeathblowLevel((short) value);
        }
        if (_highLevelSkill.magicDeathblowLevel > 0.0f) {
            value = this.valueChanged(_host.getBaseProperty().getMagicDeathblowLevel(), _highLevelSkill.magicDeathblowLevel, operator);
            _host.getBaseProperty().addMagicDeathblowLevel((short) value);
        }
        if (_highLevelSkill.physicsAttackHarmValue > 0.0f) {
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                value = this.valueChanged((float) _host.getActualProperty().getAdditionalPhysicsAttackHarmValue(), _highLevelSkill.physicsAttackHarmValue, operator) - this.valueChanged((float) _host.getActualProperty().getAdditionalPhysicsAttackHarmValue(), _lowLevelSkill.physicsAttackHarmValue, operator);
                _host.getActualProperty().addAdditionalPhysicsAttackHarmValue((int) value);
            } else {
                value = this.valueChanged(1.0f, _highLevelSkill.physicsAttackHarmValue, operator) - this.valueChanged(1.0f, _lowLevelSkill.physicsAttackHarmValue, operator);
                _host.getActualProperty().addAdditionalPhysicsAttackHarmScale(value);
            }
        }
        if (_highLevelSkill.bePhysicsHarmValue > 0.0f) {
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                value = this.valueChanged((float) _host.getActualProperty().getAdditionalHarmValueBePhysicsAttack(), _highLevelSkill.bePhysicsHarmValue, operator) - this.valueChanged((float) _host.getActualProperty().getAdditionalHarmValueBePhysicsAttack(), _lowLevelSkill.bePhysicsHarmValue, operator);
                _host.getActualProperty().addAdditionalHarmValueBePhysicsAttack((int) value);
            } else {
                value = this.valueChanged(1.0f, _highLevelSkill.bePhysicsHarmValue, operator) - this.valueChanged(1.0f, _lowLevelSkill.bePhysicsHarmValue, operator);
                _host.getActualProperty().addAdditionalHarmScaleBePhysicsAttack(value);
            }
        }
        if (_highLevelSkill.magicHarmValue != 0.0f) {
            value = this.valueChanged(1.0f, _highLevelSkill.magicHarmValue, operator) - this.valueChanged(1.0f, _lowLevelSkill.magicHarmValue, operator);
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                if (_highLevelSkill.magicHarmType != null) {
                    _host.getActualProperty().addAdditionalMagicHarm(_highLevelSkill.magicHarmType, value);
                } else {
                    MagicHarmList list = new MagicHarmList(value);
                    _host.getActualProperty().addAdditionalMagicHarm(list);
                }
            } else if (_highLevelSkill.magicHarmType != null) {
                _host.getActualProperty().addAdditionalMagicHarmScale(_highLevelSkill.magicHarmType, value);
            } else {
                MagicHarmList list = new MagicHarmList(value);
                _host.getActualProperty().addAdditionalMagicHarmScale(list);
            }
        }
        if (_highLevelSkill.magicHarmValueBeAttack != 0.0f) {
            value = this.valueChanged(1.0f, _highLevelSkill.magicHarmValueBeAttack, operator) - this.valueChanged(1.0f, _lowLevelSkill.magicHarmValueBeAttack, operator);
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                if (_highLevelSkill.magicHarmTypeBeAttack != null) {
                    _host.getActualProperty().addAdditionalMagicHarmBeAttack(_highLevelSkill.magicHarmTypeBeAttack, value);
                } else {
                    MagicHarmList list = new MagicHarmList(value);
                    _host.getActualProperty().addAdditionalMagicHarmBeAttack(list);
                }
            } else if (_highLevelSkill.magicHarmTypeBeAttack != null) {
                _host.getActualProperty().addAdditionalMagicHarmScaleBeAttack(_highLevelSkill.magicHarmTypeBeAttack, value);
            } else {
                MagicHarmList list = new MagicHarmList(value);
                _host.getActualProperty().addAdditionalMagicHarmScaleBeAttack(list);
            }
        }
        if (_highLevelSkill.hate > 0.0f && _host.getObjectType() == EObjectType.PLAYER) {
            value = this.valueChanged(1.0f, _highLevelSkill.hate, operator) - this.valueChanged(1.0f, _lowLevelSkill.hate, operator);
            _host.changeHatredModulus(value);
        }
        if (_highLevelSkill.resistSpecialStatus != null) {
            value = this.valueChanged(0.0f, _highLevelSkill.resistSpecialStatusOdds, operator) - this.valueChanged(0.0f, _lowLevelSkill.resistSpecialStatusOdds, operator);
            if (_highLevelSkill.resistSpecialStatus == ESpecialStatus.DUMB) {
                ResistOddsList resistOddsList = _host.getResistOddsList();
                resistOddsList.forbidSpellOdds += value;
            } else if (_highLevelSkill.resistSpecialStatus == ESpecialStatus.FAINT) {
                ResistOddsList resistOddsList2 = _host.getResistOddsList();
                resistOddsList2.insensibleOdds += value;
            } else if (_highLevelSkill.resistSpecialStatus == ESpecialStatus.SLEEP) {
                ResistOddsList resistOddsList3 = _host.getResistOddsList();
                resistOddsList3.sleepingOdds += value;
            } else if (_highLevelSkill.resistSpecialStatus == ESpecialStatus.PHY_BOOM) {
                ResistOddsList resistOddsList4 = _host.getResistOddsList();
                resistOddsList4.physicsDeathblowOdds += value;
            } else if (_highLevelSkill.resistSpecialStatus == ESpecialStatus.MAG_BOOM) {
                ResistOddsList resistOddsList5 = _host.getResistOddsList();
                resistOddsList5.magicDeathblowOdds += value;
            } else if (_highLevelSkill.resistSpecialStatus == ESpecialStatus.STOP) {
                ResistOddsList resistOddsList6 = _host.getResistOddsList();
                resistOddsList6.fixBodyOdds += value;
            }
        }
        if (_highLevelSkill.allSkillReleaseTime > 0.0f) {
            ArrayList<ActiveSkill> list2 = _host.activeSkillList;
            for (int i = 0; i < list2.size(); ++i) {
                ActiveSkill activeSkill = list2.get(i);
                value = this.valueChanged(((ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(activeSkill.skillUnit.id)).releaseTime, _highLevelSkill.allSkillReleaseTime, operator) - this.valueChanged(((ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(activeSkill.skillUnit.id)).releaseTime, _lowLevelSkill.allSkillReleaseTime, operator);
                ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) activeSkill.skillUnit;
                activeSkillUnit.releaseTime += value;
                ResponseMessageQueue.getInstance().put(_host.getMsgQueueIndex(), new RefreshSkillTime(_host));
            }
        }
        if (_highLevelSkill.specialSkillReleaseTimeIDList != null) {
            for (int j = 0; j < _lowLevelSkill.specialSkillReleaseTimeIDList.size(); ++j) {
                for (int k = 0; k < _host.activeSkillList.size(); ++k) {
                    ActiveSkill activeSkill2 = _host.activeSkillList.get(k);
                    if (activeSkill2.id == _lowLevelSkill.specialSkillReleaseTimeIDList.get(j)) {
                        value = this.valueChanged(((ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(activeSkill2.skillUnit.id)).releaseTime, _highLevelSkill.specialSkillReleaseTime, operator) - this.valueChanged(((ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(activeSkill2.skillUnit.id)).releaseTime, _lowLevelSkill.specialSkillReleaseTime, operator);
                        ActiveSkillUnit activeSkillUnit2 = (ActiveSkillUnit) activeSkill2.skillUnit;
                        activeSkillUnit2.releaseTime += value;
                        ResponseMessageQueue.getInstance().put(_host.getMsgQueueIndex(), new UpdateActiveSkillNotify(activeSkill2));
                        break;
                    }
                }
            }
        }
        return basePropertyChanged;
    }

    private boolean learnNewEnhancePropertySkill(final ChangePropertyUnit _newSkill, final HeroPlayer _host) {
        EMathCaluOperator operator = _newSkill.caluOperator;
        boolean basePropertyChanged = false;
        if (_newSkill.strength > 0.0f || _newSkill.agility > 0.0f || _newSkill.stamina > 0.0f || _newSkill.inte > 0.0f || _newSkill.spirit > 0.0f || _newSkill.lucky > 0.0f || _newSkill.defense > 0.0f || _newSkill.maxHp > 0.0f || _newSkill.maxMp > 0.0f || _newSkill.hitLevel > 0.0f || _newSkill.physicsDuckLevel > 0.0f || _newSkill.physicsDeathblowLevel > 0.0f || _newSkill.magicDeathblowLevel > 0.0f || _newSkill.stamina > 0.0f || _newSkill.stamina > 0.0f || _newSkill.stamina > 0.0f || _newSkill.physicsAttackInterval > 0.0f) {
            basePropertyChanged = true;
        }
        float value = 0.0f;
        if (_newSkill.physicsAttackHarmValue > 0.0f) {
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                value = this.valueChanged((float) _host.getActualProperty().getAdditionalPhysicsAttackHarmValue(), _newSkill.physicsAttackHarmValue, operator);
                _host.getActualProperty().addAdditionalPhysicsAttackHarmValue((int) value);
            } else {
                value = this.valueChanged(1.0f, _newSkill.physicsAttackHarmValue, operator);
                _host.getActualProperty().addAdditionalPhysicsAttackHarmScale(value);
            }
        }
        if (_newSkill.bePhysicsHarmValue > 0.0f) {
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                value = this.valueChanged((float) _host.getActualProperty().getAdditionalHarmValueBePhysicsAttack(), _newSkill.bePhysicsHarmValue, operator);
                _host.getActualProperty().addAdditionalHarmValueBePhysicsAttack((int) value);
            } else {
                value = this.valueChanged(1.0f, _newSkill.bePhysicsHarmValue, operator);
                _host.getActualProperty().addAdditionalHarmScaleBePhysicsAttack(value);
            }
        }
        if (_newSkill.magicHarmValue != 0.0f) {
            value = this.valueChanged(1.0f, _newSkill.magicHarmValue, operator);
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                if (_newSkill.magicHarmType != null) {
                    _host.getActualProperty().addAdditionalMagicHarm(_newSkill.magicHarmType, value);
                } else {
                    MagicHarmList list = new MagicHarmList(value);
                    _host.getActualProperty().addAdditionalMagicHarm(list);
                }
            } else if (_newSkill.magicHarmType != null) {
                _host.getActualProperty().addAdditionalMagicHarmScale(_newSkill.magicHarmType, value);
            } else {
                MagicHarmList list = new MagicHarmList(value);
                _host.getActualProperty().addAdditionalMagicHarmScale(list);
            }
        }
        if (_newSkill.magicHarmValueBeAttack != 0.0f) {
            value = this.valueChanged(1.0f, _newSkill.magicHarmValueBeAttack, operator);
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                if (_newSkill.magicHarmTypeBeAttack != null) {
                    _host.getActualProperty().addAdditionalMagicHarmBeAttack(_newSkill.magicHarmTypeBeAttack, value);
                } else {
                    MagicHarmList list = new MagicHarmList(value);
                    _host.getActualProperty().addAdditionalMagicHarmBeAttack(list);
                }
            } else if (_newSkill.magicHarmTypeBeAttack != null) {
                _host.getActualProperty().addAdditionalMagicHarmScaleBeAttack(_newSkill.magicHarmTypeBeAttack, value);
            } else {
                MagicHarmList list = new MagicHarmList(value);
                _host.getActualProperty().addAdditionalMagicHarmScaleBeAttack(list);
            }
        }
        if (_newSkill.hate > 0.0f && _host.getObjectType() == EObjectType.PLAYER) {
            value = this.valueChanged(1.0f, _newSkill.hate, operator);
            _host.changeHatredModulus(value);
        }
        if (_newSkill.resistSpecialStatus != null) {
            value = this.valueChanged(0.0f, _newSkill.resistSpecialStatusOdds, operator);
            if (_newSkill.resistSpecialStatus == ESpecialStatus.DUMB) {
                ResistOddsList resistOddsList = _host.getResistOddsList();
                resistOddsList.forbidSpellOdds += value;
            } else if (_newSkill.resistSpecialStatus == ESpecialStatus.FAINT) {
                ResistOddsList resistOddsList2 = _host.getResistOddsList();
                resistOddsList2.insensibleOdds += value;
            } else if (_newSkill.resistSpecialStatus == ESpecialStatus.SLEEP) {
                ResistOddsList resistOddsList3 = _host.getResistOddsList();
                resistOddsList3.sleepingOdds += value;
            } else if (_newSkill.resistSpecialStatus == ESpecialStatus.PHY_BOOM) {
                ResistOddsList resistOddsList4 = _host.getResistOddsList();
                resistOddsList4.physicsDeathblowOdds += value;
            } else if (_newSkill.resistSpecialStatus == ESpecialStatus.MAG_BOOM) {
                ResistOddsList resistOddsList5 = _host.getResistOddsList();
                resistOddsList5.magicDeathblowOdds += value;
            } else if (_newSkill.resistSpecialStatus == ESpecialStatus.STOP) {
                ResistOddsList resistOddsList6 = _host.getResistOddsList();
                resistOddsList6.fixBodyOdds += value;
            }
        }
        if (_newSkill.allSkillReleaseTime > 0.0f) {
            ArrayList<ActiveSkill> list2 = _host.activeSkillList;
            for (int i = 0; i < list2.size(); ++i) {
                ActiveSkill activeSkill = list2.get(i);
                value = this.valueChanged(((ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(activeSkill.skillUnit.id)).releaseTime, _newSkill.allSkillReleaseTime, operator);
                ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) activeSkill.skillUnit;
                activeSkillUnit.releaseTime += value;
                ResponseMessageQueue.getInstance().put(_host.getMsgQueueIndex(), new UpdateActiveSkillNotify(activeSkill));
            }
        }
        if (_newSkill.specialSkillReleaseTimeIDList != null) {
            for (int j = 0; j < _newSkill.specialSkillReleaseTimeIDList.size(); ++j) {
                for (int k = 0; k < _host.activeSkillList.size(); ++k) {
                    ActiveSkill activeSkill2 = _host.activeSkillList.get(k);
                    if (activeSkill2.id == _newSkill.specialSkillReleaseTimeIDList.get(j)) {
                        value = this.valueChanged(((ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(activeSkill2.skillUnit.id)).releaseTime, _newSkill.specialSkillReleaseTime, operator);
                        ActiveSkillUnit activeSkillUnit2 = (ActiveSkillUnit) activeSkill2.skillUnit;
                        activeSkillUnit2.releaseTime += value;
                        ResponseMessageQueue.getInstance().put(_host.getMsgQueueIndex(), new UpdateActiveSkillNotify(activeSkill2));
                        break;
                    }
                }
            }
        }
        return basePropertyChanged;
    }

    public Skill getSkillModel(final int _skillID) {
        return SkillDict.getInstance().getSkill(_skillID);
    }

    public Skill getMonsterSkillModel(final int _skillID) {
        return MonsterSkillDict.getInstance().getSkillUnitRef(_skillID);
    }

    public Skill getSkillIns(final int _skillID) {
        Skill skill = SkillDict.getInstance().getSkill(_skillID);
        if (skill != null) {
            try {
                return skill.clone();
            } catch (CloneNotSupportedException ex) {
            }
        }
        return null;
    }

    public boolean petReleasePassiveSkill(final Pet pet, final int type) {
        return false;
    }

    private void petClearPassiveSkill(final Pet pet, final PetPassiveSkill _skill) {
        if (_skill.targetType == ETargetType.MYSELF) {
            pet.getActualProperty().setAgility((int) this.caluValue((float) pet.getActualProperty().getAgility(), _skill.agility, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            pet.getActualProperty().setInte((int) this.caluValue((float) pet.getActualProperty().getInte(), _skill.inte, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            pet.getActualProperty().setLucky((int) this.caluValue((float) pet.getActualProperty().getLucky(), _skill.lucky, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            pet.getActualProperty().setStrength((int) this.caluValue((float) pet.getActualProperty().getStrength(), _skill.strength, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            pet.getActualProperty().setSpirit((int) this.caluValue((float) pet.getActualProperty().getSpirit(), _skill.spirit, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            pet.getActualProperty().setHitLevel((short) this.caluValue(pet.getActualProperty().getHitLevel(), _skill.hitLevel, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            pet.getActualProperty().setMpMax((int) this.caluValue((float) pet.getActualProperty().getMpMax(), _skill.maxMp, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            pet.getActualProperty().setMagicDeathblowLevel((short) this.caluValue(pet.getActualProperty().getMagicDeathblowLevel(), _skill.magicDeathblowLevel, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            pet.getActualProperty().setPhysicsDeathblowLevel((short) this.caluValue(pet.getActualProperty().getPhysicsDeathblowLevel(), _skill.physicsDeathblowLevel, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            pet.getActualProperty().addAdditionalHarmValueBePhysicsAttack((int) _skill.physicsAttackHarmValue);
            if (_skill.magicHarmType != null) {
                pet.getActualProperty().addAdditionalMagicHarm(_skill.magicHarmType, _skill.magicHarmValue);
            }
            pet.setBaseAttackImmobilityTime((int) this.caluValue((float) pet.getActualAttackImmobilityTime(), _skill.physicsAttackInterval, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
        }
        if (_skill.targetType == ETargetType.OWNER) {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(pet.masterID);
            player.getActualProperty().setAgility((int) this.caluValue((float) player.getActualProperty().getAgility(), _skill.agility, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            player.getActualProperty().setInte((int) this.caluValue((float) player.getActualProperty().getInte(), _skill.inte, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            player.getActualProperty().setLucky((int) this.caluValue((float) player.getActualProperty().getLucky(), _skill.lucky, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            player.getActualProperty().setStrength((int) this.caluValue((float) player.getActualProperty().getStrength(), _skill.strength, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            player.getActualProperty().setSpirit((int) this.caluValue((float) player.getActualProperty().getSpirit(), _skill.spirit, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            player.getActualProperty().setHitLevel((short) this.caluValue(player.getActualProperty().getHitLevel(), _skill.hitLevel, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            player.getActualProperty().setMpMax((int) this.caluValue((float) player.getActualProperty().getMpMax(), _skill.maxMp, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            player.getActualProperty().setMagicDeathblowLevel((short) this.caluValue(player.getActualProperty().getMagicDeathblowLevel(), _skill.magicDeathblowLevel, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            player.getActualProperty().setPhysicsDeathblowLevel((short) this.caluValue(player.getActualProperty().getPhysicsDeathblowLevel(), _skill.physicsDeathblowLevel, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
            player.getActualProperty().addAdditionalHarmValueBePhysicsAttack((int) _skill.physicsAttackHarmValue);
            if (_skill.magicHarmType != null) {
                player.getActualProperty().addAdditionalMagicHarm(_skill.magicHarmType, _skill.magicHarmValue);
            }
            player.setBaseAttackImmobilityTime((int) this.caluValue((float) player.getActualAttackImmobilityTime(), _skill.physicsAttackInterval, EMathCaluOperator.getReverseCaluOperator(_skill.caluOperator)));
        }
    }

    private void petPassiveSkill(final Pet pet, final PetPassiveSkill _skill) {
        if (_skill.targetType == ETargetType.MYSELF) {
            pet.getActualProperty().setAgility((int) this.caluValue((float) pet.getActualProperty().getAgility(), _skill.agility, _skill.caluOperator));
            pet.getActualProperty().setInte((int) this.caluValue((float) pet.getActualProperty().getInte(), _skill.inte, _skill.caluOperator));
            pet.getActualProperty().setLucky((int) this.caluValue((float) pet.getActualProperty().getLucky(), _skill.lucky, _skill.caluOperator));
            pet.getActualProperty().setStrength((int) this.caluValue((float) pet.getActualProperty().getStrength(), _skill.strength, _skill.caluOperator));
            pet.getActualProperty().setSpirit((int) this.caluValue((float) pet.getActualProperty().getSpirit(), _skill.spirit, _skill.caluOperator));
            pet.getActualProperty().setHitLevel((short) this.caluValue(pet.getActualProperty().getHitLevel(), _skill.hitLevel, _skill.caluOperator));
            pet.getActualProperty().setMpMax((int) this.caluValue((float) pet.getActualProperty().getMpMax(), _skill.maxMp, _skill.caluOperator));
            pet.getActualProperty().setMagicDeathblowLevel((short) this.caluValue(pet.getActualProperty().getMagicDeathblowLevel(), _skill.magicDeathblowLevel, _skill.caluOperator));
            pet.getActualProperty().setPhysicsDeathblowLevel((short) this.caluValue(pet.getActualProperty().getPhysicsDeathblowLevel(), _skill.physicsDeathblowLevel, _skill.caluOperator));
            pet.getActualProperty().addAdditionalHarmValueBePhysicsAttack((int) _skill.physicsAttackHarmValue);
            if (_skill.magicHarmType != null) {
                pet.getActualProperty().addAdditionalMagicHarm(_skill.magicHarmType, _skill.magicHarmValue);
            }
            pet.setBaseAttackImmobilityTime((int) this.caluValue((float) pet.getActualAttackImmobilityTime(), _skill.physicsAttackInterval, _skill.caluOperator));
        }
        if (_skill.targetType == ETargetType.OWNER) {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(pet.masterID);
            player.getActualProperty().setAgility((int) this.caluValue((float) player.getActualProperty().getAgility(), _skill.agility, _skill.caluOperator));
            player.getActualProperty().setInte((int) this.caluValue((float) player.getActualProperty().getInte(), _skill.inte, _skill.caluOperator));
            player.getActualProperty().setLucky((int) this.caluValue((float) player.getActualProperty().getLucky(), _skill.lucky, _skill.caluOperator));
            player.getActualProperty().setStrength((int) this.caluValue((float) player.getActualProperty().getStrength(), _skill.strength, _skill.caluOperator));
            player.getActualProperty().setSpirit((int) this.caluValue((float) player.getActualProperty().getSpirit(), _skill.spirit, _skill.caluOperator));
            player.getActualProperty().setHitLevel((short) this.caluValue(player.getActualProperty().getHitLevel(), _skill.hitLevel, _skill.caluOperator));
            player.getActualProperty().setMpMax((int) this.caluValue((float) player.getActualProperty().getMpMax(), _skill.maxMp, _skill.caluOperator));
            player.getActualProperty().setMagicDeathblowLevel((short) this.caluValue(player.getActualProperty().getMagicDeathblowLevel(), _skill.magicDeathblowLevel, _skill.caluOperator));
            player.getActualProperty().setPhysicsDeathblowLevel((short) this.caluValue(player.getActualProperty().getPhysicsDeathblowLevel(), _skill.physicsDeathblowLevel, _skill.caluOperator));
            player.getActualProperty().addAdditionalHarmValueBePhysicsAttack((int) _skill.physicsAttackHarmValue);
            if (_skill.magicHarmType != null) {
                player.getActualProperty().addAdditionalMagicHarm(_skill.magicHarmType, _skill.magicHarmValue);
            }
            player.setBaseAttackImmobilityTime((int) this.caluValue((float) player.getActualAttackImmobilityTime(), _skill.physicsAttackInterval, _skill.caluOperator));
        }
    }

    private float caluValue(final float baseValue, final float _caluValue, final EMathCaluOperator caluOperator) {
        switch (caluOperator) {
            case ADD: {
                return baseValue + _caluValue;
            }
            case DEC: {
                return baseValue - _caluValue;
            }
            case MUL: {
                return baseValue * _caluValue;
            }
            case DIV: {
                return baseValue / _caluValue;
            }
            default: {
                return baseValue + _caluValue;
            }
        }
    }

    public boolean petReleaseSkill(final Pet _releaser, final int _skillID, final ME2GameObject _target, final byte _direction) {
        SkillServiceImpl.log.debug((Object) ("petReleaseSkill start ... " + _releaser.isDied() + " -- " + " _target isEnable = " + _target.isEnable()));
        if (!_releaser.isEnable() || _releaser.isDied() || _target == null || !_target.isEnable()) {
            return false;
        }
        PetActiveSkill activeSkill = _releaser.getPetActiveSkillByID(_skillID);
        if (activeSkill == null) {
            return false;
        }
        SkillServiceImpl.log.debug((Object) ("activeSkill  = " + activeSkill.name));
        if (activeSkill.lastUseTime + activeSkill.coolDownTime * 1000 > System.currentTimeMillis()) {
            SkillServiceImpl.log.debug((Object) "\u5ba0\u7269\u6280\u80fd\u653b\u51fb\uff0c\u51b7\u5374\u65f6\u95f4\u672a\u5230\u3002");
            return false;
        }
        SkillServiceImpl.log.debug((Object) ("pet skill attack target type = " + activeSkill.targetType));
        if (activeSkill.targetType == ETargetType.ENEMY) {
            if (ETargetRangeType.SINGLE == activeSkill.targetRangeType) {
                if (activeSkill.skillType == EActiveSkillType.PHYSICS) {
                    this.singlePhysicsAttackSkill(_releaser, _target, activeSkill);
                } else {
                    this.singleMagicAttackSkill(_releaser, _target, activeSkill);
                }
            }
        } else if (activeSkill.targetType == ETargetType.OWNER) {
            this.singleResumeSkill(_releaser, _target, activeSkill);
        } else if (activeSkill.targetType == ETargetType.MYSELF) {
            this.singleResumeSkill(_releaser, _releaser, activeSkill);
        } else if (activeSkill.targetType == ETargetType.DIER) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _target).getMsgQueueIndex(), new ReviveConfirm(_releaser.getName(), _releaser.getCellX(), _releaser.getCellY(), activeSkill.resumeHp, activeSkill.resumeMp, (short) 60));
        }
        activeSkill.lastUseTime = System.currentTimeMillis();
        return true;
    }

    public boolean playerReleaseSkill(final HeroPlayer _releaser, final int _skillID, ME2GameObject _target, final byte _direction) {
        if (!_releaser.isEnable() || _releaser.isDead() || _target == null || !_target.isEnable()) {
            return false;
        }
        ActiveSkill activeSkill = _releaser.activeSkillTable.get(_skillID);
        if (activeSkill == null || !this.canReleaseSkill(_releaser, _target, activeSkill)) {
            return false;
        }
        ActiveSkillUnit skillUnit = (ActiveSkillUnit) activeSkill.skillUnit;
        if (skillUnit.targetType == ETargetType.ENEMY) {
            if (ETargetRangeType.SINGLE == skillUnit.targetRangeType) {
                if (skillUnit.activeSkillType == EActiveSkillType.PHYSICS) {
                    this.singlePhysicsAttackSkill(_releaser, _target, activeSkill);
                } else {
                    this.singleMagicAttackSkill(_releaser, _target, activeSkill);
                }
            } else if (ETargetRangeType.SOME == skillUnit.targetRangeType) {
                ArrayList<ME2GameObject> targetList = null;
                if (EAOERangeBaseLine.RELEASER == skillUnit.rangeBaseLine) {
                    _target = _releaser;
                }
                if (EAOERangeType.FRONT_RECT == skillUnit.rangeMode) {
                    targetList = MapServiceImpl.getInstance().getAttackableObjectListInForeRange(_target.where(), skillUnit.rangeX, skillUnit.rangeY, _releaser, skillUnit.rangeTargetNumber);
                } else {
                    targetList = MapServiceImpl.getInstance().getAttackableObjectListInRange(_target.where(), _target.getCellX(), _target.getCellY(), skillUnit.rangeX, _releaser, skillUnit.rangeTargetNumber);
                }
                if (targetList != null && targetList.size() > 0) {
                    if (skillUnit.activeSkillType == EActiveSkillType.PHYSICS) {
                        this.groupPhysicsAttackSkill(_releaser, targetList, activeSkill);
                    } else {
                        this.groupMagicAttackSkill(_releaser, targetList, activeSkill);
                    }
                }
            }
        } else if (skillUnit.targetType == ETargetType.FRIEND) {
            if (ETargetRangeType.SINGLE == skillUnit.targetRangeType) {
                this.singleResumeSkill(_releaser, _target, activeSkill);
            } else if (ETargetRangeType.SOME == skillUnit.targetRangeType) {
                ArrayList<HeroPlayer> targetList2 = null;
                HeroPlayer rangeBaseLintTarget = (HeroPlayer) ((skillUnit.rangeBaseLine == EAOERangeBaseLine.RELEASER) ? _releaser : _target);
                if (EAOERangeType.FRONT_RECT == skillUnit.rangeMode) {
                    targetList2 = MapServiceImpl.getInstance().getFriendsPlayerListInForeRange(rangeBaseLintTarget, skillUnit.rangeX, skillUnit.rangeY, skillUnit.rangeTargetNumber);
                } else {
                    targetList2 = MapServiceImpl.getInstance().getFriendsPlayerInRange(rangeBaseLintTarget, _target.getCellX(), _target.getCellY(), skillUnit.rangeX, skillUnit.rangeTargetNumber);
                }
                if (targetList2 != null && targetList2.size() > 0) {
                    this.groupResumeSkill(_releaser, targetList2, activeSkill);
                }
            } else {
                ArrayList<HeroPlayer> targetList2 = null;
                if (EAOERangeBaseLine.RELEASER == skillUnit.rangeBaseLine) {
                    _target = _releaser;
                }
                if (EAOERangeType.FRONT_RECT == skillUnit.rangeMode) {
                    targetList2 = MapServiceImpl.getInstance().getGroupPlayerListInForeRange(_releaser, skillUnit.rangeX, skillUnit.rangeY, skillUnit.rangeTargetNumber);
                } else {
                    targetList2 = MapServiceImpl.getInstance().getGroupPlayerInRange(_releaser, _target.getCellX(), _target.getCellY(), skillUnit.rangeX, skillUnit.rangeTargetNumber);
                }
                if (targetList2 != null && targetList2.size() > 0) {
                    this.groupResumeSkill(_releaser, targetList2, activeSkill);
                } else {
                    this.singleResumeSkill(_releaser, _releaser, activeSkill);
                }
            }
        } else if (skillUnit.targetType == ETargetType.MYSELF) {
            this.singleResumeSkill(_releaser, _releaser, activeSkill);
        } else if (skillUnit.targetType == ETargetType.DIER) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _target).getMsgQueueIndex(), new ReviveConfirm(_releaser.getName(), _releaser.getCellX(), _releaser.getCellY(), skillUnit.resumeHp, skillUnit.resumeMp, (short) 60));
        }
        activeSkill.reduceCoolDownTime = activeSkill.coolDownTime;
        if (activeSkill.coolDownID > 0 && activeSkill.reduceCoolDownTime > 300) {
            for (final ActiveSkill otherSkill : _releaser.activeSkillList) {
                if (otherSkill.id != activeSkill.id && otherSkill.coolDownID == activeSkill.coolDownID) {
                    otherSkill.reduceCoolDownTime = otherSkill.coolDownTime;
                }
            }
        }
        return true;
    }

    public void sendSingleSkillAnimation(final ME2GameObject _releaser, final ME2GameObject _target, final int _releaseAnimation, final int _releaseImage, final int _accepteAnimation, final int _accepteImage, final byte _actionID, final byte _tier, final byte _reHeight, final byte _accHeight, final byte _isDirection) {
        if (_accepteAnimation == 0 && _releaseAnimation == 0) {
            return;
        }
        AbsResponseMessage msg = new SkillAnimationNotify(_releaser, _releaseAnimation, _releaseImage, _target, _accepteAnimation, _accepteImage, _actionID, _tier, _reHeight, _accHeight, _isDirection);
        if (EObjectType.PLAYER == _releaser.getObjectType()) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(), msg);
            MapSynchronousInfoBroadcast.getInstance().put(_releaser.where(), msg, true, ((HeroPlayer) _releaser).getID());
        } else {
            MapSynchronousInfoBroadcast.getInstance().put(_releaser.where(), msg, false, 0);
        }
    }

    public void sendGroupAttackSkillAnimation(final ME2GameObject _releaser, final ArrayList<ME2GameObject> _targetList, final int _releaseAnimation, final int _releaseImage, final int _accepteAnimation, final int _accepteImage, final byte _actionID, final byte _tier, final byte _reHeight, final byte _accHeight, final byte _isDirection) {
        if (_accepteAnimation == 0 && _releaseAnimation == 0) {
            return;
        }
        AbsResponseMessage msg = new SkillAnimationNotify(_releaser, _targetList, _releaseAnimation, _releaseImage, _accepteAnimation, _accepteImage, _actionID, _tier, _reHeight, _accHeight, _isDirection);
        if (EObjectType.PLAYER == _releaser.getObjectType()) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(), msg);
            MapSynchronousInfoBroadcast.getInstance().put(_releaser.where(), msg, true, ((HeroPlayer) _releaser).getID());
        } else {
            MapSynchronousInfoBroadcast.getInstance().put(_releaser.where(), msg, false, 0);
        }
    }

    private void singlePhysicsAttackSkill(final HeroPlayer _player, final ME2GameObject _target, final ActiveSkill _activeSkill) {
        ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
        FightServiceImpl.getInstance().refreshFightTime(_player, _target);
        this.sendSingleSkillAnimation(_player, _target, activeSkillUnit.releaseAnimationID, activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, activeSkillUnit.activeImageID, activeSkillUnit.animationAction, activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, activeSkillUnit.heightRelation, activeSkillUnit.isDirection);
        if (SkillServiceImpl.RANDOM.nextInt(100) > CEService.attackPhysicsHitOdds(_player.getActualProperty().getLucky(), _player.getActualProperty().getHitLevel(), _player.getLevel(), _target.getLevel()) - CEService.attackPhysicsDuckOdds(_player.getLevel(), _target.getActualProperty().getAgility(), _target.getActualProperty().getLucky(), _target.getActualProperty().getPhysicsDuckLevel(), _target.getLevel())) {
            EffectServiceImpl.getInstance().checkTouchEffect(_player, _target, ETouchType.ACTIVE, true);
            this.checkTouchSkill(_player, _target, ETouchType.ACTIVE, true);
            FightServiceImpl.getInstance().processMiss(_player, _target);
            return;
        }
        if (activeSkillUnit.hateValue > 0 && _target instanceof Monster) {
            Monster monster = (Monster) _target;
            if (monster.getAttackerAtFirst() == null) {
                monster.setAttackerAtFirst(_player);
            }
            monster.addTargetHatredValue(_player, activeSkillUnit.hateValue);
        }
        if (activeSkillUnit.physicsHarmValue > 0 || activeSkillUnit.weaponHarmMult > 0.0f) {
            int attack = 0;
            int harmValue = 0;
            if (activeSkillUnit.weaponHarmMult > 0.0f) {
                EquipmentInstance weapon = _player.getBodyWear().getWeapon();
                if (weapon != null) {
                    attack = CEService.weaponPhysicsAttackBySkill(_player.getActualProperty().getActualPhysicsAttack(), activeSkillUnit.weaponHarmMult, activeSkillUnit.physicsHarmValue);
                }
            }
            harmValue = CEService.physicsHarm(_player.getLevel(), attack, _target.getLevel(), _target.getActualProperty().getDefense());
            boolean isDeathblow = false;
            float physicsDeathblowOdds = CEService.attackPhysicsDeathblowOdds(_player.getActualProperty().getAgility(), _player.getActualProperty().getPhysicsDeathblowLevel(), _player.getLevel(), _target.getLevel());
            if (SkillServiceImpl.RANDOM.nextInt(100) <= physicsDeathblowOdds - _target.getResistOddsList().physicsDeathblowOdds) {
                harmValue = CEService.calculateDeathblowHarm(harmValue, _player.getActualProperty().getLucky());
                isDeathblow = true;
                EffectServiceImpl.getInstance().checkTouchEffect(_player, _target, ETouchType.TOUCH_DEATHBLOW_BY_PHYSICS, true);
                EffectServiceImpl.getInstance().checkTouchEffect(_target, _player, ETouchType.BE_DEATHBLOW_BY_PHYSICS, false);
            }
            int tempHarm = 0;
            tempHarm += (int) (harmValue * _player.getActualProperty().getAdditionalPhysicsAttackHarmScale());
            System.out.println("\u4e58\u6570:" + _player.getActualProperty().getAdditionalPhysicsAttackHarmScale());
            SkillServiceImpl.log.info((Object) ("\u9644\u52a0\u4f24\u5bb3\u7b2c1\u6b21:" + tempHarm));
            tempHarm += _player.getActualProperty().getAdditionalPhysicsAttackHarmValue();
            SkillServiceImpl.log.info((Object) ("\u9644\u52a0\u4f24\u5bb3\u7b2c2\u6b21:" + tempHarm));
            tempHarm += (int) (harmValue * _target.getActualProperty().getAdditionalHarmScaleBePhysicsAttack());
            SkillServiceImpl.log.info((Object) ("\u9644\u52a0\u4f24\u5bb3\u7b2c3\u6b21:" + tempHarm));
            tempHarm += _target.getActualProperty().getAdditionalHarmValueBePhysicsAttack();
            SkillServiceImpl.log.info((Object) ("\u9644\u52a0\u4f24\u5bb3\u7b2c4\u6b21:" + tempHarm));
            harmValue += tempHarm;
            if (FightServiceImpl.getInstance().processReduceHp(_player, _target, harmValue, true, isDeathblow, null)) {
                EffectServiceImpl.getInstance().checkTouchEffect(_player, _target, ETouchType.ATTACK_BY_PHYSICS, true);
                this.checkTouchSkill(_player, _target, ETouchType.ATTACK_BY_PHYSICS, true);
                return;
            }
        }
        if (_activeSkill.addEffectUnit != null) {
            EffectServiceImpl.getInstance().appendSkillEffect(_player, _target, _activeSkill.addEffectUnit);
        } else if (_activeSkill.addSkillUnit != null) {
            this.additionalSkillUnitActive(_player, _target, (ActiveSkillUnit) _activeSkill.addSkillUnit, 1, 1.0f);
        }
        if (activeSkillUnit.additionalOddsActionUnitList != null) {
            AdditionalActionUnit[] additionalOddsActionUnitList;
            for (int length = (additionalOddsActionUnitList = activeSkillUnit.additionalOddsActionUnitList).length, i = 0; i < length; ++i) {
                AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList[i];
                if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                    this.additionalSkillUnitActive(_player, _target, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                } else if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                    SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                    EffectServiceImpl.getInstance().appendSkillEffect(_player, _target, additionalActionUnit.skillOrEffectUnitID);
                }
            }
        }
    }

    private void singlePhysicsAttackSkill(final Monster _monster, final ME2GameObject _target, final ActiveSkill _activeSkill) {
        ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
        FightServiceImpl.getInstance().refreshFightTime(_monster, _target);
        this.sendSingleSkillAnimation(_monster, _target, activeSkillUnit.releaseAnimationID, activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, activeSkillUnit.activeImageID, activeSkillUnit.animationAction, activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, activeSkillUnit.heightRelation, activeSkillUnit.isDirection);
        float odds = CEService.attackPhysicsHitOdds(_monster.getActualProperty().getLucky(), _monster.getActualProperty().getHitLevel(), _monster.getLevel(), _target.getLevel()) - CEService.attackPhysicsDuckOdds(_monster.getLevel(), _target.getActualProperty().getAgility(), _target.getActualProperty().getLucky(), _target.getActualProperty().getPhysicsDuckLevel(), _target.getLevel());
        int radom = SkillServiceImpl.RANDOM.nextInt(100);
        if (radom > odds) {
            EffectServiceImpl.getInstance().checkTouchEffect(_monster, _target, ETouchType.ACTIVE, true);
            this.checkTouchSkill(_monster, _target, ETouchType.ACTIVE, true);
            FightServiceImpl.getInstance().processMiss(_monster, _target);
            return;
        }
        if (activeSkillUnit.physicsHarmValue > 0) {
            int attack = activeSkillUnit.physicsHarmValue;
            int harmValue = 0;
            harmValue = CEService.physicsHarm(_monster.getLevel(), attack, _target.getLevel(), _target.getActualProperty().getDefense());
            boolean isDeathblow = false;
            float physicsDeathblowOdds = CEService.attackPhysicsDeathblowOdds(_monster.getActualProperty().getAgility(), _monster.getActualProperty().getPhysicsDeathblowLevel(), _monster.getLevel(), _target.getLevel());
            if (SkillServiceImpl.RANDOM.nextInt(100) <= physicsDeathblowOdds - _target.getResistOddsList().physicsDeathblowOdds) {
                harmValue = CEService.calculateDeathblowHarm(harmValue, _monster.getActualProperty().getLucky());
                isDeathblow = true;
            }
            harmValue += (int) (harmValue * _monster.getActualProperty().getAdditionalPhysicsAttackHarmScale());
            harmValue += _monster.getActualProperty().getAdditionalPhysicsAttackHarmValue();
            harmValue += (int) (harmValue * _target.getActualProperty().getAdditionalHarmScaleBePhysicsAttack());
            harmValue += _target.getActualProperty().getAdditionalHarmValueBePhysicsAttack();
            if (FightServiceImpl.getInstance().processReduceHp(_monster, _target, harmValue, true, isDeathblow, null)) {
                return;
            }
        }
        EffectServiceImpl.getInstance().checkTouchEffect(_monster, _target, activeSkillUnit.activeTouchType, true);
        EffectServiceImpl.getInstance().checkTouchEffect(_target, _monster, activeSkillUnit.passiveTouchType, true);
        if (_activeSkill.addEffectUnit != null) {
            EffectServiceImpl.getInstance().appendSkillEffect(_monster, _target, _activeSkill.addEffectUnit);
        }
        if (activeSkillUnit.additionalOddsActionUnitList != null) {
            AdditionalActionUnit[] additionalOddsActionUnitList;
            for (int length = (additionalOddsActionUnitList = activeSkillUnit.additionalOddsActionUnitList).length, i = 0; i < length; ++i) {
                AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList[i];
                if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                    SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                    EffectServiceImpl.getInstance().appendSkillEffect(_monster, _target, additionalActionUnit.skillOrEffectUnitID);
                }
            }
        }
    }

    private void singlePhysicsAttackSkill(final Pet _pet, final ME2GameObject _target, final PetActiveSkill _activeSkill) {
    }

    private void singleMagicAttackSkill(final HeroPlayer _player, final ME2GameObject _target, final ActiveSkill _activeSkill) {
        ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
        FightServiceImpl.getInstance().refreshFightTime(_player, _target);
        this.sendSingleSkillAnimation(_player, _target, activeSkillUnit.releaseAnimationID, activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, activeSkillUnit.activeImageID, activeSkillUnit.animationAction, activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, activeSkillUnit.heightRelation, activeSkillUnit.isDirection);
        EffectServiceImpl.getInstance().checkTouchEffect(_player, _target, ETouchType.USE_MAGIC, true);
        this.checkTouchSkill(_player, _target, ETouchType.USE_MAGIC, true);
        if (activeSkillUnit.needMagicHit) {
            float odds = CEService.attackMagicHitOdds(_player.getActualProperty().getLucky(), _player.getActualProperty().getHitLevel(), _player.getLevel(), _target.getLevel(), _target.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(activeSkillUnit.magicHarmType));
            if (SkillServiceImpl.RANDOM.nextInt(100) > odds) {
                FightServiceImpl.getInstance().processMiss(_player, _target);
                return;
            }
        }
        if (activeSkillUnit.magicHarmHpValue > 0) {
            int magicAttack = CEService.magicHarmBySkill((float) (int) _player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(activeSkillUnit.magicHarmType), activeSkillUnit.magicHarmHpValue, activeSkillUnit.releaseTime, _player.getLevel(), _activeSkill.level);
            int harmValue = CEService.attackMagicHarm(_player.getLevel(), magicAttack, _target.getLevel(), _target.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(activeSkillUnit.magicHarmType));
            boolean isDeathblow = false;
            float magicDeathblowOdds = CEService.attackMagicDeathblowOdds(_player.getActualProperty().getMagicDeathblowLevel(), _player.getLevel(), _target.getLevel());
            if (SkillServiceImpl.RANDOM.nextInt(100) <= magicDeathblowOdds - _target.getResistOddsList().magicDeathblowOdds) {
                harmValue = CEService.calculateDeathblowHarm(harmValue, _player.getActualProperty().getLucky());
                isDeathblow = true;
                EffectServiceImpl.getInstance().checkTouchEffect(_player, _target, ETouchType.TOUCH_DEATHBLOW_BY_MAGIC, true);
                EffectServiceImpl.getInstance().checkTouchEffect(_target, _player, ETouchType.TOUCH_DEATHBLOW_BY_MAGIC, false);
            }
            harmValue += (int) (harmValue * _player.getActualProperty().getAdditionalMagicHarmScale(activeSkillUnit.magicHarmType));
            harmValue += (int) _player.getActualProperty().getAdditionalMagicHarm(activeSkillUnit.magicHarmType);
            harmValue += (int) (harmValue * _target.getActualProperty().getAdditionalMagicHarmScaleBeAttack(activeSkillUnit.magicHarmType));
            harmValue += (int) _target.getActualProperty().getAdditionalMagicHarmBeAttack(activeSkillUnit.magicHarmType);
            EffectServiceImpl.getInstance().checkTouchEffect(_player, _target, ETouchType.ATTACK_BY_MAGIC, true);
            this.checkTouchSkill(_player, _target, ETouchType.ATTACK_BY_MAGIC, true);
            if (FightServiceImpl.getInstance().processReduceHp(_player, _target, harmValue, true, isDeathblow, activeSkillUnit.magicHarmType)) {
                return;
            }
        }
        if (activeSkillUnit.hateValue > 0 && _target instanceof Monster) {
            Monster monster = (Monster) _target;
            if (monster.getAttackerAtFirst() == null) {
                monster.setAttackerAtFirst(_player);
            }
            monster.addTargetHatredValue(_player, activeSkillUnit.hateValue);
        }
        if (activeSkillUnit.magicHarmMpValue != 0) {
            _player.addMp(-activeSkillUnit.resumeMp);
            FightServiceImpl.getInstance().processSingleTargetMpChange(_player, false);
        }
        EffectServiceImpl.getInstance().checkTouchEffect(_target, _player, ETouchType.USE_MAGIC, true);
        this.checkTouchSkill(_target, _player, ETouchType.USE_MAGIC, true);
        if (_activeSkill.addEffectUnit != null) {
            EffectServiceImpl.getInstance().appendSkillEffect(_player, _target, _activeSkill.addEffectUnit);
        } else if (_activeSkill.addSkillUnit != null) {
            this.additionalSkillUnitActive(_player, _target, (ActiveSkillUnit) _activeSkill.addSkillUnit, 1, 1.0f);
        }
        if (activeSkillUnit.additionalOddsActionUnitList != null) {
            AdditionalActionUnit[] additionalOddsActionUnitList;
            for (int length = (additionalOddsActionUnitList = activeSkillUnit.additionalOddsActionUnitList).length, i = 0; i < length; ++i) {
                AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList[i];
                if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                    this.additionalSkillUnitActive(_player, _target, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                } else if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                    SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                    EffectServiceImpl.getInstance().appendSkillEffect(_player, _target, additionalActionUnit.skillOrEffectUnitID);
                }
            }
        }
    }

    private void singleMagicAttackSkill(final Monster _monster, final ME2GameObject _target, final ActiveSkill _activeSkill) {
        try {
            ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
            FightServiceImpl.getInstance().refreshFightTime(_monster, _target);
            this.sendSingleSkillAnimation(_monster, _target, activeSkillUnit.releaseAnimationID, activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, activeSkillUnit.activeImageID, activeSkillUnit.animationAction, activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, activeSkillUnit.heightRelation, activeSkillUnit.isDirection);
            float odds = CEService.attackMagicHitOdds(_monster.getActualProperty().getLucky(), _monster.getActualProperty().getHitLevel(), _monster.getLevel(), _target.getLevel(), _target.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(activeSkillUnit.magicHarmType));
            int radom = SkillServiceImpl.RANDOM.nextInt(100);
            if (radom > odds) {
                EffectServiceImpl.getInstance().checkTouchEffect(_monster, _target, ETouchType.ATTACK_BY_MAGIC, true);
                this.checkTouchSkill(_monster, _target, ETouchType.ATTACK_BY_MAGIC, true);
                FightServiceImpl.getInstance().processMiss(_monster, _target);
                return;
            }
            if (activeSkillUnit.magicHarmHpValue > 0) {
                int magicAttack = CEService.magicHarmBySkill((float) (int) _monster.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(activeSkillUnit.magicHarmType), activeSkillUnit.magicHarmHpValue, activeSkillUnit.releaseTime, _monster.getLevel(), _activeSkill.level);
                int harmValue = CEService.attackMagicHarm(_monster.getLevel(), magicAttack, _target.getLevel(), _target.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(activeSkillUnit.magicHarmType));
                boolean isDeathblow = false;
                float magicDeathblowOdds = CEService.attackMagicDeathblowOdds(_monster.getActualProperty().getMagicDeathblowLevel(), _monster.getLevel(), _target.getLevel());
                if (SkillServiceImpl.RANDOM.nextInt(100) <= magicDeathblowOdds - _target.getResistOddsList().magicDeathblowOdds) {
                    harmValue = CEService.calculateDeathblowHarm(harmValue, _monster.getActualProperty().getLucky());
                    isDeathblow = true;
                }
                harmValue += (int) (harmValue * _monster.getActualProperty().getAdditionalMagicHarmScale(activeSkillUnit.magicHarmType));
                harmValue += (int) _monster.getActualProperty().getAdditionalMagicHarm(activeSkillUnit.magicHarmType);
                harmValue += (int) (harmValue * _target.getActualProperty().getAdditionalMagicHarmScaleBeAttack(activeSkillUnit.magicHarmType));
                harmValue += (int) _target.getActualProperty().getAdditionalMagicHarmBeAttack(activeSkillUnit.magicHarmType);
                if (FightServiceImpl.getInstance().processReduceHp(_monster, _target, harmValue, true, isDeathblow, activeSkillUnit.magicHarmType)) {
                    return;
                }
            }
            if (activeSkillUnit.magicHarmMpValue != 0) {
                _monster.addMp(-activeSkillUnit.magicHarmMpValue);
            }
            if (_activeSkill.addEffectUnit != null) {
                EffectServiceImpl.getInstance().appendSkillEffect(_monster, _target, _activeSkill.addEffectUnit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void singleMagicAttackSkill(final Pet _pet, final ME2GameObject _target, final PetActiveSkill _activeSkill) {
    }

    private void groupPhysicsAttackSkill(final HeroPlayer _player, final ArrayList<ME2GameObject> _targetList, final ActiveSkill _activeSkill) {
        ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
        this.sendGroupAttackSkillAnimation(_player, _targetList, activeSkillUnit.releaseAnimationID, activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, activeSkillUnit.activeImageID, activeSkillUnit.animationAction, activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, activeSkillUnit.heightRelation, activeSkillUnit.isDirection);
        if (activeSkillUnit.physicsHarmValue > 0 || activeSkillUnit.hateValue > 0) {
            int harmValue = 0;
            int attack = 0;
            if (activeSkillUnit.weaponHarmMult > 0.0f) {
                EquipmentInstance weapon = _player.getBodyWear().getWeapon();
                if (weapon != null) {
                    attack += CEService.weaponPhysicsAttackBySkill(_player.getActualProperty().getActualPhysicsAttack(), activeSkillUnit.weaponHarmMult, activeSkillUnit.physicsHarmValue);
                }
            }
            if (activeSkillUnit.isAverageResult) {
                harmValue = CEService.physicsHarm(_player.getLevel(), attack, _player.getLevel(), 0);
                harmValue += (int) (harmValue * _player.getActualProperty().getAdditionalPhysicsAttackHarmScale());
                harmValue += _player.getActualProperty().getAdditionalPhysicsAttackHarmValue();
                harmValue /= _targetList.size();
                int i = 0;
                while (i < _targetList.size()) {
                    ME2GameObject target = _targetList.get(i);
                    harmValue += (int) (harmValue * target.getActualProperty().getAdditionalHarmScaleBePhysicsAttack());
                    harmValue += target.getActualProperty().getAdditionalHarmValueBePhysicsAttack();
                    if (FightServiceImpl.getInstance().processReduceHp(_player, target, harmValue, true, false, null)) {
                        _targetList.remove(i);
                    } else {
                        ++i;
                    }
                }
            } else {
                int j = 0;
                while (j < _targetList.size()) {
                    ME2GameObject target2 = _targetList.get(j);
                    if (SkillServiceImpl.RANDOM.nextInt(100) > CEService.attackPhysicsHitOdds(_player.getActualProperty().getLucky(), _player.getActualProperty().getHitLevel(), _player.getLevel(), target2.getLevel()) - CEService.attackPhysicsDuckOdds(_player.getLevel(), target2.getActualProperty().getAgility(), target2.getActualProperty().getLucky(), target2.getActualProperty().getPhysicsDuckLevel(), target2.getLevel())) {
                        FightServiceImpl.getInstance().processMiss(_player, target2);
                        ++j;
                    } else {
                        if (activeSkillUnit.hateValue > 0 && target2 instanceof Monster) {
                            Monster monster = (Monster) target2;
                            if (monster.getAttackerAtFirst() == null) {
                                monster.setAttackerAtFirst(_player);
                            }
                            monster.addTargetHatredValue(_player, activeSkillUnit.hateValue);
                        }
                        harmValue = CEService.physicsHarm(_player.getLevel(), attack, target2.getLevel(), target2.getActualProperty().getDefense());
                        boolean isDeathblow = false;
                        if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.attackPhysicsDeathblowOdds(_player.getActualProperty().getAgility(), _player.getActualProperty().getPhysicsDeathblowLevel(), _player.getLevel(), target2.getLevel()) - target2.getResistOddsList().physicsDeathblowOdds) {
                            harmValue = CEService.calculateDeathblowHarm(harmValue, _player.getActualProperty().getLucky());
                            isDeathblow = true;
                            EffectServiceImpl.getInstance().checkTouchEffect(_player, target2, ETouchType.TOUCH_DEATHBLOW_BY_PHYSICS, true);
                            EffectServiceImpl.getInstance().checkTouchEffect(target2, _player, ETouchType.TOUCH_DEATHBLOW_BY_PHYSICS, false);
                        }
                        harmValue += (int) (harmValue * _player.getActualProperty().getAdditionalPhysicsAttackHarmScale());
                        harmValue += _player.getActualProperty().getAdditionalPhysicsAttackHarmValue();
                        harmValue += (int) (harmValue * target2.getActualProperty().getAdditionalHarmScaleBePhysicsAttack());
                        harmValue += target2.getActualProperty().getAdditionalHarmValueBePhysicsAttack();
                        if (FightServiceImpl.getInstance().processReduceHp(_player, target2, harmValue, true, isDeathblow, null)) {
                            _targetList.remove(j);
                        } else {
                            ++j;
                        }
                    }
                }
            }
        }
        if (_activeSkill.addEffectUnit != null) {
            for (final ME2GameObject target3 : _targetList) {
                EffectServiceImpl.getInstance().appendSkillEffect(_player, target3, _activeSkill.addEffectUnit);
                if (activeSkillUnit.additionalOddsActionUnitList != null) {
                    AdditionalActionUnit[] additionalOddsActionUnitList;
                    for (int length = (additionalOddsActionUnitList = activeSkillUnit.additionalOddsActionUnitList).length, k = 0; k < length; ++k) {
                        AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList[k];
                        if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                            this.additionalSkillUnitActive(_player, target3, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                        } else if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                            SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                            EffectServiceImpl.getInstance().appendSkillEffect(_player, target3, additionalActionUnit.skillOrEffectUnitID);
                        }
                    }
                }
            }
        } else if (_activeSkill.addSkillUnit != null) {
            for (final ME2GameObject target3 : _targetList) {
                this.additionalSkillUnitActive(_player, target3, (ActiveSkillUnit) _activeSkill.addSkillUnit, 1, 1.0f);
                if (activeSkillUnit.additionalOddsActionUnitList != null) {
                    AdditionalActionUnit[] additionalOddsActionUnitList2;
                    for (int length2 = (additionalOddsActionUnitList2 = activeSkillUnit.additionalOddsActionUnitList).length, l = 0; l < length2; ++l) {
                        AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList2[l];
                        if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                            this.additionalSkillUnitActive(_player, target3, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                        } else if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                            SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                            EffectServiceImpl.getInstance().appendSkillEffect(_player, target3, additionalActionUnit.skillOrEffectUnitID);
                        }
                    }
                }
            }
        } else if (activeSkillUnit.additionalOddsActionUnitList != null) {
            for (final ME2GameObject target3 : _targetList) {
                AdditionalActionUnit[] additionalOddsActionUnitList3;
                for (int length3 = (additionalOddsActionUnitList3 = activeSkillUnit.additionalOddsActionUnitList).length, n = 0; n < length3; ++n) {
                    AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList3[n];
                    if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                        this.additionalSkillUnitActive(_player, target3, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                    } else if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                        SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                        EffectServiceImpl.getInstance().appendSkillEffect(_player, target3, additionalActionUnit.skillOrEffectUnitID);
                    }
                }
            }
        }
        for (final ME2GameObject target3 : _targetList) {
            EffectServiceImpl.getInstance().checkTouchEffect(target3, _player, ETouchType.ATTACK_BY_MAGIC, true);
            this.checkTouchSkill(target3, _player, ETouchType.ATTACK_BY_MAGIC, true);
            if (target3.getObjectType() == EObjectType.PLAYER) {
                _player.refreshPvPFightTime(((HeroPlayer) target3).getUserID());
                ((HeroPlayer) target3).refreshPvPFightTime(_player.getUserID());
            }
        }
    }

    private void groupMagicAttackSkill(final HeroPlayer _player, final ArrayList<ME2GameObject> _targetList, final ActiveSkill _activeSkill) {
        ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
        this.sendGroupAttackSkillAnimation(_player, _targetList, activeSkillUnit.releaseAnimationID, activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, activeSkillUnit.activeImageID, activeSkillUnit.animationAction, activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, activeSkillUnit.heightRelation, activeSkillUnit.isDirection);
        EffectServiceImpl.getInstance().checkTouchEffect(_player, null, ETouchType.USE_MAGIC, true);
        this.checkTouchSkill(_player, null, ETouchType.USE_MAGIC, true);
        if (activeSkillUnit.magicHarmHpValue > 0) {
            int magicAttack = CEService.magicHarmBySkill((float) (int) _player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(activeSkillUnit.magicHarmType), activeSkillUnit.magicHarmHpValue, activeSkillUnit.releaseTime, _player.getLevel(), _activeSkill.level);
            if (activeSkillUnit.isAverageResult) {
                int harmValue = CEService.attackMagicHarm(_player.getLevel(), magicAttack, _player.getLevel(), 0);
                harmValue += (int) (harmValue * _player.getActualProperty().getAdditionalMagicHarmScale(activeSkillUnit.magicHarmType));
                harmValue += (int) _player.getActualProperty().getAdditionalMagicHarm(activeSkillUnit.magicHarmType);
                harmValue /= _targetList.size();
                int i = 0;
                while (i < _targetList.size()) {
                    ME2GameObject target = _targetList.get(i);
                    harmValue += (int) (harmValue * target.getActualProperty().getAdditionalMagicHarmScaleBeAttack(activeSkillUnit.magicHarmType));
                    harmValue += (int) target.getActualProperty().getAdditionalMagicHarmBeAttack(activeSkillUnit.magicHarmType);
                    if (FightServiceImpl.getInstance().processReduceHp(_player, target, harmValue, true, false, activeSkillUnit.magicHarmType)) {
                        _targetList.remove(i);
                    } else {
                        ++i;
                    }
                }
            } else {
                int j = 0;
                while (j < _targetList.size()) {
                    ME2GameObject target2 = _targetList.get(j);
                    if (activeSkillUnit.needMagicHit && SkillServiceImpl.RANDOM.nextInt(100) > CEService.attackMagicHitOdds(_player.getActualProperty().getLucky(), _player.getActualProperty().getHitLevel(), _player.getLevel(), target2.getLevel(), target2.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(activeSkillUnit.magicHarmType))) {
                        FightServiceImpl.getInstance().processMiss(_player, target2);
                        ++j;
                    } else {
                        int harmValue = CEService.attackMagicHarm(_player.getLevel(), magicAttack, target2.getLevel(), target2.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(activeSkillUnit.magicHarmType));
                        boolean isDeathblow = false;
                        if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.attackPhysicsDeathblowOdds(_player.getActualProperty().getAgility(), _player.getActualProperty().getPhysicsDeathblowLevel(), _player.getLevel(), target2.getLevel()) - target2.getResistOddsList().physicsDeathblowOdds) {
                            harmValue = CEService.calculateDeathblowHarm(harmValue, _player.getActualProperty().getLucky());
                            isDeathblow = true;
                            EffectServiceImpl.getInstance().checkTouchEffect(_player, target2, ETouchType.TOUCH_DEATHBLOW_BY_MAGIC, true);
                            EffectServiceImpl.getInstance().checkTouchEffect(target2, _player, ETouchType.TOUCH_DEATHBLOW_BY_MAGIC, false);
                        }
                        harmValue += (int) (harmValue * _player.getActualProperty().getAdditionalMagicHarmScale(activeSkillUnit.magicHarmType));
                        harmValue += (int) _player.getActualProperty().getAdditionalMagicHarm(activeSkillUnit.magicHarmType);
                        harmValue += (int) (harmValue * target2.getActualProperty().getAdditionalMagicHarmScaleBeAttack(activeSkillUnit.magicHarmType));
                        harmValue += (int) target2.getActualProperty().getAdditionalMagicHarmBeAttack(activeSkillUnit.magicHarmType);
                        EffectServiceImpl.getInstance().checkTouchEffect(_player, target2, ETouchType.ATTACK_BY_MAGIC, true);
                        this.checkTouchSkill(_player, target2, ETouchType.ATTACK_BY_MAGIC, true);
                        if (FightServiceImpl.getInstance().processReduceHp(_player, target2, harmValue, true, isDeathblow, activeSkillUnit.magicHarmType)) {
                            _targetList.remove(j);
                        } else {
                            EffectServiceImpl.getInstance().checkTouchEffect(target2, _player, ETouchType.USE_MAGIC, true);
                            this.checkTouchSkill(target2, _player, ETouchType.USE_MAGIC, true);
                            ++j;
                        }
                    }
                }
            }
        }
        if (_activeSkill.addEffectUnit != null) {
            for (final ME2GameObject target3 : _targetList) {
                EffectServiceImpl.getInstance().appendSkillEffect(_player, target3, _activeSkill.addEffectUnit);
                if (activeSkillUnit.additionalOddsActionUnitList != null) {
                    AdditionalActionUnit[] additionalOddsActionUnitList;
                    for (int length = (additionalOddsActionUnitList = activeSkillUnit.additionalOddsActionUnitList).length, k = 0; k < length; ++k) {
                        AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList[k];
                        if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                            this.additionalSkillUnitActive(_player, target3, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                        } else if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                            SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                            EffectServiceImpl.getInstance().appendSkillEffect(_player, target3, additionalActionUnit.skillOrEffectUnitID);
                        }
                    }
                }
            }
        } else if (_activeSkill.addSkillUnit != null) {
            for (final ME2GameObject target3 : _targetList) {
                this.additionalSkillUnitActive(_player, target3, (ActiveSkillUnit) _activeSkill.addSkillUnit, 1, 1.0f);
                if (activeSkillUnit.additionalOddsActionUnitList != null) {
                    AdditionalActionUnit[] additionalOddsActionUnitList2;
                    for (int length2 = (additionalOddsActionUnitList2 = activeSkillUnit.additionalOddsActionUnitList).length, l = 0; l < length2; ++l) {
                        AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList2[l];
                        if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                            this.additionalSkillUnitActive(_player, target3, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                        } else if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                            SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                            EffectServiceImpl.getInstance().appendSkillEffect(_player, target3, additionalActionUnit.skillOrEffectUnitID);
                        }
                    }
                }
            }
        } else if (activeSkillUnit.additionalOddsActionUnitList != null) {
            for (final ME2GameObject target3 : _targetList) {
                AdditionalActionUnit[] additionalOddsActionUnitList3;
                for (int length3 = (additionalOddsActionUnitList3 = activeSkillUnit.additionalOddsActionUnitList).length, n = 0; n < length3; ++n) {
                    AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList3[n];
                    if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                        this.additionalSkillUnitActive(_player, target3, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                    } else if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                        SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                        EffectServiceImpl.getInstance().appendSkillEffect(_player, target3, additionalActionUnit.skillOrEffectUnitID);
                    }
                }
            }
        }
        for (final ME2GameObject target3 : _targetList) {
            EffectServiceImpl.getInstance().checkTouchEffect(target3, _player, ETouchType.ATTACK_BY_MAGIC, true);
            this.checkTouchSkill(target3, _player, ETouchType.ATTACK_BY_MAGIC, true);
            if (target3.getObjectType() == EObjectType.PLAYER) {
                _player.refreshPvPFightTime(((HeroPlayer) target3).getUserID());
                ((HeroPlayer) target3).refreshPvPFightTime(_player.getUserID());
            }
        }
    }

    public void additionalSkillUnitActive(final HeroPlayer _player, final ME2GameObject _target, final ActiveSkillUnit _activeSkillUnit, final int _totalTimes, final float _odds) {
        if (_activeSkillUnit.activeSkillType == EActiveSkillType.PHYSICS) {
            if (_activeSkillUnit.targetType == ETargetType.ENEMY) {
                if (_activeSkillUnit.targetRangeType == ETargetRangeType.SINGLE) {
                    if ((_activeSkillUnit.physicsHarmValue > 0 || _activeSkillUnit.weaponHarmMult > 0.0f) && SkillServiceImpl.RANDOM.nextFloat() <= _odds) {
                        int attack = 0;
                        int harmValue = 0;
                        if (_activeSkillUnit.weaponHarmMult > 0.0f) {
                            EquipmentInstance weapon = _player.getBodyWear().getWeapon();
                            if (weapon != null) {
                                attack = CEService.weaponPhysicsAttackBySkill(_player.getActualProperty().getActualPhysicsAttack(), _activeSkillUnit.weaponHarmMult, _activeSkillUnit.physicsHarmValue);
                            }
                        }
                        harmValue = CEService.physicsHarm(_player.getLevel(), attack, _target.getLevel(), _target.getActualProperty().getDefense());
                        harmValue *= (int) _target.getActualProperty().getAdditionalHarmScaleBePhysicsAttack();
                        harmValue += _target.getActualProperty().getAdditionalHarmValueBePhysicsAttack();
                        FightServiceImpl.getInstance().processReduceHp(_player, _target, harmValue, true, false, null);
                        for (int times = 0; times < _totalTimes - 1; ++times) {
                            if (!_target.isDead() && SkillServiceImpl.RANDOM.nextFloat() <= _odds) {
                                FightServiceImpl.getInstance().processReduceHp(_player, _target, harmValue, true, false, null);
                            }
                        }
                    }
                } else if (_activeSkillUnit.targetRangeType == ETargetRangeType.SOME) {
                    ME2GameObject rangeBaseLintTarget = (_activeSkillUnit.rangeBaseLine == EAOERangeBaseLine.RELEASER) ? _player : _target;
                    ArrayList<ME2GameObject> targetList;
                    if (_activeSkillUnit.rangeMode == EAOERangeType.CENTER) {
                        targetList = MapServiceImpl.getInstance().getAttackableObjectListInRange(rangeBaseLintTarget.where(), rangeBaseLintTarget.getCellX(), rangeBaseLintTarget.getCellY(), _activeSkillUnit.rangeX, _player, _activeSkillUnit.rangeTargetNumber);
                    } else {
                        targetList = MapServiceImpl.getInstance().getAttackableObjectListInForeRange(rangeBaseLintTarget.where(), _activeSkillUnit.rangeX, _activeSkillUnit.rangeY, _player, _activeSkillUnit.rangeTargetNumber);
                    }
                    if (targetList != null && targetList.size() > 0) {
                        for (final ME2GameObject target : targetList) {
                            if (_activeSkillUnit.physicsHarmValue > 0 || _activeSkillUnit.weaponHarmMult > 0.0f) {
                                int attack2 = 0;
                                int harmValue2 = 0;
                                if (_activeSkillUnit.weaponHarmMult > 0.0f) {
                                    EquipmentInstance weapon2 = _player.getBodyWear().getWeapon();
                                    if (weapon2 != null) {
                                        attack2 += CEService.weaponPhysicsAttackBySkill(_player.getActualProperty().getActualPhysicsAttack(), _activeSkillUnit.weaponHarmMult, _activeSkillUnit.physicsHarmValue);
                                    }
                                }
                                attack2 += _activeSkillUnit.physicsHarmValue;
                                harmValue2 = CEService.physicsHarm(_player.getLevel(), attack2, _target.getLevel(), _target.getActualProperty().getDefense());
                                harmValue2 *= (int) target.getActualProperty().getAdditionalHarmScaleBePhysicsAttack();
                                harmValue2 += target.getActualProperty().getAdditionalHarmValueBePhysicsAttack();
                                FightServiceImpl.getInstance().processReduceHp(_player, target, harmValue2, true, false, null);
                            }
                        }
                    }
                }
            } else if (_activeSkillUnit.targetType == ETargetType.MYSELF) {
                if (_activeSkillUnit.resumeHp != 0) {
                    FightServiceImpl.getInstance().processHpChange(_player, _player, _activeSkillUnit.resumeHp, false, null);
                }
                if (_activeSkillUnit.resumeMp != 0) {
                    _player.addMp(_activeSkillUnit.resumeMp);
                    FightServiceImpl.getInstance().processSingleTargetMpChange(_player, true);
                }
            } else if (_activeSkillUnit.targetType == ETargetType.FRIEND) {
                if (_activeSkillUnit.targetRangeType == ETargetRangeType.SINGLE) {
                    if (_activeSkillUnit.resumeHp != 0) {
                        FightServiceImpl.getInstance().processHpChange(_player, _target, _activeSkillUnit.resumeHp, false, null);
                    }
                    if (_activeSkillUnit.resumeMp != 0) {
                        _target.addMp(_activeSkillUnit.resumeMp);
                        FightServiceImpl.getInstance().processSingleTargetMpChange(_target, true);
                    }
                } else {
                    ME2GameObject rangeBaseLintTarget2 = (_activeSkillUnit.rangeBaseLine == EAOERangeBaseLine.RELEASER) ? _player : _target;
                    ArrayList<HeroPlayer> targetList2;
                    if (_activeSkillUnit.targetRangeType == ETargetRangeType.TEAM) {
                        if (_activeSkillUnit.rangeMode == EAOERangeType.CENTER) {
                            targetList2 = MapServiceImpl.getInstance().getGroupPlayerInRange((HeroPlayer) rangeBaseLintTarget2, rangeBaseLintTarget2.getCellX(), rangeBaseLintTarget2.getCellY(), _activeSkillUnit.rangeX, _activeSkillUnit.rangeTargetNumber);
                        } else {
                            targetList2 = MapServiceImpl.getInstance().getGroupPlayerListInForeRange((HeroPlayer) rangeBaseLintTarget2, _activeSkillUnit.rangeX, _activeSkillUnit.rangeY, _activeSkillUnit.rangeTargetNumber);
                        }
                    } else if (_activeSkillUnit.rangeMode == EAOERangeType.CENTER) {
                        targetList2 = MapServiceImpl.getInstance().getFriendsPlayerInRange((HeroPlayer) rangeBaseLintTarget2, rangeBaseLintTarget2.getCellX(), rangeBaseLintTarget2.getCellY(), _activeSkillUnit.rangeX, _activeSkillUnit.rangeTargetNumber);
                    } else {
                        targetList2 = MapServiceImpl.getInstance().getFriendsPlayerListInForeRange((HeroPlayer) rangeBaseLintTarget2, _activeSkillUnit.rangeX, _activeSkillUnit.rangeY, _activeSkillUnit.rangeTargetNumber);
                    }
                    if (targetList2 != null && targetList2.size() > 0) {
                        for (final HeroPlayer player : targetList2) {
                            if (_activeSkillUnit.resumeHp != 0) {
                                FightServiceImpl.getInstance().processHpChange(_player, player, _activeSkillUnit.resumeHp, false, null);
                            }
                            if (_activeSkillUnit.resumeMp != 0) {
                                player.addMp(_activeSkillUnit.resumeMp);
                                FightServiceImpl.getInstance().processSingleTargetMpChange(player, true);
                            }
                        }
                    }
                }
            }
        }
    }

    private void singleResumeSkill(final Pet _pet, final ME2GameObject _target, final PetActiveSkill _activeSkill) {
    }

    private void singleResumeSkill(final HeroPlayer _player, final ME2GameObject _target, final ActiveSkill _activeSkill) {
        ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
        this.sendSingleSkillAnimation(_player, _target, activeSkillUnit.releaseAnimationID, activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, activeSkillUnit.activeImageID, activeSkillUnit.animationAction, activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, activeSkillUnit.heightRelation, activeSkillUnit.isDirection);
        if (activeSkillUnit.resumeHp > 0) {
            int resumeHpValue = CEService.magicResume(activeSkillUnit.resumeHp, _player.getActualProperty().getSpirit(), _player.getActualProperty().getInte(), _player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SANCTITY), activeSkillUnit.releaseTime);
            boolean isDeathblow = false;
            if (SkillServiceImpl.RANDOM.nextInt(100) <= _player.getActualProperty().getMagicDeathblowOdds()) {
                resumeHpValue = CEService.calculateDeathblowHarm(resumeHpValue, _player.getActualProperty().getLucky());
                isDeathblow = true;
                EffectServiceImpl.getInstance().checkTouchEffect(_player, null, ETouchType.TOUCH_DEATHBLOW_BY_MAGIC, true);
            }
            FightServiceImpl.getInstance().processAddHp(_player, _target, resumeHpValue, true, isDeathblow);
        }
        if (activeSkillUnit.resumeMp > 0) {
            _target.addMp(activeSkillUnit.resumeMp);
            FightServiceImpl.getInstance().processSingleTargetMpChange(_target, true);
        }
        if (_activeSkill.addEffectUnit != null) {
            EffectServiceImpl.getInstance().appendSkillEffect(_player, _target, _activeSkill.addEffectUnit);
            if (_activeSkill.addEffectUnit != null && _activeSkill.addEffectUnit instanceof StaticEffect) {
                StaticEffect se = (StaticEffect) _activeSkill.addEffectUnit;
                if (se.allSkillReleaseTime > 0.0f) {
                    RefreshSkillTime msg = new RefreshSkillTime(_player);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                }
            }
        } else if (_activeSkill.addSkillUnit != null) {
            this.additionalSkillUnitActive(_player, _target, (ActiveSkillUnit) _activeSkill.addSkillUnit, 1, 1.0f);
        }
        if (activeSkillUnit.additionalOddsActionUnitList != null) {
            AdditionalActionUnit[] additionalOddsActionUnitList;
            for (int length = (additionalOddsActionUnitList = activeSkillUnit.additionalOddsActionUnitList).length, i = 0; i < length; ++i) {
                AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList[i];
                if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                    this.additionalSkillUnitActive(_player, _target, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                } else if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                    SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                    EffectServiceImpl.getInstance().appendSkillEffect(_player, _target, additionalActionUnit.skillOrEffectUnitID);
                }
            }
        }
        EffectServiceImpl.getInstance().checkTouchEffect(_player, _target, ETouchType.RESUME_BY_MAGIC, true);
        this.checkTouchSkill(_player, _target, ETouchType.RESUME_BY_MAGIC, true);
        if (activeSkillUnit.cleanEffectFeature != null) {
            if (SkillServiceImpl.RANDOM.nextFloat() <= activeSkillUnit.cleanEffectOdds) {
                EffectServiceImpl.getInstance().cleanEffect(_player, _target, activeSkillUnit.cleanEffectFeature, activeSkillUnit.cleanEffectMaxLevel, activeSkillUnit.cleanEffectNumberPerTimes);
            }
        } else if (activeSkillUnit.cleanDetailEffectLowerID > 0 && activeSkillUnit.cleandetailEffectUperID > 0 && SkillServiceImpl.RANDOM.nextFloat() <= activeSkillUnit.cleanEffectOdds) {
            EffectServiceImpl.getInstance().cleanEffect(_player, _target, activeSkillUnit.cleanDetailEffectLowerID, activeSkillUnit.cleandetailEffectUperID);
        }
    }

    private void groupResumeSkill(final HeroPlayer _player, final ArrayList<HeroPlayer> _targetList, final ActiveSkill _activeSkill) {
        ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
        ArrayList<ME2GameObject> targetList = new ArrayList<ME2GameObject>(_targetList.size());
        for (final HeroPlayer target : _targetList) {
            targetList.add(target);
            if (activeSkillUnit.resumeHp > 0) {
                int resumeHpValue = CEService.magicResume(activeSkillUnit.resumeHp, _player.getActualProperty().getSpirit(), _player.getActualProperty().getInte(), _player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SANCTITY), activeSkillUnit.releaseTime);
                boolean isDeathblow = false;
                if (SkillServiceImpl.RANDOM.nextInt(100) <= _player.getActualProperty().getMagicDeathblowOdds()) {
                    resumeHpValue = CEService.calculateDeathblowHarm(resumeHpValue, _player.getActualProperty().getLucky());
                    isDeathblow = true;
                    EffectServiceImpl.getInstance().checkTouchEffect(_player, null, ETouchType.TOUCH_DEATHBLOW_BY_MAGIC, true);
                }
                FightServiceImpl.getInstance().processAddHp(_player, target, resumeHpValue, true, isDeathblow);
            }
            if (activeSkillUnit.resumeMp > 0) {
                target.addMp(activeSkillUnit.resumeMp);
                FightServiceImpl.getInstance().processSingleTargetMpChange(target, true);
            }
            EffectServiceImpl.getInstance().checkTouchEffect(_player, null, ETouchType.RESUME_BY_MAGIC, true);
            this.checkTouchSkill(_player, null, ETouchType.RESUME_BY_MAGIC, true);
            if (activeSkillUnit.cleanEffectFeature != null) {
                if (SkillServiceImpl.RANDOM.nextFloat() > activeSkillUnit.cleanEffectOdds) {
                    continue;
                }
                EffectServiceImpl.getInstance().cleanEffect(_player, target, activeSkillUnit.cleanEffectFeature, activeSkillUnit.cleanEffectMaxLevel, activeSkillUnit.cleanEffectNumberPerTimes);
            } else {
                if (activeSkillUnit.cleanDetailEffectLowerID <= 0 || activeSkillUnit.cleandetailEffectUperID <= 0 || SkillServiceImpl.RANDOM.nextFloat() > activeSkillUnit.cleanEffectOdds) {
                    continue;
                }
                EffectServiceImpl.getInstance().cleanEffect(_player, target, activeSkillUnit.cleanDetailEffectLowerID, activeSkillUnit.cleandetailEffectUperID);
            }
        }
        if (_activeSkill.addEffectUnit != null) {
            for (final ME2GameObject target2 : _targetList) {
                EffectServiceImpl.getInstance().appendSkillEffect(_player, target2, _activeSkill.addEffectUnit);
                if (activeSkillUnit.additionalOddsActionUnitList != null) {
                    AdditionalActionUnit[] additionalOddsActionUnitList;
                    for (int length = (additionalOddsActionUnitList = activeSkillUnit.additionalOddsActionUnitList).length, i = 0; i < length; ++i) {
                        AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList[i];
                        if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                            this.additionalSkillUnitActive(_player, target2, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                        } else if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                            SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                            EffectServiceImpl.getInstance().appendSkillEffect(_player, target2, additionalActionUnit.skillOrEffectUnitID);
                        }
                    }
                }
            }
        } else if (_activeSkill.addSkillUnit != null) {
            for (final ME2GameObject target2 : _targetList) {
                this.additionalSkillUnitActive(_player, target2, (ActiveSkillUnit) _activeSkill.addSkillUnit, 1, 1.0f);
                if (activeSkillUnit.additionalOddsActionUnitList != null) {
                    AdditionalActionUnit[] additionalOddsActionUnitList2;
                    for (int length2 = (additionalOddsActionUnitList2 = activeSkillUnit.additionalOddsActionUnitList).length, j = 0; j < length2; ++j) {
                        AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList2[j];
                        if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                            this.additionalSkillUnitActive(_player, target2, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                        } else if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                            SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                            EffectServiceImpl.getInstance().appendSkillEffect(_player, target2, additionalActionUnit.skillOrEffectUnitID);
                        }
                    }
                }
            }
        } else if (activeSkillUnit.additionalOddsActionUnitList != null) {
            for (final ME2GameObject target2 : _targetList) {
                AdditionalActionUnit[] additionalOddsActionUnitList3;
                for (int length3 = (additionalOddsActionUnitList3 = activeSkillUnit.additionalOddsActionUnitList).length, k = 0; k < length3; ++k) {
                    AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList3[k];
                    if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                        this.additionalSkillUnitActive(_player, target2, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                    } else if (SkillServiceImpl.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                        SkillServiceImpl.log.info((Object) ("CEService.oddsFormat-->" + CEService.oddsFormat(additionalActionUnit.activeOdds)));
                        EffectServiceImpl.getInstance().appendSkillEffect(_player, target2, additionalActionUnit.skillOrEffectUnitID);
                    }
                }
            }
        }
        this.sendGroupAttackSkillAnimation(_player, targetList, activeSkillUnit.releaseAnimationID, activeSkillUnit.releaseImageID, activeSkillUnit.activeAnimationID, activeSkillUnit.activeImageID, activeSkillUnit.animationAction, activeSkillUnit.tierRelation, activeSkillUnit.releaseHeightRelation, activeSkillUnit.heightRelation, activeSkillUnit.isDirection);
    }

    public void checkTouchSkill(final ME2GameObject _host, final ME2GameObject _target, final ETouchType _activeTouchType, final boolean _isSkillTouch) {
        ArrayList<PassiveSkill> passiveList = null;
        if (_host instanceof HeroPlayer) {
            passiveList = ((HeroPlayer) _host).passiveSkillList;
            if (passiveList != null && passiveList.size() > 0) {
                for (int i = 0; i < passiveList.size(); ++i) {
                    PassiveSkillUnit passiveSkillUnit;
                    try {
                        passiveSkillUnit = (PassiveSkillUnit) passiveList.get(i).skillUnit;
                    } catch (Exception e) {
                        SkillServiceImpl.log.error((Object) "\u6280\u80fd\u5f3a\u8f6c\u4e3a\u88ab\u52a8\u6280\u80fd\u5931\u8d25", (Throwable) e);
                        break;
                    }
                    if (!(passiveSkillUnit instanceof EnhanceSkillUnit)) {
                        passiveSkillUnit.touch(_host, _target, _activeTouchType, _isSkillTouch);
                    }
                }
            }
        }
    }

    private boolean canReleaseSkill(final HeroPlayer _releaser, final ME2GameObject _target, final ActiveSkill _activeSkill) {
        if (_activeSkill.reduceCoolDownTime > 0) {
            ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u6280\u80fd\u51b7\u5374\u4e2d"));
            return false;
        }
        ActiveSkillUnit skillUnit = (ActiveSkillUnit) _activeSkill.skillUnit;
        if (ETargetType.DIER == skillUnit.targetType) {
            if (!_target.isDead() || _releaser.getClan() != _target.getClan()) {
                ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u4e0d\u8981\u5bf9\u8fd9\u4e2a\u76ee\u6807\u6709\u975e\u5206\u4e4b\u60f3"));
                return false;
            }
        } else if (ETargetType.ENEMY == skillUnit.targetType && (ETargetRangeType.SINGLE == skillUnit.targetRangeType || (ETargetRangeType.SOME == skillUnit.targetRangeType && EAOERangeBaseLine.TARGET == skillUnit.rangeBaseLine)) && _releaser.getClan() == _target.getClan()) {
            if (_target.getObjectType() == EObjectType.PLAYER) {
                if (!DuelServiceImpl.getInstance().isDueling(_releaser.getUserID(), ((HeroPlayer) _target).getUserID())) {
                    ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u4e0d\u8981\u5bf9\u8fd9\u4e2a\u76ee\u6807\u6709\u975e\u5206\u4e4b\u60f3"));
                    return false;
                }
            } else if (_target.getObjectType() == EObjectType.MONSTER) {
                ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u4e0d\u8981\u5bf9\u8fd9\u4e2a\u76ee\u6807\u6709\u975e\u5206\u4e4b\u60f3"));
                return false;
            }
        }
        double added = 0.0;
        if (_target instanceof Monster) {
            MonsterImageConfDict.Config monsterConfig = MonsterImageConfDict.get(((Monster) _target).getImageID());
            added = monsterConfig.grid / 2;
        }
        boolean outDistance = (_releaser.getCellX() - _target.getCellX()) * (_releaser.getCellX() - _target.getCellX()) + (_releaser.getCellY() - _target.getCellY()) * (_releaser.getCellY() - _target.getCellY()) > (skillUnit.targetDistance + added) * (skillUnit.targetDistance + added);
        if (ETargetType.ENEMY == skillUnit.targetType) {
            if ((ETargetRangeType.SINGLE == skillUnit.targetRangeType || (ETargetRangeType.SOME == skillUnit.targetRangeType && EAOERangeBaseLine.TARGET == skillUnit.rangeBaseLine)) && outDistance) {
                ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u60a8\u8fd9\u79bb\u5f97\u4e5f\u592a\u8fdc\u4e86"));
                return false;
            }
        } else if (ETargetType.FRIEND == skillUnit.targetType && EObjectType.PLAYER == _target.getObjectType() && !DuelServiceImpl.getInstance().isDueling(_releaser.getUserID(), ((HeroPlayer) _target).getUserID()) && (ETargetRangeType.SINGLE == skillUnit.targetRangeType || (ETargetRangeType.SOME == skillUnit.targetRangeType && EAOERangeBaseLine.TARGET == skillUnit.rangeBaseLine)) && outDistance) {
            ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u60a8\u8fd9\u79bb\u5f97\u4e5f\u592a\u8fdc\u4e86"));
            return false;
        }
        if (_activeSkill.onlyNotFightingStatus && _releaser.isInFighting()) {
            ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u6253\u67b6\u7684\u65f6\u5019\u53ef\u4e0d\u80fd\u7528\u8fd9\u4e2a"));
            return false;
        }
        if (!_releaser.canReleaseMagicSkill()) {
            ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u672a\u6ee1\u8db3\u6280\u80fd\u65bd\u653e\u6761\u4ef6"));
            return false;
        }
        if (_activeSkill.needWeaponType != null) {
            EquipmentInstance weaponInstance = _releaser.getBodyWear().getWeapon();
            if (weaponInstance == null) {
                return false;
            }
            boolean weaponType = false;
            for (int i = 0; i < _activeSkill.needWeaponType.size(); ++i) {
                if (((Weapon) weaponInstance.getArchetype()).getWeaponType() == _activeSkill.needWeaponType.get(i)) {
                    weaponType = true;
                    break;
                }
            }
            if (!weaponType) {
                ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u60a8\u624b\u4e2d\u7684\u6b66\u5668\u65e0\u6cd5\u4f7f\u7528\u8be5\u6280\u80fd"));
                return false;
            }
        }
        if (_activeSkill.hpConditionTargetType != null) {
            if (ETargetType.MYSELF == _activeSkill.hpConditionTargetType) {
                if (_activeSkill.hpConditionCompareLine == 1) {
                    if (_releaser.getHPPercent() < _activeSkill.hpConditionPercent) {
                        ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u672a\u6ee1\u8db3\u6280\u80fd\u65bd\u653e\u6761\u4ef6"));
                        return false;
                    }
                } else if (_releaser.getHPPercent() > _activeSkill.hpConditionPercent) {
                    ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u672a\u6ee1\u8db3\u6280\u80fd\u65bd\u653e\u6761\u4ef6"));
                    return false;
                }
            } else {
                if (ETargetType.TARGET != _activeSkill.hpConditionTargetType) {
                    return false;
                }
                if (_activeSkill.hpConditionCompareLine == 1) {
                    if (_target.getHPPercent() < _activeSkill.hpConditionPercent) {
                        ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u672a\u6ee1\u8db3\u6280\u80fd\u65bd\u653e\u6761\u4ef6"));
                        return false;
                    }
                } else if (_target.getHPPercent() > _activeSkill.hpConditionPercent) {
                    ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u672a\u6ee1\u8db3\u6280\u80fd\u65bd\u653e\u6761\u4ef6"));
                    return false;
                }
            }
        }
        boolean confirmEffectCondition = false;
        if (_activeSkill.releaserExistsEffectName != null) {
            for (final Effect effect : _releaser.effectList) {
                if (effect.name.equals(_activeSkill.releaserExistsEffectName)) {
                    confirmEffectCondition = true;
                    break;
                }
            }
            if (!confirmEffectCondition) {
                ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u9700\u8981\u81ea\u8eab\u6548\u679c\uff1a" + _activeSkill.releaserExistsEffectName));
                return false;
            }
        } else if (_activeSkill.releaserUnexistsEffectName != null) {
            confirmEffectCondition = true;
            for (final Effect effect : _releaser.effectList) {
                if (effect.name.equals(_activeSkill.releaserUnexistsEffectName)) {
                    confirmEffectCondition = false;
                    break;
                }
            }
            if (!confirmEffectCondition) {
                ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u9700\u8981\u81ea\u8eab\u65e0\uff1a" + _activeSkill.releaserUnexistsEffectName));
                return false;
            }
        } else if (_activeSkill.targetExistsEffectName != null) {
            confirmEffectCondition = false;
            if (_target.effectList.size() > 0) {
                for (int j = 0; j < _target.effectList.size(); ++j) {
                    try {
                        if (!_target.effectList.get(j).name.equals(_activeSkill.targetExistsEffectName)) {
                            continue;
                        }
                        confirmEffectCondition = true;
                    } catch (Exception e) {
                        confirmEffectCondition = true;
                    }
                    break;
                }
            }
            if (!confirmEffectCondition) {
                ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u9700\u8981\u76ee\u6807\u6548\u679c\uff1a" + _activeSkill.targetExistsEffectName));
                return false;
            }
        } else if (_activeSkill.targetUnexistsEffectName != null) {
            confirmEffectCondition = true;
            if (_target.effectList.size() > 0) {
                for (int j = 0; j < _target.effectList.size(); ++j) {
                    try {
                        if (!_target.effectList.get(j).name.equals(_activeSkill.targetExistsEffectName)) {
                            continue;
                        }
                        confirmEffectCondition = false;
                    } catch (Exception e) {
                        confirmEffectCondition = true;
                    }
                    break;
                }
            }
            if (!confirmEffectCondition) {
                ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u9700\u8981\u76ee\u6807\u65e0\uff1a" + _activeSkill.targetUnexistsEffectName));
                return false;
            }
        }
        if (skillUnit.consumeMp > 0) {
            if (_releaser.getMp() < skillUnit.consumeMp) {
                ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u9b54\u6cd5\u4e0d\u8db3"));
                return false;
            }
            _releaser.addMp(-skillUnit.consumeMp);
            FightServiceImpl.getInstance().processSingleTargetMpChange(_releaser, false);
        } else if (skillUnit.consumeFp > 0) {
            if (_releaser.getForceQuantity() < skillUnit.consumeFp) {
                ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u529b\u503c\u4e0d\u8db3"));
                return false;
            }
            _releaser.consumeForceQuantity(skillUnit.consumeFp);
            FightServiceImpl.getInstance().processPersionalForceQuantityChange(_releaser);
        } else if (skillUnit.consumeGp > 0) {
            if (_releaser.getGasQuantity() < skillUnit.consumeGp) {
                ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u6c14\u503c\u4e0d\u8db3"));
                return false;
            }
            _releaser.consumeGasQuantity(skillUnit.consumeGp);
            FightServiceImpl.getInstance().processPersionalForceQuantityChange(_releaser);
        }
        if (skillUnit.consumeHp > 0) {
            if (_releaser.getHp() < skillUnit.consumeHp) {
                ResponseMessageQueue.getInstance().put(_releaser.getMsgQueueIndex(), new Warning("\u751f\u547d\u4e0d\u8db3"));
                return false;
            }
            _releaser.addHp(-skillUnit.consumeHp);
            FightServiceImpl.getInstance().processPersionalHpChange(_releaser, -skillUnit.consumeHp);
        }
        return true;
    }

    public boolean monsterReleaseSkill(final Monster _releaser, final HeroPlayer _target, final byte _direction, final ActiveSkill _activeSkill) {
        if (!_releaser.isEnable() || _releaser.isDead() || _target == null || !_target.isEnable()) {
            return false;
        }
        ActiveSkill activeSkill = _activeSkill;
        if (activeSkill == null) {
            return false;
        }
        ActiveSkillUnit skillUnit = (ActiveSkillUnit) activeSkill.skillUnit;
        if (skillUnit.activeSkillType == EActiveSkillType.PHYSICS) {
            this.singlePhysicsAttackSkill(_releaser, _target, activeSkill);
        } else {
            this.singleMagicAttackSkill(_releaser, _target, activeSkill);
        }
        return true;
    }

    public void changePropertySkillAction(final HeroPlayer _player, final boolean _isInit) {
        for (final PassiveSkill passiveSkill : _player.passiveSkillList) {
            if (passiveSkill.skillUnit instanceof ChangePropertyUnit && passiveSkill.level > 0) {
                this.enhanceProperty((ChangePropertyUnit) passiveSkill.skillUnit, _player, _isInit);
            }
        }
    }

    public boolean comprehendSkill(final HeroPlayer _player, final int _skillID) {
        return true;
    }

    public ArrayList<Skill> getLearnableSkillList(final ArrayList<Skill> _vocationSkillList, final HeroPlayer _player) {
        ArrayList<Skill> list = null;
        for (final Skill skill : _vocationSkillList) {
            boolean isNewSkill = true;
            if (_player.getLevel() >= skill.learnerLevel) {
                if (ESkillType.ACTIVE == skill.getType()) {
                    for (int i = 0; i < _player.activeSkillList.size(); ++i) {
                        Skill existsSkill = _player.activeSkillList.get(i);
                        if (existsSkill.isSameName(skill)) {
                            if (existsSkill.feature == skill.feature && skill.level - existsSkill.level == 1) {
                                if (list == null) {
                                    list = new ArrayList<Skill>();
                                }
                                list.add(skill);
                            }
                            isNewSkill = false;
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < _player.passiveSkillList.size(); ++i) {
                        Skill existsSkill = _player.passiveSkillList.get(i);
                        if (existsSkill.isSameName(skill)) {
                            if (existsSkill.feature == skill.feature && skill.level - existsSkill.level == 1) {
                                if (list == null) {
                                    list = new ArrayList<Skill>();
                                }
                                list.add(skill);
                            }
                            isNewSkill = false;
                            break;
                        }
                    }
                }
                if (!isNewSkill || skill.level != 1) {
                    continue;
                }
                if (list == null) {
                    list = new ArrayList<Skill>();
                }
                list.add(skill);
            }
        }
        return list;
    }

    public void changeVocationProcess(final HeroPlayer _player) {
        ArrayList<Skill> skills = SkillDict.getInstance().getChangeVocationSkills(_player.getVocation());
        for (final Skill skill : skills) {
            if (skill != null) {
                if (skill instanceof ActiveSkill) {
                    _player.activeSkillList.add((ActiveSkill) skill);
                    _player.activeSkillTable.put(skill.id, (ActiveSkill) skill);
                } else {
                    _player.passiveSkillList.add((PassiveSkill) skill);
                }
            }
        }
        for (final PassiveSkill passiveSkill : _player.passiveSkillList) {
            if (passiveSkill.skillUnit instanceof EnhanceSkillUnit) {
                this.enhanceSkill((EnhanceSkillUnit) passiveSkill.skillUnit, _player, true, false);
            }
        }
        SkillDAO.changeCovation(_player.getUserID(), skills);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new LearnedSkillListNotify(_player));
    }

    public boolean learnSkill(final HeroPlayer _player, final int _skillID) {
        Skill oldSkill = SkillServiceImpl.instance.getSkillIns(_skillID);
        if (oldSkill == null) {
            SkillServiceImpl.log.info((Object) ("\u6839\u636eID\u83b7\u5f97\u6280\u80fd\u4e3anull.ID=" + _skillID));
            return false;
        }
        if (oldSkill.next == null) {
            SkillServiceImpl.log.info((Object) "wrong:\u6280\u80fd\u5df2\u8fbe\u5230\u6700\u9ad8\u7ea7,\u6216\u8005\u8be5\u6280\u80fd\u7b49\u7ea7\u4e0d\u8fde\u8d2f.\u8bf7\u68c0\u67e5xml");
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6280\u80fd\u5df2\u8fbe\u6700\u9ad8\u7b49\u7ea7"));
            return false;
        }
        if (_player.getMoney() < oldSkill.learnFreight) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u91d1\u94b1\u4e0d\u591f"));
            return false;
        }
        if (_player.getLevel() < oldSkill.learnerLevel) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u7b49\u7ea7\u4e0d\u591f"));
            return false;
        }
        if (_player.surplusSkillPoint < oldSkill.skillPoints) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u6280\u80fd\u70b9\u6570\u4e0d\u591f"));
            return false;
        }
        if (oldSkill instanceof ActiveSkill) {
            if (!(oldSkill.next instanceof ActiveSkill)) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25,\u9700\u8981\u52b3\u70e6\u60a8\u518d\u8bd5."));
                SkillServiceImpl.log.info((Object) ("\u6280\u80fd\u7684\u4e0b\u4e00\u7ea7\u4e0d\u662f\u4e3b\u52a8\u6280\u80fd,\u8bf7\u68c0\u67e5EXCEL\u8868\u914d\u7f6e:" + oldSkill.name));
                return false;
            }
            Iterator<ActiveSkill> it = _player.activeSkillList.iterator();
            while (it.hasNext()) {
                if (oldSkill.id == it.next().id) {
                    it.remove();
                }
            }
            _player.activeSkillTable.remove(oldSkill.id);
            if (oldSkill.next == null) {
                SkillServiceImpl.log.info((Object) "");
            }
            _player.activeSkillList.add((ActiveSkill) oldSkill.next);
            _player.activeSkillTable.put(oldSkill.next.id, (ActiveSkill) oldSkill.next);
            for (final PassiveSkill passiveSkill : _player.passiveSkillList) {
                if (passiveSkill.skillUnit instanceof EnhanceSkillUnit) {
                    EnhanceSkillUnit enhanceSkillUnit = (EnhanceSkillUnit) passiveSkill.skillUnit;
                    EnhanceSkillUnit.EnhanceUnit[] enhanceUnitList;
                    for (int length = (enhanceUnitList = enhanceSkillUnit.enhanceUnitList).length, i = 0; i < length; ++i) {
                        EnhanceSkillUnit.EnhanceUnit enhanceUnit = enhanceUnitList[i];
                        this.unitEnhance(enhanceUnit, oldSkill.next, true, false, null);
                    }
                }
            }
        } else {
            Iterator<PassiveSkill> it2 = _player.passiveSkillList.iterator();
            while (it2.hasNext()) {
                if (oldSkill.id == it2.next().id) {
                    it2.remove();
                }
            }
            Skill passive = oldSkill.next;
            if (passive.addEffectUnit != null && passive.addEffectUnit instanceof StaticEffect) {
                StaticEffect se = (StaticEffect) passive.addEffectUnit;
                if (se.allSkillReleaseTime > 0.0f) {
                    RefreshSkillTime msg = new RefreshSkillTime(_player);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                }
            }
            _player.passiveSkillList.add((PassiveSkill) oldSkill.next);
            if (oldSkill.next.skillUnit instanceof EnhanceSkillUnit) {
                if (oldSkill.next.level > 1) {
                    this.enhanceSkill((EnhanceSkillUnit) oldSkill.skillUnit, _player, false, false);
                }
                this.enhanceSkill((EnhanceSkillUnit) oldSkill.next.skillUnit, _player, true, true);
            } else if (oldSkill.next.skillUnit instanceof ChangePropertyUnit && this.enhancePropertySkillUpgrade((ChangePropertyUnit) oldSkill.skillUnit, (ChangePropertyUnit) oldSkill.next.skillUnit, _player)) {
                PlayerServiceImpl.getInstance().reCalculateRoleProperty(_player);
                PlayerServiceImpl.getInstance().refreshRoleProperty(_player);
                MapSynchronousInfoBroadcast.getInstance().put(_player.where(), new RefreshObjectViewValue(_player), true, _player.getID());
            }
        }
        if (SkillDAO.LearnSkill(false, _player.getUserID(), oldSkill.next.id, oldSkill.id)) {
            if (oldSkill.next instanceof ActiveSkill) {
                PlayerServiceImpl.getInstance().upgradeShortcutKeySkill(_player, oldSkill.id, oldSkill.next.id);
            }
            _player.surplusSkillPoint -= oldSkill.skillPoints;
            PlayerServiceImpl.getInstance().addMoney(_player, -oldSkill.learnFreight, 1.0f, 2, "\u5347\u7ea7\u6280\u80fd");
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new SkillUpgradeNotify(oldSkill.next, _player));
            return true;
        }
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u55ef\uff0c\u8fd9\u4e2a\uff0c\u4f60\u61c2\u7684"));
        return false;
    }

    public void forgetSkill(final HeroPlayer _player) {
        try {
            int forgetGoods = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(340029);
            RinseSkill rinse = (RinseSkill) GoodsContents.getGoods(340029);
            if (forgetGoods <= 0) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709%fn,\u662f\u5426\u53bb\u5546\u57ce\u8d2d\u4e70".replaceAll("%fn", rinse.getName()), (byte) 2, (byte) 1));
                return;
            }
            boolean isHaveSkill = false;
            for (int i = 0; i < _player.activeSkillList.size(); ++i) {
                Skill skill = _player.activeSkillList.get(i);
                if (skill.level > 0) {
                    isHaveSkill = true;
                    break;
                }
            }
            if (!isHaveSkill) {
                for (int i = 0; i < _player.passiveSkillList.size(); ++i) {
                    Skill skill = _player.passiveSkillList.get(i);
                    if (skill.level > 0) {
                        isHaveSkill = true;
                        break;
                    }
                }
            }
            if (!isHaveSkill) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u8fd8\u672a\u5b66\u4e60\u6280\u80fd,\u4e0d\u9700\u8981\u6d17\u70b9", (byte) 1));
                return;
            }
            ArrayList<Integer> forgetSkillIDList = new ArrayList<Integer>();
            for (int j = 0; j < _player.activeSkillList.size(); ++j) {
                ActiveSkill activeSkill = _player.activeSkillList.get(j);
                forgetSkillIDList.add(activeSkill.id);
            }
            for (int j = 0; j < _player.passiveSkillList.size(); ++j) {
                PassiveSkill passiveSkill = _player.passiveSkillList.get(j);
                forgetSkillIDList.add(passiveSkill.id);
            }
            if (forgetSkillIDList.size() == 0) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u53ef\u4ee5\u9057\u5fd8\u7684\u6280\u80fd"));
                return;
            }
            SkillDAO.forgetSkill(_player);
            short point = (short) (_player.getLevel() * PlayerServiceImpl.getInstance().getConfig().getUpgradeSkillPoint() - 1);
            point += (short) PlayerServiceImpl.getInstance().getPlayerHeavenBookSkillPoint(_player);
            point += NoviceServiceImpl.getInstance().getConfig().novice_award_skill_point;
            point += (short) PlayerServiceImpl.getInstance().getConfig().init_surplus_skill_point;
            point += (short) PlayerServiceImpl.getInstance().getConfig().forget_skill_back_point;
            _player.surplusSkillPoint = point;
            this.reloadSkillList(_player, null);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new LearnedSkillListNotify(_player));
            PlayerServiceImpl.getInstance().resetSkillShortcutKey(_player);
            PlayerServiceImpl.getInstance().reCalculateRoleProperty(_player);
            PlayerServiceImpl.getInstance().refreshRoleProperty(_player);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ShortcutKeyListNotify(_player));
            MapSynchronousInfoBroadcast.getInstance().put(_player.where(), new RefreshObjectViewValue(_player), true, _player.getID());
            GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getSpecialGoodsBag(), rinse, 1, CauseLog.RINSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadSkillList(final HeroPlayer _player, final ArrayList<int[]> _skillInfoList) {
        _player.activeSkillList.clear();
        _player.activeSkillTable.clear();
        _player.passiveSkillList.clear();
        ArrayList<Skill> skills = SkillDict.getInstance().getSkillsByVocation(_player.getVocation());
        for (final Skill skill : skills) {
            if (skill != null) {
                if (skill instanceof ActiveSkill) {
                    _player.activeSkillList.add((ActiveSkill) skill);
                    _player.activeSkillTable.put(skill.id, (ActiveSkill) skill);
                } else {
                    _player.passiveSkillList.add((PassiveSkill) skill);
                }
            }
        }
        for (final PassiveSkill passiveSkill : _player.passiveSkillList) {
            if (passiveSkill.skillUnit instanceof EnhanceSkillUnit) {
                this.enhanceSkill((EnhanceSkillUnit) passiveSkill.skillUnit, _player, true, false);
            }
        }
    }

    private boolean unitEnhance(final EnhanceSkillUnit.EnhanceUnit enhanceUnit, final Skill _skill, final boolean _isEnhance, final boolean _needNotifyClient, final HeroPlayer _player) {
        boolean enhanced = false;
        if (_skill.skillUnit instanceof EnhanceSkillUnit) {
            return false;
        }
        if (EnhanceSkillUnit.EnhanceDataType.SKILL == enhanceUnit.skillDataType) {
            SkillServiceImpl.log.info((Object) ("enhanceUnit.skillName == " + enhanceUnit.skillName));
            SkillServiceImpl.log.info((Object) ("_skill.name == " + _skill.name));
            if (enhanceUnit.skillName.equals(_skill.name) && enhanceUnit.dataField == EnhanceSkillUnit.SkillDataField.COOL_DOWN) {
                float value = this.valueChanged((float) ((ActiveSkill) this.getSkillModel(_skill.id)).coolDownTime, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                switch (enhanceUnit.dataField) {
                    case COOL_DOWN: {
                        if (!_isEnhance) {
                            value = -value;
                        }
                        ActiveSkill activeSkill = (ActiveSkill) _skill;
                        activeSkill.coolDownTime += (int) value;
                        enhanced = true;
                        break;
                    }
                    case NEED_WEAPON: {
                        String temp = "";
                        for (int i = 0; i < enhanceUnit.changeString.length; ++i) {
                            temp = enhanceUnit.changeString[i];
                            ((ActiveSkill) _skill).needWeaponType.add(Weapon.EWeaponType.getType(temp));
                        }
                        enhanced = true;
                        break;
                    }
                }
                if (_needNotifyClient) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new UpdateActiveSkillNotify((ActiveSkill) _skill));
                }
            }
        } else if (EnhanceSkillUnit.EnhanceDataType.SKILL_UNIT == enhanceUnit.skillDataType) {
            SkillUnit skillUnit = null;
            if (enhanceUnit.skillName.equals(_skill.skillUnit.name)) {
                skillUnit = _skill.skillUnit;
            } else if (_skill.addSkillUnit != null && enhanceUnit.skillName.equals(_skill.addSkillUnit.name)) {
                skillUnit = _skill.addSkillUnit;
            }
            if (skillUnit != null) {
                switch (enhanceUnit.dataField) {
                    case PHYSICS_HARM: {
                        if (skillUnit instanceof ActiveSkillUnit) {
                            ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;
                            SkillServiceImpl.log.info((Object) ("\u88ab\u5f3a\u5316\u6280\u80fd:" + activeSkillUnit.name));
                            float value = this.valueChanged((float) activeSkillUnit.physicsHarmValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ActiveSkillUnit activeSkillUnit2 = activeSkillUnit;
                            activeSkillUnit2.physicsHarmValue += (int) value;
                            break;
                        }
                        if (skillUnit instanceof TouchUnit) {
                            TouchUnit touchPassiveSkill = (TouchUnit) skillUnit;
                            float value = this.valueChanged((float) touchPassiveSkill.physicsHarmValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            TouchUnit touchUnit = touchPassiveSkill;
                            touchUnit.physicsHarmValue += (int) value;
                            break;
                        }
                        if (skillUnit instanceof ChangePropertyUnit) {
                            ChangePropertyUnit changePropertyPassiveSkill = (ChangePropertyUnit) skillUnit;
                            float value = this.valueChanged(changePropertyPassiveSkill.physicsAttackHarmValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ChangePropertyUnit changePropertyUnit = changePropertyPassiveSkill;
                            changePropertyUnit.physicsAttackHarmValue += value;
                            break;
                        }
                        break;
                    }
                    case MAGIC_HARM: {
                        if (skillUnit instanceof ActiveSkillUnit) {
                            ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;
                            float value = this.valueChanged((float) activeSkillUnit.magicHarmHpValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ActiveSkillUnit activeSkillUnit3 = activeSkillUnit;
                            activeSkillUnit3.magicHarmHpValue += (int) value;
                            break;
                        }
                        if (skillUnit instanceof TouchUnit) {
                            TouchUnit touchPassiveSkill = (TouchUnit) skillUnit;
                            float value = this.valueChanged((float) touchPassiveSkill.magicHarmHpValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            TouchUnit touchUnit2 = touchPassiveSkill;
                            touchUnit2.magicHarmHpValue += (int) value;
                            break;
                        }
                        if (skillUnit instanceof ChangePropertyUnit) {
                            ChangePropertyUnit changePropertyPassiveSkill = (ChangePropertyUnit) skillUnit;
                            float value = this.valueChanged(changePropertyPassiveSkill.magicHarmValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ChangePropertyUnit changePropertyUnit2 = changePropertyPassiveSkill;
                            changePropertyUnit2.magicHarmValue += value;
                            break;
                        }
                        break;
                    }
                    case HATE: {
                        if (skillUnit instanceof ActiveSkillUnit) {
                            ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;
                            float value = this.valueChanged((float) activeSkillUnit.hateValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ActiveSkillUnit activeSkillUnit4 = activeSkillUnit;
                            activeSkillUnit4.hateValue += (int) value;
                            break;
                        }
                        if (skillUnit instanceof TouchUnit) {
                            TouchUnit touchPassiveSkill = (TouchUnit) skillUnit;
                            float value = this.valueChanged((float) touchPassiveSkill.hateValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            TouchUnit touchUnit3 = touchPassiveSkill;
                            touchUnit3.hateValue += (int) value;
                            break;
                        }
                        if (skillUnit instanceof ChangePropertyUnit) {
                            ChangePropertyUnit changePropertyPassiveSkill = (ChangePropertyUnit) skillUnit;
                            float value = this.valueChanged(changePropertyPassiveSkill.hate, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ChangePropertyUnit changePropertyUnit3 = changePropertyPassiveSkill;
                            changePropertyUnit3.hate += value;
                            break;
                        }
                        break;
                    }
                    case MAGIC_REDUCE: {
                        if (skillUnit instanceof ActiveSkillUnit) {
                            ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;
                            float value = this.valueChanged((float) activeSkillUnit.magicHarmMpValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ActiveSkillUnit activeSkillUnit5 = activeSkillUnit;
                            activeSkillUnit5.magicHarmMpValue += (int) value;
                            break;
                        }
                        if (skillUnit instanceof TouchUnit) {
                            TouchUnit touchPassiveSkill = (TouchUnit) skillUnit;
                            float value = this.valueChanged((float) touchPassiveSkill.magicHarmMpValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            TouchUnit touchUnit4 = touchPassiveSkill;
                            touchUnit4.magicHarmMpValue += (int) value;
                            break;
                        }
                        break;
                    }
                    case HP_RESUME: {
                        if (skillUnit instanceof ActiveSkillUnit) {
                            ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;
                            float value = this.valueChanged((float) activeSkillUnit.resumeHp, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ActiveSkillUnit activeSkillUnit6 = activeSkillUnit;
                            activeSkillUnit6.resumeHp += (int) value;
                            break;
                        }
                        if (skillUnit instanceof TouchUnit) {
                            TouchUnit touchPassiveSkill = (TouchUnit) skillUnit;
                            float value = this.valueChanged((float) touchPassiveSkill.resumeHp, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            TouchUnit touchUnit5 = touchPassiveSkill;
                            touchUnit5.resumeHp += (int) value;
                            break;
                        }
                        break;
                    }
                    case RELEASE_TIME: {
                        if (!(skillUnit instanceof ActiveSkillUnit)) {
                            break;
                        }
                        ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;
                        float value = this.valueChanged(((ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(activeSkillUnit.id)).releaseTime, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                        if (!_isEnhance) {
                            value = -value;
                        }
                        ActiveSkillUnit activeSkillUnit7 = activeSkillUnit;
                        activeSkillUnit7.releaseTime += value;
                        if (_needNotifyClient) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new UpdateActiveSkillNotify((ActiveSkill) _skill));
                            break;
                        }
                        break;
                    }
                    case TARGET_DISTANCE: {
                        if (!(skillUnit instanceof ActiveSkillUnit)) {
                            break;
                        }
                        ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;
                        float value = this.valueChanged(activeSkillUnit.targetDistance, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                        if (!_isEnhance) {
                            value = -value;
                        }
                        ActiveSkillUnit activeSkillUnit8 = activeSkillUnit;
                        activeSkillUnit8.targetDistance += (byte) value;
                        if (_needNotifyClient) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new UpdateActiveSkillNotify((ActiveSkill) _skill));
                            break;
                        }
                        break;
                    }
                    case MP_CONSUME: {
                        if (!(skillUnit instanceof ActiveSkillUnit)) {
                            break;
                        }
                        ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;
                        if (activeSkillUnit.consumeMp > 0) {
                            float value = this.valueChanged((float) activeSkillUnit.consumeMp, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ActiveSkillUnit activeSkillUnit9 = activeSkillUnit;
                            activeSkillUnit9.consumeMp += (int) value;
                        } else if (activeSkillUnit.consumeFp > 0) {
                            float value = this.valueChanged(activeSkillUnit.consumeFp, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ActiveSkillUnit activeSkillUnit10 = activeSkillUnit;
                            activeSkillUnit10.consumeFp += (short) value;
                        } else if (activeSkillUnit.consumeGp > 0) {
                            float value = this.valueChanged(activeSkillUnit.consumeGp, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ActiveSkillUnit activeSkillUnit11 = activeSkillUnit;
                            activeSkillUnit11.consumeGp += (short) value;
                        }
                        if (_needNotifyClient) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new UpdateActiveSkillNotify((ActiveSkill) _skill));
                            break;
                        }
                        break;
                    }
                    case HP_CONSUME: {
                        if (!(skillUnit instanceof ActiveSkillUnit)) {
                            break;
                        }
                        ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;
                        float value = this.valueChanged((float) activeSkillUnit.consumeHp, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                        if (!_isEnhance) {
                            value = -value;
                        }
                        ActiveSkillUnit activeSkillUnit12 = activeSkillUnit;
                        activeSkillUnit12.consumeHp += (int) value;
                        if (_needNotifyClient) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new UpdateActiveSkillNotify((ActiveSkill) _skill));
                            break;
                        }
                        break;
                    }
                    case RANGE_X: {
                        if (skillUnit instanceof ActiveSkillUnit) {
                            ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;
                            float value = this.valueChanged(activeSkillUnit.rangeX, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ActiveSkillUnit activeSkillUnit13 = activeSkillUnit;
                            activeSkillUnit13.rangeX += (byte) value;
                            break;
                        }
                        if (skillUnit instanceof TouchUnit) {
                            TouchUnit touchPassiveSkill = (TouchUnit) skillUnit;
                            float value = this.valueChanged(touchPassiveSkill.rangeX, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            TouchUnit touchUnit6 = touchPassiveSkill;
                            touchUnit6.rangeX += (byte) value;
                            break;
                        }
                        break;
                    }
                    case WEAPON_HARM_MULT: {
                        if (skillUnit instanceof ActiveSkillUnit) {
                            ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) skillUnit;
                            float value = this.valueChanged(activeSkillUnit.weaponHarmMult, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                            if (!_isEnhance) {
                                value = -value;
                            }
                            ActiveSkillUnit activeSkillUnit14 = activeSkillUnit;
                            activeSkillUnit14.weaponHarmMult += value;
                            break;
                        }
                        break;
                    }
                }
                enhanced = true;
            }
        } else if (EnhanceSkillUnit.EnhanceDataType.EFFECT_UNIT == enhanceUnit.skillDataType && _skill.addEffectUnit != null && enhanceUnit.skillName.equals(_skill.addEffectUnit.name)) {
            Effect effect = _skill.addEffectUnit;
            switch (enhanceUnit.dataField) {
                case MAGIC_HARM: {
                    if (effect instanceof DynamicEffect) {
                        DynamicEffect dynamicEffect = (DynamicEffect) effect;
                        float value = this.valueChanged((float) dynamicEffect.hpHarmTotal, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                        if (!_isEnhance) {
                            value = -value;
                        }
                        DynamicEffect dynamicEffect2 = dynamicEffect;
                        dynamicEffect2.hpHarmTotal += (int) value;
                        break;
                    }
                    if (effect instanceof StaticEffect) {
                        StaticEffect staticEffect = (StaticEffect) effect;
                        float value = this.valueChanged(staticEffect.magicHarmValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                        if (!_isEnhance) {
                            value = -value;
                        }
                        StaticEffect staticEffect2 = staticEffect;
                        staticEffect2.magicHarmValue += value;
                        break;
                    }
                    if (effect instanceof TouchEffect) {
                        TouchEffect touchEffect = (TouchEffect) effect;
                        float value = this.valueChanged((float) touchEffect.hpHarmValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                        if (!_isEnhance) {
                            value = -value;
                        }
                        TouchEffect touchEffect2 = touchEffect;
                        touchEffect2.hpHarmValue += (int) value;
                        break;
                    }
                    break;
                }
                case STRENGTH: {
                    StaticEffect staticEffect = (StaticEffect) effect;
                    float value = this.valueChanged(staticEffect.strength, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                    if (!_isEnhance) {
                        value = -value;
                    }
                    StaticEffect staticEffect3 = staticEffect;
                    staticEffect3.strength += value;
                    break;
                }
                case INTE: {
                    StaticEffect staticEffect = (StaticEffect) effect;
                    float value = this.valueChanged(staticEffect.inte, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                    if (!_isEnhance) {
                        value = -value;
                    }
                    StaticEffect staticEffect4 = staticEffect;
                    staticEffect4.inte += value;
                    break;
                }
                case AGILITY: {
                    StaticEffect staticEffect = (StaticEffect) effect;
                    float value = this.valueChanged(staticEffect.agility, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                    if (!_isEnhance) {
                        value = -value;
                    }
                    StaticEffect staticEffect5 = staticEffect;
                    staticEffect5.agility += value;
                    break;
                }
                case SPIRIT: {
                    StaticEffect staticEffect = (StaticEffect) effect;
                    float value = this.valueChanged(staticEffect.spirit, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                    if (!_isEnhance) {
                        value = -value;
                    }
                    StaticEffect staticEffect6 = staticEffect;
                    staticEffect6.spirit += value;
                    break;
                }
                case DEFENSE: {
                    StaticEffect staticEffect = (StaticEffect) effect;
                    float value = this.valueChanged(staticEffect.defense, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                    if (!_isEnhance) {
                        value = -value;
                    }
                    StaticEffect staticEffect7 = staticEffect;
                    staticEffect7.defense += value;
                    break;
                }
                case FASTNESS_VALUE: {
                    StaticEffect staticEffect = (StaticEffect) effect;
                    float value = this.valueChanged(staticEffect.magicFastnessValue, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                    if (!_isEnhance) {
                        value = -value;
                    }
                    StaticEffect staticEffect8 = staticEffect;
                    staticEffect8.magicFastnessValue += value;
                    break;
                }
                case DUCK_LEVEL: {
                    StaticEffect staticEffect = (StaticEffect) effect;
                    float value = this.valueChanged(staticEffect.physicsDuckLevel, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                    if (!_isEnhance) {
                        value = -value;
                    }
                    StaticEffect staticEffect9 = staticEffect;
                    staticEffect9.physicsDuckLevel += value;
                    break;
                }
                case HIT_LEVEL: {
                    StaticEffect staticEffect = (StaticEffect) effect;
                    float value = this.valueChanged(staticEffect.hitLevel, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                    if (!_isEnhance) {
                        value = -value;
                    }
                    StaticEffect staticEffect10 = staticEffect;
                    staticEffect10.hitLevel += value;
                    break;
                }
                case PHISICS_DEATHBLOW_LEVEL: {
                    StaticEffect staticEffect = (StaticEffect) effect;
                    float value = this.valueChanged(staticEffect.physicsDeathblowLevel, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                    if (!_isEnhance) {
                        value = -value;
                    }
                    StaticEffect staticEffect11 = staticEffect;
                    staticEffect11.physicsDeathblowLevel += value;
                    break;
                }
                case WEAPON_IMMO: {
                    StaticEffect staticEffect = (StaticEffect) effect;
                    float value = this.valueChanged(staticEffect.physicsAttackInterval, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                    if (!_isEnhance) {
                        value = -value;
                    }
                    StaticEffect staticEffect12 = staticEffect;
                    staticEffect12.physicsAttackInterval += value;
                    break;
                }
                case EFFECT_KEEP_TIME: {
                    float value = this.valueChanged(effect.keepTime, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                    if (!_isEnhance) {
                        value = -value;
                    }
                    effect.addKeepTime((short) value);
                    break;
                }
                case HP_MAX: {
                    StaticEffect staticEffect = (StaticEffect) effect;
                    float value = this.valueChanged(staticEffect.maxHp, enhanceUnit.changeMulti, enhanceUnit.caluOperator);
                    if (!_isEnhance) {
                        value = -value;
                    }
                    StaticEffect staticEffect13 = staticEffect;
                    staticEffect13.maxHp += value;
                    break;
                }
            }
            enhanced = true;
        }
        return enhanced;
    }

    private float valueChanged(final float _baseValue, final float _caluModulus, final EMathCaluOperator _operator) {
        if (_caluModulus > 0.0f) {
            switch (_operator) {
                case ADD: {
                    return _caluModulus;
                }
                case DEC: {
                    return -_caluModulus;
                }
                case MUL: {
                    if (_caluModulus > 1.0f) {
                        return _baseValue * (_caluModulus - 1.0f);
                    }
                    if (_caluModulus < 1.0f) {
                        return -(_baseValue * (1.0f - _caluModulus));
                    }
                    return -(_baseValue * ((_caluModulus - 1.0f) / _caluModulus));
                }
                case DIV: {
                    return -(_baseValue * ((_caluModulus - 1.0f) / _caluModulus));
                }
            }
        }
        return 0.0f;
    }
}
