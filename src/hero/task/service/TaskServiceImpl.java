// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.service;

import hero.npc.message.ChangeNpcStat;
import hero.task.message.ResponseTaskItems;
import hero.log.service.ServiceType;
import hero.charge.service.ChargeServiceImpl;
import hero.item.dictionary.GoodsContents;
import hero.share.service.ShareServiceImpl;
import hero.share.service.ShareConfig;
import hero.task.message.NotifyMapNpcTaskMark;
import hero.expressions.service.CEService;
import hero.log.service.LogServiceImpl;
import hero.item.detail.EGoodsType;
import hero.share.service.ME2ObjectList;
import hero.npc.message.GroundTaskGoodsEmergeNotify;
import hero.map.EMapType;
import hero.map.Map;
import hero.effect.service.EffectServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseMapBottomData;
import hero.map.service.MapServiceImpl;
import hero.map.message.SwitchMapFailNotify;
import hero.npc.message.NpcResetNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.message.ChangeGroundTaskGoodsStat;
import hero.item.bag.exception.BagException;
import hero.npc.others.GroundTaskGoods;
import hero.npc.message.ChangeTaskGearStat;
import hero.npc.others.TaskGear;
import hero.task.message.TaskListChangerNotify;
import hero.log.service.CauseLog;
import hero.task.message.NotifyPlayerReciveRepeateTaskTimes;
import yoyo.service.base.player.IPlayer;
import hero.player.service.PlayerDAO;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import hero.task.message.ChangeNpcTaskMark;
import hero.npc.service.NotPlayerServiceImpl;
import hero.task.message.RefreshTaskStatus;
import hero.group.Group;
import hero.group.service.GroupServiceImpl;
import hero.item.legacy.TaskGoodsLegacyInfo;
import hero.npc.Monster;
import hero.item.Goods;
import org.dom4j.Document;
import hero.task.Award;
import hero.task.target.BaseTaskTarget;
import hero.task.target.ETastTargetType;
import hero.task.target.TaskType;
import hero.manufacture.ManufactureType;
import hero.player.define.EClan;
import hero.share.EVocation;
import hero.task.Condition;
import hero.task.target.TaskTargetOpenGear;
import hero.task.target.TaskTargetFoundAPath;
import hero.task.target.TaskTargetEscortNpc;
import hero.task.target.TaskTargetGoods;
import hero.item.service.GoodsServiceImpl;
import hero.item.SingleGoods;
import hero.task.target.TaskTargetKillMonster;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.TimerTask;
import java.util.Iterator;
import hero.share.service.LogWriter;
import hero.npc.Npc;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import yoyo.service.base.session.Session;
import java.util.Timer;
import javolution.util.FastList;
import hero.task.MonsterTaskGoodsSetting;
import hero.task.Task;
import hero.task.Push;
import hero.task.TaskInstance;
import java.util.ArrayList;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class TaskServiceImpl extends AbsServiceAdaptor<TaskConfig> {

    private static Logger log;
    private FastMap<Integer, ArrayList<Integer>> playerCompletedTaskListMap;
    private FastMap<Integer, ArrayList<TaskInstance>> playerExsitsTaskListMap;
    private FastMap<Integer, Push> pushDataTable;
    private FastMap<String, Integer[]> pushPlayerGoods;
    private FastMap<Integer, Task> taskDictionary;
    private FastMap<String, ArrayList<Task>> npcTaskDictionary;
    private FastMap<String, ArrayList<MonsterTaskGoodsSetting>> monsterTaskGoodsDictory;
    private FastList<EscortNpcTaskInfo> escortTaskExcuteList;
    private FastList<FoundAPathInfo> foundAPathTaskList;
    private static TaskServiceImpl instance;
    private static final byte ESCORT_TASK_VALIDATE_DISTANCE = 15;
    private static final int ESCORT_TASK_INTERVAL = 5000;
    private static final int FOUND_A_PATH_TASK_INTERVAL = 10000;
    public static final byte MAX_TASK_NUMBER_AT_TIME = 20;
    private static final int THREAD_START_DELAY = 30000;
    private Timer timer;

    static {
        TaskServiceImpl.log = Logger.getLogger((Class) TaskServiceImpl.class);
    }

    public static TaskServiceImpl getInstance() {
        if (TaskServiceImpl.instance == null) {
            TaskServiceImpl.instance = new TaskServiceImpl();
        }
        return TaskServiceImpl.instance;
    }

    private TaskServiceImpl() {
        this.escortTaskExcuteList = (FastList<EscortNpcTaskInfo>) new FastList();
        this.foundAPathTaskList = (FastList<FoundAPathInfo>) new FastList();
        this.config = new TaskConfig();
        this.timer = new Timer();
        this.playerCompletedTaskListMap = (FastMap<Integer, ArrayList<Integer>>) new FastMap();
        this.playerExsitsTaskListMap = (FastMap<Integer, ArrayList<TaskInstance>>) new FastMap();
        this.taskDictionary = (FastMap<Integer, Task>) new FastMap();
        this.pushDataTable = (FastMap<Integer, Push>) new FastMap();
        this.npcTaskDictionary = (FastMap<String, ArrayList<Task>>) new FastMap();
        this.monsterTaskGoodsDictory = (FastMap<String, ArrayList<MonsterTaskGoodsSetting>>) new FastMap();
        this.pushPlayerGoods = (FastMap<String, Integer[]>) new FastMap();
    }

    @Override
    public void dbUpdate(final int _userID) {
    }

    @Override
    public void createSession(final Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
        if (player != null) {
            this.playerCompletedTaskListMap.put(_session.userID, new ArrayList());
            this.playerExsitsTaskListMap.put(_session.userID, new ArrayList());
            TaskDAO.loadTask(player, (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_session.userID), (ArrayList<Integer>) this.playerCompletedTaskListMap.get(_session.userID));
        }
    }

    @Override
    public void sessionFree(final Session _session) {
        try {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
            if (player != null && player.getEscortTarget() != null) {
                synchronized (this.escortTaskExcuteList) {
                    for (int i = 0; i < this.escortTaskExcuteList.size(); ++i) {
                        EscortNpcTaskInfo info = (EscortNpcTaskInfo) this.escortTaskExcuteList.get(i);
                        if (info.trigger == player) {
                            info = (EscortNpcTaskInfo) this.escortTaskExcuteList.remove(i);
                            this.endEscort(info);
                            if (info.spareTaskMemberList != null) {
                                for (final HeroPlayer other : info.spareTaskMemberList) {
                                    if (other.isEnable()) {
                                        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new Warning("\u2018" + info.task.getName() + "\u2019\u4efb\u52a1\u5931\u8d25", (byte) 0));
                                    }
                                }
                            }
                            info.trigger = null;
                            info.spareTaskMemberList.clear();
                            break;
                        }
                        if (info.spareTaskMemberList.contains(player)) {
                            info.spareTaskMemberList.remove(player);
                            player.setEscortTarget(null);
                            break;
                        }
                    }
                }
                // monitorexit(this.escortTaskExcuteList)
            }
        } catch (Exception e) {
            LogWriter.println("\u91ca\u653e\u4efb\u52a1\u4f1a\u8bdd\u51fa\u9519");
        }
    }

    @Override
    public void clean(final int _userID) {
        this.playerExsitsTaskListMap.remove(_userID);
        this.playerCompletedTaskListMap.remove(_userID);
    }

    @Override
    protected void start() {
        this.load();
        this.timer.schedule(new EscortNpcTaskManager(), 30000L, 5000L);
        this.timer.schedule(new FoundAPathTaskManager(), 30000L, 10000L);
    }

    public Task getTask(final int _taskID) {
        return (Task) this.taskDictionary.get(_taskID);
    }

    public TaskInstance getPlayerTask(final int _userID, final int _taskID) {
        ArrayList<TaskInstance> taskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_userID);
        for (final TaskInstance task : taskList) {
            if (task.getArchetype().getID() == _taskID) {
                return task;
            }
        }
        return null;
    }

    private void load() {
        File dataPath = null;
        FastMap<Integer, BaseTaskTarget> killMonsterTargetList = (FastMap<Integer, BaseTaskTarget>) new FastMap();
        try {
            dataPath = new File(((TaskConfig) this.config).getKillMonsterTaskTargetDataPath());
            File[] dataFileList = dataPath.listFiles();
            File[] array;
            for (int length = (array = dataFileList).length, l = 0; l < length; ++l) {
                File dataFile = array[l];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    TaskTargetKillMonster tastTarget = null;
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            tastTarget = new TaskTargetKillMonster(Integer.parseInt(subE.elementTextTrim("id")), subE.elementTextTrim("monsterID").toLowerCase(), Short.parseShort(subE.elementTextTrim("number")));
                            String mapID;
                            String mapX;
                            String mapY;
                            if ((mapID = subE.elementTextTrim("mapID")) != null && (mapX = subE.elementTextTrim("mapX")) != null && (mapY = subE.elementTextTrim("mapY")) != null) {
                                tastTarget.setTransmitMapInfo(new short[]{Short.parseShort(mapID), Short.parseShort(mapX), Short.parseShort(mapY)});
                            }
                            killMonsterTargetList.put(tastTarget.getID(), tastTarget);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FastMap<Integer, BaseTaskTarget> goodsTargetList = (FastMap<Integer, BaseTaskTarget>) new FastMap();
        try {
            dataPath = new File(((TaskConfig) this.config).getGoodsTaskTargetDataPath());
            File[] dataFileList2 = dataPath.listFiles();
            File[] array2;
            for (int length2 = (array2 = dataFileList2).length, n = 0; n < length2; ++n) {
                File dataFile2 = array2[n];
                if (dataFile2.getName().endsWith(".xml")) {
                    SAXReader reader2 = new SAXReader();
                    Document document2 = reader2.read(dataFile2);
                    Element rootE2 = document2.getRootElement();
                    Iterator<Element> rootIt2 = (Iterator<Element>) rootE2.elementIterator();
                    TaskTargetGoods tastTarget2 = null;
                    while (rootIt2.hasNext()) {
                        Element subE2 = rootIt2.next();
                        if (subE2 != null) {
                            TaskServiceImpl.log.debug(("load task target goods id == " + subE2.elementTextTrim("id")));
                            try {
                                tastTarget2 = new TaskTargetGoods(Integer.parseInt(subE2.elementTextTrim("id")), (SingleGoods) GoodsServiceImpl.getInstance().getGoodsByID(Integer.parseInt(subE2.elementTextTrim("goodsID"))), Short.parseShort(subE2.elementTextTrim("number")));
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            String mapID;
                            String mapX;
                            String mapY;
                            if ((mapID = subE2.elementTextTrim("mapID")) != null && (mapX = subE2.elementTextTrim("mapX")) != null && (mapY = subE2.elementTextTrim("mapY")) != null) {
                                tastTarget2.setTransmitMapInfo(new short[]{Short.parseShort(mapID), Short.parseShort(mapX), Short.parseShort(mapY)});
                            }
                            goodsTargetList.put(tastTarget2.getID(), tastTarget2);
                        }
                    }
                }
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        FastMap<Integer, BaseTaskTarget> escortNpcTargetList = (FastMap<Integer, BaseTaskTarget>) new FastMap();
        try {
            dataPath = new File(((TaskConfig) this.config).getEscortNpcTaskTargetDataPath());
            File[] dataFileList3 = dataPath.listFiles();
            File[] array3;
            for (int length3 = (array3 = dataFileList3).length, n2 = 0; n2 < length3; ++n2) {
                File dataFile3 = array3[n2];
                if (dataFile3.getName().endsWith(".xml")) {
                    SAXReader reader3 = new SAXReader();
                    Document document3 = reader3.read(dataFile3);
                    Element rootE3 = document3.getRootElement();
                    Iterator<Element> rootIt3 = (Iterator<Element>) rootE3.elementIterator();
                    TaskTargetEscortNpc tastTarget3 = null;
                    while (rootIt3.hasNext()) {
                        Element subE3 = rootIt3.next();
                        if (subE3 != null) {
                            TaskServiceImpl.log.debug(("load task target escort npc = " + subE3.elementTextTrim("id")));
                            tastTarget3 = new TaskTargetEscortNpc(Integer.parseInt(subE3.elementTextTrim("id")), subE3.elementTextTrim("npcID").toLowerCase(), Integer.parseInt(subE3.elementTextTrim("totalTime")) * 60 * 1000, Integer.parseInt(subE3.elementTextTrim("mapID")), Short.parseShort(subE3.elementTextTrim("mapX")), Short.parseShort(subE3.elementTextTrim("mapY")), Short.parseShort(subE3.elementTextTrim("range")));
                            escortNpcTargetList.put(tastTarget3.getID(), tastTarget3);
                        }
                    }
                }
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        FastMap<Integer, BaseTaskTarget> foundPathTargetList = (FastMap<Integer, BaseTaskTarget>) new FastMap();
        try {
            dataPath = new File(((TaskConfig) this.config).getFoundPathTaskTargetDataPath());
            File[] dataFileList4 = dataPath.listFiles();
            File[] array4;
            for (int length4 = (array4 = dataFileList4).length, n3 = 0; n3 < length4; ++n3) {
                File dataFile4 = array4[n3];
                if (dataFile4.getName().endsWith(".xml")) {
                    SAXReader reader4 = new SAXReader();
                    Document document4 = reader4.read(dataFile4);
                    Element rootE4 = document4.getRootElement();
                    Iterator<Element> rootIt4 = (Iterator<Element>) rootE4.elementIterator();
                    TaskTargetFoundAPath tastTarget4 = null;
                    while (rootIt4.hasNext()) {
                        Element subE4 = rootIt4.next();
                        if (subE4 != null) {
                            TaskServiceImpl.log.debug(("load task target found path = " + subE4.elementTextTrim("id")));
                            tastTarget4 = new TaskTargetFoundAPath(Integer.parseInt(subE4.elementTextTrim("id")), Short.parseShort(subE4.elementTextTrim("mapID")), Short.parseShort(subE4.elementTextTrim("mapX")), Short.parseShort(subE4.elementTextTrim("mapY")), Short.parseShort(subE4.elementTextTrim("range")));
                            foundPathTargetList.put(tastTarget4.getID(), tastTarget4);
                        }
                    }
                }
            }
        } catch (Exception e5) {
            e5.printStackTrace();
        }
        FastMap<Integer, BaseTaskTarget> openGearTargetList = (FastMap<Integer, BaseTaskTarget>) new FastMap();
        try {
            dataPath = new File(((TaskConfig) this.config).getOpenGearTaskTargetDataPath());
            File[] dataFileList5 = dataPath.listFiles();
            File[] array5;
            for (int length5 = (array5 = dataFileList5).length, n4 = 0; n4 < length5; ++n4) {
                File dataFile5 = array5[n4];
                if (dataFile5.getName().endsWith(".xml")) {
                    SAXReader reader5 = new SAXReader();
                    Document document5 = reader5.read(dataFile5);
                    Element rootE5 = document5.getRootElement();
                    Iterator<Element> rootIt5 = (Iterator<Element>) rootE5.elementIterator();
                    TaskTargetOpenGear tastTarget5 = null;
                    while (rootIt5.hasNext()) {
                        Element subE5 = rootIt5.next();
                        if (subE5 != null) {
                            tastTarget5 = new TaskTargetOpenGear(Integer.parseInt(subE5.elementTextTrim("id")), subE5.elementTextTrim("npcID").toLowerCase());
                            String mapID;
                            String mapX;
                            String mapY;
                            if ((mapID = subE5.elementTextTrim("mapID")) != null && (mapX = subE5.elementTextTrim("mapX")) != null && (mapY = subE5.elementTextTrim("mapY")) != null) {
                                tastTarget5.setTransmitMapInfo(new short[]{Short.parseShort(mapID), Short.parseShort(mapX), Short.parseShort(mapY)});
                            }
                            openGearTargetList.put(tastTarget5.getID(), tastTarget5);
                        }
                    }
                }
            }
        } catch (Exception e6) {
            e6.printStackTrace();
        }
        FastMap<Integer, String[]> descriptionList = (FastMap<Integer, String[]>) new FastMap();
        try {
            dataPath = new File(((TaskConfig) this.config).getDescDataPath());
            File[] dataFileList6 = dataPath.listFiles();
            File[] array6;
            for (int length6 = (array6 = dataFileList6).length, n5 = 0; n5 < length6; ++n5) {
                File dataFile6 = array6[n5];
                if (dataFile6.getName().endsWith(".xml")) {
                    SAXReader reader6 = new SAXReader();
                    Document document6 = reader6.read(dataFile6);
                    Element rootE6 = document6.getRootElement();
                    Iterator<Element> rootIt6 = (Iterator<Element>) rootE6.elementIterator();
                    String[] descList = null;
                    while (rootIt6.hasNext()) {
                        Element subE6 = rootIt6.next();
                        if (subE6 != null) {
                            descList = new String[]{subE6.elementTextTrim("receiveContent"), subE6.elementTextTrim("viewContent"), subE6.elementTextTrim("submitContent")};
                            descriptionList.put(Integer.parseInt(subE6.elementTextTrim("id")), descList);
                        }
                    }
                }
            }
        } catch (Exception e7) {
            e7.printStackTrace();
        }
        try {
            dataPath = new File(((TaskConfig) this.config).getTaskPushPath());
            File[] dataFileList6 = dataPath.listFiles();
            File[] array7;
            for (int length7 = (array7 = dataFileList6).length, n6 = 0; n6 < length7; ++n6) {
                File dataFile6 = array7[n6];
                if (dataFile6.getName().endsWith(".xml")) {
                    SAXReader reader6 = new SAXReader();
                    Document document6 = reader6.read(dataFile6);
                    Element rootE6 = document6.getRootElement();
                    Iterator<Element> rootIt6 = (Iterator<Element>) rootE6.elementIterator();
                    while (rootIt6.hasNext()) {
                        Element subE5 = rootIt6.next();
                        if (subE5 != null) {
                            Push push = new Push();
                            push.id = Integer.parseInt(subE5.elementTextTrim("id"));
                            push.pushNum = Integer.parseInt(subE5.elementTextTrim("pushNum"));
                            push.commPushContent = String.valueOf(subE5.elementTextTrim("commPushContent"));
                            push.commConfirmContent = String.valueOf(subE5.elementTextTrim("commConfirmContent"));
                            push.point = Integer.parseInt(subE5.elementTextTrim("point"));
                            push.goodsID = Integer.parseInt(subE5.elementTextTrim("goods"));
                            push.time = Integer.parseInt(subE5.elementTextTrim("countDown"));
                            push.limitContent = new String[push.pushNum];
                            push.pushContent = new String[push.pushNum];
                            push.pushType = new int[push.pushNum];
                            int index = 0;
                            for (int i = 0; i < push.pushNum; ++i) {
                                index = i + 1;
                                push.limitContent[i] = String.valueOf(subE5.elementTextTrim("limit" + index + "Content"));
                                push.pushContent[i] = String.valueOf(subE5.elementTextTrim("push" + index + "Content"));
                                push.pushType[i] = Integer.valueOf(subE5.elementTextTrim("push" + index + "Type"));
                            }
                            this.pushDataTable.put(push.id, push);
                        }
                    }
                }
            }
        } catch (Exception e7) {
            e7.printStackTrace();
        }
        try {
            dataPath = new File(((TaskConfig) this.config).getTaskDataPath());
            File[] dataFileList6 = dataPath.listFiles();
            File[] array8;
            for (int length8 = (array8 = dataFileList6).length, n7 = 0; n7 < length8; ++n7) {
                File dataFile6 = array8[n7];
                if (dataFile6.getName().endsWith(".xml")) {
                    SAXReader reader6 = new SAXReader();
                    Document document6 = reader6.read(dataFile6);
                    Element rootE6 = document6.getRootElement();
                    Iterator<Element> rootIt6 = (Iterator<Element>) rootE6.elementIterator();
                    Task task = null;
                    while (rootIt6.hasNext()) {
                        Element subE6 = rootIt6.next();
                        if (subE6 != null) {
                            task = new Task(Integer.parseInt(subE6.elementTextTrim("id")), subE6.elementTextTrim("name"), Short.parseShort(subE6.elementTextTrim("level")), subE6.elementTextTrim("isRepeated").equals("\u662f"));
                            task.setDifficultyLevel(Task.ETaskDifficultyLevel.get(subE6.elementTextTrim("difficultyLevel")));
                            String taskPushID = subE6.elementTextTrim("taskFeeMode");
                            if (taskPushID != null && !taskPushID.equals("")) {
                                int pushID = Integer.parseInt(taskPushID);
                                task.setTaskPush((Push) this.pushDataTable.get(pushID));
                            }
                            Condition condition = new Condition();
                            condition.vocation = EVocation.getVocationByDesc(subE6.elementTextTrim("vocation"));
                            condition.clan = EClan.getClanByDesc(subE6.elementTextTrim("clan"));
                            condition.level = Short.parseShort(subE6.elementTextTrim("needLevel"));
                            TaskServiceImpl.log.debug(("task[" + task.getName() + "], vovation=" + condition.vocation + " -- clan=" + condition.clan + " -- needlevel= " + condition.level));
                            String completedTaskID = subE6.elementTextTrim("completedTaskID");
                            if (completedTaskID != null) {
                                condition.completeTaskID = Integer.parseInt(completedTaskID);
                            }
                            String taskNext = subE6.elementTextTrim("taskNext");
                            if (taskNext != null) {
                                condition.taskNext = Integer.parseInt(taskNext);
                            }
                            String data = subE6.elementTextTrim("isMainLine");
                            if (data != null && data.equals("\u662f")) {
                                task.setMainLine();
                            }
                            String needManufSkillType = subE6.elementTextTrim("needManufSkill");
                            if (needManufSkillType != null) {
                                condition.manufactureType = ManufactureType.get(Byte.parseByte(needManufSkillType));
                            }
                            String taskType = subE6.elementTextTrim("taskType");
                            if (taskType != null) {
                                condition.taskType = TaskType.getTaskTypeByType(taskType);
                            } else {
                                condition.taskType = TaskType.SINGLE;
                            }
                            TaskServiceImpl.log.debug(("task [" + task.getName() + "] -- type=" + condition.taskType));
                            task.setCondition(condition);
                            task.setDescList((String[]) descriptionList.get(Integer.parseInt(subE6.elementTextTrim("descListID"))));
                            for (int j = 1; j <= 5; ++j) {
                                ETastTargetType targetType = ETastTargetType.getTastTargetTypeByDesc(subE6.elementTextTrim("target" + j + "Type"));
                                if (targetType == null) {
                                    break;
                                }
                                int targetID = Integer.parseInt(subE6.elementTextTrim("target" + j + "ID"));
                                BaseTaskTarget taskTarget = null;
                                switch (targetType) {
                                    case KILL_MONSTER: {
                                        taskTarget = (BaseTaskTarget) killMonsterTargetList.get(targetID);
                                        break;
                                    }
                                    case GOODS: {
                                        taskTarget = (BaseTaskTarget) goodsTargetList.get(targetID);
                                        break;
                                    }
                                    case ESCORT_NPC: {
                                        taskTarget = (BaseTaskTarget) escortNpcTargetList.get(targetID);
                                        break;
                                    }
                                    case FOUND_A_PATH: {
                                        taskTarget = (BaseTaskTarget) foundPathTargetList.get(targetID);
                                        if (taskTarget != null) {
                                            this.addFoundPathTask(task.getID(), (TaskTargetFoundAPath) taskTarget);
                                            break;
                                        }
                                        break;
                                    }
                                    case OPEN_GEAR: {
                                        taskTarget = (BaseTaskTarget) openGearTargetList.get(targetID);
                                        break;
                                    }
                                }
                                if (taskTarget != null) {
                                    task.addTarget(taskTarget);
                                }
                            }
                            task.setDistributeNpcModelID(subE6.elementTextTrim("distributeNpcID").toLowerCase());
                            for (int j = 1; j <= 3; ++j) {
                                String goodsID = subE6.elementTextTrim("receiveGoods" + j + "ID");
                                if (goodsID == null) {
                                    break;
                                }
                                short number = Short.parseShort(subE6.elementTextTrim("receiveGoods" + j + "Nums"));
                                task.addReceiveGoods(Integer.parseInt(goodsID), number);
                            }
                            String effectID = subE6.elementTextTrim("receiveEffectID");
                            if (effectID != null) {
                                task.setReceiveEffectID(Integer.parseInt(effectID));
                            }
                            task.setSubmitNpcID(subE6.elementTextTrim("submitNpcID").toLowerCase());
                            Award award = new Award();
                            String temp = subE6.elementTextTrim("awardExp");
                            if (temp != null) {
                                award.experience = Integer.parseInt(temp);
                            }
                            temp = subE6.elementTextTrim("awardMoney");
                            if (temp != null) {
                                award.money = Integer.parseInt(temp);
                            }
                            temp = subE6.elementTextTrim("endToMapID");
                            if (temp != null) {
                                award.mapID = Short.parseShort(temp);
                            } else {
                                award.mapID = -1;
                            }
                            temp = subE6.elementTextTrim("endToMapX");
                            if (temp != null) {
                                award.mapX = Short.parseShort(temp);
                            } else {
                                award.mapX = -1;
                            }
                            temp = subE6.elementTextTrim("endToMapY");
                            if (temp != null) {
                                award.mapY = Short.parseShort(temp);
                            } else {
                                award.mapY = -1;
                            }
                            temp = subE6.elementTextTrim("awardSkillID");
                            if (temp != null) {
                                award.skillID = Integer.parseInt(temp);
                            }
                            temp = subE6.elementTextTrim("awardEffectID");
                            if (temp != null) {
                                award.effectID = Integer.parseInt(temp);
                            }
                            for (int k = 1; k <= 5; ++k) {
                                String goodsID2 = subE6.elementTextTrim("sAwardGood" + k + "ID");
                                if (goodsID2 == null) {
                                    break;
                                }
                                Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(Integer.parseInt(goodsID2));
                                if (goods != null) {
                                    short number2 = Short.parseShort(subE6.elementTextTrim("sAwardGood" + k + "Nums"));
                                    award.addOptionalGoods(goods, number2);
                                } else {
                                    LogWriter.println("\u52a0\u8f7d\u4efb\u52a1\u53ef\u9009\u5956\u52b1\u7269\u54c1\u9519\u8bef\uff1a" + task.getName() + "  \u7269\u54c1\u7f16\u53f7\uff1a" + goodsID2);
                                }
                            }
                            for (int k = 1; k <= 5; ++k) {
                                String goodsID2 = subE6.elementTextTrim("gAwardGood" + k + "ID");
                                if (goodsID2 == null) {
                                    break;
                                }
                                Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(Integer.parseInt(goodsID2));
                                if (goods != null) {
                                    short number2 = 0;
                                    try {
                                        number2 = Short.parseShort(subE6.elementTextTrim("gAwardGood" + k + "Nums"));
                                    } catch (Exception e8) {
                                        System.out.println("\u52a0\u8f7d\u7269\u54c1" + goodsID2 + "\u7684\u6570\u91cf\u5931\u8d25");
                                        e8.printStackTrace();
                                        break;
                                    }
                                    award.addBoundGoods(GoodsServiceImpl.getInstance().getGoodsByID(Integer.parseInt(goodsID2)), number2);
                                } else {
                                    LogWriter.println("\u52a0\u8f7d\u4efb\u52a1\u5fc5\u5f97\u5956\u52b1\u7269\u54c1\u9519\u8bef\uff1a" + task.getName() + "  \u7269\u54c1\u7f16\u53f7\uff1a" + goodsID2);
                                }
                            }
                            task.setAward(award);
                            this.taskDictionary.put(task.getID(), task);
                            ArrayList<Task> list = (ArrayList<Task>) this.npcTaskDictionary.get(task.getDistributeNpcModelID());
                            if (list == null) {
                                list = new ArrayList<Task>();
                                this.npcTaskDictionary.put(task.getDistributeNpcModelID(), list);
                            }
                            list.add(task);
                        }
                    }
                }
            }
        } catch (Exception e7) {
            e7.printStackTrace();
        }
        try {
            dataPath = new File(((TaskConfig) this.config).getMonsterTaskGoodsDataPath());
            File[] dataFileList6 = dataPath.listFiles();
            File[] array9;
            for (int length9 = (array9 = dataFileList6).length, n8 = 0; n8 < length9; ++n8) {
                File dataFile6 = array9[n8];
                if (dataFile6.getName().endsWith(".xml")) {
                    SAXReader reader6 = new SAXReader();
                    Document document6 = reader6.read(dataFile6);
                    Element rootE6 = document6.getRootElement();
                    Iterator<Element> rootIt6 = (Iterator<Element>) rootE6.elementIterator();
                    ArrayList<MonsterTaskGoodsSetting> list2 = null;
                    MonsterTaskGoodsSetting set = null;
                    while (rootIt6.hasNext()) {
                        Element subE7 = rootIt6.next();
                        if (subE7 != null) {
                            String monsterModelID = subE7.elementTextTrim("monsterID");
                            list2 = (ArrayList<MonsterTaskGoodsSetting>) this.monsterTaskGoodsDictory.get(monsterModelID);
                            if (list2 == null) {
                                list2 = new ArrayList<MonsterTaskGoodsSetting>();
                                this.monsterTaskGoodsDictory.put(monsterModelID, list2);
                            }
                            set = new MonsterTaskGoodsSetting(Integer.parseInt(subE7.elementTextTrim("goodsID")), Integer.parseInt(subE7.elementTextTrim("taskID")), Integer.parseInt(subE7.elementTextTrim("odds")) / 100.0f, Short.parseShort(subE7.elementTextTrim("maxNumber")));
                            list2.add(set);
                        }
                    }
                }
            }
        } catch (Exception e7) {
            e7.printStackTrace();
        }
    }

    public ArrayList<MonsterTaskGoodsSetting> getTaskGoodsDropList(final String _monsterModelID) {
        return (ArrayList<MonsterTaskGoodsSetting>) this.monsterTaskGoodsDictory.get(_monsterModelID);
    }

    private boolean canReceiveTask(final HeroPlayer _player, final Task _task) {
        ArrayList<TaskInstance> taskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        TaskServiceImpl.log.debug("exists task list ...");
        for (final TaskInstance task : taskList) {
            TaskServiceImpl.log.debug(("exists task id = " + task.getArchetype().getID() + " ;; _taks id =" + _task.getID()));
            if (task.getArchetype().getID() == _task.getID()) {
                return false;
            }
        }
        TaskServiceImpl.log.debug("next completed task list ....");
        ArrayList<Integer> completedTaskList = (ArrayList<Integer>) this.playerCompletedTaskListMap.get(_player.getUserID());
        if (!_task.isRepeated() && completedTaskList.contains(_task.getID())) {
            return false;
        }
        TaskServiceImpl.log.debug(("next condition task : " + _task.getName()));
        return _task.getCondition().check(_player.getUserID(), _player.getVocation(), _player.getClan(), _player.getLevel(), completedTaskList);
    }

    public boolean hasCompleteTask(final int _userID, final int _taskID) {
        ArrayList<Integer> completedTaskList = (ArrayList<Integer>) this.playerCompletedTaskListMap.get(_userID);
        return completedTaskList != null && completedTaskList.contains(_taskID);
    }

    public ArrayList<TaskGoodsLegacyInfo> processTaskAboutMonster(final Monster _monster, final HeroPlayer _killer) {
        if (_killer.getGroupID() <= 0) {
            return this.processTaskAboutMonsterPersonally(_monster, _killer);
        }
        Group group = GroupServiceImpl.getInstance().getGroup(_killer.getGroupID());
        if (group != null) {
            return this.processTaskAboutMonsterInGroup(_monster, group);
        }
        return null;
    }

    private ArrayList<TaskGoodsLegacyInfo> processTaskAboutMonsterInGroup(final Monster _monster, final Group _group) {
        ArrayList<TaskGoodsLegacyInfo> legacyTaskGoodsInfoList = null;
        ArrayList<HeroPlayer> playerList = _group.getValidatePlayerList(_monster.where().getID());
        ArrayList<TaskInstance> memberTaskList = null;
        for (final HeroPlayer player : playerList) {
            memberTaskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(player.getUserID());
            for (final TaskInstance task : memberTaskList) {
                if (_group != null) {
                    Task.ETaskDifficultyLevel difficultyLevel = task.getArchetype().getDifficultyLevel();
                    if ((Task.ETaskDifficultyLevel.EASY == difficultyLevel || Task.ETaskDifficultyLevel.DIFFICULT == difficultyLevel) && _group.getMemberNumber() > 10) {
                        continue;
                    }
                }
                if (!task.isCompleted()) {
                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();
                    for (final BaseTaskTarget target : targetList) {
                        if (!target.isCompleted() && ETastTargetType.KILL_MONSTER == target.getType() && ((TaskTargetKillMonster) target).monsterModelID.equals(_monster.getModelID())) {
                            ((TaskTargetKillMonster) target).numberChanged(1);
                            TaskDAO.updateTaskProgress(player.getUserID(), task);
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new RefreshTaskStatus(task.getArchetype().getID(), target.getID(), target.isCompleted(), target.getDescripiton(), task.isCompleted()));
                            if (!task.isCompleted()) {
                                break;
                            }
                            Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(task.getArchetype().getSubmitNpcID());
                            if (taskNpc != null && taskNpc.where() == player.where()) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), 2));
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
        ArrayList<MonsterTaskGoodsSetting> monsterTaskGoodsList = (ArrayList<MonsterTaskGoodsSetting>) this.monsterTaskGoodsDictory.get(_monster.getModelID());
        if (monsterTaskGoodsList != null) {
            for (final MonsterTaskGoodsSetting monsterTaskGoods : monsterTaskGoodsList) {
                TaskGoodsLegacyInfo legacyTaskGoodsInfo = null;
                boolean goodsChecked = false;
                Task taskModel = this.getTask(monsterTaskGoods.taskID);
                if (_group != null && _group.getMemberNumber() > 10 && taskModel.getDifficultyLevel() != Task.ETaskDifficultyLevel.NIGHTMARE) {
                    continue;
                }
                for (final HeroPlayer player2 : playerList) {
                    boolean playerChecked = false;
                    if (player2.isEnable() && player2.where() == _monster.where()) {
                        memberTaskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(player2.getUserID());
                        for (final TaskInstance task2 : memberTaskList) {
                            if (playerChecked) {
                                break;
                            }
                            if (task2.getArchetype() != taskModel || task2.isCompleted()) {
                                continue;
                            }
                            ArrayList<BaseTaskTarget> targetList2 = task2.getTargetList();
                            for (final BaseTaskTarget target2 : targetList2) {
                                if (ETastTargetType.GOODS == target2.getType() && !target2.isCompleted() && ((TaskTargetGoods) target2).goods.getID() == monsterTaskGoods.goodsID) {
                                    if (goodsChecked) {
                                        if (legacyTaskGoodsInfo != null) {
                                            legacyTaskGoodsInfo.addPicker(player2.getUserID());
                                        }
                                    } else {
                                        int number = monsterTaskGoods.getDropNumber();
                                        if (number > 0) {
                                            legacyTaskGoodsInfo = new TaskGoodsLegacyInfo(task2.getArchetype().getID(), monsterTaskGoods.goodsID, number);
                                            if (legacyTaskGoodsInfoList == null) {
                                                legacyTaskGoodsInfoList = new ArrayList<TaskGoodsLegacyInfo>();
                                            }
                                            legacyTaskGoodsInfo.addPicker(player2.getUserID());
                                            legacyTaskGoodsInfoList.add(legacyTaskGoodsInfo);
                                        }
                                        goodsChecked = true;
                                    }
                                    playerChecked = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return legacyTaskGoodsInfoList;
    }

    private ArrayList<TaskGoodsLegacyInfo> processTaskAboutMonsterPersonally(final Monster _monster, final HeroPlayer _player) {
        ArrayList<TaskGoodsLegacyInfo> legacyTaskGoodsInfoList = null;
        ArrayList<TaskInstance> taskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        if (taskList != null) {
            for (final TaskInstance task : taskList) {
                if (!task.isCompleted()) {
                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();
                    for (final BaseTaskTarget target : targetList) {
                        if (!target.isCompleted() && ETastTargetType.KILL_MONSTER == target.getType() && ((TaskTargetKillMonster) target).monsterModelID.equals(_monster.getModelID())) {
                            ((TaskTargetKillMonster) target).numberChanged(1);
                            TaskDAO.updateTaskProgress(_player.getUserID(), task);
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new RefreshTaskStatus(task.getArchetype().getID(), target.getID(), target.isCompleted(), target.getDescripiton(), task.isCompleted()));
                            if (!task.isCompleted()) {
                                break;
                            }
                            Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(task.getArchetype().getSubmitNpcID());
                            if (taskNpc != null && taskNpc.where() == _player.where()) {
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), 2));
                                break;
                            }
                            break;
                        }
                    }
                }
            }
            ArrayList<MonsterTaskGoodsSetting> monsterTaskGoodsList = (ArrayList<MonsterTaskGoodsSetting>) this.monsterTaskGoodsDictory.get(_monster.getModelID());
            if (monsterTaskGoodsList != null) {
                for (final MonsterTaskGoodsSetting monsterTaskGoods : monsterTaskGoodsList) {
                    TaskGoodsLegacyInfo legacyTaskGoodsInfo = null;
                    boolean goodsCheck = false;
                    for (final TaskInstance task2 : taskList) {
                        if (goodsCheck) {
                            break;
                        }
                        if (task2.getArchetype().getID() != monsterTaskGoods.taskID || task2.isCompleted()) {
                            continue;
                        }
                        ArrayList<BaseTaskTarget> targetList2 = task2.getTargetList();
                        for (final BaseTaskTarget target2 : targetList2) {
                            if (ETastTargetType.GOODS == target2.getType() && !target2.isCompleted() && ((TaskTargetGoods) target2).goods.getID() == monsterTaskGoods.goodsID) {
                                int number = monsterTaskGoods.getDropNumber();
                                if (number > 0) {
                                    legacyTaskGoodsInfo = new TaskGoodsLegacyInfo(task2.getArchetype().getID(), monsterTaskGoods.goodsID, number);
                                    if (legacyTaskGoodsInfoList == null) {
                                        legacyTaskGoodsInfoList = new ArrayList<TaskGoodsLegacyInfo>();
                                    }
                                    legacyTaskGoodsInfoList.add(legacyTaskGoodsInfo);
                                }
                                goodsCheck = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return legacyTaskGoodsInfoList;
    }

    public ArrayList<Task> getNotReceTaskList(final String _npcModelID, final HeroPlayer _player) {
        ArrayList<Task> npcTaskList = (ArrayList<Task>) this.npcTaskDictionary.get(_npcModelID);
        if (npcTaskList == null) {
            return null;
        }
        ArrayList<Task> notReceive = new ArrayList<Task>();
        for (final Task task : npcTaskList) {
            if (!this.canReceiveTask(_player, task)) {
                notReceive.add(task);
            }
        }
        if (notReceive.size() != 0) {
            return notReceive;
        }
        return null;
    }

    public ArrayList<Task> getReceiveableTaskList(final String _npcModelID, final HeroPlayer _player) {
        ArrayList<Task> npcTaskList = (ArrayList<Task>) this.npcTaskDictionary.get(_npcModelID);
        if (npcTaskList == null) {
            return null;
        }
        ArrayList<Task> canReceiveTaskList = new ArrayList<Task>();
        for (final Task task : npcTaskList) {
            TaskServiceImpl.log.debug(("task[" + task.getName() + "] type=" + task.getCondition().taskType));
            if (this.canReceiveTask(_player, task)) {
                canReceiveTaskList.add(task);
            }
        }
        if (canReceiveTaskList.size() != 0) {
            return canReceiveTaskList;
        }
        return null;
    }

    public ArrayList<Task> getReceiveableTaskList(final HeroPlayer _player) {
        ArrayList<TaskInstance> existsTaskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        ArrayList<Integer> completedTaskIDList = (ArrayList<Integer>) this.playerCompletedTaskListMap.get(_player.getUserID());
        Iterator<Task> taskList = this.taskDictionary.values().iterator();
        ArrayList<Task> canReceiveableTaskList = null;
        while (taskList.hasNext()) {
            boolean validateTask = true;
            Task task = taskList.next();
            if (!task.isRepeated() && completedTaskIDList.contains(task.getID())) {
                continue;
            }
            if (!task.getCondition().check(_player.getUserID(), _player.getVocation(), _player.getClan(), _player.getLevel(), completedTaskIDList) || _player.getLevel() - task.getCondition().level <= -1) {
                continue;
            }
            for (final TaskInstance taskIns : existsTaskList) {
                if (taskIns.getArchetype().getID() == task.getID()) {
                    validateTask = false;
                    break;
                }
            }
            if (!validateTask) {
                continue;
            }
            if (canReceiveableTaskList == null) {
                canReceiveableTaskList = new ArrayList<Task>();
            }
            canReceiveableTaskList.add(task);
        }
        if (canReceiveableTaskList != null && canReceiveableTaskList.size() > 1) {
            Collections.sort(canReceiveableTaskList, new TaskComparator());
        }
        ArrayList<Task> returnTasks = new ArrayList<Task>();
        if (canReceiveableTaskList != null && canReceiveableTaskList.size() > ((TaskConfig) this.config).can_receive_task_number) {
            for (int i = 0; i < 20; ++i) {
                Task t = canReceiveableTaskList.get(i);
                returnTasks.add(t);
            }
        } else {
            returnTasks = canReceiveableTaskList;
        }
        return returnTasks;
    }

    private ArrayList<Task> getSortTasks(final ArrayList<Task> _list) {
        Task[] tasks = new Task[_list.size()];
        for (int i = 0; i < _list.size(); ++i) {
            tasks[i] = _list.get(i);
        }
        Task k = null;
        for (int j = 0; j < tasks.length; ++j) {
            for (int l = j + 1; l < tasks.length; ++l) {
                if (tasks[j].getLevel() > tasks[l].getLevel()) {
                    k = tasks[j];
                    tasks[j] = tasks[l];
                    tasks[l] = k;
                }
            }
        }
        ArrayList<Task> taskList = new ArrayList<Task>();
        for (int m = tasks.length - 1; m > -1; --m) {
            taskList.add(tasks[m]);
        }
        return taskList;
    }

    public void receiveTask(final HeroPlayer _player, final int _taskID) {
        Task task = (Task) this.taskDictionary.get(_taskID);
        if (task != null) {
            if (task.isRepeated()) {
                TaskServiceImpl.log.debug((String.valueOf(_player.getName()) + " \u63a5\u6536\u5faa\u73af\u4efb\u52a1[" + task.getName() + "],receivedRepeateTaskTimes=" + _player.receivedRepeateTaskTimes));
                if (!_player.canReceiveRepeateTask()) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5bf9\u4e0d\u8d77\uff0c\u4eca\u65e5\u60a8\u7684\"\u5faa\u73af\u4efb\u52a1\"\u5df2\u63a5\u6536 " + _player.receivedRepeateTaskTimes + " \u6b21\uff0c\u5982\u679c\u60a8\u60f3\u7ee7\u7eed\u63a5\u6536\"\u5faa\u73af\u4efb\u52a1\",\u8bf7\u5230\u5546\u57ce\u8d2d\u4e70\"\u4efb\u52a1\u5237\u65b0\u5377\u8f74\"\u9053\u5177\u3002", (byte) 2, (byte) 1));
                    return;
                }
                ++_player.receivedRepeateTaskTimes;
                PlayerDAO.updateRepeateTask(_player);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NotifyPlayerReciveRepeateTaskTimes(_player));
            }
            ArrayList<TaskInstance> taskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
            synchronized (taskList) {
                for (final TaskInstance existsTask : taskList) {
                    if (existsTask.getArchetype().getID() == _taskID) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u6709\u8be5\u4efb\u52a1", (byte) 0));
                        // monitorexit(taskList)
                        return;
                    }
                }
                if (((ArrayList) this.playerCompletedTaskListMap.get(_player.getUserID())).contains(_taskID)) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u5b8c\u6210\u8be5\u4efb\u52a1", (byte) 0));
                    // monitorexit(taskList)
                    return;
                }
                if (task.getReceiveGoodsList() != null && task.getReceiveGoodsList().size() < _player.getInventory().getTaskToolBag().getEmptyGridNumber()) {
                    ArrayList<int[]> receiveGoodsList = task.getReceiveGoodsList();
                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();
                    for (final int[] goods : receiveGoodsList) {
                        GoodsServiceImpl.getInstance().addGoods2Package(_player, GoodsServiceImpl.getInstance().getGoodsByID(goods[0]), goods[1], CauseLog.TASKAWARD);
                        for (final BaseTaskTarget target : targetList) {
                            if (ETastTargetType.GOODS == target.getType() && ((TaskTargetGoods) target).goods.getID() == goods[0]) {
                                ((TaskTargetGoods) target).numberChanged(goods[1]);
                                break;
                            }
                        }
                    }
                }
                TaskDAO.insertNewTask(_player.getUserID(), task);
                TaskInstance taskInstance = new TaskInstance(task);
                taskList.add(taskInstance);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new TaskListChangerNotify((byte) 1, taskInstance));
                Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(task.getDistributeNpcModelID());
                if (taskNpc != null && taskNpc.where().getID() == _player.where().getID()) {
                    byte npcTaskMarks = this.getTaskMark(task.getDistributeNpcModelID(), _player);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), npcTaskMarks));
                }
                taskNpc = NotPlayerServiceImpl.getInstance().getNpc(task.getSubmitNpcID());
                if (taskInstance.isCompleted() && taskNpc != null && taskNpc.where() == _player.where()) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), 2));
                }
                if (_player.where().getTaskGearList() != null) {
                    ArrayList<TaskGear> gearList = _player.where().getTaskGearList();
                    if (gearList.size() > 0) {
                        for (final TaskGear gear : gearList) {
                            if (task.getID() == gear.getTaskIDAbout()) {
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeTaskGearStat(gear.getID(), gear.getCellY(), true));
                            }
                        }
                    }
                }
                if (_player.where().getGroundTaskGoodsList() != null) {
                    ArrayList<GroundTaskGoods> groundTaskGoodsList = _player.where().getGroundTaskGoodsList();
                    if (groundTaskGoodsList.size() > 0) {
                        for (final GroundTaskGoods taskGoods : groundTaskGoodsList) {
                            if (task.getID() == taskGoods.getTaskIDAbout()) {
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeTaskGearStat(taskGoods.getID(), taskGoods.getCellY(), true));
                            }
                        }
                    }
                }
            }
            // monitorexit(taskList)
        }
    }

    public void cancelTask(final HeroPlayer _player, final int _taskID) {
        ArrayList<TaskInstance> existsTaskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        for (final TaskInstance existsTask : existsTaskList) {
            if (existsTask.getArchetype().getID() == _taskID) {
                existsTaskList.remove(existsTask);
                TaskDAO.deleteTask(_player.getUserID(), _taskID);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new TaskListChangerNotify((byte) 3, existsTask));
                if (existsTask.getArchetype().getReceiveGoodsList() != null) {
                    ArrayList<int[]> receiveGoodsList = existsTask.getArchetype().getReceiveGoodsList();
                    for (final int[] goods : receiveGoodsList) {
                        try {
                            GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getTaskToolBag(), GoodsServiceImpl.getInstance().getGoodsByID(goods[0]), CauseLog.CANCELTASK);
                        } catch (BagException pe) {
                            pe.printStackTrace();
                        }
                    }
                }
                if (existsTask.getTargetList() != null) {
                    ArrayList<BaseTaskTarget> targetList = existsTask.getTargetList();
                    for (final BaseTaskTarget target : targetList) {
                        if (ETastTargetType.GOODS == target.getType()) {
                            try {
                                GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getTaskToolBag(), ((TaskTargetGoods) target).goods, CauseLog.CANCELTASK);
                                if (_player.where().getGroundTaskGoodsList().size() <= 0) {
                                    continue;
                                }
                                for (final GroundTaskGoods taskGoods : _player.where().getGroundTaskGoodsList()) {
                                    if (taskGoods.getTaskToolIDAbout() == ((TaskTargetGoods) target).goods.getID()) {
                                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeGroundTaskGoodsStat(taskGoods.getID(), taskGoods.getCellY(), false));
                                    }
                                }
                            } catch (BagException pe) {
                                pe.printStackTrace();
                            }
                        } else if (ETastTargetType.OPEN_GEAR == target.getType()) {
                            if (_player.where().getTaskGearList().size() <= 0) {
                                continue;
                            }
                            for (final TaskGear gear : _player.where().getTaskGearList()) {
                                if (gear.getModelID().equals(((TaskTargetOpenGear) target).gearModelID)) {
                                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeTaskGearStat(gear.getID(), gear.getCellY(), false));
                                    break;
                                }
                            }
                        } else {
                            if (ETastTargetType.ESCORT_NPC != target.getType()) {
                                continue;
                            }
                            synchronized (this.escortTaskExcuteList) {
                                int i = 0;
                                while (i < this.escortTaskExcuteList.size()) {
                                    EscortNpcTaskInfo info = (EscortNpcTaskInfo) this.escortTaskExcuteList.get(i);
                                    if (info.trigger == _player && _player.getEscortTarget() != null && _player.getEscortTarget().getModelID().equals(((TaskTargetEscortNpc) target).npcModelID)) {
                                        this.escortTaskExcuteList.remove(i);
                                        info.npc.stopFollowTask();
                                        _player.setEscortTarget(null);
                                        info.trigger = null;
                                        info.npc.setCellX(info.npc.getOrgX());
                                        info.npc.setCellY(info.npc.getOrgY());
                                        if (info.npc.where() != info.npc.getOrgMap()) {
                                            info.npc.gotoMap(info.npc.getOrgMap());
                                        } else {
                                            MapSynchronousInfoBroadcast.getInstance().put(info.npc.where(), new NpcResetNotify(info.npc.getID(), info.npc.getCellX(), info.npc.getCellY()), false, 0);
                                        }
                                        info.npc = null;
                                        if (info.spareTaskMemberList != null) {
                                            for (final HeroPlayer other : info.spareTaskMemberList) {
                                                ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new Warning("\u2018" + existsTask.getArchetype().getName() + "\u2019" + "\u4efb\u52a1\u5931\u8d25", (byte) 0));
                                            }
                                            info.spareTaskMemberList.clear();
                                            info.spareTaskMemberList = null;
                                            break;
                                        }
                                        break;
                                    } else {
                                        if (info.spareTaskMemberList != null && info.spareTaskMemberList.contains(_player)) {
                                            info.spareTaskMemberList.remove(_player);
                                            _player.setEscortTarget(null);
                                            break;
                                        }
                                        ++i;
                                    }
                                }
                            }
                            // monitorexit(this.escortTaskExcuteList)
                        }
                    }
                }
                Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(existsTask.getArchetype().getDistributeNpcModelID());
                if (taskNpc != null && taskNpc.where() == _player.where()) {
                    byte npcTaskMarks = this.getTaskMark(taskNpc.getModelID(), _player);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), npcTaskMarks));
                }
                if (existsTask.isCompleted()) {
                    taskNpc = NotPlayerServiceImpl.getInstance().getNpc(existsTask.getArchetype().getSubmitNpcID());
                    if (taskNpc != null && taskNpc.where() == _player.where()) {
                        byte npcTaskMarks = this.getTaskMark(taskNpc.getModelID(), _player);
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), npcTaskMarks));
                    }
                }
                return;
            }
        }
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4e0d\u5b58\u5728\u7684\u4efb\u52a1", (byte) 0));
    }

    public boolean transmitToTaskTarget(final HeroPlayer _player, final int _taskID, final int _targetID) {
        ArrayList<TaskInstance> existsTaskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        for (final TaskInstance existsTask : existsTaskList) {
            if (existsTask.getArchetype().getID() == _taskID && existsTask.getTargetList() != null) {
                ArrayList<BaseTaskTarget> targetList = existsTask.getTargetList();
                for (final BaseTaskTarget target : targetList) {
                    if (target.getID() == _targetID) {
                        short[] transmitMapInfo = target.getTransmitMapInfo();
                        if (target.getType() == ETastTargetType.FOUND_A_PATH || target.getType() == ETastTargetType.ESCORT_NPC) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8fd9\u79cd\u4efb\u52a1\u60a8\u8fd8\u662f\u4eb2\u81ea\u53bb\u4e00\u8d9f\u6bd4\u8f83\u597d", (byte) 0));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new SwitchMapFailNotify("\u8fd9\u79cd\u4efb\u52a1\u60a8\u8fd8\u662f\u4eb2\u81ea\u53bb\u4e00\u8d9f\u6bd4\u8f83\u597d"));
                            return false;
                        }
                        if (transmitMapInfo == null) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4e0d\u53ef\u4f20\u9001", (byte) 0));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new SwitchMapFailNotify("\u4e0d\u53ef\u4f20\u9001"));
                            return false;
                        }
                        Map currentMap = _player.where();
                        Map targetMap = MapServiceImpl.getInstance().getNormalMapByID(transmitMapInfo[0]);
                        if (targetMap == null || currentMap.getID() == targetMap.getID()) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u5728\u8fd9\u91cc\u4e86", (byte) 0));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new SwitchMapFailNotify("\u60a8\u5df2\u5728\u8fd9\u91cc\u4e86"));
                            return false;
                        }
                        _player.setCellX(transmitMapInfo[1]);
                        _player.setCellY(transmitMapInfo[2]);
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, targetMap, currentMap));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapGameObjectList(_player.getLoginInfo().clientType, targetMap));
                        _player.gotoMap(targetMap);
                        EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
                        Npc escortNpc = _player.getEscortTarget();
                        if (escortNpc != null) {
                            getInstance().endEscortNpcTask(_player, escortNpc);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean transmitToTaskNpc(final HeroPlayer _player, final int _taskID, final boolean _isReceiveTask) {
        Npc taskNpc = null;
        if (!_isReceiveTask) {
            ArrayList<TaskInstance> existsTaskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
            for (final TaskInstance existsTask : existsTaskList) {
                if (existsTask.getArchetype().getID() == _taskID) {
                    taskNpc = NotPlayerServiceImpl.getInstance().getNpc(existsTask.getArchetype().getSubmitNpcID());
                }
            }
        } else {
            taskNpc = NotPlayerServiceImpl.getInstance().getNpc(((Task) this.taskDictionary.get(_taskID)).getDistributeNpcModelID());
        }
        if (taskNpc == null) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4e0d\u53ef\u4f20\u9001", (byte) 0));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new SwitchMapFailNotify("\u4e0d\u53ef\u4f20\u9001"));
            return false;
        }
        if (EMapType.DUNGEON == taskNpc.getOrgMap().getMapType()) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u65e0\u6cd5\u4f20\u9001\u5230\u526f\u672c\u4e2d", (byte) 0));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new SwitchMapFailNotify("\u65e0\u6cd5\u4f20\u9001\u5230\u526f\u672c\u4e2d"));
            return false;
        }
        Map currentMap = _player.where();
        Map targetMap = taskNpc.where();
        if (targetMap == null || currentMap.getID() == targetMap.getID()) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u5728\u8fd9\u91cc\u4e86", (byte) 0));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new SwitchMapFailNotify("\u60a8\u5df2\u5728\u8fd9\u91cc\u4e86"));
            return false;
        }
        short x = 0;
        short y = 0;
        if (taskNpc.getOrgY() + 1 <= targetMap.getHeight() && targetMap.isRoad(taskNpc.getOrgX(), taskNpc.getOrgY() + 1)) {
            x = taskNpc.getOrgX();
            y = (short) (taskNpc.getOrgY() + 1);
        } else if (taskNpc.getOrgY() - 1 >= 0 && targetMap.isRoad(taskNpc.getOrgX(), taskNpc.getOrgY() - 1)) {
            x = taskNpc.getOrgX();
            y = (short) (taskNpc.getOrgY() - 1);
        } else if (taskNpc.getOrgX() + 1 <= targetMap.getWidth() && targetMap.isRoad(taskNpc.getOrgX() + 1, taskNpc.getOrgY())) {
            x = (short) (taskNpc.getOrgX() + 1);
            y = taskNpc.getOrgY();
        }
        _player.setCellX(x);
        _player.setCellY(y);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, targetMap, currentMap));
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapGameObjectList(_player.getLoginInfo().clientType, targetMap));
        _player.gotoMap(targetMap);
        EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
        Npc escortNpc = _player.getEscortTarget();
        if (escortNpc != null) {
            getInstance().endEscortNpcTask(_player, escortNpc);
        }
        return true;
    }

    public void groundTaskGoodsRebirth(final GroundTaskGoods _groundTaskGoods) {
        ME2ObjectList mapPlayerList = _groundTaskGoods.where().getPlayerList();
        if (mapPlayerList.size() > 0) {
            HeroPlayer player = null;
            for (int i = 0; i < mapPlayerList.size(); ++i) {
                player = (HeroPlayer) mapPlayerList.get(i);
                if (player.isEnable()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new GroundTaskGoodsEmergeNotify(player.getLoginInfo().clientType, _groundTaskGoods, getInstance().getGroundTaskGoodsOperateMark(player, _groundTaskGoods)));
                }
            }
            player = null;
        }
    }

    public void openGear(final HeroPlayer _player, final int _gearID) {
        ArrayList<TaskInstance> taskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        if (taskList != null && taskList.size() > 0) {
            TaskGear gear = NotPlayerServiceImpl.getInstance().getTaskGear(_gearID);
            for (final TaskInstance task : taskList) {
                if (!task.isCompleted()) {
                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();
                    for (final BaseTaskTarget target : targetList) {
                        if (!target.isCompleted() && ETastTargetType.OPEN_GEAR == target.getType() && ((TaskTargetOpenGear) target).gearModelID.equals(gear.getModelID())) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeTaskGearStat(_gearID, gear.getCellY(), false));
                            ((TaskTargetOpenGear) target).complete();
                            TaskDAO.updateTaskProgress(_player.getUserID(), task);
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new RefreshTaskStatus(task.getArchetype().getID(), target.getID(), target.isCompleted(), target.getDescripiton(), task.isCompleted()));
                            if (!task.isCompleted()) {
                                break;
                            }
                            Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(task.getArchetype().getSubmitNpcID());
                            if (taskNpc != null && taskNpc.where() == _player.where()) {
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), 2));
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public void pickGroundTaskGoods(final HeroPlayer _player, final int _groundTaskGoodsID) {
        ArrayList<TaskInstance> taskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        if (taskList != null && taskList.size() > 0) {
            GroundTaskGoods groundTaskGoods = NotPlayerServiceImpl.getInstance().getGroundTaskGoodsTable(_groundTaskGoodsID);
            if (groundTaskGoods != null) {
                for (final TaskInstance task : taskList) {
                    if (!task.isCompleted() && task.getArchetype().getID() == groundTaskGoods.getTaskIDAbout()) {
                        ArrayList<BaseTaskTarget> targetList = task.getTargetList();
                        for (final BaseTaskTarget target : targetList) {
                            if (!target.isCompleted() && ETastTargetType.GOODS == target.getType() && ((TaskTargetGoods) target).goods.getID() == groundTaskGoods.getTaskToolIDAbout()) {
                                if (GoodsServiceImpl.getInstance().addGoods2Package(_player, ((TaskTargetGoods) target).goods, 1, CauseLog.TASKAWARD) == null) {
                                    break;
                                }
                                ((TaskTargetGoods) target).numberChanged(1);
                                NotPlayerServiceImpl.getInstance().groundTaskGoodsBePicked(groundTaskGoods);
                                TaskDAO.updateTaskProgress(_player.getUserID(), task);
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new RefreshTaskStatus(task.getArchetype().getID(), target.getID(), target.isCompleted(), target.getDescripiton(), task.isCompleted()));
                                if (!target.isCompleted()) {
                                    break;
                                }
                                ArrayList<GroundTaskGoods> groundTaskGoodsList = _player.where().getGroundTaskGoodsList();
                                if (groundTaskGoodsList.size() > 0) {
                                    for (int i = 0; i < groundTaskGoodsList.size(); ++i) {
                                        GroundTaskGoods other = groundTaskGoodsList.get(i);
                                        if (other.getModelID().equals(groundTaskGoods.getModelID())) {
                                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeGroundTaskGoodsStat(other.getID(), other.getCellY(), false));
                                        }
                                    }
                                }
                                if (!task.isCompleted()) {
                                    break;
                                }
                                Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(task.getArchetype().getSubmitNpcID());
                                if (taskNpc != null && taskNpc.where() == _player.where()) {
                                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), 2));
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void submitTask(final HeroPlayer _player, final int _taskID, final int _goodsIDByUser) {
        if (((ArrayList) this.playerCompletedTaskListMap.get(_player.getUserID())).contains(_taskID)) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u5b8c\u6210\u8be5\u4efb\u52a1", (byte) 0));
            return;
        }
        ArrayList<TaskInstance> existsTaskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        synchronized (existsTaskList) {
            for (int i = 0; i < existsTaskList.size(); ++i) {
                TaskInstance existsTask = existsTaskList.get(i);
                if (existsTask.getArchetype().getID() == _taskID && existsTask.isCompleted()) {
                    if (existsTask.getTargetList() != null) {
                        ArrayList<BaseTaskTarget> targetList = existsTask.getTargetList();
                        for (final BaseTaskTarget target : targetList) {
                            if (ETastTargetType.GOODS == target.getType()) {
                                try {
                                    GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getTaskToolBag(), ((TaskTargetGoods) target).goods, CauseLog.SUBMITTASK);
                                } catch (BagException pe) {
                                    pe.printStackTrace();
                                }
                            }
                        }
                    }
                    Award award = existsTask.getArchetype().getAward();
                    int goodsID = award.selectGoodsVerify(_goodsIDByUser);
                    if (goodsID > 0) {
                        Goods awardGoods = GoodsServiceImpl.getInstance().getGoodsByID(goodsID);
                        if (awardGoods != null) {
                            int goodsNumber = 1;
                            if (EGoodsType.EQUIPMENT != awardGoods.getGoodsType()) {
                                goodsNumber = award.getOptionalGoodsNumber(awardGoods);
                            }
                            if (goodsNumber != 0) {
                                GoodsServiceImpl.getInstance().addGoods2PackageByTask(_player, awardGoods, goodsNumber, CauseLog.TASKAWARD);
                            }
                        }
                    }
                    if (award.getBoundGoodsList() != null) {
                        ArrayList<Award.AwardGoodsUnit> boundBoundGoodsList = award.getBoundGoodsList();
                        for (final Award.AwardGoodsUnit awardGoodsUnit : boundBoundGoodsList) {
                            GoodsServiceImpl.getInstance().addGoods2PackageByTask(_player, awardGoodsUnit.goods, awardGoodsUnit.number, CauseLog.TASKAWARD);
                        }
                    }
                    existsTaskList.remove(i);
                    if (!existsTask.getArchetype().isRepeated()) {
                        ((ArrayList) this.playerCompletedTaskListMap.get(_player.getUserID())).add(_taskID);
                    }
                    TaskDAO.completeTask(_player.getUserID(), _taskID, existsTask.getArchetype().isRepeated());
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new TaskListChangerNotify((byte) 2, existsTask));
                    PlayerServiceImpl.getInstance().addMoney(_player, award.money, 1.0f, 2, "\u4efb\u52a1\u5956\u52b1");
                    if (((TaskConfig) this.config).is_use_push) {
                        Push push = existsTask.getArchetype().getTaskPush();
                        if (push != null && !((TaskConfig) this.config).confine_publisher_list.contains(_player.getLoginInfo().publisher)) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(push.commPushContent, (byte) 3, (byte) 2, push.id, push.time));
                        }
                    }
                    LogServiceImpl.getInstance().taskFinished(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), _taskID, existsTask.getArchetype().getName());
                    if (award.mapID != -1 && award.mapX != -1 && award.mapY != -1) {
                        Map currentMap = _player.where();
                        Map targetMap = MapServiceImpl.getInstance().getNormalMapByID(award.mapID);
                        _player.setCellX(award.mapX);
                        _player.setCellY(award.mapY);
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, targetMap, currentMap));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapGameObjectList(_player.getLoginInfo().clientType, targetMap));
                        _player.gotoMap(targetMap);
                        EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
                    }
                    PlayerServiceImpl.getInstance().addExperience(_player, CEService.taskExperience(_player.getLevel(), existsTask.getArchetype().getLevel(), award.experience), 1.0f, 2);
                    this.notifyMapNpcTaskMark(_player, _player.where());
                    // monitorexit(existsTaskList)
                    return;
                }
            }
        }
        // monitorexit(existsTaskList)
    }

    public void addTaskGoods(final HeroPlayer _player, final int _taskID, final Goods _taskGoods, final int _number) {
        ArrayList<TaskInstance> playerList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        for (final TaskInstance taskInstance : playerList) {
            if (!taskInstance.isCompleted() && taskInstance.getArchetype().getID() == _taskID) {
                ArrayList<BaseTaskTarget> targetList = taskInstance.getTargetList();
                for (final BaseTaskTarget target : targetList) {
                    if (!target.isCompleted() && ETastTargetType.GOODS == target.getType() && ((TaskTargetGoods) target).goods == _taskGoods) {
                        ((TaskTargetGoods) target).numberChanged(_number);
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new RefreshTaskStatus(taskInstance.getArchetype().getID(), target.getID(), target.isCompleted(), target.getDescripiton(), taskInstance.isCompleted()));
                        if (!taskInstance.isCompleted()) {
                            break;
                        }
                        Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(taskInstance.getArchetype().getSubmitNpcID());
                        if (taskNpc != null && taskNpc.where() == _player.where()) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), 2));
                            break;
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    public void addTaskGoods(final HeroPlayer _player, final int _taskGoodsID, final int _number) {
        ArrayList<TaskInstance> playerList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        for (final TaskInstance taskInstance : playerList) {
            if (!taskInstance.isCompleted()) {
                ArrayList<BaseTaskTarget> targetList = taskInstance.getTargetList();
                for (final BaseTaskTarget target : targetList) {
                    if (!target.isCompleted() && ETastTargetType.GOODS == target.getType() && ((TaskTargetGoods) target).goods.getID() == _taskGoodsID) {
                        ((TaskTargetGoods) target).numberChanged(_number);
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new RefreshTaskStatus(taskInstance.getArchetype().getID(), target.getID(), target.isCompleted(), target.getDescripiton(), taskInstance.isCompleted()));
                        if (taskInstance.isCompleted()) {
                            Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(taskInstance.getArchetype().getSubmitNpcID());
                            if (taskNpc != null && taskNpc.where() == _player.where()) {
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), 2));
                            }
                        }
                    }
                }
            }
        }
    }

    public void reduceTaskGoods(final HeroPlayer _player, final int _taskToolID) {
        ArrayList<TaskInstance> playerList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        for (final TaskInstance taskInstance : playerList) {
            ArrayList<BaseTaskTarget> targetList = taskInstance.getTargetList();
            for (final BaseTaskTarget target : targetList) {
                if (ETastTargetType.GOODS == target.getType() && ((TaskTargetGoods) target).goods.getID() == _taskToolID) {
                    if (target.isCompleted()) {
                        ArrayList<GroundTaskGoods> groundTaskGoodsList = _player.where().getGroundTaskGoodsList();
                        if (groundTaskGoodsList.size() > 0) {
                            for (int i = 0; i < groundTaskGoodsList.size(); ++i) {
                                GroundTaskGoods other = groundTaskGoodsList.get(i);
                                if (other.getTaskToolIDAbout() == _taskToolID) {
                                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeGroundTaskGoodsStat(other.getID(), other.getCellY(), true));
                                }
                            }
                        }
                    }
                    if (taskInstance.isCompleted()) {
                        Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(taskInstance.getArchetype().getSubmitNpcID());
                        if (taskNpc != null && taskNpc.where() == _player.where()) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), 0));
                        }
                    }
                    ((TaskTargetGoods) target).numberChanged(-((TaskTargetGoods) target).number);
                    break;
                }
            }
        }
    }

    private byte getTaskMark(final String _npcModelID, final HeroPlayer _player) {
        ArrayList<TaskInstance> playerList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        for (final TaskInstance taskInstance : playerList) {
            if (taskInstance.isCompleted() && taskInstance.getArchetype().getSubmitNpcID().equals(_npcModelID)) {
                return 2;
            }
        }
        ArrayList<Task> taskList = getInstance().getReceiveableTaskList(_npcModelID, _player);
        if (taskList != null) {
            return 1;
        }
        for (final TaskInstance taskInstance2 : playerList) {
            if (!taskInstance2.isCompleted() && taskInstance2.getArchetype().getSubmitNpcID().equals(_npcModelID)) {
                return 3;
            }
        }
        return 0;
    }

    public void notifyMapNpcTaskMark(final HeroPlayer _player, final Map _map) {
        if (_map.getNpcList().size() > 0) {
            ArrayList<Integer> taskMarkList = new ArrayList<Integer>();
            for (int i = 0; i < _map.getNpcList().size(); ++i) {
                Npc npc = (Npc) _map.getNpcList().get(i);
                byte npcTaskMarks = this.getTaskMark(npc.getModelID(), _player);
                taskMarkList.add(npc.getID());
                taskMarkList.add((int) npcTaskMarks);
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NotifyMapNpcTaskMark(taskMarkList));
        }
    }

    public void notifyMapGearOperateMark(final HeroPlayer _player, final Map _map) {
        ArrayList<TaskGear> gearList = _map.getTaskGearList();
        if (gearList.size() > 0) {
            ArrayList<TaskInstance> taskList = getInstance().getTaskList(_player.getUserID());
            if (taskList.size() > 0) {
                for (final TaskGear gear : gearList) {
                    for (final TaskInstance task : taskList) {
                        if (task.getArchetype().getID() == gear.getTaskIDAbout() && !task.isCompleted()) {
                            ArrayList<BaseTaskTarget> targetList = task.getTargetList();
                            for (final BaseTaskTarget target : targetList) {
                                if (ETastTargetType.OPEN_GEAR == target.getType() && ((TaskTargetOpenGear) target).gearModelID.equals(gear.getModelID())) {
                                    if (target.isCompleted()) {
                                        return;
                                    }
                                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeTaskGearStat(gear.getID(), gear.getCellY(), true));
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public void notifyGroundTaskGoodsOperateMark(final HeroPlayer _player, final Map _map) {
        ArrayList<GroundTaskGoods> groundTaskGoodsList = _map.getGroundTaskGoodsList();
        if (groundTaskGoodsList.size() > 0) {
            ArrayList<TaskInstance> taskList = getInstance().getTaskList(_player.getUserID());
            if (taskList.size() > 0) {
                for (final GroundTaskGoods taskGoods : groundTaskGoodsList) {
                    for (final TaskInstance task : taskList) {
                        if (task.getArchetype().getID() == taskGoods.getTaskIDAbout() && !task.isCompleted()) {
                            ArrayList<BaseTaskTarget> targetList = task.getTargetList();
                            for (final BaseTaskTarget target : targetList) {
                                if (ETastTargetType.GOODS == target.getType() && ((TaskTargetGoods) target).goods.getID() == taskGoods.getTaskToolIDAbout()) {
                                    if (target.isCompleted()) {
                                        return;
                                    }
                                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ChangeGroundTaskGoodsStat(taskGoods.getID(), taskGoods.getCellY(), true));
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public boolean getGroundTaskGoodsOperateMark(final HeroPlayer _player, final GroundTaskGoods _groundTaskGoods) {
        ArrayList<TaskInstance> taskList = getInstance().getTaskList(_player.getUserID());
        if (taskList.size() > 0) {
            for (final TaskInstance task : taskList) {
                if (!task.isCompleted()) {
                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();
                    for (final BaseTaskTarget target : targetList) {
                        if (ETastTargetType.GOODS == target.getType() && ((TaskTargetGoods) target).goods.getID() == _groundTaskGoods.getTaskToolIDAbout()) {
                            return !target.isCompleted();
                        }
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<TaskInstance> getTaskList(final int _userID) {
        return (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_userID);
    }

    public Push getTaskPush(final int _pushID) {
        return (Push) this.pushDataTable.get(_pushID);
    }

    public void asynTaskPushItem(final String _transID, final boolean _result) {
        int userID = ((Integer[]) this.pushPlayerGoods.get(_transID))[0];
        int pushID = ((Integer[]) this.pushPlayerGoods.get(_transID))[1];
        int pushType = ((Integer[]) this.pushPlayerGoods.get(_transID))[2];
        Push push = (Push) this.pushDataTable.get(pushID);
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(userID);
        boolean isCpmpel = false;
        if (push != null && player != null && this.getEmptyGrid(push.goodsID, player)) {
            int price = push.point / ShareServiceImpl.getInstance().getConfig().getFeePointConvert();
            if (getInstance().getConfig().is_proxy_compel_give && pushType == 2) {
                isCpmpel = true;
            }
            if (getInstance().getConfig().is_sms_compel_give && pushType == 1) {
                isCpmpel = true;
            }
            if (_result || isCpmpel) {
                if (player.getInventory().getSpecialGoodsBag().getEmptyGridNumber() > 0) {
                    GoodsServiceImpl.getInstance().addGoods2Package(player, push.goodsID, 1, CauseLog.TASKPUSH);
                } else {
                    LogServiceImpl.getInstance().taskPushOption(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), pushID, 9, "\u7528\u6237\u70b9\u6570\u5145\u8db3,\u4f46\u662f\u6263\u70b9\u5931\u8d25.\u4efb\u52a1\u8ba1\u8d39\u672a\u6210\u529f", price);
                }
                if (_result) {
                    LogServiceImpl.getInstance().taskPushOption(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), pushID, 5, "\u8ba1\u8d39\u6210\u529f,\u4e0b\u53d1\u88c5\u5907", price);
                } else if (isCpmpel) {
                    LogServiceImpl.getInstance().taskPushOption(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), pushID, 7, "\u8ba1\u8d39\u5931\u8d25,\u5f3a\u5236\u4e0b\u53d1\u88c5\u5907", price);
                }
            } else {
                if (pushType != 1) {
                }
                LogServiceImpl.getInstance().taskPushOption(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), pushID, 8, "\u8ba1\u8d39\u5931\u8d25,\u4e14\u672a\u4e0b\u53d1\u88c5\u5907", price);
            }
        }
        this.pushPlayerGoods.remove(_transID);
    }

    private boolean getEmptyGrid(final int _goodsID, final HeroPlayer _player) {
        boolean result = false;
        String bagNameString = "";
        if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.EQUIPMENT) {
            int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
            bagNameString = EGoodsType.EQUIPMENT.getDescription();
            if (empty > 0) {
                result = true;
            }
        } else if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.MATERIAL) {
            int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
            bagNameString = EGoodsType.MATERIAL.getDescription();
            if (empty > 0) {
                result = true;
            }
        } else if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.MEDICAMENT) {
            int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
            bagNameString = EGoodsType.MEDICAMENT.getDescription();
            if (empty > 0) {
                result = true;
            }
        } else if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.PET) {
            int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
            bagNameString = EGoodsType.PET.getDescription();
            if (empty > 0) {
                result = true;
            }
        } else if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.PET_GOODS) {
            int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
            bagNameString = EGoodsType.PET.getDescription();
            if (empty > 0) {
                result = true;
            }
        } else if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.SPECIAL_GOODS) {
            int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
            bagNameString = EGoodsType.SPECIAL_GOODS.getDescription();
            if (empty > 0) {
                result = true;
            }
        } else if (GoodsContents.getGoodsType(_goodsID) == EGoodsType.TASK_TOOL) {
            int empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
            bagNameString = EGoodsType.SPECIAL_GOODS.getDescription();
            if (empty > 0) {
                result = true;
            }
        }
        if (!result) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u975e\u5e38\u9057\u61be\uff0c\u60a8%fn\u80cc\u5305\u5185\u5df2\u6ee1\uff0c\u4e0d\u80fd\u63a5\u6536\u6b64\u6b21\u63a8\u8350\u7684\u6781\u54c1\u88c5\u5907".replaceAll("%fn", bagNameString)));
        }
        return result;
    }

    public void confirmTaskPush(final HeroPlayer _player, final int _pushID, final int _pushType, final String _productID, final int _proxyID, final String _tranID, final String _mobileUserID) {
        Push push = (Push) this.pushDataTable.get(_pushID);
        if (push != null && this.getEmptyGrid(push.goodsID, _player)) {
            int price = push.point / ShareServiceImpl.getInstance().getConfig().getFeePointConvert();
            if (_pushType == 0) {
                if (push.point <= _player.getChargeInfo().pointAmount) {
                    Goods goods = GoodsContents.getGoods(push.goodsID);
                    boolean reduce = ChargeServiceImpl.getInstance().reducePoint(_player, push.point, push.goodsID, goods.getName(), 1, ServiceType.BUY_TOOLS);
                    if (reduce) {
                        if (_player.getInventory().getSpecialGoodsBag().getEmptyGridNumber() > 0) {
                            GoodsServiceImpl.getInstance().addGoods2Package(_player, push.goodsID, 1, CauseLog.TASKPUSH);
                            LogServiceImpl.getInstance().taskPushOption(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), _pushID, 5, "\u7528\u6237\u70b9\u6570\u5145\u8db3,\u5df2\u6263\u9664\u70b9\u6570.\u88c5\u5907\u5df2\u4e0b\u53d1", price);
                        } else {
                            LogServiceImpl.getInstance().taskPushOption(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), _pushID, 9, "\u7528\u6237\u70b9\u6570\u5145\u8db3,\u5df2\u6263\u9664\u70b9\u6570.\u4f46\u662f\u5305\u88f9\u5df2\u6ee1,\u6dfb\u52a0\u88c5\u5907\u5931\u8d25", price);
                        }
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25"));
                        LogServiceImpl.getInstance().taskPushOption(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), _pushID, 6, "\u7528\u6237\u70b9\u6570\u5145\u8db3,\u4f46\u6263\u70b9\u5931\u8d25.\u4efb\u52a1\u8ba1\u8d39\u672a\u6210\u529f", price);
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6e38\u620f\u70b9\u6570\u4e0d\u8db3\uff0c\u8bf7\u5230\u5546\u57ce\u5145\u503c\uff0c\u53ef\u4eab\u66f4\u591a\u4f18\u60e0"));
                }
            } else if (_pushType == 2) {
                Integer[] data = {_player.getUserID(), _pushID, _pushType};
                this.pushPlayerGoods.put(_tranID, data);
                String ngUrlID = "new type, please call gameserver developer";
                if (_proxyID == 1) {
                    ngUrlID = "hero";
                } else if (_proxyID == 0) {
                    ngUrlID = "jiutian";
                }
                boolean[] result = ChargeServiceImpl.getInstance().ngBuyMallTools(ngUrlID, _player.getLoginInfo().accountID, _productID, _mobileUserID, _player.getUserID(), _player.getLoginInfo().publisher, ServiceType.FEE, 1, price);
                this.asynTaskPushItem(_tranID, result[0]);
            } else if (_pushType == 1) {
                Integer[] data = {_player.getUserID(), _pushID, _pushType};
                this.pushPlayerGoods.put(_tranID, data);
            }
        }
    }

    public void enterTaskPush(final HeroPlayer _player, final int _pushID, final int _pushType, final boolean _isNext) {
        Push push = (Push) this.pushDataTable.get(_pushID);
        if (push != null && this.getEmptyGrid(push.goodsID, _player)) {
            String confirmContent = "";
            if (_pushType == 0) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(push.commConfirmContent, (byte) 3, (byte) 3, push.id, 0));
            } else {
                int index = 0;
                for (int i = 0; i < push.pushType.length; ++i) {
                    if (push.pushType[i] == _pushType) {
                        index = i;
                        break;
                    }
                }
                if (_isNext) {
                    confirmContent = push.pushContent[index];
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(confirmContent, (byte) 3, (byte) 4, push.id, 0));
                } else {
                    confirmContent = push.limitContent[index];
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(confirmContent, (byte) 1));
                }
            }
        }
    }

    public void loadPlayerTaskList(final HeroPlayer _player) {
        TaskDAO.loadTask(_player, (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID()), (ArrayList<Integer>) this.playerCompletedTaskListMap.get(_player.getUserID()));
    }

    public void sendPlayerTaskList(final HeroPlayer _player) {
        ArrayList<TaskInstance> taskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_player.getUserID());
        if (taskList != null && taskList.size() > 0) {
            Collections.sort(taskList, new TaskInstanceComparator());
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseTaskItems(taskList));
        }
    }

    public ArrayList<Integer> getCompltedTaskIDList(final int _userID) {
        return (ArrayList<Integer>) this.playerCompletedTaskListMap.get(_userID);
    }

    public void beginEscortNpcTask(final HeroPlayer _player, final Task _task, final TaskTargetEscortNpc _escortTarget, final Npc _npc) {
        synchronized (this.escortTaskExcuteList) {
            _npc.beginFollow(_player);
            _player.setEscortTarget(_npc);
            MapSynchronousInfoBroadcast.getInstance().put(_npc.where(), new ChangeNpcStat(_npc.getID(), _npc.canInteract()), false, 0);
            EscortNpcTaskInfo info = new EscortNpcTaskInfo();
            info.task = _task;
            info.escortTarget = _escortTarget;
            info.trigger = _player;
            info.npc = _npc;
            info.traceTime = _escortTarget.countTime;
            this.escortTaskExcuteList.add(info);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u2018" + info.task.getName() + "\u2019\u4efb\u52a1\u5f00\u59cb", (byte) 0));
            int groupID = _player.getGroupID();
            if (groupID > 0) {
                Group group = GroupServiceImpl.getInstance().getGroup(groupID);
                ArrayList<HeroPlayer> playerList = group.getPlayerList();
                for (final HeroPlayer other : playerList) {
                    if (other != _player) {
                        ArrayList<TaskInstance> taskList = this.getTaskList(other.getUserID());
                        for (final TaskInstance task : taskList) {
                            if (task.getArchetype() == _task) {
                                if (task.isCompleted() || _npc.where() != other.where()) {
                                    break;
                                }
                                boolean inDistance = 225 >= (other.getCellX() - _npc.getCellX()) * (other.getCellX() - _npc.getCellX()) + (other.getCellY() - _npc.getCellY()) * (other.getCellY() - _npc.getCellY());
                                if (inDistance) {
                                    info.addSpareTaskMember(other);
                                    ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new Warning("\u2018" + info.task.getName() + "\u2019\u4efb\u52a1\u5f00\u59cb", (byte) 0));
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        // monitorexit(this.escortTaskExcuteList)
    }

    public void endEscortNpcTask(final HeroPlayer _player, final Npc _targetNpc) {
        synchronized (this.escortTaskExcuteList) {
            int i = 0;
            while (i < this.escortTaskExcuteList.size()) {
                EscortNpcTaskInfo info = (EscortNpcTaskInfo) this.escortTaskExcuteList.get(i);
                if (info.npc == _targetNpc) {
                    if (_player == info.trigger) {
                        this.escortTaskExcuteList.remove(i);
                        ResponseMessageQueue.getInstance().put(info.trigger.getMsgQueueIndex(), new Warning("\u2018" + info.task.getName() + "\u2019\u4efb\u52a1\u5931\u8d25", (byte) 0));
                        if (info.getSpareTaskMemberList() != null && info.getSpareTaskMemberList().size() > 0) {
                            for (final HeroPlayer member : info.getSpareTaskMemberList()) {
                                ResponseMessageQueue.getInstance().put(member.getMsgQueueIndex(), new Warning("\u2018" + info.task.getName() + "\u2019\u4efb\u52a1\u5931\u8d25", (byte) 0));
                            }
                        }
                        info.npc.stopFollowTask();
                        info.trigger.setEscortTarget(null);
                        info.npc.setCellX(info.npc.getOrgX());
                        info.npc.setCellY(info.npc.getOrgY());
                        if (info.npc.where() != info.npc.getOrgMap()) {
                            info.npc.gotoMap(info.npc.getOrgMap());
                        } else {
                            MapSynchronousInfoBroadcast.getInstance().put(info.npc.where(), new NpcResetNotify(info.npc.getID(), info.npc.getCellX(), info.npc.getCellY()), false, 0);
                        }
                        info.trigger = null;
                        info.spareTaskMemberList.clear();
                        break;
                    }
                    if (info.spareTaskMemberList != null && info.spareTaskMemberList.contains(_player)) {
                        info.spareTaskMemberList.remove(_player);
                        _player.setEscortTarget(null);
                        break;
                    }
                    break;
                } else {
                    ++i;
                }
            }
        }
        // monitorexit(this.escortTaskExcuteList)
    }

    private void excuteEscortNpcTask() {
        int i = 0;
        while (i < this.escortTaskExcuteList.size()) {
            EscortNpcTaskInfo info = (EscortNpcTaskInfo) this.escortTaskExcuteList.get(i);
            short npcLocationX = info.npc.getCellX();
            short npcLocationY = info.npc.getCellY();
            int npcWhereMapID = info.npc.where().getID();
            if ((!info.trigger.isEnable() || info.trigger.isDead()) && this.endEscort(info)) {
                this.escortTaskExcuteList.remove(i);
                ResponseMessageQueue.getInstance().put(info.trigger.getMsgQueueIndex(), new Warning("\u2018" + info.task.getName() + "\u2019\u4efb\u52a1\u5931\u8d25", (byte) 0));
                if (info.getSpareTaskMemberList() != null && info.getSpareTaskMemberList().size() > 0) {
                    for (final HeroPlayer member : info.getSpareTaskMemberList()) {
                        if (member.isEnable()) {
                            ResponseMessageQueue.getInstance().put(member.getMsgQueueIndex(), new Warning("\u2018" + info.task.getName() + "\u2019\u4efb\u52a1\u5931\u8d25", (byte) 0));
                        }
                    }
                }
                info.trigger = null;
                info.spareTaskMemberList.clear();
            } else {
                if (npcWhereMapID == info.escortTarget.mapID && npcLocationX >= info.escortTarget.x - info.escortTarget.mistakeRang && npcLocationX <= info.escortTarget.x + info.escortTarget.mistakeRang && npcLocationY >= info.escortTarget.y - info.escortTarget.mistakeRang && npcLocationY <= info.escortTarget.y + info.escortTarget.mistakeRang) {
                    this.reachDestination(info);
                    if (this.endEscort(info)) {
                        this.escortTaskExcuteList.remove(i);
                        info.trigger = null;
                        info.spareTaskMemberList.clear();
                        continue;
                    }
                }
                if (info.traceTime <= 0 && this.endEscort(info)) {
                    this.escortTaskExcuteList.remove(i);
                    ResponseMessageQueue.getInstance().put(info.trigger.getMsgQueueIndex(), new Warning("\u2018" + info.task.getName() + "\u2019\u4efb\u52a1\u5931\u8d25", (byte) 0));
                    if (info.getSpareTaskMemberList() != null && info.getSpareTaskMemberList().size() > 0) {
                        for (final HeroPlayer member : info.getSpareTaskMemberList()) {
                            if (member.isEnable()) {
                                ResponseMessageQueue.getInstance().put(member.getMsgQueueIndex(), new Warning("\u2018" + info.task.getName() + "\u2019\u4efb\u52a1\u5931\u8d25", (byte) 0));
                            }
                        }
                    }
                    info.trigger = null;
                    info.spareTaskMemberList.clear();
                } else {
                    boolean inDistance = 225 < (info.trigger.getCellX() - npcLocationX) * (info.trigger.getCellX() - npcLocationX) + (info.trigger.getCellY() - npcLocationY) * (info.trigger.getCellY() - npcLocationY);
                    if ((npcWhereMapID != info.trigger.where().getID() || inDistance) && this.endEscort(info)) {
                        this.escortTaskExcuteList.remove(i);
                        ResponseMessageQueue.getInstance().put(info.trigger.getMsgQueueIndex(), new Warning("\u2018" + info.task.getName() + "\u2019\u4efb\u52a1\u5931\u8d25", (byte) 0));
                        if (info.getSpareTaskMemberList() != null && info.getSpareTaskMemberList().size() > 0) {
                            for (final HeroPlayer member2 : info.getSpareTaskMemberList()) {
                                ResponseMessageQueue.getInstance().put(member2.getMsgQueueIndex(), new Warning("\u2018" + info.task.getName() + "\u2019\u4efb\u52a1\u5931\u8d25", (byte) 0));
                            }
                        }
                        info.trigger = null;
                        info.spareTaskMemberList.clear();
                    } else {
                        EscortNpcTaskInfo escortNpcTaskInfo = info;
                        escortNpcTaskInfo.traceTime -= 5000;
                        ++i;
                    }
                }
            }
        }
    }

    private boolean endEscort(final EscortNpcTaskInfo _taskInfo) {
        _taskInfo.npc.stopFollowTask();
        _taskInfo.trigger.setEscortTarget(null);
        _taskInfo.npc.setCellX(_taskInfo.npc.getOrgX());
        _taskInfo.npc.setCellY(_taskInfo.npc.getOrgY());
        if (_taskInfo.npc.where() != _taskInfo.npc.getOrgMap()) {
            _taskInfo.npc.gotoMap(_taskInfo.npc.getOrgMap());
        } else {
            MapSynchronousInfoBroadcast.getInstance().put(_taskInfo.npc.where(), new NpcResetNotify(_taskInfo.npc.getID(), _taskInfo.npc.getCellX(), _taskInfo.npc.getCellY()), false, 0);
        }
        return true;
    }

    private void excuteFoundAPathTask() {
        for (final FoundAPathInfo info : this.foundAPathTaskList) {
            ArrayList<HeroPlayer> playerList = MapServiceImpl.getInstance().getAllPlayerListInCircle(info.map, info.x, info.y, info.mistakeRang);
            if (playerList != null && playerList.size() > 0) {
                for (final HeroPlayer player : playerList) {
                    ArrayList<TaskInstance> taskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(player.getUserID());
                    for (final TaskInstance task : taskList) {
                        if (task.getArchetype().getID() == info.taskID) {
                            if (!task.isCompleted()) {
                                ArrayList<BaseTaskTarget> targetList = task.getTargetList();
                                for (final BaseTaskTarget target : targetList) {
                                    if (ETastTargetType.FOUND_A_PATH == target.getType() && !target.isCompleted() && target.getID() == info.tastTargetID) {
                                        ((TaskTargetFoundAPath) target).complete();
                                        TaskDAO.updateTaskProgress(player.getUserID(), task);
                                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new RefreshTaskStatus(task.getArchetype().getID(), target.getID(), target.isCompleted(), target.getDescripiton(), task.isCompleted()));
                                        if (!task.isCompleted()) {
                                            break;
                                        }
                                        Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(task.getArchetype().getSubmitNpcID());
                                        if (taskNpc != null && taskNpc.where() == player.where()) {
                                            byte npcTaskMarks = this.getTaskMark(taskNpc.getModelID(), player);
                                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), npcTaskMarks));
                                            break;
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void addFoundPathTask(final int _taskID, final TaskTargetFoundAPath _foundAPathTarget) {
        FoundAPathInfo foundAPathInfo = new FoundAPathInfo();
        foundAPathInfo.taskID = _taskID;
        foundAPathInfo.tastTargetID = _foundAPathTarget.getID();
        foundAPathInfo.map = MapServiceImpl.getInstance().getNormalMapByID(_foundAPathTarget.mapID);
        foundAPathInfo.x = _foundAPathTarget.x;
        foundAPathInfo.y = _foundAPathTarget.y;
        foundAPathInfo.mistakeRang = _foundAPathTarget.mistakeRang;
        this.foundAPathTaskList.add(foundAPathInfo);
    }

    private void reachDestination(final EscortNpcTaskInfo _info) {
        synchronized (this.escortTaskExcuteList) {
            ArrayList<TaskInstance> taskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(_info.trigger.getUserID());
            boolean refreshTaskSuccessful = false;
            for (final TaskInstance task : taskList) {
                if (refreshTaskSuccessful) {
                    break;
                }
                if (task.getArchetype() != _info.task) {
                    continue;
                }
                if (!task.isCompleted()) {
                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();
                    for (final BaseTaskTarget target : targetList) {
                        if (ETastTargetType.ESCORT_NPC == target.getType() && !target.isCompleted() && ((TaskTargetEscortNpc) target).getID() == _info.escortTarget.getID()) {
                            ((TaskTargetEscortNpc) target).complete();
                            TaskDAO.updateTaskProgress(_info.trigger.getUserID(), task);
                            ResponseMessageQueue.getInstance().put(_info.trigger.getMsgQueueIndex(), new RefreshTaskStatus(task.getArchetype().getID(), target.getID(), target.isCompleted(), target.getDescripiton(), task.isCompleted()));
                            if (task.isCompleted()) {
                                Npc taskNpc = NotPlayerServiceImpl.getInstance().getNpc(task.getArchetype().getSubmitNpcID());
                                if (taskNpc != null && taskNpc.where() == _info.trigger.where()) {
                                    byte npcTaskMarks = this.getTaskMark(taskNpc.getModelID(), _info.trigger);
                                    ResponseMessageQueue.getInstance().put(_info.trigger.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc.getID(), npcTaskMarks));
                                }
                            }
                            refreshTaskSuccessful = true;
                            break;
                        }
                    }
                    break;
                }
                break;
            }
            if (_info.spareTaskMemberList != null) {
                for (final HeroPlayer other : _info.spareTaskMemberList) {
                    if (_info.npc.where() == other.where()) {
                        boolean inDistance = 225 >= (other.getCellX() - _info.npc.getCellX()) * (other.getCellX() - _info.npc.getCellX()) + (other.getCellY() - _info.npc.getCellY()) * (other.getCellY() - _info.npc.getCellY());
                        if (!inDistance) {
                            continue;
                        }
                        taskList = (ArrayList<TaskInstance>) this.playerExsitsTaskListMap.get(other.getUserID());
                        refreshTaskSuccessful = false;
                        for (final TaskInstance task2 : taskList) {
                            if (refreshTaskSuccessful) {
                                break;
                            }
                            if (task2.getArchetype() != _info.task) {
                                continue;
                            }
                            if (!task2.isCompleted()) {
                                ArrayList<BaseTaskTarget> targetList2 = task2.getTargetList();
                                for (final BaseTaskTarget target2 : targetList2) {
                                    if (ETastTargetType.ESCORT_NPC == target2.getType() && !target2.isCompleted() && ((TaskTargetEscortNpc) target2).getID() == _info.escortTarget.getID()) {
                                        ((TaskTargetEscortNpc) target2).complete();
                                        TaskDAO.updateTaskProgress(other.getUserID(), task2);
                                        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new RefreshTaskStatus(task2.getArchetype().getID(), target2.getID(), target2.isCompleted(), target2.getDescripiton(), task2.isCompleted()));
                                        if (task2.isCompleted()) {
                                            Npc taskNpc2 = NotPlayerServiceImpl.getInstance().getNpc(task2.getArchetype().getSubmitNpcID());
                                            if (task2.isCompleted() && taskNpc2 != null && taskNpc2.where() == other.where()) {
                                                byte npcTaskMarks2 = this.getTaskMark(taskNpc2.getModelID(), other);
                                                ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new ChangeNpcTaskMark(taskNpc2.getID(), npcTaskMarks2));
                                            }
                                        }
                                        refreshTaskSuccessful = true;
                                        break;
                                    }
                                }
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
        // monitorexit(this.escortTaskExcuteList)
    }

    class TaskComparator implements Comparator<Task> {

        @Override
        public int compare(final Task o1, final Task o2) {
            Short o1Level = (Short) o1.getLevel();
            Short o2Level = (Short) o2.getLevel();
            boolean o1MainLine = o1.getMainLine();
            boolean o2MainLine = o2.getMainLine();
            boolean o1IsRepeated = o1.isRepeated();
            boolean o2IsRepeated = o2.isRepeated();
            if (o1MainLine && !o2MainLine) {
                return -1;
            }
            if (!o1MainLine && o2MainLine) {
                return 1;
            }
            if (o1MainLine && o2MainLine) {
                return 0;
            }
            if (o1Level.compareTo(o2Level) > 0) {
                return -1;
            }
            if (o1Level.compareTo(o2Level) == 0) {
                if (o1IsRepeated) {
                    return 1;
                }
                return 0;
            } else {
                if (o1Level.compareTo(o2Level) < 0) {
                    return 1;
                }
                return 0;
            }
        }
    }

    class TaskInstanceComparator implements Comparator<TaskInstance> {

        @Override
        public int compare(final TaskInstance o1, final TaskInstance o2) {
            Short o1Level = (Short) o1.getArchetype().getLevel();
            Short o2Level = (Short) o2.getArchetype().getLevel();
            Boolean o1MainLine = o1.getArchetype().getMainLine();
            Boolean o2MainLine = o2.getArchetype().getMainLine();
            Boolean o1IsRepeated = o1.getArchetype().isRepeated();
            Boolean o2IsRepeated = o2.getArchetype().isRepeated();
            if (o1MainLine && !o2MainLine) {
                return -1;
            }
            if (!o1MainLine && o2MainLine) {
                return 1;
            }
            if (o1MainLine && o2MainLine) {
                return 0;
            }
            if (o1Level.compareTo(o2Level) > 0) {
                return -1;
            }
            if (o1Level.compareTo(o2Level) == 0) {
                if (o1IsRepeated) {
                    return 1;
                }
                return 0;
            } else {
                if (o1Level.compareTo(o2Level) < 0) {
                    return 1;
                }
                return 0;
            }
        }
    }

    class FoundAPathTaskManager extends TimerTask {

        @Override
        public void run() {
            try {
                TaskServiceImpl.this.excuteFoundAPathTask();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class EscortNpcTaskManager extends TimerTask {

        @Override
        public void run() {
            try {
                TaskServiceImpl.this.excuteEscortNpcTask();
            } catch (Exception e) {
                TaskServiceImpl.log.error("\u62a4\u9001\u4efb\u52a1\u7ebf\u7a0b\u51fa\u9519");
            }
        }
    }

    class EscortNpcTaskInfo {

        Task task;
        TaskTargetEscortNpc escortTarget;
        HeroPlayer trigger;
        private ArrayList<HeroPlayer> spareTaskMemberList;
        Npc npc;
        int traceTime;

        EscortNpcTaskInfo() {
            this.spareTaskMemberList = new ArrayList<HeroPlayer>();
        }

        public ArrayList<HeroPlayer> getSpareTaskMemberList() {
            return this.spareTaskMemberList;
        }

        public void addSpareTaskMember(final HeroPlayer _member) {
            if (_member != null) {
                this.spareTaskMemberList.add(_member);
            }
        }
    }

    class FoundAPathInfo {

        int taskID;
        int tastTargetID;
        Map map;
        short x;
        short y;
        short mistakeRang;
    }
}
