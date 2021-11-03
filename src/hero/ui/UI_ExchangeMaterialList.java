// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import hero.item.Goods;
import java.util.Iterator;
import java.io.IOException;
import hero.item.dictionary.GoodsContents;
import yoyo.tools.YOYOOutputStream;
import hero.item.bag.SingleGoodsBag;
import hero.item.expand.ExchangeGoods;

public class UI_ExchangeMaterialList {

    public static byte[] getBytes(final ExchangeGoods _exchangeGoods, final SingleGoodsBag _materialBag) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeInt(_exchangeGoods.getGoodeModel().getID());
            output.writeByte(_exchangeGoods.getMaterialList().size());
            for (final int[] materialInfo : _exchangeGoods.getMaterialList()) {
                Goods goods = GoodsContents.getGoods(materialInfo[0]);
                output.writeShort(goods.getIconID());
                output.writeUTF(goods.getName());
                output.writeShort(materialInfo[1]);
                output.writeByte(goods.getTrait().value());
                output.writeShort(_materialBag.getGoodsNumber(materialInfo[0]));
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
        return EUIType.EXCHANGE_MATERIAL_LIST;
    }
}
