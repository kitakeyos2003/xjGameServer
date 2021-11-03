// 
// Decompiled by Procyon v0.5.36
// 
package hero.lover.service;

import hero.player.HeroPlayer;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.ResultSet;
import hero.share.service.ShareServiceImpl;
import java.util.Date;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import hero.share.service.LogWriter;
import yoyo.service.tools.database.DBServiceImpl;
import org.apache.log4j.Logger;

public class LoverDAO {

    private static Logger log;

    static {
        LoverDAO.log = Logger.getLogger((Class) LoverDAO.class);
    }

    public static void deletePlayer(final String _name) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("delete from lover where roleA='" + _name + "' OR roleB='" + _name + "'");
        } catch (Exception ex) {
            LogWriter.error("\u5220\u9664\u4e24\u4e2a\u4eba\u7684\u5173\u7cfb", ex);
            ex.printStackTrace();
            try {
                if (stmt != null) {
                    stmt.close();
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
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static LoverServiceImpl.MarryStatus registerMarriage(final String _name1, final String _name2) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        LoverServiceImpl.MarryStatus status = LoverServiceImpl.MarryStatus.NOT_LOVER;
        short sta = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT success FROM lover WHERE ((roleA='" + _name1 + "' AND roleB='" + _name2 + "') OR (roleA='" + _name2 + "' AND roleB='" + _name1 + "')) LIMIT 1");
            if (result.next()) {
                sta = result.getShort(1);
            }
            if (sta == LoverServiceImpl.MarryStatus.LOVED_SUCCESS.getStatus()) {
                stmt.close();
                stmt = null;
                stmt = conn.createStatement();
                stmt.executeUpdate("UPDATE lover SET success=" + LoverServiceImpl.MarryStatus.SUCCESS.getStatus() + ",register_date='" + ShareServiceImpl.DateTimeToString(new Date()) + "' WHERE ((roleA='" + _name1 + "' AND roleB='" + _name2 + "') " + " OR (roleA='" + _name2 + "' AND roleB='" + _name1 + "')) LIMIT 1");
                status = LoverServiceImpl.MarryStatus.SUCCESS;
            } else if (sta == LoverServiceImpl.MarryStatus.SUCCESS.getStatus()) {
                status = LoverServiceImpl.MarryStatus.MARRIED;
            } else {
                status = LoverServiceImpl.MarryStatus.NOT_LOVER;
            }
        } catch (Exception ex) {
            LogWriter.error("\u6ce8\u518c\u7ed3\u5a5a", ex);
            ex.printStackTrace();
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return status;
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (result != null) {
                result.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public static LoverServiceImpl.MarryStatus divorceMarriage(final String name) {
        String otherName = whoLoveMe(name);
        LoverServiceImpl.MarryStatus status;
        if (otherName == null) {
            otherName = whoMarriedMe(name);
            if (otherName == null) {
                status = LoverServiceImpl.MarryStatus.NOT_LOVER;
            } else {
                updateMarryStatus(name, otherName, LoverServiceImpl.MarryStatus.DIVORCE_SUCCESS);
                status = LoverServiceImpl.MarryStatus.DIVORCE_SUCCESS;
            }
        } else {
            status = LoverServiceImpl.MarryStatus.LOVED_NO_MARRY;
        }
        return status;
    }

    public static void marryFaild(final String _name1, final String _name2) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE lover SET success=" + LoverServiceImpl.MarryStatus.LOVED_SUCCESS.getStatus() + ",register_date='" + ShareServiceImpl.DateTimeToString(new Date()) + "' WHERE ((roleA='" + _name1 + "' AND roleB='" + _name2 + "') " + " OR (roleA='" + _name2 + "' AND roleB='" + _name1 + "')) LIMIT 1");
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (stmt != null) {
                    stmt.close();
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
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static LoverServiceImpl.MarryStatus propose(final String _name1, final String _name2) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT success FROM lover WHERE (roleA='" + _name1 + "' AND roleB='" + _name2 + "') " + "OR (roleA='" + _name2 + "' AND roleB='" + _name1 + "') LIMIT 1");
            if (!result.next()) {
                result.close();
                stmt.close();
                stmt = conn.createStatement();
                stmt.executeUpdate("INSERT INTO lover(roleA,roleB,success,register_date) VALUES ('" + _name1 + "','" + _name2 + "', " + LoverServiceImpl.MarryStatus.LOVED_SUCCESS.getStatus() + ", '" + ShareServiceImpl.DateTimeToString(new Date()) + "')");
                stmt.close();
                return LoverServiceImpl.MarryStatus.LOVED_SUCCESS;
            }
            int success = result.getInt(1);
            LoverDAO.log.debug((Object) ("resutl success = " + success));
            if (success == LoverServiceImpl.MarryStatus.LOVED_SUCCESS.getStatus()) {
                return LoverServiceImpl.MarryStatus.LOVED_NO_MARRY;
            }
            if (success == LoverServiceImpl.MarryStatus.SUCCESS.getStatus()) {
                return LoverServiceImpl.MarryStatus.MARRIED;
            }
            if (success == LoverServiceImpl.MarryStatus.BREAK_UP.getStatus() || success == LoverServiceImpl.MarryStatus.DIVORCE_SUCCESS.getStatus()) {
                result.close();
                stmt.close();
                stmt = conn.createStatement();
                stmt.executeUpdate("UPDATE lover SET success=" + LoverServiceImpl.MarryStatus.LOVED_SUCCESS.getStatus() + ",register_date='" + ShareServiceImpl.DateTimeToString(new Date()) + "' WHERE (roleA='" + _name1 + "' AND roleB='" + _name2 + "') " + " OR (roleA='" + _name2 + "' AND roleB='" + _name1 + "') LIMIT 1");
                stmt.close();
                return LoverServiceImpl.MarryStatus.LOVED_SUCCESS;
            }
        } catch (Exception ex) {
            LoverDAO.log.error((Object) "\u6210\u4e3a\u604b\u4eba error: ", (Throwable) ex);
            ex.printStackTrace();
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return LoverServiceImpl.MarryStatus.NOT_LOVER;
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (result != null) {
                result.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return LoverServiceImpl.MarryStatus.NOT_LOVER;
    }

    public static void updateMarryStatus(final String _name1, final String _name2, final LoverServiceImpl.MarryStatus status) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE lover SET success=" + status.getStatus() + ",register_date='" + ShareServiceImpl.DateTimeToString(new Date()) + "' WHERE ((roleA='" + _name1 + "' AND roleB='" + _name2 + "') " + " OR (roleA='" + _name2 + "' AND roleB='" + _name1 + "')) LIMIT 1");
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return;
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    public static void deleteTimeOut() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            Calendar calendar = Calendar.getInstance();
            String date = calendar.get(1) + "-" + (calendar.get(2) + 1) + "-" + calendar.get(5);
            stmt.executeUpdate("DELETE FROM wedding WHERE wed_date<'" + date + "'");
            calendar.add(5, -7);
            String before = ShareServiceImpl.DateTimeToString(calendar.getTime());
            stmt.executeUpdate("DELETE FROM lover WHERE success=0 AND register_date < '" + before + "'");
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (stmt != null) {
                    stmt.close();
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
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String[] hasMarried(final String _srcName, final ArrayList<HeroPlayer> _player) {
        if (_player.size() == 0) {
            return null;
        }
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        String str = "SELECT * FROM lover WHERE ";
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            String tmpName = null;
            String[] family = new String[2];
            for (int i = 0; i < _player.size(); ++i) {
                tmpName = _player.get(i).getName();
                str = String.valueOf(str) + "((roleA like '" + _srcName + "' AND roleB like '" + tmpName + "') OR ";
                str = String.valueOf(str) + "(roleA like '" + tmpName + "' AND roleB like '" + _srcName + "'))";
                if (i != _player.size() - 1) {
                    str = String.valueOf(str) + " OR ";
                } else {
                    str = String.valueOf(str) + " LIMIT 1";
                }
            }
            result = stmt.executeQuery(str);
            if (result.next()) {
                family[0] = result.getString("roleA");
                family[1] = result.getString("roleB");
                return family;
            }
        } catch (Exception ex) {
            LogWriter.error(str, ex);
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (result != null) {
                result.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String whoMarriedMe(final String _name) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT * FROM lover WHERE (roleA='" + _name + "' OR roleB='" + _name + "') AND success=" + LoverServiceImpl.MarryStatus.SUCCESS.getStatus() + " limit 1");
            if (result.next()) {
                String roleA = result.getString("roleA");
                String roleB = result.getString("roleB");
                return _name.equalsIgnoreCase(roleA) ? roleB : roleA;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (result != null) {
                result.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String whoLoveMe(final String _name) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT * FROM lover WHERE (roleA='" + _name + "' OR roleB='" + _name + "') AND success=" + LoverServiceImpl.MarryStatus.LOVED_SUCCESS.getStatus() + " limit 1");
            if (result.next()) {
                String roleA = result.getString("roleA");
                String roleB = result.getString("roleB");
                return _name.equalsIgnoreCase(roleA) ? roleB : roleA;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (result != null) {
                result.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean registerWedding(final String _date, final String _name) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT * FROM wedding WHERE wed_date='" + _date + "' limit 1");
            if (result.next()) {
                return false;
            }
            stmt.executeUpdate("INSERT INTO wedding VALUES('" + _date + "','" + _name + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            if (result != null) {
                result.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String whoWedding(final String _date) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT * FROM wedding WHERE wed_date='" + _date + "' limit 1");
            if (result.next()) {
                return result.getString("user_name");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (result != null) {
                result.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
