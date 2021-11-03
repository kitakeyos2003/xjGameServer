// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.teach;

import hero.share.service.LogWriter;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import yoyo.service.tools.database.DBServiceImpl;
import org.apache.log4j.Logger;

public class TeachDAO {

    private static Logger log;
    private static final String SELECT_TEACH_SQL = "SELECT * FROM master_apprentice WHERE apprentice_user_id = ? OR master_user_id = ? LIMIT 6";
    private static final String SELECT_TEACH_BY_NAME_SQL = "select * from master_apprentice where apprentice_name=?  or master_name=? limit 6";
    private static final String INSERT_MASTER_APPRENTICE = "INSERT INTO master_apprentice(apprentice_user_id,apprentice_name,master_user_id,master_name) VALUES(?,?,?,?)";
    private static final String UPDATE_MASTER_APPRENTICE = "UPDATE master_apprentice SET teach_times = ?,level_of_last_teach = ? WHERE apprentice_user_id = ? LIMIT 1";
    private static final String DELETE_MASTER_APPRENTICE = "DELETE FROM master_apprentice WHERE apprentice_user_id=? LIMIT 1";
    private static final String DELETE_All_MASTER_APPRENTICE_BY_MASTER = "DELETE FROM master_apprentice WHERE master_user_id=? LIMIT 5";
    private static final String DELETE_ALL_RELATION_SQL = "DELETE FROM master_apprentice WHERE apprentice_user_id = ? OR master_user_id = ? LIMIT 6";

    static {
        TeachDAO.log = Logger.getLogger((Class) TeachDAO.class);
    }

    public static void loadMasterApprenticeRelation(final int _userID, final MasterApprentice _masterApprentice) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM master_apprentice WHERE apprentice_user_id = ? OR master_user_id = ? LIMIT 6");
            pstm.setInt(1, _userID);
            pstm.setInt(2, _userID);
            set = pstm.executeQuery();
            if (set.next()) {
                int apprenticeUserID = set.getInt(1);
                String apprenticeName = set.getString(2);
                int masterUserID = set.getInt(3);
                String masterName = set.getString(4);
                byte teachTimes = set.getByte(5);
                short levelOfLastTeach = set.getShort(6);
                if (apprenticeUserID == _userID) {
                    TeachDAO.log.debug((Object) ("load masterapprentice .. user is apprenticeUser... masterUserID=" + masterUserID));
                    _masterApprentice.setMaster(masterUserID, masterName, false);
                } else {
                    TeachDAO.log.debug((Object) ("load masterapprentice .. user is master... apprenticeUserID=" + apprenticeUserID));
                    _masterApprentice.addNewApprenticer(apprenticeUserID, apprenticeName, teachTimes, levelOfLastTeach);
                }
                while (set.next()) {
                    apprenticeUserID = set.getInt(1);
                    apprenticeName = set.getString(2);
                    masterUserID = set.getInt(3);
                    masterName = set.getString(4);
                    teachTimes = set.getByte(5);
                    levelOfLastTeach = set.getShort(6);
                    if (apprenticeUserID == _userID) {
                        _masterApprentice.setMaster(masterUserID, masterName, false);
                    } else {
                        _masterApprentice.addNewApprenticer(apprenticeUserID, apprenticeName, teachTimes, levelOfLastTeach);
                    }
                }
            }
        } catch (Exception e) {
            TeachDAO.log.error((Object) "\u52a0\u8f7d\u5e08\u5f92\u5173\u7cfb\u4fe1\u606f: ", (Throwable) e);
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
    }

    public static MasterApprentice loadMasterApprenticeRelationByName(final String userName, final MasterApprentice _masterApprentice) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("select * from master_apprentice where apprentice_name=?  or master_name=? limit 6");
            pstm.setString(1, userName);
            pstm.setString(2, userName);
            set = pstm.executeQuery();
            if (set.next()) {
                int apprenticeUserID = set.getInt(1);
                String apprenticeName = set.getString(2);
                int masterUserID = set.getInt(3);
                String masterName = set.getString(4);
                byte teachTimes = set.getByte(5);
                short levelOfLastTeach = set.getShort(6);
                TeachDAO.log.debug((Object) ("load off line masterApprentice : " + apprenticeName + ", mastername=" + masterName));
                if (apprenticeName.equals(userName)) {
                    TeachDAO.log.debug((Object) "apprenticeName.equals(userName)");
                    _masterApprentice.setMaster(masterUserID, masterName, false);
                } else {
                    TeachDAO.log.debug((Object) "!!!! apprenticeName.equals(userName)");
                    _masterApprentice.addNewApprenticer(apprenticeUserID, apprenticeName, teachTimes, levelOfLastTeach);
                }
                while (set.next()) {
                    apprenticeUserID = set.getInt(1);
                    apprenticeName = set.getString(2);
                    masterUserID = set.getInt(3);
                    masterName = set.getString(4);
                    teachTimes = set.getByte(5);
                    levelOfLastTeach = set.getShort(6);
                    TeachDAO.log.debug((Object) ("load off line masterApprentice 2 : " + apprenticeName + ", mastername=" + masterName));
                    if (apprenticeName.equals(userName)) {
                        _masterApprentice.setMaster(masterUserID, masterName, false);
                    } else {
                        _masterApprentice.addNewApprenticer(apprenticeUserID, apprenticeName, teachTimes, levelOfLastTeach);
                    }
                }
            }
        } catch (Exception e) {
            TeachDAO.log.error((Object) "\u6839\u636e\u6635\u79f0\u83b7\u53d6\u73a9\u5bb6\u5e08\u5f92\u5173\u7cfb error: ", (Throwable) e);
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
        return _masterApprentice;
    }

    public static boolean insertMasterApprentice(final int _apprenticeUserID, final String _apprenticeName, final int _masterUserID, final String _masterName) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO master_apprentice(apprentice_user_id,apprentice_name,master_user_id,master_name) VALUES(?,?,?,?)");
            pstm.setInt(1, _apprenticeUserID);
            pstm.setString(2, _apprenticeName);
            pstm.setInt(3, _masterUserID);
            pstm.setString(4, _masterName);
            pstm.executeUpdate();
            return true;
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
        return false;
    }

    public static boolean changeMasterApprentice(final int _apprenticeUserID, final byte _times, final short _levelOfLastTeach) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE master_apprentice SET teach_times = ?,level_of_last_teach = ? WHERE apprentice_user_id = ? LIMIT 1");
            pstm.setByte(1, _times);
            pstm.setShort(2, _levelOfLastTeach);
            pstm.setInt(3, _apprenticeUserID);
            pstm.executeUpdate();
            return true;
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
        return false;
    }

    public static boolean deleteAllMasterApprenticeRelation(final int masterID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM master_apprentice WHERE master_user_id=? LIMIT 5");
            pstm.setInt(1, masterID);
            pstm.executeUpdate();
            return true;
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
        return false;
    }

    public static boolean deleteMasterApprentice(final int _apprenticeUserID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM master_apprentice WHERE apprentice_user_id=? LIMIT 1");
            pstm.setInt(1, _apprenticeUserID);
            pstm.executeUpdate();
            return true;
        } catch (Exception e) {
            TeachDAO.log.error((Object) "deleteMasterApprentice error : ", (Throwable) e);
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
        return false;
    }

    public static boolean deleteAll(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM master_apprentice WHERE apprentice_user_id = ? OR master_user_id = ? LIMIT 6");
            pstm.setInt(1, _userID);
            pstm.setInt(2, _userID);
            pstm.executeUpdate();
            return true;
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
        return false;
    }
}
