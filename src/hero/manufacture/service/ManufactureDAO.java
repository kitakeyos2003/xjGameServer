// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture.service;

import java.sql.ResultSet;
import hero.manufacture.ManufactureType;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import yoyo.service.tools.database.DBServiceImpl;
import hero.manufacture.Manufacture;

public class ManufactureDAO {

    private static final String UPDATE_MANUF_SQL = "UPDATE manuf SET lvl = ?,lvl_point = ? WHERE userid = ? and manuf_type=? LIMIT 1";
    private static final String SEL_MANUF_SQL = "SELECT manuf_type,lvl,lvl_point FROM manuf WHERE userid = ? LIMIT 2";
    private static final String SEL_MANUF_LIST_SQL = "SELECT manufid FROM manuf_list WHERE userid = ?";
    private static final String DEL_MANUF_SQL = "DELETE FROM manuf WHERE userid = ? LIMIT 2";
    private static final String DEL_MANUF_LIST_SQL = "DELETE FROM manuf_list WHERE userid = ? AND manufid = ?";
    private static final String DEL_MANUF_LISTS_SQL = "DELETE FROM manuf_list WHERE userid = ?";
    private static final String ADD_MANUF_LIST_SQL = "INSERT INTO manuf_list SET userid = ?,manufid = ?";
    private static final String ADD_MANUF_SQL = "INSERT INTO manuf SET userid = ?,manuf_type=?";

    public static void updateManuf(final int _userID, final Manufacture _manuf) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE manuf SET lvl = ?,lvl_point = ? WHERE userid = ? and manuf_type=? LIMIT 1");
            pstm.setByte(1, _manuf.getLvl());
            pstm.setInt(2, _manuf.getPoint());
            pstm.setInt(3, _userID);
            pstm.executeUpdate();
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
    }

    public static List<Manufacture> loadManufByUserID(final int _userID) {
        List<Manufacture> manufList = new ArrayList<Manufacture>();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT manuf_type,lvl,lvl_point FROM manuf WHERE userid = ? LIMIT 2");
            pstm.setInt(1, _userID);
            rs = pstm.executeQuery();
            if (rs.next()) {
                byte manuf_type = rs.getByte(1);
                byte lvl = rs.getByte(2);
                int point = rs.getInt(3);
                rs.close();
                rs = null;
                pstm.close();
                pstm = null;
                Manufacture manuf = new Manufacture(ManufactureType.get(manuf_type));
                manuf.setLvl(lvl);
                manuf.setPoint(point);
                pstm = conn.prepareStatement("SELECT manufid FROM manuf_list WHERE userid = ?");
                pstm.setInt(1, _userID);
                rs = pstm.executeQuery();
                while (rs.next()) {
                    int manufid = rs.getInt(1);
                    manuf.addManufID(manufid);
                }
                manufList.add(manuf);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
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
            if (rs != null) {
                rs.close();
            }
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex3) {
        }
        return manufList;
    }

    public static void forgetManufByUserID(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM manuf WHERE userid = ? LIMIT 2");
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
            delManufListsByUserID(_userID);
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
    }

    public static void delManufListByUserID(final int _userID, final int _manufID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM manuf_list WHERE userid = ? AND manufid = ?");
            pstm.setInt(1, _userID);
            pstm.setInt(2, _manufID);
            pstm.executeUpdate();
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
    }

    public static void delManufListsByUserID(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM manuf_list WHERE userid = ?");
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
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
    }

    public static void addManufID(final int _userID, final int _manufID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO manuf_list SET userid = ?,manufid = ?");
            pstm.setInt(1, _userID);
            pstm.setInt(2, _manufID);
            pstm.executeUpdate();
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
    }

    public static void studyManuf(final int _userID, final ManufactureType _type) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO manuf SET userid = ?,manuf_type=?");
            pstm.setInt(1, _userID);
            pstm.setByte(2, _type.getID());
            pstm.executeUpdate();
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
    }
}
