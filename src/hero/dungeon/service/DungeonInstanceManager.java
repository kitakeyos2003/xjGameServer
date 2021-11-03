// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon.service;

import java.util.ArrayList;
import hero.player.HeroPlayer;
import javolution.util.FastMap;
import hero.dungeon.Dungeon;
import javolution.util.FastList;
import java.util.TimerTask;

public class DungeonInstanceManager extends TimerTask {

    private FastList<Dungeon> noneHistoryDungeonList;
    private FastList<Dungeon> historyDungeonList;
    private FastMap<Integer, Dungeon> historyDungeonTable;
    private FastList<Dungeon> dungeonListThatWillClear;
    private static DungeonInstanceManager instance;
    private static final long DUNGEON_EXISTS_TIME_IN_MEMERY = 1800000L;
    public static final int START_RELAY = 30000;
    public static final long CHECK_PERIOD = 200000L;

    private DungeonInstanceManager() {
        this.noneHistoryDungeonList = (FastList<Dungeon>) new FastList();
        this.historyDungeonList = (FastList<Dungeon>) new FastList();
        this.historyDungeonTable = (FastMap<Integer, Dungeon>) new FastMap();
        this.dungeonListThatWillClear = (FastList<Dungeon>) new FastList();
    }

    public void add(final Dungeon _dungeon) {
        if (!_dungeon.needSaveHistory()) {
            this.noneHistoryDungeonList.add(_dungeon);
        } else {
            this.historyDungeonTable.put(_dungeon.getHistoryID(), _dungeon);
            this.historyDungeonList.add(_dungeon);
        }
    }

    public Dungeon getHistoryDungeon(final int _historyID) {
        return (Dungeon) this.historyDungeonTable.get(_historyID);
    }

    public Dungeon getHistoryDungeon(final int _groupID, final int _dungeonID, final byte _pattern) {
        synchronized (this.historyDungeonList) {
            Dungeon dungeon = null;
            for (int i = 0; i < this.historyDungeonList.size(); ++i) {
                dungeon = (Dungeon) this.historyDungeonList.get(i);
                if (dungeon.getGroupID() == _groupID && dungeon.getID() == _dungeonID && dungeon.getPattern() == _pattern) {
                    // monitorexit(this.historyDungeonList)
                    return dungeon;
                }
            }
        }
        // monitorexit(this.historyDungeonList)
        return null;
    }

    public Dungeon getNoneHistoryDungeon(final int _groupID, final int _dungeonID) {
        synchronized (this.noneHistoryDungeonList) {
            for (final Dungeon dungeon : this.noneHistoryDungeonList) {
                if (dungeon.getGroupID() == _groupID && dungeon.getID() == _dungeonID) {
                    // monitorexit(this.noneHistoryDungeonList)
                    return dungeon;
                }
            }
        }
        // monitorexit(this.noneHistoryDungeonList)
        return null;
    }

    public Dungeon getMarryDungeon(final int _dungeonID, final int _historyID) {
        synchronized (this.noneHistoryDungeonList) {
            for (final Dungeon dungeon : this.noneHistoryDungeonList) {
                if (dungeon.getID() == _dungeonID && dungeon.getHistoryID() == _historyID) {
                    // monitorexit(this.noneHistoryDungeonList)
                    return dungeon;
                }
            }
        }
        // monitorexit(this.noneHistoryDungeonList)
        return null;
    }

    public void removeNoneHistoryDungeon(final int _groupID) {
        synchronized (this.noneHistoryDungeonList) {
            int i = 0;
            while (i < this.noneHistoryDungeonList.size()) {
                Dungeon dungeon = (Dungeon) this.noneHistoryDungeonList.get(i);
                if (dungeon.getGroupID() == _groupID && dungeon.getPlayerNumber() == 0) {
                    dungeon.destroy();
                    this.noneHistoryDungeonList.remove(i);
                } else {
                    ++i;
                }
            }
        }
        // monitorexit(this.noneHistoryDungeonList)
    }

    public boolean addDungeonToMonitor(final Dungeon _dungeion) {
        if (!this.dungeonListThatWillClear.contains(_dungeion)) {
            synchronized (this.dungeonListThatWillClear) {
                // monitorexit(this.dungeonListThatWillClear)
                return this.dungeonListThatWillClear.add(_dungeion);
            }
        }
        return false;
    }

    public boolean removeDungeonFromMonitor(final Dungeon _dungeion) {
        synchronized (this.dungeonListThatWillClear) {
            // monitorexit(this.dungeonListThatWillClear)
            return this.dungeonListThatWillClear.remove(_dungeion);
        }
    }

    public static DungeonInstanceManager getInstance() {
        if (DungeonInstanceManager.instance == null) {
            DungeonInstanceManager.instance = new DungeonInstanceManager();
        }
        return DungeonInstanceManager.instance;
    }

    public void clearHistoryDungeon(final int _historyID) {
        synchronized (this.historyDungeonTable) {
            Dungeon dungeon = (Dungeon) this.historyDungeonTable.remove(_historyID);
            if (dungeon != null) {
                Label_0098:
                {
                    if (dungeon.getPlayerNumber() == 0) {
                        // monitorenter(dungeonListThatWillClear = this.dungeonListThatWillClear)
                        try {
                            this.dungeonListThatWillClear.remove(dungeon);
                            // monitorexit(dungeonListThatWillClear)
                            break Label_0098;
                        } finally {
                        }
                    }
                    ArrayList<HeroPlayer> playerList = dungeon.getPlayerList();
                    for (HeroPlayer heroPlayer : playerList) {
                    }
                }
                dungeon.destroy();
                // monitorenter(historyDungeonList = this.historyDungeonList)
                try {
                    this.historyDungeonList.remove(dungeon);
                } // monitorexit(historyDungeonList)
                finally {
                }
            }
        }
        // monitorexit(this.historyDungeonTable)
    }

    @Override
    public void run() {
        synchronized (this.dungeonListThatWillClear) {
            int i = 0;
            while (i < this.dungeonListThatWillClear.size()) {
                Dungeon dungeon = (Dungeon) this.dungeonListThatWillClear.get(i);
                if (System.currentTimeMillis() - dungeon.getTimeOfNobody() >= 1800000L) {
                    dungeon.destroy();
                    if (dungeon.getPattern() == 2 || dungeon.getPlayerNumberLimit() == 15) {
                        this.historyDungeonList.remove(dungeon);
                        this.historyDungeonTable.remove(dungeon.getHistoryID());
                    } else {
                        this.noneHistoryDungeonList.remove(dungeon);
                    }
                    this.dungeonListThatWillClear.remove(i);
                } else {
                    ++i;
                }
            }
        }
        // monitorexit(this.dungeonListThatWillClear)
    }
}
