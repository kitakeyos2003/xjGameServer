// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui.data;

import hero.item.SpecialGoods;
import hero.item.service.GoodsServiceImpl;
import hero.item.SingleGoods;
import hero.item.bag.SingleGoodsBag;
import hero.item.EquipmentInstance;
import java.io.IOException;
import hero.item.Weapon;
import hero.item.detail.EGoodsType;
import yoyo.tools.YOYOOutputStream;
import hero.item.bag.EquipmentContainer;

public class ActiveGoodsBagData {

    public static byte[] getData(final EquipmentContainer _equipmentPackage) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(EGoodsType.EQUIPMENT.value());
            EquipmentInstance[] equipmentDataList = _equipmentPackage.getEquipmentList();
            output.writeByte(equipmentDataList.length);
            output.writeByte(_equipmentPackage.getFullGridNumber());
            for (int i = 0; i < equipmentDataList.length; ++i) {
                EquipmentInstance instance = equipmentDataList[i];
                if (instance != null) {
                    output.writeByte(i);
                    output.writeInt(instance.getInstanceID());
                    output.writeShort(instance.getArchetype().getIconID());
                    StringBuffer name = new StringBuffer();
                    name.append(instance.getArchetype().getName());
                    int level = instance.getGeneralEnhance().getLevel();
                    if (level > 0) {
                        name.append("+");
                        name.append(level);
                    }
                    int flash = instance.getGeneralEnhance().getFlash();
                    if (flash > 0) {
                        name.append("(\u95ea");
                        name.append(flash);
                        name.append(")");
                    }
                    output.writeUTF(name.toString());
                    if (instance.getArchetype() instanceof Weapon) {
                        output.writeByte(1);
                        output.writeBytes(instance.getArchetype().getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeByte(instance.existSeal());
                        output.writeShort(instance.getCurrentDurabilityPoint());
                        output.writeInt(instance.getArchetype().getRetrievePrice());
                        output.writeUTF(instance.getGeneralEnhance().getUpEndString());
                        output.writeShort(instance.getGeneralEnhance().getFlashView()[0]);
                        output.writeShort(instance.getGeneralEnhance().getFlashView()[1]);
                        output.writeByte(instance.getGeneralEnhance().detail.length);
                        for (int j = 0; j < instance.getGeneralEnhance().detail.length; ++j) {
                            if (instance.getGeneralEnhance().detail[j][0] == 1) {
                                output.writeByte(instance.getGeneralEnhance().detail[j][1] + 1);
                            } else {
                                output.writeByte(0);
                            }
                        }
                    } else {
                        output.writeByte(2);
                        output.writeBytes(instance.getArchetype().getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeByte(instance.existSeal());
                        output.writeShort(instance.getCurrentDurabilityPoint());
                        output.writeInt(instance.getArchetype().getRetrievePrice());
                        output.writeUTF(instance.getGeneralEnhance().getUpEndString());
                        output.writeShort(instance.getGeneralEnhance().getArmorFlashView()[0]);
                        output.writeShort(instance.getGeneralEnhance().getArmorFlashView()[1]);
                        output.writeByte(instance.getGeneralEnhance().detail.length);
                        for (int j = 0; j < instance.getGeneralEnhance().detail.length; ++j) {
                            if (instance.getGeneralEnhance().detail[j][0] == 1) {
                                output.writeByte(instance.getGeneralEnhance().detail[j][1] + 1);
                            } else {
                                output.writeByte(0);
                            }
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

    public static byte[] getData(final SingleGoodsBag _singleGoodsPackage, final EGoodsType _goodsType) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(_goodsType.value());
            int[][] singleGoodsDataList = _singleGoodsPackage.getAllItem();
            output.writeByte(singleGoodsDataList.length);
            output.writeByte(_singleGoodsPackage.getFullGridNumber());
            for (int i = 0; i < singleGoodsDataList.length; ++i) {
                int[] goodsData = singleGoodsDataList[i];
                if (goodsData[0] != 0) {
                    SingleGoods goods = (SingleGoods) GoodsServiceImpl.getInstance().getGoodsByID(goodsData[0]);
                    if (goods != null) {
                        output.writeByte(i);
                        output.writeInt(goods.getID());
                        output.writeShort(goods.getIconID());
                        output.writeUTF(goods.getName());
                        output.writeByte(goods.getTrait().value());
                        output.writeShort(goodsData[1]);
                        output.writeByte(goodsData[1]);
                        output.writeInt(goods.getSellPrice());
                        int level = 1;
                        if (goods.getNeedLevel() > 1) {
                            level = goods.getNeedLevel();
                        }
                        output.writeShort(level);
                        if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                            if (((SpecialGoods) goods).canBeSell()) {
                                output.writeUTF(String.valueOf(goods.getDescription()) + "\n" + "\u51fa\u552e\u4ef7\u683c\uff1a" + goods.getRetrievePrice());
                            } else {
                                output.writeUTF(String.valueOf(goods.getDescription()) + "\n" + "\u4e0d\u53ef\u51fa\u552e");
                            }
                        } else {
                            output.writeUTF(String.valueOf(goods.getDescription()) + "\n" + "\u51fa\u552e\u4ef7\u683c\uff1a" + goods.getRetrievePrice());
                        }
                        output.writeByte(goods.exchangeable() ? 1 : 0);
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
