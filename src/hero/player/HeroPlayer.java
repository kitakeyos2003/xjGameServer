// 
// Decompiled by Procyon v0.5.36
// 
package hero.player;

import hero.fight.broadcast.SpecialViewStatusBroadcast;
import hero.effect.service.EffectServiceImpl;
import hero.item.Goods;
import hero.share.message.Warning;
import hero.player.service.PlayerDAO;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.player.message.NextGiftTimeNotify;
import hero.item.dictionary.GoodsContents;
import hero.fight.service.FightConfig;
import hero.fight.service.FightServiceImpl;
import hero.fight.message.FightStatusChangeNotify;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.session.Session;
import hero.guild.service.GuildServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.expressions.service.CEService;
import hero.lover.service.LoverServiceImpl;
import hero.npc.function.system.MarryNPC;
import hero.group.service.GroupServiceImpl;
import hero.map.message.PlayerRefreshNotify;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.message.DisappearNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.Map;
import java.util.Iterator;
import java.util.List;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.service.PlayerServiceImpl;
import hero.player.service.PlayerConfig;
import java.util.TimerTask;
import hero.effect.Effect;
import hero.share.EObjectType;
import hero.item.special.BigTonicBall;
import hero.lover.service.LoverLevel;
import java.util.Timer;
import hero.npc.dict.AnswerQuestionData;
import hero.skill.PassiveSkill;
import hero.skill.ActiveSkill;
import java.util.HashMap;
import javolution.util.FastMap;
import hero.npc.Monster;
import java.util.ArrayList;
import hero.npc.Npc;
import hero.item.detail.EGoodsTrait;
import hero.item.bag.Inventory;
import hero.item.bag.PlayerBodyWearPetList;
import hero.item.bag.BodyWear;
import hero.charge.ChargeInfo;
import hero.player.define.ESex;
import org.apache.log4j.Logger;
import yoyo.service.base.player.IPlayer;
import hero.share.ME2GameObject;

public class HeroPlayer extends ME2GameObject implements IPlayer {

    private static Logger log;
    private int sessionID;
    private int msgQueueIndex;
    private int userID;
    private int exp;
    private int expShow;
    private int upgradeNeedExp;
    private int upgradeNeedExpShow;
    private int hpIntervalResumeValue;
    private int mpIntervalResumeValue;
    private int mpIntervalResumeValueInFight;
    public long lastAttackTime;
    public long loginTime;
    public long offlineTime;
    public long lastLogoutTime;
    public long totalPlayTime;
    public long nowPlayTime;
    public boolean giftTipSend;
    public int lastReceiveGift;
    private long lastTimeOfUPdateDB;
    private ESex sex;
    private boolean swaping;
    private LoginInfo gameInfo;
    private ChargeInfo chargeInfo;
    private BodyWear bodyWear;
    private PlayerBodyWearPetList bodyWearPetList;
    private Inventory inventory;
    private int[][] shortcutKeyList;
    public boolean needUpdateDB;
    private int groupID;
    private int guildID;
    private short homeID;
    private EGoodsTrait autoSellTrait;
    private int duelTargetUserID;
    private boolean isSelling;
    public boolean inited;
    public boolean isRemotePhysicsAttack;
    private Npc escortTarget;
    private float hatredModulus;
    private float experienceModulus;
    private static final float maxExperienceModulus = 6.0f;
    private float moneyModulus;
    private static final float maxMoneyModulus = 1.1f;
    private ArrayList<Monster> inWhatMonsterHatredList;
    private FastMap<Integer, Long> pvpTargetTable;
    public HashMap<Integer, ActiveSkill> activeSkillTable;
    public ArrayList<ActiveSkill> activeSkillList;
    public ArrayList<PassiveSkill> passiveSkillList;
    public AnswerQuestionData questionGroup;
    public byte[] bagSizes;
    private Timer timer;
    public String spouse;
    public short surplusSkillPoint;
    public boolean canRemoveAllFromMarryMap;
    public byte illegalOperation;
    public long timeVerify;
    public byte walkCounter;
    public Timer waitingTimer;
    public boolean waitingTimerRunning;
    public long leftMasterTime;
    private int loverValue;
    public LoverLevel loverLever;
    public boolean marryed;
    public long chatWorldTime;
    public long chatClanTime;
    public static final byte MAX_HEAVENBOOK_POSITION = 3;
    public int[] heaven_book_ids;
    public boolean heavenBookSame;
    public byte currInlayHeavenBookPosition;
    public int currInlayHeavenBookID;
    public long speedyMail;
    public short hairdoImageID;
    private BigTonicBall redBigTonicBall;
    private BigTonicBall buleBigTonicBall;
    public int currHookHours;
    public boolean buyHookExp;
    public int receivedRepeateTaskTimes;
    private int canReceiveRepeateTaskTimes;
    public int walkIllegalCounter;
    public boolean openWorldChat;
    public boolean openClanChat;
    public boolean openMapChat;
    public boolean openSingleChat;
    private String dcndjtk;

    static {
        HeroPlayer.log = Logger.getLogger((Class) HeroPlayer.class);
    }

    public HeroPlayer(final int _userID) {
        this.hatredModulus = 1.0f;
        this.experienceModulus = 1.0f;
        this.moneyModulus = 1.0f;
        this.spouse = "";
        this.canRemoveAllFromMarryMap = false;
        this.waitingTimer = null;
        this.waitingTimerRunning = false;
        this.marryed = false;
        this.heaven_book_ids = new int[3];
        this.heavenBookSame = false;
        this.currInlayHeavenBookPosition = -1;
        this.currInlayHeavenBookID = 0;
        this.canReceiveRepeateTaskTimes = 5;
        this.openWorldChat = true;
        this.openClanChat = true;
        this.openMapChat = true;
        this.openSingleChat = true;
        this.objectType = EObjectType.PLAYER;
        this.setUserID(_userID);
        this.setMoveSpeed((byte) 3);
        this.bodyWear = new BodyWear();
        this.bodyWearPetList = new PlayerBodyWearPetList();
        this.shortcutKeyList = new int[26][2];
        this.gameInfo = new LoginInfo();
        this.inWhatMonsterHatredList = new ArrayList<Monster>();
        this.pvpTargetTable = (FastMap<Integer, Long>) new FastMap();
        this.activeSkillTable = new HashMap<Integer, ActiveSkill>();
        this.activeSkillList = new ArrayList<ActiveSkill>();
        this.passiveSkillList = new ArrayList<PassiveSkill>();
        this.effectList = new ArrayList<Effect>();
        this.chargeInfo = new ChargeInfo(_userID);
        this.autoSellTrait = EGoodsTrait.SHI_QI;
        this.redBigTonicBall = null;
        this.buleBigTonicBall = null;
        this.questionGroup = null;
    }

    public HeroPlayer() {
        this.hatredModulus = 1.0f;
        this.experienceModulus = 1.0f;
        this.moneyModulus = 1.0f;
        this.spouse = "";
        this.canRemoveAllFromMarryMap = false;
        this.waitingTimer = null;
        this.waitingTimerRunning = false;
        this.marryed = false;
        this.heaven_book_ids = new int[3];
        this.heavenBookSame = false;
        this.currInlayHeavenBookPosition = -1;
        this.currInlayHeavenBookID = 0;
        this.canReceiveRepeateTaskTimes = 5;
        this.openWorldChat = true;
        this.openClanChat = true;
        this.openMapChat = true;
        this.openSingleChat = true;
        this.gameInfo = new LoginInfo();
        this.bodyWear = new BodyWear();
        this.bodyWearPetList = new PlayerBodyWearPetList();
    }

    @Override
    public void active() {
        super.active();
        this.isDead = false;
        this.inFighting = false;
        this.lastTimeOfUPdateDB = System.currentTimeMillis();
        this.lastAttackTime = this.lastTimeOfUPdateDB;
        (this.timer = new Timer()).schedule(new HeartBeat(), 10000L, 3000L);
        this.initPet();
        this.initHairdo();
    }

    private void initHairdo() {
        this.hairdoImageID = PlayerServiceImpl.getInstance().getConfig().getLimbsConfig().getHairIcon(this.sex, this.getClan());
    }

    @Override
    public void init() {
        this.inited = true;
        this.illegalOperation = 0;
        this.timeVerify = System.currentTimeMillis();
    }

    public void initPet() {
        List<Pet> petlist = PetServiceImpl.getInstance().getPetList(this.userID);
        if (petlist != null && petlist.size() > 0) {
            for (final Pet pet : petlist) {
                if (pet.isView) {
                    pet.loginTime = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public void invalid() {
        super.invalid();
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        this.sessionID = -1;
        this.msgQueueIndex = -1;
    }

    @Override
    public void free() {
        this.clearMonsterAbout();
        this.invalid();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void gotoMap(final Map _map) {
        HeroPlayer.log.debug((String.valueOf(this.where().getID()) + " -- player " + this.getName() + " goto map id = " + _map.getID() + " -- etype = " + _map.getMapType()));
        if (this.where() != null && _map != null) {
            this.where().getPlayerList().remove(this);
            short currMapID = 0;
            if (this.where() != _map) {
                MapSynchronousInfoBroadcast.getInstance().put(this.where(), new DisappearNotify(this.getObjectType().value(), this.getID(), this.getHp(), this.getBaseProperty().getHpMax(), this.getMp(), this.getBaseProperty().getMpMax()), false, 0);
                currMapID = this.where().getID();
            }
            this.live(_map);
            _map.getPlayerList().add(this);
            MapSynchronousInfoBroadcast.getInstance().put(this.where(), new PlayerRefreshNotify(this), true, this.getID());
            GroupServiceImpl.getInstance().groupMemberListHpMpNotify(this);
            this.needUpdateDB = true;
            if (currMapID == 406 && this.canRemoveAllFromMarryMap) {
                if (this.getUserID() == MarryNPC.marryer[0]) {
                    PlayerServiceImpl.getInstance().getPlayerByUserID(MarryNPC.marryer[1]).canRemoveAllFromMarryMap = false;
                } else if (this.getUserID() == MarryNPC.marryer[1]) {
                    PlayerServiceImpl.getInstance().getPlayerByUserID(MarryNPC.marryer[0]).canRemoveAllFromMarryMap = false;
                }
                MarryNPC.loverExitMarryMap(this);
                Timer removeAllPlayerOutMarryMapTimer = LoverServiceImpl.getInstance().getRemoveAllPlayerOutMarryMapTimer(this.getUserID());
                if (LoverServiceImpl.getInstance().getRemoveAllPlayerOutMarryMapTimer(this.getUserID()) != null) {
                    removeAllPlayerOutMarryMapTimer.cancel();
                }
                this.canRemoveAllFromMarryMap = false;
            }
        }
    }

    public void born(final Map _map) {
        if (_map != null) {
            this.live(_map);
            _map.getPlayerList().add(this);
            MapSynchronousInfoBroadcast.getInstance().put(this.where(), new PlayerRefreshNotify(this), true, this.getID());
        }
    }

    public void addMoney(final int _money) {
        if (_money != 0) {
            this.money += _money;
            if (this.money > 1000000000) {
                this.money = 1000000000;
            } else if (this.money < 0) {
                this.money = 0;
            }
            this.needUpdateDB = true;
        }
    }

    public void setExp(final int _exp) {
        if (_exp > 1000000000) {
            this.exp = 1000000000;
        } else {
            this.exp = _exp;
        }
    }

    public int getExp() {
        return this.exp;
    }

    public int getExpShow() {
        return this.expShow;
    }

    public void setExpShow(final int _exp) {
        this.expShow = _exp;
    }

    public int getUpgradeNeedExp() {
        return this.upgradeNeedExp;
    }

    public int getUpgradeNeedExpShow() {
        return this.upgradeNeedExpShow;
    }

    public void setUpgradeNeedExp(final int _exp) {
        this.upgradeNeedExp = _exp;
    }

    public void setUpgradeNeedExpShow(final int _exp) {
        this.upgradeNeedExpShow = _exp;
    }

    public boolean addExp(final int _exp) {
        boolean getExp = false;
        HeroPlayer.log.info(("[" + this.getName() + "]\u83b7\u5f97\u7ecf\u9a8c\u503c:" + _exp));
        if (this.getLevel() >= PlayerServiceImpl.getInstance().getConfig().max_level) {
            return getExp;
        }
        if (_exp <= 0) {
            return getExp;
        }
        this.exp += _exp;
        this.expShow += _exp;
        while (this.exp >= this.upgradeNeedExp) {
            if (this.getLevel() >= PlayerServiceImpl.getInstance().getConfig().max_level) {
                this.exp = this.upgradeNeedExp - 1;
                this.expShow = this.upgradeNeedExpShow - 1;
                break;
            }
            this.exp -= this.upgradeNeedExp;
            this.upgrade();
        }
        getExp = true;
        this.needUpdateDB = true;
        return getExp;
    }

    public boolean addExp(final int _exp, final float _modulus) {
        boolean getExp = false;
        if (this.getLevel() >= PlayerServiceImpl.getInstance().getConfig().max_level) {
            return getExp;
        }
        if (_exp <= 0) {
            return getExp;
        }
        this.exp += (int) (_exp * _modulus);
        this.expShow += (int) (_exp * _modulus);
        while (this.exp >= this.upgradeNeedExp) {
            if (this.getLevel() >= PlayerServiceImpl.getInstance().getConfig().max_level) {
                this.exp = this.upgradeNeedExp - 1;
                this.expShow = this.upgradeNeedExpShow - 1;
                break;
            }
            this.exp -= this.upgradeNeedExp;
            this.upgrade();
        }
        getExp = true;
        this.needUpdateDB = true;
        return getExp;
    }

    private int upgrade() {
        this.setLevel(this.getLevel() + 1);
        int level = this.getLevel();
        int nowAdd = 0;
        int nextAdd = 0;
        for (int i = 1; i <= level; ++i) {
            if (i == level) {
                nextAdd += CEService.totalUpgradeExp(i);
            } else {
                nowAdd += CEService.totalUpgradeExp(i);
                nextAdd += CEService.totalUpgradeExp(i);
            }
        }
        this.upgradeNeedExp = CEService.expToNextLevel(this.getLevel(), (float) this.upgradeNeedExp);
        this.expShow = this.exp + nowAdd;
        this.upgradeNeedExpShow = nextAdd;
        this.surplusSkillPoint += (short) PlayerServiceImpl.getInstance().getConfig().getUpgradeSkillPoint();
        PlayerServiceImpl.getInstance().roleUpgrade(this);
        PlayerServiceImpl.getInstance().updateLevel(this.getUserID(), this.getLevel(), this.getVocation(), this.getMoney(), this.getExp());
        LogServiceImpl.getInstance().upgradeLog(this.getLoginInfo().accountID, this.getUserID(), this.getName(), this.getLoginInfo().loginMsisdn, this.where().getName(), this.getLevel());
        GuildServiceImpl.getInstance().menberUpgrade(this);
        return this.getLevel();
    }

    public void setHomeID(final short _homeID) {
        this.homeID = _homeID;
    }

    public short getHomeID() {
        return this.homeID;
    }

    public void clearMonsterAbout() {
        if (this.inWhatMonsterHatredList.size() > 0) {
            for (final Monster monster : this.inWhatMonsterHatredList) {
                monster.clearHatred(this);
            }
            this.inWhatMonsterHatredList.clear();
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setInventory(final Inventory _inventory) {
        this.inventory = _inventory;
    }

    public ESex getSex() {
        return this.sex;
    }

    public void setSex(final ESex _sex) {
        this.sex = _sex;
    }

    public void resetHpResumeValue(final int _value) {
        this.hpIntervalResumeValue = _value;
    }

    public int getHpResumeValue() {
        return this.hpIntervalResumeValue;
    }

    public void resetMpResumeValue(final int _value) {
        this.mpIntervalResumeValue = _value;
    }

    public int getMpResumeValue() {
        return this.mpIntervalResumeValue;
    }

    public void resetMpResumeValueAtFight(final int _value) {
        this.mpIntervalResumeValueInFight = _value;
    }

    public int getMpResumeValueAtFight() {
        return this.mpIntervalResumeValueInFight;
    }

    @Override
    public int getMsgQueueIndex() {
        return this.msgQueueIndex;
    }

    @Override
    public void setSession(final Session _session) {
        if (_session != null) {
            this.sessionID = _session.ID;
            this.msgQueueIndex = _session.index;
            _session.objectID = this.getID();
            _session.nickName = this.getName();
        }
    }

    @Override
    public int getSessionID() {
        return this.sessionID;
    }

    public void setUserID(final int _uid) {
        this.userID = _uid;
    }

    public int getUserID() {
        return this.userID;
    }

    public BodyWear getBodyWear() {
        return this.bodyWear;
    }

    public PlayerBodyWearPetList getBodyWearPetList() {
        return this.bodyWearPetList;
    }

    @Override
    public void enterFight() {
        if (!this.inFighting) {
            super.enterFight();
            ResponseMessageQueue.getInstance().put(this.getMsgQueueIndex(), new FightStatusChangeNotify(true));
        }
    }

    @Override
    public void disengageFight() {
        if (this.inFighting && this.isEnable()) {
            super.disengageFight();
            if (this.getRedTonicBall() != null) {
                this.getRedTonicBall().use(this);
            }
            if (this.getBuleTonicBall() != null) {
                this.getBuleTonicBall().use(this);
            }
            ResponseMessageQueue.getInstance().put(this.getMsgQueueIndex(), new FightStatusChangeNotify(false));
        }
    }

    public void clearHatredSource() {
        this.inWhatMonsterHatredList.clear();
    }

    public int[][] getShortcutKeyList() {
        return this.shortcutKeyList;
    }

    public void heartBeat() {
        try {
            if (this.enabled) {
                if (!this.isDead) {
                    if (!this.inFighting) {
                        if (this.getHp() < this.getActualProperty().getHpMax()) {
                            this.addHp(this.getHpResumeValue());
                            FightServiceImpl.getInstance().processPersionalHpChange(this, this.getHpResumeValue());
                            this.needUpdateDB = true;
                        }
                        if (this.getMp() < this.getActualProperty().getMpMax()) {
                            this.addMp(this.getMpResumeValue());
                            FightServiceImpl.getInstance().processSingleTargetMpChange(this, false);
                            this.needUpdateDB = true;
                        }
                    } else {
                        if (this.getMp() < this.getActualProperty().getMpMax()) {
                            this.addMp(this.getMpResumeValueAtFight());
                            FightServiceImpl.getInstance().processSingleTargetMpChange(this, false);
                            this.needUpdateDB = true;
                        }
                        if (this.pvpTargetTable.size() > 0) {
                            Iterator<Integer> pvpTargetIterator = this.pvpTargetTable.keySet().iterator();
                            long now = System.currentTimeMillis();
                            while (pvpTargetIterator.hasNext()) {
                                int pvpTargetUserID = pvpTargetIterator.next();
                                if (now - (long) this.pvpTargetTable.get(pvpTargetUserID) >= FightConfig.DISENGAGE_FIGHT_TIME) {
                                    pvpTargetIterator.remove();
                                }
                            }
                            if (this.pvpTargetTable.size() == 0 && this.inWhatMonsterHatredList.size() == 0) {
                                this.disengageFight();
                            }
                        }
                    }
                }
                if (this.effectList.size() > 0) {
                    int i = 0;
                    while (i < this.effectList.size()) {
                        try {
                            Effect effect = this.effectList.get(i);
                            if (!effect.heartbeat(this)) {
                                continue;
                            }
                            ++i;
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
                this.nowPlayTime = (System.currentTimeMillis() - this.loginTime) / 60000L;
                int nowTime = (int) (this.totalPlayTime + this.nowPlayTime);
                int nowGift = this.lastReceiveGift + 1;
                CountDownGiftData gift = PlayerServiceImpl.getInstance().getCountDownGift(nowGift);
                if (gift != null && PlayerServiceImpl.getInstance().getConfig().open_countdown_gift) {
                    Goods goods = GoodsContents.getGoods(gift.giftBagID);
                    if (goods != null) {
                        if (!this.giftTipSend) {
                            ResponseMessageQueue.getInstance().put(this.msgQueueIndex, new NextGiftTimeNotify(gift.needTime - nowTime, goods.getName(), goods.getDescription(), gift.icon));
                            this.giftTipSend = true;
                        }
                        if (nowTime >= gift.needTime) {
                            GoodsServiceImpl.getInstance().addGoods2Package(this, gift.giftBagID, 1, CauseLog.COUNTDOWNGIFTSEND);
                            this.lastReceiveGift = nowGift;
                            PlayerDAO.updateLastGift(this.userID, gift.id);
                            CountDownGiftData nextGift = PlayerServiceImpl.getInstance().getCountDownGift(nowGift + 1);
                            byte tipUI = 1;
                            if (this.isSwaping()) {
                                tipUI = 0;
                            }
                            ResponseMessageQueue.getInstance().put(this.msgQueueIndex, new Warning("\u606d\u559c\u60a8\u83b7\u5f97\u7cfb\u7edf\u53d1\u653e\u7684%fn,\u4e0b\u4e00\u4e2a\u793c\u5305\u5c06\u5728%ft\u5206\u949f\u540e\u4e0b\u53d1,\u5c4a\u65f6\u8bf7\u786e\u4fdd\u60a8\u7684\u5b9d\u7269\u80cc\u5305\u67091\u4e2a\u4f4d\u7f6e\u63a5\u6536\u5b83".replaceAll("%fn", goods.getName()).replaceAll("%ft", String.valueOf(nextGift.needTime - gift.needTime)), tipUI));
                            goods = GoodsContents.getGoods(nextGift.giftBagID);
                            ResponseMessageQueue.getInstance().put(this.msgQueueIndex, new NextGiftTimeNotify(nextGift.needTime - gift.needTime, goods.getName(), goods.getDescription(), gift.icon));
                        }
                    }
                }
                for (int j = 0; j < this.activeSkillList.size(); ++j) {
                    this.activeSkillList.get(j).traceIntervalCDTime();
                }
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public boolean canBeAttackBy(final ME2GameObject _object) {
        return _object.getClan() != this.getClan();
    }

    public LoginInfo getLoginInfo() {
        return this.gameInfo;
    }

    @Override
    public void die(final ME2GameObject _killer) {
        super.die(_killer);
        EffectServiceImpl.getInstance().playerDie(this);
        if (this.inWhatMonsterHatredList.size() > 0) {
            for (final Monster monster : this.inWhatMonsterHatredList) {
                monster.clearHatred(this);
            }
            this.inWhatMonsterHatredList.clear();
        }
        if (this.pvpTargetTable.size() > 0) {
            Iterator<Integer> pvpTargetIterator = this.pvpTargetTable.keySet().iterator();
            while (pvpTargetIterator.hasNext()) {
                HeroPlayer pvpTarget = PlayerServiceImpl.getInstance().getPlayerByUserID(pvpTargetIterator.next());
                if (pvpTarget != null) {
                    pvpTarget.removePvpTarget(this.getUserID());
                }
            }
            this.pvpTargetTable.clear();
        }
        SpecialViewStatusBroadcast.send(this, (byte) 4);
        GoodsServiceImpl.getInstance().processEquipmentDurabilityAfterDie(this);
    }

    @Override
    public void revive(final ME2GameObject _savior) {
        super.revive(_savior);
    }

    public int getGroupID() {
        return this.groupID;
    }

    public void setGroupID(final int _groupID) {
        this.groupID = _groupID;
    }

    public void startDuel(final int _targetUserID) {
        this.duelTargetUserID = _targetUserID;
    }

    public boolean inDuelStatus() {
        return this.duelTargetUserID != 0;
    }

    public void endDuel() {
        this.duelTargetUserID = 0;
    }

    public int getDuelTargetUserID() {
        return this.duelTargetUserID;
    }

    public float getHatredModulus() {
        return this.hatredModulus;
    }

    public void changeHatredModulus(final float _changeModulus) {
        this.hatredModulus += _changeModulus;
    }

    public void clearHatredModulus() {
        this.hatredModulus = 1.0f;
    }

    public boolean isSwaping() {
        return this.swaping;
    }

    public void swapBegin() {
        this.swaping = true;
    }

    public void swapOver() {
        this.swaping = false;
    }

    public float getExperienceModulus() {
        return this.experienceModulus * PlayerServiceImpl.getInstance().getConfig().getCurrExpModulus();
    }

    public void changeExperienceModulus(final float _changeModulus) {
        if (_changeModulus > 0.0f) {
            this.experienceModulus *= 1.0f + _changeModulus;
        } else if (_changeModulus < 0.0f) {
            this.experienceModulus /= 1.0f - _changeModulus;
        }
        if (this.experienceModulus > 6.0f) {
            this.experienceModulus = 6.0f;
        }
    }

    public float getMoneyModulus() {
        return this.moneyModulus;
    }

    public void changeMoneyModulus(final float _changeModulus) {
        if (_changeModulus > 0.0f) {
            this.moneyModulus *= 1.0f + _changeModulus;
        } else if (_changeModulus < 0.0f) {
            this.moneyModulus /= 1.0f - _changeModulus;
        }
        if (this.moneyModulus > 1.1f) {
            this.moneyModulus = 1.1f;
        }
    }

    public void clearMoneyModulus() {
        this.moneyModulus = 1.0f;
    }

    public void setEscortTarget(final Npc _npc) {
        this.escortTarget = _npc;
    }

    public Npc getEscortTarget() {
        return this.escortTarget;
    }

    public int getGuildID() {
        return this.guildID;
    }

    public void setGuildID(final int _guildID) {
        this.guildID = _guildID;
    }

    public void addHatredSource(final Monster _monster) {
        if (!this.inWhatMonsterHatredList.contains(_monster)) {
            this.inWhatMonsterHatredList.add(_monster);
        }
    }

    public void removeHatredSource(final Monster _monster) {
        this.inWhatMonsterHatredList.remove(_monster);
        if (this.inWhatMonsterHatredList.size() == 0 && this.pvpTargetTable.size() == 0) {
            this.disengageFight();
        }
    }

    public void beResumeHpByOthers(final HeroPlayer _others, final int _valideResumeValue) {
        if (this.inWhatMonsterHatredList.size() > 0) {
            for (final Monster monster : this.inWhatMonsterHatredList) {
                monster.targetBeResumed(_others, _valideResumeValue);
            }
        }
        if (this.pvpTargetTable.size() > 0) {
            Iterator<Integer> pvpTargetIterator = this.pvpTargetTable.keySet().iterator();
            while (pvpTargetIterator.hasNext()) {
                HeroPlayer pvpTarget = PlayerServiceImpl.getInstance().getPlayerByUserID(pvpTargetIterator.next());
                if (pvpTarget != null) {
                    pvpTarget.refreshPvPFightTime(_others.getUserID());
                    _others.refreshPvPFightTime(pvpTarget.getUserID());
                }
            }
        }
        if (this.inFighting) {
            _others.enterFight();
        }
    }

    public int getHatredMonsterNumber() {
        return this.inWhatMonsterHatredList.size();
    }

    public int getPvpPlayerNumber() {
        return this.pvpTargetTable.size();
    }

    public void refreshPvPFightTime(final int _userID) {
        this.enterFight();
        this.pvpTargetTable.put(_userID, System.currentTimeMillis());
    }

    public void removePvpTarget(final int _userID) {
        this.pvpTargetTable.remove(_userID);
        if (this.pvpTargetTable.size() == 0 && this.inWhatMonsterHatredList.size() == 0) {
            this.disengageFight();
        }
    }

    public void clearPvpTarget() {
        this.pvpTargetTable.clear();
    }

    public void setLastTimeOfUPdateDB(final long _time) {
        this.lastTimeOfUPdateDB = _time;
    }

    public long getLastTimeOfUPdateDB() {
        return this.lastTimeOfUPdateDB;
    }

    public ChargeInfo getChargeInfo() {
        return this.chargeInfo;
    }

    public boolean isSelling() {
        return this.isSelling;
    }

    public void setSellStatus(final boolean _status) {
        this.isSelling = _status;
    }

    public void setAutoSellTrait(final EGoodsTrait _goodsTrait) {
        this.autoSellTrait = _goodsTrait;
    }

    public EGoodsTrait getAutoSellTrait() {
        return this.autoSellTrait;
    }

    public int getLoverValue() {
        return this.loverValue;
    }

    public void setLoverValue(final int _loverValue) {
        this.loverValue = _loverValue;
    }

    public boolean addLoverValue(final int loverValue) {
        this.loverValue += loverValue;
        this.needUpdateDB = true;
        if (!this.marryed && this.loverValue > 3000) {
            this.loverValue = 3000;
            return false;
        }
        if (this.marryed) {
            LoverServiceImpl.getInstance().loverUpgrade(this);
        }
        return true;
    }

    public void clearLoverValue() {
        this.loverValue = 0;
        this.needUpdateDB = true;
    }

    public int loverDays(final HeroPlayer player) {
        return player.getLoverValue() / 10 / 60 / 2;
    }

    @Override
    public void happenFight() {
    }

    @Override
    public byte getDefaultSpeed() {
        return 3;
    }

    public BigTonicBall getBuleTonicBall() {
        return this.buleBigTonicBall;
    }

    public void setBuleTonicBall(final BigTonicBall _TonicBall) {
        if (this.buleBigTonicBall != null) {
            this.buleBigTonicBall.isActivate = 0;
        }
        if (_TonicBall != null && _TonicBall.isActivate == 0) {
            _TonicBall.isActivate = 1;
        }
        this.buleBigTonicBall = _TonicBall;
    }

    public BigTonicBall getRedTonicBall() {
        return this.redBigTonicBall;
    }

    public void setRedTonicBall(final BigTonicBall _TonicBall) {
        if (this.redBigTonicBall != null) {
            this.redBigTonicBall.isActivate = 0;
        }
        if (_TonicBall != null && _TonicBall.isActivate == 0) {
            _TonicBall.isActivate = 1;
        }
        this.redBigTonicBall = _TonicBall;
    }

    public int getCanReceiveRepeateTaskTimes() {
        return this.canReceiveRepeateTaskTimes;
    }

    public void setCanReceiveRepeateTaskTimes(final int canReceiveRepeateTaskTimes) {
        this.canReceiveRepeateTaskTimes = canReceiveRepeateTaskTimes;
    }

    public boolean canReceiveRepeateTask() {
        return this.receivedRepeateTaskTimes < this.canReceiveRepeateTaskTimes;
    }

    @Override
    public void addHp(final int _hp) {
        if (!this.isSelling()) {
            super.addHp(_hp);
        }
    }

    @Override
    public void setHp(final int _hp) {
        if (!this.isSelling()) {
            super.setHp(_hp);
        }
    }

    public String getDcndjtk() {
        return this.dcndjtk;
    }

    public void setDcndjtk(final String dcndjtk) {
        this.dcndjtk = dcndjtk;
    }

    class HeartBeat extends TimerTask {

        @Override
        public void run() {
            HeroPlayer.this.heartBeat();
        }
    }
}
