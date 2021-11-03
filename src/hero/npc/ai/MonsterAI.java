// Decompiled with: Procyon 0.5.36
// Class Version: 6
package hero.npc.ai;

import hero.npc.ME2NotPlayer;
import java.util.Iterator;
import java.util.ArrayList;
import hero.expressions.service.CEService;
import hero.map.service.MapServiceImpl;
import hero.npc.service.AStar;
import hero.npc.service.NotPlayerServiceImpl;
import hero.npc.service.NpcConfig;
import hero.fight.service.FightServiceImpl;
import hero.npc.dict.MonsterImageConfDict;
import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.MonsterEmergeNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.ai.data.FightAIData;
import hero.npc.ai.data.AIDataDict;
import hero.player.HeroPlayer;
import hero.share.service.ME2ObjectList;
import hero.npc.ENotPlayerIntention;
import hero.fight.broadcast.SpecialViewStatusBroadcast;
import hero.share.ME2GameObject;
import java.util.Random;
import hero.npc.ai.data.Changes;
import hero.map.Decorater;
import hero.npc.Monster;
import org.apache.log4j.Logger;

public class MonsterAI implements Runnable {

    private static Logger log;
    private Monster dominator;
    private int chaseGridNum;
    private byte[][] movePath;
    private Decorater[] newMovePath;
    private int currentLocationOfPath;
    private int lastLocationOfPath;
    private byte movePathType;
    private long lastActiveMoveTime;
    public SpecialAI[] specialAIList;
    public SkillAI[] skillAIList;
    public SkillAI[] currentSkillAIList;
    public long traceDisappearTime;
    public int traceRunAwayGrid;
    public Changes currentChangesData;
    public long timeClockWhenChanges;
    public int traceHpWhenChanges;
    private static final Random RANDOM_BUILDER;
    private static final int FAST_DISTANCE = 9;
    private static final byte TRACK_TIMES = 4;
    private static final byte GRID_NUMBER_OF_PER_WHEN_RUNAWAY = 3;
    private static final byte MOVE_TYPE_OF_NONE = 0;
    private static final byte MOVE_TYPE_OF_AROUND = 1;
    private static final byte MOVE_TYPE_OF_LINE = 2;

    static {
        MonsterAI.log = Logger.getLogger(MonsterAI.class);
        RANDOM_BUILDER = new Random();
    }

    public MonsterAI(final Monster dominator) {
        this.dominator = dominator;
        this.movePathType = 0;
        this.initFightAI();
    }

    @Override
    public void run() {
        try {
            if (this.dominator.isEnable() && !this.dominator.isDead()) {
                if (this.dominator.isCalled() && System.currentTimeMillis() - this.dominator.getRefreshTime() >= this.dominator.getExistsTime()) {
                    this.dominator.clearFightInfo();
                    this.dominator.destroy();
                    this.dominator.where().getMonsterList().remove(this.dominator);
                    SpecialViewStatusBroadcast.send(this.dominator, (byte) 4);
                    return;
                }
                if (this.dominator.getIntention() == ENotPlayerIntention.AI_INTENTION_ACTIVE) {
                    this.onIntentionActive();
                } else if (this.dominator.getIntention() == ENotPlayerIntention.AI_INTENTION_ATTACK) {
                    this.onIntentionAttack();
                }
            } else {
                if (this.dominator.isEnable() && this.dominator.isDead()) {
                    MonsterAI.log.error("it enable is true but it dead state is true");
                    MonsterAI.log.error("monter's name=" + this.dominator.getName() + ";modeid=" + this.dominator.getModelID());
                }
                if (System.currentTimeMillis() - this.dominator.getDieTime() >= this.dominator.getRetime() * 1000) {
                    if (this.dominator.isInDungeon()) {
                        if (this.dominator.where().getMonsterModelIDAbout() == null) {
                            this.dominator.destroy();
                            return;
                        }
                        ME2ObjectList monsterList = this.dominator.where().getMonsterList();
                        boolean b = true;
                        for (int i = 0; i < monsterList.size(); ++i) {
                            if (((Monster) monsterList.get(i)).getModelID().equals(this.dominator.where().getMonsterModelIDAbout())) {
                                b = false;
                                break;
                            }
                        }
                        if (b) {
                            this.dominator.destroy();
                            return;
                        }
                    }
                    this.dominator.revive(null);
                    this.dominator.setIntention(ENotPlayerIntention.AI_INTENTION_ACTIVE);
                }
            }
        } catch (Exception ex) {
            MonsterAI.log.error("怪物AI error : ", ex);
        }
    }

    private void onIntentionActive() {
        if (this.dominator.isActiveAttackType()) {
            HeroPlayer scanAttackableTarget = this.scanAttackableTarget();
            if (scanAttackableTarget != null) {
                scanAttackableTarget.enterFight();
                this.dominator.addHattedTarget(scanAttackableTarget);
                this.dominator.changeInvention(ENotPlayerIntention.AI_INTENTION_ATTACK, scanAttackableTarget);
                return;
            }
        }
        if (this.dominator.where().getPlayerList().size() > 0 && MonsterAI.RANDOM_BUILDER.nextBoolean()) {
            this.walk();
        }
    }

    private void initFightAI() {
        FightAIData fightAIData = AIDataDict.getInstance().getFightAIData(this.dominator.getFightAIID());
        if (fightAIData != null) {
            if (fightAIData.specialAIList != null) {
                this.specialAIList = new SpecialAI[fightAIData.specialAIList.length];
                for (int i = 0; i < fightAIData.specialAIList.length; ++i) {
                    this.specialAIList[i] = new SpecialAI(this.dominator, fightAIData.specialAIList[i]);
                }
            }
            if (fightAIData.skillAIList != null) {
                this.skillAIList = AIDataDict.getInstance().buildSkillAIList(fightAIData.skillAIList, this.dominator.getHPPercent());
                this.currentSkillAIList = this.skillAIList;
            }
        }
    }

    public void clearDynamicData() {
        this.chaseGridNum = 0;
        this.currentLocationOfPath = 0;
        this.lastLocationOfPath = 0;
    }

    private void ai() {
        try {
            if (this.dominator.isVisible()) {
                if (!this.dominator.isRunningAway()) {
                    if (!this.dominator.isChangesStatus()) {
                        this.executeSpecialAI();
                    } else if (this.currentChangesData.endChanges(this.dominator, this.timeClockWhenChanges, this.traceHpWhenChanges)) {
                        this.executeSpecialAI();
                    }
                } else if (this.traceRunAwayGrid > 0) {
                    this.walkWhenRunAway();
                    if (this.traceRunAwayGrid != 0) {
                        return;
                    }
                    this.executeSpecialAI();
                }
            } else {
                this.traceDisappearTime -= 1000L;
                if (this.traceDisappearTime > 0L) {
                    return;
                }
                this.gotoValideEmergeVocation();
                MapSynchronousInfoBroadcast.getInstance().put(this.dominator.where(), new MonsterEmergeNotify(this.dominator.getID(), this.dominator.getCellX(), this.dominator.getCellY()), false, 0);
                this.dominator.emerge();
                this.executeSpecialAI();
            }
            if (this.dominator.isVisible() && !this.dominator.isRunningAway()) {
                this.executeSkillAI();
            }
        } catch (Exception ex) {
            if (this.dominator != null) {
                MonsterAI.log.error("怪物=" + this.dominator.getName() + ";id=" + this.dominator.getModelID());
            } else {
                MonsterAI.log.error("怪物为NULL ");
            }
            MonsterAI.log.error("怪物AI出现严重异常:", ex);
        }
    }

    public void setMovePath(final byte[][] movePath) {
        if (movePath != null) {
            this.movePath = movePath;
            if (this.movePath[this.movePath.length - 1][0] == this.movePath[0][0] && this.movePath[this.movePath.length - 1][1] == this.movePath[0][1]) {
                this.movePathType = 1;
            } else {
                this.movePathType = 2;
            }
        }
    }

    public void setMovePath(final Decorater[] newMovePath) {
        if (newMovePath != null) {
            this.newMovePath = newMovePath;
            if (this.newMovePath[this.newMovePath.length - 1].x == this.newMovePath[0].x && this.newMovePath[this.newMovePath.length - 1].y == this.newMovePath[0].y) {
                this.movePathType = 1;
            } else {
                this.movePathType = 2;
            }
        }
    }

    private void executeSpecialAI() {
        if (this.specialAIList != null) {
            SpecialAI[] specialAIList;
            for (int length = (specialAIList = this.specialAIList).length, i = 0; i < length; ++i) {
                SpecialAI specialAI = specialAIList[i];
                if (!this.dominator.isVisible() || this.dominator.isRunningAway()) {
                    break;
                }
                if (this.dominator.isChangesStatus()) {
                    break;
                }
                specialAI.execute();
            }
        }
    }

    private void executeSkillAI() {
        if (this.currentSkillAIList != null) {
            SkillAI[] currentSkillAIList;
            for (int length = (currentSkillAIList = this.currentSkillAIList).length, i = 0; i < length; ++i) {
                SkillAI skillAI = currentSkillAIList[i];
                if (!this.dominator.isVisible()) {
                    break;
                }
                if (this.dominator.isRunningAway()) {
                    break;
                }
                skillAI.execute(this.dominator);
            }
        }
    }

    private void onIntentionAttack() {
        if (!this.dominator.inValidateFightTime() || this.dominator.getHatredTargetNums() == 0) {
            this.disengageFight();
            return;
        }
        this.normalAttack();
    }

    public void normalAttack() {
        HeroPlayer attackTarget = this.dominator.getAttackTarget();
        if (attackTarget == null) {
            this.disengageFight();
            return;
        }
        if (0L == this.dominator.getLastAttackTime()) {
            this.dominator.setFirstAttackTime();
        }
        if (this.dominator.moveable() && !this.dominator.isInsensible() && !this.dominator.isSleeping()) {
            MonsterImageConfDict.Config value = MonsterImageConfDict.get(this.dominator.getImageID());
            if ((this.dominator.getCellX() - attackTarget.getCellX()) * (this.dominator.getCellX() - attackTarget.getCellX()) + (this.dominator.getCellY() - attackTarget.getCellY()) * (this.dominator.getCellY() - attackTarget.getCellY()) <= (this.dominator.getAttackRange() + value.grid / 2) * (this.dominator.getAttackRange() + value.grid / 2)) {
                if (System.currentTimeMillis() - this.dominator.getLastAttackTime() >= this.dominator.getActualAttackImmobilityTime()) {
                    this.ai();
                    FightServiceImpl.getInstance().processPhysicsAttack(this.dominator, attackTarget);
                    this.dominator.refreshLastAttackTime();
                }
            } else {
                byte[] path = AStar.getPath(this.dominator.getCellX(), this.dominator.getCellY(), attackTarget.getCellX(), attackTarget.getCellY(), NotPlayerServiceImpl.getInstance().getConfig().MONSTER_MOVE_MOST_FAST_GRID, this.dominator.getAttackRange(), this.dominator.where());
                if (path != null) {
                    this.ai();
                    this.dominator.goAlone(path, attackTarget);
                    this.chase(path.length);
                    if ((this.dominator.getCellX() - attackTarget.getCellX()) * (this.dominator.getCellY() - attackTarget.getCellY()) <= this.dominator.getAttackRange() * this.dominator.getAttackRange() && System.currentTimeMillis() - this.dominator.getLastAttackTime() >= this.dominator.getActualAttackImmobilityTime()) {
                        FightServiceImpl.getInstance().processPhysicsAttack(this.dominator, attackTarget);
                        this.dominator.refreshLastAttackTime();
                    }
                    if ((this.dominator.getOrgX() - this.dominator.getCellX()) * (this.dominator.getOrgX() - this.dominator.getCellX()) + (this.dominator.getOrgY() - this.dominator.getCellY()) * (this.dominator.getOrgY() - this.dominator.getCellY()) > NotPlayerServiceImpl.getInstance().getConfig().ai_follow_distance * NotPlayerServiceImpl.getInstance().getConfig().ai_follow_distance && this.chaseGridNum > NotPlayerServiceImpl.getInstance().getConfig().ai_follow_grid) {
                        this.dominator.hatredTargetAway(attackTarget);
                    }
                } else {
                    this.disengageFight();
                }
            }
        }
    }

    private void disengageFight() {
        this.clearDynamicData();
        this.dominator.disengageFight();
    }

    public void chase(final int n) {
        this.chaseGridNum += n;
    }

    private HeroPlayer scanAttackableTarget() {
        ArrayList<HeroPlayer> monsterValidateTargetListInCircle = MapServiceImpl.getInstance().getMonsterValidateTargetListInCircle(this.dominator.where(), this.dominator, 9);
        if (monsterValidateTargetListInCircle == null || monsterValidateTargetListInCircle.size() == 0) {
            return null;
        }
        for (final HeroPlayer heroPlayer : monsterValidateTargetListInCircle) {
            if (CEService.inAttackRange(this.dominator.getObjectLevel().value(), this.dominator.getLevel(), this.dominator.getCellX(), this.dominator.getCellY(), heroPlayer.getLevel(), heroPlayer.getCellX(), heroPlayer.getCellY())) {
                return heroPlayer;
            }
        }
        return null;
    }

    private void gotoValideEmergeVocation() {
        byte b = this.getRandomDirection();
        for (int i = 0; i < 5; i = (byte) (i + 1)) {
            b = this.trackNext(true, 1, b);
            if (b <= 0) {
                break;
            }
            this.dominator.go(b);
        }
    }

    public void walkWhenRunAway() {
        int n = (this.traceRunAwayGrid >= 3) ? 3 : this.traceRunAwayGrid;
        byte[] array = new byte[n];
        byte b = this.getRandomDirection();
        for (int i = 0; i < n; i = (byte) (i + 1)) {
            b = this.trackNext(true, 1, b);
            if (b <= 0) {
                break;
            }
            this.dominator.go(b);
            array[i] = b;
        }
        NotPlayerServiceImpl.getInstance().broadcastNotPlayerWalkPath(this.dominator, array, null);
        this.traceRunAwayGrid -= n;
    }

    private void walk() {
        if (this.movePathType != 0 && this.dominator.moveable() && System.currentTimeMillis() - this.lastActiveMoveTime >= NotPlayerServiceImpl.getInstance().getConfig().MONSTER_ACTIVE_MOVE_INTERVAL) {
            int n;
            if (1 == this.movePathType) {
                n = ((this.movePath.length - 1 > NotPlayerServiceImpl.getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME) ? NotPlayerServiceImpl.getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME : (this.movePath.length - 1));
            } else if (this.currentLocationOfPath == this.movePath.length - 1) {
                n = ((this.movePath.length - 1 > NotPlayerServiceImpl.getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME) ? NotPlayerServiceImpl.getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME : (this.movePath.length - 1));
            } else if (this.currentLocationOfPath == 0 || this.currentLocationOfPath > this.lastLocationOfPath) {
                n = ((this.movePath.length - 1 - this.currentLocationOfPath > NotPlayerServiceImpl.getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME) ? NotPlayerServiceImpl.getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME : (this.movePath.length - 1 - this.currentLocationOfPath));
            } else {
                n = ((this.currentLocationOfPath > NotPlayerServiceImpl.getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME) ? NotPlayerServiceImpl.getInstance().getConfig().MONSTER_MOVE_GRID_NUM_PER_TIME : this.currentLocationOfPath);
            }
            byte[] array = new byte[n];
            for (int i = 0; i < n; i = (byte) (i + 1)) {
                byte trackFixedPath = this.trackFixedPath();
                if (trackFixedPath <= 0) {
                    break;
                }
                this.dominator.go(trackFixedPath);
                array[i] = trackFixedPath;
            }
            this.lastActiveMoveTime = System.currentTimeMillis();
            NotPlayerServiceImpl.getInstance().broadcastNotPlayerWalkPath(this.dominator, array, null);
        }
    }

    private byte trackFixedPath() {
        if (1 == this.movePathType) {
            if (this.currentLocationOfPath < this.movePath.length - 2) {
                ++this.currentLocationOfPath;
            } else {
                this.currentLocationOfPath = 0;
            }
        } else if (2 == this.movePathType) {
            if (this.currentLocationOfPath == 0) {
                this.lastLocationOfPath = this.currentLocationOfPath;
                ++this.currentLocationOfPath;
            } else if (this.currentLocationOfPath == this.movePath.length - 1) {
                this.lastLocationOfPath = this.currentLocationOfPath;
                --this.currentLocationOfPath;
            } else if (this.currentLocationOfPath > this.lastLocationOfPath) {
                this.lastLocationOfPath = this.currentLocationOfPath;
                ++this.currentLocationOfPath;
            } else {
                this.lastLocationOfPath = this.currentLocationOfPath;
                --this.currentLocationOfPath;
            }
        }
        byte b = this.movePath[this.currentLocationOfPath][0];
        byte b2 = this.movePath[this.currentLocationOfPath][1];
        if (b == this.dominator.getCellX()) {
            if (b2 < this.dominator.getCellY()) {
                return 1;
            }
            return 2;
        } else {
            if (b2 != this.dominator.getCellY()) {
                return -1;
            }
            if (b < this.dominator.getCellX()) {
                return 3;
            }
            return 4;
        }
    }

    private byte trackNext(final boolean b, int n, final byte b2) {
        if (b) {
            if (this.dominator.passable(b2)) {
                return b2;
            }
            return this.trackNext(false, ++n, b2);
        } else {
            if (n > 4) {
                return 0;
            }
            switch (b2) {
                case 1: {
                    if (this.dominator.passable((byte) 4)) {
                        return 4;
                    }
                    return this.trackNext(false, ++n, (byte) 4);
                }
                case 2: {
                    if (this.dominator.passable((byte) 3)) {
                        return 3;
                    }
                    return this.trackNext(false, ++n, (byte) 3);
                }
                case 3: {
                    if (this.dominator.passable((byte) 1)) {
                        return 1;
                    }
                    return this.trackNext(false, ++n, (byte) 1);
                }
                case 4: {
                    if (this.dominator.passable((byte) 2)) {
                        return 2;
                    }
                    return this.trackNext(false, ++n, (byte) 2);
                }
                default: {
                    return 0;
                }
            }
        }
    }

    private byte getRandomDirection() {
        return (byte) (MonsterAI.RANDOM_BUILDER.nextInt(4) + 1);
    }
}
