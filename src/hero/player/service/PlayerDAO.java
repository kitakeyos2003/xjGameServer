// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.service;

import java.util.Iterator;
import hero.skill.Skill;
import hero.skill.dict.SkillDict;
import hero.item.service.EquipmentFactory;
import hero.item.EquipmentInstance;
import java.util.ArrayList;
import javolution.util.FastList;
import java.sql.Statement;
import hero.gm.service.GmServiceImpl;
import hero.share.service.ShareServiceImpl;
import java.sql.Timestamp;
import hero.map.service.MapServiceImpl;
import hero.map.service.MapRelationDict;
import hero.share.EObjectLevel;
import hero.item.detail.EGoodsTrait;
import hero.expressions.service.CEService;
import hero.player.HeroPlayer;
import yoyo.service.base.player.IPlayer;
import hero.log.service.LogServiceImpl;
import hero.player.HeroRoleView;
import hero.player.define.ESex;
import hero.player.define.EClan;
import hero.share.service.LogWriter;
import java.util.HashMap;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import yoyo.service.tools.database.DBServiceImpl;
import hero.share.EVocation;
import org.apache.log4j.Logger;
import yoyo.service.base.player.IPlayerDAO;

public class PlayerDAO implements IPlayerDAO {

    private static Logger log;
    private static final String INSERT_PLAYER_SQL = "INSERT INTO player(user_id,account_id,server_id,nickname,sex,clan,vocation,lvl,money,hp,mp,where_id,where_x,where_y,home_id,surplus_skill_point,clan_chat_time,world_chat_time)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_PLAYER_SQL = "UPDATE player SET vocation=?,lvl=?,money=?,exp=?,hp=?,mp=?,where_id=?,where_x=?,where_y=?,total_play_time=?,last_logout_time=?,surplus_skill_point=?,lover_value=?,last_login_time=?,power_value=?,receive_repeate_task_times=? where user_id=? LIMIT 1";
    private static final String UPDATE_NOVICE_SQL = "UPDATE player SET novice=0 where user_id=? LIMIT 1";
    private static final String UPDATE_HEAVEN_BOOK = "UPDATE player t SET t.heaven_book_1=?,t.heaven_book_2=?,t.heaven_book_3=?,t.surplus_skill_point=?  WHERE t.user_id=?";
    private static final String UPDATE_AUTO_SELL_TRAIT_SQL = "UPDATE player SET auto_sell_trait=? where user_id=? LIMIT 1";
    private static final String INSERT_PLAYER_BODY_EQUIPMENT_SQL = "INSERT INTO player_carry_equipment(instance_id,user_id,container_type) VALUES (?,?,?)";
    private static final String INSERT_EQUIPMENT_INSTANCE_SQL = "INSERT INTO equipment_instance(instance_id,equipment_id,creator_user_id,owner_user_id,current_durability, bind) VALUES (?,?,?,?,?,?)";
    private static final String DELETE_PLAYER_SQL = "DELETE FROM player WHERE user_id=? limit 1";
    private static final String DELETE_PLAYER_EQUIPMENT_SQL = "DELETE FROM player_carry_equipment WHERE user_id=?";
    private static final String DELETE_PLAYER_EQUIPMENT_INSTANCE_SQL = "DELETE FROM equipment_instance WHERE owner_user_id=?";
    private static final String SELECT_PLAYER_SQL = "SELECT account_id,nickname,sex,clan,vocation,lvl,money,exp,hp,mp,novice,where_id,where_x,where_y,home_id,bag_size,auto_sell_trait,total_play_time,last_logout_time,msisdn, surplus_skill_point,left_master_time,lover_value, last_receive_gift, heaven_book_1,heaven_book_2,heaven_book_3,server_id,receive_repeate_task_times FROM player WHERE user_id=? LIMIT 1";
    private static final String SELECT_PLAYER_LAST_GIFT_SQL = "SELECT last_receive_gift FROM player WHERE user_id=? LIMIT 1";
    private static final String UPDATE_PLAYER_LAST_GIFT_SQL = "UPDATE player set last_receive_gift=?  WHERE user_id=? LIMIT 1";
    private static final String SELECT_PLAYER_GMTOOL_SQL = "SELECT account_id,nickname,sex,clan,vocation,lvl,money,exp,last_login_time,last_logout_time FROM player WHERE user_id=? LIMIT 1";
    private static final String SELECT_PLAYER_OFF_LINE_SQL = "select user_id,sex,clan,vocation,lvl,account_id,money,exp from player where nickname=? limit 1";
    private static final String INSERT_MEDICAMENT_SQL = "INSERT INTO player_single_goods(user_id,goods_type,goods_id,goods_number,package_index) VALUES (?,?,?,?,?)";
    private static final String INSERT_SHORTCUT_KEY_SQL = "INSERT INTO player_shortcut_key(user_id,shortcut_key) VALUES (?,?)";
    private static final String UPDATE_SHORTCUT_KEY_SQL = "UPDATE player_shortcut_key SET shortcut_key = ? WHERE user_id = ? LIMIT 1";
    private static final String SELECT_SHORTCUT_KEY_SQL = "SELECT * FROM player_shortcut_key WHERE user_id = ? LIMIT 1";
    private static final String INSERT_SKILL_SQL = "INSERT INTO player_skill (user_id,skill_id) VALUES (?,?)";
    private static final String SELECT_PLAYER_LEVEL_SQL = "SELECT lvl FROM player WHERE user_id=? LIMIT 1";
    private static final String UPDATE_PLAYER_PROPERTY_SQL = "UPDATE player SET lvl=?,vocation=?,money=?,exp=? WHERE user_id=? LIMIT 1";
    private static final String SELECT_USERNAME_FROM_ACCOUNTID = "SELECT username,curr_publisher,msisdn,client_version,agent,client_jar_type,bind_msisdn,password FROM account WHERE account_id=? LIMIT 1";
    private static final String UDPATE_PLAYER_LEFT_MASTER_TIME = "UPDATE player t SET t.left_master_time=? where t.user_id=?";
    private static final String UPDATE_WORLD_CHAT = "update player set world_chat_time = ?  where user_id = ?";
    private static final String UPDATE_CLAN_CHAT = "update player set clan_chat_time = ?  where user_id = ?";
    private static final String SELECT_CLAN_CHAT = "select clan_chat_time from player where user_id = ?";
    private static final String SELECT_WORLD_CHAT = "select world_chat_time from player where user_id = ?";
    private static final String SELECT_CHAT_BLANK = "select * from chat_black where account_id=? and user_id = ?";
    private static final String INSERT_ROLE_BLANK = "insert into role_black(user_id,nickname,keep_time,start_time,end_time,memo) values(?,?,?,?,?,?)";
    private static final String INSERT_ACCOUNT_BLANK = "insert into account_black(account_id,username,keep_time,start_time,end_time,memo) values(?,?,?,?,?,?)";
    private static final String INSERT_CHAT_BLANK = "insert into chat_black(user_id,nickname,keep_time,start_time,end_time,memo) values(?,?,?,?,?,?)";
    private static final String DELETE_FORBID_ACCOUNT_SQL = "DELETE FROM account_black WHERE account_id=?";
    private static final String DELETE_FORBID_ROLE_SQL = "DELETE FROM role_black WHERE user_id=?";
    private static final String DELETE_CHAT_BLANK_SQL = "delete from chat_black where user_id=?";
    private static final String PLAYER_LOVER_ORDER = "select t.user_id from player t where t.lover_value>0 order by t.lover_value desc,t.lvl desc";
    private static final String INERT_PVP_INFO = "insert into pvp(winner_user_id,winner_vocation,failer_user_id,failer_vocation) value(?,?,?,?)";
    private static final String ADD_REPEATE_TASK_GOODS = "insert into repeate_task_tools(user_id,goods_id,max_times) values(?,?,?)";
    private static final String SELECT_REPEATE_TASK_GOODS_TIMES = "select sum(t.max_times) from repeate_task_tools t where t.user_id=? and DATE_FORMAT(t.create_time,'%Y%m%d')=DATE_FORMAT(NOW(),'%Y%m%d')";
    private static final String CLEAR_PLAYER_RECEIVE_REPEATE_TASK_TIMES = "update player t set t.receive_repeate_task_times=0 where t.receive_repeate_task_times>0";
    private static final String DELETE_REPEATE_TASK_TOOLS = "delete from repeate_task_tools";
    private static final String UPDATE_PLAYER_LOVER_VALUE = "update player t set t.lover_value=? where t.user_id=?";
    private static final String COUNT_WINNER_NUMBER = "select count(*) from pvp t where t.winner_user_id=?";
    private static final String COUNT_FAILER_NUMBER = "select count(*) from pvp t where t.failer_user_id=?";
    private static final String EXITS_NICKNAME = "SELECT * FROM player WHERE nickname like BINARY ? LIMIT 1";
    private static String DEFAULT_SHORTCUT_KEY_DESC;
    private static final String BAG_SIZE_CONNECTOR = "&";
    private static int INTERVAL_OF_UPDATE_DB;
    private static final String SHORTCUT_KEY_SEPARATOR = "#";
    private static final String SHORTCUT_KEY_CONNECTOR = "&";
    private static final int[][] FIELD_FIXED_SHORTCUT_KEY;
    private static final byte[][] SECOND_FIELD_FIXED_SHORTCUT_KEY;
    private static final byte[][] KEY_OF_WALKING;
    private static final String DEFAULT_KEY_OF_WALKING_DESC = "4&1&5#5&1&8#6&1&14#7&1&24#10&1&1#11&1&2#12&1&3#13&1&4#14&1&6#15&1&9#16&1&11#17&1&16#18&1&10#23&1&1#24&1&2#25&1&3#26&1&4#";
    public static final long DEFAULT_LOGOUT_TIME = 315504000000L;

    static {
        PlayerDAO.log = Logger.getLogger((Class) PlayerDAO.class);
        PlayerDAO.INTERVAL_OF_UPDATE_DB = 60000;
        FIELD_FIXED_SHORTCUT_KEY = new int[][]{{1, 2, 1}, {2, 3, 0}, {3, 3, 0}, {4, 1, 15}, {5, 1, 14}, {8, 1, 24}};
        SECOND_FIELD_FIXED_SHORTCUT_KEY = new byte[][]{{1, 1, 8}, {2, 1, 17}, {3, 1, 9}, {4, 1, 16}};
        KEY_OF_WALKING = new byte[][]{{10, 1, 1}, {11, 1, 2}, {12, 1, 3}, {13, 1, 4}};
    }

    private static final void initHotKeyByVocation(final EVocation _vocation) {
        StringBuffer defaultChar = new StringBuffer();
        int initSkillID = PlayerServiceImpl.getInstance().getConfig().getInitSkill(_vocation);
        PlayerDAO.FIELD_FIXED_SHORTCUT_KEY[0][2] = initSkillID;
        PlayerDAO.FIELD_FIXED_SHORTCUT_KEY[1][2] = PlayerServiceImpl.getInstance().getConfig().default_red_medicament;
        PlayerDAO.FIELD_FIXED_SHORTCUT_KEY[2][2] = PlayerServiceImpl.getInstance().getConfig().default_blue_medicament;
        for (int i = 0; i < PlayerDAO.FIELD_FIXED_SHORTCUT_KEY.length; ++i) {
            defaultChar.append(PlayerDAO.FIELD_FIXED_SHORTCUT_KEY[i][0]).append("&").append(PlayerDAO.FIELD_FIXED_SHORTCUT_KEY[i][1]).append("&").append(PlayerDAO.FIELD_FIXED_SHORTCUT_KEY[i][2]).append("#");
        }
        for (int i = 0; i < PlayerDAO.KEY_OF_WALKING.length; ++i) {
            defaultChar.append(PlayerDAO.KEY_OF_WALKING[i][0]).append("&").append(PlayerDAO.KEY_OF_WALKING[i][1]).append("&").append(PlayerDAO.KEY_OF_WALKING[i][2]).append("#");
        }
        for (int i = 0; i < PlayerDAO.SECOND_FIELD_FIXED_SHORTCUT_KEY.length; ++i) {
            defaultChar.append(PlayerDAO.SECOND_FIELD_FIXED_SHORTCUT_KEY[i][0] + 13).append("&").append(PlayerDAO.SECOND_FIELD_FIXED_SHORTCUT_KEY[i][1]).append("&").append(PlayerDAO.SECOND_FIELD_FIXED_SHORTCUT_KEY[i][2]).append("#");
        }
        for (int i = 0; i < PlayerDAO.KEY_OF_WALKING.length; ++i) {
            defaultChar.append(PlayerDAO.KEY_OF_WALKING[i][0] + 13).append("&").append(PlayerDAO.KEY_OF_WALKING[i][1]).append("&").append(PlayerDAO.KEY_OF_WALKING[i][2]).append("#");
        }
        PlayerDAO.DEFAULT_SHORTCUT_KEY_DESC = defaultChar.toString();
    }

    public static int getPlayerFailerNumber(final int userID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int num = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("select count(*) from pvp t where t.failer_user_id=?");
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            if (rs.next()) {
                num = rs.getInt(1);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            PlayerDAO.log.error((Object) "\u83b7\u53d6\u73a9\u5bb6\u88ab\u6740\u6b21\u6570 error: ", (Throwable) e);
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
            return num;
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
        return num;
    }

    public static int getPlayerWinnerNumber(final int userID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int num = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("select count(*) from pvp t where t.winner_user_id=?");
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            if (rs.next()) {
                num = rs.getInt(1);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            PlayerDAO.log.error((Object) "\u83b7\u53d6\u73a9\u5bb6\u6740\u654c\u6b21\u6570 error: ", (Throwable) e);
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
            return num;
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
        return num;
    }

    public void updatePlayerLoverValue(final int userID, final int loverValue) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("update player t set t.lover_value=? where t.user_id=?");
            ps.setInt(1, loverValue);
            ps.setInt(2, userID);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            PlayerDAO.log.error((Object) "\u4fee\u6539\u73a9\u5bb6\u7231\u60c5\u503c error: ", (Throwable) e);
            try {
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
            return;
        } finally {
            try {
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

    public static void clearPlayerReceiveRepeatTaskTimes() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("update player t set t.receive_repeate_task_times=0 where t.receive_repeate_task_times>0");
            ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement("delete from repeate_task_tools");
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            PlayerDAO.log.error((Object) "\u5237\u65b0\u73a9\u5bb6\u63a5\u6536\u5faa\u73af\u4efb\u52a1\u7684\u6b21\u6570 error: ", (Throwable) e);
            try {
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
            return;
        } finally {
            try {
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

    public static int getRepeatTaskGoodsTimes(final int userID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int res = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("select sum(t.max_times) from repeate_task_tools t where t.user_id=? and DATE_FORMAT(t.create_time,'%Y%m%d')=DATE_FORMAT(NOW(),'%Y%m%d')");
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            if (rs.next()) {
                res = rs.getInt(1);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            PlayerDAO.log.error((Object) "\u83b7\u53d6\u73a9\u5bb6\u4f7f\u7528\u5faa\u73af\u4efb\u52a1\u9053\u5177\u6dfb\u52a0\u7684\u53ef\u63a5\u6536\u6b21\u6570 error: ", (Throwable) e);
            try {
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
            return res;
        } finally {
            try {
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
        return res;
    }

    public static int insertRepeatTaskGoods(final int userID, final int goodsID, final int maxTimes) {
        Connection conn = null;
        PreparedStatement ps = null;
        int res = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("insert into repeate_task_tools(user_id,goods_id,max_times) values(?,?,?)");
            ps.setInt(1, userID);
            ps.setInt(2, goodsID);
            ps.setInt(3, maxTimes);
            res = ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (Exception e) {
            PlayerDAO.log.error((Object) "\u7ed9\u73a9\u5bb6\u6dfb\u52a0\u5faa\u73af\u4efb\u52a1\u9053\u5177 error: ", (Throwable) e);
            try {
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
            return res;
        } finally {
            try {
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
        return res;
    }

    public static void insertPvpInfo(final int winnerUserID, final int winnerVocation, final int failerUserID, final int failerVocation) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("insert into pvp(winner_user_id,winner_vocation,failer_user_id,failer_vocation) value(?,?,?,?)");
            ps.setInt(1, winnerUserID);
            ps.setInt(2, winnerVocation);
            ps.setInt(3, failerUserID);
            ps.setInt(4, failerVocation);
            ps.executeUpdate();
        } catch (Exception e) {
            PlayerDAO.log.error((Object) "\u63d2\u5165\u73a9\u5bb6PK\u6570\u636e error: ", (Throwable) e);
            try {
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
            return;
        } finally {
            try {
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

    public Map<Integer, Integer> loverValueOrderMap() {
        Map<Integer, Integer> loverOrderMap = new HashMap<Integer, Integer>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("select t.user_id from player t where t.lover_value>0 order by t.lover_value desc,t.lvl desc");
            rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                int userID = rs.getInt("user_id");
                loverOrderMap.put(userID, i);
                ++i;
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            PlayerDAO.log.error((Object) "\u73a9\u5bb6\u7231\u60c5\u503c\u6392\u884c error: ", (Throwable) e);
            try {
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
            return loverOrderMap;
        } finally {
            try {
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
        return loverOrderMap;
    }

    public static final void updateWorldChatWait(final int _userID, final long _newTime) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("update player set world_chat_time = ?  where user_id = ?");
            ps.setLong(1, _newTime);
            ps.setInt(2, _userID);
            ps.executeUpdate();
        } catch (Exception e) {
            LogWriter.error(null, e);
            try {
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
            return;
        } finally {
            try {
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

    public static final void updateClanChatWait(final int _userID, final long _newTime) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("update player set clan_chat_time = ?  where user_id = ?");
            ps.setLong(1, _newTime);
            ps.setInt(2, _userID);
            ps.executeUpdate();
        } catch (Exception e) {
            LogWriter.error(null, e);
            try {
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
            return;
        } finally {
            try {
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

    public static final long loadClanChatWait(final int _userID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        long time = 0L;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("select clan_chat_time from player where user_id = ?");
            ps.setInt(1, _userID);
            set = ps.executeQuery();
            if (set.next()) {
                time = set.getLong("clan_chat_time");
            } else {
                set.close();
                set = null;
                ps.close();
                ps = null;
            }
        } catch (Exception sqle) {
            LogWriter.error(null, sqle);
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (set != null) {
                set.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex2) {
        }
        return time;
    }

    public static final long loadWorldChatWait(final int _userID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        long time = 0L;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("select world_chat_time from player where user_id = ?");
            ps.setInt(1, _userID);
            set = ps.executeQuery();
            if (set.next()) {
                time = set.getLong("world_chat_time");
            } else {
                set.close();
                set = null;
                ps.close();
                ps = null;
            }
        } catch (Exception sqle) {
            LogWriter.error(null, sqle);
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (set != null) {
                set.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex2) {
        }
        return time;
    }

    @Override
    public byte[] createRole(final int _accountID, final short _serverID, final int _userID, final String[] _paras) {
        String _nickname = _paras[0].toString();
        EClan clan = EClan.getClan(Short.parseShort(_paras[1]));
        EVocation vocation = EVocation.getVocationByID(Short.parseShort(_paras[2]));
        ESex sex = ESex.getSex(Short.parseShort(_paras[3]));
        short clientType = Short.parseShort(_paras[4]);
        int roleUserID = initPlayerDB(_accountID, _serverID, _userID, _nickname, clan, vocation, sex, clientType);
        byte[] roleDesc = null;
        if (roleUserID > 0) {
            roleDesc = HeroRoleView.getInstance().getNewRoleDesc(roleUserID, _nickname, clan, vocation, sex, clientType);
        }
        try {
            LogServiceImpl.getInstance().createDelRoleLog("\u521b\u5efa", _accountID, _serverID, _userID, _nickname, clan.getDesc(), vocation.getDesc(), sex.getDesc(), clientType, roleUserID > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roleDesc;
    }

    @Override
    public int deleteRole(final int _userID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int rst = 0;
        String name = "";
        int accountID = 0;
        int serverID = -1;
        String clan = "";
        String vocation = "";
        String sex = "";
        short clientType = -1;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("SELECT account_id,nickname,sex,clan,vocation,lvl,money,exp,hp,mp,novice,where_id,where_x,where_y,home_id,bag_size,auto_sell_trait,total_play_time,last_logout_time,msisdn, surplus_skill_point,left_master_time,lover_value, last_receive_gift, heaven_book_1,heaven_book_2,heaven_book_3,server_id,receive_repeate_task_times FROM player WHERE user_id=? LIMIT 1");
            ps.setInt(1, _userID);
            rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("nickname");
                accountID = rs.getInt("account_id");
                serverID = rs.getInt("server_id");
                clan = EClan.getClan(rs.getInt("clan")).getDesc();
                vocation = EVocation.getVocationByID(rs.getInt("vocation")).getDesc();
                sex = ESex.getSex(rs.getInt("sex")).getDesc();
            }
            ps = conn.prepareStatement("DELETE FROM player WHERE user_id=? limit 1");
            ps.setInt(1, _userID);
            rst = ps.executeUpdate();
            if (1 == rst) {
                ps.close();
                ps = null;
                ps = conn.prepareStatement("DELETE FROM player_carry_equipment WHERE user_id=?");
                ps.setInt(1, _userID);
                ps.executeUpdate();
                ps.close();
                ps = null;
                ps = conn.prepareStatement("DELETE FROM equipment_instance WHERE owner_user_id=?");
                ps.setInt(1, _userID);
                ps.executeUpdate();
                ps.close();
                ps = null;
                ps = conn.prepareStatement("DELETE FROM player_skill WHERE user_id = ? LIMIT 200");
                ps.setInt(1, _userID);
                ps.executeUpdate();
                ps.close();
                ps = null;
                ps = conn.prepareStatement("DELETE FROM player_effect WHERE user_id = ? LIMIT 30");
                ps.setInt(1, _userID);
                ps.executeUpdate();
                ps.close();
                ps = null;
                ps = conn.prepareStatement("DELETE FROM player_shortcut_key WHERE user_id = ? LIMIT 1");
                ps.setInt(1, _userID);
                ps.executeUpdate();
                ps.close();
                ps = null;
                ps = conn.prepareStatement("DELETE FROM player_completed_task WHERE user_id = ? LIMIT 20");
                ps.setInt(1, _userID);
                ps.executeUpdate();
                ps.close();
                ps = null;
                ps = conn.prepareStatement("DELETE FROM player_exsits_task WHERE user_id = ? LIMIT 30");
                ps.setInt(1, _userID);
                ps.executeUpdate();
                ps.close();
                ps = null;
                ps = conn.prepareStatement("DELETE FROM player_single_goods WHERE user_id = ? LIMIT 300");
                ps.setInt(1, _userID);
                ps.executeUpdate();
                ps.close();
                ps = null;
                ps = conn.prepareStatement("DELETE FROM cd WHERE user_id = ? LIMIT 30");
                ps.setInt(1, _userID);
                ps.executeUpdate();
                ps.close();
                ps = null;
                ps = conn.prepareStatement("DELETE FROM sports_point WHERE user_id = ? LIMIT 30");
                ps.setInt(1, _userID);
                ps.executeUpdate();
                ps.close();
                ps = null;
                ps = conn.prepareStatement("DELETE FROM lover WHERE roleA =? OR roleB=? LIMIT 1");
                ps.setString(1, name);
                ps.setString(2, name);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LogWriter.error(this, e);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex2) {
        }
        int flag = (rst == 1) ? 1 : 0;
        try {
            LogServiceImpl.getInstance().createDelRoleLog("\u5220\u9664", accountID, (short) serverID, _userID, name, clan, vocation, sex, clientType, flag > 0);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return flag;
    }

    @Override
    public byte[] listRole(final int[] _userIDList) {
        return HeroRoleView.getInstance().get(_userIDList);
    }

    @Override
    public IPlayer load(final int _userID) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        HeroPlayer player = null;
        try {
            con = DBServiceImpl.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT account_id,nickname,sex,clan,vocation,lvl,money,exp,hp,mp,novice,where_id,where_x,where_y,home_id,bag_size,auto_sell_trait,total_play_time,last_logout_time,msisdn, surplus_skill_point,left_master_time,lover_value, last_receive_gift, heaven_book_1,heaven_book_2,heaven_book_3,server_id,receive_repeate_task_times FROM player WHERE user_id=? LIMIT 1");
            pstm.setInt(1, _userID);
            rs = pstm.executeQuery();
            if (rs.next()) {
                String bagSizeDesc = rs.getString("bag_size");
                String[] bagSizeList = bagSizeDesc.split("&");
                int bagsize = bagSizeList.length;
                byte[] bagSizes = new byte[bagsize];
                if (bagsize == 6) {
                    bagSizes = new byte[]{0, 0, 0, 0, 0, 0, 16};
                }
                if (bagsize == 5) {
                    bagSizes = new byte[]{0, 0, 0, 0, 0, 16, 16};
                }
                if (bagsize == 4) {
                    bagSizes = new byte[]{0, 0, 0, 0, 16, 16, 16};
                }
                PlayerDAO.log.debug((Object) ("player bag size = " + bagsize));
                for (int i = 0; i < bagsize; ++i) {
                    bagSizes[i] = Byte.parseByte(bagSizeList[i]);
                }
                player = new HeroPlayer(_userID);
                player.bagSizes = bagSizes;
                int accountID = rs.getInt("account_id");
                String nickName = rs.getString("nickname");
                short sex = rs.getShort("sex");
                int vocation = rs.getInt("vocation");
                int clan = rs.getInt("clan");
                short level = rs.getShort("lvl");
                if (level > PlayerServiceImpl.getInstance().getConfig().max_level) {
                    level = PlayerServiceImpl.getInstance().getConfig().max_level;
                }
                int hp = rs.getInt("hp");
                int mp = rs.getInt("mp");
                int exp = rs.getInt("exp");
                int money = rs.getInt("money");
                if (money > 1000000000) {
                    money = 1000000000;
                }
                short whereMapID = rs.getShort("where_id");
                short isNovice = rs.getShort("novice");
                short whereX = rs.getShort("where_x");
                short whereY = rs.getShort("where_y");
                short homeID = rs.getShort("home_id");
                short autoSellTrait = rs.getShort("auto_sell_trait");
                long totalPlayerTime = rs.getLong("total_play_time");
                Timestamp lastLogoutTime = rs.getTimestamp("last_logout_time");
                short skillPoints = rs.getShort("surplus_skill_point");
                Timestamp leftMasterTime = rs.getTimestamp("left_master_time");
                int lastReceiveGift = rs.getInt("last_receive_gift");
                int loverValue = rs.getInt("lover_value");
                int heaven_book_1 = rs.getInt("heaven_book_1");
                int heaven_book_2 = rs.getInt("heaven_book_2");
                int heaven_book_3 = rs.getInt("heaven_book_3");
                int receive_repeate_task_times = rs.getInt("receive_repeate_task_times");
                player.getLoginInfo().accountID = accountID;
                player.setName(nickName);
                player.setSex(ESex.getSex(sex));
                player.setVocation(EVocation.getVocationByID(vocation));
                player.setClan(EClan.getClan(clan));
                player.setLevel(level);
                player.setExp(exp);
                int nextAdd = 0;
                int nowAdd = 0;
                for (int j = 1; j <= level; ++j) {
                    if (j == level) {
                        nextAdd += CEService.totalUpgradeExp(j);
                    } else {
                        nowAdd += CEService.totalUpgradeExp(j);
                        nextAdd += CEService.totalUpgradeExp(j);
                    }
                }
                nowAdd += exp;
                player.setUpgradeNeedExp(CEService.totalUpgradeExp(level));
                player.setUpgradeNeedExpShow(nextAdd);
                player.setExpShow(nowAdd);
                player.setMoney(money);
                player.setAutoSellTrait(EGoodsTrait.getTrait(autoSellTrait));
                player.totalPlayTime = totalPlayerTime;
                player.lastLogoutTime = lastLogoutTime.getTime();
                player.surplusSkillPoint = skillPoints;
                player.leftMasterTime = leftMasterTime.getTime();
                player.setLoverValue(loverValue);
                player.heaven_book_ids[0] = heaven_book_1;
                player.heaven_book_ids[1] = heaven_book_2;
                player.heaven_book_ids[2] = heaven_book_3;
                player.lastReceiveGift = lastReceiveGift;
                player.receivedRepeateTaskTimes = receive_repeate_task_times;
                if (heaven_book_1 > 0 && heaven_book_1 == heaven_book_2 && heaven_book_1 == heaven_book_3) {
                    player.heavenBookSame = true;
                }
                hero.map.Map map;
                if (hp == 0) {
                    player.setHp(CEService.hpByStamina(CEService.playerBaseAttribute(player.getLevel(), player.getVocation().getStaminaCalPara()), player.getLevel(), player.getObjectLevel().getHpCalPara()));
                    player.setMp(CEService.mpByInte(CEService.playerBaseAttribute(player.getLevel(), player.getVocation().getInteCalcPara()), player.getLevel(), EObjectLevel.NORMAL.getMpCalPara()));
                    short[] relations = MapRelationDict.getInstance().getRelationByMapID(whereMapID);
                    short mapid = relations[2];
                    if (player.getClan() == EClan.HE_MU_DU && relations[8] > 0) {
                        mapid = relations[8];
                    }
                    map = MapServiceImpl.getInstance().getNormalMapByID(mapid);
                    whereX = map.getBornX();
                    whereY = map.getBornY();
                } else {
                    player.setHp(hp);
                    player.setMp(mp);
                    if (5000 < whereMapID) {
                        short[] relations = MapRelationDict.getInstance().getRelationByMapID(whereMapID);
                        short mapid = relations[3];
                        if (player.getClan() == EClan.HE_MU_DU && relations[9] > 0) {
                            mapid = relations[9];
                        }
                        map = MapServiceImpl.getInstance().getNormalMapByID(mapid);
                        whereX = map.getBornX();
                        whereY = map.getBornY();
                    } else {
                        map = MapServiceImpl.getInstance().getNormalMapByID(whereMapID);
                    }
                }
                player.live(map);
                player.setCellX(whereX);
                player.setCellY(whereY);
                player.setHomeID(homeID);
                loadShortcutKeyList(_userID, player.getShortcutKeyList());
                return player;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogWriter.println("SQLException in PlayerLoader.loadPlayerByUserID() : " + _userID);
            LogWriter.error(null, e);
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (con != null) {
                    con.close();
                    con = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return null;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (con != null) {
                    con.close();
                    con = null;
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
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public IPlayer loadOffLinePlayerToGmTool(final int _userID) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        HeroPlayer player = null;
        try {
            con = DBServiceImpl.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT account_id,nickname,sex,clan,vocation,lvl,money,exp,last_login_time,last_logout_time FROM player WHERE user_id=? LIMIT 1");
            pstm.setInt(1, _userID);
            rs = pstm.executeQuery();
            if (rs.next()) {
                player = new HeroPlayer(_userID);
                int accountID = rs.getInt("account_id");
                String nickName = rs.getString("nickname");
                short sex = rs.getShort("sex");
                int vocation = rs.getInt("vocation");
                int clan = rs.getInt("clan");
                short level = rs.getShort("lvl");
                if (level > PlayerServiceImpl.getInstance().getConfig().max_level) {
                    level = PlayerServiceImpl.getInstance().getConfig().max_level;
                }
                int exp = rs.getInt("exp");
                int money = rs.getInt("money");
                if (money > 1000000000) {
                    money = 1000000000;
                }
                Timestamp lastLoginTime = rs.getTimestamp("last_login_time");
                Timestamp lastLogoutTime = rs.getTimestamp("last_logout_time");
                player.getLoginInfo().accountID = accountID;
                player.setName(nickName);
                player.setSex(ESex.getSex(sex));
                player.setVocation(EVocation.getVocationByID(vocation));
                player.setClan(EClan.getClan(clan));
                player.setLevel(level);
                player.setExp(exp);
                player.setMoney(money);
                player.loginTime = lastLoginTime.getTime();
                player.lastLogoutTime = lastLogoutTime.getTime();
                return player;
            }
        } catch (Exception e) {
            LogWriter.println("SQLException in PlayerLoader.loadOffLinePlayerToGmTool() : " + _userID);
            LogWriter.error(null, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (con != null) {
                    con.close();
                    con = null;
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (SQLException ex2) {
        }
        return null;
    }

    public HeroPlayer getOffLinePlayerByName(final String name) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        HeroPlayer player = null;
        try {
            con = DBServiceImpl.getInstance().getConnection();
            pstm = con.prepareStatement("select user_id,sex,clan,vocation,lvl,account_id,money,exp from player where nickname=? limit 1");
            pstm.setString(1, name);
            rs = pstm.executeQuery();
            while (rs.next()) {
                player = new HeroPlayer();
                player.setName(name);
                player.setUserID(rs.getInt("user_id"));
                player.setSex(ESex.getSex(rs.getShort("sex")));
                player.setVocation(EVocation.getVocationByID(rs.getInt("vocation")));
                player.setClan(EClan.getClan(rs.getInt("clan")));
                player.setLevel(rs.getShort("lvl"));
                player.getLoginInfo().accountID = rs.getInt("account_id");
                player.setMoney(rs.getInt("money"));
                player.setExp(rs.getInt("exp"));
            }
            rs.close();
            pstm.close();
            con.close();
        } catch (SQLException e) {
            PlayerDAO.log.error((Object) "get offline player error :\u3000", (Throwable) e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (con != null) {
                    con.close();
                    con = null;
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (SQLException ex2) {
        }
        return player;
    }

    public static void updateRepeateTask(final IPlayer _player) {
        Connection conn = null;
        PreparedStatement ps = null;
        HeroPlayer player = (HeroPlayer) _player;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE player SET vocation=?,lvl=?,money=?,exp=?,hp=?,mp=?,where_id=?,where_x=?,where_y=?,total_play_time=?,last_logout_time=?,surplus_skill_point=?,lover_value=?,last_login_time=?,power_value=?,receive_repeate_task_times=? where user_id=? LIMIT 1");
            ps.setShort(1, player.getVocation().value());
            ps.setShort(2, player.getLevel());
            ps.setInt(3, player.getMoney());
            ps.setInt(4, player.getExp());
            ps.setInt(5, player.getHp());
            ps.setInt(6, player.getMp());
            ps.setInt(7, player.where().getID());
            ps.setShort(8, player.getCellX());
            ps.setShort(9, player.getCellY());
            player.nowPlayTime = (System.currentTimeMillis() - player.loginTime) / 60000L;
            ps.setLong(10, player.totalPlayTime + player.nowPlayTime);
            ps.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
            ps.setInt(12, player.surplusSkillPoint);
            ps.setInt(13, player.getLoverValue());
            ps.setTimestamp(14, new Timestamp(player.loginTime));
            int power = ShareServiceImpl.getInstance().calPlayerPower(player);
            ps.setInt(15, power);
            ps.setInt(16, player.receivedRepeateTaskTimes);
            ps.setInt(17, player.getUserID());
            ps.executeUpdate();
            player.needUpdateDB = false;
        } catch (SQLException e) {
            LogWriter.error(null, e);
            PlayerDAO.log.error((Object) "DB update player error : ", (Throwable) e);
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                PlayerDAO.log.error((Object) "DB update player error : ", (Throwable) e2);
            }
            return;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                PlayerDAO.log.error((Object) "DB update player error : ", (Throwable) e2);
            }
        }
        try {
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            PlayerDAO.log.error((Object) "DB update player error : ", (Throwable) e2);
        }
    }

    @Override
    public void updateDB(final IPlayer _player) {
        Connection conn = null;
        PreparedStatement ps = null;
        HeroPlayer player = (HeroPlayer) _player;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE player SET vocation=?,lvl=?,money=?,exp=?,hp=?,mp=?,where_id=?,where_x=?,where_y=?,total_play_time=?,last_logout_time=?,surplus_skill_point=?,lover_value=?,last_login_time=?,power_value=?,receive_repeate_task_times=? where user_id=? LIMIT 1");
            ps.setShort(1, player.getVocation().value());
            ps.setShort(2, player.getLevel());
            ps.setInt(3, player.getMoney());
            ps.setInt(4, player.getExp());
            ps.setInt(5, player.getHp());
            ps.setInt(6, player.getMp());
            ps.setInt(7, player.where().getID());
            ps.setShort(8, player.getCellX());
            ps.setShort(9, player.getCellY());
            player.nowPlayTime = (System.currentTimeMillis() - player.loginTime) / 60000L;
            ps.setLong(10, player.totalPlayTime + player.nowPlayTime);
            ps.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
            ps.setInt(12, player.surplusSkillPoint);
            ps.setInt(13, player.getLoverValue());
            ps.setTimestamp(14, new Timestamp(player.loginTime));
            int power = ShareServiceImpl.getInstance().calPlayerPower(player);
            ps.setInt(15, power);
            ps.setInt(16, player.receivedRepeateTaskTimes);
            ps.setInt(17, player.getUserID());
            ps.executeUpdate();
            player.needUpdateDB = false;
        } catch (SQLException e) {
            LogWriter.error(null, e);
            PlayerDAO.log.error((Object) "DB update player error : ", (Throwable) e);
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                PlayerDAO.log.error((Object) "DB update player error : ", (Throwable) e2);
            }
            return;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                PlayerDAO.log.error((Object) "DB update player error : ", (Throwable) e2);
            }
        }
        try {
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            PlayerDAO.log.error((Object) "DB update player error : ", (Throwable) e2);
        }
    }

    public static void updatePlayerHeavenBookID(final HeroPlayer _player) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE player t SET t.heaven_book_1=?,t.heaven_book_2=?,t.heaven_book_3=?,t.surplus_skill_point=?  WHERE t.user_id=?");
            ps.setInt(1, _player.heaven_book_ids[0]);
            ps.setInt(2, _player.heaven_book_ids[1]);
            ps.setInt(3, _player.heaven_book_ids[2]);
            ps.setInt(4, _player.surplusSkillPoint);
            ps.setInt(5, _player.getUserID());
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            PlayerDAO.log.error((Object) "update player heaven bookID error \uff1a", (Throwable) e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException ex2) {
        }
    }

    public void updateLevel(final int _playerUserID, final short _level, final EVocation _vocation, final int _money, final int _exp) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE player SET lvl=?,vocation=?,money=?,exp=? WHERE user_id=? LIMIT 1");
            ps.setShort(1, _level);
            ps.setByte(2, _vocation.value());
            ps.setInt(3, _money);
            ps.setInt(4, _exp);
            ps.setInt(5, _playerUserID);
            ps.executeUpdate();
        } catch (SQLException e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException ex2) {
        }
    }

    public void updateDB(final int _playerUserID, final short _level, final EVocation _vocation, final int _money, final int _exp) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE player SET lvl=?,vocation=?,money=?,exp=? WHERE user_id=? LIMIT 1");
            ps.setShort(1, _level);
            ps.setByte(2, _vocation.value());
            ps.setInt(3, _money);
            ps.setInt(4, _exp);
            ps.setInt(5, _playerUserID);
            ps.executeUpdate();
        } catch (SQLException e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException ex2) {
        }
    }

    public void updatePlayerLeftMasterTime(final int userID, final long leftMasterTime) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE player t SET t.left_master_time=? where t.user_id=?");
            ps.setTimestamp(1, new Timestamp(leftMasterTime));
            ps.setInt(2, userID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            LogWriter.error("update player leftMasterTime error:", e);
            PlayerDAO.log.error((Object) "update player leftMasterTime error:", (Throwable) e);
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                PlayerDAO.log.error((Object) "update player leftMasterTime error: ", (Throwable) e2);
            }
            return;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                PlayerDAO.log.error((Object) "update player leftMasterTime error: ", (Throwable) e2);
            }
        }
        try {
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e2) {
            PlayerDAO.log.error((Object) "update player leftMasterTime error: ", (Throwable) e2);
        }
    }

    public void updateAutoSellTrait(final int _userID, final EGoodsTrait _newTrait) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE player SET auto_sell_trait=? where user_id=? LIMIT 1");
            ps.setShort(1, (short) _newTrait.value());
            ps.setInt(2, _userID);
            ps.executeUpdate();
        } catch (SQLException e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException ex2) {
        }
    }

    public static boolean playerExitsByNickname(final String _nickname) {
        boolean isExits = false;
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM player WHERE nickname like BINARY ? LIMIT 1");
            pstm.setString(1, _nickname);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                isExits = true;
            }
            rs.close();
            rs = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex2) {
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex3) {
        }
        return isExits;
    }

    public static short getRoleLevel(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        short level = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT lvl FROM player WHERE user_id=? LIMIT 1");
            pstm.setInt(1, _userID);
            resultSet = pstm.executeQuery();
            if (resultSet.next()) {
                level = resultSet.getShort("lvl");
            }
        } catch (Exception ex) {
            LogWriter.error(null, ex);
            try {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return level;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return level;
    }

    public static void loadPlayerAccountInfo(final HeroPlayer player) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT username,curr_publisher,msisdn,client_version,agent,client_jar_type,bind_msisdn,password FROM account WHERE account_id=? LIMIT 1");
            pstm.setInt(1, player.getLoginInfo().accountID);
            resultSet = pstm.executeQuery();
            if (resultSet.next()) {
                player.getLoginInfo().username = resultSet.getString("username");
                player.getLoginInfo().password = resultSet.getString("password");
                player.getLoginInfo().loginMsisdn = resultSet.getString("msisdn");
                player.getLoginInfo().publisher = Integer.parseInt(resultSet.getString("curr_publisher"));
                player.getLoginInfo().clientType = Short.parseShort(resultSet.getString("client_jar_type"));
                player.getLoginInfo().clientVersion = resultSet.getString("client_version");
                player.getLoginInfo().boundMsisdn = resultSet.getString("bind_msisdn");
            }
        } catch (Exception ex) {
            LogWriter.error(null, ex);
            try {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getChatBlankByUserID(final int accountID, final int _userID) {
        boolean isBlack = false;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("select * from chat_black where account_id=? and user_id = ?");
            pstm.setInt(1, accountID);
            pstm.setInt(2, _userID);
            resultSet = pstm.executeQuery();
            if (resultSet.next()) {
                isBlack = true;
            }
        } catch (Exception ex) {
            PlayerDAO.log.error((Object) ("\u83b7\u53d6\u7981\u8a00\u73a9\u5bb6 " + _userID + "  error : "), (Throwable) ex);
            LogWriter.error("", ex);
            try {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return isBlack;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isBlack;
    }

    public boolean setPlayerUserIDBlank(final int _userID, final String nickname, final int keepTime, final String startTime, final String endTime, final String memo) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("insert into role_black(user_id,nickname,keep_time,start_time,end_time,memo) values(?,?,?,?,?,?)");
            pstm.setInt(1, _userID);
            pstm.setString(2, nickname);
            pstm.setInt(3, keepTime);
            pstm.setTimestamp(4, Timestamp.valueOf(startTime));
            pstm.setTimestamp(5, Timestamp.valueOf(endTime));
            pstm.setString(6, memo);
            pstm.executeQuery();
        } catch (Exception ex) {
            PlayerDAO.log.error((Object) ("\u8bbe\u7f6e\u89d2\u8272\u9ed1\u540d\u5355 " + _userID + "  error : "), (Throwable) ex);
            return false;
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean setPlayerAccountIDBlank(final int _accountID, final String username, final int keepTime, final String startTime, final String endTime, final String memo) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("insert into account_black(account_id,username,keep_time,start_time,end_time,memo) values(?,?,?,?,?,?)");
            pstm.setInt(1, _accountID);
            pstm.setString(2, username);
            pstm.setInt(3, keepTime);
            pstm.setTimestamp(4, Timestamp.valueOf(startTime));
            pstm.setTimestamp(5, Timestamp.valueOf(endTime));
            pstm.setString(6, memo);
            pstm.executeQuery();
        } catch (Exception ex) {
            PlayerDAO.log.error((Object) ("\u8bbe\u7f6e\u8d26\u53f7\u9ed1\u540d\u5355 " + _accountID + "  error : "), (Throwable) ex);
            return false;
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean setPlayerChatBlank(final int _userID, final String nickname, final int keepTime, final String startTime, final String endTime, final String memo) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("insert into chat_black(user_id,nickname,keep_time,start_time,end_time,memo) values(?,?,?,?,?,?)");
            pstm.setInt(1, _userID);
            pstm.setString(2, nickname);
            pstm.setInt(3, keepTime);
            pstm.setTimestamp(4, Timestamp.valueOf(startTime));
            pstm.setTimestamp(5, Timestamp.valueOf(endTime));
            pstm.setString(6, memo);
            pstm.executeQuery();
        } catch (Exception ex) {
            PlayerDAO.log.error((Object) ("\u8bbe\u7f6e\u7981\u8a00\u89d2\u8272  " + _userID + " error : "), (Throwable) ex);
            return false;
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean deletePlayerUserIDBlack(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM role_black WHERE user_id=?");
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            PlayerDAO.log.error((Object) ("\u5220\u9664\u89d2\u8272\u9ed1\u540d\u5355 " + _userID + "  error : "), (Throwable) ex);
            return false;
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean deletePlayerAccountIDBlack(final int _accountID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM account_black WHERE account_id=?");
            pstm.setInt(1, _accountID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            PlayerDAO.log.error((Object) ("\u5220\u9664\u8d26\u53f7\u9ed1\u540d\u5355 " + _accountID + "  error : "), (Throwable) ex);
            return false;
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean deletePlayerChatBlack(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("delete from chat_black where user_id=?");
            pstm.executeUpdate();
        } catch (Exception ex) {
            PlayerDAO.log.error((Object) ("\u5220\u9664\u89d2\u8272\u7981\u8a00 " + _userID + " error : "), (Throwable) ex);
            return false;
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void updateNovice(final int _userID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE player SET novice=0 where user_id=? LIMIT 1");
            ps.setInt(1, _userID);
            ps.executeUpdate();
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex2) {
        }
    }

    public static int getNovice(final int _userID) {
        Connection conn = null;
        Statement state = null;
        ResultSet result = null;
        int novice = 1;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            state = conn.createStatement();
            result = state.executeQuery("select novice from player where user_id=" + _userID + " limit 1");
            if (result.next()) {
                novice = result.getInt("novice");
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (result != null) {
                result.close();
            }
            if (state != null) {
                state.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex2) {
        }
        return novice;
    }

    public static void updatePlayerInfo(final FastList<HeroPlayer> _playerList) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement("UPDATE player SET vocation=?,lvl=?,money=?,exp=?,hp=?,mp=?,where_id=?,where_x=?,where_y=?,total_play_time=?,last_logout_time=?,surplus_skill_point=?,lover_value=?,last_login_time=?,power_value=?,receive_repeate_task_times=? where user_id=? LIMIT 1");
            boolean needUpdateDB = false;
            for (int i = 0; i < _playerList.size(); ++i) {
                HeroPlayer player = (HeroPlayer) _playerList.get(i);
                if (player.isEnable() && player.needUpdateDB && System.currentTimeMillis() - player.getLastTimeOfUPdateDB() >= PlayerDAO.INTERVAL_OF_UPDATE_DB) {
                    pstm.setShort(1, player.getVocation().value());
                    pstm.setShort(2, player.getLevel());
                    pstm.setInt(3, player.getMoney());
                    pstm.setInt(4, player.getExp());
                    pstm.setInt(5, player.getHp());
                    pstm.setInt(6, player.getMp());
                    pstm.setInt(7, player.where().getID());
                    pstm.setShort(8, player.getCellX());
                    pstm.setShort(9, player.getCellY());
                    player.nowPlayTime = (System.currentTimeMillis() - player.loginTime) / 60000L;
                    pstm.setLong(10, player.totalPlayTime + player.nowPlayTime);
                    pstm.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
                    pstm.setInt(12, player.surplusSkillPoint);
                    pstm.setInt(13, player.getLoverValue());
                    pstm.setTimestamp(14, new Timestamp(player.loginTime));
                    int power = ShareServiceImpl.getInstance().calPlayerPower(player);
                    pstm.setInt(15, power);
                    pstm.setInt(16, player.receivedRepeateTaskTimes);
                    pstm.setInt(17, player.getUserID());
                    pstm.addBatch();
                    needUpdateDB = true;
                    player.needUpdateDB = false;
                    player.setLastTimeOfUPdateDB(System.currentTimeMillis());
                }
            }
            if (needUpdateDB) {
                pstm.executeBatch();
                conn.commit();
            }
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            LogWriter.error(null, ex);
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int initPlayerDB(final int _accountID, final short _serverID, final int _userID, final String _nickname, final EClan _clan, final EVocation _vocation, final ESex _sex, final short _clientType) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO player(user_id,account_id,server_id,nickname,sex,clan,vocation,lvl,money,hp,mp,where_id,where_x,where_y,home_id,surplus_skill_point,clan_chat_time,world_chat_time)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            conn.setAutoCommit(false);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _accountID);
            pstm.setShort(3, _serverID);
            pstm.setString(4, _nickname);
            pstm.setShort(5, _sex.value());
            pstm.setShort(6, _clan.getID());
            pstm.setShort(7, _vocation.value());
            pstm.setShort(8, (short) 1);
            pstm.setInt(9, PlayerServiceImpl.getInstance().getConfig().init_money);
            pstm.setInt(10, PlayerServiceImpl.getInstance().getConfig().getInitHp(_vocation));
            pstm.setInt(11, PlayerServiceImpl.getInstance().getConfig().getInitMp(_vocation));
            pstm.setShort(12, PlayerServiceImpl.getInstance().getInitBornMapID(_clan));
            pstm.setShort(13, PlayerServiceImpl.getInstance().getInitBornX(_clan));
            pstm.setShort(14, PlayerServiceImpl.getInstance().getInitBornY(_clan));
            pstm.setShort(15, PlayerServiceImpl.getInstance().getInitBornMapID(_clan));
            pstm.setInt(16, PlayerServiceImpl.getInstance().getConfig().init_surplus_skill_point);
            pstm.setLong(17, System.currentTimeMillis() - 300000L);
            pstm.setLong(18, System.currentTimeMillis() - 300000L);
            pstm.execute();
            pstm.close();
            pstm = null;
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement("INSERT INTO equipment_instance(instance_id,equipment_id,creator_user_id,owner_user_id,current_durability, bind) VALUES (?,?,?,?,?,?)");
            int[] armorIDs = PlayerServiceImpl.getInstance().getConfig().getInitArmorIDs(_vocation.getType());
            PlayerDAO.log.info((Object) ("init player armorIDS length = " + armorIDs.length));
            ArrayList<EquipmentInstance> equipmentList = new ArrayList<EquipmentInstance>();
            int[] array;
            for (int length = (array = armorIDs).length, j = 0; j < length; ++j) {
                int armorID = array[j];
                if (armorID != 0) {
                    EquipmentInstance armor = EquipmentFactory.getInstance().build(_userID, _userID, armorID);
                    equipmentList.add(armor);
                }
            }
            armorIDs = PlayerServiceImpl.getInstance().getConfig().getInitJewelryIDs(_vocation.getType());
            PlayerDAO.log.info((Object) ("init player armorIDS length = " + armorIDs.length));
            int[] array2;
            for (int length2 = (array2 = armorIDs).length, k = 0; k < length2; ++k) {
                int armorID = array2[k];
                if (armorID != 0) {
                    EquipmentInstance armor = EquipmentFactory.getInstance().build(_userID, _userID, armorID);
                    equipmentList.add(armor);
                }
            }
            int initWeaponID = PlayerServiceImpl.getInstance().getConfig().getInitWeaponID(_vocation);
            PlayerDAO.log.info((Object) ("initWeaponID = " + initWeaponID));
            EquipmentInstance initWeapon = EquipmentFactory.getInstance().build(_userID, _userID, PlayerServiceImpl.getInstance().getConfig().getInitWeaponID(_vocation));
            PlayerDAO.log.info((Object) ("init Weapon = " + initWeapon));
            equipmentList.add(initWeapon);
            PlayerDAO.log.info((Object) ("init player equip size = " + equipmentList.size()));
            for (final EquipmentInstance ei : equipmentList) {
                pstm.setInt(1, ei.getInstanceID());
                pstm.setInt(2, ei.getArchetype().getID());
                pstm.setInt(3, ei.getCreatorUserID());
                pstm.setInt(4, ei.getOwnerUserID());
                pstm.setInt(5, ei.getCurrentDurabilityPoint());
                pstm.setInt(6, 1);
                pstm.addBatch();
            }
            pstm.executeBatch();
            conn.commit();
            pstm.close();
            pstm = null;
            pstm = conn.prepareStatement("INSERT INTO player_carry_equipment(instance_id,user_id,container_type) VALUES (?,?,?)");
            for (final EquipmentInstance ei : equipmentList) {
                pstm.setInt(1, ei.getInstanceID());
                pstm.setInt(2, _userID);
                pstm.setShort(3, (short) 2);
                pstm.addBatch();
            }
            pstm.executeBatch();
            conn.commit();
            pstm.close();
            pstm = null;
            pstm = conn.prepareStatement("INSERT INTO player_single_goods(user_id,goods_type,goods_id,goods_number,package_index) VALUES (?,?,?,?,?)");
            int[][] medicamentDataList = PlayerServiceImpl.getInstance().getConfig().getInitMedicamentData(_vocation);
            if (medicamentDataList != null) {
                for (int i = 0; i < medicamentDataList.length; ++i) {
                    pstm.setInt(1, _userID);
                    pstm.setShort(2, (short) 1);
                    pstm.setInt(3, medicamentDataList[i][0]);
                    pstm.setInt(4, medicamentDataList[i][1]);
                    pstm.setInt(5, i);
                    pstm.addBatch();
                }
                pstm.executeBatch();
                conn.commit();
                pstm.close();
                pstm = null;
            }
            pstm = conn.prepareStatement("INSERT INTO player_skill (user_id,skill_id) VALUES (?,?)");
            ArrayList<Skill> skills = SkillDict.getInstance().getSkillsByVocation(_vocation);
            PlayerDAO.log.debug((Object) "\u65b0\u73a9\u5bb6\u6280\u80fd\u521d\u59cb\u5316:");
            PlayerDAO.log.debug((Object) ("\u65b0\u73a9\u5bb6\u804c\u4e1a:" + _vocation.getDesc()));
            PlayerDAO.log.debug((Object) (String.valueOf(skills.size()) + "\u4e2a\u6280\u80fd\u88ab\u521d\u59cb\u5316"));
            int initSkillID = PlayerServiceImpl.getInstance().getConfig().getInitSkill(_vocation);
            for (final Skill skill : skills) {
                pstm.setInt(1, _userID);
                if (skill.next != null && skill.next.id == initSkillID) {
                    pstm.setInt(2, initSkillID);
                } else {
                    pstm.setInt(2, skill.id);
                }
                PlayerDAO.log.debug((Object) ("\u4e3auserid=" + _userID + "\u7684" + _vocation.getDesc() + "\u804c\u4e1a\u7684\u73a9\u5bb6\u52a0\u8f7d\u6280\u80fd:" + skill.name + ";\u6280\u80fd\u9650\u5236\u804c\u4e1a:" + skill.learnerVocation[0].getDesc()));
                pstm.addBatch();
            }
            pstm.executeBatch();
            conn.commit();
            pstm.close();
            pstm = null;
            conn.setAutoCommit(true);
            initHotKeyByVocation(_vocation);
            pstm = conn.prepareStatement("INSERT INTO player_shortcut_key(user_id,shortcut_key) VALUES (?,?)");
            pstm.setInt(1, _userID);
            pstm.setString(2, PlayerDAO.DEFAULT_SHORTCUT_KEY_DESC);
            pstm.executeUpdate();
            pstm.close();
            pstm = null;
            return _userID;
        } catch (Exception ex) {
            LogWriter.error(null, ex);
            PlayerDAO.log.error((Object) "sql cmd error:", (Throwable) ex);
            ex.printStackTrace();
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    if (!conn.getAutoCommit()) {
                        conn.setAutoCommit(true);
                    }
                    conn.close();
                }
            } catch (SQLException ex2) {
            }
        }
        return -1;
    }

    public static int getAccountIDByNickName(final String name) {
        Connection conn = null;
        Statement state = null;
        ResultSet result = null;
        int accountID = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            state = conn.createStatement();
            result = state.executeQuery("select account_id from player where nickname='" + name + "' limit 1");
            if (result.next()) {
                accountID = result.getInt("account_id");
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (result != null) {
                result.close();
            }
            if (state != null) {
                state.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex2) {
        }
        return accountID;
    }

    public static int getAccountIDByMSISDN(final String number) {
        Connection conn = null;
        Statement state = null;
        ResultSet result = null;
        int accountID = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            state = conn.createStatement();
            result = state.executeQuery("select account_id from player where msisdn='" + number + "' limit 1");
            if (result.next()) {
                accountID = result.getInt("account_id");
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (result != null) {
                result.close();
            }
            if (state != null) {
                state.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex2) {
        }
        return accountID;
    }

    public static int getUserIDByName(final String name) {
        Connection conn = null;
        Statement state = null;
        ResultSet result = null;
        int userID = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            state = conn.createStatement();
            result = state.executeQuery("select user_id from player where nickname='" + name + "' limit 1");
            if (result.next()) {
                userID = result.getInt("user_id");
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (result != null) {
                result.close();
            }
            if (state != null) {
                state.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex2) {
        }
        return userID;
    }

    public static final int[][] loadShortcutKeyList(final int _userID, final int[][] _shortcutKeyList) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("SELECT * FROM player_shortcut_key WHERE user_id = ? LIMIT 1");
            ps.setInt(1, _userID);
            set = ps.executeQuery();
            String[] singleShortcutKeyDescList;
            if (set.next()) {
                String shortcutKeyDesc = set.getString("shortcut_key");
                singleShortcutKeyDescList = shortcutKeyDesc.split("#");
            } else {
                set.close();
                set = null;
                ps.close();
                ps = null;
                ps = conn.prepareStatement("INSERT INTO player_shortcut_key(user_id,shortcut_key) VALUES (?,?)");
                ps.setInt(1, _userID);
                ps.setString(2, "4&1&5#5&1&8#6&1&14#7&1&24#10&1&1#11&1&2#12&1&3#13&1&4#14&1&6#15&1&9#16&1&11#17&1&16#18&1&10#23&1&1#24&1&2#25&1&3#26&1&4#");
                ps.executeUpdate();
                ps.close();
                ps = null;
                singleShortcutKeyDescList = "4&1&5#5&1&8#6&1&14#7&1&24#10&1&1#11&1&2#12&1&3#13&1&4#14&1&6#15&1&9#16&1&11#17&1&16#18&1&10#23&1&1#24&1&2#25&1&3#26&1&4#".split("#");
            }
            if (singleShortcutKeyDescList != null && singleShortcutKeyDescList.length > 0) {
                String[] array;
                for (int length = (array = singleShortcutKeyDescList).length, i = 0; i < length; ++i) {
                    String singleShortcutKeyDesc = array[i];
                    String[] shortcutKeyInfo = singleShortcutKeyDesc.split("&");
                    int shortcutKey = Integer.parseInt(shortcutKeyInfo[0]);
                    _shortcutKeyList[shortcutKey - 1][0] = Integer.parseInt(shortcutKeyInfo[1]);
                    _shortcutKeyList[shortcutKey - 1][1] = Integer.parseInt(shortcutKeyInfo[2]);
                }
            }
        } catch (Exception sqle) {
            LogWriter.error(null, sqle);
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (set != null) {
                set.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex2) {
        }
        return _shortcutKeyList;
    }

    public void updateShortcutKey(final int _userID, final int[][] _shortcutKeyList) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String shortcutKeyDesc = formatShortcutKey(_shortcutKeyList);
            if (shortcutKeyDesc != null) {
                conn = DBServiceImpl.getInstance().getConnection();
                ps = conn.prepareStatement("UPDATE player_shortcut_key SET shortcut_key = ? WHERE user_id = ? LIMIT 1");
                ps.setString(1, shortcutKeyDesc);
                ps.setInt(2, _userID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex2) {
        }
    }

    public static void updateLastGift(final int _userID, final int _giftID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE player set last_receive_gift=?  WHERE user_id=? LIMIT 1");
            ps.setInt(1, _giftID);
            ps.setInt(2, _userID);
            ps.executeUpdate();
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex2) {
        }
    }

    private static String formatShortcutKey(final int[][] _shortcutKeyList) {
        if (_shortcutKeyList != null && _shortcutKeyList.length > 0) {
            StringBuffer defaultChar = new StringBuffer();
            for (int i = 0; i < _shortcutKeyList.length; ++i) {
                if (_shortcutKeyList[i][0] != 0) {
                    defaultChar.append(i + 1).append("&").append(_shortcutKeyList[i][0]).append("&").append(_shortcutKeyList[i][1]).append("#");
                }
            }
            return defaultChar.toString();
        }
        return null;
    }
}
