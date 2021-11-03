// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import hero.player.service.PlayerServiceImpl;
import hero.player.service.PlayerConfig;
import hero.share.EVocation;
import yoyo.service.tools.database.DBServiceImpl;
import hero.share.RankInfo;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import hero.gm.service.GmServiceImpl;
import hero.share.Inotice;
import java.util.List;
import org.apache.log4j.Logger;

public class ShareDAO {

    private static Logger log;
    private static final String RANK_LEVEL = "select user_id,nickname,vocation,lvl from player order by lvl desc,total_play_time desc limit 20";
    private static final String RANK_LEVEL_SINGLE_VOCATION = "select user_id,nickname,vocation,lvl from player where vocation=? order by lvl desc,total_play_time desc limit 20";
    private static final String RANK_LEVEL_TWO_VOCATION = "select user_id,nickname,vocation,lvl from player where vocation=? or vocation=? order by lvl desc,total_play_time desc limit 20";
    private static final String RANK_MONEY = "select user_id,nickname,vocation,money from player order by money desc,total_play_time desc limit 20";
    private static final String RANK_MONEY_VOCATION = "select user_id,nickname,vocation,money from player where vocation=? order by money desc,total_play_time desc limit 20";
    private static final String RANK_MONEY_TWO_VOCATION = "select user_id,nickname,vocation,money from player where vocation=? or vocation=? order by money desc,total_play_time desc limit 20";
    private static final String RANK_LOVER_VALUE = "select user_id,nickname,vocation,lover_value from player where lover_value>0 order by lover_value desc,total_play_time desc limit 20";
    private static final String RANK_LOVER_VALUE_VOCATION = "select user_id,nickname,vocation,lover_value from player where vocation=? and lover_value>0 order by lover_value desc,total_play_time desc limit 20";
    private static final String RANK_LOVER_VALUE_TWO_VOCATION = "select user_id,nickname,vocation,lover_value from player where (vocation=? or vocation=?) and lover_value>0 order by lover_value desc,total_play_time desc limit 20";
    private static String SUB_DATE_SQL;
    private static String RANK_KILLER;
    private static String RANK_KILLER_VOCATION;
    private static String RANK_KILLER_TWO_VOCATION;
    private static final String RANK_POWER = "select t.user_id,t.nickname,t.vocation,t.power_value from player t order by t.power_value desc,t.total_play_time desc limit 20";
    private static final String RANK_POWER_VOCATION = "select t.user_id,t.nickname,t.vocation,t.power_value from player t where t.vocation=? order by t.power_value desc,t.total_play_time desc limit 20";
    private static final String RANK_POWER_TWO_VOCATION = "select t.user_id,t.nickname,t.vocation,t.power_value from player t where t.vocation=? or t.vocation=? order by t.power_value desc,t.total_play_time desc limit 20";
    private static final String RANK_GUILD = "SELECT g.id,g.name,g.level,COUNT(*) AS num FROM guild_member m,(SELECT id,name,level FROM guild) g WHERE m.guild_id=g.id GROUP BY g.id,g.level,g.name ORDER BY g.level DESC,num DESC ";
    private static String SELECT_INDEX_NOTICE;

    static {
        ShareDAO.log = Logger.getLogger((Class) ShareDAO.class);
        ShareDAO.SUB_DATE_SQL = "t.create_time>=DATE_SUB(CURDATE(),INTERVAL WEEKDAY(CURDATE()) DAY) AND t.create_time<DATE_ADD(DATE_SUB(CURDATE(),INTERVAL WEEKDAY(CURDATE()) DAY),INTERVAL 1 WEEK)";
        ShareDAO.RANK_KILLER = "select p.user_id,p.num,t.nickname,t.vocation from(select count(*) as num,winner_user_id as user_id from pvp t where " + ShareDAO.SUB_DATE_SQL + " group by winner_user_id) p," + "player t where p.user_id=t.user_id order by p.num desc,t.total_play_time desc limit 30";
        ShareDAO.RANK_KILLER_VOCATION = "select p.user_id,p.num,t.nickname,t.vocation from(select count(*) as num,winner_user_id as user_id from pvp t where winner_vocation=? and " + ShareDAO.SUB_DATE_SQL + " group by winner_user_id) p," + "player t where p.user_id=t.user_id order by p.num desc,t.total_play_time desc limit 30";
        ShareDAO.RANK_KILLER_TWO_VOCATION = "select p.user_id,p.num,t.nickname,t.vocation from(select count(*) as num,winner_user_id as user_id from pvp t where (winner_vocation=? or winner_vocation=?) and " + ShareDAO.SUB_DATE_SQL + " group by winner_user_id) p," + "player t where p.user_id=t.user_id order by p.num desc,t.total_play_time desc limit 30";
        ShareDAO.SELECT_INDEX_NOTICE = "select t.id,t.title,t.content,t.is_top,t.color from index_notice t where (t.server_id=0 or t.server_id=?)  %ftype and t.is_show=1 order by t.is_top asc,t.sequence asc,t.update_time desc";
    }

    public static List<Inotice> getInoticeList(final int type) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Inotice> inoticeList = null;
        try {
            if (type > 0) {
                ShareDAO.SELECT_INDEX_NOTICE = ShareDAO.SELECT_INDEX_NOTICE.replaceAll("%ftype", " and type=" + type);
            } else {
                ShareDAO.SELECT_INDEX_NOTICE = ShareDAO.SELECT_INDEX_NOTICE.replaceAll("%ftype", "");
            }
            conn = GmServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(ShareDAO.SELECT_INDEX_NOTICE);
            ps.setInt(1, GmServiceImpl.serverID);
            rs = ps.executeQuery();
            inoticeList = new ArrayList<Inotice>();
            while (rs.next()) {
                Inotice inotice = new Inotice();
                inotice.id = rs.getInt("id");
                inotice.title = rs.getString("title");
                inotice.content = rs.getString("content");
                inotice.top = rs.getInt("is_top");
                inotice.color = ShareServiceImpl.getInstance().hexStr2Int(rs.getString("color"));
                inoticeList.add(inotice);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            ShareDAO.log.error((Object) "\u83b7\u53d6\u516c\u544a/\u6d3b\u52a8\u5217\u8868 error:", (Throwable) e);
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return inoticeList;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return inoticeList;
    }

    public static boolean isByUse(final String _tableName, final String[] _columnName, final String[] _key) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String RECEIVE_IS_BY_USE = "SELECT receive_account_id FROM %ftable WHERE ".replaceAll("%ftable", _tableName);
        try {
            for (int i = 0; i < _columnName.length; ++i) {
                if (i == 0) {
                    RECEIVE_IS_BY_USE = String.valueOf(RECEIVE_IS_BY_USE) + " " + _columnName[i] + " = ? ";
                } else {
                    RECEIVE_IS_BY_USE = String.valueOf(RECEIVE_IS_BY_USE) + " and " + _columnName[i] + " = ? ";
                }
            }
            conn = ShareServiceImpl.getInstance().getResourceConnection();
            ps = conn.prepareStatement(RECEIVE_IS_BY_USE);
            for (int i = 0; i < _key.length; ++i) {
                ps.setString(i + 1, _key[i]);
            }
            rs = ps.executeQuery();
            int receAccount = -1;
            while (rs.next()) {
                receAccount = rs.getInt("receive_account_id");
                if (receAccount > 0) {
                    result = true;
                    break;
                }
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            ShareDAO.log.error((Object) "\u67e5\u8be2\u662f\u5426\u5df2\u7ecf\u4f7f\u7528\u5931\u8d25", (Throwable) e);
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return result;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return result;
    }

    public static boolean isJoinIt(final String _tableName, final int _nowAccountID) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String RECEIVE_IS_JOIN_IT = "SELECT * FROM %ftable WHERE receive_account_id = ?";
        try {
            RECEIVE_IS_JOIN_IT = RECEIVE_IS_JOIN_IT.replaceAll("%ftable", _tableName);
            conn = ShareServiceImpl.getInstance().getResourceConnection();
            ps = conn.prepareStatement(RECEIVE_IS_JOIN_IT);
            ps.setInt(1, _nowAccountID);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = true;
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            ShareDAO.log.error((Object) "\u67e5\u8be2\u662f\u5426\u5df2\u7ecf\u9886\u53d6\u5931\u8d25", (Throwable) e);
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return result;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return result;
    }

    public static boolean InputVerify(final String _tableName, final String[] _columnName, final String[] _key) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String RECEIVE_BY_INPUT = "SELECT count(1) as row FROM %ftable WHERE ".replaceAll("%ftable", _tableName);
        try {
            for (int i = 0; i < _columnName.length; ++i) {
                if (i == 0) {
                    RECEIVE_BY_INPUT = String.valueOf(RECEIVE_BY_INPUT) + " " + _columnName[i] + " = ? ";
                } else {
                    RECEIVE_BY_INPUT = String.valueOf(RECEIVE_BY_INPUT) + " and " + _columnName[i] + " = ? ";
                }
            }
            conn = ShareServiceImpl.getInstance().getResourceConnection();
            ps = conn.prepareStatement(RECEIVE_BY_INPUT);
            for (int i = 0; i < _key.length; ++i) {
                ps.setString(i + 1, _key[i]);
            }
            rs = ps.executeQuery();
            int receAccount = -1;
            while (rs.next()) {
                receAccount = rs.getInt("row");
                if (receAccount > 0) {
                    result = true;
                    break;
                }
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            ShareDAO.log.error((Object) "\u67e5\u8be2\u662f\u5426\u5df2\u7ecf\u4f7f\u7528\u5931\u8d25", (Throwable) e);
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return result;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return result;
    }

    public static boolean updateEvidenveRece(final String _tableName, final String[] _columnName, final String[] _key, final int _accountID, final int _userID) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String UPDATE_EVIDENVE_RECEIVE = "UPDATE %ftable SET receive_account_id = ?, receive_user_id = ? WHERE ".replaceAll("%ftable", _tableName);
        try {
            for (int i = 0; i < _columnName.length; ++i) {
                if (i == 0) {
                    UPDATE_EVIDENVE_RECEIVE = String.valueOf(UPDATE_EVIDENVE_RECEIVE) + " " + _columnName[i] + " = ? ";
                } else {
                    UPDATE_EVIDENVE_RECEIVE = String.valueOf(UPDATE_EVIDENVE_RECEIVE) + " and " + _columnName[i] + " = ? ";
                }
            }
            conn = ShareServiceImpl.getInstance().getResourceConnection();
            ps = conn.prepareStatement(UPDATE_EVIDENVE_RECEIVE);
            ps.setInt(1, _accountID);
            ps.setInt(2, _userID);
            for (int i = 0; i < _key.length; ++i) {
                ps.setString(i + 3, _key[i]);
            }
            if (ps.executeUpdate() > 0) {
                result = true;
            }
            ps.close();
            conn.close();
        } catch (Exception e) {
            ShareDAO.log.error((Object) "\u66f4\u65b0\u5df2\u7ecf\u9886\u53d6\u6570\u636e\u5931\u8d25", (Throwable) e);
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return result;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return result;
    }

    public static List<RankInfo> getGuildRankList() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RankInfo> rankInfoList = new ArrayList<RankInfo>();
        RankInfo rankInfo = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("SELECT g.id,g.name,g.level,COUNT(*) AS num FROM guild_member m,(SELECT id,name,level FROM guild) g WHERE m.guild_id=g.id GROUP BY g.id,g.level,g.name ORDER BY g.level DESC,num DESC ");
            rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                String name = rs.getString("name");
                byte guildLevel = rs.getByte("level");
                int value = rs.getInt("num");
                rankInfo = new RankInfo();
                rankInfo.rank = i;
                rankInfo.userID = 0;
                rankInfo.name = name;
                rankInfo.vocation = new StringBuilder(String.valueOf(guildLevel)).toString();
                rankInfo.value = value;
                rankInfoList.add(rankInfo);
                ++i;
            }
        } catch (Exception e) {
            ShareDAO.log.error((Object) "\u5e2e\u6d3e\u6392\u884c error: ", (Throwable) e);
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return rankInfoList;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return rankInfoList;
    }

    public static List<RankInfo> getPowerRankList(final int vocation1, final int vocation2, final boolean moreVocations) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RankInfo> rankInfoList = new ArrayList<RankInfo>();
        RankInfo rankInfo = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            if (moreVocations) {
                ps = conn.prepareStatement("select t.user_id,t.nickname,t.vocation,t.power_value from player t where t.vocation=? or t.vocation=? order by t.power_value desc,t.total_play_time desc limit 20");
                ps.setInt(1, vocation1);
                ps.setInt(2, vocation2);
            } else if (vocation1 == 0) {
                ps = conn.prepareStatement("select t.user_id,t.nickname,t.vocation,t.power_value from player t order by t.power_value desc,t.total_play_time desc limit 20");
            } else {
                ps = conn.prepareStatement("select t.user_id,t.nickname,t.vocation,t.power_value from player t where t.vocation=? order by t.power_value desc,t.total_play_time desc limit 20");
                ps.setInt(1, vocation1);
            }
            rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                String name = rs.getString("nickname");
                byte vocationValue = rs.getByte("vocation");
                int userID = rs.getInt("user_id");
                int value = rs.getInt("power_value");
                rankInfo = new RankInfo();
                rankInfo.rank = i;
                rankInfo.userID = userID;
                rankInfo.name = name;
                rankInfo.vocation = EVocation.getVocationByID(vocationValue).getDesc();
                rankInfo.value = value;
                rankInfoList.add(rankInfo);
                ++i;
            }
        } catch (Exception e) {
            ShareDAO.log.error((Object) "\u5b9e\u529b\u6392\u884c error: ", (Throwable) e);
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return rankInfoList;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return rankInfoList;
    }

    public static List<RankInfo> getKillerRankInfoList(final int vocation1, final int vocation2, final boolean moreVocations) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RankInfo> rankInfoList = new ArrayList<RankInfo>();
        RankInfo rankInfo = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            if (moreVocations) {
                ps = conn.prepareStatement(ShareDAO.RANK_KILLER_TWO_VOCATION);
                ps.setInt(1, vocation1);
                ps.setInt(2, vocation2);
            } else if (vocation1 == 0) {
                ps = conn.prepareStatement(ShareDAO.RANK_KILLER);
            } else {
                ps = conn.prepareStatement(ShareDAO.RANK_KILLER_VOCATION);
                ps.setInt(1, vocation1);
            }
            rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                String name = rs.getString("nickname");
                int value = rs.getInt("num");
                byte vocationValue = rs.getByte("vocation");
                int userID = rs.getInt("user_id");
                rankInfo = new RankInfo();
                rankInfo.rank = i;
                rankInfo.userID = userID;
                rankInfo.name = name;
                rankInfo.value = value;
                rankInfo.vocation = EVocation.getVocationByID(vocationValue).getDesc();
                rankInfoList.add(rankInfo);
                ++i;
            }
        } catch (Exception e) {
            ShareDAO.log.error((Object) "\u6740\u654c\u6392\u884c error: ", (Throwable) e);
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return rankInfoList;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return rankInfoList;
    }

    public static List<RankInfo> getLevelRankInfoList(final int vocation1, final int vocation2, final boolean moreVocations) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RankInfo> rankInfoList = new ArrayList<RankInfo>();
        RankInfo rankInfo = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            if (moreVocations) {
                ps = conn.prepareStatement("select user_id,nickname,vocation,lvl from player where vocation=? or vocation=? order by lvl desc,total_play_time desc limit 20");
                ps.setInt(1, vocation1);
                ps.setInt(2, vocation2);
            } else if (vocation1 == 0) {
                ps = conn.prepareStatement("select user_id,nickname,vocation,lvl from player order by lvl desc,total_play_time desc limit 20");
            } else {
                ps = conn.prepareStatement("select user_id,nickname,vocation,lvl from player where vocation=? order by lvl desc,total_play_time desc limit 20");
                ps.setInt(1, vocation1);
            }
            rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                String name = rs.getString("nickname");
                int value = rs.getInt("lvl");
                byte vocationValue = rs.getByte("vocation");
                int userID = rs.getInt("user_id");
                if (value > PlayerServiceImpl.getInstance().getConfig().max_level) {
                    value = PlayerServiceImpl.getInstance().getConfig().max_level;
                }
                rankInfo = new RankInfo();
                rankInfo.rank = i;
                rankInfo.userID = userID;
                rankInfo.name = name;
                rankInfo.value = value;
                rankInfo.vocation = EVocation.getVocationByID(vocationValue).getDesc();
                rankInfoList.add(rankInfo);
                ++i;
            }
        } catch (Exception e) {
            ShareDAO.log.error((Object) "\u7b49\u7ea7\u6392\u884c error: ", (Throwable) e);
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return rankInfoList;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return rankInfoList;
    }

    public static List<RankInfo> getMoneyRankInfoList(final int vocation1, final int vocation2, final boolean moreVocations) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RankInfo> rankInfoList = new ArrayList<RankInfo>();
        RankInfo rankInfo = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            if (moreVocations) {
                ps = conn.prepareStatement("select user_id,nickname,vocation,money from player where vocation=? or vocation=? order by money desc,total_play_time desc limit 20");
                ps.setInt(1, vocation1);
                ps.setInt(2, vocation2);
            } else if (vocation1 == 0) {
                ps = conn.prepareStatement("select user_id,nickname,vocation,money from player order by money desc,total_play_time desc limit 20");
            } else {
                ps = conn.prepareStatement("select user_id,nickname,vocation,money from player where vocation=? order by money desc,total_play_time desc limit 20");
                ps.setInt(1, vocation1);
            }
            rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                String name = rs.getString("nickname");
                int value = rs.getInt("money");
                byte vocationValue = rs.getByte("vocation");
                int userID = rs.getInt("user_id");
                rankInfo = new RankInfo();
                rankInfo.rank = i;
                rankInfo.userID = userID;
                rankInfo.name = name;
                rankInfo.vocation = EVocation.getVocationByID(vocationValue).getDesc();
                rankInfo.value = value;
                rankInfoList.add(rankInfo);
                ++i;
            }
        } catch (Exception e) {
            ShareDAO.log.error((Object) "\u8d22\u5bcc\u6392\u884c error: ", (Throwable) e);
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return rankInfoList;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return rankInfoList;
    }

    public static List<RankInfo> getLoverValueRankInfoList(final int vocation1, final int vocation2, final boolean moreVocations) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RankInfo> rankInfoList = new ArrayList<RankInfo>();
        RankInfo rankInfo = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            if (moreVocations) {
                ps = conn.prepareStatement("select user_id,nickname,vocation,lover_value from player where (vocation=? or vocation=?) and lover_value>0 order by lover_value desc,total_play_time desc limit 20");
                ps.setInt(1, vocation1);
                ps.setInt(2, vocation2);
            } else if (vocation1 == 0) {
                ps = conn.prepareStatement("select user_id,nickname,vocation,lover_value from player where lover_value>0 order by lover_value desc,total_play_time desc limit 20");
            } else {
                ps = conn.prepareStatement("select user_id,nickname,vocation,lover_value from player where vocation=? and lover_value>0 order by lover_value desc,total_play_time desc limit 20");
                ps.setInt(1, vocation1);
            }
            rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                String name = rs.getString("nickname");
                int value = rs.getInt("lover_value");
                byte vocationValue = rs.getByte("vocation");
                int userID = rs.getInt("user_id");
                rankInfo = new RankInfo();
                rankInfo.rank = i;
                rankInfo.userID = userID;
                rankInfo.name = name;
                rankInfo.vocation = EVocation.getVocationByID(vocationValue).getDesc();
                rankInfo.value = value;
                rankInfoList.add(rankInfo);
                ++i;
            }
        } catch (Exception e) {
            ShareDAO.log.error((Object) "\u7231\u60c5\u6392\u884c error: ", (Throwable) e);
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return rankInfoList;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return rankInfoList;
    }
}
