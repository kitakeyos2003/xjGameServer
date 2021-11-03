// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.sql.Timestamp;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import hero.share.service.LogWriter;
import hero.dungeon.DungeonHistory;
import yoyo.service.tools.database.DBServiceImpl;

public class DungeonDAO {

    private static final String SELECT_HISTORY_SQL = "SELECT * FROM dungeon_history";
    private static final String INSERT_HISTORY_SQL = "INSERT INTO dungeon_history(history_id,dungeon_id,pattern,type,death_boss_list,include_player_list) values(?,?,?,?,?,?)";
    private static final String UPDATE_HISTORY_SQL = "UPDATE dungeon_history SET death_boss_list=? ,include_player_list=? WHERE history_id=? LIMIT 1";
    private static final String DELETE_HISTORY_SQL = "DELETE FROM dungeon_history WHERE (type=1 AND build_time<?) OR (type=2 AND build_time<?)";
    private static final String MONSTER_AND_MAP_CONNECTOR = "#";
    private static final String SAME_ELEMENT_CONNECTOR = "&";

    public static int loadDungeonHistory() {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        int maxDungeonHistoryID = 1000;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM dungeon_history");
            set = pstm.executeQuery();
            while (set.next()) {
                int historyID = set.getInt("history_id");
                int dungeonID = set.getInt("dungeon_id");
                byte pattern = set.getByte("pattern");
                byte type = set.getByte("type");
                String deathBossList = set.getString("death_boss_list");
                String includePlayer = set.getString("include_player_list");
                Date buildTime = set.getTimestamp("build_time");
                DungeonHistory history = new DungeonHistory(historyID, dungeonID, pattern, type, buildTime);
                parseDeathBossList(deathBossList, history);
                parsePlayerList(includePlayer, history);
                DungeonHistoryManager.getInstance().addDungeonHistory(history);
                if (historyID > maxDungeonHistoryID) {
                    maxDungeonHistoryID = historyID;
                }
            }
            DungeonHistory history = null;
            return maxDungeonHistoryID;
        } catch (Exception e) {
            LogWriter.error(null, e);
            return -1;
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    public static void buildDungeonHistory(final DungeonHistory _history) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO dungeon_history(history_id,dungeon_id,pattern,type,death_boss_list,include_player_list) values(?,?,?,?,?,?)");
            pstm.setInt(1, _history.getID());
            pstm.setInt(2, _history.getDungeonID());
            pstm.setByte(3, _history.getPattern());
            pstm.setByte(4, _history.getDungeonType());
            String dataChars = formatDeathBossList(_history.getDeathBossTable());
            if (dataChars != null) {
                pstm.setString(5, dataChars);
                dataChars = formatPlayerUserIDList(_history.getIncludePlayerUserIDList());
                if (dataChars != null) {
                    pstm.setString(6, dataChars);
                    pstm.executeUpdate();
                }
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex2) {
        }
    }

    public static void changeDungeonHistoryContent(final DungeonHistory _history) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE dungeon_history SET death_boss_list=? ,include_player_list=? WHERE history_id=? LIMIT 1");
            String dataChars = formatDeathBossList(_history.getDeathBossTable());
            if (dataChars != null) {
                pstm.setString(1, dataChars);
                dataChars = formatPlayerUserIDList(_history.getIncludePlayerUserIDList());
                if (dataChars != null) {
                    pstm.setString(2, dataChars);
                    pstm.setInt(3, _history.getID());
                    pstm.executeUpdate();
                }
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex2) {
        }
    }

    public static void deleteDungeonHistory(final Timestamp _normalGroudHistoryTime, final Timestamp _raidHistoryTime) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM dungeon_history WHERE (type=1 AND build_time<?) OR (type=2 AND build_time<?)");
            pstm.setTimestamp(1, _normalGroudHistoryTime);
            pstm.setTimestamp(2, _raidHistoryTime);
            pstm.executeUpdate();
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex2) {
        }
    }

    private static void parseDeathBossList(final String _deathBossListChar, final DungeonHistory _history) {
        String[] bossInfoList = _deathBossListChar.split("&");
        String[] array;
        for (int length = (array = bossInfoList).length, i = 0; i < length; ++i) {
            String bossInfo = array[i];
            String[] detail = bossInfo.split("#");
            _history.addDeathBoss(detail[0], Short.parseShort(detail[1]));
        }
    }

    private static String formatPlayerUserIDList(final Vector<Integer> _includePlayerUserIDList) {
        if (_includePlayerUserIDList != null && _includePlayerUserIDList.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (final int userID : _includePlayerUserIDList) {
                sb.append(userID).append("&");
            }
            return sb.toString();
        }
        return null;
    }

    private static void parsePlayerList(final String _playerListChar, final DungeonHistory _history) {
        String[] playerIDList = _playerListChar.split("&");
        String[] array;
        for (int length = (array = playerIDList).length, i = 0; i < length; ++i) {
            String userID = array[i];
            _history.addPlayer(Integer.parseInt(userID));
        }
    }

    private static String formatDeathBossList(final HashMap<String, Short> _deathBossTable) {
        if (_deathBossTable != null && _deathBossTable.size() > 0) {
            StringBuffer defaultChar = new StringBuffer();
            for (final String monsterModelID : _deathBossTable.keySet()) {
                defaultChar.append(monsterModelID).append("#").append(_deathBossTable.get(monsterModelID)).append("&");
            }
            return defaultChar.toString();
        }
        return null;
    }
}
