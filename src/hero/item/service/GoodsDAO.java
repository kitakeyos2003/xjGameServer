// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.service;

import hero.item.bag.Inventory;
import hero.item.enhance.EnhanceService;
import hero.player.HeroPlayer;
import hero.item.special.PetPerCard;
import hero.item.special.BigTonicBall;
import hero.item.special.ESpecialGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.SpecialGoods;
import java.sql.ResultSet;
import java.util.Iterator;
import hero.item.EquipmentInstance;
import java.util.ArrayList;
import hero.item.bag.SingleGoodsBag;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import hero.share.service.LogWriter;
import yoyo.service.tools.database.DBServiceImpl;
import org.apache.log4j.Logger;

public final class GoodsDAO {

    private static Logger log;
    private static final String SELECT_MAX_SPECAIL_GOODS_ID = "SELECT MAX(id) as max_goods_id from player_single_goods";
    private static final String UPDATE_BAG_EQUIPMENT_LOCATION_SQL = "UPDATE player_carry_equipment SET container_type=?,package_index=? WHERE instance_id=? LIMIT 1";
    private static final String DELETE_PLAYER_CARRY_EQUIPMENT_SQL = "DELETE FROM player_carry_equipment WHERE instance_id=? LIMIT 1";
    private static final String DELETE_EQUIPMENT_INSTANCE_SQL = "DELETE FROM equipment_instance WHERE instance_id=? LIMIT 1";
    private static final String INSERT_EQUIPMENT_INSTANCE_SQL = "INSERT INTO equipment_instance(instance_id,equipment_id,creator_user_id,owner_user_id,current_durability,be_sealed,bind,owner_type) VALUES (?,?,?,?,?,?,?,?)";
    private static final String CLEAR_UP_EQUIPMENT_PACKAGE_BATCH_SQL = "UPDATE player_carry_equipment SET package_index=? WHERE instance_id=? LIMIT 1";
    private static final String UPDATA_SINGLE_GOODS_BAG_SQL = "UPDATE player_single_goods SET goods_number=? WHERE user_id=? AND goods_id=? AND package_index=? LIMIT 1";
    private static final String INSERT_EQUIPMENT_BAG_SQL = "INSERT INTO player_carry_equipment(instance_id,user_id,package_index,container_type) VALUES (?,?,?,?)";
    private static final String INSERT_EQUIPMENT_TO_BODY_SQL = "INSERT INTO player_carry_equipment(instance_id,user_id,container_type) VALUES (?,?,?)";
    private static final String INSERT_SINGLE_GOODS_SQL = "INSERT INTO player_single_goods(user_id,goods_type,goods_id,goods_number,package_index,id) VALUES (?,?,?,?,?,?)";
    private static final String DELETE_GRID_SINGLE_GOODS_SQL = "DELETE FROM player_single_goods WHERE user_id=? AND package_index=? AND goods_id=? LIMIT 1";
    private static final String DELETE_SINGLE_GOODS_SQL = "DELETE FROM player_single_goods WHERE user_id=? AND goods_id=? LIMIT 80";
    private static final String DELETE_PACKAGE_SINGLE_GOODS_SQL = "DELETE FROM player_single_goods WHERE user_id=? AND goods_type=? LIMIT 80";
    private static final String SELECT_SINGLE_GOODS_SQL = "SELECT id,goods_id,goods_type,goods_number,package_index from player_single_goods where user_id=? LIMIT 280";
    private static final String SELECT_TONIC_GOODS_SQL = "SELECT tonic_id,surplus_point,type  from big_tonic_ball  where single_goods_id=? LIMIT 1";
    private static final String SELECT_PET_PER_CARD_GOODS_SQL = "SELECT card_id,surplus_point  from pet_per_card  where single_goods_id=? LIMIT 1";
    private static final String SELECT_EQUIPMENT_SQL = "SELECT player_carry_equipment.instance_id,equipment_id,creator_user_id,current_durability,generic_enhance_desc,bloody_enhance_desc,be_sealed,bind,container_type,package_index FROM equipment_instance JOIN player_carry_equipment ON user_id=? AND player_carry_equipment.instance_id=equipment_instance.instance_id LIMIT 100";
    private static final String UPDATE_EQUIPMENT_OWNER_SQL = "UPDATE player_carry_equipment SET user_id=?,package_index=? WHERE instance_id=? LIMIT 1";
    private static final String UPDATE_EQUIPMENT_DURABILITY_SQL = "UPDATE equipment_instance SET current_durability=? WHERE instance_id=? LIMIT 1";
    private static final String UPDATE_EQUIPMENT_SEAL_SQL = "UPDATE equipment_instance SET be_sealed=0 WHERE instance_id=? LIMIT 1";
    private static final String UPDATE_EQUIPMENT_BIND_SQL = "UPDATE equipment_instance SET bind = 1 WHERE instance_id=? LIMIT 1";
    private static final String UPDATE_WEAPON_ENHANCE_SQL = "UPDATE equipment_instance SET bloody_enhance_desc=? WHERE instance_id=? LIMIT 1";
    private static final String UPDATE_EQUIPMENT_ENHANCE_SQL = "UPDATE equipment_instance SET generic_enhance_desc=? WHERE instance_id=? LIMIT 1";
    private static final String UPDATE_EQUIPMENT_INSTANCE_OWNER_SQL = "UPDATE equipment_instance SET owner_user_id=? WHERE instance_id=? LIMIT 1";
    private static final String UPDATE_BAG_SIZE_SQL = "UPDATE player SET bag_size=? WHERE user_id=? LIMIT 1";
    private static final String SEL_INSTANCE_SQL = "SELECT equipment_id,creator_user_id,owner_user_id,current_durability,generic_enhance_desc,bloody_enhance_desc,be_sealed,bind FROM equipment_instance WHERE instance_id = ? LIMIT 1";
    private static final String UPDATE_HOME_SQL = "UPDATE player SET home_id=? WHERE user_id=? LIMIT 1";
    private static final String BAG_SIZE_CONNECTOR = "&";

    static {
        GoodsDAO.log = Logger.getLogger((Class) GoodsDAO.class);
    }

    public static boolean diceEquipment(final int _equipmentInsID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM player_carry_equipment WHERE instance_id=? LIMIT 1");
            pstm.setInt(1, _equipmentInsID);
            if (pstm.executeUpdate() > 0) {
                pstm.close();
                pstm = null;
                pstm = conn.prepareStatement("DELETE FROM equipment_instance WHERE instance_id=? LIMIT 1");
                pstm.setInt(1, _equipmentInsID);
                if (pstm.executeUpdate() > 0) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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
            return false;
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
        return false;
    }

    public static boolean removeEquipmentOfBag(final int _equipmentInsID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM player_carry_equipment WHERE instance_id=? LIMIT 1");
            pstm.setInt(1, _equipmentInsID);
            if (pstm.executeUpdate() > 0) {
                return true;
            }
        } catch (Exception ex) {
            LogWriter.error(null, ex);
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
        return false;
    }

    public static boolean diceEquipmentInstance(final int _equipmentInsID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("DELETE FROM equipment_instance WHERE instance_id=? LIMIT 1");
            pstm.setInt(1, _equipmentInsID);
            if (pstm.executeUpdate() > 0) {
                return true;
            }
        } catch (Exception ex) {
            LogWriter.error(null, ex);
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
        return false;
    }

    public static boolean removeSingleGoodsFromBag(final int _ownerUserID, final short _gridIndex, final int _goodsID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM player_single_goods WHERE user_id=? AND package_index=? AND goods_id=? LIMIT 1");
            pstm.setInt(1, _ownerUserID);
            pstm.setShort(2, _gridIndex);
            pstm.setInt(3, _goodsID);
            if (pstm.executeUpdate() > 0) {
                return true;
            }
        } catch (Exception ex) {
            GoodsDAO.log.error((Object) "\u5220\u9664\u67d0\u4e2a\u683c\u5b50\u91cc\u7684\u975e\u88c5\u5907\u7269\u54c1 error:", (Throwable) ex);
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
        return false;
    }

    public static boolean removeSingleGoodsFromBag(final int _ownerUserID, final int _goodsID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM player_single_goods WHERE user_id=? AND goods_id=? LIMIT 80");
            pstm.setInt(1, _ownerUserID);
            pstm.setInt(2, _goodsID);
            if (pstm.executeUpdate() > 0) {
                return true;
            }
        } catch (Exception ex) {
            LogWriter.error(null, ex);
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
        return false;
    }

    public static void clearUpSingleGoodsPackage(final int _ownerUserID, final SingleGoodsBag _singleGoodsPackage, final byte _singleGoodsType) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement("DELETE FROM player_single_goods WHERE user_id=? AND goods_type=? LIMIT 80");
            pstm.setInt(1, _ownerUserID);
            pstm.setShort(2, _singleGoodsType);
            pstm.executeUpdate();
            pstm.close();
            pstm = null;
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement("INSERT INTO player_single_goods(user_id,goods_type,goods_id,goods_number,package_index,id) VALUES (?,?,?,?,?,?)");
            int[][] goodsList = _singleGoodsPackage.getAllItem();
            for (int i = 0; i < goodsList.length; ++i) {
                if (goodsList[i][0] != 0) {
                    pstm.setInt(1, _ownerUserID);
                    pstm.setShort(2, _singleGoodsType);
                    pstm.setInt(3, goodsList[i][0]);
                    pstm.setInt(4, goodsList[i][1]);
                    pstm.setInt(5, i);
                    int id = GoodsServiceImpl.getInstance().getUseableSpecailID();
                    pstm.setInt(6, id);
                    pstm.addBatch();
                }
            }
            pstm.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            GoodsDAO.log.error((Object) "\u6574\u7406\u975e\u88c5\u5907\u7269\u54c1\u5305 error:", (Throwable) ex);
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

    public static void changeEquipmentLocation(final int _equipmentInsID, final byte _containerType, final int _newGridIndex) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE player_carry_equipment SET container_type=?,package_index=? WHERE instance_id=? LIMIT 1");
            pstm.setInt(1, _containerType);
            pstm.setShort(2, (short) _newGridIndex);
            pstm.setInt(3, _equipmentInsID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            LogWriter.error(null, ex);
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

    public static boolean changeEquipmentOwner(final int _newMasterUserID, final int _equipmentInsID, final int _newGridIndex) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE player_carry_equipment SET user_id=?,package_index=? WHERE instance_id=? LIMIT 1");
            pstm.setInt(1, _newMasterUserID);
            pstm.setShort(2, (short) _newGridIndex);
            pstm.setInt(3, _equipmentInsID);
            pstm.executeUpdate();
            pstm.close();
            pstm = null;
            pstm = conn.prepareStatement("UPDATE equipment_instance SET owner_user_id=? WHERE instance_id=? LIMIT 1");
            pstm.setInt(1, _newMasterUserID);
            pstm.setInt(2, _equipmentInsID);
            pstm.executeUpdate();
            return true;
        } catch (Exception ex) {
            LogWriter.error(null, ex);
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
        return false;
    }

    public static void updateEquipmentDurability(final ArrayList<EquipmentInstance> _equipmentInsList) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement("UPDATE equipment_instance SET current_durability=? WHERE instance_id=? LIMIT 1");
            for (final EquipmentInstance ei : _equipmentInsList) {
                pstm.setInt(1, ei.getCurrentDurabilityPoint());
                pstm.setInt(2, ei.getInstanceID());
                pstm.addBatch();
            }
            pstm.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            LogWriter.error(null, ex);
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

    public static void removeEquipmentSeal(final EquipmentInstance _equipmentIns) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE equipment_instance SET be_sealed=0 WHERE instance_id=? LIMIT 1");
            pstm.setInt(1, _equipmentIns.getInstanceID());
            pstm.executeUpdate();
        } catch (Exception ex) {
            LogWriter.error(null, ex);
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

    public static void bindEquipment(final EquipmentInstance _equipmentIns) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE equipment_instance SET bind = 1 WHERE instance_id=? LIMIT 1");
            pstm.setInt(1, _equipmentIns.getInstanceID());
            pstm.executeUpdate();
        } catch (Exception ex) {
            LogWriter.error(null, ex);
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

    public static void updateWeaponBloodyEnhance(final int _weaponInstanceID, final String _bloodyEnhanceDesc) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE equipment_instance SET bloody_enhance_desc=? WHERE instance_id=? LIMIT 1");
            pstm.setString(1, _bloodyEnhanceDesc);
            pstm.setInt(2, _weaponInstanceID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            LogWriter.error(null, ex);
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

    public static void updateEquipmentEnhance(final int _equipmentInstanceID, final String _enhanceDesc) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE equipment_instance SET generic_enhance_desc=? WHERE instance_id=? LIMIT 1");
            pstm.setString(1, _enhanceDesc);
            pstm.setInt(2, _equipmentInstanceID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            LogWriter.error(null, ex);
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

    public static void clearUpEquipmentList(final EquipmentInstance[] _equipmentList) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement("UPDATE player_carry_equipment SET package_index=? WHERE instance_id=? LIMIT 1");
            for (short i = 0; i < _equipmentList.length; ++i) {
                if (_equipmentList[i] != null) {
                    pstm.setShort(1, i);
                    pstm.setInt(2, _equipmentList[i].getInstanceID());
                    pstm.addBatch();
                }
            }
            pstm.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            LogWriter.error(null, ex);
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

    public static void load() {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT MAX(id) as max_goods_id from player_single_goods");
            resultSet = pstm.executeQuery();
            if (resultSet.next()) {
                int maxGuildID = resultSet.getInt("max_goods_id");
                if (maxGuildID > 0) {
                    GoodsServiceImpl.getInstance().setUseableSpecailID(++maxGuildID);
                }
            }
        } catch (Exception e) {
            GoodsDAO.log.error((Object) ": ", (Throwable) e);
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstm != null) {
                    pstm.close();
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
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstm != null) {
                    pstm.close();
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
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    public static void addSingleGoods(final int _userID, final byte _singleGoodsType, final int _goodsID, final int _goodsNums, final int _packageGridIndex) {
        Connection conn = null;
        PreparedStatement pstm = null;
        PreparedStatement pstmInsert = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO player_single_goods(user_id,goods_type,goods_id,goods_number,package_index,id) VALUES (?,?,?,?,?,?)");
            pstm.setInt(1, _userID);
            pstm.setShort(2, _singleGoodsType);
            pstm.setInt(3, _goodsID);
            pstm.setInt(4, _goodsNums);
            pstm.setInt(5, _packageGridIndex);
            int id = GoodsServiceImpl.getInstance().getUseableSpecailID();
            pstm.setInt(6, id);
            pstm.executeUpdate();
            pstm.close();
            pstm = null;
            if (_singleGoodsType == 4) {
                SpecialGoods specialGoods = (SpecialGoods) GoodsContents.getGoods(_goodsID);
                if (specialGoods.getType() == ESpecialGoodsType.BIG_TONIC) {
                    BigTonicBall ball = (BigTonicBall) specialGoods;
                    String sql = "select id from player_single_goods where user_id=? and goods_id=? and package_index=?";
                    pstm = conn.prepareStatement(sql);
                    pstm.setInt(1, _userID);
                    pstm.setInt(2, _goodsID);
                    pstm.setInt(3, _packageGridIndex);
                    ResultSet set = pstm.executeQuery();
                    while (set.next()) {
                        sql = "insert into big_tonic_ball (single_goods_id,tonic_id,surplus_point,type)  VALUES (?,?,?,?)";
                        pstmInsert = conn.prepareStatement(sql);
                        pstmInsert.setInt(1, id);
                        pstmInsert.setInt(2, _goodsID);
                        pstmInsert.setInt(3, ball.surplusPoint);
                        pstmInsert.setInt(4, ball.isActivate);
                        pstmInsert.executeUpdate();
                    }
                } else if (specialGoods.getType() == ESpecialGoodsType.PET_PER) {
                    PetPerCard card = (PetPerCard) specialGoods;
                    String sql = "select id from player_single_goods where user_id=? and goods_id=? and package_index=?";
                    pstm = conn.prepareStatement(sql);
                    pstm.setInt(1, _userID);
                    pstm.setInt(2, _goodsID);
                    pstm.setInt(3, _packageGridIndex);
                    ResultSet set = pstm.executeQuery();
                    while (set.next()) {
                        sql = "insert into pet_per_card (single_goods_id,card_id,surplus_point)  VALUES (?,?,?)";
                        pstmInsert = conn.prepareStatement(sql);
                        pstmInsert.setInt(1, id);
                        pstmInsert.setInt(2, _goodsID);
                        pstmInsert.setInt(3, card.surplusPoint);
                        pstmInsert.executeUpdate();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                    pstmInsert.close();
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
                pstmInsert.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex3) {
        }
    }

    public static void buildEquipment2Bag(final int _userID, final EquipmentInstance _ei, final int _gridIndex) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO equipment_instance(instance_id,equipment_id,creator_user_id,owner_user_id,current_durability,be_sealed,bind,owner_type) VALUES (?,?,?,?,?,?,?,?)");
            pstm.setInt(1, _ei.getInstanceID());
            pstm.setInt(2, _ei.getArchetype().getID());
            pstm.setInt(3, _ei.getCreatorUserID());
            pstm.setInt(4, _ei.getOwnerUserID());
            pstm.setInt(5, _ei.getCurrentDurabilityPoint());
            pstm.setByte(6, (byte) (_ei.existSeal() ? 1 : 0));
            pstm.setByte(7, (byte) (_ei.isBind() ? 1 : 0));
            pstm.setShort(8, _ei.getOwnerType());
            if (pstm.executeUpdate() == 1) {
                pstm.close();
                pstm = null;
                pstm = conn.prepareStatement("INSERT INTO player_carry_equipment(instance_id,user_id,package_index,container_type) VALUES (?,?,?,?)");
                pstm.setInt(1, _ei.getInstanceID());
                pstm.setInt(2, _userID);
                pstm.setShort(3, (short) _gridIndex);
                if (_ei.getOwnerType() == 1) {
                    pstm.setShort(4, (short) 1);
                } else if (_ei.getOwnerType() == 2) {
                    pstm.setShort(4, (short) 3);
                }
                pstm.executeUpdate();
            }
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

    public static void buildEquipmentInstance(final EquipmentInstance _ei) {
        Connection conn = null;
        PreparedStatement pstm = null;
        GoodsDAO.log.debug((Object) ("\u5411\u5b9e\u4f8b\u8868\u4e2d\u63d2\u5165\u88c5\u5907\u5b9e\u4f8b id=" + _ei.getInstanceID() + "  ownertype=" + _ei.getOwnerType()));
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO equipment_instance(instance_id,equipment_id,creator_user_id,owner_user_id,current_durability,be_sealed,bind,owner_type) VALUES (?,?,?,?,?,?,?,?)");
            pstm.setInt(1, _ei.getInstanceID());
            pstm.setInt(2, _ei.getArchetype().getID());
            pstm.setInt(3, _ei.getCreatorUserID());
            pstm.setInt(4, _ei.getOwnerUserID());
            pstm.setInt(5, _ei.getCurrentDurabilityPoint());
            pstm.setByte(6, (byte) (_ei.existSeal() ? 1 : 0));
            pstm.setByte(7, (byte) (_ei.isBind() ? 1 : 0));
            pstm.setShort(8, _ei.getOwnerType());
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

    public static void buildEquipment2Body(final int _userID, final EquipmentInstance _ei) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO equipment_instance(instance_id,equipment_id,creator_user_id,owner_user_id,current_durability,be_sealed,bind,owner_type) VALUES (?,?,?,?,?,?,?,?)");
            pstm.setInt(1, _ei.getInstanceID());
            pstm.setInt(2, _ei.getArchetype().getID());
            pstm.setInt(3, _ei.getCreatorUserID());
            pstm.setInt(4, _ei.getOwnerUserID());
            pstm.setInt(5, _ei.getCurrentDurabilityPoint());
            pstm.setByte(6, (byte) (_ei.existSeal() ? 1 : 0));
            pstm.setByte(7, (byte) (_ei.isBind() ? 1 : 0));
            pstm.setShort(8, _ei.getOwnerType());
            if (pstm.executeUpdate() == 1) {
                pstm.close();
                pstm = null;
                pstm = conn.prepareStatement("INSERT INTO player_carry_equipment(instance_id,user_id,container_type) VALUES (?,?,?)");
                pstm.setInt(1, _ei.getInstanceID());
                pstm.setInt(2, _userID);
                pstm.setShort(3, (short) 2);
                pstm.executeUpdate();
            }
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

    public static void addEquipment2Bag(final int _userID, final EquipmentInstance _ei, final int _packageIndex) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO player_carry_equipment(instance_id,user_id,package_index,container_type) VALUES (?,?,?,?)");
            pstm.setInt(1, _ei.getInstanceID());
            pstm.setInt(2, _userID);
            pstm.setShort(3, (short) _packageIndex);
            if (_ei.getOwnerType() == 1) {
                pstm.setShort(4, (short) 1);
            } else if (_ei.getOwnerType() == 2) {
                pstm.setShort(4, (short) 3);
            }
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

    public static void updatePetPer(final int _userID, final int _bagGridIndex, final int _goodsID, final int _surplusPoint) {
        String sql = "select id from player_single_goods where user_id=? and goods_id=? and package_index=?";
        Connection conn = null;
        PreparedStatement pstm = null;
        PreparedStatement pstmCard = null;
        ResultSet resultSet = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _goodsID);
            pstm.setInt(3, _bagGridIndex);
            resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                sql = "update pet_per_card set surplus_point=? where single_goods_id=?";
                pstmCard = conn.prepareStatement(sql);
                pstmCard.setInt(1, _surplusPoint);
                pstmCard.setInt(2, id);
                pstmCard.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                    pstmCard.close();
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
                pstmCard.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex3) {
        }
    }

    public static int selectTonic(final int _userID, final int _bagGridIndex, final int _goodsID) {
        String sql = "select id from player_single_goods where user_id=? and goods_id=? and package_index=?";
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        int tonicID = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _goodsID);
            pstm.setInt(3, _bagGridIndex);
            resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                tonicID = resultSet.getInt("id");
            }
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
        return tonicID;
    }

    public static void updateTonic(final int _userID, final int _bagGridIndex, final int _goodsID, final int _surplusPoint, final int _type) {
        String sql = "select id from player_single_goods where user_id=? and goods_id=? and package_index=?";
        Connection conn = null;
        PreparedStatement pstm = null;
        PreparedStatement pstmTonic = null;
        ResultSet resultSet = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _goodsID);
            pstm.setInt(3, _bagGridIndex);
            resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                sql = "update big_tonic_ball set surplus_point=?,type=? where single_goods_id=?";
                pstmTonic = conn.prepareStatement(sql);
                pstmTonic.setInt(1, _surplusPoint);
                pstmTonic.setInt(2, _type);
                pstmTonic.setInt(3, id);
                pstmTonic.executeUpdate();
            }
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

    public static boolean updateGridSingleGoodsNumberOfBag(final int _userID, final int _goodsID, final int _number, final short _bagGridIndex) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE player_single_goods SET goods_number=? WHERE user_id=? AND goods_id=? AND package_index=? LIMIT 1");
            pstm.setInt(1, _number);
            pstm.setInt(2, _userID);
            pstm.setInt(3, _goodsID);
            pstm.setShort(4, _bagGridIndex);
            pstm.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
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
        return true;
    }

    public static void loadPlayerWearGoods(final HeroPlayer _player) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT player_carry_equipment.instance_id,equipment_id,creator_user_id,current_durability,generic_enhance_desc,bloody_enhance_desc,be_sealed,bind,container_type,package_index FROM equipment_instance JOIN player_carry_equipment ON user_id=? AND player_carry_equipment.instance_id=equipment_instance.instance_id LIMIT 100");
            pstm.setInt(1, _player.getUserID());
            resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                int instanceID = resultSet.getInt("instance_id");
                int equipmentID = resultSet.getInt("equipment_id");
                int creatorUserID = resultSet.getInt("creator_user_id");
                int currentDurabilityPoint = resultSet.getInt("current_durability");
                short containerType = resultSet.getShort("container_type");
                short packageIndex = resultSet.getShort("package_index");
                String genericEnhanceDesc = resultSet.getString("generic_enhance_desc");
                String bloodyEnhanceDesc = resultSet.getString("bloody_enhance_desc");
                byte existSeal = resultSet.getByte("be_sealed");
                byte isBind = resultSet.getByte("bind");
                EquipmentInstance ei = EquipmentFactory.getInstance().buildFromDB(creatorUserID, _player.getUserID(), instanceID, equipmentID, currentDurabilityPoint, existSeal, isBind);
                if (ei != null) {
                    if (ei.getOwnerType() == 1) {
                        EnhanceService.getInstance().parseEnhanceDesc(ei, genericEnhanceDesc, bloodyEnhanceDesc);
                    }
                    if (2 != containerType) {
                        continue;
                    }
                    _player.getBodyWear().wear(ei);
                } else {
                    GoodsDAO.log.debug((Object) ("--loadPlayerWearGoods-\u7528\u6237ID\u4e3a:" + _player.getUserID() + "\u7684\u7528\u6237---"));
                    GoodsDAO.log.debug((Object) ("--loadPlayerWearGoods-\u52a0\u8f7dinstanceID\u4e3a:" + instanceID + "\u7684\u65f6\u5019\u83b7\u5f97\u7684\u662fNULL---"));
                }
            }
        } catch (Exception e) {
            GoodsDAO.log.error((Object) "\u53ea\u52a0\u8f7d\u73a9\u5bb6\u5df2\u88c5\u5907\u4e0a\u7684\u88c5\u5907\u7269\u54c1\u65f6 error : ", (Throwable) e);
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstm != null) {
                    pstm.close();
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
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstm != null) {
                    pstm.close();
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
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    public static void loadPlayerEquipment(final HeroPlayer _player) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        PreparedStatement pstmSpecial = null;
        ResultSet specialSet = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT player_carry_equipment.instance_id,equipment_id,creator_user_id,current_durability,generic_enhance_desc,bloody_enhance_desc,be_sealed,bind,container_type,package_index FROM equipment_instance JOIN player_carry_equipment ON user_id=? AND player_carry_equipment.instance_id=equipment_instance.instance_id LIMIT 100");
            pstm.setInt(1, _player.getUserID());
            resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                int instanceID = resultSet.getInt("instance_id");
                int equipmentID = resultSet.getInt("equipment_id");
                int creatorUserID = resultSet.getInt("creator_user_id");
                int currentDurabilityPoint = resultSet.getInt("current_durability");
                short containerType = resultSet.getShort("container_type");
                short packageIndex = resultSet.getShort("package_index");
                String genericEnhanceDesc = resultSet.getString("generic_enhance_desc");
                String bloodyEnhanceDesc = resultSet.getString("bloody_enhance_desc");
                byte existSeal = resultSet.getByte("be_sealed");
                byte isBind = resultSet.getByte("bind");
                EquipmentInstance ei = EquipmentFactory.getInstance().buildFromDB(creatorUserID, _player.getUserID(), instanceID, equipmentID, currentDurabilityPoint, existSeal, isBind);
                if (ei != null) {
                    if (ei.getOwnerType() == 1) {
                        EnhanceService.getInstance().parseEnhanceDesc(ei, genericEnhanceDesc, bloodyEnhanceDesc);
                    }
                    if (2 == containerType) {
                        _player.getBodyWear().wear(ei);
                    } else if (3 == containerType) {
                        _player.getInventory().getPetEquipmentBag().add(packageIndex, ei);
                    } else {
                        if (4 == containerType) {
                            continue;
                        }
                        _player.getInventory().getEquipmentBag().add(packageIndex, ei);
                    }
                } else {
                    GoodsDAO.log.debug((Object) ("---\u53ea\u52a0\u8f7d\u73a9\u5bb6\u7684\u88c5\u5907 \u7528\u6237ID\u4e3a:" + _player.getUserID() + "\u7684\u7528\u6237---"));
                    GoodsDAO.log.debug((Object) ("---\u53ea\u52a0\u8f7d\u73a9\u5bb6\u7684\u88c5\u5907 \u52a0\u8f7dinstanceID\u4e3a:" + instanceID + "\u7684\u65f6\u5019\u83b7\u5f97\u7684\u662fNULL---"));
                }
            }
            resultSet.close();
            resultSet = null;
            pstm.close();
            pstm = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            LogWriter.error(null, ex);
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
                if (specialSet != null) {
                    specialSet.close();
                }
                if (pstmSpecial != null) {
                    pstmSpecial.close();
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
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
                if (specialSet != null) {
                    specialSet.close();
                }
                if (pstmSpecial != null) {
                    pstmSpecial.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (pstm != null) {
                pstm.close();
            }
            if (specialSet != null) {
                specialSet.close();
            }
            if (pstmSpecial != null) {
                pstmSpecial.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadPlayerGoods(final HeroPlayer _player) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        PreparedStatement pstmSpecial = null;
        ResultSet specialSet = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT player_carry_equipment.instance_id,equipment_id,creator_user_id,current_durability,generic_enhance_desc,bloody_enhance_desc,be_sealed,bind,container_type,package_index FROM equipment_instance JOIN player_carry_equipment ON user_id=? AND player_carry_equipment.instance_id=equipment_instance.instance_id LIMIT 100");
            pstm.setInt(1, _player.getUserID());
            resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                int instanceID = resultSet.getInt("instance_id");
                int equipmentID = resultSet.getInt("equipment_id");
                int creatorUserID = resultSet.getInt("creator_user_id");
                int currentDurabilityPoint = resultSet.getInt("current_durability");
                short containerType = resultSet.getShort("container_type");
                short packageIndex = resultSet.getShort("package_index");
                String genericEnhanceDesc = resultSet.getString("generic_enhance_desc");
                String bloodyEnhanceDesc = resultSet.getString("bloody_enhance_desc");
                byte existSeal = resultSet.getByte("be_sealed");
                byte isBind = resultSet.getByte("bind");
                EquipmentInstance ei = EquipmentFactory.getInstance().buildFromDB(creatorUserID, _player.getUserID(), instanceID, equipmentID, currentDurabilityPoint, existSeal, isBind);
                if (ei != null) {
                    if (ei.getOwnerType() == 1) {
                        EnhanceService.getInstance().parseEnhanceDesc(ei, genericEnhanceDesc, bloodyEnhanceDesc);
                    }
                    if (2 == containerType) {
                        _player.getBodyWear().wear(ei);
                    } else if (3 == containerType) {
                        _player.getInventory().getPetEquipmentBag().add(packageIndex, ei);
                    } else {
                        if (4 == containerType) {
                            continue;
                        }
                        _player.getInventory().getEquipmentBag().add(packageIndex, ei);
                    }
                } else {
                    GoodsDAO.log.debug((Object) ("---\u7528\u6237ID\u4e3a:" + _player.getUserID() + "\u7684\u7528\u6237---"));
                    GoodsDAO.log.debug((Object) ("---\u52a0\u8f7dinstanceID\u4e3a:" + instanceID + "\u7684\u65f6\u5019\u83b7\u5f97\u7684\u662fNULL---"));
                }
            }
            resultSet.close();
            resultSet = null;
            pstm.close();
            pstm = null;
            pstm = conn.prepareStatement("SELECT id,goods_id,goods_type,goods_number,package_index from player_single_goods where user_id=? LIMIT 280");
            pstm.setInt(1, _player.getUserID());
            resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                int goodsID = resultSet.getInt("goods_id");
                short goodsType = resultSet.getShort("goods_type");
                int number = resultSet.getInt("goods_number");
                short packageIndex = resultSet.getShort("package_index");
                switch (goodsType) {
                    case 4: {
                        _player.getInventory().getSpecialGoodsBag().load(goodsID, number, packageIndex);
                        SpecialGoods specialGoods = (SpecialGoods) GoodsContents.getGoods(goodsID);
                        if (specialGoods instanceof BigTonicBall) {
                            int specialID = resultSet.getInt("id");
                            pstmSpecial = conn.prepareStatement("SELECT tonic_id,surplus_point,type  from big_tonic_ball  where single_goods_id=? LIMIT 1");
                            pstmSpecial.setInt(1, specialID);
                            specialSet = pstmSpecial.executeQuery();
                            while (specialSet.next()) {
                                int surplus = specialSet.getInt("surplus_point");
                                int type = specialSet.getInt("type");
                                _player.getInventory().getSpecialGoodsBag().loadBigTonicBall(goodsID, number, packageIndex, surplus, type, specialGoods);
                            }
                            continue;
                        }
                        if (specialGoods instanceof PetPerCard) {
                            int specialID = resultSet.getInt("id");
                            pstmSpecial = conn.prepareStatement("SELECT card_id,surplus_point  from pet_per_card  where single_goods_id=? LIMIT 1");
                            pstmSpecial.setInt(1, specialID);
                            specialSet = pstmSpecial.executeQuery();
                            while (specialSet.next()) {
                                int surplus = specialSet.getInt("surplus_point");
                                _player.getInventory().getSpecialGoodsBag().loadBigPetPerCard(goodsID, number, packageIndex, surplus, specialGoods);
                            }
                            continue;
                        }
                        continue;
                    }
                    default: {
                        continue;
                    }
                    case 2: {
                        _player.getInventory().getMaterialBag().load(goodsID, number, packageIndex);
                        continue;
                    }
                    case 1: {
                        _player.getInventory().getMedicamentBag().load(goodsID, number, packageIndex);
                        continue;
                    }
                    case 3: {
                        _player.getInventory().getTaskToolBag().load(goodsID, number, packageIndex);
                        continue;
                    }
                    case 5: {
                        _player.getInventory().getPetGoodsBag().load(goodsID, number, packageIndex);
                        continue;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LogWriter.error(null, ex);
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
                if (specialSet != null) {
                    specialSet.close();
                }
                if (pstmSpecial != null) {
                    pstmSpecial.close();
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
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
                if (specialSet != null) {
                    specialSet.close();
                }
                if (pstmSpecial != null) {
                    pstmSpecial.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (pstm != null) {
                pstm.close();
            }
            if (specialSet != null) {
                specialSet.close();
            }
            if (pstmSpecial != null) {
                pstmSpecial.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getStorageSpecialGoods(final HeroPlayer _player, final int _goodsID, final int _specialID, final int number, final int packageIndex) {
        Connection conn = null;
        ResultSet resultSet = null;
        PreparedStatement pstmSpecial = null;
        try {
            SpecialGoods specialGoods = (SpecialGoods) GoodsContents.getGoods(_goodsID);
            conn = DBServiceImpl.getInstance().getConnection();
            if (specialGoods instanceof BigTonicBall) {
                pstmSpecial = conn.prepareStatement("SELECT tonic_id,surplus_point,type  from big_tonic_ball  where single_goods_id=? LIMIT 1");
                pstmSpecial.setInt(1, _specialID);
                resultSet = pstmSpecial.executeQuery();
                while (resultSet.next()) {
                    int surplus = resultSet.getInt("surplus_point");
                    int type = resultSet.getInt("type");
                    _player.getInventory().getSpecialGoodsBag().loadBigTonicBall(_goodsID, number, packageIndex, surplus, type, specialGoods);
                    pstmSpecial = conn.prepareStatement("INSERT INTO player_single_goods(user_id,goods_type,goods_id,goods_number,package_index,id) VALUES (?,?,?,?,?,?)");
                    pstmSpecial.setInt(1, _player.getUserID());
                    pstmSpecial.setInt(2, specialGoods.getSingleGoodsType());
                    pstmSpecial.setInt(3, _goodsID);
                    pstmSpecial.setInt(4, number);
                    pstmSpecial.setInt(5, packageIndex);
                    pstmSpecial.setInt(6, _specialID);
                    pstmSpecial.executeUpdate();
                }
            } else if (specialGoods instanceof PetPerCard) {
                pstmSpecial = conn.prepareStatement("SELECT card_id,surplus_point  from pet_per_card  where single_goods_id=? LIMIT 1");
                pstmSpecial.setInt(1, _specialID);
                resultSet = pstmSpecial.executeQuery();
                while (resultSet.next()) {
                    int surplus = resultSet.getInt("surplus_point");
                    _player.getInventory().getSpecialGoodsBag().loadBigPetPerCard(_goodsID, number, packageIndex, surplus, specialGoods);
                    pstmSpecial = conn.prepareStatement("INSERT INTO player_single_goods(user_id,goods_type,goods_id,goods_number,package_index,id) VALUES (?,?,?,?,?,?)");
                    pstmSpecial.setInt(1, _player.getUserID());
                    pstmSpecial.setInt(2, specialGoods.getSingleGoodsType());
                    pstmSpecial.setInt(3, _goodsID);
                    pstmSpecial.setInt(4, number);
                    pstmSpecial.setInt(5, packageIndex);
                    pstmSpecial.setInt(6, _specialID);
                    pstmSpecial.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmSpecial != null) {
                    pstmSpecial.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (pstmSpecial != null) {
                pstmSpecial.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex2) {
        }
    }

    public static EquipmentInstance getEquipmentInstanceFromDB(final int _instanceID) {
        EquipmentInstance instance = null;
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT equipment_id,creator_user_id,owner_user_id,current_durability,generic_enhance_desc,bloody_enhance_desc,be_sealed,bind FROM equipment_instance WHERE instance_id = ? LIMIT 1");
            pstm.setInt(1, _instanceID);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                int equipmentID = rs.getInt("equipment_id");
                int creatorUserID = rs.getInt("creator_user_id");
                int ownerUserID = rs.getInt("owner_user_id");
                int currentDurabilityPoint = rs.getInt("current_durability");
                String genericEnhanceDesc = rs.getString("generic_enhance_desc");
                String bloodyEnhanceDesc = rs.getString("bloody_enhance_desc");
                byte existSeal = rs.getByte("be_sealed");
                byte isBind = rs.getByte("bind");
                instance = EquipmentFactory.getInstance().buildFromDB(creatorUserID, ownerUserID, _instanceID, equipmentID, currentDurabilityPoint, existSeal, isBind);
                EnhanceService.getInstance().parseEnhanceDesc(instance, genericEnhanceDesc, bloodyEnhanceDesc);
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
        return instance;
    }

    public static void updatePlayerBagSize(final HeroPlayer _player) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE player SET bag_size=? WHERE user_id=? LIMIT 1");
            Inventory inventory = _player.getInventory();
            String bagSizeDesc = new StringBuffer().append(inventory.getEquipmentBag().getSize()).append("&").append(inventory.getMedicamentBag().getSize()).append("&").append(inventory.getMaterialBag().getSize()).append("&").append(inventory.getSpecialGoodsBag().getSize()).append("&").append(inventory.getPetEquipmentBag().getSize()).append("&").append(inventory.getPetContainer().getSize()).append("&").append(inventory.getPetGoodsBag().getSize()).toString();
            pstm.setString(1, bagSizeDesc);
            pstm.setInt(2, _player.getUserID());
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

    public static void updateHome(final int _playerUserID, final short _homeID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE player SET home_id=? WHERE user_id=? LIMIT 1");
            ps.setShort(1, _homeID);
            ps.setInt(2, _playerUserID);
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
}
