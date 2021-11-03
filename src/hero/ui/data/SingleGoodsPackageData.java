// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui.data;

import hero.item.Goods;
import hero.item.TaskTool;
import java.io.IOException;
import hero.item.SpecialGoods;
import hero.item.detail.EGoodsType;
import hero.item.special.PetPerCard;
import hero.item.special.BigTonicBall;
import hero.item.service.GoodsServiceImpl;
import hero.item.SingleGoods;
import yoyo.tools.YOYOOutputStream;
import hero.item.bag.SingleGoodsBag;
import org.apache.log4j.Logger;

public class SingleGoodsPackageData {

    private static Logger log;

    static {
        SingleGoodsPackageData.log = Logger.getLogger((Class) SingleGoodsPackageData.class);
    }

    public static byte[] getData(final SingleGoodsBag _singleGoodsPackage, final boolean _viewOperate, final int[][] _shortcutKeyList, final String _tabName) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte((byte) 3);
            int[][] singleGoodsDataList = _singleGoodsPackage.getAllItem();
            SingleGoodsPackageData.log.debug((Object) ("singleGoodsDataList size = " + singleGoodsDataList.length));
            output.writeUTF(_tabName);
            output.writeByte(singleGoodsDataList.length);
            output.writeByte(_singleGoodsPackage.getFullGridNumber());
            for (int i = 0; i < singleGoodsDataList.length; ++i) {
                int[] goodsData = singleGoodsDataList[i];
                if (goodsData[0] != 0) {
                    SingleGoods goods = (SingleGoods) GoodsServiceImpl.getInstance().getGoodsByID(goodsData[0]);
                    if (goods instanceof BigTonicBall) {
                        goods = _singleGoodsPackage.tonicList.get(i);
                    }
                    if (goods instanceof PetPerCard) {
                        goods = _singleGoodsPackage.petPerCardList.get(i);
                    }
                    if (goods != null) {
                        SingleGoodsPackageData.log.debug((Object) ("goodsname = " + goods.getName() + ",goodsid=" + goods.getID()));
                        output.writeByte(i);
                        output.writeInt(goods.getID());
                        output.writeShort(goods.getIconID());
                        output.writeUTF(goods.getName());
                        output.writeByte(goods.getTrait().value());
                        output.writeShort(goodsData[1]);
                        output.writeByte(goodsData[1]);
                        output.writeInt(goods.getRetrievePrice());
                        SingleGoodsPackageData.log.debug((Object) ("goods.trait=" + goods.getTrait().value() + ",num=" + goodsData[1] + ",price=" + goods.getRetrievePrice()));
                        int level = 1;
                        if (goods.getNeedLevel() > 1) {
                            level = goods.getNeedLevel();
                        }
                        output.writeShort(level);
                        SingleGoodsPackageData.log.debug((Object) ("goods level=" + level));
                        if (goods.getGoodsType() == EGoodsType.TASK_TOOL) {
                            output.writeUTF(goods.getDescription());
                            output.writeByte(goods.exchangeable() ? 1 : 0);
                        } else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS || goods.getGoodsType() == EGoodsType.PET_GOODS) {
                            if (((SpecialGoods) goods).canBeSell()) {
                                output.writeUTF(String.valueOf(goods.getDescription()) + "\n" + "\u51fa\u552e\u4ef7\u683c\uff1a" + goods.getRetrievePrice());
                            } else {
                                output.writeUTF(String.valueOf(goods.getDescription()) + "\n" + "\u4e0d\u53ef\u51fa\u552e");
                            }
                            output.writeByte(goods.exchangeable() ? 1 : 0);
                        } else {
                            output.writeUTF(String.valueOf(goods.getDescription()) + "\n" + "\u51fa\u552e\u4ef7\u683c\uff1a" + goods.getRetrievePrice());
                            output.writeByte(goods.exchangeable() ? 1 : 0);
                        }
                        if (goods.useable() || goods.getGoodsType() == EGoodsType.MEDICAMENT) {
                            output.writeByte(1);
                        } else {
                            output.writeByte(0);
                        }
                        int shortcutKey = -1;
                        if ((goods.getGoodsType() == EGoodsType.MEDICAMENT || goods.getGoodsType() == EGoodsType.TASK_TOOL || goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) && _viewOperate && _shortcutKeyList != null) {
                            for (int j = 0; j < _shortcutKeyList.length; ++j) {
                                if (_shortcutKeyList[j][0] == 3 && _shortcutKeyList[j][1] == goods.getID()) {
                                    shortcutKey = j;
                                    break;
                                }
                            }
                        }
                        output.writeByte(shortcutKey);
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

    public static byte[] getStorageData(final SingleGoodsBag _singleGoodsPackage, final boolean _viewOperate, final int[][] _shortcutKeyList) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte((byte) 3);
            int[][] singleGoodsDataList = _singleGoodsPackage.getAllItem();
            output.writeByte(singleGoodsDataList.length);
            output.writeByte(_singleGoodsPackage.getFullGridNumber());
            for (int i = 0; i < singleGoodsDataList.length; ++i) {
                int[] goodsData = singleGoodsDataList[i];
                if (goodsData[0] != 0) {
                    Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsData[0]);
                    if (goods != null) {
                        output.writeByte(i);
                        output.writeInt(goods.getID());
                        output.writeShort(goods.getIconID());
                        output.writeUTF(goods.getName());
                        output.writeByte(goods.getTrait().value());
                        output.writeShort(goodsData[1]);
                        output.writeByte(goodsData[1]);
                        byte useable = 0;
                        if (goods.getGoodsType() == EGoodsType.TASK_TOOL) {
                            output.writeUTF(goods.getDescription());
                            output.writeByte(1);
                            if (((TaskTool) goods).useable()) {
                                useable = 1;
                            }
                        } else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                            if (((SpecialGoods) goods).canBeSell()) {
                                output.writeUTF(String.valueOf(goods.getDescription()) + "\n" + "\u51fa\u552e\u4ef7\u683c\uff1a" + goods.getRetrievePrice());
                            } else {
                                output.writeUTF(String.valueOf(goods.getDescription()) + "\n" + "\u4e0d\u53ef\u51fa\u552e");
                            }
                            output.writeByte(1);
                            if (((SpecialGoods) goods).useable()) {
                                useable = 1;
                            }
                        } else {
                            output.writeUTF(String.valueOf(goods.getDescription()) + "\n" + "\u51fa\u552e\u4ef7\u683c\uff1a" + goods.getRetrievePrice());
                            output.writeByte(1);
                        }
                        output.writeByte(useable);
                        int shortcutKey = -1;
                        if ((goods.getGoodsType() == EGoodsType.MEDICAMENT || goods.getGoodsType() == EGoodsType.TASK_TOOL) && _viewOperate) {
                            for (int j = 0; j < _shortcutKeyList.length; ++j) {
                                if (_shortcutKeyList[j][1] == goods.getID()) {
                                    shortcutKey = j;
                                    break;
                                }
                            }
                        }
                        output.writeByte(shortcutKey);
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
