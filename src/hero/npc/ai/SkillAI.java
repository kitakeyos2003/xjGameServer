// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.ai;

import java.util.Iterator;
import java.util.ArrayList;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.skill.service.SkillServiceImpl;
import hero.npc.message.RangeSkillWarning;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.ETargetRangeType;
import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.MonsterShoutNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.skill.unit.ActiveSkillUnit;
import hero.npc.Monster;
import java.util.Random;
import hero.npc.ai.data.SkillAIData;
import org.apache.log4j.Logger;

public class SkillAI {

    private static Logger log;
    private boolean hasExecuted;
    private float lastTraceHpPercent;
    private long timeThatLastExecuted;
    private long releaseWarningTime;
    private int targetPlayerUserID;
    private SkillAIData data;
    public static final byte USE_TIMES_OF_INTERVAL = 1;
    public static final byte USE_TIMES_OF_ONLY = 2;
    public static final byte INTERVAL_CONDITION_OF_TIME = 1;
    public static final byte INTERVAL_CONDITION_OF_HP_CONSUME = 2;
    public static final byte INTERVAL_CONDITION_OF_HATRED_TARGET_DIE = 3;
    public static final byte ONLY_CONDITION_OF_FIGHT_TIME = 1;
    public static final byte ONLY_CONDITION_OF_TRACE_HP = 2;
    public static final byte ONLY_CONDITION_OF_TRACE_MP = 3;
    public static final byte TARGET_SET_CONDITION_OF_HATRED = 1;
    public static final byte TARGET_SET_CONDITION_OF_HP = 2;
    public static final byte TARGET_SET_CONDITION_OF_MP = 3;
    public static final byte TARGET_SET_CONDITION_OF_DISTANCE = 4;
    public static final byte TARGET_SET_CONDITION_OF_SELF_CENTER = 5;
    private static final byte RELEASE_FAILER_OF_ODDS = -1;
    private static final byte RELEASE_FAILER_OF_SKILL_CONDITION = 0;
    private static final byte RELEASE_WAIT = 2;
    private static final byte RELEASE_SUCCESS = 1;
    private static final Random RANDOM;

    static {
        SkillAI.log = Logger.getLogger((Class) SkillAI.class);
        RANDOM = new Random();
    }

    public SkillAI(final SkillAIData _data, final float _traceHpPercent) {
        this.data = _data;
        this.lastTraceHpPercent = _traceHpPercent;
    }

    public void execute(final Monster _dominator) {
        if (1 == this.data.useTimesType) {
            if (this.data.intervalCondition == 1) {
                long currentTime = System.currentTimeMillis();
                if (this.data == null || this.data.skill == null || this.data.skill.skillUnit == null) {
                    SkillAI.log.warn((Object) "\u602a\u7269\u6570\u636e\u4e3aNULL.\u8bf7\u68c0\u67e5\u602a\u7269AI\u8868\u683c");
                    return;
                }
                if (currentTime - this.timeThatLastExecuted >= this.data.intervalTime && currentTime - this.timeThatLastExecuted >= ((ActiveSkillUnit) this.data.skill.skillUnit).releaseTime) {
                    byte releaseResult = this.releaseSkill(_dominator);
                    if (releaseResult != 0) {
                        this.timeThatLastExecuted = currentTime;
                    }
                    if (1 == releaseResult) {
                        _dominator.refreshLastAttackTime();
                    }
                }
            } else if (this.data.intervalCondition == 2) {
                if (this.lastTraceHpPercent - _dominator.getHPPercent() >= this.data.hpConsumePercent) {
                    byte releaseResult2 = this.releaseSkill(_dominator);
                    if (releaseResult2 != 0) {
                        this.lastTraceHpPercent = _dominator.getHPPercent();
                    }
                    if (1 == releaseResult2) {
                        _dominator.refreshLastAttackTime();
                    }
                }
            } else {
                byte intervalCondition = this.data.intervalCondition;
            }
        } else if (!this.hasExecuted) {
            if (this.data.onlyReleaseCondition == 1) {
                if (System.currentTimeMillis() - _dominator.getTimeOfEnterFight() >= this.data.timeOfFighting) {
                    byte releaseResult2 = this.releaseSkill(_dominator);
                    if (releaseResult2 != 0) {
                        this.hasExecuted = true;
                    }
                    if (1 == releaseResult2) {
                        _dominator.refreshLastAttackTime();
                    }
                }
            } else if (this.data.onlyReleaseCondition == 2) {
                if (_dominator.getHPPercent() <= this.data.hpTracePercent) {
                    byte releaseResult2 = this.releaseSkill(_dominator);
                    if (releaseResult2 != 0) {
                        this.hasExecuted = true;
                    }
                    if (1 == releaseResult2) {
                        _dominator.refreshLastAttackTime();
                    }
                }
            } else if (this.data.onlyReleaseCondition == 3) {
                if (_dominator.getMPPercent() <= this.data.mpTracePercent) {
                    byte releaseResult2 = this.releaseSkill(_dominator);
                    if (releaseResult2 != 0) {
                        this.hasExecuted = true;
                    }
                    if (1 == releaseResult2) {
                        _dominator.refreshLastAttackTime();
                    }
                }
            } else {
                this.hasExecuted = true;
            }
        }
    }

    private byte releaseSkill(final Monster _dominator) {
        if (this.data.releaseDelay > 0) {
            if (this.releaseWarningTime == 0L) {
                if (SkillAI.RANDOM.nextFloat() > this.data.odds) {
                    return -1;
                }
                this.releaseWarningTime = System.currentTimeMillis();
                MapSynchronousInfoBroadcast.getInstance().put(_dominator.where(), new MonsterShoutNotify(_dominator.getID(), this.data.shoutContentWhenRelease), false, 0);
                HeroPlayer target = this.getTarget(_dominator);
                if (target != null) {
                    this.targetPlayerUserID = target.getUserID();
                    if (ETargetRangeType.SOME == ((ActiveSkillUnit) this.data.skill.skillUnit).targetRangeType) {
                        RangeSkillWarning msg = null;
                        if (EAOERangeType.CENTER == ((ActiveSkillUnit) this.data.skill.skillUnit).rangeMode) {
                            if (EAOERangeBaseLine.RELEASER == ((ActiveSkillUnit) this.data.skill.skillUnit).rangeBaseLine) {
                                msg = new RangeSkillWarning(((ActiveSkillUnit) this.data.skill.skillUnit).rangeX, ((ActiveSkillUnit) this.data.skill.skillUnit).rangeY, true, _dominator.getObjectType().value(), _dominator.getID(), 0, 0);
                            } else {
                                msg = new RangeSkillWarning(((ActiveSkillUnit) this.data.skill.skillUnit).rangeX, ((ActiveSkillUnit) this.data.skill.skillUnit).rangeY, true, target.getObjectType().value(), target.getID(), 0, 0);
                            }
                        } else {
                            int upperLeftX = 0;
                            int upperLeftY = 0;
                            int xLength = 0;
                            int yLength = 0;
                            switch (_dominator.getDirection()) {
                                case 1: {
                                    xLength = ((ActiveSkillUnit) this.data.skill.skillUnit).rangeX;
                                    yLength = ((ActiveSkillUnit) this.data.skill.skillUnit).rangeY;
                                    int radiu = xLength / 2;
                                    upperLeftX = _dominator.getCellX() - radiu;
                                    upperLeftY = _dominator.getCellY() - yLength;
                                    break;
                                }
                                case 2: {
                                    xLength = ((ActiveSkillUnit) this.data.skill.skillUnit).rangeX;
                                    yLength = ((ActiveSkillUnit) this.data.skill.skillUnit).rangeY;
                                    int radiu = xLength / 2;
                                    upperLeftX = _dominator.getCellX() - radiu;
                                    upperLeftY = _dominator.getCellY() + 1;
                                    break;
                                }
                                case 3: {
                                    xLength = ((ActiveSkillUnit) this.data.skill.skillUnit).rangeY;
                                    yLength = ((ActiveSkillUnit) this.data.skill.skillUnit).rangeX;
                                    int radiu = yLength / 2;
                                    upperLeftX = _dominator.getCellX() - xLength;
                                    upperLeftY = _dominator.getCellY() - radiu;
                                    break;
                                }
                                case 4: {
                                    xLength = ((ActiveSkillUnit) this.data.skill.skillUnit).rangeY;
                                    yLength = ((ActiveSkillUnit) this.data.skill.skillUnit).rangeX;
                                    int radiu = yLength / 2;
                                    upperLeftX = _dominator.getCellX() + 1;
                                    upperLeftY = _dominator.getCellY() + radiu;
                                    break;
                                }
                            }
                            msg = new RangeSkillWarning(xLength, yLength, false, 0, 0, upperLeftX, upperLeftY);
                        }
                        MapSynchronousInfoBroadcast.getInstance().put(_dominator.where(), msg, false, 0);
                    } else if (ETargetRangeType.SINGLE == ((ActiveSkillUnit) this.data.skill.skillUnit).targetRangeType) {
                        SkillServiceImpl.getInstance().monsterReleaseSkill(_dominator, target, _dominator.getDirection(), this.data.skill);
                        SkillAI.log.info((Object) ("\u602a\u7269\u91ca\u653e:" + this.data.skill.name));
                        SkillAI.log.info((Object) ("\u6280\u80fd\u5355\u5143:" + this.data.skill.skillUnit.name));
                        if (this.data.skill.addEffectUnit == null) {
                            SkillAI.log.info((Object) "\u6280\u80fd\u9644\u5e26\u7684\u6548\u679c\u5355\u5143\u4e3anull");
                        }
                    }
                    return 2;
                }
                return -1;
            } else {
                if (System.currentTimeMillis() - this.releaseWarningTime < this.data.releaseDelay) {
                    return 2;
                }
                HeroPlayer target = PlayerServiceImpl.getInstance().getPlayerByUserID(this.targetPlayerUserID);
                if (target == null || !target.isEnable() || target.isDead() || target.where() != _dominator.where()) {
                    target = this.getTarget(_dominator);
                    if (target != null) {
                        this.targetPlayerUserID = target.getUserID();
                    }
                }
                if (SkillServiceImpl.getInstance().monsterReleaseSkill(_dominator, target, _dominator.getDirection(), this.data.skill)) {
                    this.releaseWarningTime = 0L;
                    return 1;
                }
                return 0;
            }
        } else {
            if (SkillAI.RANDOM.nextFloat() > this.data.odds) {
                return -1;
            }
            if (SkillServiceImpl.getInstance().monsterReleaseSkill(_dominator, this.getTarget(_dominator), _dominator.getDirection(), this.data.skill)) {
                return 1;
            }
            return 0;
        }
    }

    private HeroPlayer getTarget(final Monster _dominator) {
        HeroPlayer target = null;
        switch (this.data.targetSettingCondition) {
            case 1: {
                target = _dominator.getHatredTargetByHatredSequence(this.data.sequenceOfSettingCondition);
                break;
            }
            case 4: {
                ArrayList<HeroPlayer> targetList = _dominator.getHatredTargetList();
                if (targetList == null) {
                    break;
                }
                ArrayList<HeroPlayer> templeteList = new ArrayList<HeroPlayer>();
                for (final HeroPlayer hatredTarget : targetList) {
                    if (hatredTarget.where() == _dominator.where() && !hatredTarget.isDead()) {
                        HeroPlayer player = null;
                        int j;
                        for (j = 0; j < templeteList.size(); ++j) {
                            player = templeteList.get(j);
                            boolean inDistance = (hatredTarget.getCellX() - _dominator.getCellX()) * (hatredTarget.getCellX() - _dominator.getCellX()) + (hatredTarget.getCellY() - _dominator.getCellY()) * (hatredTarget.getCellY() - _dominator.getCellY()) <= (player.getCellX() - _dominator.getCellX()) * (player.getCellX() - _dominator.getCellX()) + (player.getCellY() - _dominator.getCellY()) * (player.getCellY() - _dominator.getCellY());
                            if (inDistance) {
                                break;
                            }
                        }
                        templeteList.add(j, player);
                    }
                }
                if (this.data.sequenceOfSettingCondition <= templeteList.size()) {
                    target = templeteList.get(this.data.sequenceOfSettingCondition - 1);
                    break;
                }
                target = _dominator.getTopHatredTarget();
                break;
            }
            case 2: {
                ArrayList<HeroPlayer> targetList = _dominator.getHatredTargetList();
                if (targetList == null) {
                    break;
                }
                ArrayList<HeroPlayer> templeteList = new ArrayList<HeroPlayer>();
                for (final HeroPlayer hatredTarget : targetList) {
                    if (hatredTarget.where() == _dominator.where() && !hatredTarget.isDead()) {
                        HeroPlayer player = null;
                        int j;
                        for (j = 0; j < templeteList.size(); ++j) {
                            player = templeteList.get(j);
                            if (hatredTarget.getHp() >= player.getHp()) {
                                break;
                            }
                        }
                        templeteList.add(j, player);
                    }
                }
                if (this.data.sequenceOfSettingCondition <= templeteList.size()) {
                    target = templeteList.get(this.data.sequenceOfSettingCondition - 1);
                    break;
                }
                target = _dominator.getTopHatredTarget();
                break;
            }
            case 3: {
                ArrayList<HeroPlayer> targetList = _dominator.getHatredTargetList();
                if (targetList == null) {
                    break;
                }
                ArrayList<HeroPlayer> templeteList = new ArrayList<HeroPlayer>();
                for (final HeroPlayer hatredTarget : targetList) {
                    if (hatredTarget.where() == _dominator.where() && !hatredTarget.isDead()) {
                        HeroPlayer player = null;
                        int j;
                        for (j = 0; j < templeteList.size(); ++j) {
                            player = templeteList.get(j);
                            if (hatredTarget.getMp() >= player.getMp()) {
                                break;
                            }
                        }
                        templeteList.add(j, player);
                    }
                }
                if (this.data.sequenceOfSettingCondition <= templeteList.size()) {
                    target = templeteList.get(this.data.sequenceOfSettingCondition - 1);
                    break;
                }
                target = _dominator.getTopHatredTarget();
                break;
            }
        }
        return target;
    }
}
