// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon.service;

import hero.share.service.LogWriter;
import hero.map.detail.Door;
import hero.map.message.DoorOpenedNotify;
import hero.npc.detail.EMonsterLevel;
import hero.npc.Monster;
import hero.map.message.SwitchMapFailNotify;
import hero.share.message.Warning;
import hero.map.message.PlayerRefreshNotify;
import hero.map.message.DisappearNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.effect.service.EffectServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.message.ResponseMapBottomData;
import yoyo.core.queue.ResponseMessageQueue;
import hero.map.Map;
import hero.map.MapModelData;
import hero.map.service.MapServiceImpl;
import hero.dungeon.DungeonDataModel;
import hero.player.HeroPlayer;
import hero.map.EMapType;
import hero.player.service.PlayerServiceImpl;
import yoyo.service.base.session.Session;
import hero.share.service.GlobalTimer;
import hero.dungeon.Dungeon;
import hero.dungeon.DungeonHistory;
import java.util.ArrayList;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class DungeonServiceImpl extends AbsServiceAdaptor<DungeonConfig> {

    private static Logger log;
    private FastMap<Integer, ArrayList<DungeonHistory>> playerDungeonHistoryTable;
    private FastMap<Integer, Dungeon> playerDungeonTable;
    private static DungeonServiceImpl instance;
    private static final String TIP_OF_MEMBER_IN_DIFFICULT_PATTERN = "\u5df2\u6709\u961f\u5458\u5728\u56f0\u96be\u96be\u5ea6\u4e2d";
    private static final String TIP_OF_MEMBER_IN_EASY_PATTERN = "\u5df2\u6709\u961f\u5458\u5728\u7b80\u5355\u96be\u5ea6\u4e2d";
    private static final String TIP_OF_DIFFERENT_HISTORY = "\u4e0e\u961f\u957f\u8fdb\u5ea6\u4e0d\u540c";
    private static final String TIP_OF_OTHER_GROUP_USEING_HISTORY = "\u53e6\u4e00\u4e2a\u961f\u4f0d\u5728\u4f60\u7684\u8fdb\u5ea6\u4e2d";
    private static final String TIP_OF_MEMBER_FULL = "\u526f\u672c\u4e2d\u4eba\u6570\u5df2\u6ee1";
    private static final String TIP_OF_HISTORY_HAPPEND = "\u8fdb\u5ea6\u5df2\u4ea7\u751f";

    static {
        DungeonServiceImpl.log = Logger.getLogger((Class) DungeonServiceImpl.class);
    }

    private DungeonServiceImpl() {
        this.config = new DungeonConfig();
        this.playerDungeonHistoryTable = (FastMap<Integer, ArrayList<DungeonHistory>>) new FastMap();
        this.playerDungeonTable = (FastMap<Integer, Dungeon>) new FastMap();
    }

    public static DungeonServiceImpl getInstance() {
        if (DungeonServiceImpl.instance == null) {
            DungeonServiceImpl.instance = new DungeonServiceImpl();
        }
        return DungeonServiceImpl.instance;
    }

    @Override
    protected void start() {
        DungeonDataModelDictionary.getInsatnce().loadDungeonModelData(((DungeonConfig) this.config).dungeon_data_path);
        DungeonHistoryManager.getInstance().init();
        DungeonInstanceManager.getInstance();
        GlobalTimer.getInstance().registe(TransmitterOfDungeon.getInstance(), 15000L, 5000L);
        GlobalTimer.getInstance().registe(DungeonInstanceManager.getInstance(), 30000L, 200000L);
    }

    @Override
    public void createSession(final Session _session) {
        ArrayList<DungeonHistory> historyList = DungeonHistoryManager.getInstance().getPlayerHistoryList(_session.userID);
        if (historyList != null) {
            this.playerDungeonHistoryTable.put(_session.userID, historyList);
        } else {
            this.playerDungeonHistoryTable.put(_session.userID, new ArrayList());
        }
    }

    @Override
    public void sessionFree(final Session _session) {
    }

    @Override
    public void clean(final int _userID) {
        this.playerDungeonHistoryTable.remove(_userID);
        Dungeon dungeon = (Dungeon) this.playerDungeonTable.remove(_userID);
        if (dungeon != null) {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
            if (player != null && player.where().getMapType() == EMapType.DUNGEON && player.getGroupID() > 0) {
                dungeon.playerLeft(player);
            }
        }
    }

    public void playerEnterDungeon(final int _userID, final Dungeon _dungeon) {
        this.playerDungeonTable.put(_userID, _dungeon);
        _dungeon.playerComeIn(_userID);
    }

    public void marryerGotoMarryDungeon(final HeroPlayer firster, final HeroPlayer seconder, final short marryMapID) {
        Dungeon dungeon = this.getWhereDungeon(firster.getUserID());
        DungeonServiceImpl.log.debug(("marryerGotoMarryDungeon = " + dungeon));
        if (dungeon == null) {
            DungeonServiceImpl.log.debug("\u7b2c\u4e00\u4e2a\u8fdb\u53bb");
            this.gotoMarryDungeonMap(0, firster, marryMapID);
        } else {
            this.gotoMarryDungeonMap(dungeon.getHistoryID(), seconder, marryMapID);
        }
    }

    private Dungeon buildMarryDungeon(final DungeonDataModel _dungeonDataModel) {
        Dungeon dungeon = new Dungeon(_dungeonDataModel);
        MapModelData[] mapModelList;
        for (int length = (mapModelList = _dungeonDataModel.mapModelList).length, i = 0; i < length; ++i) {
            MapModelData mapModeldata = mapModelList[i];
            Map map = MapServiceImpl.getInstance().buildDungeonMap((byte) 1, mapModeldata, _dungeonDataModel.name, false);
            dungeon.initInternalMap(map);
            if (mapModeldata.id == _dungeonDataModel.entranceMapID) {
                dungeon.setEntranceMap(map);
            }
        }
        DungeonInstanceManager.getInstance().add(dungeon);
        return dungeon;
    }

    public boolean gotoMarryDungeonMap(final int _dungeonID, final HeroPlayer _player, final short mapID) {
        DungeonDataModel dungeonData = DungeonDataModelDictionary.getInsatnce().getDungeonDataModelByMapid(mapID);
        DungeonServiceImpl.log.debug(("\u8fdb\u5165\u5a5a\u793c\u793c\u5802\u526f\u672c\u5730\u56fe gotoMarryDungeonMap = " + dungeonData));
        if (dungeonData != null) {
            Dungeon dungeon = DungeonInstanceManager.getInstance().getMarryDungeon(dungeonData.id, _dungeonID);
            DungeonServiceImpl.log.debug(("goto marry dungeon = " + dungeon));
            if (dungeon == null) {
                dungeon = this.buildMarryDungeon(dungeonData);
            }
            if (dungeon != null) {
                if (dungeon.getPlayerNumber() < 100) {
                    Map entranceMap = dungeon.getEntranceMap();
                    _player.setCellX(entranceMap.getBornX());
                    _player.setCellY(entranceMap.getBornY());
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, entranceMap, _player.where()));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapGameObjectList(_player.getLoginInfo().clientType, entranceMap));
                    EffectServiceImpl.getInstance().sendEffectList(_player, entranceMap);
                    _player.where().getPlayerList().remove(_player);
                    if (_player.where() != entranceMap) {
                        MapSynchronousInfoBroadcast.getInstance().put(_player.where(), new DisappearNotify(_player.getObjectType().value(), _player.getID(), _player.getHp(), _player.getBaseProperty().getHpMax(), _player.getMp(), _player.getBaseProperty().getMpMax()), false, 0);
                    }
                    _player.live(entranceMap);
                    entranceMap.getPlayerList().add(_player);
                    MapSynchronousInfoBroadcast.getInstance().put(_player.where(), new PlayerRefreshNotify(_player), true, _player.getID());
                    _player.needUpdateDB = true;
                    this.playerEntryMarryDungeon(_player.getUserID(), dungeon);
                    return true;
                }
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u793c\u5802\u4eba\u6570\u5df2\u6ee1"));
                return false;
            }
        } else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5a5a\u793c\u526f\u672c\u6570\u636e\u9519\u8bef\uff0c\u4e0d\u80fd\u8fdb\u5165\uff01"));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new SwitchMapFailNotify("\u5a5a\u793c\u526f\u672c\u6570\u636e\u9519\u8bef\uff0c\u4e0d\u80fd\u8fdb\u5165\uff01"));
        }
        return false;
    }

    public void playerEntryMarryDungeon(final int _userID, final Dungeon _dungeon) {
        this.playerDungeonTable.put(_userID, _dungeon);
        _dungeon.playerEntryMarryDungeon(_userID);
    }

    public void playerLeftDungeon(final HeroPlayer _player) {
        Dungeon dungeon = (Dungeon) this.playerDungeonTable.remove(_player.getUserID());
        if (dungeon != null) {
            dungeon.playerLeft(_player);
        }
    }

    public Dungeon getWhereDungeon(final int _userID) {
        return (Dungeon) this.playerDungeonTable.get(_userID);
    }

    public void processMonsterDie(final Monster _monster, final Dungeon _dungeon) {
        try {
            if (_dungeon != null) {
                if (EMonsterLevel.NORMAL == _monster.getMonsterLevel()) {
                    if (_monster.where().getMonsterModelIDAbout() != null) {
                        if (_dungeon.bossIsDead(_monster.where().getMonsterModelIDAbout())) {
                            _monster.destroy();
                        }
                    } else {
                        _monster.destroy();
                    }
                } else {
                    _dungeon.addDeathBoss(_monster.getModelID());
                    Door[] doorList;
                    for (int length = (doorList = _monster.where().doorList).length, i = 0; i < length; ++i) {
                        Door door = doorList[i];
                        if (door.monsterIDAbout != null && -1 != _monster.getModelID().indexOf(door.monsterIDAbout)) {
                            door.visible = true;
                            DoorOpenedNotify msg = new DoorOpenedNotify(_monster.where().getID(), door.targetMapID);
                            MapSynchronousInfoBroadcast.getInstance().put(_monster.where(), msg, false, 0);
                        }
                    }
                    if (_dungeon.needSaveHistory()) {
                        DungeonHistory history = DungeonHistoryManager.getInstance().getHistory(_dungeon.getHistoryID());
                        if (history != null) {
                            history.addDeathBoss(_monster.getModelID(), _monster.where().getID());
                            DungeonDAO.changeDungeonHistoryContent(history);
                        } else {
                            history = new DungeonHistory(_dungeon, _monster.where().getID(), _monster.getModelID());
                            ArrayList<HeroPlayer> playerList = _monster.getHatredTargetList();
                            AbsResponseMessage msg2 = new Warning(String.valueOf(_dungeon.getName()) + "\u8fdb\u5ea6\u5df2\u4ea7\u751f");
                            for (final HeroPlayer player : playerList) {
                                history.addPlayer(player.getUserID());
                                ((ArrayList) this.playerDungeonHistoryTable.get(player.getUserID())).add(history);
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg2);
                            }
                            DungeonHistoryManager.getInstance().addDungeonHistory(history);
                            DungeonDAO.buildDungeonHistory(history);
                        }
                    }
                    _monster.destroy();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<DungeonHistory> getHistoryList(final int _userID) {
        return (ArrayList<DungeonHistory>) this.playerDungeonHistoryTable.get(_userID);
    }

    public Dungeon tryEnterDungeon(final HeroPlayer _player, final DungeonDataModel _dungeonData, final byte _pattern, final int _groupID, final int _leaderUserID) {
        try {
            if (2 == _pattern) {
                Dungeon dungeon = DungeonInstanceManager.getInstance().getNoneHistoryDungeon(_groupID, _dungeonData.id);
                if (dungeon != null && dungeon.getPlayerNumber() > 0) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5df2\u6709\u961f\u5458\u5728\u7b80\u5355\u96be\u5ea6\u4e2d"));
                    return null;
                }
            } else {
                Dungeon dungeon = DungeonInstanceManager.getInstance().getHistoryDungeon(_groupID, _dungeonData.id, (byte) 2);
                if (dungeon != null && dungeon.getPlayerNumber() > 0) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5df2\u6709\u961f\u5458\u5728\u56f0\u96be\u96be\u5ea6\u4e2d"));
                    return null;
                }
            }
            if (2 == _pattern || 15 == _dungeonData.playerNumberLimit) {
                DungeonHistory dungeonHistory = this.getDungeonHistory(_player.getUserID(), _dungeonData.id, _pattern);
                if (dungeonHistory != null) {
                    if (!dungeonHistory.containsPlayer(_leaderUserID)) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4e0e\u961f\u957f\u8fdb\u5ea6\u4e0d\u540c"));
                        return null;
                    }
                    Dungeon dungeon = DungeonInstanceManager.getInstance().getHistoryDungeon(dungeonHistory.getID());
                    if (dungeon == null) {
                        return this.buildDungeonByHistory(dungeonHistory, _groupID);
                    }
                    if (dungeon.getGroupID() == _groupID) {
                        if (dungeon.getPlayerNumber() >= dungeon.getPlayerNumberLimit()) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u526f\u672c\u4e2d\u4eba\u6570\u5df2\u6ee1"));
                            return null;
                        }
                        return dungeon;
                    } else {
                        if (dungeon.getPlayerNumber() == 0) {
                            dungeon.setGroupID(_groupID);
                            return dungeon;
                        }
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u53e6\u4e00\u4e2a\u961f\u4f0d\u5728\u4f60\u7684\u8fdb\u5ea6\u4e2d"));
                        return null;
                    }
                } else {
                    dungeonHistory = this.getDungeonHistory(_leaderUserID, _dungeonData.id, _pattern);
                    if (dungeonHistory != null) {
                        Dungeon dungeon = DungeonInstanceManager.getInstance().getHistoryDungeon(dungeonHistory.getID());
                        if (dungeon == null) {
                            dungeon = this.buildDungeonByHistory(dungeonHistory, _groupID);
                            if (dungeonHistory.addPlayer(_player.getUserID())) {
                                DungeonDAO.changeDungeonHistoryContent(dungeonHistory);
                            }
                            return dungeon;
                        }
                        if (dungeon.getGroupID() == _groupID) {
                            if (dungeon.getPlayerNumber() >= dungeon.getPlayerNumberLimit()) {
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u526f\u672c\u4e2d\u4eba\u6570\u5df2\u6ee1"));
                                return null;
                            }
                            if (dungeonHistory.addPlayer(_player.getUserID())) {
                                DungeonDAO.changeDungeonHistoryContent(dungeonHistory);
                            }
                            return dungeon;
                        } else {
                            if (dungeon.getPlayerNumber() == 0) {
                                dungeon.setGroupID(_groupID);
                                return dungeon;
                            }
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u53e6\u4e00\u4e2a\u961f\u4f0d\u5728\u4f60\u7684\u8fdb\u5ea6\u4e2d"));
                            return null;
                        }
                    } else {
                        Dungeon dungeon = DungeonInstanceManager.getInstance().getHistoryDungeon(_groupID, _dungeonData.id, _pattern);
                        if (dungeon == null) {
                            return this.buildNewDungeon(_dungeonData, _pattern, _groupID);
                        }
                        if (dungeon.getPlayerNumber() >= dungeon.getPlayerNumberLimit()) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u526f\u672c\u4e2d\u4eba\u6570\u5df2\u6ee1"));
                            return null;
                        }
                        dungeonHistory = DungeonHistoryManager.getInstance().getHistory(dungeon.getHistoryID());
                        if (dungeonHistory != null && dungeonHistory.addPlayer(_player.getUserID())) {
                            DungeonDAO.changeDungeonHistoryContent(dungeonHistory);
                        }
                        return dungeon;
                    }
                }
            } else {
                Dungeon dungeon = DungeonInstanceManager.getInstance().getNoneHistoryDungeon(_groupID, _dungeonData.id);
                if (dungeon == null) {
                    return this.buildNewDungeon(_dungeonData, _pattern, _groupID);
                }
                if (dungeon.getPlayerNumber() >= dungeon.getPlayerNumberLimit()) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u526f\u672c\u4e2d\u4eba\u6570\u5df2\u6ee1"));
                    return null;
                }
                return dungeon;
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
            return null;
        }
    }

    private DungeonHistory getDungeonHistory(final int _playerUserID, final int _dungeonID, final byte _pattern) {
        ArrayList<DungeonHistory> dungeonHistoryList = (ArrayList<DungeonHistory>) this.playerDungeonHistoryTable.get(_playerUserID);
        if (dungeonHistoryList != null) {
            for (final DungeonHistory history : dungeonHistoryList) {
                if (history.getDungeonID() == _dungeonID && history.getPattern() == _pattern) {
                    return history;
                }
            }
        }
        return null;
    }

    private Dungeon buildDungeonByHistory(final DungeonHistory _history, final int _groupID) {
        DungeonDataModel dungeonDataModel = DungeonDataModelDictionary.getInsatnce().get(_history.getDungeonID());
        Dungeon dungeon = new Dungeon(_history, dungeonDataModel, _groupID);
        MapModelData[] mapModelList;
        for (int length = (mapModelList = dungeonDataModel.mapModelList).length, i = 0; i < length; ++i) {
            MapModelData mapModeldata = mapModelList[i];
            boolean monsterAboutExists = true;
            Short monsterAboutWhereID;
            if (_history.getPattern() == 2) {
                monsterAboutWhereID = _history.getDeathBossTable().get(String.valueOf(mapModeldata.monsterModelIDAbout) + "h");
            } else {
                monsterAboutWhereID = _history.getDeathBossTable().get(mapModeldata.monsterModelIDAbout);
            }
            if (monsterAboutWhereID != null && monsterAboutWhereID == mapModeldata.id) {
                monsterAboutExists = false;
            }
            Map map = MapServiceImpl.getInstance().buildDungeonMap(_history.getPattern(), mapModeldata, dungeonDataModel.name, monsterAboutExists);
            dungeon.initInternalMap(map);
            if (mapModeldata.id == dungeonDataModel.entranceMapID) {
                dungeon.setEntranceMap(map);
            }
        }
        DungeonInstanceManager.getInstance().add(dungeon);
        return dungeon;
    }

    private Dungeon buildNewDungeon(final DungeonDataModel _dungeonDataModel, final byte _pattern, final int _groupID) {
        Dungeon dungeon = new Dungeon(_dungeonDataModel, _pattern, _groupID);
        MapModelData[] mapModelList;
        for (int length = (mapModelList = _dungeonDataModel.mapModelList).length, i = 0; i < length; ++i) {
            MapModelData mapModeldata = mapModelList[i];
            Map map = MapServiceImpl.getInstance().buildDungeonMap(_pattern, mapModeldata, _dungeonDataModel.name, true);
            dungeon.initInternalMap(map);
            if (mapModeldata.id == _dungeonDataModel.entranceMapID) {
                dungeon.setEntranceMap(map);
            }
        }
        DungeonInstanceManager.getInstance().add(dungeon);
        return dungeon;
    }
}
