// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.service;

import java.util.List;
import hero.share.EVocation;
import hero.player.define.EClan;
import hero.player.define.ESex;
import java.util.ArrayList;
import hero.share.service.LogWriter;
import yoyo.service.tools.database.DBServiceImpl;
import hero.share.letter.Letter;
import java.util.HashMap;
import hero.chat.service.GmNotice;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;

public class GmDAO {

    private static Logger log;
    private static final String SQL_QUERY_EQUIPMENT = "SELECT equipment_id,container_type FROM equipment_instance JOIN player_carry_equipment ON user_id=? AND player_carry_equipment.instance_id=equipment_instance.instance_id LIMIT 100";
    private static final String SQL_QUERY_SINGLE_GOODS = "SELECT goods_id,goods_type,goods_number from player_single_goods where user_id=? LIMIT 280";
    private static final String SQL_QUERY_BLACKLIST_ACCOUNT = "select * from account_black;";
    private static final String SQL_QUERY_BLACKLIST_ROLE = "select * from role_black;";
    private static final String SQL_QUERY_BLACKLIST_CHAT = "select * from chat_black;";
    private static final String SQL_QUERY_GM_NAME = "select name from gm where name=? and pwd=? limit 1";
    private static final String SQL_MODIFY_GM_PWD = "update gm set pwd=? where name=? limit 1";
    private static final String SQL_QUERY_GM_USERNAME = "select username from account where account_id=? limit 1";
    private static final String SQL_QUERY_GM_ACCOUNT_ID = "select account_id from account_black where account_id=? limit 1";
    private static final String SQL_ADD_ACCOUNT_BLACK = "insert into account_black values(?,?,?,?,?)";
    private static final String SQL_QUERY_ACCOUNTID_FROM_ACCOUNT = "select account_id from account where username=? limit 1";
    private static final String SQL_QUERY_ACCOUNTID_FROM_ROLE = "select account_id from role where nickname=? limit 1";
    private static final String SQL_QUERY_USERID_FROM_ROLEBACK = "select user_id from role_black where nickname=? limit 1";
    private static final String SQL_QUERY_USERNAME_FROM_ACCOUNT = "select username from account where account_id=? limit 1";
    private static final String SQL_QUERY_USERID_FROM_ROLEBLACK = "select user_id from role_black where user_id=? limit 1";
    private static final String SQL_ADD_ROLE_BLACK = "insert into role_black values(?,?,?,?,?)";
    private static final String SQL_QUERY_USERID_FROM_CHATBLACK = "select user_id from chat_black where user_id=? limit 1";
    private static final String SQL_ADD_CHAT_BLACK = "insert into chat_black values(?,?,?,?,?)";
    private static final String SQL_REMOVE_CHAT_BLACK = "delete from chat_black where nickname=? limit 1";
    private static final String SQL_REMOVE_ACCOUNT_BLACK = "delete from account_black where username=? limit 1";
    private static final String SQL_REMOVE_ROLE_BLACK = "delete from role_black where user_id=? limit 1";
    private static final String SQL_UPDATE_ACCOUNT_PWD = "update account set password=? where username=? limit 1";
    private static final String SQL_UPDATE_ACCOUNT_NAME = "update account set username=? where username=? limit 1";
    private static final String SQL_QUERY_ACCOUNTINFO_FROM_ACCOUNT = "select * from account where username=? limit 1";
    private static final String SQL_QUERY_ROLEINFO = "select * from player where account_id=? limit 3";
    private static final String SQL_MODIFY_ROLE_MAP = "update player set where_id=? where nickname=? limit 1";
    private static final String INSERT_GM_LETTER = "insert into gm_letter(sender_role_id,content,type,serverID) values(?,?,?,?)";
    private static final String SELECT_GM_REPLY_LETTER_INFO = "select t.name,gl.reply_content,gl.reply_time,gl.sender_role_id from gm t,gm_letter gl where t.id=gl.reply_gm_id and gl.id=? limit 1";
    private static final String SELECT_GM_NOTICE_SQL = "select * from gm_notice where (serverID=? or serverID=0) and end_time>? order by start_time";
    private static final String SELECT_RECHARGE_PRESENT_POINT = "select present_point from recharge_present_point where price=? and (server_id=? or server_id=0) and start_time<? and end_time>? order by update_time desc limit 1";
    private static SimpleDateFormat dateFormat;

    static {
        GmDAO.log = Logger.getLogger((Class) GmDAO.class);
        GmDAO.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static int getPresentPoint(final int price) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int presentPoint = 0;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("select present_point from recharge_present_point where price=? and (server_id=? or server_id=0) and start_time<? and end_time>? order by update_time desc limit 1");
            ps.setInt(1, price);
            ps.setInt(2, GmServiceImpl.serverID);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setString(3, GmDAO.dateFormat.format(now));
            ps.setString(4, GmDAO.dateFormat.format(now));
            rs = ps.executeQuery();
            if (rs.next()) {
                presentPoint = rs.getInt("present_point");
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            GmDAO.log.error((Object) "\u67e5\u8be2\u516c\u544a\u5217\u8868 :", (Throwable) ex);
            ex.printStackTrace();
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return presentPoint;
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
            } catch (SQLException e) {
                e.printStackTrace();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presentPoint;
    }

    public static Map<Integer, GmNotice> getGmNoticeList(final int serverID) {
        Map<Integer, GmNotice> gmNoticeMap = new HashMap<Integer, GmNotice>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        GmNotice notice = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("select * from gm_notice where (serverID=? or serverID=0) and end_time>? order by start_time");
            pstmt.setInt(1, serverID);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            pstmt.setString(2, GmDAO.dateFormat.format(now));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String content = rs.getString("content");
                Timestamp startTime = rs.getTimestamp("start_time");
                Timestamp endTime = rs.getTimestamp("end_time");
                int intervalTime = rs.getInt("interval_time");
                int times = rs.getInt("times");
                notice = new GmNotice();
                notice.setId(id);
                notice.setContent(content);
                notice.setStartTime(startTime);
                notice.setEndTime(endTime);
                notice.setIntervalTime(intervalTime);
                notice.setTimes(times);
                gmNoticeMap.put(id, notice);
            }
        } catch (Exception ex) {
            GmDAO.log.error((Object) "\u67e5\u8be2\u516c\u544a\u5217\u8868 :", (Throwable) ex);
            ex.printStackTrace();
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return gmNoticeMap;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gmNoticeMap;
    }

    public static Letter getLetterInfo(final int gmLetterID, final Letter letter) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("select t.name,gl.reply_content,gl.reply_time,gl.sender_role_id from gm t,gm_letter gl where t.id=gl.reply_gm_id and gl.id=? limit 1");
            pstmt.setInt(1, gmLetterID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String gm = rs.getString(1);
                String content = rs.getString(2);
                Timestamp reply_time = rs.getTimestamp(3);
                int receiverID = rs.getInt(4);
                letter.senderName = "\u7cfb\u7edfGM:" + gm;
                letter.content = content;
                letter.sendTime = reply_time.getTime();
                letter.receiverUserID = receiverID;
            }
        } catch (Exception ex) {
            GmDAO.log.error((Object) "\u6839\u636e\u56de\u590d\u90ae\u4ef6\u7684GMid \u67e5\u8be2GM\u540d\u548c\u90ae\u4ef6\u5185\u5bb9\u3001\u56de\u590d\u65f6\u95f4 error :", (Throwable) ex);
            ex.printStackTrace();
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return letter;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return letter;
    }

    public static boolean sendGMLetter(final int userID, final String content, final byte type) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("insert into gm_letter(sender_role_id,content,type,serverID) values(?,?,?,?)");
            pstmt.setInt(1, userID);
            pstmt.setString(2, content);
            pstmt.setInt(3, type);
            pstmt.setInt(4, GmServiceImpl.serverID);
            pstmt.execute();
        } catch (Exception ex) {
            GmDAO.log.error((Object) "\u6dfb\u52a0 GM \u90ae\u4ef6 error :", (Throwable) ex);
            ex.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean changeGMPassword(final String name, final String oldPwd, final String newPwd) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet set = null;
        boolean flag = false;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("select name from gm where name=? and pwd=? limit 1");
            pstmt.setString(1, name);
            pstmt.setString(2, oldPwd);
            set = pstmt.executeQuery();
            String str = null;
            if (set.next()) {
                str = set.getString("name");
            }
            if (str != null) {
                set.close();
                set = null;
                pstmt.close();
                pstmt = null;
                pstmt = conn.prepareStatement("update gm set pwd=? where name=? limit 1");
                pstmt.setString(1, str);
                pstmt.setString(2, newPwd);
                if (pstmt.executeUpdate() > 0) {
                    flag = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return flag;
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (set != null) {
                set.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static String getMSISDNByAccountID(final int accountID) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet result = null;
        String msisdn = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("select username from account where account_id=? limit 1");
            pstmt.setInt(1, accountID);
            result = pstmt.executeQuery();
            if (result.next()) {
                msisdn = result.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (result != null) {
                    result.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return msisdn;
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (result != null) {
                result.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return msisdn;
    }

    public static int getRoleUIDByNickname(final String name) {
        Connection conn = null;
        ResultSet set = null;
        PreparedStatement pstmt = null;
        int uid = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("select user_id from role_black where nickname=? limit 1");
            pstmt.setString(1, name);
            set = pstmt.executeQuery();
            if (set.next()) {
                uid = set.getInt("user_id");
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return uid;
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (set != null) {
                set.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return uid;
    }

    public static int getAccountIDByUserName(final String _username) {
        Connection conn = null;
        ResultSet set = null;
        PreparedStatement pstmt = null;
        int account_id = 0;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("select account_id from account where username=? limit 1");
            pstmt.setString(1, _username);
            set = pstmt.executeQuery();
            if (set.next()) {
                account_id = set.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return account_id;
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (set != null) {
                set.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return account_id;
    }

    public static int getAccountIDByRolename(final String _roleName) {
        Connection conn = null;
        ResultSet set = null;
        PreparedStatement pstmt = null;
        int account_id = 0;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("select account_id from role where nickname=? limit 1");
            pstmt.setString(1, _roleName);
            set = pstmt.executeQuery();
            if (set.next()) {
                account_id = set.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return account_id;
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (set != null) {
                set.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return account_id;
    }

    public static String getAccountUserNameByRolename(final String _roleName) {
        Connection conn = null;
        ResultSet set = null;
        PreparedStatement pstmt = null;
        int account_id = 0;
        String username = "";
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("select account_id from role where nickname=? limit 1");
            pstmt.setString(1, _roleName);
            set = pstmt.executeQuery();
            if (set.next()) {
                account_id = set.getInt(1);
            }
            set.close();
            set = null;
            pstmt.close();
            pstmt = null;
            if (account_id > 0) {
                pstmt = conn.prepareStatement("select username from account where account_id=? limit 1");
                pstmt.setInt(1, account_id);
                set = pstmt.executeQuery();
                if (set.next()) {
                    username = set.getString(1);
                }
                set.close();
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return username;
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (set != null) {
                set.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return username;
    }

    public static boolean deleteBlackChat(final String _nickname) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean flag = false;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("delete from chat_black where nickname=? limit 1");
            pstmt.setString(1, _nickname);
            flag = (pstmt.executeUpdate() > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return flag;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return flag;
    }

    public static boolean deleteBlackAccount(final String _username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean flag = false;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("delete from account_black where username=? limit 1");
            pstmt.setString(1, _username);
            flag = (pstmt.executeUpdate() > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return flag;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return flag;
    }

    public static boolean deleteBlackRole(final int _uid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean flag = false;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("delete from role_black where user_id=? limit 1");
            pstmt.setInt(1, _uid);
            flag = (pstmt.executeUpdate() > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return flag;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return flag;
    }

    public static boolean changeAccountPassword(final String _username, final String _password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        boolean flag = false;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("select account_id from account where username=? limit 1");
            pstmt.setString(1, _username);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                flag = true;
            }
            resultSet.close();
            resultSet = null;
            pstmt.close();
            pstmt = null;
            if (flag) {
                pstmt = conn.prepareStatement("update account set password=? where username=? limit 1");
                pstmt.setString(1, _password);
                pstmt.setString(2, _username);
                flag = (pstmt.executeUpdate() > 0);
            }
        } catch (SQLException e) {
            flag = false;
            e.printStackTrace();
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return flag;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return flag;
    }

    public static boolean changeAccountMobile(final String _account, final String _mobile) {
        boolean flag = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("select account_id from account where username=? limit 1");
            pstmt.setString(1, _account);
            resultSet = pstmt.executeQuery();
            if (!resultSet.next()) {
                return false;
            }
            resultSet.close();
            resultSet = null;
            pstmt.close();
            pstmt = null;
            pstmt = conn.prepareStatement("update account set username=? where username=? limit 1");
            pstmt.setString(1, _mobile);
            pstmt.setString(2, _account);
            if (pstmt.executeUpdate() > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return flag;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return flag;
    }

    public static boolean changePlayerMap(final String nickname, final hero.map.Map _targetMap) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("update player set where_id=? where nickname=? limit 1");
            pstmt.setInt(1, _targetMap.getID());
            pstmt.setInt(2, _targetMap.getBornX());
            pstmt.setInt(3, _targetMap.getBornY());
            pstmt.setString(4, nickname);
            if (pstmt.executeUpdate() > 0) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return result;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return result;
    }

    public static String getAccountInfo(final String _username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet set = null;
        String accountInfo = "";
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("select * from account where username=? limit 1");
            pstmt.setString(1, _username);
            set = pstmt.executeQuery();
            if (set.next()) {
                accountInfo = "\u8d26\u53f7ID:" + set.getInt("account_id") + "\n" + "\u8d26\u53f7:" + set.getString("username") + "\n" + "\u5bc6\u7801:" + set.getString("password") + "\n" + "\u767b\u9646\u624b\u673a:" + set.getString("username") + "\n" + "\u5ba2\u6237\u7aef\u7248\u672c:" + set.getString("client_version") + "\n" + "\u521b\u5efa\u65f6\u95f4:" + set.getTimestamp("create_time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return accountInfo;
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (set != null) {
                set.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return accountInfo;
    }

    public static String[] getRoleInfos(final int _account_id) {
        Connection conn = null;
        ResultSet set = null;
        PreparedStatement pstmt = null;
        String[] roleInfos = new String[0];
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstmt = conn.prepareStatement("select * from player where account_id=? limit 3");
            pstmt.setInt(1, _account_id);
            set = pstmt.executeQuery();
            List<String> list = new ArrayList<String>();
            while (set.next()) {
                list.add("\u89d2\u8272ID:" + set.getInt("user_id") + "\n" + "\u89d2\u8272\u540d:" + set.getString("nickname") + "\n" + "\u7ea7\u522b:" + set.getByte("lvl") + "\n" + "\u6027\u522b:" + ESex.getSex(set.getByte("sex")).getDesc() + "\n" + "\u9635\u8425:" + EClan.getClan(set.getByte("clan")).getDesc() + "\n" + "\u804c\u4e1a:" + EVocation.getVocationByID(set.getByte("vocation")).getDesc());
            }
            int len = list.size();
            if (len > 0) {
                roleInfos = new String[len];
                for (int i = 0; i < len; ++i) {
                    roleInfos[i] = list.get(i);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return roleInfos;
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (set != null) {
                set.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return roleInfos;
    }
}
