// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.expand;

import hero.item.Goods;
import java.util.ArrayList;

public class ExchangeGoods extends ExpandGoods {

    private ArrayList<int[]> materialList;

    public ExchangeGoods(final Goods _goods) {
        super(_goods);
        this.materialList = new ArrayList<int[]>();
    }

    public void addExchangeMaterial(final int _goodsID, final int _number) {
        this.materialList.add(new int[]{_goodsID, _number});
    }

    public ArrayList<int[]> getMaterialList() {
        return this.materialList;
    }

    @Override
    public byte getType() {
        return 2;
    }
}
