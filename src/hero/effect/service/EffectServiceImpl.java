// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect.service;

import hero.effect.detail.TouchEffect;
import hero.skill.detail.ETouchType;
import hero.fight.service.FightServiceImpl;
import hero.effect.detail.DynamicEffect;
import hero.effect.message.MoveSpeedChangerNotify;
import hero.share.MoveSpeed;
import hero.npc.detail.EMonsterLevel;
import hero.fight.broadcast.SpecialViewStatusBroadcast;
import hero.effect.message.RemoveEffectNotify;
import hero.map.service.MapServiceImpl;
import hero.skill.detail.ETargetType;
import hero.share.ResistOddsList;
import hero.share.message.RefreshObjectViewValue;
import hero.player.service.PlayerServiceImpl;
import hero.skill.detail.ESpecialStatus;
import hero.skill.dict.SkillUnitDict;
import hero.skill.unit.ActiveSkillUnit;
import hero.skill.ActiveSkill;
import hero.share.EObjectType;
import hero.share.EMagic;
import hero.share.MagicHarmList;
import hero.skill.detail.EMathCaluOperator;
import hero.effect.message.RefreshEffectNotify;
import hero.share.message.Warning;
import java.util.ArrayList;
import java.util.Iterator;
import hero.share.service.ME2ObjectList;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.effect.message.AddEffectNotify;
import hero.map.Map;
import hero.share.ME2GameObject;
import hero.effect.detail.StaticEffect;
import hero.effect.Effect;
import hero.player.HeroPlayer;
import yoyo.service.base.session.Session;
import hero.effect.dictionry.EffectDictionary;
import java.util.TimerTask;
import java.util.Timer;
import hero.npc.Monster;
import javolution.util.FastList;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class EffectServiceImpl extends AbsServiceAdaptor<EffectConfig> {

    private static Logger log;
    private static EffectServiceImpl instance;
    private FastList<Monster> existsEffectMonsterList;
    private Timer timer;

    static {
        EffectServiceImpl.log = Logger.getLogger((Class) EffectServiceImpl.class);
    }

    public static EffectServiceImpl getInstance() {
        if (EffectServiceImpl.instance == null) {
            EffectServiceImpl.instance = new EffectServiceImpl();
        }
        return EffectServiceImpl.instance;
    }

    private EffectServiceImpl() {
        this.config = new EffectConfig();
        this.existsEffectMonsterList = (FastList<Monster>) new FastList();
        (this.timer = new Timer()).schedule(new MonsterEffectCheckTask(), 10000L, 3000L);
    }

    @Override
    protected void start() {
        EffectDictionary.getInstance().load((EffectConfig) this.config);
    }

    @Override
    public void createSession(final Session _session) {
    }

    @Override
    public void sessionFree(final Session _session) {
    }

    public void staticEffectAction(final HeroPlayer _player) {
        for (int i = 0; i < _player.effectList.size(); ++i) {
            Effect effect = _player.effectList.get(i);
            if (effect instanceof StaticEffect) {
                this.changePropertyValue(_player, (StaticEffect) effect, true);
            }
        }
    }

    public void sendEffectList(final HeroPlayer _player, final Map _where) {
        if (_where == null) {
            return;
        }
        ME2ObjectList monsterList = _where.getMonsterList();
        ME2ObjectList otherPlayerList = _where.getPlayerList();
        ME2GameObject _target = null;
        Effect ef = null;
        for (int j = 0; j < _player.effectList.size(); ++j) {
            ef = _player.effectList.get(j);
            AddEffectNotify msg = new AddEffectNotify(_player, ef);
            MapSynchronousInfoBroadcast.getInstance().put(_player.where(), msg, false, 0);
            if (ef != null && ef instanceof StaticEffect) {
                this.notifyMoveToOther((StaticEffect) _player.effectList.get(j), _player);
            }
        }
        for (int i = 0; i < otherPlayerList.size(); ++i) {
            _target = otherPlayerList.get(i);
            for (final Effect effect : _target.effectList) {
                if (effect == null) {
                    continue;
                }
                AddEffectNotify msg2 = new AddEffectNotify(_target, effect);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg2);
                if (!(effect instanceof StaticEffect)) {
                    continue;
                }
                this.notifyMoveToPlayer((StaticEffect) effect, _player, _target);
            }
        }
        for (int i = 0; i < monsterList.size(); ++i) {
            _target = monsterList.get(i);
            for (final Effect effect : _target.effectList) {
                if (effect == null) {
                    continue;
                }
                AddEffectNotify msg2 = new AddEffectNotify(_target, effect);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg2);
            }
        }
    }

    public void playerDie(final HeroPlayer _player) {
    }

    public void removeDuelEffect(final HeroPlayer _one, final HeroPlayer _other) {
    }

    public void downMountEffect(final ME2GameObject _player) {
        ArrayList<Effect> targetEffectList = _player.effectList;
        synchronized (targetEffectList) {
            Effect existsEffect = null;
            for (int i = 0; i < targetEffectList.size(); ++i) {
                existsEffect = targetEffectList.get(i);
                if (existsEffect.feature == Effect.EffectFeature.MOUNT) {
                    this.removeEffect(_player, existsEffect, false, false);
                    break;
                }
            }
        }
        // monitorexit(targetEffectList)
    }

    public boolean haveAlikeMount(final ME2GameObject _player, final Effect _effect) {
        boolean result = false;
        ArrayList<Effect> targetEffectList = _player.effectList;
        synchronized (targetEffectList) {
            Effect existsEffect = null;
            for (int i = 0; i < targetEffectList.size(); ++i) {
                existsEffect = targetEffectList.get(i);
                if (existsEffect.ID == _effect.ID) {
                    result = true;
                    break;
                }
            }
        }
        // monitorexit(targetEffectList)
        return result;
    }

    public void appendSkillEffect(final ME2GameObject _skillReleaser, final ME2GameObject _target, final Effect _effectModel) {
        if (_target == null) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _skillReleaser).getMsgQueueIndex(), new Warning("\u65e0\u6548\u7684\u76ee\u6807"));
            return;
        }
        ArrayList<Effect> targetEffectList = _target.effectList;
        synchronized (targetEffectList) {
            if (targetEffectList.size() > 0) {
                for (int i = 0; i < targetEffectList.size(); ++i) {
                    if (_effectModel.keepTimeType != Effect.EKeepTimeType.N_A) {
                        Effect existsEffect = targetEffectList.get(i);
                        if (existsEffect.releaser == _skillReleaser) {
                            if (existsEffect.name.equals(_effectModel.name) && existsEffect.ID == _effectModel.ID) {
                                existsEffect.resetTraceTime();
                                if (existsEffect.totalTimes > 1) {
                                    existsEffect.addCurrentCountTimes(_target);
                                }
                                RefreshEffectNotify msg = new RefreshEffectNotify(existsEffect, existsEffect.releaser.getID());
                                if (_target instanceof HeroPlayer) {
                                    ResponseMessageQueue.getInstance().put(((HeroPlayer) _target).getMsgQueueIndex(), msg);
                                    MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg, true, _target.getID());
                                } else {
                                    MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg, false, 0);
                                }
                                EffectServiceImpl.log.info("\u53d7\u5230\u76f8\u540c\u7684\u6548\u679c,\u91cd\u7f6e\u6548\u679c\u6301\u7eed\u65f6\u95f4.");
                                // monitorexit(targetEffectList)
                                return;
                            }
                        } else if (existsEffect.name.equals(_effectModel.name) && existsEffect.level == _effectModel.level) {
                            existsEffect.resetTraceTime();
                            existsEffect.releaser = _skillReleaser;
                            RefreshEffectNotify msg = new RefreshEffectNotify(existsEffect, existsEffect.releaser.getID());
                            if (_target instanceof HeroPlayer) {
                                ResponseMessageQueue.getInstance().put(((HeroPlayer) _target).getMsgQueueIndex(), msg);
                                MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg, true, _target.getID());
                            } else {
                                MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg, false, 0);
                            }
                            EffectServiceImpl.log.info("\u53d7\u5230\u76f8\u540c\u7684\u6548\u679c,\u91cd\u7f6e\u6548\u679c\u6301\u7eed\u65f6\u95f4.");
                            // monitorexit(targetEffectList)
                            return;
                        }
                    }
                }
                for (int i = 0; i < targetEffectList.size(); ++i) {
                    Effect existsEffect = targetEffectList.get(i);
                    if (_effectModel.keepTimeType == existsEffect.keepTimeType && _effectModel.trait == existsEffect.trait) {
                        if (_effectModel.keepTimeType == Effect.EKeepTimeType.N_A && existsEffect.releaser == _skillReleaser) {
                            this.removeEffect(_skillReleaser, existsEffect, false, true);
                            if (existsEffect.ID == _effectModel.ID) {
                                // monitorexit(targetEffectList)
                                return;
                            }
                            break;
                        } else {
                            if (existsEffect.name.equals(_effectModel.name) && _effectModel.level >= existsEffect.level) {
                                this.removeEffect(_target, existsEffect, false, true);
                                break;
                            }
                            if (existsEffect.name.equals(_effectModel.name) && _skillReleaser instanceof HeroPlayer) {
                                ResponseMessageQueue.getInstance().put(((HeroPlayer) _skillReleaser).getMsgQueueIndex(), new Warning("\u5b58\u5728\u66f4\u9ad8\u7b49\u7ea7\u7684\u6548\u679c"));
                                // monitorexit(targetEffectList)
                                return;
                            }
                        }
                    }
                }
            }
            try {
                Effect newEffect = _effectModel.clone();
                if (newEffect.build(_skillReleaser, _target)) {
                    targetEffectList.add(newEffect);
                    EffectServiceImpl.log.info(("\u7ed9\u76ee\u6807[" + _target.getName() + "]" + "\u65bd\u52a0" + newEffect.name));
                    AddEffectNotify msg2 = new AddEffectNotify(_target, newEffect);
                    if (_target instanceof HeroPlayer) {
                        ResponseMessageQueue.getInstance().put(((HeroPlayer) _target).getMsgQueueIndex(), msg2);
                        MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg2, true, _target.getID());
                    } else {
                        MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg2, false, 0);
                    }
                    if (_target instanceof Monster && !this.existsEffectMonsterList.contains(_target)) {
                        this.existsEffectMonsterList.add((Monster) _target);
                    }
                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        // monitorexit(targetEffectList)
    }

    public void addWeaponEnhanceEffect(final HeroPlayer _player, final Effect _effectModel) {
        if (_effectModel == null) {
            return;
        }
        ArrayList<Effect> playerEffectList = _player.effectList;
        synchronized (playerEffectList) {
            if (playerEffectList.size() > 0) {
                for (int i = 0; i < playerEffectList.size(); ++i) {
                    Effect existsEffect = playerEffectList.get(i);
                    if (_effectModel.ID == existsEffect.ID) {
                        // monitorexit(playerEffectList)
                        return;
                    }
                }
            }
            playerEffectList.add(_effectModel);
        }
        // monitorexit(playerEffectList)
    }

    public void removeWeaponEnhanceEffect(final HeroPlayer _player, final Effect _effectModel) {
        if (_effectModel == null) {
            return;
        }
        ArrayList<Effect> playerEffectList = _player.effectList;
        synchronized (playerEffectList) {
            if (playerEffectList.size() > 0) {
                for (int i = 0; i < playerEffectList.size(); ++i) {
                    Effect existsEffect = playerEffectList.get(i);
                    if (_effectModel.ID == existsEffect.ID) {
                        playerEffectList.remove(i);
                        // monitorexit(playerEffectList)
                        return;
                    }
                }
            }
        }
        // monitorexit(playerEffectList)
    }

    public void appendSkillEffect(final ME2GameObject _skillReleaser, final ME2GameObject _target, final int _effectID) {
        Effect effectModel = EffectDictionary.getInstance().getEffectRef(_effectID);
        if (effectModel != null) {
            this.appendSkillEffect(_skillReleaser, _target, effectModel);
        }
    }

    public boolean changePropertyValue(final ME2GameObject _object, final StaticEffect _staticEffect, final boolean _isBuild) {
        boolean propertyIsChanged = false;
        boolean skillReleaseTimeChanged = false;
        float value = 0.0f;
        EMathCaluOperator operator = _staticEffect.caluOperator;
        if (_staticEffect.strength > 0.0f) {
            value = this.changeValue((float) _object.getBaseProperty().getStrength(), _staticEffect.strength, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addStrength((int) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.agility > 0.0f) {
            value = this.changeValue((float) _object.getBaseProperty().getAgility(), _staticEffect.agility, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addAgility((int) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.stamina > 0.0f) {
            value = this.changeValue((float) _object.getBaseProperty().getStamina(), _staticEffect.stamina, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addStamina((int) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.inte > 0.0f) {
            value = this.changeValue((float) _object.getBaseProperty().getInte(), _staticEffect.inte, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addInte((int) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.spirit > 0.0f) {
            value = this.changeValue((float) _object.getBaseProperty().getSpirit(), _staticEffect.spirit, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addSpirit((int) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.lucky > 0.0f) {
            value = this.changeValue((float) _object.getBaseProperty().getLucky(), _staticEffect.lucky, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addLucky((int) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.defense > 0.0f) {
            value = this.changeValue((float) _object.getBaseProperty().getDefense(), _staticEffect.defense, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addDefense((int) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.maxHp > 0.0f) {
            value = this.changeValue((float) _object.getBaseProperty().getHpMax(), _staticEffect.maxHp, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addHpMax((int) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.maxMp > 0.0f) {
            value = this.changeValue((float) _object.getBaseProperty().getMpMax(), _staticEffect.maxMp, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addMpMax((int) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.hitLevel > 0.0f) {
            value = this.changeValue(_object.getBaseProperty().getHitLevel(), _staticEffect.hitLevel, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addHitLevel((short) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.physicsDuckLevel > 0.0f) {
            value = this.changeValue(_object.getBaseProperty().getPhysicsDuckLevel(), _staticEffect.physicsDuckLevel, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addPhysicsDuckLevel((short) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.physicsDeathblowLevel > 0.0f) {
            value = this.changeValue(_object.getBaseProperty().getPhysicsDeathblowLevel(), _staticEffect.physicsDeathblowLevel, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addPhysicsDeathblowLevel((short) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.magicDeathblowLevel > 0.0f) {
            value = this.changeValue(_object.getBaseProperty().getMagicDeathblowLevel(), _staticEffect.magicDeathblowLevel, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.getActualProperty().addMagicDeathblowLevel((short) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.physicsAttackHarmValue > 0.0f) {
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                value = this.changeValue((float) _object.getBaseProperty().getAdditionalPhysicsAttackHarmValue(), _staticEffect.physicsAttackHarmValue, operator, false);
                value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
                _object.getActualProperty().addAdditionalPhysicsAttackHarmValue((int) value);
            } else {
                value = this.changeValue(_object.getBaseProperty().getAdditionalPhysicsAttackHarmScale(), _staticEffect.physicsAttackHarmValue, EMathCaluOperator.ADD, true);
                value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
                System.out.println("\u51c6\u5907add:" + value);
                float f = _object.getActualProperty().addAdditionalPhysicsAttackHarmScale(value);
                System.out.println("\u6dfb\u52a0\u4e2d\u7684\u53d8\u5316\u503c" + f);
                System.out.println("\u589e\u52a0\u540e:" + _object.getActualProperty().getAdditionalPhysicsAttackHarmScale());
            }
            propertyIsChanged = true;
        }
        if (_staticEffect.bePhysicsHarmValue > 0.0f) {
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                value = this.changeValue((float) _object.getBaseProperty().getAdditionalHarmValueBePhysicsAttack(), _staticEffect.bePhysicsHarmValue, operator, false);
                value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
                _object.getActualProperty().addAdditionalHarmValueBePhysicsAttack((int) value);
            } else {
                value = this.changeValue(_object.getBaseProperty().getAdditionalHarmScaleBePhysicsAttack(), _staticEffect.bePhysicsHarmValue, operator, false);
                value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
                _object.getActualProperty().addAdditionalHarmScaleBePhysicsAttack(value);
            }
            propertyIsChanged = true;
        }
        if (_staticEffect.magicHarmValue != 0.0f) {
            value = this.changeValue(1.0f, _staticEffect.magicHarmValue, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                if (_staticEffect.magicHarmType != null) {
                    _object.getActualProperty().addAdditionalMagicHarm(_staticEffect.magicHarmType, value);
                } else {
                    MagicHarmList list = new MagicHarmList(value);
                    _object.getActualProperty().addAdditionalMagicHarm(list);
                }
            } else if (_staticEffect.magicHarmType != null) {
                _object.getActualProperty().addAdditionalMagicHarmScale(_staticEffect.magicHarmType, value);
            } else {
                MagicHarmList list = new MagicHarmList(value);
                _object.getActualProperty().addAdditionalMagicHarmScale(list);
            }
            propertyIsChanged = true;
        }
        if (_staticEffect.magicHarmValueBeAttack != 0.0f) {
            value = this.changeValue(1.0f, _staticEffect.magicHarmValueBeAttack, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            if (operator == EMathCaluOperator.ADD || operator == EMathCaluOperator.DEC) {
                if (_staticEffect.magicHarmTypeBeAttack != null) {
                    _object.getActualProperty().addAdditionalMagicHarmBeAttack(_staticEffect.magicHarmTypeBeAttack, value);
                } else {
                    MagicHarmList list = new MagicHarmList(value);
                    _object.getActualProperty().addAdditionalMagicHarmBeAttack(list);
                }
            } else if (_staticEffect.magicHarmTypeBeAttack != null) {
                _object.getActualProperty().addAdditionalMagicHarmScaleBeAttack(_staticEffect.magicHarmTypeBeAttack, value);
            } else {
                MagicHarmList list = new MagicHarmList(value);
                _object.getActualProperty().addAdditionalMagicHarmScaleBeAttack(list);
            }
            propertyIsChanged = true;
        }
        if (_staticEffect.magicFastnessValue != 0.0f) {
            if (_staticEffect.magicFastnessType != null) {
                value = this.changeValue((float) _object.getBaseProperty().getMagicFastnessList().getEMagicFastnessValue(_staticEffect.magicFastnessType), _staticEffect.magicFastnessValue, operator, false);
            } else {
                EMagic[] magic = EMagic.values();
                for (int i = 0; i < magic.length; ++i) {
                    value = this.changeValue((float) _object.getBaseProperty().getMagicFastnessList().getEMagicFastnessValue(magic[i]), _staticEffect.magicFastnessValue, operator, false);
                    if (value != 0.0f) {
                        value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
                        _object.getActualProperty().getMagicFastnessList().add(magic[i], (int) value);
                    }
                }
            }
            propertyIsChanged = true;
        }
        if (_staticEffect.hate > 0.0f) {
            if (_object.getObjectType() == EObjectType.PLAYER) {
                value = this.changeValue(1.0f, _staticEffect.hate, operator, false);
                value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
                ((HeroPlayer) _object).changeHatredModulus(value);
            }
            propertyIsChanged = true;
        }
        if (_staticEffect.physicsAttackInterval > 0.0f) {
            value = this.changeValue((float) _object.getBaseAttackImmobilityTime(), _staticEffect.physicsAttackInterval, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            _object.addActualAttackImmobilityTime((int) value);
            propertyIsChanged = true;
        }
        if (_staticEffect.allSkillReleaseTime > 0.0f) {
            ArrayList<ActiveSkill> list2 = null;
            if (_object instanceof HeroPlayer) {
                list2 = ((HeroPlayer) _object).activeSkillList;
                skillReleaseTimeChanged = true;
                for (int j = 0; j < list2.size(); ++j) {
                    ActiveSkill activeSkill = list2.get(j);
                    value = this.changeValue(((ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(activeSkill.skillUnit.id)).releaseTime, _staticEffect.allSkillReleaseTime, operator, false);
                    value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
                    ActiveSkillUnit activeSkillUnit = (ActiveSkillUnit) activeSkill.skillUnit;
                    activeSkillUnit.releaseTime += value;
                }
            }
        }
        if (_staticEffect.specialSkillReleaseTimeIDList != null) {
            ArrayList<ActiveSkill> skillListBeRefresh = new ArrayList<ActiveSkill>();
            ArrayList<ActiveSkill> objectSkillList = ((HeroPlayer) _object).activeSkillList;
            for (int k = 0; k < _staticEffect.specialSkillReleaseTimeIDList.size(); ++k) {
                for (int l = 0; l < objectSkillList.size(); ++l) {
                    ActiveSkill activeSkill2 = objectSkillList.get(l);
                    if (activeSkill2.id == _staticEffect.specialSkillReleaseTimeIDList.get(k)) {
                        value = this.changeValue(((ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(activeSkill2.skillUnit.id)).releaseTime, _staticEffect.specialSkillReleaseTime, operator, false);
                        value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
                        ActiveSkillUnit activeSkillUnit2 = (ActiveSkillUnit) activeSkill2.skillUnit;
                        activeSkillUnit2.releaseTime += value;
                        skillListBeRefresh.add(objectSkillList.get(l));
                        break;
                    }
                }
            }
            if (skillListBeRefresh.size() > 0 && _object instanceof HeroPlayer) {
                skillReleaseTimeChanged = true;
            }
        }
        if (_staticEffect.resistSpecialStatus != null) {
            value = this.changeValue(0.0f, _staticEffect.resistSpecialStatusOdds, operator, false);
            value = (_isBuild ? value : (-(_staticEffect.currentCountTimes * value)));
            if (_staticEffect.resistSpecialStatus == ESpecialStatus.DUMB) {
                ResistOddsList resistOddsList = _object.getResistOddsList();
                resistOddsList.forbidSpellOdds += value;
            } else if (_staticEffect.resistSpecialStatus == ESpecialStatus.FAINT) {
                ResistOddsList resistOddsList2 = _object.getResistOddsList();
                resistOddsList2.insensibleOdds += value;
            } else if (_staticEffect.resistSpecialStatus == ESpecialStatus.SLEEP) {
                ResistOddsList resistOddsList3 = _object.getResistOddsList();
                resistOddsList3.sleepingOdds += value;
            } else if (_staticEffect.resistSpecialStatus == ESpecialStatus.LAUGH) {
                if (_object.getObjectType() == EObjectType.MONSTER) {
                    ResistOddsList resistOddsList4 = ((Monster) _object).getResistOddsList();
                    resistOddsList4.provokeOdds += value;
                }
            } else if (_staticEffect.resistSpecialStatus == ESpecialStatus.PHY_BOOM) {
                ResistOddsList resistOddsList5 = _object.getResistOddsList();
                resistOddsList5.physicsDeathblowOdds += value;
            } else if (_staticEffect.resistSpecialStatus == ESpecialStatus.MAG_BOOM) {
                ResistOddsList resistOddsList6 = _object.getResistOddsList();
                resistOddsList6.magicDeathblowOdds += value;
            } else if (_staticEffect.resistSpecialStatus == ESpecialStatus.STOP) {
                ResistOddsList resistOddsList7 = _object.getResistOddsList();
                resistOddsList7.fixBodyOdds += value;
            }
            if (value != 0.0f) {
                propertyIsChanged = true;
            }
        }
        if (propertyIsChanged) {
            if (_object.getObjectType() == EObjectType.PLAYER) {
                PlayerServiceImpl.getInstance().refreshRoleProperty((HeroPlayer) _object);
            }
            MapSynchronousInfoBroadcast.getInstance().put(_object.where(), new RefreshObjectViewValue(_object), true, _object.getID());
        }
        return propertyIsChanged || skillReleaseTimeChanged;
    }

    private float changeValue(final float _baseValue, final float _caluModulus, final EMathCaluOperator _operator, final boolean _isOdd) {
        if (_caluModulus > 0.0f) {
            switch (_operator) {
                case ADD: {
                    if (_isOdd && _caluModulus > 1.0f) {
                        return _caluModulus - 1.0f;
                    }
                    if (_caluModulus > 1.0f) {
                        return _caluModulus;
                    }
                    if (_caluModulus < 1.0f) {
                        return -(1.0f - _caluModulus);
                    }
                    return -_caluModulus;
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

    public void scanAureoleRadiationTarget(final ME2GameObject _releaser, final Effect _aureoleEffect) {
        if (_aureoleEffect.keepTimeType == Effect.EKeepTimeType.N_A) {
            Effect.AureoleRadiationRange range = _aureoleEffect.aureoleRadiationRange;
            if (_releaser instanceof HeroPlayer) {
                HeroPlayer player = (HeroPlayer) _releaser;
                if (_aureoleEffect.aureoleRadiationRange.targetType == ETargetType.ENEMY) {
                    ArrayList<ME2GameObject> radiationTargetList = MapServiceImpl.getInstance().getAttackableObjectListInRange(_releaser.where(), _releaser.getCellX(), _releaser.getCellY(), _aureoleEffect.aureoleRadiationRange.rangeRadiu, _releaser, -1);
                    if (radiationTargetList != null && radiationTargetList.size() > 0) {
                        ArrayList<Effect> targetEffectList = null;
                        Effect existsEffect = null;
                        boolean beRadiation = false;
                        for (int i = 0; i < radiationTargetList.size(); ++i) {
                            ME2GameObject target = radiationTargetList.get(i);
                            if (target.getID() != _releaser.getID()) {
                                targetEffectList = target.effectList;
                                if (targetEffectList.contains(_aureoleEffect)) {
                                    return;
                                }
                                beRadiation = true;
                                for (int j = 0; j < targetEffectList.size(); ++j) {
                                    existsEffect = targetEffectList.get(j);
                                    if (Effect.EKeepTimeType.N_A == existsEffect.keepTimeType) {
                                        if (existsEffect.ID == _aureoleEffect.ID) {
                                            return;
                                        }
                                        if (existsEffect.name.equals(_aureoleEffect.name)) {
                                            if (existsEffect.level >= _aureoleEffect.level) {
                                                beRadiation = false;
                                                break;
                                            }
                                            targetEffectList.remove(existsEffect);
                                            existsEffect.destory(target);
                                            if (existsEffect instanceof StaticEffect) {
                                                this.removeMove((StaticEffect) existsEffect, target);
                                            }
                                            MapSynchronousInfoBroadcast.getInstance().put(target.where(), new RemoveEffectNotify(target, existsEffect), false, 0);
                                            break;
                                        }
                                    }
                                }
                                if (beRadiation) {
                                    targetEffectList.add(_aureoleEffect);
                                    _aureoleEffect.radiationTarget(_releaser, target);
                                    MapSynchronousInfoBroadcast.getInstance().put(target.where(), new AddEffectNotify(target, _aureoleEffect), false, 0);
                                    if (target instanceof Monster && !this.existsEffectMonsterList.contains(target)) {
                                        this.existsEffectMonsterList.add((Monster) target);
                                    }
                                }
                            }
                        }
                    }
                } else if (_aureoleEffect.aureoleRadiationRange.targetType == ETargetType.FRIEND && player.getGroupID() > 0) {
                    ArrayList<HeroPlayer> radiationTargetList2 = MapServiceImpl.getInstance().getGroupPlayerInRange((HeroPlayer) _releaser, _releaser.getCellX(), _releaser.getCellY(), range.rangeRadiu, -1);
                    if (radiationTargetList2 != null && radiationTargetList2.size() > 0) {
                        ArrayList<Effect> targetEffectList = null;
                        Effect existsEffect = null;
                        boolean beRadiation = false;
                        for (int i = 0; i < radiationTargetList2.size(); ++i) {
                            ME2GameObject target = radiationTargetList2.get(i);
                            if (target.getID() != _releaser.getID()) {
                                targetEffectList = target.effectList;
                                if (targetEffectList.contains(_aureoleEffect)) {
                                    return;
                                }
                                beRadiation = true;
                                for (int j = 0; j < targetEffectList.size(); ++j) {
                                    existsEffect = targetEffectList.get(j);
                                    if (Effect.EKeepTimeType.N_A == existsEffect.keepTimeType) {
                                        if (existsEffect.ID == _aureoleEffect.ID) {
                                            return;
                                        }
                                        if (existsEffect.name.equals(_aureoleEffect.name)) {
                                            if (existsEffect.level >= _aureoleEffect.level) {
                                                return;
                                            }
                                            targetEffectList.remove(existsEffect);
                                            existsEffect.destory(target);
                                            MapSynchronousInfoBroadcast.getInstance().put(target.where(), new RemoveEffectNotify(target, existsEffect), false, 0);
                                            break;
                                        }
                                    }
                                }
                                if (beRadiation) {
                                    targetEffectList.add(_aureoleEffect);
                                    _aureoleEffect.radiationTarget(_releaser, target);
                                    MapSynchronousInfoBroadcast.getInstance().put(target.where(), new AddEffectNotify(target, _aureoleEffect), false, 0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean checkAureoleValidity(final ME2GameObject _host, final Effect _aureoleEffect) {
        if (_aureoleEffect.keepTimeType == Effect.EKeepTimeType.N_A) {
            ME2GameObject releaser = _aureoleEffect.releaser;
            if (releaser == _host) {
                return true;
            }
            Effect.AureoleRadiationRange range = _aureoleEffect.aureoleRadiationRange;
            if (Math.abs(_host.getCellX() - releaser.getCellX()) > range.rangeRadiu || Math.abs(_host.getCellY() - releaser.getCellY()) > range.rangeRadiu) {
                this.removeEffect(_host, _aureoleEffect, false, false);
                return false;
            }
            if (!ObjectCheckor.isValidate(releaser)) {
                this.removeEffect(_host, _aureoleEffect, false, false);
                return false;
            }
            if (releaser instanceof HeroPlayer && _host instanceof HeroPlayer && (((HeroPlayer) releaser).getGroupID() == 0 || ((HeroPlayer) releaser).getGroupID() != ((HeroPlayer) _host).getGroupID())) {
                this.removeEffect(_host, _aureoleEffect, false, false);
                return false;
            }
            if (_host.where() != releaser.where()) {
                this.removeEffect(_host, _aureoleEffect, false, false);
                return false;
            }
        }
        return true;
    }

    public boolean appendSpecialStatus(final ME2GameObject _releaser, final ME2GameObject _host, final StaticEffect _effect) {
        EffectServiceImpl.log.info(("\u6dfb\u52a0\u7279\u6b8a\u6548\u679c\uff1a" + _effect.name));
        ESpecialStatus specialStatus = _effect.specialStatus;
        byte existsMaxSpeedLevel = 0;
        byte existsSpeedLevel = 0;
        if (specialStatus != null) {
            ArrayList<Effect> effectList = _host.effectList;
            if (effectList != null && effectList.size() > 0) {
                for (int i = 0; i < effectList.size(); ++i) {
                    Effect existsEffect = effectList.get(i);
                    if (_effect != existsEffect && existsEffect instanceof StaticEffect && ((StaticEffect) existsEffect).specialStatus == specialStatus) {
                        existsSpeedLevel = ((StaticEffect) existsEffect).getSpecialStatusLevel();
                        if (existsMaxSpeedLevel < existsSpeedLevel) {
                            existsMaxSpeedLevel = existsSpeedLevel;
                        }
                    }
                }
            }
            switch (specialStatus) {
                case HIDE: {
                    _host.disappear();
                    SpecialViewStatusBroadcast.send(_host, (byte) 2);
                    return true;
                }
                case DUMB: {
                    if (_host instanceof Monster && ((Monster) _host).getMonsterLevel() == EMonsterLevel.BOSS) {
                        ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(), new Warning("\u514d\u75ab"));
                        break;
                    }
                    if (_host.canReleaseMagicSkill()) {
                        _host.forbidReleaseMagicSkill();
                        SpecialViewStatusBroadcast.send(_host, (byte) 5);
                    }
                    return true;
                }
                case FAINT: {
                    if (_host instanceof Monster && ((Monster) _host).getMonsterLevel() == EMonsterLevel.BOSS) {
                        ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(), new Warning("\u514d\u75ab"));
                        break;
                    }
                    if (!_host.isInsensible()) {
                        _host.beInComa();
                        SpecialViewStatusBroadcast.send(_host, (byte) 6);
                    }
                    return true;
                }
                case SLEEP: {
                    if (_host instanceof Monster && ((Monster) _host).getMonsterLevel() == EMonsterLevel.BOSS) {
                        ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(), new Warning("\u514d\u75ab"));
                        break;
                    }
                    if (!_host.isSleeping()) {
                        _host.sleep();
                        SpecialViewStatusBroadcast.send(_host, (byte) 7);
                    }
                    return true;
                }
                case LAUGH: {
                    if (_host instanceof Monster && _releaser instanceof HeroPlayer) {
                        ((Monster) _host).beProvoke((HeroPlayer) _releaser);
                        return true;
                    }
                    break;
                }
                case STOP: {
                    if (_host instanceof Monster && ((Monster) _host).getMonsterLevel() == EMonsterLevel.BOSS) {
                        ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(), new Warning("\u514d\u75ab"));
                        break;
                    }
                    if (_host.moveable()) {
                        _host.fixBody();
                        SpecialViewStatusBroadcast.send(_host, (byte) 8);
                    }
                    return true;
                }
                case MOVE_FAST: {
                    _host.addAddSpeedState(true);
                    byte speedLevel = _effect.getSpecialStatusLevel();
                    byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
                    MapSynchronousInfoBroadcast.getInstance().put(_host.where(), new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(), speed), false, 0);
                    return true;
                }
                case MOVE_SLOWLY: {
                    if (_host instanceof Monster && ((Monster) _host).getMonsterLevel() == EMonsterLevel.BOSS) {
                        ResponseMessageQueue.getInstance().put(((HeroPlayer) _releaser).getMsgQueueIndex(), new Warning("\u514d\u75ab"));
                        break;
                    }
                    _host.addSlowSpeedState(true);
                    byte speedLevel = _effect.getSpecialStatusLevel();
                    byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
                    MapSynchronousInfoBroadcast.getInstance().put(_host.where(), new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(), speed), false, 0);
                    return true;
                }
            }
        }
        return true;
    }

    public void clearSpecialStatus(final ME2GameObject _host, final StaticEffect _effect) {
        ESpecialStatus specialStatus = _effect.specialStatus;
        if (specialStatus != null) {
            switch (specialStatus) {
                case HIDE: {
                    _host.emerge();
                    SpecialViewStatusBroadcast.send(_host, (byte) 1);
                    break;
                }
                case DUMB: {
                    if (!_host.canReleaseMagicSkill()) {
                        _host.relieveMagicSkillLimit();
                        SpecialViewStatusBroadcast.send(_host, (byte) 10);
                        break;
                    }
                    break;
                }
                case FAINT: {
                    if (_host.isInsensible()) {
                        _host.relieveComa();
                        SpecialViewStatusBroadcast.send(_host, (byte) 11);
                        break;
                    }
                    break;
                }
                case SLEEP: {
                    if (_host.isSleeping()) {
                        _host.wakeUp();
                        SpecialViewStatusBroadcast.send(_host, (byte) 12);
                        break;
                    }
                    break;
                }
                case STOP: {
                    if (!_host.moveable()) {
                        _host.relieveFixBodyLimit();
                        SpecialViewStatusBroadcast.send(_host, (byte) 13);
                        break;
                    }
                    break;
                }
                case MOVE_FAST: {
                    _host.removeAddSpeedState();
                    byte speedLevel = _effect.getSpecialStatusLevel();
                    byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
                    MapSynchronousInfoBroadcast.getInstance().put(_host.where(), new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(), speed), false, 0);
                    RemoveEffectNotify msg = new RemoveEffectNotify(_host, _effect);
                    if (_host instanceof HeroPlayer) {
                        HeroPlayer player = (HeroPlayer) _host;
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                        MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg, true, player.getID());
                        break;
                    }
                    MapSynchronousInfoBroadcast.getInstance().put(_host.where(), msg, false, 0);
                    break;
                }
                case MOVE_SLOWLY: {
                    _host.removeSlowSpeedState();
                    byte speedLevel = _effect.getSpecialStatusLevel();
                    byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
                    MapSynchronousInfoBroadcast.getInstance().put(_host.where(), new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(), speed), false, 0);
                    RemoveEffectNotify msg = new RemoveEffectNotify(_host, _effect);
                    if (_host instanceof HeroPlayer) {
                        HeroPlayer player = (HeroPlayer) _host;
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                        MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg, true, player.getID());
                        break;
                    }
                    MapSynchronousInfoBroadcast.getInstance().put(_host.where(), msg, false, 0);
                    break;
                }
            }
        }
    }

    public void clearHideEffect(final ME2GameObject _host) {
        if (_host != null) {
            ArrayList<Effect> effectList = _host.effectList;
            if (effectList != null && effectList.size() > 0) {
                for (int i = 0; i < effectList.size(); ++i) {
                    Effect existsEffect = effectList.get(i);
                    if (existsEffect instanceof StaticEffect && ((StaticEffect) existsEffect).specialStatus == ESpecialStatus.HIDE) {
                        existsEffect.destory(_host);
                        effectList.remove(existsEffect);
                    }
                }
            }
        }
    }

    public void executeDynamicEffect(final DynamicEffect _effect, final ME2GameObject _host) {
        if (_effect.trait == Effect.EffectTrait.BUFF) {
            if (_effect.hpResumeValue != 0) {
                FightServiceImpl.getInstance().processHpChange(_effect.releaser, _host, _effect.hpResumeValue, _effect.isDeathblow, _effect.harmMagicType);
            }
            if (_effect.mpResumeValue != 0) {
                _host.addMp(_effect.mpResumeValue);
                FightServiceImpl.getInstance().processSingleTargetMpChange(_host, true);
            }
        } else {
            if (_effect.hpHarmValue != 0) {
                FightServiceImpl.getInstance().processHpChange(_effect.releaser, _host, -_effect.hpHarmValue, _effect.isDeathblow, _effect.harmMagicType);
            }
            if (_effect.mpHarmValue != 0) {
                _host.addMp(-_effect.mpHarmValue);
                FightServiceImpl.getInstance().processSingleTargetMpChange(_host, true);
            }
        }
    }

    public void checkTouchEffect(final ME2GameObject _host, final ME2GameObject _target, final ETouchType _activeTouchType, final boolean _isSkillTouch) {
        if (_host != null && _host.isEnable() && !_host.isDead()) {
            ArrayList<Effect> effectList = null;
            if (_host instanceof HeroPlayer) {
                effectList = ((HeroPlayer) _host).effectList;
                if (effectList != null && effectList.size() > 0) {
                    for (int i = 0; i < effectList.size(); ++i) {
                        Effect effect;
                        try {
                            effect = effectList.get(i);
                        } catch (Exception e) {
                            break;
                        }
                        if (effect instanceof TouchEffect && ((TouchEffect) effect).touchType == _activeTouchType) {
                            ((TouchEffect) effect).touch((HeroPlayer) _host, _target, _activeTouchType, _isSkillTouch, ((TouchEffect) effect).harmMagicType);
                        }
                    }
                }
            }
        }
    }

    public void removeEffect(final ME2GameObject _host, final Effect _effect, final boolean _isTimeEnd, final boolean _isCompelRemoveIcon) {
        ArrayList<Effect> targetEffectList = _host.effectList;
        Label_0160:
        {
            if (_effect.keepTimeType == Effect.EKeepTimeType.N_A) {
                if (_host == _effect.releaser) {
                    if (_effect.aureoleRadiationTargetList.size() <= 0) {
                        break Label_0160;
                    }
                    synchronized (_effect.aureoleRadiationTargetList) {
                        for (int i = 0; i < _effect.aureoleRadiationTargetList.size(); ++i) {
                            ME2GameObject target = _effect.aureoleRadiationTargetList.get(i);
                            if (target.effectList.remove(_effect)) {
                                _effect.destory(target);
                                if (target.isEnable() && !target.isDead()) {
                                    MapSynchronousInfoBroadcast.getInstance().put(target.where(), new RemoveEffectNotify(target, _effect), false, 0);
                                }
                            }
                        }
                        _effect.aureoleRadiationTargetList.clear();
                        // monitorexit(_effect.aureoleRadiationTargetList)
                        break Label_0160;
                    }
                }
                _effect.aureoleRadiationTargetList.remove(_host);
            }
        }
        if (targetEffectList.remove(_effect)) {
            _effect.destory(_host);
            if ((!_host.isDead() && _isTimeEnd) || _isCompelRemoveIcon) {
                RemoveEffectNotify msg = new RemoveEffectNotify(_host, _effect);
                if (_host instanceof HeroPlayer) {
                    ResponseMessageQueue.getInstance().put(((HeroPlayer) _host).getMsgQueueIndex(), msg);
                    MapSynchronousInfoBroadcast.getInstance().put(_host.where(), msg, true, _host.getID());
                } else {
                    MapSynchronousInfoBroadcast.getInstance().put(_host.where(), msg, false, 0);
                }
            }
        }
    }

    private void notifyMoveToPlayer(final StaticEffect _effect, final HeroPlayer _player, final ME2GameObject _other) {
        if (_effect.specialStatus == ESpecialStatus.MOVE_FAST || _effect.specialStatus == ESpecialStatus.MOVE_SLOWLY) {
            byte speedLevel = _effect.getSpecialStatusLevel();
            byte speed = MoveSpeed.getNowSpeed(_other.getMoveSpeedState(), speedLevel);
            MoveSpeedChangerNotify msg = new MoveSpeedChangerNotify(_other.getObjectType().value(), _other.getID(), speed);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
        }
    }

    private void notifyMoveToOther(final StaticEffect _effect, final ME2GameObject _host) {
        ESpecialStatus specialStatus = _effect.specialStatus;
        if (specialStatus != null) {
            switch (specialStatus) {
                case MOVE_FAST: {
                    EffectServiceImpl.log.info("\u8be5\u73a9\u5bb6\u6709\u88ab\u52a0\u901f!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    byte speedLevel = _effect.getSpecialStatusLevel();
                    byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
                    MapSynchronousInfoBroadcast.getInstance().put(_host.where(), new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(), speed), false, 0);
                    break;
                }
                case MOVE_SLOWLY: {
                    EffectServiceImpl.log.info("\u8be5\u73a9\u5bb6\u6709\u88ab\u51cf\u901f!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    byte speedLevel = _effect.getSpecialStatusLevel();
                    byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
                    MapSynchronousInfoBroadcast.getInstance().put(_host.where(), new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(), speed), false, 0);
                    break;
                }
            }
        }
    }

    public void removeMove(final StaticEffect _effect, final ME2GameObject _host) {
        if (_effect != null && _effect.specialStatus != null) {
            ESpecialStatus specialStatus = _effect.specialStatus;
            if (specialStatus != null) {
                switch (specialStatus) {
                    case MOVE_FAST: {
                        EffectServiceImpl.log.info("\u79fb\u9664\u52a0\u901f\u5ea6!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        _host.removeAddSpeedState();
                        byte speedLevel = _effect.getSpecialStatusLevel();
                        byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
                        MapSynchronousInfoBroadcast.getInstance().put(_host.where(), new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(), speed), false, 0);
                        break;
                    }
                    case MOVE_SLOWLY: {
                        EffectServiceImpl.log.info("\u79fb\u9664\u51cf\u901f\u5ea6!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        _host.removeSlowSpeedState();
                        byte speedLevel = _effect.getSpecialStatusLevel();
                        byte speed = MoveSpeed.getNowSpeed(_host.getMoveSpeedState(), speedLevel);
                        MapSynchronousInfoBroadcast.getInstance().put(_host.where(), new MoveSpeedChangerNotify(_host.getObjectType().value(), _host.getID(), speed), false, 0);
                        break;
                    }
                }
            }
        }
    }

    public void cleanEffect(final HeroPlayer _player, final ME2GameObject _host, final Effect.EffectFeature _effectFeature, final byte _effectMaxLevel, final short _number) {
        if (_host.effectList.size() > 0) {
            int totalCleaned = 0;
            int i = 0;
            while (i < _host.effectList.size()) {
                Effect effect = _host.effectList.get(i);
                if (Effect.EKeepTimeType.LIMITED == effect.keepTimeType && Effect.EffectTrait.DEBUFFF == effect.trait && (effect.feature == _effectFeature || _effectFeature == Effect.EffectFeature.ALL) && effect.featureLevel <= _effectMaxLevel) {
                    _host.effectList.remove(i);
                    effect.destory(_host);
                    ++totalCleaned;
                    RemoveEffectNotify msg = new RemoveEffectNotify(_host, effect);
                    if (effect instanceof StaticEffect) {
                        this.removeMove((StaticEffect) effect, _host);
                    }
                    if (_host.getID() == _player.getID()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                        MapSynchronousInfoBroadcast.getInstance().put(_player.where(), msg, true, _player.getID());
                    } else {
                        MapSynchronousInfoBroadcast.getInstance().put(_host.where(), msg, false, 0);
                    }
                    if (totalCleaned >= _number) {
                        return;
                    }
                    continue;
                } else {
                    ++i;
                }
            }
        }
    }

    public void cleanEffect(final HeroPlayer _player, final ME2GameObject _host, final int _effectIDLowLimit, final int _effectIDUperLimit) {
        if (_host.effectList.size() > 0) {
            for (int i = 0; i < _host.effectList.size(); ++i) {
                Effect effect = _host.effectList.get(i);
                if (effect.ID >= _effectIDLowLimit && effect.ID <= _effectIDLowLimit) {
                    _host.effectList.remove(i);
                    effect.destory(_host);
                    RemoveEffectNotify msg = new RemoveEffectNotify(_host, effect);
                    if (effect instanceof StaticEffect) {
                        this.removeMove((StaticEffect) effect, _host);
                    }
                    if (_host.getID() == _player.getID()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                        MapSynchronousInfoBroadcast.getInstance().put(_player.where(), msg, true, _player.getID());
                    } else {
                        MapSynchronousInfoBroadcast.getInstance().put(_host.where(), msg, false, 0);
                    }
                    return;
                }
            }
        }
    }

    public class MonsterEffectCheckTask extends TimerTask {

        @Override
        public void run() {
            if (EffectServiceImpl.this.existsEffectMonsterList.size() > 0) {
                int i = 0;
                while (i < EffectServiceImpl.this.existsEffectMonsterList.size()) {
                    Monster monster = (Monster) EffectServiceImpl.this.existsEffectMonsterList.get(i);
                    if (monster.effectList.size() > 0) {
                        int j = 0;
                        while (j < monster.effectList.size()) {
                            try {
                                Effect effect = monster.effectList.get(j);
                                if (!effect.heartbeat(monster)) {
                                    continue;
                                }
                                ++j;
                            } catch (Exception e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    }
                    if (monster.effectList.size() == 0) {
                        EffectServiceImpl.this.existsEffectMonsterList.remove(i);
                    } else {
                        ++i;
                    }
                }
            }
        }
    }
}
