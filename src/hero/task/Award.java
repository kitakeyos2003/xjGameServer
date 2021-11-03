// 
// Decompiled by Procyon v0.5.36
// 
package hero.task;

import java.util.Iterator;
import hero.item.Goods;
import java.util.ArrayList;

public class Award {

    public int money;
    public int experience;
    public int skillID;
    public int effectID;
    public short mapID;
    public short mapX;
    public short mapY;
    private ArrayList<AwardGoodsUnit> optionalGoodList;
    private ArrayList<AwardGoodsUnit> boundGoodList;

    public void addOptionalGoods(final Goods _goods, final int _number) {
        if (this.optionalGoodList == null) {
            this.optionalGoodList = new ArrayList<AwardGoodsUnit>();
        }
        this.optionalGoodList.add(new AwardGoodsUnit(_goods, (byte) _number));
    }

    public ArrayList<AwardGoodsUnit> getOptionalGoodsList() {
        return this.optionalGoodList;
    }

    public int selectGoodsVerify(final int _goodsID) {
        int result = 0;
        if (this.optionalGoodList != null) {
            for (int i = 0; i < this.optionalGoodList.size(); ++i) {
                int existGoodsID = this.optionalGoodList.get(i).goods.getID();
                if (existGoodsID == _goodsID) {
                    result = existGoodsID;
                    break;
                }
                result = existGoodsID;
            }
        }
        return result;
    }

    public byte getOptionalGoodsNumber(final Goods _goods) {
        if (this.optionalGoodList != null) {
            for (final AwardGoodsUnit awardGoods : this.optionalGoodList) {
                if (awardGoods.goods == _goods) {
                    return awardGoods.number;
                }
            }
        }
        return 0;
    }

    public void addBoundGoods(final Goods _goods, final int _number) {
        if (this.boundGoodList == null) {
            this.boundGoodList = new ArrayList<AwardGoodsUnit>();
        }
        this.boundGoodList.add(new AwardGoodsUnit(_goods, (byte) _number));
    }

    public ArrayList<AwardGoodsUnit> getBoundGoodsList() {
        return this.boundGoodList;
    }

    public static class AwardGoodsUnit {

        public Goods goods;
        public byte number;

        public AwardGoodsUnit(final Goods _goods, final byte _number) {
            this.goods = _goods;
            this.number = _number;
        }
    }
}
