// 
// Decompiled by Procyon v0.5.36
// 
package hero.social.service;

import hero.social.SocialRelationList;
import java.sql.ResultSet;
import hero.social.ESocialRelationType;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import hero.share.service.LogWriter;
import yoyo.service.tools.database.DBServiceImpl;
import org.apache.log4j.Logger;

public class SocialDAO {

    private static Logger log;
    public static String SQL_OF_INSERT;
    public static String SQL_OF_REMOVE_ONE;
    public static String SQL_OF_REMOVE_ALL;
    public static String SQL_OF_BE_REMOVED;
    public static String SQL_OF_LOAD;

    static {
        SocialDAO.log = Logger.getLogger((Class) SocialDAO.class);
        SocialDAO.SQL_OF_INSERT = "INSERT INTO social VALUES(?,?,?,?)";
        SocialDAO.SQL_OF_REMOVE_ONE = "DELETE FROM social WHERE user_id=? AND member_user_id=? LIMIT 1";
        SocialDAO.SQL_OF_REMOVE_ALL = "DELETE FROM social WHERE user_id=? LIMIT 200";
        SocialDAO.SQL_OF_BE_REMOVED = "DELETE FROM social WHERE member_user_id=?";
        SocialDAO.SQL_OF_LOAD = "SELECT * FROM social WHERE user_id=? LIMIT 200";
    }

    public static void add(final int _userID, final int _memberUserID, final String _memberName, final byte _type, final byte _vocation, final short _lvl) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SocialDAO.SQL_OF_INSERT);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _memberUserID);
            pstm.setString(3, _memberName);
            pstm.setInt(4, _type);
            pstm.executeUpdate();
        } catch (Exception ex) {
            SocialDAO.log.error((Object) ("\u6dfb\u52a0\u5931\u8d25:" + SocialDAO.SQL_OF_INSERT));
            LogWriter.error(null, ex);
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeOne(final int _userID, final int _memberUserID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SocialDAO.SQL_OF_REMOVE_ONE);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _memberUserID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            SocialDAO.log.error((Object) ("\u5220\u9664\u793e\u4ea4\u5173\u7cfb\u5931\u8d25:" + SocialDAO.SQL_OF_REMOVE_ONE));
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeAll(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SocialDAO.SQL_OF_REMOVE_ALL);
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
            pstm.close();
            pstm = null;
            pstm = conn.prepareStatement(SocialDAO.SQL_OF_BE_REMOVED);
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            LogWriter.error(null, ex);
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean beFriend(final String _beUser, final int _hostUserID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        boolean result = false;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SocialDAO.SQL_OF_LOAD);
            pstm.setInt(1, _hostUserID);
            set = pstm.executeQuery();
            while (set.next()) {
                String memberName = set.getString("member_name");
                byte type = set.getByte("social_type");
                if (_beUser.equals(memberName) && ESocialRelationType.FRIEND.value() == type) {
                    result = true;
                }
            }
        } catch (Exception ex) {
            LogWriter.error(null, ex);
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void load(final int _userID, final SocialRelationList _list) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SocialDAO.SQL_OF_LOAD);
            pstm.setInt(1, _userID);
            set = pstm.executeQuery();
            while (set.next()) {
                int memberUserID = set.getInt("member_user_id");
                String memberName = set.getString("member_name");
                byte type = set.getByte("social_type");
                _list.add(type, memberUserID, memberName);
            }
        } catch (Exception ex) {
            LogWriter.error(null, ex);
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
