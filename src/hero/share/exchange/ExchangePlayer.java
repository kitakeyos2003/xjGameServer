// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.exchange;

public class ExchangePlayer {

    public String nickname;
    public byte state;
    public short[] gridIndex;
    public int[] goodsID;
    public short[] goodsNum;
    public int money;
    public byte[] goodsType;
    public boolean locked;
    private static final int MAX_SIZE = 6;

    protected ExchangePlayer(final String _nickname) {
        this.locked = false;
        this.nickname = _nickname;
        this.state = 0;
        this.gridIndex = new short[6];
        this.goodsID = new int[6];
        this.goodsNum = new short[6];
        this.money = 0;
        this.goodsType = new byte[6];
    }

    public boolean addExchangeGoods(final short _index, final int _goodsID, final short _goodsNum, final byte _goodsType) {
        for (int i = 0; i < this.goodsID.length; ++i) {
            if (this.goodsID[i] == 0) {
                this.gridIndex[i] = _index;
                this.goodsID[i] = _goodsID;
                this.goodsNum[i] = _goodsNum;
                this.goodsType[i] = _goodsType;
                return true;
            }
        }
        return false;
    }

    public boolean removeExchangeGoods() {
        int i = 0;
        if (i < this.goodsID.length) {
            this.goodsID[i] = 0;
            this.gridIndex[i] = 0;
            this.goodsNum[i] = 0;
            this.goodsType[i] = 0;
            return true;
        }
        return false;
    }

    public boolean removeSingleExchangeGoods(final int index, final int goodsid) {
        for (int i = 0; i < this.goodsID.length; ++i) {
            if (i == index && this.goodsID[i] == goodsid) {
                this.goodsID[i] = 0;
                this.gridIndex[i] = 0;
                this.goodsNum[i] = 0;
                this.goodsType[i] = 0;
                return true;
            }
        }
        return false;
    }
}
