// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge;

import java.sql.Timestamp;

public class ChargeInfo {

    public int accountID;
    public int userID;
    public byte paytype;
    public byte rechargetype;
    public int other_account_id;
    public int other_user_id;
    public String trans_id;
    public Timestamp rechargetime;
    public int price;
    public String fpcode;
    public byte result;
    public Timestamp result_time;
    public String order_id;
    public String statusCode;
    public int pointAmount;
    public long expBookTimeTotal;
    public long huntBookTimeTotal;
    public long offLineTimeTotal;

    public ChargeInfo(final int _userID) {
        this.userID = _userID;
    }

    public int addPointAmount(final int _point) {
        if (_point > 0) {
            return this.pointAmount += _point;
        }
        return -1;
    }

    public int reducePointAmount(final int _point) {
        if (this.pointAmount >= _point) {
            return this.pointAmount -= _point;
        }
        return -1;
    }
}
