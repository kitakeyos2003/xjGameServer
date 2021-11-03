// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.service;

import hero.task.message.NotifyPlayerReciveRepeateTaskTimes;
import hero.item.bag.exception.BagException;
import hero.item.special.HeavenBook;
import hero.item.special.ESpecialGoodsType;
import hero.log.service.ServiceType;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.detail.EGoodsTrait;
import hero.player.message.RefreshRoleProperty;
import hero.item.Goods;
import hero.player.message.HotKeySumByMedicament;
import hero.item.SpecialGoods;
import hero.item.message.GoodsShortcutKeyChangeNotify;
import hero.share.cd.CDUnit;
import hero.item.Medicament;
import hero.item.dictionary.GoodsContents;
import hero.player.message.VocationChangeNotify;
import hero.micro.help.HelpService;
import hero.group.Group;
import hero.task.service.TaskServiceImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.player.message.RoleUpgradeNotify;
import hero.player.message.ExperienceAddNotify;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.player.message.MoneyChangeNofity;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.player.IPlayerDAO;
import yoyo.service.ServiceManager;
import hero.npc.function.system.postbox.MailService;
import hero.share.letter.LetterService;
import hero.micro.teach.TeachService;
import hero.social.service.SocialServiceImpl;
import hero.guild.service.GuildServiceImpl;
import java.io.IOException;
import hero.share.EVocationType;
import hero.player.define.ESex;
import yoyo.tools.YOYOOutputStream;
import yoyo.service.base.player.IPlayer;
import hero.share.service.MagicDamage;
import hero.item.EquipmentInstance;
import hero.share.EVocation;
import hero.effect.service.EffectServiceImpl;
import hero.skill.service.SkillServiceImpl;
import hero.share.EMagic;
import hero.item.Weapon;
import hero.item.dictionary.SuiteEquipmentDataDict;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.Calendar;
import hero.micro.store.PersionalStore;
import java.util.Iterator;
import java.util.List;
import hero.share.service.ShareServiceImpl;
import yoyo.service.base.session.SessionServiceImpl;
import hero.group.service.GroupServiceImpl;
import hero.micro.store.StoreService;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.item.service.GoodsDAO;
import hero.item.bag.Inventory;
import hero.charge.service.ChargeServiceImpl;
import hero.map.service.MapServiceImpl;
import hero.player.define.EClan;
import hero.map.service.MapRelationDict;
import hero.share.EObjectLevel;
import hero.expressions.service.CEService;
import hero.lover.service.LoverServiceImpl;
import hero.share.service.LogWriter;
import yoyo.service.base.session.Session;
import java.util.TimerTask;
import java.util.HashMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Timer;
import hero.player.CountDownGiftData;
import javolution.util.FastMap;
import hero.player.HeroPlayer;
import javolution.util.FastList;
import org.apache.log4j.Logger;
import yoyo.service.base.player.IPlayerService;
import yoyo.service.base.AbsServiceAdaptor;

public class PlayerServiceImpl extends AbsServiceAdaptor<PlayerConfig> implements IPlayerService {

    private static Logger log;
    private static PlayerServiceImpl instance;
    private FastList<HeroPlayer> playerList;
    private FastMap<Integer, HeroPlayer> playerTableByUserID;
    private FastMap<Integer, HeroPlayer> sessionIDKeyPlayerTable;
    private FastMap<Integer, CountDownGiftData> countDownGiftDict;
    private PlayerDAO dao;
    public static final byte MONEY_DRAW_LOCATION_NONE = 0;
    public static final byte MONEY_DRAW_LOCATION_MIDDLE = 1;
    public static final byte MONEY_DRAW_LOCATION_WARNING = 2;
    private Timer timer;
    private Timer loverValueTimer;
    private FastMap<Integer, Timer> loverValueTimerMap;
    private Map<Integer, Integer> loverValueOrderMap;
    private Timer loverValueOrderTimer;
    public static final short REPLY_INLAY_HEAVEN_BOOK_COMMAND_CODE = 1049;
    private static final int UPDATE_LOVER_VALUE_ORDER_INTERVAL = 7200000;
    private static final int UPDATE_DB_DELAY = 55000;
    private static final int UPDATE_DB_INTERVAL = 60000;
    private static final int CLEAR_PLAYER_TASK_DELAY = 140000;
    private static final int CLEAR_PLAYER_TASK_CHECK_INTERVAL = 60000;
    private static final int PLAYER_OBJECT_KEEP_TIME = 60000;
    public static final byte SHUTCUT_KEY_TYPE_OF_SYSTEM = 1;
    public static final byte SHUTCUT_KEY_TYPE_OF_SKILL = 2;
    public static final byte SHUTCUT_KEY_TYPE_OF_GOODS = 3;
    public static final SimpleDateFormat DATE_FORMATTER;

    static {
        PlayerServiceImpl.log = Logger.getLogger((Class) PlayerServiceImpl.class);
        (DATE_FORMATTER = (SimpleDateFormat) DateFormat.getDateTimeInstance()).applyPattern("yy-MM-dd HH:mm:ss:SS");
    }

    private PlayerServiceImpl() {
        this.loverValueTimerMap = (FastMap<Integer, Timer>) new FastMap();
        this.loverValueOrderMap = new HashMap<Integer, Integer>();
        this.config = new PlayerConfig();
        this.playerList = (FastList<HeroPlayer>) new FastList();
        this.sessionIDKeyPlayerTable = (FastMap<Integer, HeroPlayer>) new FastMap();
        this.playerTableByUserID = (FastMap<Integer, HeroPlayer>) new FastMap();
        this.timer = new Timer();
        this.loverValueOrderTimer = new Timer();
        this.dao = new PlayerDAO();
        this.countDownGiftDict = (FastMap<Integer, CountDownGiftData>) new FastMap();
    }

    public static PlayerServiceImpl getInstance() {
        if (PlayerServiceImpl.instance == null) {
            PlayerServiceImpl.instance = new PlayerServiceImpl();
        }
        return PlayerServiceImpl.instance;
    }

    public Timer getLoverValueTimerByUserID(final int userID) {
        return (Timer) this.loverValueTimerMap.get(userID);
    }

    public void startLoverValueTimer(final HeroPlayer player) {
        (this.loverValueTimer = new Timer()).schedule(new PlayerLoverValueTimer(player.getName(), player.spouse), 0L, 60000L);
        this.loverValueTimerMap.put(player.getUserID(), this.loverValueTimer);
    }

    public void removeLoverValueTimer(final HeroPlayer player) {
        this.loverValueTimerMap.remove(player.getUserID());
    }

    @Override
    public void createSession(final Session _session) {
        HeroPlayer player = (HeroPlayer) this.playerTableByUserID.get(_session.userID);
        if (player == null) {
            player = (HeroPlayer) this.dao.load(_session.userID);
            if (player == null) {
                LogWriter.println("\u52a0\u8f7d\u89d2\u8272\u5931\u8d25 \uff1a" + _session.userID);
                return;
            }
            PlayerDAO.loadPlayerAccountInfo(player);
            player.chatClanTime = PlayerDAO.loadClanChatWait(player.getUserID());
            player.chatWorldTime = PlayerDAO.loadWorldChatWait(player.getUserID());
            String spouse = LoverServiceImpl.getInstance().whoLoveMe(player.getName());
            if (spouse != null) {
                player.spouse = spouse;
            } else {
                spouse = LoverServiceImpl.getInstance().whoMarriedMe(player.getName());
                if (spouse != null) {
                    player.marryed = true;
                    player.spouse = spouse;
                    player.loverLever = LoverServiceImpl.getInstance().getLoverLevel(player.getLoverValue());
                }
            }
            if (spouse != null) {
                HeroPlayer other = this.getPlayerByName(spouse);
                if (other != null && other.isEnable() && !other.isDead()) {
                    this.startLoverValueTimer(player);
                }
            }
            PlayerServiceImpl.log.debug(("[" + player.getName() + "] spouse:" + spouse));
            int rtt = PlayerDAO.getRepeatTaskGoodsTimes(player.getUserID());
            PlayerServiceImpl.log.info(("rtt =" + rtt + ",player.receivedRepeateTaskTimes=" + player.receivedRepeateTaskTimes));
            if (rtt > 5 && rtt + 5 < player.receivedRepeateTaskTimes) {
                player.receivedRepeateTaskTimes = 0;
            }
            player.setCanReceiveRepeateTaskTimes(player.getCanReceiveRepeateTaskTimes() + rtt);
            this.playerList.add(player);
            this.playerTableByUserID.put(_session.userID, player);
            LogWriter.println("\u52a0\u8f7d\u89d2\u8272\u6210\u529f \uff1a" + player.getName());
        } else {
            if (player.isDead()) {
                player.setHp(CEService.hpByStamina(CEService.playerBaseAttribute(player.getLevel(), player.getVocation().getStaminaCalPara()), player.getLevel(), player.getObjectLevel().getHpCalPara()));
                player.setMp(CEService.mpByInte(CEService.playerBaseAttribute(player.getLevel(), player.getVocation().getInteCalcPara()), player.getLevel(), EObjectLevel.NORMAL.getMpCalPara()));
                short[] relations = MapRelationDict.getInstance().getRelationByMapID(player.where().getID());
                short mapid = relations[2];
                if (player.getClan() == EClan.HE_MU_DU && relations[8] > 0) {
                    mapid = relations[8];
                }
                hero.map.Map where = MapServiceImpl.getInstance().getNormalMapByID(mapid);
                player.setCellX(where.getBornX());
                player.setCellY(where.getBornY());
                player.live(where);
            }
            player.offlineTime = 0L;
        }
        player.loginTime = System.currentTimeMillis();
        player.setSession(_session);
        player.needUpdateDB = true;
        player.getChargeInfo().pointAmount = ChargeServiceImpl.getInstance().getBalancePoint(player.getLoginInfo().accountID);
        PlayerServiceImpl.log.debug(("login init player point=" + player.getChargeInfo().pointAmount));
        this.sessionIDKeyPlayerTable.put(_session.ID, player);
    }

    public HeroPlayer load(final int userID) {
        HeroPlayer player = (HeroPlayer) this.dao.load(userID);
        if (player != null && player.getInventory() == null) {
            player.setInventory(new Inventory(player.getUserID(), player.bagSizes));
            GoodsDAO.loadPlayerGoods(player);
        }
        return player;
    }

    @Override
    public void sessionFree(final Session _session) {
        if (_session != null) {
            try {
                HeroPlayer player = (HeroPlayer) this.sessionIDKeyPlayerTable.remove(_session.ID);
                if (player != null) {
                    getInstance().removeLoverValueTimer(player);
                    player.free();
                    player.offlineTime = System.currentTimeMillis();
                    LogServiceImpl.getInstance().roleOnOffLog(player.getLoginInfo().accountID, player.getUserID(), player.getName(), player.getLoginInfo().loginMsisdn, player.loginTime, player.getLoginInfo().logoutCause, player.getLoginInfo().userAgent, player.getLoginInfo().clientVersion, player.getLoginInfo().clientType, player.getLoginInfo().communicatePipe, player.where().getName(), player.offlineTime, player.getLoginInfo().publisher, player.where().getID());
                    List<Pet> petlist = PetServiceImpl.getInstance().getPetList(player.getUserID());
                    if (petlist != null && petlist.size() > 0) {
                        for (final Pet pet : petlist) {
                            PetServiceImpl.getInstance().updatePet(player.getUserID(), pet);
                        }
                    }
                    PersionalStore store = StoreService.get(player.getUserID());
                    if (store != null && (store.opened || player.isSelling())) {
                        PlayerServiceImpl.log.debug(("\u9000\u51fa\u6e38\u620f\uff0c\u6446\u644a\u72b6\u6001 = " + store.opened + ", player storestatus = " + player.isSelling()));
                        StoreService.takeOffAll(player);
                        StoreService.clear(player.getUserID());
                    }
                    GroupServiceImpl.getInstance().clean(player.getUserID());
                    getInstance().getPlayerList().remove(player);
                    SessionServiceImpl.getInstance().fireSessionFree(player.getSessionID());
                    getInstance().getSessionPlayerList().remove(player.getSessionID());
                    getInstance().getUserIDPlayerList().remove(player.getUserID());
                    ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(player.getUserID());
                    PlayerServiceImpl.log.info((String.valueOf(player.getName()) + "; sessionID=" + _session.ID + "  \u9000\u51fa,\u6216\u8005\u88ab\u8e22"));
                }
            } catch (Exception e) {
                LogWriter.error(PlayerServiceImpl.instance, e);
            }
        }
    }

    @Override
    public void clean(final int _userID) {
        this.playerTableByUserID.remove(_userID);
    }

    @Override
    protected void start() {
        this.timer.schedule(new PlayerInfoUpdateTask(), 55000L, 60000L);
        this.timer.schedule(new PlayerClearTask(), 140000L, 60000L);
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        this.timer.schedule(new ClearPlayerReceiveRepeateTaskTimes(), calendar.getTimeInMillis() - System.currentTimeMillis(), 86400000L);
        this.loverValueOrderTimer.schedule(new PlayerLoverValueOrderTimer(), 0L, 7200000L);
        this.loadCountDownGift(((PlayerConfig) this.config).countdown_gift_data_path);
    }

    private void loadCountDownGift(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e2) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
            return;
        }
        File[] dataFileList = dataPath.listFiles();
        try {
            File[] array;
            for (int length = (array = dataFileList).length, i = 0; i < length; ++i) {
                File dataFile = array[i];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    CountDownGiftData countDownGift = null;
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            countDownGift = new CountDownGiftData();
                            countDownGift.name = subE.elementTextTrim("name");
                            countDownGift.giftBagID = Integer.valueOf(subE.elementTextTrim("giftBagID"));
                            countDownGift.needTime = Integer.valueOf(subE.elementTextTrim("needTime"));
                            countDownGift.id = Integer.valueOf(subE.elementTextTrim("id"));
                            countDownGift.content = subE.elementTextTrim("content");
                            countDownGift.icon = Integer.valueOf(subE.elementTextTrim("icon"));
                        }
                        if (!this.countDownGiftDict.containsKey(countDownGift.id)) {
                            this.countDownGiftDict.put(countDownGift.id, countDownGift);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CountDownGiftData getCountDownGift(final int _id) {
        return (CountDownGiftData) this.countDownGiftDict.get(_id);
    }

    public int getPlayerLoverValueOrder(final int userID) {
        return (this.loverValueOrderMap.get(userID) == null) ? 0 : this.loverValueOrderMap.get(userID);
    }

    public short getInitBornMapID(final EClan _clan) {
        return ((PlayerConfig) this.config).getBornMapID(_clan);
    }

    public short getInitBornX(final EClan _clan) {
        return ((PlayerConfig) this.config).getBornPoint(_clan)[0];
    }

    public short getInitBornY(final EClan _clan) {
        return ((PlayerConfig) this.config).getBornPoint(_clan)[1];
    }

    public void initProperty(final HeroPlayer _player) {
        int maxHP = 0;
        int maxMP = 0;
        int stamina = 0;
        int inte = 0;
        int strength = 0;
        int spirit = 0;
        int lucky = 0;
        int defense = 0;
        int agility = 0;
        short physicsDeathblowLevel = 0;
        short magicDeathblowLevel = 0;
        short hitLevel = 0;
        short duckLevel = 0;
        int level = _player.getLevel();
        EVocation vocation = _player.getVocation();
        stamina += CEService.playerBaseAttribute(level, vocation.getStaminaCalPara());
        inte += CEService.playerBaseAttribute(level, vocation.getInteCalcPara());
        strength += CEService.playerBaseAttribute(level, vocation.getStrengthCalcPara());
        spirit += CEService.playerBaseAttribute(level, vocation.getSpiritCalcPara());
        lucky += CEService.playerBaseAttribute(level, vocation.getLuckyCalcPara());
        agility += CEService.playerBaseAttribute(level, vocation.getAgilityCalcPara());
        physicsDeathblowLevel = CEService.physicsDeathblowLevel(agility, lucky);
        magicDeathblowLevel = CEService.magicDeathblowLevel(inte, lucky);
        hitLevel = CEService.hitLevel(lucky);
        duckLevel = CEService.duckLevel(agility, lucky);
        EquipmentInstance[] equipmentList = _player.getBodyWear().getEquipmentList();
        _player.getBaseProperty().getMagicFastnessList().clear();
        EquipmentInstance[] array;
        for (int length = (array = equipmentList).length, i = 0; i < length; ++i) {
            EquipmentInstance e = array[i];
            if (e != null && e.getCurrentDurabilityPoint() > 0) {
                maxHP += e.getArchetype().atribute.hp;
                maxHP += (int) (e.getArchetype().atribute.hp * e.getGeneralEnhance().getBasicModulus());
                maxMP += e.getArchetype().atribute.mp;
                maxMP += (int) (e.getArchetype().atribute.mp * e.getGeneralEnhance().getBasicModulus());
                stamina += e.getArchetype().atribute.stamina;
                stamina += (int) (e.getArchetype().atribute.stamina * e.getGeneralEnhance().getBasicModulus());
                inte += e.getArchetype().atribute.inte;
                inte += (int) (e.getArchetype().atribute.inte * e.getGeneralEnhance().getBasicModulus());
                strength += e.getArchetype().atribute.strength;
                strength += (int) (e.getArchetype().atribute.strength * e.getGeneralEnhance().getBasicModulus());
                spirit += e.getArchetype().atribute.spirit;
                spirit += (int) (e.getArchetype().atribute.spirit * e.getGeneralEnhance().getBasicModulus());
                lucky += e.getArchetype().atribute.lucky;
                lucky += (int) (e.getArchetype().atribute.lucky * e.getGeneralEnhance().getBasicModulus());
                agility += e.getArchetype().atribute.agility;
                agility += (int) (e.getArchetype().atribute.agility * e.getGeneralEnhance().getBasicModulus());
                defense += e.getArchetype().atribute.defense;
                defense += (int) (e.getArchetype().atribute.defense * e.getGeneralEnhance().getDefenseModulus());
                physicsDeathblowLevel += e.getArchetype().atribute.physicsDeathblowLevel;
                physicsDeathblowLevel += (short) (e.getArchetype().atribute.physicsDeathblowLevel * e.getGeneralEnhance().getAdjuvantModulus());
                magicDeathblowLevel += e.getArchetype().atribute.magicDeathblowLevel;
                magicDeathblowLevel += (short) (e.getArchetype().atribute.magicDeathblowLevel * e.getGeneralEnhance().getAdjuvantModulus());
                hitLevel += e.getArchetype().atribute.hitLevel;
                hitLevel += (short) (e.getArchetype().atribute.hitLevel * e.getGeneralEnhance().getAdjuvantModulus());
                duckLevel += e.getArchetype().atribute.duckLevel;
                duckLevel += (short) (e.getArchetype().atribute.duckLevel * e.getGeneralEnhance().getDefenseModulus());
                _player.getBaseProperty().getMagicFastnessList().add(e.getArchetype().getMagicFastnessList());
                _player.getBaseProperty().getMagicFastnessList().add(e.getArchetype().getMagicFastnessList(), e.getGeneralEnhance().getDefenseModulus());
            }
        }
        short suiteID = _player.getBodyWear().hasSuite();
        if (suiteID != 0) {
            SuiteEquipmentDataDict.SuiteEData suiteEData = SuiteEquipmentDataDict.getInstance().getSuiteData(suiteID);
            if (suiteEData != null) {
                if (suiteEData.atribute != null) {
                    maxHP += suiteEData.atribute.hp;
                    maxMP += suiteEData.atribute.mp;
                    stamina += suiteEData.atribute.stamina;
                    inte += suiteEData.atribute.inte;
                    strength += suiteEData.atribute.strength;
                    spirit += suiteEData.atribute.spirit;
                    lucky += suiteEData.atribute.lucky;
                    agility += suiteEData.atribute.agility;
                    defense += suiteEData.atribute.defense;
                    physicsDeathblowLevel += suiteEData.atribute.physicsDeathblowLevel;
                    magicDeathblowLevel += suiteEData.atribute.magicDeathblowLevel;
                    hitLevel += suiteEData.atribute.hitLevel;
                    duckLevel += suiteEData.atribute.duckLevel;
                }
                _player.getBaseProperty().getMagicFastnessList().add(suiteEData.fastnessList);
            }
        }
        defense += CEService.defenseBySpirit(spirit, vocation.getPhysicsDefenceSpiritPara());
        _player.getBaseProperty().setStamina(stamina);
        _player.getBaseProperty().setInte(inte);
        _player.getBaseProperty().setStrength(strength);
        _player.getBaseProperty().setSpirit(spirit);
        _player.getBaseProperty().setLucky(lucky);
        _player.getBaseProperty().setAgility(agility);
        _player.getBaseProperty().setDefense(defense);
        _player.getBaseProperty().setPhysicsDeathblowLevel(physicsDeathblowLevel);
        _player.getBaseProperty().setMagicDeathblowLevel(magicDeathblowLevel);
        _player.getBaseProperty().setHitLevel(hitLevel);
        _player.getBaseProperty().setPhysicsDuckLevel(duckLevel);
        maxHP += CEService.hpByStamina(stamina, level, _player.getObjectLevel().getHpCalPara());
        maxMP += CEService.mpByInte(inte, level, EObjectLevel.NORMAL.getMpCalPara());
        _player.getBaseProperty().setHpMax(maxHP);
        _player.getBaseProperty().setMpMax(maxMP);
        Weapon weapon = null;
        EquipmentInstance ei = _player.getBodyWear().getWeapon();
        int weaponMinMagicHarm = 0;
        int weaponMaxMagicHarm = 0;
        int maxPhysicsAttack;
        int minPhysicsAttack;
        if (ei != null) {
            weapon = (Weapon) ei.getArchetype();
            if (ei.getCurrentDurabilityPoint() > 0) {
                _player.setAttackRange(weapon.getAttackDistance());
                _player.setBaseAttackImmobilityTime((int) (weapon.getImmobilityTime() * 1000.0f));
                if (ei.getMagicDamage() != null) {
                    weaponMaxMagicHarm = ei.getMagicDamage().maxDamageValue;
                    weaponMinMagicHarm = ei.getMagicDamage().minDamageValue;
                }
                maxPhysicsAttack = CEService.maxPhysicsAttack(strength, agility, vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), ei.getWeaponMaxPhysicsAttack(), weaponMaxMagicHarm, weapon.getImmobilityTime(), EObjectLevel.NORMAL.getPhysicsAttckCalPara());
                minPhysicsAttack = CEService.minPhysicsAttack(strength, agility, vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), ei.getWeaponMinPhysicsAttack(), weaponMinMagicHarm, weapon.getImmobilityTime(), EObjectLevel.NORMAL.getPhysicsAttckCalPara());
            } else {
                _player.setAttackRange((short) 3);
                _player.setBaseAttackImmobilityTime(1000);
                maxPhysicsAttack = CEService.maxPhysicsAttack(strength, agility, vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), 0, 0, 1.0f, EObjectLevel.NORMAL.getPhysicsAttckCalPara());
                minPhysicsAttack = CEService.minPhysicsAttack(strength, agility, vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), 0, 0, 1.0f, EObjectLevel.NORMAL.getPhysicsAttckCalPara());
            }
        } else {
            _player.setAttackRange((short) 3);
            _player.setBaseAttackImmobilityTime(1000);
            maxPhysicsAttack = CEService.maxPhysicsAttack(strength, agility, vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), 0, 0, 1.0f, EObjectLevel.NORMAL.getPhysicsAttckCalPara());
            minPhysicsAttack = CEService.minPhysicsAttack(strength, agility, vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), 0, 0, 1.0f, EObjectLevel.NORMAL.getPhysicsAttckCalPara());
        }
        _player.getBaseProperty().setMaxPhysicsAttack(maxPhysicsAttack);
        _player.getBaseProperty().setMinPhysicsAttack(minPhysicsAttack);
        int baseMagicHarmByInte = CEService.magicHarmByInte(inte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.SANCTITY, (float) baseMagicHarmByInte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.UMBRA, (float) baseMagicHarmByInte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.FIRE, (float) baseMagicHarmByInte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.WATER, (float) baseMagicHarmByInte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.SOIL, (float) baseMagicHarmByInte);
        if (weapon != null && ei.getCurrentDurabilityPoint() > 0) {
            MagicDamage weaponMagicDamage = ei.getMagicDamage();
            if (weaponMagicDamage != null) {
                int magicHarmValue = (weaponMagicDamage.minDamageValue + weaponMagicDamage.maxDamageValue) / 2;
                _player.getBaseProperty().getBaseMagicHarmList().reset(weaponMagicDamage.magic, (float) magicHarmValue);
            }
        }
        _player.clearHatredModulus();
        _player.getActualProperty().clearNoneBaseProperty();
        _player.getResistOddsList().clear();
        SkillServiceImpl.getInstance().changePropertySkillAction(_player, true);
        _player.getActualProperty().getMagicFastnessList().reset(_player.getBaseProperty().getMagicFastnessList());
        _player.getActualProperty().getBaseMagicHarmList().reset(_player.getBaseProperty().getBaseMagicHarmList());
        _player.getActualProperty().setMaxPhysicsAttack(_player.getBaseProperty().getMaxPhysicsAttack());
        _player.getActualProperty().setMinPhysicsAttack(_player.getBaseProperty().getMinPhysicsAttack());
        _player.setActualAttackImmobilityTime(_player.getBaseAttackImmobilityTime());
        _player.getActualProperty().setStamina(_player.getBaseProperty().getStamina());
        _player.getActualProperty().setInte(_player.getBaseProperty().getInte());
        _player.getActualProperty().setStrength(_player.getBaseProperty().getStrength());
        _player.getActualProperty().setSpirit(_player.getBaseProperty().getSpirit());
        _player.getActualProperty().setLucky(_player.getBaseProperty().getLucky());
        _player.getActualProperty().setAgility(_player.getBaseProperty().getAgility());
        _player.getActualProperty().setDefense(_player.getBaseProperty().getDefense());
        _player.getActualProperty().setPhysicsDeathblowLevel(_player.getBaseProperty().getPhysicsDeathblowLevel());
        _player.getActualProperty().setMagicDeathblowLevel(_player.getBaseProperty().getMagicDeathblowLevel());
        _player.getActualProperty().setHitLevel(_player.getBaseProperty().getHitLevel());
        _player.getActualProperty().setPhysicsDuckLevel(_player.getBaseProperty().getPhysicsDuckLevel());
        _player.getActualProperty().setHpMax(_player.getBaseProperty().getHpMax());
        _player.getActualProperty().setMpMax(_player.getBaseProperty().getMpMax());
        EffectServiceImpl.getInstance().staticEffectAction(_player);
        if (_player.getHp() > _player.getActualProperty().getHpMax()) {
            _player.setHp(_player.getActualProperty().getHpMax());
        }
        if (_player.getMp() > _player.getActualProperty().getMpMax()) {
            _player.setMp(_player.getActualProperty().getMpMax());
        }
        _player.resetHpResumeValue(CEService.hpResumeAuto(_player.getLevel(), _player.getActualProperty().getSpirit(), _player.getVocation().getStaminaCalPara()));
        _player.resetMpResumeValue(CEService.mpResumeAuto(_player.getLevel(), _player.getActualProperty().getSpirit(), _player.getVocation().getInteCalcPara()));
        _player.resetMpResumeValueAtFight(CEService.mpResumeAutoInFighting(_player.getMpResumeValue()));
    }

    public void reCalculateRoleProperty(final HeroPlayer _player) {
        int maxHP = 0;
        int maxMP = 0;
        int stamina = 0;
        int inte = 0;
        int strength = 0;
        int spirit = 0;
        int lucky = 0;
        int defense = 0;
        int agility = 0;
        short physicsDeathblowLevel = 0;
        short magicDeathblowLevel = 0;
        short hitLevel = 0;
        short duckLevel = 0;
        int level = _player.getLevel();
        EVocation vocation = _player.getVocation();
        stamina += CEService.playerBaseAttribute(level, vocation.getStaminaCalPara());
        inte += CEService.playerBaseAttribute(level, vocation.getInteCalcPara());
        strength += CEService.playerBaseAttribute(level, vocation.getStrengthCalcPara());
        spirit += CEService.playerBaseAttribute(level, vocation.getSpiritCalcPara());
        lucky += CEService.playerBaseAttribute(level, vocation.getLuckyCalcPara());
        agility += CEService.playerBaseAttribute(level, vocation.getAgilityCalcPara());
        physicsDeathblowLevel = CEService.physicsDeathblowLevel(agility, lucky);
        magicDeathblowLevel = CEService.magicDeathblowLevel(inte, lucky);
        hitLevel = CEService.hitLevel(lucky);
        duckLevel = CEService.duckLevel(agility, lucky);
        EquipmentInstance[] equipmentList = _player.getBodyWear().getEquipmentList();
        _player.getBaseProperty().getMagicFastnessList().clear();
        EquipmentInstance[] array;
        for (int length = (array = equipmentList).length, i = 0; i < length; ++i) {
            EquipmentInstance e = array[i];
            if (e != null && e.getCurrentDurabilityPoint() > 0) {
                maxHP += e.getArchetype().atribute.hp;
                maxHP += (int) (e.getArchetype().atribute.hp * e.getGeneralEnhance().getBasicModulus());
                maxMP += e.getArchetype().atribute.mp;
                maxMP += (int) (e.getArchetype().atribute.mp * e.getGeneralEnhance().getBasicModulus());
                stamina += e.getArchetype().atribute.stamina;
                stamina += (int) (e.getArchetype().atribute.stamina * e.getGeneralEnhance().getBasicModulus());
                inte += e.getArchetype().atribute.inte;
                inte += (int) (e.getArchetype().atribute.inte * e.getGeneralEnhance().getBasicModulus());
                strength += e.getArchetype().atribute.strength;
                strength += (int) (e.getArchetype().atribute.strength * e.getGeneralEnhance().getBasicModulus());
                spirit += e.getArchetype().atribute.spirit;
                spirit += (int) (e.getArchetype().atribute.spirit * e.getGeneralEnhance().getBasicModulus());
                lucky += e.getArchetype().atribute.lucky;
                lucky += (int) (e.getArchetype().atribute.lucky * e.getGeneralEnhance().getBasicModulus());
                agility += e.getArchetype().atribute.agility;
                agility += (int) (e.getArchetype().atribute.agility * e.getGeneralEnhance().getBasicModulus());
                defense += e.getArchetype().atribute.defense;
                defense += (int) (e.getArchetype().atribute.defense * e.getGeneralEnhance().getDefenseModulus());
                physicsDeathblowLevel += e.getArchetype().atribute.physicsDeathblowLevel;
                physicsDeathblowLevel += (short) (e.getArchetype().atribute.physicsDeathblowLevel * e.getGeneralEnhance().getAdjuvantModulus());
                magicDeathblowLevel += e.getArchetype().atribute.magicDeathblowLevel;
                magicDeathblowLevel += (short) (e.getArchetype().atribute.magicDeathblowLevel * e.getGeneralEnhance().getAdjuvantModulus());
                hitLevel += e.getArchetype().atribute.hitLevel;
                hitLevel += (short) (e.getArchetype().atribute.hitLevel * e.getGeneralEnhance().getAdjuvantModulus());
                duckLevel += e.getArchetype().atribute.duckLevel;
                duckLevel += (short) (e.getArchetype().atribute.duckLevel * e.getGeneralEnhance().getDefenseModulus());
                _player.getBaseProperty().getMagicFastnessList().add(e.getArchetype().getMagicFastnessList());
                _player.getBaseProperty().getMagicFastnessList().add(e.getArchetype().getMagicFastnessList(), e.getGeneralEnhance().getDefenseModulus());
            }
        }
        short suiteID = _player.getBodyWear().hasSuite();
        if (suiteID != 0) {
            SuiteEquipmentDataDict.SuiteEData suiteEData = SuiteEquipmentDataDict.getInstance().getSuiteData(suiteID);
            if (suiteEData != null) {
                if (suiteEData.atribute != null) {
                    maxHP += suiteEData.atribute.hp;
                    maxMP += suiteEData.atribute.mp;
                    stamina += suiteEData.atribute.stamina;
                    inte += suiteEData.atribute.inte;
                    strength += suiteEData.atribute.strength;
                    spirit += suiteEData.atribute.spirit;
                    lucky += suiteEData.atribute.lucky;
                    agility += suiteEData.atribute.agility;
                    defense += suiteEData.atribute.defense;
                    physicsDeathblowLevel += suiteEData.atribute.physicsDeathblowLevel;
                    magicDeathblowLevel += suiteEData.atribute.magicDeathblowLevel;
                    hitLevel += suiteEData.atribute.hitLevel;
                    duckLevel += suiteEData.atribute.duckLevel;
                }
                _player.getBaseProperty().getMagicFastnessList().add(suiteEData.fastnessList);
            }
        }
        defense += CEService.defenseBySpirit(spirit, vocation.getPhysicsDefenceSpiritPara());
        _player.getBaseProperty().setStamina(stamina);
        _player.getBaseProperty().setInte(inte);
        _player.getBaseProperty().setStrength(strength);
        _player.getBaseProperty().setSpirit(spirit);
        _player.getBaseProperty().setLucky(lucky);
        _player.getBaseProperty().setAgility(agility);
        _player.getBaseProperty().setDefense(defense);
        _player.getBaseProperty().setPhysicsDeathblowLevel(physicsDeathblowLevel);
        _player.getBaseProperty().setMagicDeathblowLevel(magicDeathblowLevel);
        _player.getBaseProperty().setHitLevel(hitLevel);
        _player.getBaseProperty().setPhysicsDuckLevel(duckLevel);
        maxHP += CEService.hpByStamina(stamina, level, _player.getObjectLevel().getHpCalPara());
        maxMP += CEService.mpByInte(inte, level, EObjectLevel.NORMAL.getMpCalPara());
        _player.getBaseProperty().setHpMax(maxHP);
        _player.getBaseProperty().setMpMax(maxMP);
        Weapon weapon = null;
        EquipmentInstance ei = _player.getBodyWear().getWeapon();
        int weaponMinMagicHarm = 0;
        int weaponMaxMagicHarm = 0;
        int maxPhysicsAttack;
        int minPhysicsAttack;
        if (ei != null) {
            weapon = (Weapon) ei.getArchetype();
            if (ei.getCurrentDurabilityPoint() > 0) {
                _player.setAttackRange(weapon.getAttackDistance());
                _player.setBaseAttackImmobilityTime((int) (weapon.getImmobilityTime() * 1000.0f));
                if (ei.getMagicDamage() != null) {
                    weaponMaxMagicHarm = ei.getMagicDamage().maxDamageValue;
                    weaponMinMagicHarm = ei.getMagicDamage().minDamageValue;
                }
                maxPhysicsAttack = CEService.maxPhysicsAttack(strength, agility, vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), ei.getWeaponMaxPhysicsAttack(), weaponMaxMagicHarm, weapon.getImmobilityTime(), EObjectLevel.NORMAL.getPhysicsAttckCalPara());
                minPhysicsAttack = CEService.minPhysicsAttack(strength, agility, vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), ei.getWeaponMinPhysicsAttack(), weaponMinMagicHarm, weapon.getImmobilityTime(), EObjectLevel.NORMAL.getPhysicsAttckCalPara());
            } else {
                _player.setAttackRange((short) 3);
                _player.setBaseAttackImmobilityTime(1000);
                maxPhysicsAttack = CEService.maxPhysicsAttack(strength, agility, vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), 0, 0, 1.0f, EObjectLevel.NORMAL.getPhysicsAttckCalPara());
                minPhysicsAttack = CEService.minPhysicsAttack(strength, agility, vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), 0, 0, 1.0f, EObjectLevel.NORMAL.getPhysicsAttckCalPara());
            }
        } else {
            _player.setAttackRange((short) 3);
            _player.setBaseAttackImmobilityTime(1000);
            maxPhysicsAttack = CEService.maxPhysicsAttack(strength, agility, vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), 0, 0, 1.0f, EObjectLevel.NORMAL.getPhysicsAttckCalPara());
            minPhysicsAttack = CEService.minPhysicsAttack(strength, agility, vocation.getPhysicsAttackParaA(), vocation.getPhysicsAttackParaB(), vocation.getPhysicsAttackParaC(), 0, 0, 1.0f, EObjectLevel.NORMAL.getPhysicsAttckCalPara());
        }
        _player.getBaseProperty().setMaxPhysicsAttack(maxPhysicsAttack);
        _player.getBaseProperty().setMinPhysicsAttack(minPhysicsAttack);
        int baseMagicHarmByInte = CEService.magicHarmByInte(inte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.SANCTITY, (float) baseMagicHarmByInte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.UMBRA, (float) baseMagicHarmByInte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.FIRE, (float) baseMagicHarmByInte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.WATER, (float) baseMagicHarmByInte);
        _player.getBaseProperty().getBaseMagicHarmList().reset(EMagic.SOIL, (float) baseMagicHarmByInte);
        if (weapon != null && ei.getCurrentDurabilityPoint() > 0) {
            MagicDamage weaponMagicDamage = ei.getMagicDamage();
            if (weaponMagicDamage != null) {
                int magicHarmValue = (weaponMagicDamage.minDamageValue + weaponMagicDamage.maxDamageValue) / 2;
                _player.getBaseProperty().getBaseMagicHarmList().reset(weaponMagicDamage.magic, (float) magicHarmValue);
            }
        }
        _player.clearHatredModulus();
        _player.getActualProperty().clearNoneBaseProperty();
        _player.getResistOddsList().clear();
        SkillServiceImpl.getInstance().changePropertySkillAction(_player, true);
        _player.getActualProperty().getMagicFastnessList().reset(_player.getBaseProperty().getMagicFastnessList());
        _player.getActualProperty().getBaseMagicHarmList().reset(_player.getBaseProperty().getBaseMagicHarmList());
        _player.getActualProperty().setMaxPhysicsAttack(_player.getBaseProperty().getMaxPhysicsAttack());
        _player.getActualProperty().setMinPhysicsAttack(_player.getBaseProperty().getMinPhysicsAttack());
        _player.setActualAttackImmobilityTime(_player.getBaseAttackImmobilityTime());
        _player.getActualProperty().setStamina(_player.getBaseProperty().getStamina());
        _player.getActualProperty().setInte(_player.getBaseProperty().getInte());
        _player.getActualProperty().setStrength(_player.getBaseProperty().getStrength());
        _player.getActualProperty().setSpirit(_player.getBaseProperty().getSpirit());
        _player.getActualProperty().setLucky(_player.getBaseProperty().getLucky());
        _player.getActualProperty().setAgility(_player.getBaseProperty().getAgility());
        _player.getActualProperty().setDefense(_player.getBaseProperty().getDefense());
        _player.getActualProperty().setPhysicsDeathblowLevel(_player.getBaseProperty().getPhysicsDeathblowLevel());
        _player.getActualProperty().setMagicDeathblowLevel(_player.getBaseProperty().getMagicDeathblowLevel());
        _player.getActualProperty().setHitLevel(_player.getBaseProperty().getHitLevel());
        _player.getActualProperty().setPhysicsDuckLevel(_player.getBaseProperty().getPhysicsDuckLevel());
        _player.getActualProperty().setHpMax(_player.getBaseProperty().getHpMax());
        _player.getActualProperty().setMpMax(_player.getBaseProperty().getMpMax());
        EffectServiceImpl.getInstance().staticEffectAction(_player);
        if (_player.getHp() > _player.getActualProperty().getHpMax()) {
            _player.setHp(_player.getActualProperty().getHpMax());
        }
        if (_player.getMp() > _player.getActualProperty().getMpMax()) {
            _player.setMp(_player.getActualProperty().getMpMax());
        }
        _player.resetHpResumeValue(CEService.hpResumeAuto(_player.getLevel(), _player.getActualProperty().getSpirit(), _player.getVocation().getStaminaCalPara()));
        _player.resetMpResumeValue(CEService.mpResumeAuto(_player.getLevel(), _player.getActualProperty().getSpirit(), _player.getVocation().getInteCalcPara()));
        _player.resetMpResumeValueAtFight(CEService.mpResumeAutoInFighting(_player.getMpResumeValue()));
    }

    @Override
    public void dbUpdate(final int _userID) {
        HeroPlayer player = (HeroPlayer) this.playerTableByUserID.get(_userID);
        if (player != null && player.isEnable()) {
            this.dao.updateDB(player);
            player.needUpdateDB = false;
        }
    }

    public boolean dbUpdate(final HeroPlayer _player) {
        if (_player != null) {
            this.dao.updateDB(_player);
            _player.needUpdateDB = false;
            _player.setLastTimeOfUPdateDB(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public boolean getNovice(final HeroPlayer _player) {
        boolean result = false;
        if (_player != null && PlayerDAO.getNovice(_player.getUserID()) == 1) {
            result = true;
        }
        return result;
    }

    public void leaveNovice(final HeroPlayer _player) {
        PlayerDAO.updateNovice(_player.getUserID());
    }

    @Override
    public byte[] createRole(final int accountID, final short serverID, final int _userID, final String[] paras) {
        return this.dao.createRole(accountID, serverID, _userID, paras);
    }

    public byte[] listDefaultRole() {
        YOYOOutputStream outPipe = new YOYOOutputStream();
        PlayerServiceImpl.log.info(("\u89e6\u53d1listDefaultRole:" + this.toString()));
        try {
            PlayerConfig config = getInstance().getConfig();
            outPipe.writeByte(EClan.getValues().length);
            EClan[] values;
            for (int length = (values = EClan.getValues()).length, i = 0; i < length; ++i) {
                EClan clan = values[i];
                ESex[] values2;
                for (int length2 = (values2 = ESex.values()).length, j = 0; j < length2; ++j) {
                    ESex sex = values2[j];
                    outPipe.writeByte(sex.value());
                    outPipe.writeByte(clan.getID());
                    outPipe.writeShort(config.getLimbsConfig().getHeadImage(sex));
                    outPipe.writeShort(config.getLimbsConfig().getHeadAnimation(sex));
                    outPipe.writeShort(config.getLimbsConfig().getHairImage(sex, clan));
                    outPipe.writeShort(config.getLimbsConfig().getHairAnimation(sex, clan));
                    outPipe.writeShort(config.getLimbsConfig().getLegImage(sex));
                    outPipe.writeShort(config.getLimbsConfig().getLegAnimation(sex));
                    outPipe.writeShort(config.getLimbsConfig().getTailImage(sex, clan));
                    outPipe.writeShort(config.getLimbsConfig().getTailAnimation(sex, clan));
                    outPipe.writeShort(config.getLimbsConfig().getDieImage(clan));
                    outPipe.writeShort(config.getLimbsConfig().getDieAnimation(clan));
                }
            }
            outPipe.writeByte(EVocationType.values().length);
            EVocationType[] values3;
            for (int length3 = (values3 = EVocationType.values()).length, k = 0; k < length3; ++k) {
                EVocationType type = values3[k];
                outPipe.writeByte(type.getID());
                outPipe.writeShort(config.getInitArmorImageGroup(type)[0]);
                outPipe.writeShort(config.getInitArmorImageGroup(type)[1]);
                outPipe.writeByte(config.getInitArmorImageGroup(type)[2]);
                outPipe.writeShort(config.getInitArmorImageGroup(type)[3]);
                outPipe.writeShort(config.getInitArmorImageGroup(type)[4]);
                outPipe.writeByte(config.getInitArmorImageGroup(type)[5]);
                outPipe.writeShort(-1);
                outPipe.writeShort(-1);
                outPipe.writeShort(config.getInitWeaponImageGroup(type)[0]);
                outPipe.writeShort(config.getInitWeaponImageGroup(type)[1]);
                outPipe.writeShort(-1);
                outPipe.writeShort(-1);
                outPipe.writeShort(-1);
                outPipe.writeShort(-1);
                outPipe.writeShort(config.getInitWeaponImageGroup(type)[2]);
                outPipe.writeShort(config.getInitWeaponImageGroup(type)[3]);
                outPipe.writeShort(-1);
                outPipe.writeShort(-1);
            }
            outPipe.flush();
        } catch (IOException e) {
            e.printStackTrace();
            PlayerServiceImpl.log.error("listDefaultRole error : ", (Throwable) e);
        }
        byte[] rtnValue = outPipe.getBytes();
        return rtnValue;
    }

    @Override
    public int deleteRole(final int _userID) {
        GuildServiceImpl.getInstance().deleteRole(_userID);
        SocialServiceImpl.getInstance().deleteRole(_userID);
        TeachService.delteRole(_userID);
        LetterService.getInstance().deleteRole(_userID);
        MailService.getInstance().deleteRole(_userID);
        ServiceManager.getInstance().clean(_userID);
        return this.dao.deleteRole(_userID);
    }

    @Override
    public byte[] listRole(final int[] userIDs) {
        for (final int userid : userIDs) {
            PlayerServiceImpl.log.debug(("listRole userid = " + userid));
        }
        return this.dao.listRole(userIDs);
    }

    public void updateLevel(final int _playerUserID, final short _level, final EVocation _vocation, final int _money, final int _exp) {
        this.dao.updateLevel(_playerUserID, _level, _vocation, _money, _exp);
    }

    @Override
    public HeroPlayer getPlayerBySessionID(final int _sessionID) {
        return (HeroPlayer) this.sessionIDKeyPlayerTable.get(_sessionID);
    }

    @Override
    public HeroPlayer getPlayerByUserID(final int _userID) {
        if (_userID <= 0) {
            return null;
        }
        return (HeroPlayer) this.playerTableByUserID.get(_userID);
    }

    @Override
    public HeroPlayer getPlayerByName(final String _name) {
        for (int i = 0; i < this.playerList.size(); ++i) {
            HeroPlayer player;
            try {
                player = (HeroPlayer) this.playerList.get(i);
            } catch (Exception e) {
                return null;
            }
            if (player.isEnable() && player.getName().equals(_name)) {
                return player;
            }
        }
        return null;
    }

    public FastList<HeroPlayer> getPlayerList() {
        return this.playerList;
    }

    public FastMap<Integer, HeroPlayer> getUserIDPlayerList() {
        return this.playerTableByUserID;
    }

    public FastMap<Integer, HeroPlayer> getSessionPlayerList() {
        return this.sessionIDKeyPlayerTable;
    }

    public FastList<HeroPlayer> getPlayerListByClan(final EClan clan) {
        FastList<HeroPlayer> list = (FastList<HeroPlayer>) new FastList();
        HeroPlayer player = null;
        for (int i = 0; i < this.playerList.size(); ++i) {
            player = (HeroPlayer) this.playerList.get(i);
            if (player.getClan() == clan) {
                list.add(player);
            }
        }
        return list;
    }

    public int getPlayerNumber() {
        return this.sessionIDKeyPlayerTable.size();
    }

    @Override
    public IPlayerDAO getDAO() {
        return this.dao;
    }

    public boolean addMoney(final HeroPlayer _player, int _money, final float _expModulus, final int _drawLocation, final String _cause) {
        _money *= (int) _expModulus;
        if (_money > 0) {
            if (_player.getMoney() + _money <= 1000000000) {
                _player.addMoney(_money);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new MoneyChangeNofity(_money, _drawLocation));
                LogServiceImpl.getInstance().moneyChangeLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, _cause, _money);
                return true;
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u8d85\u8fc7\u4e0a\u9650"));
            return false;
        } else {
            if (_money >= 0) {
                return true;
            }
            if (_player.getMoney() + _money >= 0) {
                _player.addMoney(_money);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new MoneyChangeNofity(_money, _drawLocation));
                LogServiceImpl.getInstance().moneyChangeLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, _cause, _money);
                return true;
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u4e0d\u5920"));
            return false;
        }
    }

    public void addExperience(final HeroPlayer _player, int _exp, final float _expModulus, final int _drawLocation) {
        if (_exp > 0) {
            _exp *= (int) _expModulus;
            if (_player.addExp(_exp)) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ExperienceAddNotify(_exp, _drawLocation, _player.getExp(), _player.getExpShow()));
            }
        }
    }

    public void roleUpgrade(final HeroPlayer _player) {
        if (_player.getGroupID() > 0 && _player.spouse != null && _player.spouse.trim().length() > 0) {
            HeroPlayer spouser = this.getPlayerByName(_player.spouse);
            if (spouser != null && spouser.getGroupID() > 0 && _player.getGroupID() == spouser.getGroupID()) {
                Group group = GroupServiceImpl.getInstance().getGroup(_player.getGroupID());
                if (group != null && group.getMemberNumber() == 2) {
                    _player.addLoverValue(500);
                    spouser.addLoverValue(500);
                }
            }
        }
        this.dbUpdate(_player);
        this.reCalculateRoleProperty(_player);
        _player.setHp(_player.getBaseProperty().getHpMax());
        int maxMp = _player.getActualProperty().getMpMax();
        _player.setMp(maxMp);
        this.refreshRoleProperty(_player);
        RoleUpgradeNotify msg = new RoleUpgradeNotify(_player.getID(), _player.getLevel(), _player.getActualProperty().getHpMax(), maxMp, _player.surplusSkillPoint);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
        MapSynchronousInfoBroadcast.getInstance().put(_player.where(), msg, true, _player.getID());
        TaskServiceImpl.getInstance().notifyMapNpcTaskMark(_player, _player.where());
        GroupServiceImpl.getInstance().refreshMemberLevel(_player);
    }

    public String changePlayerProperty(final int _roleUserID, final short _level, final byte _vocationID, final int _money, int _exp) {
        if (_level > ((PlayerConfig) this.config).max_level) {
            return "\u7b49\u7ea7\u8d85\u51fa\u6700\u5927\u503c";
        }
        EVocation vocation = EVocation.getVocationByID(_vocationID);
        if (vocation == null) {
            return "\u4e0d\u5b58\u5728\u7684\u804c\u4e1a";
        }
        if (_money < 0) {
            return "\u91d1\u94b1\u4e0d\u80fd\u4e3a\u8d1f\u6570";
        }
        if (_exp < 0) {
            return "\u7ecf\u9a8c\u503c\u4e0d\u80fd\u4e3a\u8d1f\u6570";
        }
        HeroPlayer player = this.getPlayerByUserID(_roleUserID);
        if (player != null) {
            if (_level == player.getLevel() && vocation == player.getVocation() && _money == player.getMoney() && _exp == player.getExp()) {
                return "\u5c5e\u6027\u672a\u53d1\u751f\u4efb\u4f55\u53d8\u5316\uff0c\u65e0\u987b\u4fee\u6539";
            }
            if (_level != player.getLevel()) {
                player.setLevel(_level);
                player.setUpgradeNeedExp(CEService.totalUpgradeExp(player.getLevel()));
                this.reCalculateRoleProperty(player);
                if (player.isEnable()) {
                    this.refreshRoleProperty(player);
                    RoleUpgradeNotify msg = new RoleUpgradeNotify(player.getID(), player.getLevel(), player.getActualProperty().getHpMax(), player.getActualProperty().getMpMax(), player.surplusSkillPoint);
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                    MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg, true, player.getID());
                    TaskServiceImpl.getInstance().notifyMapNpcTaskMark(player, player.where());
                    HelpService.helpTip(player);
                    GroupServiceImpl.getInstance().refreshMemberLevel(player);
                }
            }
            if (vocation != player.getVocation()) {
                player.setVocation(vocation);
                this.refreshRoleProperty(player);
                if (player.isEnable()) {
                    getInstance().refreshRoleProperty(player);
                    VocationChangeNotify msg2 = new VocationChangeNotify(player.getID(), vocation.value(), player.getActualProperty().getHpMax(), player.getActualProperty().getMpMax());
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg2);
                    MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg2, true, player.getID());
                    TaskServiceImpl.getInstance().notifyMapNpcTaskMark(player, player.where());
                    GroupServiceImpl.getInstance().refreshMemberVocation(player);
                }
            }
            if (_money != player.getMoney()) {
                int moneyChange = _money - player.getMoney();
                player.addMoney(moneyChange);
                if (player.isEnable()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new MoneyChangeNofity(moneyChange, 0));
                }
                LogServiceImpl.getInstance().moneyChangeLog(player.getLoginInfo().accountID, player.getUserID(), player.getName(), player.getLoginInfo().loginMsisdn, "GM\u4fee\u6539", _money);
            }
            if (_exp != player.getExp()) {
                if (_exp >= player.getUpgradeNeedExp()) {
                    return "\u7ecf\u9a8c\u8d85\u51fa\u5f53\u524d\u7b49\u7ea7\u6700\u5927\u503c";
                }
                player.setExp(_exp);
                if (player.isEnable()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ExperienceAddNotify(_exp, 0, player.getExp(), player.getExpShow()));
                }
            } else if (player.getExp() >= player.getUpgradeNeedExp()) {
                player.setExp(player.getUpgradeNeedExp() - 1);
                _exp = player.getExp();
            }
        } else {
            int expOfUpgrade = CEService.totalUpgradeExp(_level);
            if (_exp >= expOfUpgrade) {
                _exp = expOfUpgrade - 1;
            }
        }
        this.dao.updateDB(_roleUserID, _level, vocation, _money, _exp);
        return null;
    }

    public void setShortcutKey(final HeroPlayer _player, final byte _shortcutKey, final byte _shortcutKeyType, final int _targetID) {
        int[][] shortcutKeyList = _player.getShortcutKeyList();
        int[][] array;
        for (int length = (array = shortcutKeyList).length, i = 0; i < length; ++i) {
            int[] shortcutKey = array[i];
            if (shortcutKey[0] == _shortcutKeyType && shortcutKey[1] == _targetID) {
                shortcutKey[1] = (shortcutKey[0] = 0);
            }
        }
        shortcutKeyList[_shortcutKey][0] = _shortcutKeyType;
        shortcutKeyList[_shortcutKey][1] = _targetID;
        this.dao.updateShortcutKey(_player.getUserID(), shortcutKeyList);
        if (_shortcutKeyType == 3) {
            Goods goods = GoodsContents.getGoods(_targetID);
            if (goods != null && goods instanceof Medicament) {
                Medicament medicament = (Medicament) goods;
                CDUnit cd = null;
                if (medicament.getMaxCdTime() > 0) {
                    cd = _player.userCDMap.get(medicament.getPublicCdVariable());
                    if (cd != null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GoodsShortcutKeyChangeNotify(_shortcutKey, cd.getTimeBySec(), cd.getMaxTime(), (short) cd.getKey()));
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GoodsShortcutKeyChangeNotify(_shortcutKey, 0, medicament.getMaxCdTime(), (short) medicament.getPublicCdVariable()));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GoodsShortcutKeyChangeNotify(_shortcutKey, 0, 0, (short) medicament.getPublicCdVariable()));
                }
            }
        }
        if (_shortcutKeyType == 3) {
            Goods goods = GoodsContents.getGoods(_targetID);
            if (goods != null && (goods instanceof Medicament || goods instanceof SpecialGoods)) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new HotKeySumByMedicament(_player));
            }
            if (goods != null && goods instanceof Medicament) {
                Medicament medicament = (Medicament) goods;
                CDUnit cd = null;
                if (medicament.getMaxCdTime() > 0) {
                    cd = _player.userCDMap.get(medicament.getPublicCdVariable());
                    if (cd != null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GoodsShortcutKeyChangeNotify(_shortcutKey, cd.getTimeBySec(), cd.getMaxTime(), (short) cd.getKey()));
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GoodsShortcutKeyChangeNotify(_shortcutKey, 0, medicament.getMaxCdTime(), (short) medicament.getPublicCdVariable()));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GoodsShortcutKeyChangeNotify(_shortcutKey, 0, 0, (short) medicament.getPublicCdVariable()));
                }
            }
        }
    }

    public void upgradeShortcutKeySkill(final HeroPlayer _player, final int _lowLevelSkillID, final int _highLevelSkillID) {
        int[][] shortcutKeyList = _player.getShortcutKeyList();
        int[][] array;
        for (int length = (array = shortcutKeyList).length, i = 0; i < length; ++i) {
            int[] shortcutKey = array[i];
            if (shortcutKey[0] == 2 && shortcutKey[1] == _lowLevelSkillID) {
                shortcutKey[1] = _highLevelSkillID;
                this.dao.updateShortcutKey(_player.getUserID(), shortcutKeyList);
                return;
            }
        }
    }

    public void resetSkillShortcutKey(final HeroPlayer _player) {
        boolean changed = false;
        int[][] shortcutKeyList;
        for (int length = (shortcutKeyList = _player.getShortcutKeyList()).length, i = 0; i < length; ++i) {
            int[] shortcutKey = shortcutKeyList[i];
            if (shortcutKey[0] == 2) {
                shortcutKey[1] = (shortcutKey[0] = 0);
                changed = true;
            }
        }
        if (changed) {
            this.dao.updateShortcutKey(_player.getUserID(), _player.getShortcutKeyList());
        }
    }

    public void setKeyOfWalking(final HeroPlayer _player, final byte[] _shortcutKeys) {
        int[][] shortcutKeyList = _player.getShortcutKeyList();
        int scan = 0;
        for (int i = 0; i < shortcutKeyList.length; ++i) {
            if (shortcutKeyList[i][0] == 1 && (shortcutKeyList[i][1] == 1 || shortcutKeyList[i][1] == 2 || shortcutKeyList[i][1] == 3 || shortcutKeyList[i][1] == 4)) {
                shortcutKeyList[i][0] = 0;
                shortcutKeyList[i][1] = 0;
                shortcutKeyList[i + 13][0] = 0;
                shortcutKeyList[i + 13][1] = 0;
                if (++scan == 4) {
                    break;
                }
            }
        }
        for (int i = 1; i <= 4; ++i) {
            shortcutKeyList[_shortcutKeys[i - 1]][0] = 1;
            shortcutKeyList[_shortcutKeys[i - 1]][1] = i;
            shortcutKeyList[_shortcutKeys[i - 1] + 13][0] = 1;
            shortcutKeyList[_shortcutKeys[i - 1] + 13][1] = i;
        }
        this.dao.updateShortcutKey(_player.getUserID(), shortcutKeyList);
    }

    public void deleteShortcutKey(final HeroPlayer _player, final byte _shortcutKeyType, final int _targetID) {
        int[][] shortcutKeyList = _player.getShortcutKeyList();
        int[][] array;
        for (int length = (array = shortcutKeyList).length, i = 0; i < length; ++i) {
            int[] shortcutKey = array[i];
            if (shortcutKey[0] == _shortcutKeyType && shortcutKey[1] == _targetID) {
                shortcutKey[1] = (shortcutKey[0] = 0);
                this.dao.updateShortcutKey(_player.getUserID(), shortcutKeyList);
            }
        }
    }

    public byte getShortKey(final int[][] _shortcutKeyList, final byte _shortcutKeyType, final int _targetID) {
        for (byte shortcutKey = 0; shortcutKey < 26; ++shortcutKey) {
            if (_shortcutKeyList[shortcutKey][0] == _shortcutKeyType && _shortcutKeyList[shortcutKey][1] == _targetID) {
                return shortcutKey;
            }
        }
        return -1;
    }

    public void refreshRoleProperty(final HeroPlayer _player) {
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new RefreshRoleProperty(_player));
    }

    public void updateAutoSellTrait(final HeroPlayer _player, final EGoodsTrait _newTrait) {
        if (_player.getAutoSellTrait() != _newTrait) {
            _player.setAutoSellTrait(_newTrait);
            this.dao.updateAutoSellTrait(_player.getUserID(), _newTrait);
        }
    }

    public int getUserIDByNameFromDB(final String _nickname) {
        return PlayerDAO.getUserIDByName(_nickname);
    }

    public void updateLeftMasterTime(final HeroPlayer player) {
        player.leftMasterTime = System.currentTimeMillis();
        this.dao.updatePlayerLeftMasterTime(player.getUserID(), player.leftMasterTime);
    }

    public HeroPlayer getOffLinePlayerInfo(final int _userID) {
        return (HeroPlayer) this.dao.loadOffLinePlayerToGmTool(_userID);
    }

    public int GMAddGoodsForPlayer(final int userID, final int goodsID, final int number) {
        int res = 2;
        try {
            HeroPlayer player = this.getPlayerByUserID(userID);
            if (player == null) {
                player = (HeroPlayer) this.dao.load(userID);
                if (player != null) {
                    player.setInventory(new Inventory(player.getUserID(), player.bagSizes));
                    GoodsDAO.loadPlayerGoods(player);
                }
            }
            if (player != null) {
                GoodsServiceImpl.getInstance().addGoods2Package(player, goodsID, number, CauseLog.GMADD);
                res = 0;
            } else {
                res = 1;
            }
        } catch (Exception e) {
            res = 2;
            PlayerServiceImpl.log.error("\u7ed9\u73a9\u5bb6\u6dfb\u52a0\u7269\u54c1 error: ", (Throwable) e);
        }
        return res;
    }

    public int GMAddPointForPlayer(final int userID, final int point) {
        int res = 2;
        try {
            HeroPlayer player = this.getPlayerByUserID(userID);
            if (player == null) {
                player = this.getOffLinePlayerInfo(userID);
                PlayerDAO.loadPlayerAccountInfo(player);
            }
            if (player != null) {
                if (point > 0) {
                    String transID = ChargeServiceImpl.getInstance().getTransIDGen();
                    boolean addres = ChargeServiceImpl.getInstance().addPoint(player, transID, point, (byte) 3, player.getLoginInfo().publisher, ServiceType.GM);
                    if (addres) {
                        res = 0;
                    }
                }
            } else {
                res = 1;
            }
        } catch (Exception e) {
            res = 2;
            PlayerServiceImpl.log.error("\u7ed9\u73a9\u5bb6\u52a0\u70b9 error: ", (Throwable) e);
        }
        return res;
    }

    public int GMModifyPlayerInfo(final int userID, final int money, final int loverValue, final int level, final int skillPoint) {
        int res = 2;
        try {
            HeroPlayer player = this.getPlayerByUserID(userID);
            if (player == null) {
                player = this.getOffLinePlayerInfo(userID);
                PlayerDAO.loadPlayerAccountInfo(player);
            }
            if (player != null) {
                PlayerServiceImpl.log.debug(("gm modify player info money=" + money + ",levle=" + level + ",skillpoint=" + skillPoint + ",lovervalue=" + loverValue));
                HeroPlayer heroPlayer = player;
                heroPlayer.surplusSkillPoint += (short) skillPoint;
                if (player.isEnable()) {
                    if (money > 0) {
                        this.addMoney(player, money, 1.0f, 0, CauseLog.GMADD.getName());
                    }
                    if (level > 0) {
                        int currLevel = player.getLevel() + level;
                        if (currLevel > ((PlayerConfig) this.config).max_level) {
                            player.addLevel(((PlayerConfig) this.config).max_level - player.getLevel());
                        } else {
                            player.addLevel(level);
                        }
                        int exp = CEService.expToNextLevel(player.getLevel(), (float) player.getUpgradeNeedExp());
                        player.setUpgradeNeedExp(exp);
                        this.reCalculateRoleProperty(player);
                        player.setHp(player.getBaseProperty().getHpMax());
                        int maxMp = player.getActualProperty().getMpMax();
                        player.setMp(maxMp);
                        this.refreshRoleProperty(player);
                        RoleUpgradeNotify msg = new RoleUpgradeNotify(player.getID(), player.getLevel(), player.getActualProperty().getHpMax(), maxMp, player.surplusSkillPoint);
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                    }
                    if (player.spouse != null && player.spouse.trim().length() > 0) {
                        player.addLoverValue(loverValue);
                    }
                    player.needUpdateDB = true;
                } else {
                    String spouse = LoverServiceImpl.getInstance().whoLoveMe(player.getName());
                    if (spouse == null) {
                        spouse = LoverServiceImpl.getInstance().whoMarriedMe(player.getName());
                    }
                    if (spouse != null && spouse.trim().length() > 0) {
                        player.addLoverValue(loverValue);
                    }
                }
                this.dao.updateDB(player);
                res = 0;
            } else {
                res = 1;
            }
        } catch (Exception e) {
            res = 2;
            PlayerServiceImpl.log.error("GM\u4fee\u6539\u73a9\u5bb6\u7684\u7b49\u7ea7\u3001\u91d1\u94b1\u3001\u7b49\u7ea7 error: ", (Throwable) e);
        }
        PlayerServiceImpl.log.debug("gm modify player info end ...");
        return res;
    }

    public HeroPlayer getOffLinePlayerInfoByName(final String name) {
        return this.dao.getOffLinePlayerByName(name);
    }

    public boolean playerChatIsBlank(final int accountID, final int _userID) {
        return this.dao.getChatBlankByUserID(accountID, _userID);
    }

    public boolean setPlayerUserIDBlack(final int _userID, final String nickname, final int keepTime, final String startTime, final String endTime, final String memo) {
        return this.dao.setPlayerUserIDBlank(_userID, nickname, keepTime, startTime, endTime, memo);
    }

    public boolean setPlayerAccountIDBlack(final int _accountID, final String username, final int keepTime, final String startTime, final String endTime, final String memo) {
        return this.dao.setPlayerAccountIDBlank(_accountID, username, keepTime, startTime, endTime, memo);
    }

    public boolean setPlayerChatBlack(final int _userID, final String nickname, final int keepTime, final String startTime, final String endTime, final String memo) {
        return this.dao.setPlayerChatBlank(_userID, nickname, keepTime, startTime, endTime, memo);
    }

    public boolean deletePlayerUserIDBlack(final int _userID) {
        return this.dao.deletePlayerUserIDBlack(_userID);
    }

    public boolean deletePlayerAccountIDBlack(final int _accountID) {
        return this.dao.deletePlayerAccountIDBlack(_accountID);
    }

    public boolean deletePlayerChatBlack(final int _userID) {
        return this.dao.deletePlayerChatBlack(_userID);
    }

    public void startInlayHeavenBook(final HeroPlayer player, final byte position, final int bookID) throws BagException {
        SpecialGoods goods = (SpecialGoods) GoodsServiceImpl.getInstance().getGoodsByID(bookID);
        if (goods.getType() == ESpecialGoodsType.HEAVEN_BOOK) {
            HeavenBook book = (HeavenBook) goods;
            book.setPosition(position);
            if (book.beUse(player, null, -1) && book.disappearImmediatelyAfterUse()) {
                goods.remove(player, (short) (-1));
            }
        } else {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u9009\u62e9\u7684\u4e0d\u662f\u5929\u4e66"));
        }
        player.currInlayHeavenBookPosition = -1;
        player.currInlayHeavenBookID = 0;
    }

    public boolean inlayHeavenBook(final HeroPlayer player, final byte position, final int bookID) {
        short currentPoint = player.surplusSkillPoint;
        HeavenBook book = (HeavenBook) GoodsServiceImpl.getInstance().getGoodsByID(bookID);
        try {
            if (player.heaven_book_ids[position] > 0) {
                HeavenBook oldBook = (HeavenBook) GoodsServiceImpl.getInstance().getGoodsByID(player.heaven_book_ids[position]);
                if (oldBook.getTrait().value() > book.getTrait().value()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4e0d\u80fd\u7528\u4f4e\u54c1\u8d28\u7684\u8986\u76d6\u9ad8\u54c1\u8d28\u7684\u5929\u4e66!"));
                    return false;
                }
                player.surplusSkillPoint -= oldBook.getSkillPoint();
                if (player.heavenBookSame) {
                    player.surplusSkillPoint -= (short) oldBook.getComBonus();
                    player.heavenBookSame = false;
                }
                player.surplusSkillPoint += book.getSkillPoint();
                player.heaven_book_ids[position] = bookID;
                if (player.heaven_book_ids[0] > 0 && player.heaven_book_ids[0] == player.heaven_book_ids[1] && player.heaven_book_ids[0] == player.heaven_book_ids[2]) {
                    player.heavenBookSame = true;
                    player.surplusSkillPoint += (short) book.getComBonus();
                }
            } else {
                player.surplusSkillPoint += book.getSkillPoint();
                player.heaven_book_ids[position] = bookID;
                if (player.heaven_book_ids[0] > 0 && player.heaven_book_ids[0] == player.heaven_book_ids[1] && player.heaven_book_ids[0] == player.heaven_book_ids[2]) {
                    player.heavenBookSame = true;
                    player.surplusSkillPoint += (short) book.getComBonus();
                }
            }
            PlayerDAO.updatePlayerHeavenBookID(player);
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u9576\u5d4c\u5929\u4e66\u6210\u529f"));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new RefreshRoleProperty(player));
            return true;
        } catch (Exception e) {
            player.surplusSkillPoint = currentPoint;
            PlayerServiceImpl.log.error("inlayHeavenBook error : ", (Throwable) e);
            return false;
        }
    }

    public void updatePlayerLoverValue(final int userID, final int loverValue) {
        this.dao.updatePlayerLoverValue(userID, loverValue);
    }

    public int getPlayerHeavenBookSkillPoint(final HeroPlayer player) {
        int point = 0;
        HeavenBook book = null;
        for (int i = 0; i < player.heaven_book_ids.length; ++i) {
            if (player.heaven_book_ids[i] > 0) {
                book = (HeavenBook) GoodsServiceImpl.getInstance().getGoodsByID(player.heaven_book_ids[i]);
                if (book != null) {
                    point += book.getSkillPoint();
                }
            }
        }
        if (player.heavenBookSame && book != null) {
            point += book.getComBonus();
        }
        return point;
    }

    static /* synthetic */ void access$6(final PlayerServiceImpl playerServiceImpl, final Map loverValueOrderMap) {
        playerServiceImpl.loverValueOrderMap = (Map<Integer, Integer>) loverValueOrderMap;
    }

    private class PlayerInfoUpdateTask extends TimerTask {

        @Override
        public void run() {
            PlayerDAO.updatePlayerInfo(PlayerServiceImpl.this.playerList);
        }
    }

    private class ClearPlayerReceiveRepeateTaskTimes extends TimerTask {

        @Override
        public void run() {
            PlayerServiceImpl.log.info("\u5237\u65b0\u73a9\u5bb6\u63a5\u6536\u5faa\u73af\u4efb\u52a1\u6b21\u6570 start ...");
            PlayerDAO.clearPlayerReceiveRepeatTaskTimes();
            FastList<HeroPlayer> playerList = PlayerServiceImpl.this.getPlayerList();
            for (final HeroPlayer player : playerList) {
                player.receivedRepeateTaskTimes = 0;
                player.setCanReceiveRepeateTaskTimes(5);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new NotifyPlayerReciveRepeateTaskTimes(player));
            }
            PlayerServiceImpl.log.info("\u5237\u65b0\u73a9\u5bb6\u63a5\u6536\u5faa\u73af\u4efb\u52a1\u6b21\u6570 end ...");
        }
    }

    private class PlayerClearTask extends TimerTask {

        @Override
        public void run() {
            LogServiceImpl.getInstance().onlineNumLog(PlayerServiceImpl.this.playerList.size());
            if (PlayerServiceImpl.this.playerList.size() > 0) {
                long now = System.currentTimeMillis();
                int i = 0;
                while (i < PlayerServiceImpl.this.playerList.size()) {
                    try {
                        HeroPlayer player = (HeroPlayer) PlayerServiceImpl.this.playerList.get(i);
                        if (!player.isEnable() && player.offlineTime > 0L && now - player.offlineTime > 60000L) {
                            PlayerServiceImpl.log.info(("\u6e05\u9664\u73a9\u5bb6\u5185\u5b58:" + player.getName()));
                            List<Pet> petlist = PetServiceImpl.getInstance().getPetList(player.getUserID());
                            if (petlist != null) {
                                for (final Pet pet : petlist) {
                                    PlayerServiceImpl.log.debug(("\u6e05\u9664\u73a9\u5bb6\u5185\u5b58\u65f6\u4fdd\u5b58\u5ba0\u7269\u4fe1\u606f\uff0cpetID= " + pet.id));
                                    PetServiceImpl.getInstance().updatePet(player.getUserID(), pet);
                                }
                            }
                            ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(player.getUserID());
                            ShareServiceImpl.getInstance().removePlayerFromRequestExchangeListByTarget(player.getUserID());
                            ServiceManager.getInstance().clean(player.getUserID());
                            PlayerServiceImpl.this.playerList.remove(i);
                            player.free();
                            player = null;
                        } else {
                            ++i;
                        }
                    } catch (Exception e) {
                        PlayerServiceImpl.log.error("\u6e05\u9664\u6389\u7ebf\u73a9\u5bb6\u53d1\u751f\u5f02\u5e38:", (Throwable) e);
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
    }

    private class PlayerLoverValueOrderTimer extends TimerTask {

        @Override
        public void run() {
            PlayerServiceImpl.access$6(PlayerServiceImpl.this, PlayerServiceImpl.this.dao.loverValueOrderMap());
        }
    }
}
