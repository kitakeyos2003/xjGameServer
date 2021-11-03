// 
// Decompiled by Procyon v0.5.36
// 
package hero.novice.service;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import hero.share.service.LogWriter;
import yoyo.service.tools.database.DBServiceImpl;
import hero.player.HeroPlayer;

public class NoviceDAO {

    private static final String UPDATE_PLAYER_AFTER_COMPLETE_SQL = "UPDATE player SET lvl=?,money=?,novice=?,where_id=?,where_x=?,where_y=? where user_id=? LIMIT 1";
    private static final String UPDATE_PLAYER_WHEN_EXIT_SQL = "UPDATE player SET novice=?,where_id=?,where_x=?,where_y=? where user_id=? LIMIT 1";

    public static void completeNoviceTeaching(final HeroPlayer _player) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE player SET lvl=?,money=?,novice=?,where_id=?,where_x=?,where_y=? where user_id=? LIMIT 1");
            ps.setShort(1, _player.getLevel());
            ps.setInt(2, _player.getMoney());
            ps.setShort(3, (short) 0);
            ps.setInt(4, _player.where().getID());
            ps.setShort(5, _player.getCellX());
            ps.setShort(6, _player.getCellY());
            ps.setInt(7, _player.getUserID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            LogWriter.error(null, e);
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e2) {
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
            } catch (SQLException e2) {
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
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    public static void exitNoviceTeaching(final HeroPlayer _player) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement("UPDATE player SET novice=?,where_id=?,where_x=?,where_y=? where user_id=? LIMIT 1");
            ps.setShort(1, (short) 0);
            ps.setInt(2, _player.where().getID());
            ps.setShort(3, _player.getCellX());
            ps.setShort(4, _player.getCellY());
            ps.setInt(5, _player.getUserID());
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
