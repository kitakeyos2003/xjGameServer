// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.service;

import hero.task.service.TaskServiceImpl;
import yoyo.service.base.AbsConfig;
import hero.npc.message.NpcWalkNotify;
import hero.npc.message.MonsterWalkNotify;
import hero.share.EObjectType;
import hero.npc.ME2NotPlayer;
import java.util.Enumeration;
import java.util.Calendar;
import hero.item.legacy.PersonalPickerBox;
import hero.log.service.LogServiceImpl;
import hero.item.message.LegacyBoxEmergeNotify;
import hero.item.legacy.MonsterLegacyBox;
import hero.item.legacy.MonsterLegacyManager;
import hero.item.legacy.RaidPickerBox;
import hero.micro.teach.TeachService;
import hero.player.service.PlayerServiceImpl;
import hero.group.Group;
import hero.group.service.GroupServiceImpl;
import hero.item.legacy.TaskGoodsLegacyInfo;
import hero.npc.message.GroundTaskGoodsDisappearNofity;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.function.FunctionBuilder;
import hero.share.ESystemFeature;
import hero.share.EVocation;
import hero.player.define.EClan;
import hero.npc.ai.data.Changes;
import hero.share.service.LogWriter;
import hero.share.EMagic;
import hero.expressions.service.CEService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.Goods;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.manufacture.ManufactureType;
import hero.manufacture.Manufacture;
import hero.manufacture.service.ManufactureServerImpl;
import hero.player.HeroPlayer;
import hero.npc.others.DoorPlate;
import hero.npc.others.RoadInstructPlate;
import hero.npc.dict.AnswerQuestionData;
import hero.share.service.DateTool;
import java.util.Date;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.system.postbox.MailService;
import yoyo.service.base.session.Session;
import java.util.TimerTask;
import hero.npc.function.system.auction.AuctionDict;
import hero.npc.dict.EvidenveGiftDict;
import hero.npc.dict.QuestionDict;
import hero.npc.function.system.exchange.TraderExchangeContentDict;
import hero.npc.function.system.trade.TraderSellContentDict;
import hero.npc.dict.DungeonManagerDict;
import hero.npc.ai.data.AIDataDict;
import hero.npc.dict.GroundTaskGoodsDataDict;
import hero.npc.dict.BoxDataDict;
import hero.npc.dict.DoorPlateDataDict;
import hero.npc.dict.RoadPlateDataDict;
import hero.npc.dict.GearDataDict;
import hero.npc.dict.AnimalImageDict;
import hero.npc.dict.AnimalDataDict;
import hero.npc.dict.NpcFunIconDict;
import hero.npc.dict.NpcImageDict;
import hero.npc.dict.NpcImageConfDict;
import hero.npc.dict.NpcDataDict;
import hero.npc.dict.NpcHelloContentDict;
import hero.npc.dict.MonsterDataDict;
import hero.npc.dict.MonsterImageDict;
import hero.npc.dict.MonsterImageConfDict;
import java.util.Timer;
import hero.npc.others.GroundTaskGoods;
import hero.npc.others.Box;
import hero.npc.others.TaskGear;
import hero.npc.others.Animal;
import javolution.util.FastList;
import hero.npc.Npc;
import hero.npc.Monster;
import javolution.util.FastMap;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class NotPlayerServiceImpl extends AbsServiceAdaptor<NpcConfig> implements INotPlayerService {

    private static Logger log;
    private static final int REFRESH_NEXT_ANSWER_QUESTION_INTERVAL = 1000;
    private static final Random random;
    private static NotPlayerServiceImpl instance;
    private Hashtable<Integer, ArrayList<Integer>> answerQuestionList;
    private Hashtable<Integer, Long> answerQuestionDayRefreshList;
    private Hashtable<Integer, Boolean> answerQuestionTimeRefreshList;
    private FastMap<Integer, Monster> monsterMapOfIDTable;
    private FastMap<Integer, Npc> npcMapOfIDTable;
    private FastMap<String, Npc> npcMapOfModelIDTable;
    private FastList<Animal> animalList;
    private FastMap<Integer, TaskGear> taskGearTable;
    private FastMap<Integer, Box> boxTable;
    private FastList<Box> emptyBoxList;
    private FastMap<Integer, GroundTaskGoods> groundTaskGoodsTable;
    private FastList<GroundTaskGoods> emptyGroundTaskGoodsList;
    public static final String NPC_MODEL_ID_PREFIX = "n";
    public static final String MONSTER_MODEL_ID_PREFIX = "m";
    public static final String ANIMAL_MODEL_ID_PREFIX = "a";
    public static final String BOX_MODEL_ID_PREFIX = "b";
    public static final String ROAD_PLATE_MODEL_ID_PREFIX = "r";
    public static final String DOOR_PLATE_MODEL_ID_PREFIX = "d";
    public static final String GEAR_MODEL_ID_PREFIX = "g";
    public static final String GROUND_TASK_GOODS_MODEL_ID_PREFIX = "t";
    private Timer timer;

    static {
        NotPlayerServiceImpl.log = Logger.getLogger((Class) NotPlayerServiceImpl.class);
        random = new Random();
    }

    private NotPlayerServiceImpl() {
        this.config = new NpcConfig();
        this.monsterMapOfIDTable = (FastMap<Integer, Monster>) new FastMap();
        this.npcMapOfIDTable = (FastMap<Integer, Npc>) new FastMap();
        this.npcMapOfModelIDTable = (FastMap<String, Npc>) new FastMap();
        this.animalList = (FastList<Animal>) new FastList();
        this.taskGearTable = (FastMap<Integer, TaskGear>) new FastMap();
        this.boxTable = (FastMap<Integer, Box>) new FastMap();
        this.emptyBoxList = (FastList<Box>) new FastList();
        this.groundTaskGoodsTable = (FastMap<Integer, GroundTaskGoods>) new FastMap();
        this.emptyGroundTaskGoodsList = (FastList<GroundTaskGoods>) new FastList();
        this.answerQuestionList = new Hashtable<Integer, ArrayList<Integer>>();
        this.answerQuestionDayRefreshList = new Hashtable<Integer, Long>();
        this.answerQuestionTimeRefreshList = new Hashtable<Integer, Boolean>();
    }

    public static NotPlayerServiceImpl getInstance() {
        if (NotPlayerServiceImpl.instance == null) {
            NotPlayerServiceImpl.instance = new NotPlayerServiceImpl();
        }
        return NotPlayerServiceImpl.instance;
    }

    @Override
    protected void start() {
        MonsterImageConfDict.init();
        MonsterImageDict.getInstance().load(((NpcConfig) this.config).MonsterImageHPath, ((NpcConfig) this.config).MonsterImageLPath);
        MonsterDataDict.getInstance().load(((NpcConfig) this.config).MONSTER_DATA_PATH);
        NpcHelloContentDict.getInstance().load(((NpcConfig) this.config).NPC_HELLO_PATH);
        NpcDataDict.getInstance().load(((NpcConfig) this.config).NPC_DATA_PATH);
        NpcImageConfDict.init();
        NpcImageDict.getInstance();
        NpcFunIconDict.getInstance().load(((NpcConfig) this.config).npc_fun_icon_path);
        AnimalDataDict.getInstance().load(((NpcConfig) this.config).ANIMAL_DATA_PATH);
        AnimalImageDict.getInstance().load(((NpcConfig) this.config).ANIMAL_IMAGE_PATH);
        GearDataDict.getInstance().load(((NpcConfig) this.config).GEAR_DATA_PATH);
        RoadPlateDataDict.getInstance().load(((NpcConfig) this.config).ROAD_PLATE_DATA_PATH);
        DoorPlateDataDict.getInstance().load(((NpcConfig) this.config).door_plate_data_path);
        BoxDataDict.getInstance().load(((NpcConfig) this.config).BOX_DATA_PATH);
        GroundTaskGoodsDataDict.getInstance().load(((NpcConfig) this.config).TASK_GOODS_ON_MAP_DATA_PATH);
        AIDataDict.getInstance().load(((NpcConfig) this.config).MONSTER_SHOUT_DATA_PATH, ((NpcConfig) this.config).MONSTER_CALL_DATA_PATH, ((NpcConfig) this.config).MONSTER_CHANGES_DATA_PATH, ((NpcConfig) this.config).MONSTER_DISAPPEAR_DATA_PATH, ((NpcConfig) this.config).MONSTER_RUN_AWAY_DATA_PATH, ((NpcConfig) this.config).MONSTER_SPECIAL_AI_DATA_PATH, ((NpcConfig) this.config).MONSTER_SKILL_AI_DATA_PATH, ((NpcConfig) this.config).MONSTER_AI_DATA_PATH);
        DungeonManagerDict.getInstance().load(((NpcConfig) this.config).dungeonManagerDataPath);
        TraderSellContentDict.getInstance().load(((NpcConfig) this.config).trader_sell_content_data_path);
        TraderExchangeContentDict.getInstance().load(((NpcConfig) this.config).trader_exchange_content_data_path);
        QuestionDict.getInstance().load(((NpcConfig) this.config).npc_function_data_anwser_question, ((NpcConfig) this.config).npc_function_data_question, ((NpcConfig) this.config).npc_function_data_award);
        EvidenveGiftDict.getInstance().load(((NpcConfig) this.config).npc_function_data_evidenve_gift, ((NpcConfig) this.config).npc_function_data_evidenve_award);
        AuctionDict.getInstance();
        new Thread(new EmptyBoxThread()).start();
        new Thread(new BePickedGroundTaskGoodsThread()).start();
        (this.timer = new Timer()).schedule(new RefreshQuestionManager(), 1000L, 1000L);
        int[] groups = QuestionDict.getInstance().getAnwserQuestionIDs();
        for (int i = 0; i < groups.length; ++i) {
            this.answerQuestionList.put(groups[i], new ArrayList<Integer>());
            this.answerQuestionDayRefreshList.put(groups[i], System.currentTimeMillis());
            this.answerQuestionTimeRefreshList.put(groups[i], false);
        }
    }

    @Override
    public void createSession(final Session _session) {
        MailService.getInstance().loadMail(_session.userID);
    }

    @Override
    public void clean(final int _userID) {
        MailService.getInstance().clear(_userID);
    }

    public Npc getNpc(final int _npcID) {
        return (Npc) this.npcMapOfIDTable.get(_npcID);
    }

    public Npc getNpc(final String _npcModelID) {
        return (Npc) this.npcMapOfModelIDTable.get(_npcModelID.toLowerCase());
    }

    public Npc getNpcByFunction(final int _function) {
        Npc npc = null;
        Npc npcTemp = null;
        for (int i = 0; i < this.npcMapOfModelIDTable.size(); ++i) {
            npcTemp = (Npc) this.npcMapOfModelIDTable.get(i);
            BaseNpcFunction function = npc.getFunction(_function);
            if (function != null) {
                npc = npcTemp;
            }
        }
        return npc;
    }

    public TaskGear getTaskGear(final int _taskGearID) {
        return (TaskGear) this.taskGearTable.get(_taskGearID);
    }

    public void joinQuestion(final int _group, final int _userID) {
        if (this.answerQuestionList.get(_group) != null) {
            synchronized (this.answerQuestionList) {
                this.answerQuestionList.get(_group).add(_userID);
            }
            // monitorexit(this.answerQuestionList)
        }
    }

    public boolean isInTime(final int _group) {
        boolean startResult = false;
        boolean endResult = false;
        AnswerQuestionData data = QuestionDict.getInstance().getAnswerQuestionData(_group);
        if (data.refreshType == 1) {
            return true;
        }
        Date nowDate = new Date(System.currentTimeMillis());
        String endTime;
        String startTime = endTime = String.valueOf(DateTool.formatDate(nowDate)) + " ";
        for (int i = 0; i < data.refreshTimeSum; ++i) {
            startTime = String.valueOf(startTime) + data.startTime[i];
            endTime = String.valueOf(endTime) + data.endTime[i];
            Date startDate = DateTool.convertLongDate(startTime);
            if (System.currentTimeMillis() > startDate.getTime()) {
                startResult = true;
            }
            Date endDate = DateTool.convertLongDate(endTime);
            if (System.currentTimeMillis() < endDate.getTime()) {
                endResult = true;
            }
            if (startResult && endResult) {
                break;
            }
            startResult = false;
            endResult = false;
        }
        return startResult && endResult;
    }

    public boolean isJoinQuestion(final int _group, final int _userID) {
        boolean result = false;
        synchronized (this.answerQuestionList) {
            if (this.answerQuestionList.get(_group) != null) {
                result = this.answerQuestionList.get(_group).contains(_userID);
            }
        }
        // monitorexit(this.answerQuestionList)
        return result;
    }

    public GroundTaskGoods getGroundTaskGoodsTable(final int _groundTaskGoodsID) {
        return (GroundTaskGoods) this.groundTaskGoodsTable.get(_groundTaskGoodsID);
    }

    public NpcDataDict.NpcData getNpcModelData(final String _npcModelID) {
        return NpcDataDict.getInstance().getNpcData(_npcModelID);
    }

    public String getNotPlayerNameByModelID(final String _modelID) {
        if (_modelID.toLowerCase().startsWith("n")) {
            NpcDataDict.NpcData npc = NpcDataDict.getInstance().getNpcData(_modelID);
            if (npc != null) {
                return npc.name;
            }
        } else if (_modelID.toLowerCase().startsWith("m")) {
            MonsterDataDict.MonsterData monster = MonsterDataDict.getInstance().getMonsterData(_modelID);
            if (monster != null) {
                return monster.name;
            }
        }
        return null;
    }

    public Animal buildAnimalInstance(final String _animalModelID) {
        AnimalDataDict.AnimalData data = AnimalDataDict.getInstance().getAnimalData(_animalModelID);
        if (data != null) {
            Animal animal = new Animal(data);
            this.animalList.add(animal);
            return animal;
        }
        return null;
    }

    public Box buildBoxInstance(final String _boxModelID) {
        BoxDataDict.BoxData data = BoxDataDict.getInstance().getBoxData(_boxModelID);
        if (data != null) {
            Box box = new Box(data);
            this.boxTable.put(box.getID(), box);
            return box;
        }
        return null;
    }

    public TaskGear buildGearInstance(final String _gearModelID) {
        GearDataDict.GearData data = GearDataDict.getInstance().getGearData(_gearModelID);
        if (data != null) {
            TaskGear gear = new TaskGear(data);
            this.taskGearTable.put(gear.getID(), gear);
            return gear;
        }
        return null;
    }

    public GroundTaskGoods buildGroundTaskGood(final String _taskGoodsModelID) {
        GroundTaskGoodsDataDict.GroundTaskGoodsData data = GroundTaskGoodsDataDict.getInstance().getTaskGoodsData(_taskGoodsModelID);
        if (data != null) {
            GroundTaskGoods taskGoods = new GroundTaskGoods(data);
            this.groundTaskGoodsTable.put(taskGoods.getID(), taskGoods);
            return taskGoods;
        }
        return null;
    }

    public RoadInstructPlate buildRoadPlate(final String _roadPlateModelID) {
        RoadPlateDataDict.RoadPlateData data = RoadPlateDataDict.getInstance().getRoadPlateData(_roadPlateModelID);
        if (data != null) {
            return new RoadInstructPlate(data);
        }
        return null;
    }

    public DoorPlate buildDoorPlate(final String _doorPlateModelID) {
        DoorPlateDataDict.DoorPlateData data = DoorPlateDataDict.getInstance().getDoorPlateData(_doorPlateModelID);
        if (data != null) {
            return new DoorPlate(data);
        }
        return null;
    }

    public void pickBox(final HeroPlayer _player, final int _boxID) {
        Box box = (Box) this.boxTable.get(_boxID);
        if (box != null) {
            synchronized (box) {
                NotPlayerServiceImpl.log.debug(("box is need gather skill = " + box.isNeedGatherSkill()));
                if (box.isNeedGatherSkill()) {
                    List<Manufacture> manufactureList = ManufactureServerImpl.getInstance().getManufactureListByUserID(_player.getUserID());
                    if (manufactureList != null) {
                        NotPlayerServiceImpl.log.debug(("manuflist size = " + manufactureList.size()));
                        boolean noStudyedGather = true;
                        for (final Manufacture manuf : manufactureList) {
                            NotPlayerServiceImpl.log.debug(("manuf type = " + manuf.getManufactureType()));
                            if (manuf.getManufactureType() == ManufactureType.GRATHER) {
                                noStudyedGather = false;
                            }
                        }
                        if (noStudyedGather) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u5b66\u4e60\u8fc7\u91c7\u96c6\u6280\u80fd\u4e0d\u80fd\u62fe\u53d6"));
                            // monitorexit(box)
                            return;
                        }
                    }
                }
                HashMap<Goods, Integer> goodsListOfBox = box.getActualGoodsTable();
                Iterator<Goods> goodsContent = goodsListOfBox.keySet().iterator();
                while (goodsContent.hasNext()) {
                    Goods goods = goodsContent.next();
                    if (GoodsServiceImpl.getInstance().addGoods2Package(_player, goods, goodsListOfBox.get(goods), CauseLog.DROP) == null) {
                        break;
                    }
                    goodsContent.remove();
                }
                if (goodsListOfBox.size() == 0) {
                    box.disappear();
                    box.where().getBoxList().remove(box);
                    this.boxTable.remove(box.getID());
                    this.emptyBoxList.add(box);
                }
            }
            // monitorexit(box)
        }
    }

    public Monster buildMonsterInstance(final String _monsterModelID) {
        NotPlayerServiceImpl.log.debug(("\u5f00\u59cb\u521b\u5efa\u602a\u7269\u5b9e\u4f8b... modelID  = " + _monsterModelID));
        Monster monster = null;
        try {
            MonsterDataDict.MonsterData data = MonsterDataDict.getInstance().getMonsterData(_monsterModelID.toLowerCase());
            NotPlayerServiceImpl.log.debug(("monster data = " + data));
            if (data != null) {
                try {
                    monster = new Monster(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (data.isActive.equals("\u4e3b\u52a8")) {
                    monster.setActiveAttackType(true);
                } else {
                    monster.setActiveAttackType(false);
                }
                if (data.isInDungeon != null && data.isInDungeon.equals("\u662f")) {
                    monster.setInDungeon();
                }
                monster.setAttackRange(Short.parseShort(data.atkRange));
                monster.setActualAttackImmobilityTime((int) (1000.0f * Float.parseFloat(data.immobilityTime)));
                if (data.assistAttackRange != null) {
                    monster.setAttackChain(Integer.parseInt(data.assistAttackRange), Integer.parseInt(data.assistPara));
                }
                int defense = 0;
                if (data.strength != null) {
                    monster.getActualProperty().setStrength(Integer.parseInt(data.strength));
                }
                if (data.agility != null) {
                    monster.getActualProperty().setAgility(Integer.parseInt(data.agility));
                }
                if (data.stamina != null) {
                    monster.getActualProperty().setStamina(Integer.parseInt(data.stamina));
                }
                if (data.inte != null) {
                    monster.getActualProperty().setInte(Integer.parseInt(data.inte));
                }
                if (data.spirit != null) {
                    monster.getActualProperty().setSpirit(Integer.parseInt(data.spirit));
                }
                if (data.lucky != null) {
                    monster.getActualProperty().setLucky(Integer.parseInt(data.lucky));
                }
                if (data.defense != null) {
                    defense = Integer.parseInt(data.defense);
                }
                int hp = CEService.hpByStamina(monster.getActualProperty().getStamina(), monster.getLevel(), monster.getObjectLevel().getHpCalPara());
                monster.getActualProperty().setHpMax(hp);
                monster.setHp(hp);
                int mp = CEService.mpByInte(monster.getActualProperty().getInte(), monster.getLevel(), monster.getObjectLevel().getMpCalPara());
                monster.getActualProperty().setMpMax(mp);
                monster.setMp(mp);
                monster.getActualProperty().setDefense(CEService.defenseBySpirit(monster.getActualProperty().getSpirit(), monster.getVocation().getPhysicsDefenceSpiritPara()) + defense);
                int maxPhysicsAttack = 0;
                int minPhysicsAttack = 0;
                if (data.minPhysicsAttack != null) {
                    minPhysicsAttack = Integer.parseInt(data.minPhysicsAttack);
                    monster.getBaseProperty().setMinPhysicsAttack(minPhysicsAttack);
                }
                if (data.maxPhysicsAttack != null) {
                    maxPhysicsAttack = Integer.parseInt(data.maxPhysicsAttack);
                    monster.getBaseProperty().setMaxPhysicsAttack(maxPhysicsAttack);
                }
                monster.getActualProperty().setMinPhysicsAttack(CEService.minPhysicsAttack(monster.getActualProperty().getStrength(), monster.getActualProperty().getAgility(), monster.getVocation().getPhysicsAttackParaA(), monster.getVocation().getPhysicsAttackParaB(), monster.getVocation().getPhysicsAttackParaC(), minPhysicsAttack, 0, monster.getActualAttackImmobilityTime() / 1000.0f, monster.getObjectLevel().getPhysicsAttckCalPara()));
                monster.getActualProperty().setMaxPhysicsAttack(CEService.maxPhysicsAttack(monster.getActualProperty().getStrength(), monster.getActualProperty().getAgility(), monster.getVocation().getPhysicsAttackParaA(), monster.getVocation().getPhysicsAttackParaB(), monster.getVocation().getPhysicsAttackParaC(), maxPhysicsAttack, 0, monster.getActualAttackImmobilityTime() / 1000.0f, monster.getObjectLevel().getPhysicsAttckCalPara()));
                int physicsDeathblowLevel = 0;
                int magicDeathblowLevel = 0;
                int hitLevel = 0;
                int duckLevel = 0;
                physicsDeathblowLevel = CEService.physicsDeathblowLevel(monster.getActualProperty().getAgility(), monster.getActualProperty().getLucky());
                magicDeathblowLevel = CEService.magicDeathblowLevel(monster.getActualProperty().getInte(), monster.getActualProperty().getLucky());
                hitLevel = CEService.hitLevel(monster.getActualProperty().getLucky());
                duckLevel = CEService.duckLevel(monster.getActualProperty().getInte(), monster.getActualProperty().getLucky());
                monster.getActualProperty().setPhysicsDeathblowLevel((short) physicsDeathblowLevel);
                monster.getActualProperty().setMagicDeathblowLevel((short) magicDeathblowLevel);
                monster.getActualProperty().setHitLevel((short) hitLevel);
                monster.getActualProperty().setPhysicsDuckLevel((short) duckLevel);
                if (data.sanctity != null) {
                    monster.getActualProperty().getMagicFastnessList().add(EMagic.SANCTITY, Integer.parseInt(data.sanctity));
                }
                if (data.umbra != null) {
                    monster.getActualProperty().getMagicFastnessList().add(EMagic.UMBRA, Integer.parseInt(data.umbra));
                }
                if (data.fire != null) {
                    monster.getActualProperty().getMagicFastnessList().add(EMagic.FIRE, Integer.parseInt(data.fire));
                }
                if (data.water != null) {
                    monster.getActualProperty().getMagicFastnessList().add(EMagic.WATER, Integer.parseInt(data.water));
                }
                if (data.soil != null) {
                    monster.getActualProperty().getMagicFastnessList().add(EMagic.SOIL, Integer.parseInt(data.soil));
                }
                int baseMagicHarmByInte = CEService.magicHarmByInte(monster.getActualProperty().getInte());
                monster.getActualProperty().getBaseMagicHarmList().add(EMagic.SANCTITY, (float) baseMagicHarmByInte);
                monster.getActualProperty().getBaseMagicHarmList().add(EMagic.UMBRA, (float) baseMagicHarmByInte);
                monster.getActualProperty().getBaseMagicHarmList().add(EMagic.FIRE, (float) baseMagicHarmByInte);
                monster.getActualProperty().getBaseMagicHarmList().add(EMagic.WATER, (float) baseMagicHarmByInte);
                monster.getActualProperty().getBaseMagicHarmList().add(EMagic.SOIL, (float) baseMagicHarmByInte);
                if (data.magicType != null) {
                    int minDamageValue = Integer.parseInt(data.minDamageValue);
                    int maxDamageValue = Integer.parseInt(data.maxDamageValue);
                    int magicHarmValue = CEService.magicHarm(monster.getActualProperty().getInte(), (minDamageValue + maxDamageValue) / 2);
                    monster.getActualProperty().getBaseMagicHarmList().add(EMagic.getMagic(data.magicType), (float) (magicHarmValue - baseMagicHarmByInte));
                }
                if (data.money != null) {
                    monster.setMoney(Integer.parseInt(data.money));
                }
                monster.setSoulIDList(data.soulIDList);
                if (data.aiID != null) {
                    monster.setAttackAiID(Integer.parseInt(data.aiID));
                }
                if (data.legacyTypeNums != null) {
                    monster.setLegacyListInfo(Byte.parseByte(data.legacyTypeMostNums), Byte.parseByte(data.legacyTypeSmallestNums));
                    if (data.item1 != null) {
                        monster.addLegacy(Integer.parseInt(data.item1), Float.valueOf(data.item1Odds), Integer.parseInt(data.item1nums));
                    }
                    if (data.item2 != null) {
                        monster.addLegacy(Integer.parseInt(data.item2), Float.valueOf(data.item2Odds), Integer.parseInt(data.item2nums));
                    }
                    if (data.item3 != null) {
                        monster.addLegacy(Integer.parseInt(data.item3), Float.valueOf(data.item3Odds), Integer.parseInt(data.item3nums));
                    }
                    if (data.item4 != null) {
                        monster.addLegacy(Integer.parseInt(data.item4), Float.valueOf(data.item4Odds), Integer.parseInt(data.item4nums));
                    }
                    if (data.item5 != null) {
                        monster.addLegacy(Integer.parseInt(data.item5), Float.valueOf(data.item5Odds), Integer.parseInt(data.item5nums));
                    }
                    if (data.item6 != null) {
                        monster.addLegacy(Integer.parseInt(data.item6), Float.valueOf(data.item6Odds), Integer.parseInt(data.item6nums));
                    }
                    if (data.item7 != null) {
                        monster.addLegacy(Integer.parseInt(data.item7), Float.valueOf(data.item7Odds), Integer.parseInt(data.item7nums));
                    }
                    if (data.item8 != null) {
                        monster.addLegacy(Integer.parseInt(data.item8), Float.valueOf(data.item8Odds), Integer.parseInt(data.item8nums));
                    }
                    if (data.item9 != null) {
                        monster.addLegacy(Integer.parseInt(data.item9), Float.valueOf(data.item9Odds), Integer.parseInt(data.item9nums));
                    }
                    if (data.item10 != null) {
                        monster.addLegacy(Integer.parseInt(data.item10), Float.valueOf(data.item10Odds), Integer.parseInt(data.item10nums));
                    }
                    if (data.item11 != null) {
                        monster.addLegacy(Integer.parseInt(data.item11), Float.valueOf(data.item11Odds), Integer.parseInt(data.item11nums));
                    }
                    if (data.item12 != null) {
                        monster.addLegacy(Integer.parseInt(data.item12), Float.valueOf(data.item12Odds), Integer.parseInt(data.item12nums));
                    }
                }
                monster.setBaseExp(CEService.monsterBaseExperience(monster.getLevel(), monster.getObjectLevel()));
                if (data.retime != null) {
                    monster.setRetime(Integer.parseInt(data.retime));
                } else {
                    monster.setRetime(5);
                }
                if (data.imageID != null) {
                    monster.setImageID(Short.parseShort(data.imageID));
                }
                if (data.animationID != null) {
                    monster.setAnimationID(Short.parseShort(data.animationID));
                }
                this.monsterMapOfIDTable.put(monster.getID(), monster);
                NotPlayerServiceImpl.log.debug(("\u521b\u5efa\u602a\u7269\u5b9e\u7269\u5b9e\u4f8b\u6210\u529f... id = " + monster.getID()));
            } else {
                LogWriter.println("\u65e0\u6cd5\u521b\u5efa\u602a\u7269\uff1a" + _monsterModelID);
            }
        } catch (Exception e2) {
            LogWriter.error(this, e2);
            NotPlayerServiceImpl.log.error("\u521b\u5efa\u602a\u7269\u5b9e\u4f8b errors : ", (Throwable) e2);
            monster = null;
        }
        return monster;
    }

    public void removeMonster(final int _monsterID) {
        this.monsterMapOfIDTable.remove(_monsterID);
    }

    public void removeNpc(final Npc _npc) {
        this.npcMapOfIDTable.remove(_npc.getID());
        this.npcMapOfModelIDTable.remove(_npc.getModelID());
    }

    public Monster processMonsterChanges(final Monster _archetype, final Changes _changesData) {
        _archetype.getActualProperty().setStrength(_changesData.strength);
        _archetype.getActualProperty().setAgility(_changesData.agility);
        _archetype.getActualProperty().setInte(_changesData.inte);
        _archetype.getActualProperty().setSpirit(_changesData.spirit);
        _archetype.getActualProperty().setLucky(_changesData.lucky);
        if (_changesData.newHp > 0) {
            _archetype.getActualProperty().setHpMax(_changesData.newHp);
            _archetype.setHp(_changesData.newHp);
        }
        int mp = CEService.mpByInte(_archetype.getActualProperty().getInte(), _archetype.getLevel(), _archetype.getObjectLevel().getMpCalPara());
        _archetype.getActualProperty().setMpMax(mp);
        _archetype.setMp(mp);
        _archetype.getActualProperty().setDefense(CEService.defenseBySpirit(_archetype.getActualProperty().getSpirit(), _archetype.getVocation().getPhysicsDefenceSpiritPara()) + _changesData.defense);
        _archetype.getActualProperty().setMinPhysicsAttack(CEService.maxPhysicsAttack(_archetype.getActualProperty().getStrength(), _archetype.getActualProperty().getAgility(), _archetype.getVocation().getPhysicsAttackParaA(), _archetype.getVocation().getPhysicsAttackParaB(), _archetype.getVocation().getPhysicsAttackParaC(), _changesData.minAttack, 0, _archetype.getActualAttackImmobilityTime() / 1000.0f, _archetype.getObjectLevel().getPhysicsAttckCalPara()));
        _archetype.getActualProperty().setMaxPhysicsAttack(CEService.maxPhysicsAttack(_archetype.getActualProperty().getStrength(), _archetype.getActualProperty().getAgility(), _archetype.getVocation().getPhysicsAttackParaA(), _archetype.getVocation().getPhysicsAttackParaB(), _archetype.getVocation().getPhysicsAttackParaC(), _changesData.maxAttack, 0, _archetype.getActualAttackImmobilityTime() / 1000.0f, _archetype.getObjectLevel().getPhysicsAttckCalPara()));
        int physicsDeathblowLevel = 0;
        int magicDeathblowLevel = 0;
        int hitLevel = 0;
        int duckLevel = 0;
        physicsDeathblowLevel = CEService.physicsDeathblowLevel(_archetype.getActualProperty().getAgility(), _archetype.getActualProperty().getLucky());
        magicDeathblowLevel = CEService.magicDeathblowLevel(_archetype.getActualProperty().getInte(), _archetype.getActualProperty().getLucky());
        hitLevel = CEService.hitLevel(_archetype.getActualProperty().getLucky());
        duckLevel = CEService.duckLevel(_archetype.getActualProperty().getInte(), _archetype.getActualProperty().getLucky());
        _archetype.getActualProperty().setPhysicsDeathblowLevel((short) physicsDeathblowLevel);
        _archetype.getActualProperty().setMagicDeathblowLevel((short) magicDeathblowLevel);
        _archetype.getActualProperty().setHitLevel((short) hitLevel);
        _archetype.getActualProperty().setPhysicsDuckLevel((short) duckLevel);
        _archetype.getActualProperty().getMagicFastnessList().reset(EMagic.SANCTITY, _changesData.sanctityFastness);
        _archetype.getActualProperty().getMagicFastnessList().reset(EMagic.UMBRA, _changesData.umbraFastness);
        _archetype.getActualProperty().getMagicFastnessList().reset(EMagic.FIRE, _changesData.fireFastness);
        _archetype.getActualProperty().getMagicFastnessList().reset(EMagic.WATER, _changesData.waterFastness);
        _archetype.getActualProperty().getMagicFastnessList().reset(EMagic.SOIL, _changesData.soilFastness);
        int baseMagicHarmByInte = CEService.magicHarmByInte(_archetype.getActualProperty().getInte());
        _archetype.getActualProperty().getBaseMagicHarmList().reset(EMagic.SANCTITY, (float) baseMagicHarmByInte);
        _archetype.getActualProperty().getBaseMagicHarmList().reset(EMagic.UMBRA, (float) baseMagicHarmByInte);
        _archetype.getActualProperty().getBaseMagicHarmList().reset(EMagic.FIRE, (float) baseMagicHarmByInte);
        _archetype.getActualProperty().getBaseMagicHarmList().reset(EMagic.WATER, (float) baseMagicHarmByInte);
        _archetype.getActualProperty().getBaseMagicHarmList().reset(EMagic.SOIL, (float) baseMagicHarmByInte);
        if (_changesData.magicType != null) {
            int magicHarmValue = CEService.magicHarm(_archetype.getActualProperty().getInte(), (_changesData.minDamageValue + _changesData.maxDamageValue) / 2);
            _archetype.getActualProperty().getBaseMagicHarmList().add(_changesData.magicType, (float) (magicHarmValue - baseMagicHarmByInte));
        }
        _archetype.setImageID(_changesData.imageID);
        return _archetype;
    }

    public Monster processMonsterChangesToArchetype(final Monster _changes, final int _archetypeHp, final Changes _changesData) {
        MonsterDataDict.MonsterData data = MonsterDataDict.getInstance().getMonsterData(_changes.getModelID().toLowerCase());
        if (data != null) {
            int defense = 0;
            if (data.strength != null) {
                _changes.getActualProperty().setStrength(Integer.parseInt(data.strength));
            }
            if (data.agility != null) {
                _changes.getActualProperty().setAgility(Integer.parseInt(data.agility));
            }
            if (data.stamina != null) {
                _changes.getActualProperty().setStamina(Integer.parseInt(data.stamina));
            }
            if (data.inte != null) {
                _changes.getActualProperty().setInte(Integer.parseInt(data.inte));
            }
            if (data.spirit != null) {
                _changes.getActualProperty().setSpirit(Integer.parseInt(data.spirit));
            }
            if (data.lucky != null) {
                _changes.getActualProperty().setLucky(Integer.parseInt(data.lucky));
            }
            if (data.defense != null) {
                defense = Integer.parseInt(data.defense);
            }
            if (_changesData.newHp > 0) {
                _changes.getActualProperty().setHpMax(CEService.hpByStamina(_changes.getActualProperty().getStamina(), _changes.getLevel(), _changes.getObjectLevel().getHpCalPara()));
                _changes.setHp(_archetypeHp);
            }
            int mp = CEService.mpByInte(_changes.getActualProperty().getInte(), _changes.getLevel(), _changes.getObjectLevel().getMpCalPara());
            _changes.getActualProperty().setMpMax(mp);
            _changes.setMp(mp);
            _changes.getActualProperty().setDefense(CEService.defenseBySpirit(_changes.getActualProperty().getSpirit(), _changes.getVocation().getPhysicsDefenceSpiritPara()) + defense);
            int maxPhysicsAttack = 0;
            int minPhysicsAttack = 0;
            if (data.minPhysicsAttack != null) {
                minPhysicsAttack = Integer.parseInt(data.minPhysicsAttack);
            }
            if (data.maxPhysicsAttack != null) {
                maxPhysicsAttack = Integer.parseInt(data.maxPhysicsAttack);
            }
            _changes.getActualProperty().setMinPhysicsAttack(CEService.maxPhysicsAttack(_changes.getActualProperty().getStrength(), _changes.getActualProperty().getAgility(), _changes.getVocation().getPhysicsAttackParaA(), _changes.getVocation().getPhysicsAttackParaB(), _changes.getVocation().getPhysicsAttackParaC(), minPhysicsAttack, 0, _changes.getActualAttackImmobilityTime() / 1000.0f, _changes.getObjectLevel().getPhysicsAttckCalPara()));
            _changes.getActualProperty().setMaxPhysicsAttack(CEService.maxPhysicsAttack(_changes.getActualProperty().getStrength(), _changes.getActualProperty().getAgility(), _changes.getVocation().getPhysicsAttackParaA(), _changes.getVocation().getPhysicsAttackParaB(), _changes.getVocation().getPhysicsAttackParaC(), maxPhysicsAttack, 0, _changes.getActualAttackImmobilityTime() / 1000.0f, _changes.getObjectLevel().getPhysicsAttckCalPara()));
            int physicsDeathblowLevel = 0;
            int magicDeathblowLevel = 0;
            int hitLevel = 0;
            int duckLevel = 0;
            physicsDeathblowLevel = CEService.physicsDeathblowLevel(_changes.getActualProperty().getAgility(), _changes.getActualProperty().getLucky());
            magicDeathblowLevel = CEService.magicDeathblowLevel(_changes.getActualProperty().getInte(), _changes.getActualProperty().getLucky());
            hitLevel = CEService.hitLevel(_changes.getActualProperty().getLucky());
            duckLevel = CEService.duckLevel(_changes.getActualProperty().getInte(), _changes.getActualProperty().getLucky());
            _changes.getActualProperty().setPhysicsDeathblowLevel((short) physicsDeathblowLevel);
            _changes.getActualProperty().setMagicDeathblowLevel((short) magicDeathblowLevel);
            _changes.getActualProperty().setHitLevel((short) hitLevel);
            _changes.getActualProperty().setPhysicsDuckLevel((short) duckLevel);
            _changes.getActualProperty().getMagicFastnessList().clear();
            if (data.sanctity != null) {
                _changes.getActualProperty().getMagicFastnessList().reset(EMagic.SANCTITY, Integer.parseInt(data.sanctity));
            }
            if (data.umbra != null) {
                _changes.getActualProperty().getMagicFastnessList().reset(EMagic.UMBRA, Integer.parseInt(data.umbra));
            }
            if (data.fire != null) {
                _changes.getActualProperty().getMagicFastnessList().reset(EMagic.FIRE, Integer.parseInt(data.fire));
            }
            if (data.water != null) {
                _changes.getActualProperty().getMagicFastnessList().reset(EMagic.WATER, Integer.parseInt(data.water));
            }
            if (data.soil != null) {
                _changes.getActualProperty().getMagicFastnessList().reset(EMagic.SOIL, Integer.parseInt(data.soil));
            }
            int baseMagicHarmByInte = CEService.magicHarmByInte(_changes.getActualProperty().getInte());
            _changes.getActualProperty().getBaseMagicHarmList().clear();
            _changes.getActualProperty().getBaseMagicHarmList().reset(EMagic.SANCTITY, (float) baseMagicHarmByInte);
            _changes.getActualProperty().getBaseMagicHarmList().reset(EMagic.UMBRA, (float) baseMagicHarmByInte);
            _changes.getActualProperty().getBaseMagicHarmList().reset(EMagic.FIRE, (float) baseMagicHarmByInte);
            _changes.getActualProperty().getBaseMagicHarmList().reset(EMagic.WATER, (float) baseMagicHarmByInte);
            _changes.getActualProperty().getBaseMagicHarmList().reset(EMagic.SOIL, (float) baseMagicHarmByInte);
            if (data.magicType != null) {
                int minDamageValue = Integer.parseInt(data.minDamageValue);
                int maxDamageValue = Integer.parseInt(data.maxDamageValue);
                int magicHarmValue = CEService.magicHarm(_changes.getActualProperty().getInte(), (minDamageValue + maxDamageValue) / 2);
                _changes.getActualProperty().getBaseMagicHarmList().add(EMagic.getMagic(data.magicType), (float) (magicHarmValue - baseMagicHarmByInte));
            }
            _changes.setImageID(Short.parseShort(data.imageID));
            return _changes;
        }
        return null;
    }

    public Monster getMonster(final int _monsterID) {
        return (Monster) this.monsterMapOfIDTable.get(_monsterID);
    }

    public Npc buildNpcInstance(final String _npcModelID) {
        Npc npc = null;
        NpcDataDict.NpcData data = NpcDataDict.getInstance().getNpcData(_npcModelID.toLowerCase());
        if (data != null) {
            npc = new Npc(data.helloContent);
            npc.setModelID(data.modelID);
            npc.setName(data.name);
            npc.setTitle(data.title);
            npc.setScreamContent(data.screamContent);
            npc.setImageID(Short.parseShort(data.imageID));
            npc.setImageType(Byte.parseByte(data.imageType));
            npc.setClan(EClan.getClanByDesc(data.clanDesc));
            npc.setAnimationID(Short.parseShort(data.animationID));
            EVocation vocation = EVocation.getVocationByDesc(data.skillEducateVocation);
            ESystemFeature feature = ESystemFeature.getFeatureByDesc(data.skillEducateFeature);
            if (data.function1 != null) {
                BaseNpcFunction f = FunctionBuilder.build(_npcModelID, npc.getID(), Integer.parseInt(data.function1), vocation, feature, npc.getClan());
                npc.addFunction(f);
                if (data.function2 != null) {
                    f = FunctionBuilder.build(_npcModelID, npc.getID(), Integer.parseInt(data.function2), vocation, feature, npc.getClan());
                    npc.addFunction(f);
                    if (data.function3 != null) {
                        f = FunctionBuilder.build(_npcModelID, npc.getID(), Integer.parseInt(data.function3), vocation, feature, npc.getClan());
                        npc.addFunction(f);
                        if (data.function4 != null) {
                            f = FunctionBuilder.build(_npcModelID, npc.getID(), Integer.parseInt(data.function4), vocation, feature, npc.getClan());
                            npc.addFunction(f);
                            if (data.function5 != null) {
                                f = FunctionBuilder.build(_npcModelID, npc.getID(), Integer.parseInt(data.function5), vocation, feature, npc.getClan());
                                npc.addFunction(f);
                            }
                        }
                    }
                }
            }
            this.npcMapOfIDTable.put(npc.getID(), npc);
            this.npcMapOfModelIDTable.put(npc.getModelID(), npc);
        }
        return npc;
    }

    public void groundTaskGoodsBePicked(final GroundTaskGoods _groundTaskGoods) {
        _groundTaskGoods.disappear();
        MapSynchronousInfoBroadcast.getInstance().put(_groundTaskGoods.where(), new GroundTaskGoodsDisappearNofity(_groundTaskGoods.getID(), _groundTaskGoods.getCellY()), false, 0);
        _groundTaskGoods.where().getGroundTaskGoodsList().remove(_groundTaskGoods);
        this.groundTaskGoodsTable.remove(_groundTaskGoods.getID());
        this.emptyGroundTaskGoodsList.add(_groundTaskGoods);
    }

    public void processMonsterLegacy(final Monster _monster, final ArrayList<int[]> _legacyItems, final ArrayList<TaskGoodsLegacyInfo> _legacyTaskGoodsList) {
        if (_monster.getAttackerAtFirst().getGroupID() > 0) {
            Group group = GroupServiceImpl.getInstance().getGroup(_monster.getAttackerAtFirst().getGroupID());
            if (group != null) {
                this.processGroupMonsterLegacy(group, _monster, _legacyItems, _legacyTaskGoodsList);
            } else {
                this.processPersonMonsterLegacy(_monster.getAttackerAtFirst(), _monster, _legacyItems, _legacyTaskGoodsList);
            }
        } else {
            this.processPersonMonsterLegacy(_monster.getAttackerAtFirst(), _monster, _legacyItems, _legacyTaskGoodsList);
        }
    }

    private void processGroupMonsterLegacy(final Group _group, final Monster _monster, final ArrayList<int[]> _legacyItems, final ArrayList<TaskGoodsLegacyInfo> _legacyTaskGoodsList) {
        if (_group != null) {
            ArrayList<HeroPlayer> playerList = _group.getValidatePlayerList(_monster.where().getID());
            int experience = 0;
            int money = (int) (_monster.getMoney() * 1.0f / playerList.size() + 0.5);
            for (final HeroPlayer player : playerList) {
                experience = CEService.getExperienceFromMonster(playerList.size(), player.getLevel(), _monster.getLevel(), _monster.getBaseExp());
                if (experience > 0) {
                    PlayerServiceImpl.getInstance().addExperience(player, experience, player.getExperienceModulus(), 1);
                }
                money += TeachService.getMasterAddMoney(player);
                if (money > 0) {
                    PlayerServiceImpl.getInstance().addMoney(player, money, player.getMoneyModulus(), 1, "\u602a\u7269\u6389\u843d");
                }
            }
            if ((_legacyItems != null && _legacyItems.size() > 0) || (_legacyTaskGoodsList != null && _legacyTaskGoodsList.size() > 0)) {
                _monster.isInDungeon();
                NotPlayerServiceImpl.log.debug(("group player size = " + _group.getMemberList().size() + ",pp list size=" + playerList.size()));
                RaidPickerBox box = new RaidPickerBox(_group.getID(), _group.getGoodsPickerUserID(_monster.where().getID()), playerList, _monster.getID(), _monster.where(), _legacyItems, _legacyTaskGoodsList, _monster.getCellX(), _monster.getCellY());
                MonsterLegacyManager.getInstance().addMonsterLegacyBox(box);
                for (final HeroPlayer player2 : playerList) {
                    ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new LegacyBoxEmergeNotify(box, box.getStateOfPicking(player2.getUserID())));
                }
                if (_legacyItems != null && _legacyItems.size() > 0) {
                    int[] goodsIDS = new int[_legacyItems.size()];
                    int[] goodsNums = new int[_legacyItems.size()];
                    String[] goodsNames = new String[_legacyItems.size()];
                    String[] goodsTypes = new String[_legacyItems.size()];
                    for (int i = 0; i < _legacyItems.size(); ++i) {
                        int[] goodsInfo = _legacyItems.get(i);
                        goodsIDS[i] = goodsInfo[0];
                        goodsNums[i] = goodsInfo[1];
                        Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsInfo[0]);
                        goodsNames[i] = goods.getName();
                        goodsTypes[i] = goods.getGoodsType().getDescription();
                    }
                    LogServiceImpl.getInstance().monsterLegacy(_monster.getModelID(), _monster.getName(), playerList.size(), true, money, _legacyItems.size(), goodsIDS, goodsNames, goodsNums, goodsTypes);
                }
            }
        }
    }

    private void processPersonMonsterLegacy(final HeroPlayer _player, final Monster _monster, final ArrayList<int[]> _legacyItems, final ArrayList<TaskGoodsLegacyInfo> _legacyTaskGoodsList) {
        try {
            int experience = CEService.getExperienceFromMonster(1, _player.getLevel(), _monster.getLevel(), _monster.getBaseExp());
            if (experience > 0) {
                PlayerServiceImpl.getInstance().addExperience(_player, experience, _player.getExperienceModulus(), 1);
            }
            int money = _monster.getMoney();
            money += TeachService.getMasterAddMoney(_player);
            if (money > 0) {
                PlayerServiceImpl.getInstance().addMoney(_player, money, _player.getMoneyModulus(), 1, "\u602a\u7269\u6389\u843d");
            }
            if (_legacyItems != null || _legacyTaskGoodsList != null) {
                MonsterLegacyBox box = new PersonalPickerBox(_player.getUserID(), _monster.getID(), _monster.where(), _legacyItems, _legacyTaskGoodsList, _monster.getCellX(), _monster.getCellY());
                MonsterLegacyManager.getInstance().addMonsterLegacyBox(box);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new LegacyBoxEmergeNotify(box, true));
                if (_legacyItems != null && _legacyItems.size() > 0) {
                    int[] goodsIDS = new int[_legacyItems.size()];
                    int[] goodsNums = new int[_legacyItems.size()];
                    String[] goodsNames = new String[_legacyItems.size()];
                    String[] goodsTypes = new String[_legacyItems.size()];
                    for (int i = 0; i < _legacyItems.size(); ++i) {
                        int[] goodsInfo = _legacyItems.get(i);
                        goodsIDS[i] = goodsInfo[0];
                        goodsNums[i] = goodsInfo[1];
                        Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsInfo[0]);
                        goodsNames[i] = goods.getName();
                        goodsTypes[i] = goods.getGoodsType().getDescription();
                    }
                    LogServiceImpl.getInstance().monsterLegacy(_monster.getModelID(), _monster.getName(), 1, false, money, _legacyItems.size(), goodsIDS, goodsNames, goodsNums, goodsTypes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void excuteRefreshQuestion() {
        Enumeration<Integer> keys = this.answerQuestionList.keys();
        while (keys.hasMoreElements()) {
            int key = keys.nextElement();
            AnswerQuestionData data = QuestionDict.getInstance().getAnswerQuestionData(key);
            if (data.refreshType == 1) {
                Date date1 = new Date(this.answerQuestionDayRefreshList.get(key));
                Date date2 = new Date(System.currentTimeMillis());
                int day = DateTool.getDifference(date1, date2);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date2);
                if (data.refreshDay > day || calendar.get(11) != 1) {
                    continue;
                }
                this.answerQuestionList.get(key).clear();
                this.answerQuestionDayRefreshList.put(key, System.currentTimeMillis());
                NotPlayerServiceImpl.log.info("\u5df2\u7ecf\u5230\u4e86\u65b0\u7684\u4e00\u5929,\u5bf9\u7b54\u9898\u6d3b\u52a8\u53c2\u4e0e\u8bb0\u5f55\u8fdb\u884c\u6e05\u96f6\u64cd\u4f5c.");
            } else {
                if (data.refreshType != 2) {
                    continue;
                }
                Date nowDate = new Date(System.currentTimeMillis());
                String endTime;
                String startTime = endTime = String.valueOf(DateTool.formatDate(nowDate)) + " ";
                for (int i = 0; i < data.refreshTimeSum; ++i) {
                    startTime = String.valueOf(startTime) + data.startTime[i];
                    endTime = String.valueOf(endTime) + data.endTime[i];
                    Date startDate = DateTool.convertLongDate(startTime);
                    Date endDate = DateTool.convertLongDate(endTime);
                    if (System.currentTimeMillis() >= startDate.getTime() && !this.answerQuestionTimeRefreshList.get(key) && System.currentTimeMillis() <= endDate.getTime()) {
                        this.answerQuestionList.get(key).clear();
                        this.answerQuestionTimeRefreshList.put(key, true);
                    } else if (System.currentTimeMillis() >= endDate.getTime() && this.answerQuestionTimeRefreshList.get(key)) {
                        this.answerQuestionTimeRefreshList.put(key, false);
                    }
                }
            }
        }
    }

    public void broadcastNotPlayerWalkPath(final ME2NotPlayer _npc, final byte[] _path, final HeroPlayer _attackTarget) {
        if (_npc.where().getPlayerList().size() > 0) {
            AbsResponseMessage msg = null;
            if (EObjectType.MONSTER == _npc.getObjectType()) {
                msg = new MonsterWalkNotify(_npc.getID(), _npc.getMoveSpeed(), _path, (byte) _npc.getCellX(), (byte) _npc.getCellY());
            } else {
                msg = new NpcWalkNotify(_npc.getID(), _path, (byte) _npc.getCellX(), (byte) _npc.getCellY());
            }
            if (_npc.where().getPlayerList().size() > 0) {
                if (_attackTarget == null) {
                    MapSynchronousInfoBroadcast.getInstance().put(_npc.where(), msg, false, 0);
                } else {
                    MapSynchronousInfoBroadcast.getInstance().put(_npc.where(), msg, false, _attackTarget.getID());
                }
            }
        }
    }

    class AnimalWalkThread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(30000L);
                while (true) {
                    for (final Animal animal : NotPlayerServiceImpl.this.animalList) {
                        if (NotPlayerServiceImpl.random.nextInt(3) > 0) {
                            animal.walk();
                        }
                    }
                    Thread.sleep(10000L);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class RefreshQuestionManager extends TimerTask {

        @Override
        public void run() {
            try {
                NotPlayerServiceImpl.this.excuteRefreshQuestion();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class EmptyBoxThread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(45000L);
                while (true) {
                    for (int i = 0; i < NotPlayerServiceImpl.this.emptyBoxList.size(); ++i) {
                        Box box = (Box) NotPlayerServiceImpl.this.emptyBoxList.get(i);
                        if (System.currentTimeMillis() - box.getDisappearTime() >= box.getRebirthInterval()) {
                            NotPlayerServiceImpl.this.emptyBoxList.remove(i);
                            NotPlayerServiceImpl.this.boxTable.put(box.getID(), box);
                            box.where().getBoxList().add(box);
                            box.rebirth(false);
                        }
                    }
                    Thread.sleep(60000L);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class BePickedGroundTaskGoodsThread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(30000L);
                while (true) {
                    for (int i = 0; i < NotPlayerServiceImpl.this.emptyGroundTaskGoodsList.size(); ++i) {
                        GroundTaskGoods groundTaskGoods = (GroundTaskGoods) NotPlayerServiceImpl.this.emptyGroundTaskGoodsList.get(i);
                        long disappear = System.currentTimeMillis() - groundTaskGoods.getDisappearTime();
                        if (disappear >= ((NpcConfig) NotPlayerServiceImpl.this.config).task_gather_rebirth_interval) {
                            TaskServiceImpl.getInstance().groundTaskGoodsRebirth(groundTaskGoods);
                            NotPlayerServiceImpl.this.emptyGroundTaskGoodsList.remove(i);
                            NotPlayerServiceImpl.this.groundTaskGoodsTable.put(groundTaskGoods.getID(), groundTaskGoods);
                            groundTaskGoods.where().getGroundTaskGoodsList().add(groundTaskGoods);
                        }
                    }
                    Thread.sleep(((NpcConfig) NotPlayerServiceImpl.this.config).task_gather_thread_run_interval * 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
