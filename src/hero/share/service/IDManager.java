// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import yoyo.service.tools.database.DBServiceImpl;

public class IDManager {

    private static final int MIN_ID = 1000;
    private static boolean INITED;
    private static int OBJECT_ID;
    private static int SESSION_ID;
    private static int MONSTER_LEGACY_BOX_ID;
    private static int EQUIPMENT_INS_ID;
    private static int GROUP_ID;
    private static final int MAX_INT = Integer.MAX_VALUE;

    static {
        IDManager.INITED = false;
    }

    public static void init() {
        if (!IDManager.INITED) {
            IDManager.OBJECT_ID = 1000;
            IDManager.SESSION_ID = 1000;
            IDManager.EQUIPMENT_INS_ID = 1000;
            IDManager.MONSTER_LEGACY_BOX_ID = 1000;
            IDManager.GROUP_ID = 0;
            initEuipmentInsID();
            IDManager.INITED = true;
        }
    }

    public static synchronized int buildEquipmentInsID() {
        if (!IDManager.INITED) {
            init();
        }
        return IDManager.EQUIPMENT_INS_ID++;
    }

    public static final int buildObjectID() {
        if (!IDManager.INITED) {
            init();
        }
        return ++IDManager.OBJECT_ID;
    }

    public static final int buildMonsterLegacyBoxID() {
        if (!IDManager.INITED) {
            init();
        }
        return ++IDManager.MONSTER_LEGACY_BOX_ID;
    }

    public static synchronized int buildSessionID() {
        if (!IDManager.INITED) {
            init();
        }
        if (IDManager.SESSION_ID >= Integer.MAX_VALUE) {
            IDManager.SESSION_ID = 1000;
        }
        return ++IDManager.SESSION_ID;
    }

    private static void initEuipmentInsID() {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("select max(instance_id) as max_id from equipment_instance");
            rs = pstm.executeQuery();
            if (rs.next()) {
                int maxEuipmentInsID = rs.getInt("max_id");
                if (maxEuipmentInsID > 0) {
                    IDManager.EQUIPMENT_INS_ID = ++maxEuipmentInsID;
                } else {
                    IDManager.EQUIPMENT_INS_ID = 1000;
                }
            } else {
                IDManager.EQUIPMENT_INS_ID = 1000;
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (rs != null) {
                rs.close();
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

    public static synchronized int buildGroupID() {
        if (IDManager.GROUP_ID + 1 > Integer.MAX_VALUE) {
            IDManager.GROUP_ID = 0;
        }
        return ++IDManager.GROUP_ID;
    }
}
