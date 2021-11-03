// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system.storage;

import hero.item.EquipmentInstance;
import hero.item.enhance.EnhanceService;
import hero.item.service.EquipmentFactory;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import yoyo.service.tools.database.DBServiceImpl;

public class WarehouseDB {

    private static final String SEL_LVL_SQL = "SELECT lvl FROM stroage_lvl WHERE nickname = ?";
    private static final String INSERT_LVL_SQL = "INSERT INTO stroage_lvl SET lvl = 0,nickname = ?";
    private static final String UP_LVL_SQL = "UPDATE stroage_lvl SET lvl = ? WHERE nickname = ?";
    private static final String INSERT_GOODS_SQL = "INSERT INTO stroage SET nickname = ?,index_id=?,goods_id=?,goods_num=?,goods_type=?,single_goods_id=?";
    private static final String UPDATE_TINOC_SQL = "UPDATE big_tonic_ball SET TYPE = ? WHERE single_goods_id=?";
    private static final String SEL_EQUIPMENT_SQL = "SELECT s.index_id,s.goods_id,s.goods_num,s.goods_type,e.equipment_id,e.creator_user_id,e.owner_user_id,e.current_durability,e.generic_enhance_desc,e.bloody_enhance_desc,e.be_sealed,e.bind FROM stroage s,equipment_instance e WHERE s.nickname = ? AND s.goods_id = e.instance_id AND s.goods_type = 0";
    private static final String SEL_GOODS_SQL = "SELECT index_id,goods_id,goods_num,goods_type,single_goods_id FROM stroage WHERE nickname = ? AND goods_type = 1";
    private static final String DEL_SQL = "DELETE FROM stroage WHERE nickname = ? AND index_id = ?";

    protected static byte selLvl(final String _nickname) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        byte lvl = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT lvl FROM stroage_lvl WHERE nickname = ?");
            pstm.setString(1, _nickname);
            rs = pstm.executeQuery();
            if (rs.next()) {
                lvl = rs.getByte(1);
            } else {
                insertLvl(_nickname);
            }
            return lvl;
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
        return lvl;
    }

    protected static void insertLvl(final String _nickname) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO stroage_lvl SET lvl = 0,nickname = ?");
            pstm.setString(1, _nickname);
            pstm.execute();
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

    protected static void updateLvl(final byte _lvl, final String _nickname) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE stroage_lvl SET lvl = ? WHERE nickname = ?");
            pstm.setByte(1, _lvl);
            pstm.setString(2, _nickname);
            pstm.execute();
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

    protected static void insertGoods(final String _nickname, final byte _index, final int _goodsID, final short _num, final short _goodsType, final int _singleGoodsID, final boolean _isAutoBall) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO stroage SET nickname = ?,index_id=?,goods_id=?,goods_num=?,goods_type=?,single_goods_id=?");
            pstm.setString(1, _nickname);
            pstm.setByte(2, _index);
            pstm.setInt(3, _goodsID);
            pstm.setShort(4, _num);
            pstm.setShort(5, _goodsType);
            pstm.setInt(6, _singleGoodsID);
            pstm.execute();
            if (_isAutoBall) {
                pstm = conn.prepareStatement("UPDATE big_tonic_ball SET TYPE = ? WHERE single_goods_id=?");
                pstm.setInt(1, 0);
                pstm.setInt(2, _singleGoodsID);
                pstm.execute();
            }
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

    protected static void selGoods(final String _nickname, final Warehouse _warehouse) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT index_id,goods_id,goods_num,goods_type,single_goods_id FROM stroage WHERE nickname = ? AND goods_type = 1");
            pstm.setString(1, _nickname);
            rs = pstm.executeQuery();
            while (rs.next()) {
                byte index = rs.getByte(1);
                int goodsID = rs.getInt(2);
                short num = rs.getShort(3);
                short _goodsType = rs.getShort(4);
                int single_goods_id = rs.getInt(5);
                EquipmentInstance _instance = null;
                _warehouse.addWarehouseGoods(index, goodsID, num, _goodsType, _instance, single_goods_id);
            }
            rs.close();
            rs = null;
            pstm.close();
            pstm = null;
            pstm = conn.prepareStatement("SELECT s.index_id,s.goods_id,s.goods_num,s.goods_type,e.equipment_id,e.creator_user_id,e.owner_user_id,e.current_durability,e.generic_enhance_desc,e.bloody_enhance_desc,e.be_sealed,e.bind FROM stroage s,equipment_instance e WHERE s.nickname = ? AND s.goods_id = e.instance_id AND s.goods_type = 0");
            pstm.setString(1, _nickname);
            rs = pstm.executeQuery();
            while (rs.next()) {
                byte index = rs.getByte(1);
                int goodsID = rs.getInt(2);
                short num = rs.getShort(3);
                short _goodsType = rs.getShort(4);
                int _equipmentID = rs.getInt(5);
                int _creatorUserID = rs.getInt(6);
                int _ownerUserID = rs.getInt(7);
                int _currentDurabilityPoint = rs.getInt(8);
                String genericEnhanceDesc = rs.getString(9);
                String bloodyEnhanceDesc = rs.getString(10);
                byte existSeal = rs.getByte(11);
                byte isBind = rs.getByte(12);
                EquipmentInstance instance = EquipmentFactory.getInstance().buildFromDB(_creatorUserID, _ownerUserID, goodsID, _equipmentID, _currentDurabilityPoint, existSeal, isBind);
                EnhanceService.getInstance().parseEnhanceDesc(instance, genericEnhanceDesc, bloodyEnhanceDesc);
                _warehouse.addWarehouseGoods(index, goodsID, num, _goodsType, instance, 0);
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
    }

    public static void delGoods(final String _nickname, final byte _index) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM stroage WHERE nickname = ? AND index_id = ?");
            pstm.setString(1, _nickname);
            pstm.setByte(2, _index);
            pstm.execute();
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
