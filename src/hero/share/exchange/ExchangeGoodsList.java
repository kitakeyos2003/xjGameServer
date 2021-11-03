// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.exchange;

import hero.item.EquipmentInstance;
import hero.item.Weapon;
import hero.item.bag.EquipmentContainer;
import hero.pet.Pet;
import hero.item.bag.PetContainer;
import hero.item.Goods;
import java.io.IOException;
import hero.item.service.GoodsServiceImpl;
import yoyo.tools.YOYOOutputStream;
import hero.item.bag.SingleGoodsBag;

public class ExchangeGoodsList {

    private static int showNum(final ExchangePlayer _eplayer, final int _gridIndex, int num, final int goodsID) {
        for (int i = 0; i < _eplayer.gridIndex.length; ++i) {
            if (_eplayer.gridIndex[i] == _gridIndex && _eplayer.goodsID[i] == goodsID) {
                num -= _eplayer.goodsNum[i];
            }
        }
        return num;
    }

    public static byte[] getData(final SingleGoodsBag _singleGoodsPackage, final ExchangePlayer _eplayer) {
        YOYOOutputStream output = new YOYOOutputStream();
        YOYOOutputStream output2 = new YOYOOutputStream();
        try {
            int[][] singleGoodsDataList = _singleGoodsPackage.getAllItem();
            int num = 0;
            for (int i = 0; i < singleGoodsDataList.length; ++i) {
                int[] goodsData = singleGoodsDataList[i];
                if (goodsData[0] != 0) {
                    Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsData[0]);
                    if (goods != null) {
                        int n = showNum(_eplayer, i, goodsData[1], goodsData[0]);
                        if (n > 0) {
                            ++num;
                            output.writeByte(i);
                            output.writeInt(goods.getID());
                            output.writeShort(goods.getIconID());
                            output.writeUTF(goods.getName());
                            output.writeByte(goods.getTrait().value());
                            output.writeShort(n);
                            output.writeUTF(String.valueOf(goods.getDescription()) + "\n" + "\u4ef7\u683c\uff1a" + goods.getRetrievePrice());
                            output.writeByte(goods.exchangeable() ? 1 : 0);
                        }
                    }
                }
            }
            output.flush();
            output2.writeByte(singleGoodsDataList.length);
            output2.writeByte(num);
            output2.writeBytes(output.getBytes());
            output2.flush();
            return output2.getBytes();
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

    public static byte[] getData(final PetContainer petPackage, final ExchangePlayer _eplayer) {
        YOYOOutputStream output = new YOYOOutputStream();
        YOYOOutputStream output2 = new YOYOOutputStream();
        try {
            Pet[] petlist = petPackage.getPetList();
            output.writeByte(petlist.length);
            output.writeByte(petPackage.getFullGridNumber());
            for (int i = 0; i < petlist.length; ++i) {
                Pet pet = petlist[i];
                if (pet != null) {
                    output.writeByte(i);
                    output.writeInt(pet.id);
                    output.writeShort(pet.iconID);
                    output.writeUTF(pet.name);
                    output.writeByte(pet.trait.value());
                    output.writeShort(1);
                    output.writeUTF(pet.name);
                    output.writeInt(pet.feeding);
                    output.writeByte((pet.bind == 1) ? 1 : 0);
                }
            }
            output.flush();
            output2.writeByte(petlist.length);
            output2.writeByte(1);
            output2.writeBytes(output.getBytes());
            output2.flush();
            return output2.getBytes();
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

    private static boolean isShow(final ExchangePlayer _eplayer, final int index, final int goodsID) {
        for (int i = 0; i < _eplayer.gridIndex.length; ++i) {
            if (_eplayer.gridIndex[i] == index && _eplayer.goodsID[i] == goodsID) {
                return true;
            }
        }
        return false;
    }

    public static byte[] getData(final EquipmentContainer _equipmentPackage, final ExchangePlayer _eplayer) {
        YOYOOutputStream output = new YOYOOutputStream();
        YOYOOutputStream output2 = new YOYOOutputStream();
        try {
            EquipmentInstance[] equipmentDataList = _equipmentPackage.getEquipmentList();
            int num = 0;
            for (int i = 0; i < equipmentDataList.length; ++i) {
                EquipmentInstance instance = equipmentDataList[i];
                if (instance != null && !isShow(_eplayer, i, instance.getInstanceID())) {
                    ++num;
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
            output2.writeByte(equipmentDataList.length);
            output2.writeByte(num);
            output2.writeBytes(output.getBytes());
            output2.flush();
            return output2.getBytes();
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
