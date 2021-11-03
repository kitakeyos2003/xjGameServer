// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon.service;

import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.TimerTask;
import java.util.Calendar;
import java.util.Timer;
import javolution.util.FastList;
import hero.dungeon.DungeonHistory;
import javolution.util.FastMap;

public class DungeonHistoryManager {

    private int usableHistoryID;
    private FastMap<Integer, DungeonHistory> historyTable;
    private FastList<DungeonHistory> historyList;
    private Timer clearTimer;
    private Calendar raidDungeonRefreshTime;
    private Calendar normalDungeonRefreshTime;
    private static DungeonHistoryManager instance;
    public static final int BASAL_HISTORY_ID = 1000;
    private static final long HISTORY_CHECK_INTERVAL = 3600000L;
    private static final long DAY = 86400000L;
    private static final long HOUR = 3600000L;
    private static final long MINUTE = 60000L;

    private DungeonHistoryManager() {
    }

    public static DungeonHistoryManager getInstance() {
        if (DungeonHistoryManager.instance == null) {
            DungeonHistoryManager.instance = new DungeonHistoryManager();
        }
        return DungeonHistoryManager.instance;
    }

    public void init() {
        if (this.usableHistoryID == 0) {
            this.historyTable = (FastMap<Integer, DungeonHistory>) new FastMap();
            this.historyList = (FastList<DungeonHistory>) new FastList();
            this.checkInvalidateHistoryOfDB();
            this.usableHistoryID = DungeonDAO.loadDungeonHistory() + 1;
            Calendar calendar = Calendar.getInstance();
            int startTime = (60 - calendar.get(12) + 5) * 60 * 1000;
            (this.clearTimer = new Timer()).schedule(new HistoryClearTast(), startTime, 3600000L);
            this.resetDungeonRefreshTime();
        }
    }

    private void resetDungeonRefreshTime() {
        (this.raidDungeonRefreshTime = Calendar.getInstance()).add(5, 7);
        this.raidDungeonRefreshTime.set(7, DungeonServiceImpl.getInstance().getConfig().raid_history_refresh_week + 1);
        this.raidDungeonRefreshTime.set(11, DungeonServiceImpl.getInstance().getConfig().history_refresh_time);
        this.raidDungeonRefreshTime.set(12, 0);
        this.raidDungeonRefreshTime.set(13, 0);
        (this.normalDungeonRefreshTime = Calendar.getInstance()).add(5, 1);
        this.normalDungeonRefreshTime.set(11, DungeonServiceImpl.getInstance().getConfig().history_refresh_time);
        this.normalDungeonRefreshTime.set(12, 0);
        this.normalDungeonRefreshTime.set(13, 0);
    }

    private void checkInvalidateHistoryOfDB() {
        Calendar raidHistoryCalendar = Calendar.getInstance();
        raidHistoryCalendar.set(7, DungeonServiceImpl.getInstance().getConfig().raid_history_refresh_week + 1);
        raidHistoryCalendar.set(11, DungeonServiceImpl.getInstance().getConfig().history_refresh_time);
        raidHistoryCalendar.set(12, 0);
        raidHistoryCalendar.set(13, 0);
        Calendar normalGroupHistoryCalendar = Calendar.getInstance();
        normalGroupHistoryCalendar.set(11, 5);
        normalGroupHistoryCalendar.set(12, 0);
        normalGroupHistoryCalendar.set(13, 0);
        DungeonDAO.deleteDungeonHistory(new Timestamp(normalGroupHistoryCalendar.getTimeInMillis()), new Timestamp(raidHistoryCalendar.getTimeInMillis()));
    }

    public void addDungeonHistory(final DungeonHistory _history) {
        if (!this.historyTable.containsKey(_history.getID())) {
            this.historyTable.put(_history.getID(), _history);
            this.historyList.add(_history);
        }
    }

    public DungeonHistory getHistory(final int _historyID) {
        return (DungeonHistory) this.historyTable.get(_historyID);
    }

    public ArrayList<DungeonHistory> getPlayerHistoryList(final int _userID) {
        ArrayList<DungeonHistory> list = new ArrayList<DungeonHistory>();
        for (int i = 0; i < this.historyList.size(); ++i) {
            DungeonHistory history = (DungeonHistory) this.historyList.get(i);
            if (history.containsPlayer(_userID)) {
                list.add(history);
            }
        }
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    public int buildHistoryID() {
        return this.usableHistoryID++;
    }

    public String[] getDungeonRefreshTimeInfo() {
        String[] refreshTimeInfo = new String[2];
        long nowMill = System.currentTimeMillis();
        long raidDungeonRefreshMill = this.raidDungeonRefreshTime.getTimeInMillis();
        long normalDungeonRefreshMill = this.normalDungeonRefreshTime.getTimeInMillis();
        int days = (int) ((raidDungeonRefreshMill - nowMill) / 86400000L);
        int hour = (int) ((raidDungeonRefreshMill - nowMill) % 86400000L / 3600000L);
        int minute = (int) ((raidDungeonRefreshMill - nowMill) % 86400000L % 3600000L / 60000L);
        refreshTimeInfo[0] = "\u8ddd\u79bb\u91cd\u7f6e" + days + "\u5929" + hour + "\u5c0f\u65f6" + minute + "\u5206";
        days = (int) ((normalDungeonRefreshMill - nowMill) / 86400000L);
        hour = (int) ((normalDungeonRefreshMill - nowMill) % 86400000L / 3600000L);
        minute = (int) ((normalDungeonRefreshMill - nowMill) % 86400000L % 3600000L / 60000L);
        refreshTimeInfo[1] = "\u8ddd\u79bb\u91cd\u7f6e" + days + "\u5929" + hour + "\u5c0f\u65f6" + minute + "\u5206";
        return refreshTimeInfo;
    }

    class HistoryClearTast extends TimerTask {

        @Override
        public void run() {
            if (DungeonServiceImpl.getInstance().getConfig().history_refresh_time == Calendar.getInstance().get(11)) {
                Calendar raidHistoryRefreshCalendar = Calendar.getInstance();
                raidHistoryRefreshCalendar.set(7, DungeonServiceImpl.getInstance().getConfig().raid_history_refresh_week + 1);
                raidHistoryRefreshCalendar.set(11, DungeonServiceImpl.getInstance().getConfig().history_refresh_time);
                raidHistoryRefreshCalendar.set(12, 0);
                raidHistoryRefreshCalendar.set(13, 0);
                Calendar normalGroupHistoryRefreshCalendar = Calendar.getInstance();
                normalGroupHistoryRefreshCalendar.set(11, 5);
                normalGroupHistoryRefreshCalendar.set(12, 0);
                normalGroupHistoryRefreshCalendar.set(13, 0);
                DungeonDAO.deleteDungeonHistory(new Timestamp(normalGroupHistoryRefreshCalendar.getTimeInMillis()), new Timestamp(raidHistoryRefreshCalendar.getTimeInMillis()));
                synchronized (DungeonHistoryManager.this.historyList) {
                    Calendar raidHistoryCalendar = Calendar.getInstance();
                    int i = 0;
                    while (i < DungeonHistoryManager.this.historyList.size()) {
                        DungeonHistory history = (DungeonHistory) DungeonHistoryManager.this.historyList.get(i);
                        if (history.getDungeonType() == 2) {
                            raidHistoryCalendar.setTime(history.getBuildTime());
                            if (raidHistoryCalendar.before(raidHistoryRefreshCalendar)) {
                                DungeonHistoryManager.this.historyList.remove(i);
                                DungeonHistoryManager.this.historyTable.remove(history.getID());
                                DungeonInstanceManager.getInstance().clearHistoryDungeon(history.getID());
                            } else {
                                ++i;
                            }
                        } else {
                            DungeonHistoryManager.this.historyList.remove(i);
                            DungeonHistoryManager.this.historyTable.remove(history.getID());
                            DungeonInstanceManager.getInstance().clearHistoryDungeon(history.getID());
                        }
                    }
                }
                // monitorexit(DungeonHistoryManager.access$0(this.this$0))
            }
            DungeonHistoryManager.this.resetDungeonRefreshTime();
        }
    }
}
