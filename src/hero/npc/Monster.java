// Decompiled with: FernFlower
// Class Version: 6
package hero.npc;

import hero.dungeon.service.DungeonServiceImpl;
import hero.effect.Effect;
import hero.fight.broadcast.SpecialViewStatusBroadcast;
import hero.fight.service.FightConfig;
import hero.gather.service.GatherServerImpl;
import hero.item.legacy.WorldLegacyDict;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.ai.MonsterAI;
import hero.npc.detail.AttackChain;
import hero.npc.detail.EMonsterLevel;
import hero.npc.dict.MonsterDataDict;
import hero.npc.message.MonsterDisengageFightNotify;
import hero.npc.message.MonsterRefreshNotify;
import hero.npc.service.NotPlayerServiceImpl;
import hero.npc.service.NpcConfig;
import hero.player.HeroPlayer;
import hero.player.define.EClan;
import hero.share.EObjectLevel;
import hero.share.EObjectType;
import hero.share.EVocation;
import hero.share.ME2GameObject;
import hero.share.service.ThreadPoolFactory;
import hero.task.service.TaskServiceImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Future;
import javolution.util.FastList;
import org.apache.log4j.Logger;

public class Monster extends ME2NotPlayer {

    private static Logger log = Logger.getLogger(Monster.class);
    private static Random random = new Random();
    private boolean isActiveAttackType;
    private int retime;
    private long dieTime;
    private long enterFightTime;
    private boolean isChangesStatus;
    private boolean runingAway;
    private int basicExp;
    private boolean isInDungeon;
    private ENotPlayerIntention intention;
    private int attackAiID;
    private EMonsterLevel monsterLevel;
    private HeroPlayer provocateur;
    private MonsterAI ai;
    private AttackChain attackChain;
    private HeroPlayer attackerAtFirst;
    private FastList hatredTargetList;
    private static final Monster.HatredComparator comparator = new Monster.HatredComparator();
    private Vector soulIDList;
    private int takeSoulUserID;
    private long lastAttackTime;
    private long lastFightTime;
    private Future aiTast;
    private byte legacyTypeMostNums;
    private byte legacyTypeSmallestNums;
    private ArrayList legacyList;
    private static final Random RANDOM = new Random();
    // $FF: synthetic field
    private static int[] $SWITCH_TABLE$hero$npc$ENotPlayerIntention;

    public Monster(MonsterDataDict.MonsterData _data) {
        this.objectType = EObjectType.MONSTER;
        this.setModelID(_data.modelID);
        this.setName(_data.name);
        this.setImageID(Short.parseShort(_data.imageID));
        this.setLevel(Short.parseShort(_data.level));
        this.setClan(EClan.getClanByDesc(_data.clan));
        if (_data.vocation != null) {
            this.setVocation(EVocation.getVocationByDesc(_data.vocation));
        } else {
            log.info("怪物职业为NULL,请检查怪物配置文件.");
            this.setVocation(EVocation.LI_SHI);
        }

        this.setObjectLevel(EObjectLevel.getNPCLevel(_data.type));
        this.setMonsterLevel(EMonsterLevel.getMonsterLevel(_data.normalOrBoss));
        this.setActualAttackImmobilityTime(1);
        this.setMoveSpeed((byte) 3);
        this.hatredTargetList = new FastList();
        this.setIntention(ENotPlayerIntention.AI_INTENTION_ACTIVE);
        this.effectList = new ArrayList();
    }

    public void active() {
        super.active();
        this.setDirection((byte) (random.nextInt(4) + 1));
        this.startAITask();
    }

    public void active(byte[][] _movePath) {
        log.debug("monster active ..");
        this.active();
        this.ai.setMovePath(_movePath);
    }

    public void startAITask() {
        log.debug("monster startAITask ..");
        if (this.aiTast == null) {
            this.ai = new MonsterAI(this);
            this.aiTast = ThreadPoolFactory.getInstance().excuteAI(this.ai, (long) (RANDOM.nextInt(1500) + 1500), (long) ((NpcConfig) NotPlayerServiceImpl.getInstance().getConfig()).MONSTER_AI_INTERVAL);
        }

    }

    public void setIntention(ENotPlayerIntention _intention) {
        this.intention = _intention;
    }

    public ENotPlayerIntention getIntention() {
        return this.intention;
    }

    public void changeInvention(ENotPlayerIntention _intention, HeroPlayer _player) {
        switch ($SWITCH_TABLE$hero$npc$ENotPlayerIntention()[_intention.ordinal()]) {
            case 2:
                this.setIntention(ENotPlayerIntention.AI_INTENTION_ACTIVE);
                this.setMoveSpeed((byte) 3);
                break;
            case 3:
                this.enterFight();
                this.setIntention(ENotPlayerIntention.AI_INTENTION_ATTACK);
                if (EMonsterLevel.NORMAL == this.monsterLevel) {
                    this.changeMoveSpeed(1);
                } else {
                    this.changeMoveSpeed(2);
                }

                this.ai.normalAttack();
        }

    }

    public void refreshLastAttackTime() {
        this.lastAttackTime = System.currentTimeMillis();
    }

    public void setFirstAttackTime() {
        this.lastAttackTime = System.currentTimeMillis() - 2000L;
    }

    public long getLastAttackTime() {
        return this.lastAttackTime;
    }

    public void stopAITast() {
        if (this.aiTast != null) {
            this.aiTast.cancel(false);
            this.aiTast = null;
            ThreadPoolFactory.getInstance().removeAI(this.ai);
            this.ai = null;
        }

    }

    public void destroy() {
        if (!this.isDead) {
            this.invalid();
            this.clearEffect();
        }

        this.stopAITast();
        NotPlayerServiceImpl.getInstance().removeMonster(this.getID());
    }

    public void setRetime(int _retime) {
        this.retime = _retime;
    }

    public int getRetime() {
        return this.retime;
    }

    public void enterFight() {
        if (!this.inFighting) {
            super.enterFight();
            this.enterFightTime = System.currentTimeMillis();
            this.lastFightTime = System.currentTimeMillis();
        }

    }

    public HeroPlayer getAttackTarget() {
        if (this.provocateur != null && this.provocateur.where() == this.where() && this.provocateur.isEnable() && !this.provocateur.isDead()) {
            return this.provocateur;
        } else {
            synchronized (this.hatredTargetList) {
                Collections.sort(this.hatredTargetList, comparator);
                Iterator var3 = this.hatredTargetList.iterator();

                Monster.HatredTarget target;
                do {
                    if (!var3.hasNext()) {
                        return null;
                    }

                    target = (Monster.HatredTarget) var3.next();
                } while (target.player == null || target.player.where() != this.where() || !target.player.isEnable() || target.player.isDead());

                return target.player;
            }
        }
    }

    public long getTimeOfEnterFight() {
        return this.enterFightTime;
    }

    public void setDieTime(long _dieTime) {
        this.dieTime = _dieTime;
    }

    public long getDieTime() {
        return this.dieTime;
    }

    public void setChangesStatus(boolean _isOrNo) {
        this.isChangesStatus = _isOrNo;
    }

    public boolean isChangesStatus() {
        return this.isChangesStatus;
    }

    public void beginRunAway(int _distance) {
        this.runingAway = true;
    }

    public void stopRunAway() {
        this.runingAway = false;
    }

    public boolean isRunningAway() {
        return this.runingAway;
    }

    public MonsterAI getAI() {
        return this.ai;
    }

    public void revive(ME2GameObject _savior) {
        this.setHp(this.getActualProperty().getHpMax());
        this.setMp(this.getActualProperty().getMpMax());
        this.setCellX(this.getOrgX());
        this.setCellY(this.getOrgY());
        super.revive(_savior);
        super.active();
        this.setDirection((byte) (random.nextInt(4) + 1));
        this.setMoveSpeed((byte) 3);
        this.where().getMonsterList().add((ME2GameObject) this);
        MapSynchronousInfoBroadcast.getInstance().put((short) 3, this.where(), new MonsterRefreshNotify((short) 3, this), false, 0);
    }

    public void die(ME2GameObject _killer) {
        super.die(_killer);
        this.invalid();
        this.dieTime = System.currentTimeMillis();
        this.ai.clearDynamicData();
        this.where().getMonsterList().remove((ME2GameObject) this);
        if (!GatherServerImpl.getInstance().processSoulWhenMonsterDied(this)) {
            SpecialViewStatusBroadcast.send(this, (byte) 4);
            if (this.isInDungeon) {
                DungeonServiceImpl.getInstance().processMonsterDie(this, this.where().getDungeon());
            }

            if (this.attackerAtFirst != null) {
                ArrayList legacyTaskGoodsInfoList = TaskServiceImpl.getInstance().processTaskAboutMonster(this, this.attackerAtFirst);

                try {
                    NotPlayerServiceImpl.getInstance().processMonsterLegacy(this, this.getActualLegacyList(), legacyTaskGoodsInfoList);
                } catch (Exception var4) {
                    var4.printStackTrace();
                }
            }
        }

        this.clearFightInfo();
        if (this.isCalled) {
            this.destroy();
        }

    }

    public void setLegacyListInfo(byte _legacyTypeMostNums, byte _legacyTypeSmallestNums) {
        this.legacyTypeMostNums = _legacyTypeMostNums;
        this.legacyTypeSmallestNums = _legacyTypeSmallestNums;
    }

    public void addLegacy(int _itemID, float _odds, int _num) {
        if (this.legacyList == null) {
            this.legacyList = new ArrayList();
        }

        log.debug("monster add legacy itemID = " + _itemID + ",odds=" + _odds + ",num=" + _num);
        this.legacyList.add(new float[]{(float) _itemID, _odds, (float) _num});
    }

    public ArrayList getActualLegacyList() {
        try {
            long monsterTime = System.currentTimeMillis();
            log.info("[" + this.getName() + "]开始计算掉落时间," + monsterTime + ";objectid=" + this.getID());
            ArrayList itemList = new ArrayList();
            ArrayList worldLegacyGoodsID = WorldLegacyDict.getInstance().getLegacyGoodID(this.getLevel());
            log.info("怪物死亡，世界掉落 数量： =" + worldLegacyGoodsID.size());
            if (worldLegacyGoodsID.size() > 0) {
                Iterator var6 = worldLegacyGoodsID.iterator();

                while (var6.hasNext()) {
                    Integer goodsID = (Integer) var6.next();
                    itemList.add(new int[]{goodsID, 1});
                }
            }

            monsterTime = System.currentTimeMillis();
            log.info("[" + this.getName() + "]完成第1个循环," + monsterTime + ";objectid=" + this.getID());
            if (this.legacyList == null) {
                if (itemList.size() != 0) {
                    log.info("[" + this.getName() + "]完成所有循环return," + monsterTime + ";objectid=" + this.getID());
                    return itemList;
                }

                log.info("[" + this.getName() + "]完成所有循环return," + monsterTime + ";objectid=" + this.getID());
                return null;
            }

            int legacyTypeNums;
            for (legacyTypeNums = random.nextInt(this.legacyTypeMostNums + 1); legacyTypeNums < this.legacyTypeSmallestNums; legacyTypeNums = random.nextInt(this.legacyTypeMostNums + 1)) {
            }

            log.info("[" + this.getName() + "]完成第2个循环," + monsterTime + ";objectid=" + this.getID());
            if (legacyTypeNums == 0) {
                log.info("[" + this.getName() + "]完成所有循环return," + monsterTime + ";objectid=" + this.getID());
                return null;
            }

            while (legacyTypeNums > 0) {
                int randomIndex = random.nextInt(this.legacyList.size());
                boolean exist = false;
                Iterator var9 = itemList.iterator();

                while (var9.hasNext()) {
                    int[] dropItem = (int[]) var9.next();
                    if ((float) dropItem[0] == ((float[]) this.legacyList.get(randomIndex))[0]) {
                        exist = true;
                        break;
                    }
                }

                if (!exist) {
                    float temp = random.nextFloat();
                    if (temp <= ((float[]) this.legacyList.get(randomIndex))[1]) {
                        int dropNums = random.nextInt((int) ((float[]) this.legacyList.get(randomIndex))[2]) + 1;
                        itemList.add(new int[]{(int) ((float[]) this.legacyList.get(randomIndex))[0], dropNums});
                    }

                    --legacyTypeNums;
                }
            }

            log.info("[" + this.getName() + "]完成第3个嵌套循环准备退出方法," + monsterTime + ";objectid=" + this.getID());
            if (itemList.size() != 0) {
                return itemList;
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        return null;
    }

    public int getBaseExp() {
        return this.basicExp;
    }

    public void setBaseExp(int _experience) {
        this.basicExp = _experience;
    }

    public int getSoulID() {
        return this.soulIDList != null ? (Integer) this.soulIDList.get(RANDOM.nextInt(this.soulIDList.size())) : 0;
    }

    public void setSoulIDList(Vector _soulIDList) {
        this.soulIDList = _soulIDList;
    }

    public int getTakeSoulUserID() {
        return this.takeSoulUserID;
    }

    public void setTakeSoulUserID(int _takeSoulUserID) {
        this.takeSoulUserID = _takeSoulUserID;
    }

    public void setAttackChain(int _range, int _factor) {
        if (_range > 0 && _factor > 0) {
            AttackChain chain = new AttackChain();
            chain.range = _range;
            chain.factor = _factor;
            this.attackChain = chain;
        }
    }

    public AttackChain getAttackChain() {
        return this.attackChain;
    }

    public void clearFightInfo() {
        super.disengageFight();
        this.enterFightTime = 0L;
        this.lastAttackTime = 0L;
        this.lastFightTime = 0L;
        this.takeSoulUserID = 0;
        this.attackerAtFirst = null;
        this.clearEffect();
        synchronized (this.hatredTargetList) {
            Iterator var3 = this.hatredTargetList.iterator();

            while (true) {
                if (!var3.hasNext()) {
                    this.hatredTargetList.clear();
                    break;
                }

                Monster.HatredTarget target = (Monster.HatredTarget) var3.next();
                if (target != null) {
                    target.player.removeHatredSource(this);
                }
            }
        }

        this.changeInvention(ENotPlayerIntention.AI_INTENTION_ACTIVE, (HeroPlayer) null);
    }

    public synchronized void disengageFight() {
        if (this.enabled) {
            this.clearFightInfo();
            this.setCellX(this.getOrgX());
            this.setCellY(this.getOrgY());
            this.setHp(this.getActualProperty().getHpMax());
            this.setMp(this.getActualProperty().getMpMax());
            MapSynchronousInfoBroadcast.getInstance().put(this.where(), new MonsterDisengageFightNotify(this.getID(), this.getCellX(), this.getCellY()), false, 0);
        }

    }

    public void setAttackAiID(int _attackAiID) {
        this.attackAiID = _attackAiID;
    }

    public int getFightAIID() {
        return this.attackAiID;
    }

    public void setActiveAttackType(boolean _attackType) {
        this.isActiveAttackType = _attackType;
    }

    public boolean isActiveAttackType() {
        return this.isActiveAttackType;
    }

    public void beProvoke(HeroPlayer _player) {
        if (_player != null && this.provocateur != _player) {
            this.provocateur = _player;
            Monster.HatredTarget firstHatredTarget = (Monster.HatredTarget) this.hatredTargetList.get(0);
            synchronized (this.hatredTargetList) {
                if (firstHatredTarget != null && firstHatredTarget.player != _player) {
                    for (int i = 0; i < this.hatredTargetList.size(); ++i) {
                        Monster.HatredTarget target = (Monster.HatredTarget) this.hatredTargetList.get(i);
                        if (target.player == _player) {
                            target.hatredValue = firstHatredTarget.hatredValue;
                            return;
                        }
                    }
                }
            }
        }

    }

    public void endProvoke() {
        this.provocateur = null;
    }

    public HeroPlayer getProvocateur() {
        return this.provocateur;
    }

    public boolean canBeAttackBy(ME2GameObject _object) {
        return _object.getClan() != this.getClan();
    }

    public EMonsterLevel getMonsterLevel() {
        return this.monsterLevel;
    }

    public void setMonsterLevel(EMonsterLevel _monsterLevel) {
        this.monsterLevel = _monsterLevel;
    }

    public boolean isInDungeon() {
        return this.isInDungeon;
    }

    public void setInDungeon() {
        this.isInDungeon = true;
    }

    public void addTargetHatredValue(HeroPlayer _player, int _value) {
        synchronized (this.hatredTargetList) {
            for (int i = 0; i < this.hatredTargetList.size(); ++i) {
                Monster.HatredTarget target = (Monster.HatredTarget) this.hatredTargetList.get(i);
                if (target.player == _player) {
                    target.hatredValue = (int) ((float) target.hatredValue + _player.getHatredModulus() * (float) _value);
                    if (target.hatredValue < 0) {
                        target.hatredValue = 0;
                    }

                    return;
                }
            }

            this.hatredTargetList.add(new Monster.HatredTarget(_player, (int) (_player.getHatredModulus() * (float) _value)));
        }

        _player.enterFight();
        _player.addHatredSource(this);
        if (!this.inFighting) {
            this.changeInvention(ENotPlayerIntention.AI_INTENTION_ATTACK, _player);
        }

    }

    public void addHattedTarget(HeroPlayer _player) {
        synchronized (this.hatredTargetList) {
            this.hatredTargetList.add(new Monster.HatredTarget(_player, 0));
        }

        _player.addHatredSource(this);
    }

    public void changeTargetHattedPercent(int _hatredSequence, int _value) {
        Monster.HatredTarget target = (Monster.HatredTarget) this.hatredTargetList.get(_hatredSequence);
        if (target != null) {
            target.hatredValue = target.hatredValue + _value;
            if (target.hatredValue < 0) {
                target.hatredValue = 0;
            }
        }

    }

    public void beHarmed(HeroPlayer _player, int _value) {
        if (_value >= 0 && this.enabled) {
            synchronized (this.hatredTargetList) {
                int i = 0;

                while (true) {
                    if (i >= this.hatredTargetList.size()) {
                        this.hatredTargetList.add(new Monster.HatredTarget(_player, (int) (_player.getHatredModulus() * (float) _value)));
                        _player.addHatredSource(this);
                        break;
                    }

                    Monster.HatredTarget target = (Monster.HatredTarget) this.hatredTargetList.get(i);
                    if (target.player == _player) {
                        target.hatredValue = (int) ((float) target.hatredValue + _player.getHatredModulus() * (float) _value);
                        return;
                    }

                    ++i;
                }
            }

            _player.enterFight();
            if (!this.inFighting) {
                this.changeInvention(ENotPlayerIntention.AI_INTENTION_ATTACK, _player);
            }

        }
    }

    public void targetBeResumed(HeroPlayer _releaser, int _value) {
        if (_releaser != null && _value > 0) {
            synchronized (this.hatredTargetList) {
                int i = 0;

                while (true) {
                    if (i >= this.hatredTargetList.size()) {
                        this.hatredTargetList.add(new Monster.HatredTarget(_releaser, (int) (_releaser.getHatredModulus() * (float) _value / 2.0F)));
                        _releaser.addHatredSource(this);
                        break;
                    }

                    Monster.HatredTarget target = (Monster.HatredTarget) this.hatredTargetList.get(i);
                    if (target.player == _releaser) {
                        target.hatredValue = (int) ((float) target.hatredValue + _releaser.getHatredModulus() * (float) _value / 2.0F);
                        return;
                    }

                    ++i;
                }
            }

            _releaser.enterFight();
        }
    }

    public void changeHattedPercent(HeroPlayer _player, float _percent) {
        synchronized (this.hatredTargetList) {
            Iterator var5 = this.hatredTargetList.iterator();

            while (var5.hasNext()) {
                Monster.HatredTarget target = (Monster.HatredTarget) var5.next();
                if (target.player == _player) {
                    target.hatredValue = (int) ((float) target.hatredValue + (float) target.hatredValue * _percent);
                    if (target.hatredValue < 0) {
                        target.hatredValue = 0;
                    }

                    return;
                }
            }

        }
    }

    public void changeHattedPercent(int _hatredSequence, float _percent) {
        Monster.HatredTarget target = (Monster.HatredTarget) this.hatredTargetList.get(_hatredSequence);
        if (target != null) {
            target.hatredValue = (int) ((float) target.hatredValue + (float) target.hatredValue * _percent);
            if (target.hatredValue < 0) {
                target.hatredValue = 0;
            }
        }

    }

    public int getHatredTargetNums() {
        return this.hatredTargetList.size();
    }

    public HeroPlayer getAttackerAtFirst() {
        return this.attackerAtFirst;
    }

    public void setAttackerAtFirst(HeroPlayer _player) {
        if (this.attackerAtFirst == null) {
            this.attackerAtFirst = _player;
        }

    }

    public boolean inValidateFightTime() {
        return 0L < this.lastFightTime && System.currentTimeMillis() - this.lastFightTime < (long) FightConfig.DISENGAGE_FIGHT_TIME;
    }

    public void happenFight() {
        this.lastFightTime = System.currentTimeMillis();
    }

    public HeroPlayer getHatredTargetByHatredSequence(int _hatredSequence) {
        if (this.hatredTargetList.size() >= _hatredSequence) {
            HeroPlayer player = ((Monster.HatredTarget) this.hatredTargetList.get(_hatredSequence - 1)).player;
            if (!player.isDead()) {
                return player;
            }
        }

        return null;
    }

    private void clearEffect() {
        synchronized (this.effectList) {
            if (this.effectList.size() > 0) {
                for (int i = 0; i < this.effectList.size(); ++i) {
                    try {
                        Effect effect = (Effect) this.effectList.get(i);
                        effect.destory(this);
                    } catch (Exception var5) {
                        var5.printStackTrace();
                        break;
                    }
                }

                this.effectList.clear();
            }

        }
    }

    public ArrayList getHatredTargetList() {
        if (this.hatredTargetList.size() > 0) {
            synchronized (this.hatredTargetList) {
                ArrayList targetList = new ArrayList();
                Iterator var4 = this.hatredTargetList.iterator();

                while (var4.hasNext()) {
                    Monster.HatredTarget target = (Monster.HatredTarget) var4.next();
                    targetList.add(target.player);
                }

                return targetList;
            }
        } else {
            return null;
        }
    }

    public void copyHatredTargetList(ArrayList _hatredTargetList) {
        synchronized (this.hatredTargetList) {
            this.hatredTargetList.clear();
            Iterator var4 = _hatredTargetList.iterator();

            while (var4.hasNext()) {
                HeroPlayer player = (HeroPlayer) var4.next();
                this.hatredTargetList.add(new Monster.HatredTarget(player, 0));
            }

        }
    }

    public HeroPlayer getTopHatredTarget() {
        return ((Monster.HatredTarget) this.hatredTargetList.get(0)).player;
    }

    public void hatredTargetAway(HeroPlayer _player) {
        synchronized (this.hatredTargetList) {
            for (int i = 0; i < this.hatredTargetList.size(); ++i) {
                Monster.HatredTarget target = (Monster.HatredTarget) this.hatredTargetList.get(i);
                if (target.player == _player) {
                    this.hatredTargetList.remove(i);
                    _player.removeHatredSource(this);
                    if (this.attackerAtFirst == _player) {
                        this.attackerAtFirst = null;
                    }

                    return;
                }
            }

        }
    }

    public void clearHatred(HeroPlayer _player) {
        synchronized (this.hatredTargetList) {
            for (int i = 0; i < this.hatredTargetList.size(); ++i) {
                if (((Monster.HatredTarget) this.hatredTargetList.get(i)).player == _player) {
                    this.hatredTargetList.remove(i);
                    return;
                }
            }

        }
    }

    public byte getDefaultSpeed() {
        return 3;
    }

    // $FF: synthetic method
    static int[] $SWITCH_TABLE$hero$npc$ENotPlayerIntention() {
        int[] var10000 = $SWITCH_TABLE$hero$npc$ENotPlayerIntention;
        if (var10000 != null) {
            return var10000;
        } else {
            int[] var0 = new int[ENotPlayerIntention.values().length];

            try {
                var0[ENotPlayerIntention.AI_INTENTION_ACTIVE.ordinal()] = 2;
            } catch (NoSuchFieldError var4) {
            }

            try {
                var0[ENotPlayerIntention.AI_INTENTION_ATTACK.ordinal()] = 3;
            } catch (NoSuchFieldError var3) {
            }

            try {
                var0[ENotPlayerIntention.AI_INTENTION_FOLLOW.ordinal()] = 4;
            } catch (NoSuchFieldError var2) {
            }

            try {
                var0[ENotPlayerIntention.AI_INTENTION_IDLE.ordinal()] = 1;
            } catch (NoSuchFieldError var1) {
            }

            $SWITCH_TABLE$hero$npc$ENotPlayerIntention = var0;
            return var0;
        }
    }

    public static class HatredComparator implements Comparator {

        public int compare(Object _o1, Object _o2) {
            return ((Monster.HatredTarget) _o2).hatredValue - ((Monster.HatredTarget) _o1).hatredValue;
        }
    }

    public class HatredTarget {

        private HeroPlayer player;
        private int hatredValue;

        public HatredTarget(HeroPlayer _player, int _hatredValue) {
            this.player = _player;
            this.hatredValue = _hatredValue;
        }
    }
}
