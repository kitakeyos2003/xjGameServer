// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect.service;

import java.util.List;
import hero.effect.Effect;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import hero.share.service.LogWriter;
import yoyo.service.tools.database.DBServiceImpl;
import hero.player.HeroPlayer;

public class EffectDAO {

    private static final String INSERT = "INSERT INTO player_effect(user_id, effect_id, keepTime, again) VALUES (?,?,?,?)";
    private static final String DELETE = "DELETE FROM player_effect WHERE user_id=?";
    private static final String SELECT = "select * from player_effect where user_id=? LIMIT 100";

    public static void deletePlayerEffect(final HeroPlayer _player) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        ArrayList<Effect> list = _player.effectList;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("select * from player_effect where user_id=? LIMIT 100");
            pstm.setInt(1, _player.getUserID());
            set = pstm.executeQuery();
            if (set != null) {
                int effect_id = 0;
                int keepTime = 0;
                Effect effect = null;
                while (set.next()) {
                    effect_id = set.getInt("effect_id");
                    keepTime = set.getInt("keepTime");
                    int again = set.getInt("again");
                    if (effect != null) {
                        list.add(effect);
                    }
                }
                pstm.close();
                conn = DBServiceImpl.getInstance().getConnection();
                pstm = conn.prepareStatement("DELETE FROM player_effect WHERE user_id=?");
                pstm.setInt(1, _player.getUserID());
                pstm.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            LogWriter.error(null, ex);
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
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
            } catch (SQLException e) {
                e.printStackTrace();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertPlayerEffect(final int _uid, final List<Effect> _list) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement("INSERT INTO player_effect(user_id, effect_id, keepTime, again) VALUES (?,?,?,?)");
            synchronized (_list) {
                int keepTime = 0;
                for (int i = 0; i < _list.size(); ++i) {
                    Effect effect = _list.get(i);
                }
            }
            pstm.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
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
}
