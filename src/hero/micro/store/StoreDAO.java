// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.store;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import hero.share.service.LogWriter;
import hero.item.EquipmentInstance;
import hero.item.enhance.EnhanceService;
import hero.item.service.EquipmentFactory;
import hero.item.detail.EGoodsType;
import yoyo.service.tools.database.DBServiceImpl;
import org.apache.log4j.Logger;

public class StoreDAO {

    private static Logger log;
    private static final String SELECT_STORE_SQL = "SELECT * FROM store_goods left join equipment_instance ON store_goods.goods_id=equipment_instance.instance_id WHERE store_goods.user_id = ? LIMIT 16";
    private static final String INSERT_STORE_SQL = "INSERT INTO store_goods VALUES(?,?,?,?,?,?)";
    private static final String DELETE_STORE_SQL = "DELETE FROM store_goods WHERE user_id = ? AND grid_index = ?";
    private static final String UPDATE_PRICE_SQL = "UPDATE store_goods SET sale_price = ? WHERE user_id = ? AND grid_index = ? AND goods_id = ?";

    static {
        StoreDAO.log = Logger.getLogger((Class) StoreDAO.class);
    }

    public static PersionalStore loadStore(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM store_goods left join equipment_instance ON store_goods.goods_id=equipment_instance.instance_id WHERE store_goods.user_id = ? LIMIT 16");
            pstm.setInt(1, _userID);
            set = pstm.executeQuery();
            PersionalStore store = null;
            while (set.next()) {
                if (store == null) {
                    store = new PersionalStore();
                }
                byte gridIndex = set.getByte(2);
                byte goodsType = set.getByte(3);
                int goodsID = set.getInt(4);
                short number = set.getShort(5);
                int salePrice = set.getInt(6);
                StoreDAO.log.debug((Object) ("load store gridindex=" + gridIndex + ",goodstype=" + goodsType + ",goodsid=" + goodsID + ",number=" + number + ",saleprice=" + salePrice));
                if (EGoodsType.EQUIPMENT.value() == goodsType) {
                    int equipmentID = set.getInt(8);
                    int creatorUserID = set.getInt(9);
                    int ownerUserID = set.getInt(10);
                    int currentDurabilityPoint = set.getInt(11);
                    String genericEnhanceDesc = set.getString(12);
                    String bloodyEnhanceDesc = set.getString(13);
                    byte existSeal = set.getByte(14);
                    byte isBind = set.getByte(15);
                    EquipmentInstance instance = EquipmentFactory.getInstance().buildFromDB(creatorUserID, ownerUserID, goodsID, equipmentID, currentDurabilityPoint, existSeal, isBind);
                    if (instance.getOwnerType() == 1) {
                        EnhanceService.getInstance().parseEnhanceDesc(instance, genericEnhanceDesc, bloodyEnhanceDesc);
                    }
                    store.add(goodsType, gridIndex, 0, (short) 0, instance, salePrice);
                } else {
                    store.add(goodsType, gridIndex, goodsID, number, null, salePrice);
                }
            }
            return store;
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
        return null;
    }

    public static boolean insertGoods2Store(final int _userID, final int[][] _newGoodsDataList) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement("INSERT INTO store_goods VALUES(?,?,?,?,?,?)");
            for (final int[] data : _newGoodsDataList) {
                pstm.setInt(1, _userID);
                pstm.setByte(2, (byte) data[4]);
                pstm.setByte(3, (byte) data[0]);
                pstm.setInt(4, data[2]);
                pstm.setShort(5, (short) data[3]);
                pstm.setInt(6, data[5]);
                pstm.addBatch();
            }
            pstm.executeBatch();
            conn.commit();
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
        return true;
    }

    public static boolean removeFromStore(final int _userID, final byte _gridIndex) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM store_goods WHERE user_id = ? AND grid_index = ?");
            pstm.setInt(1, _userID);
            pstm.setShort(2, _gridIndex);
            if (pstm.executeUpdate() == 1) {
                return true;
            }
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

    public static boolean changePrice(final int _userID, final byte _gridIndex, final int _goodsID, final int _newPrice) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE store_goods SET sale_price = ? WHERE user_id = ? AND grid_index = ? AND goods_id = ?");
            pstm.setInt(1, _newPrice);
            pstm.setInt(2, _userID);
            pstm.setByte(3, _gridIndex);
            pstm.setInt(4, _goodsID);
            if (pstm.executeUpdate() == 1) {
                return true;
            }
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
}
