// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.sports;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import hero.share.service.LogWriter;
import yoyo.service.tools.database.DBServiceImpl;

public class SportsDAO {

    private static final String SELECT_POINT_SQL = "SELECT * FROM sports_point WHERE user_id = ? LIMIT 1";
    private static final String INSERT_POINT_SQL = "INSERT INTO sports_point VALUES(?,?,?,?,?)";
    private static final String UPDATE_POINT_SQL = "UPDATE sports_point SET c_y = ? , y_l = ? , t_y = ? , s_w = ? WHERE user_id = ? LIMIT 1";

    public static short[] loadSportsPoint(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM sports_point WHERE user_id = ? LIMIT 1");
            pstm.setInt(1, _userID);
            set = pstm.executeQuery();
            if (set.next()) {
                short[] sportsPointList = new short[4];
                sportsPointList[ESportsClan.CHI_YOU_MAN_YI.getID() - 1] = set.getShort("c_y");
                sportsPointList[ESportsClan.YAN_LONG_YONG_SHI.getID() - 1] = set.getShort("y_l");
                sportsPointList[ESportsClan.TIAN_YU_ZHI_JUN.getID() - 1] = set.getShort("t_y");
                sportsPointList[ESportsClan.SHUN_WANG_WEI_DUI.getID() - 1] = set.getShort("s_w");
                return sportsPointList;
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
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
        } catch (Exception ex2) {
        }
        return null;
    }

    public static short[] insertSportsPoint(final int _userID, final short[] _sportsPointList) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO sports_point VALUES(?,?,?,?,?)");
            pstm.setInt(1, _userID);
            pstm.setShort(2, _sportsPointList[ESportsClan.CHI_YOU_MAN_YI.getID() - 1]);
            pstm.setShort(3, _sportsPointList[ESportsClan.YAN_LONG_YONG_SHI.getID() - 1]);
            pstm.setShort(4, _sportsPointList[ESportsClan.TIAN_YU_ZHI_JUN.getID() - 1]);
            pstm.setShort(5, _sportsPointList[ESportsClan.SHUN_WANG_WEI_DUI.getID() - 1]);
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
        return null;
    }

    public static short[] updateSportsPoint(final int _userID, final short[] _sportsPointList) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE sports_point SET c_y = ? , y_l = ? , t_y = ? , s_w = ? WHERE user_id = ? LIMIT 1");
            pstm.setShort(1, _sportsPointList[ESportsClan.CHI_YOU_MAN_YI.getID() - 1]);
            pstm.setShort(2, _sportsPointList[ESportsClan.YAN_LONG_YONG_SHI.getID() - 1]);
            pstm.setShort(3, _sportsPointList[ESportsClan.TIAN_YU_ZHI_JUN.getID() - 1]);
            pstm.setShort(4, _sportsPointList[ESportsClan.SHUN_WANG_WEI_DUI.getID() - 1]);
            pstm.setInt(5, _userID);
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
        return null;
    }
}
