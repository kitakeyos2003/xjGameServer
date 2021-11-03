// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import hero.item.Goods;
import java.util.ArrayList;
import hero.item.Equipment;
import hero.item.service.GoodsServiceImpl;
import hero.npc.function.system.storage.WarehouseGoods;
import java.io.IOException;
import yoyo.tools.YOYOOutputStream;
import hero.npc.function.system.storage.Warehouse;

public class UI_StorageGoodsList {

    public static final byte TYPE_OF_SINGLE_GOODS = 1;
    public static final byte TYPE_OF_EQUIPMENT = 2;

    public static byte[] getBytes(final String[] _menuList, final Warehouse _warehouse) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeBytes(getData(_warehouse));
            output.writeByte(_menuList.length);
            for (int i = 0; i < _menuList.length; ++i) {
                String menu = _menuList[i];
                output.writeUTF(menu);
                output.writeByte(0);
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

    private static byte[] getData(final Warehouse _warehouse) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte((byte) 5);
            ArrayList<WarehouseGoods> goodsList = _warehouse.getGoodsList();
            output.writeByte(goodsList.size());
            output.writeByte(_warehouse.getGoodsNum());
            for (int i = 0; i < goodsList.size(); ++i) {
                WarehouseGoods wgoods = goodsList.get(i);
                if (wgoods != null) {
                    Goods goods = null;
                    if (wgoods.goodsType == 0) {
                        goods = wgoods.instance.getArchetype();
                    } else {
                        goods = GoodsServiceImpl.getInstance().getGoodsByID(wgoods.goodsID);
                    }
                    if (goods != null) {
                        output.writeByte(i);
                        output.writeInt(wgoods.goodsID);
                        output.writeShort(goods.getIconID());
                        output.writeShort(goods.getNeedLevel());
                        if (goods instanceof Equipment) {
                            StringBuffer name = new StringBuffer();
                            name.append(wgoods.instance.getArchetype().getName());
                            int level = wgoods.instance.getGeneralEnhance().getLevel();
                            if (level > 0) {
                                name.append("+");
                                name.append(level);
                            }
                            int flash = wgoods.instance.getGeneralEnhance().getFlash();
                            if (flash > 0) {
                                name.append("(\u95ea");
                                name.append(flash);
                                name.append(")");
                            }
                            output.writeUTF(name.toString());
                        } else {
                            output.writeUTF(goods.getName());
                        }
                        if (goods instanceof Equipment) {
                            output.writeByte((byte) 2);
                            output.writeBytes(((Equipment) goods).getFixPropertyBytes());
                            output.writeByte(wgoods.instance.isBind());
                            output.writeByte(wgoods.instance.existSeal());
                            output.writeShort(((Equipment) goods).getMaxDurabilityPoint());
                            output.writeShort(wgoods.goodsNum);
                        } else {
                            output.writeByte((byte) 1);
                            output.writeByte(goods.getTrait().value());
                            output.writeUTF(goods.getDescription());
                            output.writeShort(wgoods.goodsNum);
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

    public static EUIType getType() {
        return EUIType.GOODS_OPERATE_LIST;
    }
}
