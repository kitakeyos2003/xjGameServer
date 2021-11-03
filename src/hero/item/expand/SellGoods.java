// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.expand;

import hero.item.Goods;

public class SellGoods extends ExpandGoods {

    private int originalSellGoodsNums;
    private int traceSellGoodsNums;

    public SellGoods(final Goods _goods) {
        super(_goods);
    }

    public void setOriginalSellGoodsNums(final int _goodsNums) {
        this.originalSellGoodsNums = _goodsNums;
    }

    public int getOriginalSellGoodsNums() {
        return this.originalSellGoodsNums;
    }

    public void setTraceSellGoodsNums(final int _goodsNums) {
        this.traceSellGoodsNums = _goodsNums;
    }

    public int getTraceSellGoodsNums() {
        return this.traceSellGoodsNums;
    }

    @Override
    public byte getType() {
        return 3;
    }
}
