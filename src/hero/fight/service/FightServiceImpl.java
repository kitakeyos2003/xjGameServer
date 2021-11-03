// 
// Decompiled by Procyon v0.5.36
// 
package hero.fight.service;

import hero.group.service.GroupServiceImpl;
import hero.fight.message.MpRefreshNotify;
import hero.player.service.PlayerServiceImpl;
import hero.fight.message.AttackMissNotify;
import hero.effect.message.RemoveEffectNotify;
import hero.effect.detail.DynamicEffect;
import hero.duel.service.DuelServiceImpl;
import hero.player.service.PlayerDAO;
import hero.npc.Monster;
import hero.skill.detail.EMathCaluOperator;
import hero.effect.detail.StaticEffect;
import hero.effect.Effect;
import java.util.ArrayList;
import hero.fight.broadcast.HpChangeBroadcast;
import hero.fight.message.HpRefreshNotify;
import hero.item.enhance.EnhanceService;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import yoyo.core.queue.ResponseMessageQueue;
import hero.fight.message.GenericAttackViewNotify;
import hero.item.Weapon;
import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.skill.service.SkillServiceImpl;
import hero.skill.detail.ETouchType;
import hero.item.service.GoodsServiceImpl;
import hero.share.EMagic;
import hero.expressions.service.CEService;
import hero.effect.service.EffectServiceImpl;
import hero.share.EObjectType;
import hero.share.ME2GameObject;
import java.util.Random;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class FightServiceImpl extends AbsServiceAdaptor<FightConfig> {

    private static Logger log;
    private static final Random RANDOM;
    private static FightServiceImpl instance;

    static {
        FightServiceImpl.log = Logger.getLogger((Class) FightServiceImpl.class);
        RANDOM = new Random();
    }

    private FightServiceImpl() {
        this.config = new FightConfig();
    }

    public static FightServiceImpl getInstance() {
        if (FightServiceImpl.instance == null) {
            FightServiceImpl.instance = new FightServiceImpl();
        }
        return FightServiceImpl.instance;
    }

    @Override
    protected void start() {
    }

    public void processPhysicsAttack(final ME2GameObject _attacker, final ME2GameObject _target) {
        this.refreshFightTime(_attacker, _target);
        if (!_attacker.isVisible() && _attacker.getObjectType() == EObjectType.PLAYER) {
            EffectServiceImpl.getInstance().clearHideEffect(_target);
        }
        this.processGenericPhysicsAttackView(_attacker, _target);
        float attackerPhysicsHitOdds = CEService.attackPhysicsHitOdds(_attacker.getActualProperty().getLucky(), _attacker.getActualProperty().getHitLevel(), _attacker.getLevel(), _target.getLevel());
        float targetPhysicsDuckOdds = CEService.attackPhysicsDuckOdds(_attacker.getLevel(), _target.getActualProperty().getAgility(), _target.getActualProperty().getLucky(), _target.getActualProperty().getPhysicsDuckLevel(), _target.getLevel());
        int value = 0;
        if (FightServiceImpl.RANDOM.nextInt(100) <= attackerPhysicsHitOdds - targetPhysicsDuckOdds) {
            value = CEService.physicsHarm(_attacker.getLevel(), _attacker.getActualProperty().getActualPhysicsAttack(), _target.getLevel(), _target.getActualProperty().getDefense());
            boolean isDeathblow = false;
            float physicsDeathblowOdds = CEService.attackPhysicsDeathblowOdds(_attacker.getActualProperty().getAgility(), _attacker.getActualProperty().getPhysicsDeathblowLevel(), _attacker.getLevel(), _target.getLevel());
            if (FightServiceImpl.RANDOM.nextInt(100) <= physicsDeathblowOdds - _target.getResistOddsList().physicsDeathblowOdds) {
                value = CEService.calculateDeathblowHarm(value, _attacker.getActualProperty().getLucky());
                isDeathblow = true;
            }
            this.processReduceHp(_attacker, _target, value, true, isDeathblow, null);
            GoodsServiceImpl.getInstance().processEquipmentDurabilityInFighting(_attacker, _target);
            if (_attacker.getObjectType() == EObjectType.PLAYER) {
                if (isDeathblow) {
                    EffectServiceImpl.getInstance().checkTouchEffect(_attacker, _target, ETouchType.TOUCH_DEATHBLOW, false);
                    SkillServiceImpl.getInstance().checkTouchSkill(_attacker, _target, ETouchType.TOUCH_DEATHBLOW, false);
                }
                EffectServiceImpl.getInstance().checkTouchEffect(_attacker, _target, ETouchType.ATTACK_BY_PHYSICS, false);
                SkillServiceImpl.getInstance().checkTouchSkill(_attacker, _target, ETouchType.ATTACK_BY_PHYSICS, false);
                EffectServiceImpl.getInstance().checkTouchEffect(_attacker, _target, ((HeroPlayer) _attacker).isRemotePhysicsAttack ? ETouchType.ATTACK_BY_DISTANCE_PHYSICS : ETouchType.ATTACK_BY_NEAR_PHYSICS, false);
                SkillServiceImpl.getInstance().checkTouchSkill(_attacker, _target, ((HeroPlayer) _attacker).isRemotePhysicsAttack ? ETouchType.ATTACK_BY_DISTANCE_PHYSICS : ETouchType.ATTACK_BY_NEAR_PHYSICS, false);
                if (_target.getObjectType() == EObjectType.PLAYER) {
                    if (isDeathblow) {
                        EffectServiceImpl.getInstance().checkTouchEffect(_target, _attacker, ETouchType.BE_DEATHBLOW_BY_PHYSICS, false);
                        SkillServiceImpl.getInstance().checkTouchSkill(_target, _attacker, ETouchType.BE_DEATHBLOW_BY_PHYSICS, false);
                    }
                    EffectServiceImpl.getInstance().checkTouchEffect(_target, _attacker, ((HeroPlayer) _attacker).isRemotePhysicsAttack ? ETouchType.BE_ATTACKED_BY_DISTANCE_PHYSICS : ETouchType.BE_ATTACKED_BY_NEAR_PHYSICS, false);
                    SkillServiceImpl.getInstance().checkTouchSkill(_target, _attacker, ((HeroPlayer) _attacker).isRemotePhysicsAttack ? ETouchType.ATTACK_BY_DISTANCE_PHYSICS : ETouchType.ATTACK_BY_NEAR_PHYSICS, false);
                }
            }
        } else {
            this.processMiss(_attacker, _target);
        }
        if (_attacker.getObjectType() == EObjectType.PLAYER) {
            ((HeroPlayer) _attacker).lastAttackTime = System.currentTimeMillis();
        }
        if (_attacker.getObjectType() == EObjectType.PET) {
            ((Pet) _attacker).lastAttackTime = System.currentTimeMillis();
        }
    }

    public short[] getFightTarget(final Weapon.EWeaponType _eType) {
        short[] result = {-1, -1};
        if (Weapon.EWeaponType.TTYPE_ZHANG == _eType) {
            result = ((FightConfig) this.config).staff_attack_target_animation;
        } else if (Weapon.EWeaponType.TYPE_BISHOU == _eType) {
            result = ((FightConfig) this.config).dagger_attack_target_animation;
        } else if (Weapon.EWeaponType.TYPE_CHUI == _eType) {
            result = ((FightConfig) this.config).hammer_attack_target_animation;
        } else if (Weapon.EWeaponType.TYPE_GONG == _eType) {
            result = ((FightConfig) this.config).bow_attack_target_animation;
        } else if (Weapon.EWeaponType.TYPE_JIAN == _eType) {
            result = ((FightConfig) this.config).sword_attack_target_animation;
        }
        return result;
    }

    public void processGenericPhysicsAttackView(final ME2GameObject _attacker, final ME2GameObject _target) {
        FightServiceImpl.log.debug((Object) ("processGenericPhysicsAttackView : attacker = " + _attacker.getObjectType() + " ,id=" + _attacker.getID() + " , _target = " + _target.getObjectType().value() + " , _tartget id=" + _target.getID()));
        AbsResponseMessage msg = new GenericAttackViewNotify(_attacker.getObjectType().value(), _attacker.getID(), _target.getObjectType().value(), _target.getID());
        if (EObjectType.MONSTER == _attacker.getObjectType() && EObjectType.PLAYER == _target.getObjectType()) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _target).getMsgQueueIndex(), msg);
            MapSynchronousInfoBroadcast.getInstance().put(_attacker.where(), new GenericAttackViewNotify(_attacker.getObjectType().value(), _attacker.getID(), _target.getObjectType().value(), _target.getID()), true, _target.getID());
        } else {
            if (EObjectType.PLAYER == _attacker.getObjectType()) {
                ResponseMessageQueue.getInstance().put(((HeroPlayer) _attacker).getMsgQueueIndex(), new GenericAttackViewNotify(_attacker.getObjectType().value(), _attacker.getID(), _target.getObjectType().value(), _target.getID(), _attacker));
            }
            MapSynchronousInfoBroadcast.getInstance().put(_attacker.where(), new GenericAttackViewNotify(_attacker.getObjectType().value(), _attacker.getID(), _target.getObjectType().value(), _target.getID(), _attacker), true, _attacker.getID());
        }
    }

    public void processTargetDie(final ME2GameObject _attacker, final ME2GameObject _target) {
        if (_attacker.getObjectType() == EObjectType.PLAYER) {
            EnhanceService.getInstance().processWeaponEnhance((HeroPlayer) _attacker, _target);
        }
        _target.die(_attacker);
    }

    public void processAddHp(final ME2GameObject _trigger, final ME2GameObject _target, final int _value, final boolean _visible, final boolean _isDeathblow) {
        if (_value > 0) {
            int validHp = _target.getActualProperty().getHpMax() - _target.getHp();
            if (validHp > 0) {
                validHp = ((validHp >= _value) ? _value : validHp);
                _target.addHp(validHp);
                if (_target.getObjectType() == EObjectType.PLAYER) {
                    ((HeroPlayer) _target).beResumeHpByOthers((HeroPlayer) _trigger, validHp);
                }
                AbsResponseMessage refreshHpMsg = new HpRefreshNotify(_target.getObjectType().value(), _target.getID(), _target.getHp(), _value, _visible, _isDeathblow);
                if (_trigger != null && _trigger.getObjectType() == EObjectType.PLAYER) {
                    ResponseMessageQueue.getInstance().put(((HeroPlayer) _trigger).getMsgQueueIndex(), refreshHpMsg);
                }
                if (_target != _trigger && _target != null && _target.getObjectType() == EObjectType.PLAYER) {
                    ResponseMessageQueue.getInstance().put(((HeroPlayer) _target).getMsgQueueIndex(), refreshHpMsg);
                }
                HpChangeBroadcast.put(_trigger, _target, _value);
            }
        }
    }

    private float changeValueByEffect(final ArrayList<Effect> _effectList, final float _value, final EMagic _accMagic) {
        float result = 0.0f;
        if (_effectList.size() > 0) {
            float multiplier = 0.0f;
            Effect effect = null;
            EMathCaluOperator operator = null;
            for (int i = 0; i < _effectList.size(); ++i) {
                effect = _effectList.get(i);
                if (effect instanceof StaticEffect) {
                    StaticEffect sEffect = (StaticEffect) effect;
                    operator = sEffect.caluOperator;
                    if (_accMagic != null) {
                        if (sEffect.magicHarmValueBeAttack > 0.0f && (sEffect.magicHarmTypeBeAttack == EMagic.ALL || sEffect.magicHarmTypeBeAttack == _accMagic)) {
                            multiplier = sEffect.magicHarmValueBeAttack;
                            FightServiceImpl.log.info((Object) ("\u653b\u51fb\u7c7b\u578b:" + _accMagic.getName() + " \u4f24\u5bb3\u52a0\u6210:" + String.valueOf(multiplier) + " \u7279\u6548\u7c7b\u578b:" + sEffect.magicHarmTypeBeAttack.getName()));
                            result += (int) this.changeValue(_value, multiplier, operator);
                        }
                    } else if (sEffect.bePhysicsHarmValue > 0.0f) {
                        multiplier = sEffect.bePhysicsHarmValue;
                        FightServiceImpl.log.info((Object) ("\u653b\u51fb\u7c7b\u578b:\u7269\u7406;\u4f24\u5bb3\u52a0\u6210:" + String.valueOf(multiplier)));
                        result += (int) this.changeValue(_value, multiplier, operator);
                    }
                }
            }
        }
        result += _value;
        return result;
    }

    private float changeValue(final float _baseValue, float _caluModulus, final EMathCaluOperator _operator) {
        float result = 0.0f;
        if (_caluModulus > 0.0f && _operator != null) {
            if (_caluModulus > 1.0f) {
                --_caluModulus;
            } else {
                _caluModulus = -(1.0f - _caluModulus);
            }
            switch (_operator) {
                case ADD: {
                    result = _baseValue + _caluModulus;
                    break;
                }
                case DEC: {
                    result = _baseValue - _caluModulus;
                    break;
                }
                case MUL: {
                    result = _baseValue * _caluModulus;
                    break;
                }
                case DIV: {
                    result = _baseValue * ((_caluModulus - 1.0f) / _caluModulus);
                    break;
                }
            }
        }
        return result;
    }

    public boolean processReduceHp(final ME2GameObject _trigger, final ME2GameObject _target, int _value, final boolean _visible, final boolean _isDeathblow, final EMagic _accMagic) {
        if (_target.isDead()) {
            FightServiceImpl.log.warn((Object) "target.isDead! hp porcess cancel");
            return true;
        }
        if (_trigger.getObjectType() == EObjectType.PLAYER && _target.getObjectType() == EObjectType.MONSTER) {
            ((Monster) _target).setAttackerAtFirst((HeroPlayer) _trigger);
        }
        if (_value >= 0 && _target.getObjectType() == EObjectType.MONSTER && _trigger.getObjectType() == EObjectType.PLAYER && !_target.isDead()) {
            ((Monster) _target).beHarmed((HeroPlayer) _trigger, (_target.getHp() >= _value) ? _value : _target.getHp());
        }
        if (_value > 0) {
            if (!_target.isVisible() && _target.getObjectType() == EObjectType.PLAYER) {
                EffectServiceImpl.getInstance().clearHideEffect(_target);
            }
            _value = (int) this.changeValueByEffect(_target.effectList, (float) _value, _accMagic);
            _value = (int) this.changeValueByEffect(_trigger.effectList, (float) _value, _accMagic);
            _target.addHp(-_value);
            AbsResponseMessage refreshHpMsg = new HpRefreshNotify(_target.getObjectType().value(), _target.getID(), _target.getHp(), -_value, _visible, _isDeathblow);
            if (_trigger != null && _trigger.getObjectType() == EObjectType.PLAYER) {
                ResponseMessageQueue.getInstance().put(((HeroPlayer) _trigger).getMsgQueueIndex(), refreshHpMsg);
            }
            if (_target != _trigger && _target != null && _target.getObjectType() == EObjectType.PLAYER) {
                ResponseMessageQueue.getInstance().put(((HeroPlayer) _target).getMsgQueueIndex(), refreshHpMsg);
            }
            HpChangeBroadcast.put(_trigger, _target, -_value);
            if (_trigger.getHp() == 0 && _target instanceof HeroPlayer && _trigger instanceof HeroPlayer) {
                HeroPlayer target = (HeroPlayer) _target;
                HeroPlayer trigger = (HeroPlayer) _trigger;
                if (target.getClan() != trigger.getClan()) {
                    PlayerDAO.insertPvpInfo(target.getUserID(), target.getVocation().value(), trigger.getUserID(), trigger.getVocation().value());
                }
            }
            if (_target.getHp() == 0) {
                if (_target instanceof HeroPlayer && _trigger instanceof HeroPlayer) {
                    HeroPlayer target = (HeroPlayer) _target;
                    HeroPlayer trigger = (HeroPlayer) _trigger;
                    if (target.getClan() != trigger.getClan()) {
                        PlayerDAO.insertPvpInfo(trigger.getUserID(), trigger.getVocation().value(), target.getUserID(), target.getVocation().value());
                    }
                }
                if (_target instanceof HeroPlayer && _trigger instanceof HeroPlayer) {
                    HeroPlayer targetPlayer = (HeroPlayer) _target;
                    if (DuelServiceImpl.getInstance().isDueling(targetPlayer.getUserID(), ((HeroPlayer) _trigger).getUserID())) {
                        ArrayList<Effect> newList = new ArrayList<Effect>();
                        FightServiceImpl.log.info((Object) ("\u5bf9\u8c61\u51b3\u6597\u5931\u8d25,\u8eab\u4e0a\u6709\u6548\u679c:" + targetPlayer.effectList.size()));
                        for (int i = 0; i < targetPlayer.effectList.size(); ++i) {
                            Effect ef = targetPlayer.effectList.get(i);
                            if (ef instanceof DynamicEffect && ((DynamicEffect) ef).hpHarmTotal > 0) {
                                RemoveEffectNotify msg = new RemoveEffectNotify(_target, ef);
                                MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg, false, 0);
                            } else {
                                newList.add(ef);
                            }
                        }
                        targetPlayer.effectList = newList;
                        FightServiceImpl.log.info((Object) ("\u5bf9\u8c61\u51b3\u6597\u5931\u8d25,\u8eab\u4e0a\u4fdd\u7559\u6548\u679c:" + targetPlayer.effectList.size()));
                        _target.setHp(1);
                        DuelServiceImpl.getInstance().wonDuel((HeroPlayer) _trigger);
                        return true;
                    }
                    if (targetPlayer.getClan() == _trigger.getClan()) {
                        FightServiceImpl.log.info((Object) ("!!\u53d7\u5230\u975e\u51b3\u6597\u6a21\u5f0f\u4e0b\u7684\u540c\u9635\u8425\u81f4\u547d\u653b\u51fb\u4f24\u5bb3,\u8eab\u4e0a\u6709\u6548\u679c:" + targetPlayer.effectList.size()));
                        ArrayList<Effect> newList = new ArrayList<Effect>();
                        for (int i = 0; i < targetPlayer.effectList.size(); ++i) {
                            Effect ef = targetPlayer.effectList.get(i);
                            if (ef instanceof DynamicEffect && ((DynamicEffect) ef).hpHarmTotal > 0) {
                                RemoveEffectNotify msg = new RemoveEffectNotify(_target, ef);
                                MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg, false, 0);
                            } else {
                                newList.add(ef);
                            }
                        }
                        targetPlayer.effectList = newList;
                        FightServiceImpl.log.info((Object) ("!!\u53d7\u5230\u975e\u51b3\u6597\u6a21\u5f0f\u4e0b\u7684\u540c\u9635\u8425\u81f4\u547d\u653b\u51fb\u4f24\u5bb3,\u8eab\u4e0a\u6709\u6548\u679c:" + targetPlayer.effectList.size()));
                        _target.setHp(1);
                        return true;
                    }
                }
                this.processTargetDie(_trigger, _target);
                return true;
            }
        }
        return false;
    }

    public void processHpChange(final ME2GameObject _trigger, final ME2GameObject _target, int _value, final boolean _isDeathblow, final EMagic _accMagic) {
        if (_target.isDead()) {
            return;
        }
        if (_trigger.getObjectType() == EObjectType.PLAYER && _target.getObjectType() == EObjectType.MONSTER) {
            ((Monster) _target).setAttackerAtFirst((HeroPlayer) _trigger);
        }
        if (_trigger != null && _value < 0 && _target.getObjectType() == EObjectType.MONSTER && _trigger.getObjectType() == EObjectType.PLAYER) {
            ((Monster) _target).beHarmed((HeroPlayer) _trigger, (_target.getHp() >= -_value) ? (-_value) : _target.getHp());
        }
        if (_target.getObjectType() == EObjectType.PLAYER && _value < 0) {
            _value = (int) this.changeValueByEffect(_target.effectList, (float) _value, _accMagic);
        }
        if (!_target.isVisible() && _value < 0) {
            EffectServiceImpl.getInstance().clearHideEffect(_target);
        }
        _target.addHp(_value);
        AbsResponseMessage refreshHpMsg = new HpRefreshNotify(_target.getObjectType().value(), _target.getID(), _target.getHp(), _value, true, _isDeathblow);
        if (_trigger != null && _trigger.getObjectType() == EObjectType.PLAYER) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _trigger).getMsgQueueIndex(), refreshHpMsg);
        }
        if (_target != _trigger && _target != null && _target.getObjectType() == EObjectType.PLAYER) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _target).getMsgQueueIndex(), refreshHpMsg);
        }
        HpChangeBroadcast.put(_trigger, _target, _value);
        if (_trigger.getHp() == 0 && _target instanceof HeroPlayer && _trigger instanceof HeroPlayer) {
            HeroPlayer target = (HeroPlayer) _target;
            HeroPlayer trigger = (HeroPlayer) _trigger;
            if (target.getClan() != trigger.getClan()) {
                PlayerDAO.insertPvpInfo(target.getUserID(), target.getVocation().value(), trigger.getUserID(), trigger.getVocation().value());
            }
        }
        if (_target.getHp() == 0) {
            if (_target instanceof HeroPlayer && _trigger instanceof HeroPlayer) {
                HeroPlayer target = (HeroPlayer) _target;
                HeroPlayer trigger = (HeroPlayer) _trigger;
                if (target.getClan() != trigger.getClan()) {
                    PlayerDAO.insertPvpInfo(trigger.getUserID(), trigger.getVocation().value(), target.getUserID(), target.getVocation().value());
                }
            }
            if (_target instanceof HeroPlayer && _trigger instanceof HeroPlayer) {
                HeroPlayer targetPlayer = (HeroPlayer) _target;
                if (DuelServiceImpl.getInstance().isDueling(targetPlayer.getUserID(), ((HeroPlayer) _trigger).getUserID())) {
                    ArrayList<Effect> newList = new ArrayList<Effect>();
                    FightServiceImpl.log.info((Object) ("\u5bf9\u8c61\u51b3\u6597\u5931\u8d25,\u8eab\u4e0a\u6709\u6548\u679c:" + targetPlayer.effectList.size()));
                    for (int i = 0; i < targetPlayer.effectList.size(); ++i) {
                        Effect ef = targetPlayer.effectList.get(i);
                        if (ef instanceof DynamicEffect && ((DynamicEffect) ef).hpHarmTotal > 0) {
                            RemoveEffectNotify msg = new RemoveEffectNotify(_target, ef);
                            MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg, false, 0);
                        } else {
                            newList.add(ef);
                        }
                    }
                    targetPlayer.effectList = newList;
                    FightServiceImpl.log.info((Object) ("\u5bf9\u8c61\u51b3\u6597\u5931\u8d25,\u8eab\u4e0a\u4fdd\u7559\u6548\u679c:" + targetPlayer.effectList.size()));
                    _target.setHp(1);
                    DuelServiceImpl.getInstance().wonDuel((HeroPlayer) _trigger);
                    return;
                }
                if (targetPlayer.getClan() == _trigger.getClan()) {
                    FightServiceImpl.log.info((Object) ("!!\u53d7\u5230\u975e\u51b3\u6597\u6a21\u5f0f\u4e0b\u7684\u540c\u9635\u8425\u81f4\u547d\u653b\u51fb\u4f24\u5bb3,\u8eab\u4e0a\u6709\u6548\u679c:" + targetPlayer.effectList.size()));
                    ArrayList<Effect> newList = new ArrayList<Effect>();
                    for (int i = 0; i < targetPlayer.effectList.size(); ++i) {
                        Effect ef = targetPlayer.effectList.get(i);
                        if (ef instanceof DynamicEffect && ((DynamicEffect) ef).hpHarmTotal > 0) {
                            RemoveEffectNotify msg = new RemoveEffectNotify(_target, ef);
                            MapSynchronousInfoBroadcast.getInstance().put(_target.where(), msg, false, 0);
                        } else {
                            newList.add(ef);
                        }
                    }
                    targetPlayer.effectList = newList;
                    FightServiceImpl.log.info((Object) ("!!\u53d7\u5230\u975e\u51b3\u6597\u6a21\u5f0f\u4e0b\u7684\u540c\u9635\u8425\u81f4\u547d\u653b\u51fb\u4f24\u5bb3,\u8eab\u4e0a\u6709\u6548\u679c:" + targetPlayer.effectList.size()));
                    _target.setHp(1);
                    return;
                }
            }
            this.processTargetDie(_trigger, _target);
        }
    }

    public void processMiss(final ME2GameObject _trigger, final ME2GameObject _target) {
        if (_target.getObjectType() == EObjectType.MONSTER && _target.isEnable() && !_target.isDead() && _trigger.getObjectType() == EObjectType.PLAYER) {
            ((Monster) _target).beHarmed((HeroPlayer) _trigger, 0);
        }
        AbsResponseMessage hpChangeViewMsg = new AttackMissNotify(_target.getObjectType().value(), _target.getID());
        if (_trigger != null && _trigger.getObjectType() == EObjectType.PLAYER) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _trigger).getMsgQueueIndex(), hpChangeViewMsg);
        }
        if (_target != _trigger && _target != null && _target.getObjectType() == EObjectType.PLAYER) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _target).getMsgQueueIndex(), hpChangeViewMsg);
        }
        if (_target != _trigger && _target != null && _trigger.getObjectType() == EObjectType.PET) {
            Pet pet = (Pet) _trigger;
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(pet.masterID);
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), hpChangeViewMsg);
        }
    }

    public void processSingleTargetMpChange(final ME2GameObject _target, final boolean _visible) {
        AbsResponseMessage refreshMpMsg = new MpRefreshNotify(_target.getObjectType().value(), _target.getID(), _target.getMp(), _visible);
        if (_target != null && _target.getObjectType() == EObjectType.PLAYER) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _target).getMsgQueueIndex(), refreshMpMsg);
        }
        MapSynchronousInfoBroadcast.getInstance().put(_target.where(), refreshMpMsg, true, _target.getID());
    }

    public void processPersionalForceQuantityChange(final ME2GameObject _object) {
        AbsResponseMessage refreshMpMsg = new MpRefreshNotify(_object.getObjectType().value(), _object.getID(), _object.getForceQuantity(), false);
        if (_object != null && _object.getObjectType() == EObjectType.PLAYER) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _object).getMsgQueueIndex(), refreshMpMsg);
        }
        MapSynchronousInfoBroadcast.getInstance().put(_object.where(), refreshMpMsg, true, _object.getID());
    }

    public void processPersionalHpChange(final ME2GameObject _object, final int _value) {
        AbsResponseMessage refreshHpMsg = new HpRefreshNotify(_object.getObjectType().value(), _object.getID(), _object.getHp(), _value, false, false);
        if (_object != null && _object.getObjectType() == EObjectType.PLAYER) {
            ResponseMessageQueue.getInstance().put(((HeroPlayer) _object).getMsgQueueIndex(), refreshHpMsg);
            GroupServiceImpl.getInstance().groupMemberListHpMpNotify((HeroPlayer) _object);
        }
        MapSynchronousInfoBroadcast.getInstance().put(_object.where(), refreshHpMsg, true, _object.getID());
    }

    public void refreshFightTime(final ME2GameObject _attacker, final ME2GameObject _target) {
        if (_attacker.getObjectType() == EObjectType.MONSTER) {
            _attacker.happenFight();
        } else if (_target.getObjectType() == EObjectType.PLAYER) {
            ((HeroPlayer) _attacker).refreshPvPFightTime(((HeroPlayer) _target).getUserID());
            ((HeroPlayer) _target).refreshPvPFightTime(((HeroPlayer) _attacker).getUserID());
        } else {
            _target.happenFight();
        }
    }
}
