// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui.data;

import java.util.Iterator;
import java.io.IOException;
import hero.item.SingleGoods;
import hero.item.expand.SellGoods;
import yoyo.tools.YOYOOutputStream;
import java.util.Set;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import hero.item.expand.ExpandGoods;
import java.util.ArrayList;
import java.util.Hashtable;

public class SingleGoodsListData {

    private static Map.Entry<String, ArrayList<ExpandGoods>>[] getSortedHashtableByKey(final Hashtable<String, ArrayList<ExpandGoods>> h) {
        Set<Map.Entry<String, ArrayList<ExpandGoods>>> set = h.entrySet();
        Map.Entry[] entries = set.toArray(new Map.Entry[set.size()]);
        Arrays.sort(entries, new Comparator() {
            @Override
            public int compare(final Object arg0, final Object arg1) {
                Object key1 = ((Map.Entry) arg0).getKey();
                Object key2 = ((Map.Entry) arg1).getKey();
                return ((Comparable) key1).compareTo(key2);
            }
        });
        return (Map.Entry<String, ArrayList<ExpandGoods>>[]) entries;
    }

    public static byte[] getData(final Hashtable<String, ArrayList<ExpandGoods>> _singleGoodsList, final int _spareGoodsType) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte((byte) 4);
            output.writeByte(_spareGoodsType);
            Map.Entry[] set = getSortedHashtableByKey(_singleGoodsList);
            output.writeByte(set.length);
            for (int i = 0; i < set.length; ++i) {
                output.writeUTF((String) set[i].getKey());
                ArrayList<ExpandGoods> goodsList = (ArrayList<ExpandGoods>) set[i].getValue();
                output.writeByte(goodsList.size());
                int index = 0;
                for (final ExpandGoods expandGoods : goodsList) {
                    switch (expandGoods.getType()) {
                        case 3: {
                            SellGoods sellGoods = (SellGoods) expandGoods;
                            if (sellGoods.getTraceSellGoodsNums() != 0) {
                                SingleGoods goods = (SingleGoods) expandGoods.getGoodeModel();
                                output.writeByte(index++);
                                output.writeInt(goods.getID());
                                output.writeShort(goods.getIconID());
                                output.writeUTF(goods.getName());
                                output.writeByte(goods.getTrait().value());
                                output.writeShort(sellGoods.getTraceSellGoodsNums());
                                output.writeInt(goods.getSellPrice());
                                output.writeShort(goods.getNeedLevel());
                                if (-1 == sellGoods.getTraceSellGoodsNums()) {
                                    output.writeByte(goods.getMaxStackNums());
                                } else {
                                    output.writeByte(sellGoods.getTraceSellGoodsNums());
                                }
                                output.writeUTF(String.valueOf(goods.getDescription()) + "\n" + "\u4ef7\u683c\uff1a" + goods.getSellPrice());
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                }
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }
}
