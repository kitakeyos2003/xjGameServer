// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon;

import hero.map.service.WeatherManager;
import hero.share.ME2GameObject;
import hero.player.HeroPlayer;
import java.util.Iterator;
import hero.dungeon.service.DungeonInstanceManager;
import hero.dungeon.service.DungeonHistoryManager;
import java.util.ArrayList;
import hero.map.Map;

public class Dungeon {

    public static final int MEMBER_NUMBER_OF_MARRY_MAP = 100;
    private int id;
    private String name;
    private byte pattern;
    private byte type;
    private boolean isInFightingBoss;
    private int groupID;
    private int historyID;
    private Map entranceMap;
    private long timeOfNobody;
    private ArrayList<Map> mapList;
    private ArrayList<String> deathBossModelIDList;
    public static final byte PATTERN_OF_EASY = 1;
    public static final byte PATTERN_OF_DIFFICULT = 2;
    public static final String SUFFIX_OF_HERO_MONSTER_ID = "h";
    public static final String DUNGEON_MAP_NAME_SEPARATE_CHAR = " . ";
    public static final byte MEMBER_NUMBER_OF_RAID = 15;
    public static final byte MEMBER_NUMBER_OF_NORMAL = 5;
    public static final byte TYPE_NORMAL_GROUP = 1;
    public static final byte TYPE_RAID = 2;
    public static final String PATTERN_DESC_OF_EASY = "\uff08\u7b80\u5355\uff09";
    public static final String PATTERN_DESC_OF_DIFFICULT = "\uff08\u56f0\u96be\uff09";

    public Dungeon(final DungeonDataModel _dungeonDataModel) {
        this.id = _dungeonDataModel.id;
        this.name = _dungeonDataModel.name;
        this.pattern = 1;
        this.type = 1;
        this.mapList = new ArrayList<Map>();
        this.historyID = DungeonHistoryManager.getInstance().buildHistoryID();
    }

    public Dungeon(final DungeonDataModel _dungeonDataModel, final byte _pattern, final int _groupID) {
        this.id = _dungeonDataModel.id;
        this.name = _dungeonDataModel.name;
        this.pattern = _pattern;
        if (15 == _dungeonDataModel.playerNumberLimit) {
            this.type = 2;
        } else {
            this.type = 1;
        }
        this.historyID = DungeonHistoryManager.getInstance().buildHistoryID();
        this.groupID = _groupID;
        this.mapList = new ArrayList<Map>();
        this.deathBossModelIDList = new ArrayList<String>();
    }

    public Dungeon(final DungeonHistory _history, final DungeonDataModel _dungeonDataModel, final int _groupID) {
        this.id = _history.getDungeonID();
        this.name = _dungeonDataModel.name;
        this.pattern = _history.getPattern();
        this.historyID = _history.getID();
        this.groupID = _groupID;
        if (15 == _dungeonDataModel.playerNumberLimit) {
            this.type = 2;
        } else {
            this.type = 1;
        }
        this.mapList = new ArrayList<Map>();
        this.deathBossModelIDList = new ArrayList<String>();
    }

    public int getID() {
        return this.id;
    }

    public int getHistoryID() {
        return this.historyID;
    }

    public boolean needSaveHistory() {
        return this.type == 2 || this.pattern == 2;
    }

    public String getName() {
        return this.name;
    }

    public byte getPattern() {
        return this.pattern;
    }

    public byte getType() {
        return this.type;
    }

    public int getGroupID() {
        return this.groupID;
    }

    public void playerEntryMarryDungeon(final int _userID) {
        int playerNumber = this.getPlayerNumber();
        if (playerNumber < 100 && 1 == playerNumber && 0L != this.timeOfNobody) {
            DungeonInstanceManager.getInstance().removeDungeonFromMonitor(this);
        }
    }

    public void setGroupID(final int _groupID) {
        this.groupID = _groupID;
    }

    public void enterFightingOfBoss() {
        this.isInFightingBoss = true;
    }

    public void endFightingOfBoss() {
        this.isInFightingBoss = false;
    }

    public boolean isInFightingBoss() {
        return this.isInFightingBoss;
    }

    public void addDeathBoss(final String _monsterModelID) {
        if (!this.deathBossModelIDList.contains(_monsterModelID)) {
            this.deathBossModelIDList.add(_monsterModelID);
        }
    }

    public boolean bossIsDead(final String _monsterModelID) {
        return this.deathBossModelIDList.contains(_monsterModelID);
    }

    public int getPlayerNumber() {
        int playerNumber = 0;
        for (final Map map : this.mapList) {
            playerNumber += map.getPlayerList().size();
        }
        return playerNumber;
    }

    public ArrayList<HeroPlayer> getPlayerList() {
        ArrayList<HeroPlayer> playList = new ArrayList<HeroPlayer>();
        for (final Map map : this.mapList) {
            for (final ME2GameObject object : map.getPlayerList()) {
                playList.add((HeroPlayer) object);
            }
        }
        return playList;
    }

    public int getPlayerNumberLimit() {
        return (this.type == 1) ? 5 : 15;
    }

    public void playerComeIn(final int _userID) {
        int playerNumber = this.getPlayerNumber();
        if (playerNumber < this.getPlayerNumberLimit() && 1 == playerNumber && 0L != this.timeOfNobody) {
            DungeonInstanceManager.getInstance().removeDungeonFromMonitor(this);
        }
    }

    public void playerLeft(final HeroPlayer _player) {
        if (this.getPlayerNumber() == 0) {
            this.timeOfNobody = System.currentTimeMillis();
            DungeonInstanceManager.getInstance().addDungeonToMonitor(this);
        }
    }

    public void setEntranceMap(final Map _map) {
        this.entranceMap = _map;
    }

    public Map getEntranceMap() {
        return this.entranceMap;
    }

    public Map getMap(final int _mapID) {
        for (final Map map : this.mapList) {
            if (map.getID() == _mapID) {
                return map;
            }
        }
        return null;
    }

    public ArrayList<Map> getInternalMapList() {
        return this.mapList;
    }

    public long getTimeOfNobody() {
        return this.timeOfNobody;
    }

    public void initInternalMap(final Map _map) {
        this.mapList.add(_map);
        _map.setDungeon(this);
    }

    public void destroy() {
        synchronized (this.mapList) {
            for (final Map map : this.mapList) {
                map.destroy();
                WeatherManager.getInstance().remove(map);
            }
        }
        // monitorexit(this.mapList)
    }
}
