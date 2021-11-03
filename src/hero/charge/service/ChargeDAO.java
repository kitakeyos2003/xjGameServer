// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import hero.gm.service.GmServiceImpl;
import hero.charge.ChargeInfo;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import hero.share.service.LogWriter;
import yoyo.service.tools.database.DBServiceImpl;
import hero.player.HeroPlayer;
import org.apache.log4j.Logger;

public class ChargeDAO {

    private static Logger log;
    private static final String INSERT_SMS_FEE_INI = "insert into sms_fee_ini(account_id,role_id,trans_id,mobile_user_id,sum_price,server_id) value (?,?,?,?,?,?)";
    private static final String INSERT_CHARGE_UP_SZF = "insert into charge_up(account_id,user_id,paytype,rechargetype,other_account_id,other_user_id,trans_id,status_code,price,order_id,status_result,fpcode)  values(?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String INSERT_CHARGE_UP_NG = "insert into charge_up(account_id,user_id,paytype,rechargetype,other_account_id,other_user_id,trans_id,status_code,price,status_result,fpcode)  values(?,?,?,?,?,?,?,?,?,?,?)";
    private static final String INSERT_SZF_CHARGE = "insert into szf_chargeup(game_id,server_id,account_id,role_id,trans_id,order_id) values(?,?,?,?,?,?)";
    private static final String UPDATE_CHARGE_UP_RESULT = "update charge_up t set t.result=?,t.result_time=? where t.order_id=? and t.trans_id=?";
    private static final String SELECT_CHARGE_UP_INFO = "select * from charge_up t where t.trans_id=? and t.order_id=?";
    private static final String SELECT_TIME_INFO_SQL = "SELECT * FROM player_time_info WHERE user_id = ? LIMIT 1";
    private static final String UPDATE_HUNT_EXP_BOOK_TIME_SQL = "UPDATE player_time_info SET hunt_exp_book_time_total=? WHERE user_id = ? LIMIT 1";
    private static final String INSERT_EXP_BOOK_TIME_SQL = "INSERT INTO player_time_info(user_id,exp_book_time_total) VALUES(?,?)";
    private static final String INSERT_HUNT_EXP_BOOK_TIME_SQL = "INSERT INTO player_time_info(user_id,hunt_exp_book_time_total) VALUES(?,?)";
    private static final String UPDATE_EXP_BOOK_INFO_SQL = "UPDATE player_time_info SET hunt_exp_book_time_total=?  WHERE user_id = ? LIMIT 1";
    private static final String DELETE_EXP_BOOK_INFO_SQL = "DELETE FROM player_time_info WHERE user_id = ? LIMIT 1";

    static {
        ChargeDAO.log = Logger.getLogger((Class) ChargeDAO.class);
    }

    public static void loadTimeInfo(final HeroPlayer _player) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        try {
            _player.getChargeInfo().offLineTimeTotal = ((_player.lastLogoutTime > 315504000000L) ? (System.currentTimeMillis() - _player.lastLogoutTime) : 0L);
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM player_time_info WHERE user_id = ? LIMIT 1");
            pstm.setInt(1, _player.getUserID());
            set = pstm.executeQuery();
            if (set.next()) {
                long expBookTimeTotal = set.getLong("exp_book_time_total");
                long huntBookTimeTotal = set.getLong("hunt_exp_book_time_total");
                _player.getChargeInfo().expBookTimeTotal = expBookTimeTotal;
                _player.getChargeInfo().huntBookTimeTotal = huntBookTimeTotal;
                if (_player.getChargeInfo().huntBookTimeTotal > 0L) {
                    _player.changeExperienceModulus(1.0f);
                    ExperienceBookService.getInstance().put(_player.getChargeInfo());
                }
            }
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

    public static void updateExpBookTimeInfo(final ChargeInfo _chargeInfo) {
    }

    public static void updateHuntExpBookTimeInfo(final ChargeInfo _chargeInfo) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE player_time_info SET hunt_exp_book_time_total=? WHERE user_id = ? LIMIT 1");
            pstm.setLong(1, _chargeInfo.huntBookTimeTotal);
            pstm.setInt(2, _chargeInfo.userID);
            int exist = pstm.executeUpdate();
            if (exist == 0) {
                pstm.close();
                pstm = null;
                pstm = conn.prepareStatement("INSERT INTO player_time_info(user_id,hunt_exp_book_time_total) VALUES(?,?)");
                pstm.setInt(1, _chargeInfo.userID);
                pstm.setLong(2, _chargeInfo.huntBookTimeTotal);
                pstm.execute();
            }
        } catch (Exception e) {
            LogWriter.error("error:\u66f4\u65b0\u72e9\u730e\u7ecf\u9a8c\u4e66\u5931\u8d25", e);
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

    public static void updateExpBookInfo(final ChargeInfo _chargeInfo) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE player_time_info SET hunt_exp_book_time_total=?  WHERE user_id = ? LIMIT 1");
            pstm.setLong(1, _chargeInfo.huntBookTimeTotal);
            pstm.setInt(2, _chargeInfo.userID);
            pstm.executeUpdate();
        } catch (Exception e) {
            LogWriter.error("error:\u66f4\u65b0\u7ecf\u9a8c\u4e66\u5931\u8d25", e);
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

    public static void clearExpBookInfo(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM player_time_info WHERE user_id = ? LIMIT 1");
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
        } catch (Exception e) {
            LogWriter.error("error:\u5220\u9664\u7ecf\u9a8c\u4e66\u5931\u8d25", e);
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

    public static void insertChargeUpSZF(final int accountID, final int userID, final byte paytype, final byte rechargetype, final int otherAccountID, final int otherUserID, final String transID, final String statusCode, final int price, final String orderID, final int syncRes, final String fpcode) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("insert into charge_up(account_id,user_id,paytype,rechargetype,other_account_id,other_user_id,trans_id,status_code,price,order_id,status_result,fpcode)  values(?,?,?,?,?,?,?,?,?,?,?,?)");
            pstm.setInt(1, accountID);
            pstm.setInt(2, userID);
            pstm.setByte(3, paytype);
            pstm.setByte(4, rechargetype);
            pstm.setInt(5, otherAccountID);
            pstm.setInt(6, otherUserID);
            pstm.setString(7, transID);
            pstm.setString(8, statusCode);
            pstm.setInt(9, price);
            pstm.setString(10, orderID);
            pstm.setInt(11, syncRes);
            pstm.setString(12, fpcode);
            pstm.executeUpdate();
            pstm.close();
            conn.close();
        } catch (Exception e) {
            ChargeDAO.log.error((Object) "\u795e\u5dde\u4ed8\u5145\u503c\u65f6\u8bb0\u5f55\u4fe1\u606f:", (Throwable) e);
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

    public static void insertChargeUpSZFAccount(final int gameID, final int serverID, final int accountID, final int userID, final String transID, final String orderID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("insert into szf_chargeup(game_id,server_id,account_id,role_id,trans_id,order_id) values(?,?,?,?,?,?)");
            pstm.setInt(1, GmServiceImpl.gameID);
            pstm.setInt(2, GmServiceImpl.serverID);
            pstm.setInt(3, accountID);
            pstm.setInt(4, userID);
            pstm.setString(5, transID);
            pstm.setString(6, orderID);
            pstm.executeUpdate();
            pstm.close();
            conn.close();
        } catch (Exception e) {
            ChargeDAO.log.error((Object) "\u5145\u503c\u65f6\u5728xj_account\u6570\u636e\u5e93\u91cc\u7684 szf_chargeup \u8bb0\u5f55\u4fe1\u606f error: ", (Throwable) e);
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

    public static int updateChargeSZF(final byte result, final Timestamp resultTime, final String transID, final String orderID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        int res = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("update charge_up t set t.result=?,t.result_time=? where t.order_id=? and t.trans_id=?");
            pstm.setByte(1, result);
            pstm.setTimestamp(2, resultTime);
            pstm.setString(3, orderID);
            pstm.setString(4, transID);
            res = pstm.executeUpdate();
            pstm.close();
            conn.close();
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
        return res;
    }

    public static void insertChargeUpNG(final int accountID, final int userID, final byte paytype, final byte rechargetype, final int otherAccountID, final int otherUserID, final String transID, final String statusCode, final int price, final int syncRes, final String fpcode) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("insert into charge_up(account_id,user_id,paytype,rechargetype,other_account_id,other_user_id,trans_id,status_code,price,status_result,fpcode)  values(?,?,?,?,?,?,?,?,?,?,?)");
            pstm.setInt(1, accountID);
            pstm.setInt(2, userID);
            pstm.setByte(3, paytype);
            pstm.setByte(4, rechargetype);
            pstm.setInt(5, otherAccountID);
            pstm.setInt(6, otherUserID);
            pstm.setString(7, transID);
            pstm.setString(8, statusCode);
            pstm.setInt(9, price);
            pstm.setInt(10, syncRes);
            pstm.setString(11, fpcode);
            pstm.executeUpdate();
            pstm.close();
            conn.close();
        } catch (Exception e) {
            ChargeDAO.log.error((Object) "\u7f51\u6e38\u5145\u503c\u65f6\u8bb0\u5f55\u4fe1\u606f error: ", (Throwable) e);
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

    public static ChargeInfo getChargeInfo(final String transID, final String orderID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        ChargeInfo info = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("select * from charge_up t where t.trans_id=? and t.order_id=?");
            pstm.setString(1, transID);
            pstm.setString(2, orderID);
            rs = pstm.executeQuery();
            if (rs.next()) {
                int userID = rs.getInt("user_id");
                int accountID = rs.getInt("account_id");
                byte paytype = rs.getByte("paytype");
                byte rechargetype = rs.getByte("rechargetype");
                info = new ChargeInfo(userID);
                if (rechargetype == 2) {
                    int otherAccountID = rs.getInt("other_account_id");
                    int otherUserID = rs.getInt("other_user_id");
                    info.other_account_id = otherAccountID;
                    info.other_user_id = otherUserID;
                }
                String statusCode = rs.getString("status_code");
                int price = rs.getInt("price");
                String fpcode = rs.getString("fpcode");
                String transid = rs.getString("trans_id");
                Timestamp rechargeTime = rs.getTimestamp("rechargetime");
                info.accountID = accountID;
                info.paytype = paytype;
                info.rechargetype = rechargetype;
                info.price = price;
                info.statusCode = statusCode;
                info.rechargetime = rechargeTime;
                info.fpcode = fpcode;
                info.trans_id = transid;
            }
        } catch (SQLException e) {
            ChargeDAO.log.error((Object) "\u83b7\u53d6\u5145\u503c\u4fe1\u606ferror :", (Throwable) e);
            e.printStackTrace();
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
        return info;
    }

    public static void saveSmsFeeIni(final int accountID, final int userID, final String transID, final String mobileUserID, final int sumPrice, final int serverID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = GmServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("insert into sms_fee_ini(account_id,role_id,trans_id,mobile_user_id,sum_price,server_id) value (?,?,?,?,?,?)");
            pstm.setInt(1, accountID);
            pstm.setInt(2, userID);
            pstm.setString(3, transID);
            pstm.setString(4, mobileUserID);
            pstm.setInt(5, sumPrice);
            pstm.setInt(6, serverID);
            pstm.executeUpdate();
        } catch (SQLException e) {
            ChargeDAO.log.error((Object) "\u4fdd\u5b58SMS\u8ba1\u8d39\u914d\u7f6e\u4fe1\u606ferror :", (Throwable) e);
            e.printStackTrace();
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
}
