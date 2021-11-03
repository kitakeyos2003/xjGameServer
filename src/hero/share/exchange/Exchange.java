// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.exchange;

public class Exchange {

    public static final byte BEGIN = 1;
    public static final byte ADD_MONEY = 2;
    public static final byte LIST_INVENTORY_GOODS = 3;
    public static final byte ADD_GOODS = 4;
    public static final byte CONFIM = 5;
    public static final byte EXCHANGE_CANCEL = 6;
    public static final byte EXCHANGE_FINISH = 7;
    public static final byte EXCHANGE_BUSY = 8;
    public static final byte EXCHANGE_LOCK = 9;
    public static final byte REMOVE_GOODS = 10;
    public static final byte REMOVE_SINGLE_GOODS = 11;
    private int exchangeID;
    private ExchangePlayer player1;
    private ExchangePlayer player2;
    public static final byte NORMAL = 0;
    public static final byte READY = 1;
    private int requestExchangeUserID;

    protected Exchange(final int _exchangeID, final String _player1Nickname, final String _player2Nickname) {
        this.exchangeID = _exchangeID;
        this.player1 = new ExchangePlayer(_player1Nickname);
        this.player2 = new ExchangePlayer(_player2Nickname);
    }

    public int getExchangeID() {
        return this.exchangeID;
    }

    public ExchangePlayer getPlayerByNickname(final String _nickname) {
        if (this.player1.nickname.equals(_nickname)) {
            return this.player1;
        }
        if (this.player2.nickname.equals(_nickname)) {
            return this.player2;
        }
        return null;
    }

    public ExchangePlayer getTargetByNickname(final String _nickname) {
        if (this.player1.nickname.equals(_nickname)) {
            return this.player2;
        }
        if (this.player2.nickname.equals(_nickname)) {
            return this.player1;
        }
        return null;
    }

    public int getRequestExchangeUserID() {
        return this.requestExchangeUserID;
    }

    public void setRequestExchangeUserID(final int requestExchangeUserID) {
        this.requestExchangeUserID = requestExchangeUserID;
    }
}
