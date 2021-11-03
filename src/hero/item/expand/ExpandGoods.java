// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.expand;

import hero.item.Goods;

public abstract class ExpandGoods {

    public static final byte ROUP_GOODS = 1;
    public static final byte EXCHANGE_GOODS = 2;
    public static final byte SELL_GOODS = 3;
    private Goods goods;
    private String expandDescription;

    public abstract byte getType();

    public ExpandGoods(final Goods _goods) {
        this.goods = _goods;
    }

    public Goods getGoodeModel() {
        return this.goods;
    }

    public String getExpandDesc() {
        return this.expandDescription;
    }
}
