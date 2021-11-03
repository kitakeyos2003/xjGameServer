// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.service;

import java.util.Iterator;
import java.util.ArrayList;
import java.sql.ResultSet;
import hero.gather.MonsterSoul;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import yoyo.service.tools.database.DBServiceImpl;
import hero.gather.Gather;

public class GatherDAO {

    private static final String UPDATE_GATHER_SQL = "UPDATE gather SET lvl = ?,lvl_point = ? WHERE userid = ? LIMIT 1";
    private static final String SEL_GATHER_SQL = "SELECT lvl,lvl_point FROM gather WHERE userid = ? LIMIT 1";
    private static final String SEL_GATHER_LIST_SQL = "SELECT soulid,num FROM gather_list WHERE userid = ?";
    private static final String SEL_GATHER_REFINEDS_SQL = "SELECT refinedid FROM gather_refineds WHERE userid = ?";
    private static final String ADD_GATHER_SQL = "INSERT INTO gather SET userid = ?,gather_type=?";
    private static final String DEL_GATHER_SQL = "DELETE FROM gather WHERE userid = ? LIMIT 1";
    private static final String ADD_REFINED_SQL = "INSERT INTO gather_refineds SET userid = ?,refinedid=?";
    private static final String DEL_REFINED_SQL = "DELETE FROM gather_refineds WHERE userid = ?";
    private static final String ADD_GATHER_LIST_SQL = "INSERT INTO gather_list SET userid = ?,ind = ?,soulid=?,num=?";
    private static final String DEL_GATHER_LISTS_SQL = "DELETE FROM gather_list WHERE userid = ?";
    private static final String DEL_GATHER_LIST_SQL = "DELETE FROM gather_list WHERE userid = ? AND ind = ?";

    public static void updateGather(final int _userID, final Gather _gather) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE gather SET lvl = ?,lvl_point = ? WHERE userid = ? LIMIT 1");
            pstm.setByte(1, _gather.getLvl());
            pstm.setInt(2, _gather.getPoint());
            pstm.setInt(3, _userID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            } catch (SQLException ex2) {
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
        } catch (SQLException ex3) {
        }
    }

    public static Gather loadGatherByUserID(final int _userID) {
        Gather gather = null;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT lvl,lvl_point FROM gather WHERE userid = ? LIMIT 1");
            pstm.setInt(1, _userID);
            rs = pstm.executeQuery();
            if (rs.next()) {
                byte lvl = rs.getByte(1);
                int point = rs.getInt(2);
                rs.close();
                rs = null;
                pstm.close();
                pstm = null;
                gather = new Gather();
                gather.setLvl(lvl);
                gather.setPoint(point);
                pstm = conn.prepareStatement("SELECT soulid,num FROM gather_list WHERE userid = ?");
                pstm.setInt(1, _userID);
                rs = pstm.executeQuery();
                while (rs.next()) {
                    int soulid = rs.getInt(1);
                    byte num = rs.getByte(2);
                    gather.loadMonsterSoul(new MonsterSoul(soulid, num));
                }
                rs.close();
                rs = null;
                pstm.close();
                pstm = null;
                pstm = conn.prepareStatement("SELECT refinedid FROM gather_refineds WHERE userid = ?");
                pstm.setInt(1, _userID);
                rs = pstm.executeQuery();
                while (rs.next()) {
                    int _refinedID = rs.getInt(1);
                    gather.addRefinedID(_refinedID);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException ex2) {
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
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException ex3) {
        }
        return gather;
    }

    public static void studyGather(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO gather SET userid = ?,gather_type=?");
            pstm.setInt(1, _userID);
            pstm.setByte(2, (byte) 0);
            pstm.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            } catch (SQLException ex2) {
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
        } catch (SQLException ex3) {
        }
    }

    public static void forgetGatherByUserID(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM gather WHERE userid = ? LIMIT 1");
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
            delRefinedByUserID(_userID);
            delGatherByUserID(_userID);
        } catch (Exception ex) {
            ex.printStackTrace();
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
            } catch (SQLException ex2) {
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
        } catch (SQLException ex3) {
        }
    }

    public static void addRefinedID(final int _userID, final int _refinedID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO gather_refineds SET userid = ?,refinedid=?");
            pstm.setInt(1, _userID);
            pstm.setInt(2, _refinedID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            } catch (SQLException ex2) {
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
        } catch (SQLException ex3) {
        }
    }

    private static void delRefinedByUserID(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM gather_refineds WHERE userid = ?");
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            } catch (SQLException ex2) {
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
        } catch (SQLException ex3) {
        }
    }

    public static void saveGahterByUserID(final int _userID, final ArrayList<MonsterSoul> _souls) {
        delGatherByUserID(_userID);
        Connection conn = null;
        PreparedStatement pstm = null;
        if (_souls.size() > 0) {
            try {
                conn = DBServiceImpl.getInstance().getConnection();
                conn.setAutoCommit(false);
                pstm = conn.prepareStatement("INSERT INTO gather_list SET userid = ?,ind = ?,soulid=?,num=?");
                byte i = 0;
                for (final MonsterSoul s : _souls) {
                    pstm.setInt(1, _userID);
                    PreparedStatement preparedStatement = pstm;
                    int n = 2;
                    byte b = i;
                    i = (byte) (b + 1);
                    preparedStatement.setByte(n, b);
                    pstm.setInt(3, s.soulID);
                    pstm.setByte(4, s.num);
                    pstm.addBatch();
                }
                pstm.executeBatch();
                conn.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
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
                } catch (SQLException ex2) {
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
            } catch (SQLException ex3) {
            }
        }
    }

    public static void delGatherByUserID(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM gather_list WHERE userid = ?");
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            } catch (SQLException ex2) {
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
        } catch (SQLException ex3) {
        }
    }

    public static void delGatherByUserID(final int _userID, final int _index) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM gather_list WHERE userid = ? AND ind = ?");
            pstm.setInt(1, _userID);
            pstm.setInt(2, _index);
            pstm.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            } catch (SQLException ex2) {
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
        } catch (SQLException ex3) {
        }
    }
}
