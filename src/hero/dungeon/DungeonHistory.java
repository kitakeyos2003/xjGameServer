// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon;

import hero.dungeon.service.DungeonDataModelDictionary;
import java.util.Vector;
import java.util.HashMap;
import java.util.Date;

public class DungeonHistory {

    private int historyID;
    private int dungeonID;
    private String dungeonName;
    private byte dungeonPattern;
    private byte dungeonType;
    private Date buildTime;
    private HashMap<String, Short> deathBossTable;
    private Vector<Integer> includePlayerUserIDList;

    public DungeonHistory(final Dungeon _dungeon, final short _mapID, final String _monsterModelID) {
        this.deathBossTable = new HashMap<String, Short>();
        this.includePlayerUserIDList = new Vector<Integer>();
        this.historyID = _dungeon.getHistoryID();
        this.dungeonID = _dungeon.getID();
        this.dungeonName = _dungeon.getName();
        this.dungeonPattern = _dungeon.getPattern();
        this.dungeonType = _dungeon.getType();
        this.buildTime = new Date();
        this.deathBossTable.put(_monsterModelID, _mapID);
    }

    public DungeonHistory(final int _historyID, final int _dungeonID, final byte _pattern, final byte _dungeonType, final Date _buildTime) {
        this.deathBossTable = new HashMap<String, Short>();
        this.includePlayerUserIDList = new Vector<Integer>();
        this.historyID = _historyID;
        this.dungeonID = _dungeonID;
        this.dungeonName = DungeonDataModelDictionary.getInsatnce().get(_dungeonID).name;
        this.dungeonPattern = _pattern;
        this.dungeonType = _dungeonType;
        this.buildTime = _buildTime;
    }

    public int getID() {
        return this.historyID;
    }

    public int getDungeonID() {
        return this.dungeonID;
    }

    public String getDungeonName() {
        return this.dungeonName;
    }

    public byte getPattern() {
        return this.dungeonPattern;
    }

    public byte getDungeonType() {
        return this.dungeonType;
    }

    public Date getBuildTime() {
        return this.buildTime;
    }

    public boolean containsPlayer(final int _userID) {
        return this.includePlayerUserIDList.contains(_userID);
    }

    public HashMap<String, Short> getDeathBossTable() {
        return this.deathBossTable;
    }

    public Vector<Integer> getIncludePlayerUserIDList() {
        return this.includePlayerUserIDList;
    }

    public boolean includePlayer(final int _userID) {
        return this.includePlayerUserIDList.contains(_userID);
    }

    public void addDeathBoss(final String _monsterModelID, final short _mapID) {
        if (!this.deathBossTable.containsKey(_monsterModelID)) {
            this.deathBossTable.put(_monsterModelID, _mapID);
        }
    }

    public boolean addPlayer(final int _userID) {
        return !this.includePlayerUserIDList.contains(_userID) && this.includePlayerUserIDList.add(_userID);
    }
}
