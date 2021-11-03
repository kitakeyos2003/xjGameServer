// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.service;

import hero.skill.PetActiveSkill;
import hero.skill.PetSkill;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import hero.pet.Pet;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import hero.share.service.LogWriter;
import hero.expressions.service.CEService;
import hero.pet.PetPK;
import yoyo.service.tools.database.DBServiceImpl;
import hero.pet.PetList;
import org.apache.log4j.Logger;

public class PetDAO {

    private static Logger log;
    private static final String SELECT_SQL = "SELECT * FROM pet WHERE user_id=? ORDER BY get_time ASC";
    private static final String SELECT_MOUNT_SQL = "SELECT pet_id from pet where status=1 and type=1 and stage=2 and user_id=?";
    private static final String INSERT_SQL = "INSERT INTO pet(user_id,pet_id,stage,type,color,born_from,bind,name,kind)  VALUES(?,?,?,?,?,?,?,?,?)";
    private static final String SELECT_MAX_ID_PET_SQL = "SELECT MAX(t.pet_id) FROM pet t";
    private static final String CLEAR_OLD_STATUS_SQL = "UPDATE pet SET status=0 WHERE user_id=? AND status=1 LIMIT 1";
    private static final String UPDATE_NOW_STATUS_SQL = "UPDATE pet SET status=1 WHERE user_id=? AND pet_id=? LIMIT 1";
    private static final String DELETE_SQL = "DELETE FROM pet WHERE user_id=? AND pet_id=? LIMIT 1";
    private static final String UPDATE_PET_STAGE_SQL = "UPDATE pet t SET t.stage=?,t.total_online_time=?,t.feeding=?,t.fun=?,t.type=?,t.curr_evolve_point=?,t.curr_herb_point=?,t.curr_carn_point=?,t.curr_fight_point=?,t.mp=?,t.rage=?,t.wit=?,t.agile=?,t.grow_exp=?,t.fight_exp=?,t.health_time=?,t.status=?,t.level=?,t.curr_level_time=? WHERE t.user_id=? AND t.pet_id=?";
    private static final String UPD_UPGRADE_PET_SQL = "UPDATE pet t SET t.level=?,t.curr_level_time=?,t.mp=?  WHERE t.user_id=? AND t.pet_id=?";
    private static final String UPDATE_PET_NAME_SQL = "UPDATE pet t SET t.name=? WHERE t.user_id=? AND t.pet_id=?";
    private static final String UPDATE_PET_OWNER_SQL = "UPDATE pet t set t.user_id=?,t.born_from=? WHERE t.user_id=? AND t.pet_id=?";
    private static final String SELECT_PET_SKILL_SQL = "SELECT * FROM pet_skill t WHERE t.pet_id=? ";
    private static String INSERT_SKILL_SQL;
    private static String UPGRADE_SKILL_SQL;
    private static String DELETE_SKILL_CD_SQL;
    private static String UPGRADE_SKILL_CD_SQL;
    private static final String UPDATE_PET_EQUIPMENT = "UPDATE pet t SET t.equip_1=?, t.equip_2=?,t.equip_3=?,t.equip_4=? where t.pet_id=?";

    static {
        PetDAO.log = Logger.getLogger((Class) PetDAO.class);
        PetDAO.INSERT_SKILL_SQL = "INSERT INTO pet_skill(pet_id,skill_id) VALUES(?,?)";
        PetDAO.UPGRADE_SKILL_SQL = "UPDATE pet_skill t SET t.skill_id = ? WHERE t.pet_id = ? AND t.skill_id = ? LIMIT 1";
        PetDAO.DELETE_SKILL_CD_SQL = "UPDATE pet_skill t SET t.trace_cd_time = 0 WHERE t.pet_id = ? AND  t.skill_id = ? LIMIT 1";
        PetDAO.UPGRADE_SKILL_CD_SQL = "UPDATE pet_skill t SET t.trace_cd_time = ? WHERE t.pet_id = ? AND t.skill_id = ?";
    }

    public static PetList load(final int _userID) {
        PetDAO.log.debug("start load pet .. @@@@@@@");
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM pet WHERE user_id=? ORDER BY get_time ASC");
            pstm.setInt(1, _userID);
            set = pstm.executeQuery();
            PetList petList = new PetList();
            int viewsize = 0;
            int lastviewsize = 0;
            while (set.next()) {
                int petID = set.getInt("pet_id");
                short viewStatus = set.getShort("status");
                short stage = set.getShort("stage");
                short type = set.getShort("type");
                short kind = set.getShort("kind");
                short color = set.getShort("color");
                short fun = set.getShort("fun");
                int curr_fight_point = set.getInt("curr_fight_point");
                int curr_evolve_time = set.getInt("curr_evolve_point");
                short born_from = set.getShort("born_from");
                short bind = set.getShort("bind");
                int feeding = set.getInt("feeding");
                int mp = set.getInt("mp");
                String name = set.getString("name");
                int curr_herb_point = set.getInt("curr_herb_point");
                int curr_carn_point = set.getInt("curr_carn_point");
                int health_time = set.getInt("health_time");
                int total_online_time = set.getInt("total_online_time");
                int level = set.getInt("level");
                int curr_level_time = set.getInt("curr_level_time");
                PetPK pk = new PetPK(kind, stage, type);
                Pet pet = PetDictionary.getInstance().getPet(pk);
                pet.id = petID;
                pet.pk = pk;
                pet.bind = bind;
                pet.color = color;
                pet.fun = fun;
                pet.currEvolvePoint = curr_evolve_time;
                pet.currFightPoint = curr_fight_point;
                pet.bornFrom = born_from;
                pet.feeding = feeding;
                pet.mp = mp;
                pet.name = name;
                pet.currCarnPoint = curr_carn_point;
                pet.currHerbPoint = curr_herb_point;
                pet.healthTime = health_time;
                pet.totalOnlineTime = total_online_time;
                pet.level = level;
                pet.currLevelTime = curr_level_time;
                if (type == 2) {
                    int rage = set.getInt("rage");
                    int wit = set.getInt("wit");
                    int agile = set.getInt("agile");
                    int grow_exp = set.getInt("grow_exp");
                    int fight_exp = set.getInt("fight_exp");
                    int equip_1 = set.getInt("equip_1");
                    int equip_2 = set.getInt("equip_2");
                    int equip_3 = set.getInt("equip_3");
                    int equip_4 = set.getInt("equip_4");
                    pet.rage = rage;
                    pet.wit = wit;
                    pet.agile = agile;
                    pet.grow_exp = grow_exp;
                    pet.fight_exp = fight_exp;
                    pet.str = CEService.playerBaseAttribute(pet.level, (float) pet.a_str);
                    pet.agi = CEService.playerBaseAttribute(pet.level, (float) pet.a_agi);
                    pet.intel = CEService.playerBaseAttribute(pet.level, (float) pet.a_intel);
                    pet.spi = CEService.playerBaseAttribute(pet.level, (float) pet.a_spi);
                    pet.luck = CEService.playerBaseAttribute(pet.level, (float) pet.a_luck);
                    pet.petEquList.add(equip_1);
                    pet.petEquList.add(equip_2);
                    pet.petEquList.add(equip_3);
                    pet.petEquList.add(equip_4);
                }
                petList.add(pet);
                PetDAO.log.debug(("pet list add [ " + _userID + " --- " + pet.id + "] end"));
                if (viewStatus == 1) {
                    if (pet.pk.getStage() != 0) {
                        pet.isView = true;
                    }
                    if (viewsize <= 2) {
                        viewsize = petList.setViewPet(pet);
                        ++viewsize;
                    }
                    if (lastviewsize > 2) {
                        continue;
                    }
                    petList.setLastTimesViewPetID(petID);
                    ++lastviewsize;
                }
            }
            PetDAO.log.debug("load pet list end..@@@@@ ");
            set.close();
            pstm.close();
            conn.close();
            return petList;
        } catch (SQLException e) {
            e.printStackTrace();
            LogWriter.error(null, e);
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
            } catch (Exception e2) {
                e2.printStackTrace();
            }
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
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public static ArrayList<int[]> loadPetSkill(final Pet pet) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM pet_skill t WHERE t.pet_id=? ");
            pstm.setInt(1, pet.id);
            rs = pstm.executeQuery();
            ArrayList<Integer> existsCDSkillIDList = null;
            ArrayList<int[]> skillInfoList = new ArrayList<int[]>();
            while (rs.next()) {
                int skillID = rs.getInt("skill_id");
                int traceCoolDownTime = rs.getInt("trace_cd_time");
                skillInfoList.add(new int[]{skillID, traceCoolDownTime});
                if (traceCoolDownTime > 0) {
                    if (existsCDSkillIDList == null) {
                        existsCDSkillIDList = new ArrayList<Integer>();
                    }
                    existsCDSkillIDList.add(skillID);
                }
            }
            rs.close();
            if (existsCDSkillIDList != null) {
                pstm.close();
                pstm = null;
                conn.setAutoCommit(false);
                pstm = conn.prepareStatement(PetDAO.DELETE_SKILL_CD_SQL);
                for (final int id : existsCDSkillIDList) {
                    pstm.setInt(1, pet.id);
                    pstm.setInt(2, id);
                    pstm.addBatch();
                }
                pstm.executeBatch();
                conn.setAutoCommit(true);
            }
            pstm.close();
            return skillInfoList;
        } catch (Exception e) {
            e.printStackTrace();
            PetDAO.log.error("\u4eceDB\u52a0\u8f7d\u5ba0\u7269\u6280\u80fd\u5217\u8868 error : ", (Throwable) e);
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    if (!conn.getAutoCommit()) {
                        conn.setAutoCommit(true);
                    }
                    conn.close();
                    conn = null;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    if (!conn.getAutoCommit()) {
                        conn.setAutoCommit(true);
                    }
                    conn.close();
                    conn = null;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public static int selectMountPet(final int _userID) {
        int petID = 0;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT pet_id from pet where status=1 and type=1 and stage=2 and user_id=?");
            pstm.setInt(1, _userID);
            rs = pstm.executeQuery();
            if (rs.next()) {
                petID = rs.getInt("pet_id");
            }
            pstm.close();
            rs.close();
            pstm = null;
            rs = null;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return petID;
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
            } catch (SQLException e2) {
                e2.printStackTrace();
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
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        return petID;
    }

    public static boolean addSkill(final int petID, final List<PetSkill> skillList) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            PetDAO.log.debug(("pet learn skill size = " + skillList.size()));
            conn = DBServiceImpl.getInstance().getConnection();
            for (final PetSkill skill : skillList) {
                if (skill.isNewSkill) {
                    pstm = conn.prepareStatement(PetDAO.INSERT_SKILL_SQL);
                    pstm.setInt(1, petID);
                    pstm.setInt(2, skill.id);
                    pstm.execute();
                    pstm.close();
                } else {
                    pstm = conn.prepareStatement(PetDAO.UPGRADE_SKILL_SQL);
                    pstm.setInt(1, skill.id);
                    pstm.setInt(2, petID);
                    pstm.setInt(3, skill._lowLevelSkillID);
                    pstm.executeUpdate();
                    pstm.close();
                }
                pstm = null;
            }
            return true;
        } catch (SQLException e) {
            PetDAO.log.error("\u5ba0\u7269\u6dfb\u52a0\u6280\u80fd to DB error : ", (Throwable) e);
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
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void updatePetSkillTraceCD(final Pet pet) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            List<PetActiveSkill> activeSkillList = pet.petActiveSkillList;
            List<PetActiveSkill> updateSkillList = null;
            for (final PetActiveSkill skill : activeSkillList) {
                if (skill.reduceCoolDownTime > 300) {
                    if (updateSkillList == null) {
                        updateSkillList = new ArrayList<PetActiveSkill>();
                    }
                    updateSkillList.add(skill);
                }
            }
            if (updateSkillList != null) {
                conn = DBServiceImpl.getInstance().getConnection();
                conn.setAutoCommit(false);
                pstm = conn.prepareStatement(PetDAO.UPGRADE_SKILL_CD_SQL);
                for (final PetActiveSkill skill : updateSkillList) {
                    pstm.setInt(1, skill.reduceCoolDownTime - 300);
                    pstm.setInt(2, pet.id);
                    pstm.setInt(3, skill.id);
                    pstm.addBatch();
                }
                pstm.executeBatch();
                conn.setAutoCommit(true);
            }
            pstm.close();
        } catch (Exception e) {
            PetDAO.log.error("\u73a9\u5bb6\u4e0b\u7ebf\u65f6\uff0c\u4fdd\u5b58\u5ba0\u7269\u6280\u80fd\u7684\u51b7\u5374\u65f6\u95f4 error : ", (Throwable) e);
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
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return;
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
            } catch (Exception e2) {
                e2.printStackTrace();
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
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void addPetForNewPlaye(final int _userID, final Pet _pet) {
        add(_userID, _pet);
    }

    public static void add(final int _userID, final Pet _pet) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT MAX(t.pet_id) FROM pet t");
            rs = pstm.executeQuery();
            rs.next();
            int id = rs.getInt(1);
            PetDAO.log.debug(("max pet id = " + id));
            rs.close();
            pstm.close();
            pstm = conn.prepareStatement("INSERT INTO pet(user_id,pet_id,stage,type,color,born_from,bind,name,kind)  VALUES(?,?,?,?,?,?,?,?,?)");
            pstm.setInt(1, _userID);
            pstm.setInt(2, id + 1);
            pstm.setShort(3, _pet.pk.getStage());
            pstm.setShort(4, _pet.pk.getType());
            pstm.setShort(5, _pet.color);
            pstm.setShort(6, _pet.bornFrom);
            pstm.setShort(7, _pet.bind);
            pstm.setString(8, _pet.name);
            pstm.setShort(9, _pet.pk.getKind());
            pstm.execute();
            pstm.close();
            conn.close();
            _pet.id = id + 1;
            PetDAO.log.debug(("add pet end . pet id=" + _pet.id));
        } catch (Exception e) {
            e.printStackTrace();
            PetDAO.log.error("add pet errors : ", (Throwable) e);
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
            } catch (SQLException e2) {
                e2.printStackTrace();
                PetDAO.log.error("\u6dfb\u52a0\u5ba0\u7269\u86cb errors : ", (Throwable) e2);
            }
            return;
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
            } catch (SQLException e2) {
                e2.printStackTrace();
                PetDAO.log.error("\u6dfb\u52a0\u5ba0\u7269\u86cb errors : ", (Throwable) e2);
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
        } catch (SQLException e2) {
            e2.printStackTrace();
            PetDAO.log.error("\u6dfb\u52a0\u5ba0\u7269\u86cb errors : ", (Throwable) e2);
        }
    }

    public static void updatePet(final int _userID, final Pet _pet) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            PetDAO.log.debug(("user : " + _userID + " , pet dao udpdate pet id = " + _pet.id + ", pet healthtime=" + _pet.healthTime));
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE pet t SET t.stage=?,t.total_online_time=?,t.feeding=?,t.fun=?,t.type=?,t.curr_evolve_point=?,t.curr_herb_point=?,t.curr_carn_point=?,t.curr_fight_point=?,t.mp=?,t.rage=?,t.wit=?,t.agile=?,t.grow_exp=?,t.fight_exp=?,t.health_time=?,t.status=?,t.level=?,t.curr_level_time=? WHERE t.user_id=? AND t.pet_id=?");
            ps.setShort(1, _pet.pk.getStage());
            ps.setLong(2, _pet.totalOnlineTime);
            ps.setInt(3, (_pet.feeding >= 0) ? _pet.feeding : 0);
            ps.setShort(4, _pet.fun);
            ps.setShort(5, _pet.pk.getType());
            ps.setInt(6, _pet.currEvolvePoint);
            ps.setInt(7, _pet.currHerbPoint);
            ps.setInt(8, _pet.currCarnPoint);
            ps.setInt(9, _pet.currFightPoint);
            ps.setInt(10, _pet.mp);
            ps.setInt(11, _pet.rage);
            ps.setInt(12, _pet.wit);
            ps.setInt(13, _pet.agile);
            ps.setInt(14, _pet.grow_exp);
            ps.setInt(15, _pet.fight_exp);
            ps.setInt(16, _pet.healthTime);
            ps.setShort(17, _pet.viewStatus);
            ps.setInt(18, _pet.level);
            ps.setInt(19, _pet.currLevelTime);
            ps.setInt(20, _userID);
            ps.setInt(21, _pet.id);
            ps.executeUpdate();
            PetDAO.log.debug("pet dao udpdate pet end....");
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            PetDAO.log.error("\u4fee\u6539\u5ba0\u7269\u5c5e\u6027:", (Throwable) e);
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (Exception e2) {
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
            } catch (Exception e2) {
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
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void upgradePet(final int _userID, final Pet pet) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE pet t SET t.level=?,t.curr_level_time=?,t.mp=?  WHERE t.user_id=? AND t.pet_id=?");
            ps.setInt(1, pet.level);
            ps.setInt(2, pet.currLevelTime);
            ps.setInt(3, pet.mp);
            ps.setInt(4, _userID);
            ps.setInt(5, pet.id);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogWriter.error("\u5ba0\u7269\u5347\u7ea7:", e);
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (Exception e2) {
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
            } catch (Exception e2) {
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
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static int updatePetName(final int _userID, final int _petID, final String name) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE pet t SET t.name=? WHERE t.user_id=? AND t.pet_id=?");
            pstm.setString(1, name);
            pstm.setInt(2, _userID);
            pstm.setInt(3, _petID);
            return pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            LogWriter.error("\u4fee\u6539\u5ba0\u7269\u540d\u79f0 :  ", e);
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                    return 0;
                }
                return 0;
            } catch (Exception e2) {
                return 0;
            }
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                return 0;
            }
        }
    }

    public static int updatePetOwner(final int buyerID, final int sellerID, final int petID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE pet t set t.user_id=?,t.born_from=? WHERE t.user_id=? AND t.pet_id=?");
            pstm.setInt(1, buyerID);
            pstm.setShort(2, (short) 0);
            pstm.setInt(3, sellerID);
            pstm.setInt(4, petID);
            int res = pstm.executeUpdate();
            return res;
        } catch (Exception e) {
            LogWriter.error("\u4ea4\u6613\u5ba0\u7269 error: ", e);
            return 0;
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void updateViewStatus(final int _userID, final int _viewPetID, final byte _updateMark) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            if (1 == _updateMark) {
                pstm = conn.prepareStatement("UPDATE pet SET status=0 WHERE user_id=? AND status=1 LIMIT 1");
                pstm.setInt(1, _userID);
                pstm.executeUpdate();
            } else if (2 == _updateMark) {
                pstm = conn.prepareStatement("UPDATE pet SET status=1 WHERE user_id=? AND pet_id=? LIMIT 1");
                pstm.setInt(1, _userID);
                pstm.setInt(2, _viewPetID);
                pstm.executeUpdate();
            } else if (3 == _updateMark) {
                pstm.close();
                pstm = conn.prepareStatement("UPDATE pet SET status=1 WHERE user_id=? AND pet_id=? LIMIT 1");
                pstm.setInt(1, _userID);
                pstm.setInt(2, _viewPetID);
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LogWriter.error(null, e);
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
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
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void dice(final int _userID, final int _petID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM pet WHERE user_id=? AND pet_id=? LIMIT 1");
            pstm.setInt(1, _userID);
            pstm.setInt(2, _petID);
            pstm.execute();
        } catch (Exception e) {
            e.printStackTrace();
            LogWriter.error(null, e);
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
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
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void updPetEquipment(final Pet pet) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE pet t SET t.equip_1=?, t.equip_2=?,t.equip_3=?,t.equip_4=? where t.pet_id=?");
            pstm.setInt(1, (pet.getPetBodyWear().getPetEqHead() == null) ? 0 : pet.getPetBodyWear().getPetEqHead().getInstanceID());
            pstm.setInt(2, (pet.getPetBodyWear().getPetEqBody() == null) ? 0 : pet.getPetBodyWear().getPetEqBody().getInstanceID());
            pstm.setInt(3, (pet.getPetBodyWear().getPetEqWeapon() == null) ? 0 : pet.getPetBodyWear().getPetEqWeapon().getInstanceID());
            pstm.setInt(4, (pet.getPetBodyWear().getPetEqTail() == null) ? 0 : pet.getPetBodyWear().getPetEqTail().getInstanceID());
            pstm.setInt(5, pet.id);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            LogWriter.error(null, e);
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
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
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
