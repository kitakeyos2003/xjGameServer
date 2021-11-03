// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui.data;

import java.util.Iterator;
import java.io.IOException;
import hero.item.EqGoods;
import hero.item.expand.SellGoods;
import yoyo.tools.YOYOOutputStream;
import java.util.Set;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import hero.item.expand.ExpandGoods;
import java.util.ArrayList;
import java.util.Hashtable;

public class EquipmentListData {

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

    public static byte[] getData(final Hashtable<String, ArrayList<ExpandGoods>> _equipmentList, final int _gridNumsOfExsitsGoods) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte((byte) 2);
            output.writeByte(_gridNumsOfExsitsGoods);
            Map.Entry[] set = getSortedHashtableByKey(_equipmentList);
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
                                EqGoods e = (EqGoods) expandGoods.getGoodeModel();
                                output.writeByte(index++);
                                output.writeInt(e.getID());
                                output.writeShort(e.getIconID());
                                output.writeUTF(e.getName());
                                output.writeBytes(e.getFixPropertyBytes());
                                output.writeByte(0);
                                output.writeByte(0);
                                output.writeShort(e.getMaxDurabilityPoint());
                                output.writeShort(sellGoods.getTraceSellGoodsNums());
                                output.writeByte(1);
                                output.writeInt(e.getSellPrice());
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
        } catch (Exception e2) {
            e2.printStackTrace();
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
