// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import hero.item.Goods;
import java.util.Iterator;
import java.io.IOException;
import yoyo.tools.YOYOOutputStream;
import hero.item.bag.SingleGoodsBag;
import hero.item.expand.ExchangeGoods;
import java.util.ArrayList;

public class UI_ExchangeItemList {

    public static byte[] getBytes(final String[] _menuList, final ArrayList<ExchangeGoods> _exchangeGoodsList, final SingleGoodsBag _materialBag) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeByte(_exchangeGoodsList.size());
            for (final ExchangeGoods exchangeGoods : _exchangeGoodsList) {
                Goods goods = exchangeGoods.getGoodeModel();
                output.writeInt(goods.getID());
                output.writeShort(goods.getIconID());
                output.writeUTF(goods.getName());
                output.writeByte(goods.getTrait().value());
                ArrayList<int[]> materialList = exchangeGoods.getMaterialList();
                int goodsMaxExchangeNumber = _materialBag.getGoodsNumber(materialList.get(0)[0]) / materialList.get(0)[1];
                for (int i = 1; i < materialList.size(); ++i) {
                    int singleMaterialExchangeNumber = _materialBag.getGoodsNumber(materialList.get(i)[0]) / materialList.get(i)[1];
                    if (goodsMaxExchangeNumber < singleMaterialExchangeNumber) {
                        goodsMaxExchangeNumber = singleMaterialExchangeNumber;
                    }
                }
                output.writeShort(goodsMaxExchangeNumber);
                output.writeUTF("\u4ef7\u683c\uff1a" + goods.getSellPrice() + "\n" + goods.getDescription());
            }
            output.writeByte(_menuList.length);
            for (int j = 0; j < _menuList.length; ++j) {
                output.writeUTF(_menuList[j]);
                output.writeByte(0);
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
        return null;
    }

    public static EUIType getType() {
        return EUIType.EXCHANGE_ITEM_LIST;
    }
}
