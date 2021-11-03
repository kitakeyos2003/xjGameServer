// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild.service;

import java.sql.ResultSet;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import hero.share.service.LogWriter;
import hero.guild.EGuildMemberRank;
import java.sql.Timestamp;
import yoyo.service.tools.database.DBServiceImpl;
import hero.guild.Guild;

public class GuildDAO {

    private static final String INSERT_GUILD_MEMBER = "INSERT INTO guild_member(guild_id, user_id, name,rank,create_time,update_time) VALUES(?,?,?,?,?,?)";
    private static final String INSERT_GUILD = "INSERT INTO guild(id, name, president_user_id,update_time) VALUES(?,?,?,?)";
    private static final String UPDATE_GUILD_PRESIDENT = "UPDATE guild SET president_user_id=?,update_time=? WHERE id=? LIMIT 1";
    private static final String UPDATE_GUILD_UP_LEVEL = "UPDATE guild SET level=? WHERE id=? LIMIT 1";
    private static final String DELETE_MEMBER = "DELETE FROM guild_member WHERE user_id=? LIMIT 1";
    private static final String CLEAR_GUILD_MEMBER = "DELETE FROM guild_member WHERE guild_id=?";
    private static final String DELETE_GUILD = "DELETE FROM guild WHERE id=? LIMIT 1";
    private static final String UPDATE_GUILD_MEMBER = "UPDATE guild_member SET rank=?,update_time=? WHERE user_id=? LIMIT 1";
    private static final String SELECT_GUILD = "SELECT * FROM guild";
    private static final String SELECT_MEMBER = "SELECT * FROM guild_member";
    private static final String SELECT_MAX_GUILD_ID = "SELECT MAX(id) as max_guild_id from guild";

    public static void create(final Guild _guild) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO guild(id, name, president_user_id,update_time) VALUES(?,?,?,?)");
            pstm.setInt(1, _guild.getID());
            pstm.setString(2, _guild.getName());
            pstm.setInt(3, _guild.getPresident().userID);
            pstm.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstm.executeUpdate();
            pstm.close();
            pstm = null;
            pstm = conn.prepareStatement("INSERT INTO guild_member(guild_id, user_id, name,rank,create_time,update_time) VALUES(?,?,?,?,?,?)");
            pstm.setInt(1, _guild.getID());
            pstm.setInt(2, _guild.getPresident().userID);
            pstm.setString(3, _guild.getPresident().name);
            pstm.setByte(4, EGuildMemberRank.PRESIDENT.value());
            pstm.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            pstm.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
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

    public static boolean add(final int _guild, final int _memberUserID, final String _memberName) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO guild_member(guild_id, user_id, name,rank,create_time,update_time) VALUES(?,?,?,?,?,?)");
            pstm.setInt(1, _guild);
            pstm.setInt(2, _memberUserID);
            pstm.setString(3, _memberName);
            pstm.setByte(4, EGuildMemberRank.NORMAL.value());
            pstm.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            pstm.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            pstm.executeUpdate();
            return true;
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
        return false;
    }

    public static void removeGuildMember(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM guild_member WHERE user_id=? LIMIT 1");
            pstm.setInt(1, _userID);
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

    public static void guildUpLevel(final int _guildID, final int _newLevel) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE guild SET level=? WHERE id=? LIMIT 1");
            pstm.setInt(1, _newLevel);
            pstm.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstm.setInt(2, _guildID);
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

    public static void updatePresident(final int _guildID, final int _newPresidentUserID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE guild SET president_user_id=?,update_time=? WHERE id=? LIMIT 1");
            pstm.setInt(1, _newPresidentUserID);
            pstm.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstm.setInt(3, _guildID);
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

    public static void distory(final int _guildID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM guild_member WHERE guild_id=?");
            pstm.setInt(1, _guildID);
            pstm.executeUpdate();
            pstm.close();
            pstm = null;
            pstm = conn.prepareStatement("DELETE FROM guild WHERE id=? LIMIT 1");
            pstm.setInt(1, _guildID);
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

    public static void changeMemberRank(final int _userID, final EGuildMemberRank _memberRank) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE guild_member SET rank=?,update_time=? WHERE user_id=? LIMIT 1");
            pstm.setInt(1, _memberRank.value());
            pstm.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstm.setInt(3, _userID);
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

    public static void transferPresident(final int _guildID, final int _oldPresidentUserID, final int _newPresidentUserID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement("UPDATE guild_member SET rank=?,update_time=? WHERE user_id=? LIMIT 1");
            pstm.setInt(1, EGuildMemberRank.PRESIDENT.value());
            pstm.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstm.setInt(3, _newPresidentUserID);
            pstm.addBatch();
            pstm.setInt(1, EGuildMemberRank.OFFICER.value());
            pstm.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstm.setInt(3, _oldPresidentUserID);
            pstm.addBatch();
            pstm.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            pstm.close();
            pstm = null;
            pstm = conn.prepareStatement("UPDATE guild SET president_user_id=?,update_time=? WHERE id=? LIMIT 1");
            pstm.setInt(1, _newPresidentUserID);
            pstm.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstm.setInt(3, _guildID);
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

    public static void load(final HashMap<Integer, Guild> _guildTable) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT MAX(id) as max_guild_id from guild");
            set = pstm.executeQuery();
            if (set.next()) {
                int maxGuildID = set.getInt("max_guild_id");
                if (maxGuildID > 0) {
                    GuildServiceImpl.getInstance().setUseableGuildID(++maxGuildID);
                }
            }
            set.close();
            set = null;
            pstm.close();
            pstm = null;
            pstm = conn.prepareStatement("SELECT * FROM guild");
            set = pstm.executeQuery();
            while (set.next()) {
                int guildID = set.getInt("id");
                String guildName = set.getString("name");
                int gjuildLevel = set.getInt("level");
                _guildTable.put(guildID, new Guild(guildID, guildName, gjuildLevel));
            }
            set.close();
            set = null;
            pstm.close();
            pstm = null;
            pstm = conn.prepareStatement("SELECT * FROM guild_member");
            set = pstm.executeQuery();
            while (set.next()) {
                int guildID = set.getInt("guild_id");
                int memberUserID = set.getInt("user_id");
                String memeberName = set.getString("name");
                byte rank = set.getByte("rank");
                Guild guild = GuildServiceImpl.getInstance().getGuild(guildID);
                if (guild != null) {
                    guild.add(memberUserID, memeberName, rank);
                }
            }
        } catch (Exception ex) {
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
}
