// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.legacy;

import hero.item.Goods;

public class DistributeGoods {

    private static short distributeGoodsID;
    public short id;
    public Goods goods;
    public RaidPickerBox box;
    public byte number;
    public boolean hasOperated;
    public int pickerUserID;
    public int maxRandom;
    public long distributeTime;
    public int partnerNumber;

    public DistributeGoods() {
        if (DistributeGoods.distributeGoodsID >= 30000) {
            DistributeGoods.distributeGoodsID = 0;
        }
        this.id = (short) (++DistributeGoods.distributeGoodsID);
    }

    public int distribute(final int _playerUserID, final int _random) {
        if (_random > this.maxRandom) {
            this.maxRandom = _random;
            this.pickerUserID = _playerUserID;
        }
        ++this.partnerNumber;
        if (this.partnerNumber == this.box.getVisitorList().size()) {
            this.hasOperated = true;
        }
        return _random;
    }
}
