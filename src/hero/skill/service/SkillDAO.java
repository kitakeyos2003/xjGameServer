// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.service;

import hero.skill.dict.SkillDict;
import hero.skill.ActiveSkill;
import hero.player.HeroPlayer;
import hero.skill.Skill;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import hero.share.service.LogWriter;
import yoyo.service.tools.database.DBServiceImpl;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class SkillDAO {

    private static Logger log;
    private static String SELECT_SKILL_SQL;
    private static String INSERT_SKILL_SQL;
    private static String UPGRADE_SKILL_SQL;
    private static String DELETE_FORGET_SKILL_SQL;
    private static String DELETE_SKILL_CD_SQL;
    private static String UPGRADE_SKILL_CD_SQL;

    static {
        SkillDAO.log = Logger.getLogger((Class) SkillDAO.class);
        SkillDAO.SELECT_SKILL_SQL = "SELECT *FROM player_skill WHERE user_id = ? LIMIT 100";
        SkillDAO.INSERT_SKILL_SQL = "INSERT INTO player_skill (user_id,skill_id) VALUES(?,?) ";
        SkillDAO.UPGRADE_SKILL_SQL = "UPDATE player_skill SET skill_id = ? WHERE user_id = ? AND skill_id = ? LIMIT 1";
        SkillDAO.DELETE_FORGET_SKILL_SQL = "DELETE FROM player_skill WHERE user_id = ?  LIMIT 300";
        SkillDAO.DELETE_SKILL_CD_SQL = "UPDATE player_skill SET trace_cd_time = 0 WHERE user_id = ? AND  skill_id = ? LIMIT 1";
        SkillDAO.UPGRADE_SKILL_CD_SQL = "UPDATE player_skill SET trace_cd_time = ? WHERE user_id = ? AND skill_id = ?";
    }

    public static ArrayList<int[]> loadPlayerSkill(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SkillDAO.SELECT_SKILL_SQL);
            pstm.setInt(1, _userID);
            set = pstm.executeQuery();
            ArrayList<Integer> existsCDSkillIDList = null;
            ArrayList<int[]> skillInfoList = new ArrayList<int[]>();
            while (set.next()) {
                int skillID = set.getInt("skill_id");
                int traceCoolDownTime = set.getInt("trace_cd_time");
                skillInfoList.add(new int[]{skillID, traceCoolDownTime});
                if (traceCoolDownTime > 0) {
                    if (existsCDSkillIDList == null) {
                        existsCDSkillIDList = new ArrayList<Integer>();
                    }
                    existsCDSkillIDList.add(skillID);
                }
            }
            if (existsCDSkillIDList != null) {
                pstm.close();
                pstm = null;
                conn.setAutoCommit(false);
                pstm = conn.prepareStatement(SkillDAO.DELETE_SKILL_CD_SQL);
                for (final int id : existsCDSkillIDList) {
                    pstm.setInt(1, _userID);
                    pstm.setInt(2, id);
                    pstm.addBatch();
                }
                pstm.executeBatch();
                conn.setAutoCommit(true);
            }
            return skillInfoList;
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
                    if (!conn.getAutoCommit()) {
                        conn.setAutoCommit(true);
                    }
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        return null;
    }

    public static boolean changeCovation(final int _userID, final ArrayList<Skill> skills) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement(SkillDAO.INSERT_SKILL_SQL);
            for (final Skill skill : skills) {
                pstm.setInt(1, _userID);
                pstm.setInt(2, skill.id);
                pstm.addBatch();
            }
            pstm.executeBatch();
            conn.commit();
            pstm.close();
            pstm = null;
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
        return false;
    }

    public static boolean LearnSkill(final boolean _isNew, final int _userID, final int _skillID, final int _lowLevelSkillID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        Label_0290:
        {
            try {
                conn = DBServiceImpl.getInstance().getConnection();
                if (_isNew) {
                    pstm = conn.prepareStatement(SkillDAO.INSERT_SKILL_SQL);
                    pstm.setInt(1, _userID);
                    pstm.setInt(2, _skillID);
                    if (1 != pstm.executeUpdate()) {
                        break Label_0290;
                    }
                } else {
                    pstm = conn.prepareStatement(SkillDAO.UPGRADE_SKILL_SQL);
                    pstm.setInt(1, _skillID);
                    pstm.setInt(2, _userID);
                    pstm.setInt(3, _lowLevelSkillID);
                    if (1 != pstm.executeUpdate()) {
                        SkillDAO.log.info((Object) "\u6267\u884cUPDATE\u672a\u5f71\u54cd\u5230\u4efb\u4f55\u7684\u884c,\u8bf7\u6ce8\u610f!!!!!");
                        SkillDAO.log.info((Object) SkillDAO.UPGRADE_SKILL_SQL);
                        SkillDAO.log.info((Object) ("_skillID:" + _skillID + "_userID:" + _userID + "_lowLevelSkillID:" + _lowLevelSkillID));
                        break Label_0290;
                    }
                }
                return true;
            } catch (Exception e) {
                SkillDAO.log.error((Object) "\u66f4\u65b0\u6280\u80fd\u6570\u636e\u5e93\u51fa\u9519:");
                e.printStackTrace();
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
        return false;
    }

    public static void updateSkillTraceCD(final HeroPlayer _player) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            ArrayList<ActiveSkill> activeSkillList = _player.activeSkillList;
            ArrayList<ActiveSkill> updateSkillList = null;
            for (final ActiveSkill skill : activeSkillList) {
                if (skill.reduceCoolDownTime > 300) {
                    if (updateSkillList == null) {
                        updateSkillList = new ArrayList<ActiveSkill>();
                    }
                    updateSkillList.add(skill);
                }
            }
            if (updateSkillList != null) {
                conn = DBServiceImpl.getInstance().getConnection();
                conn.setAutoCommit(false);
                pstm = conn.prepareStatement(SkillDAO.UPGRADE_SKILL_CD_SQL);
                for (final ActiveSkill skill : updateSkillList) {
                    pstm.setInt(1, skill.reduceCoolDownTime - 300);
                    pstm.setInt(2, _player.getUserID());
                    pstm.setInt(3, skill.id);
                    pstm.addBatch();
                }
                pstm.executeBatch();
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
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
            } catch (Exception ex) {
            }
        }
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
        } catch (Exception ex2) {
        }
    }

    public static void forgetSkill(final HeroPlayer _player) {
        Connection conn = null;
        PreparedStatement pstm = null;
        PreparedStatement pstmInsert = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement(SkillDAO.DELETE_FORGET_SKILL_SQL);
            pstm.setInt(1, _player.getUserID());
            pstm.addBatch();
            pstm.executeBatch();
            conn.commit();
            pstmInsert = conn.prepareStatement(SkillDAO.INSERT_SKILL_SQL);
            ArrayList<Skill> skills = SkillDict.getInstance().getSkillsByVocation(_player.getVocation());
            for (final Skill skill : skills) {
                pstmInsert.setInt(1, _player.getUserID());
                pstmInsert.setInt(2, skill.id);
                pstmInsert.addBatch();
            }
            pstmInsert.executeBatch();
            conn.commit();
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (pstm != null) {
                    pstmInsert.close();
                    pstm.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (pstm != null) {
                pstmInsert.close();
                pstm.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (Exception ex2) {
        }
    }
}
