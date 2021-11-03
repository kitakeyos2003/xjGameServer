// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import hero.item.Goods;
import java.util.Iterator;
import java.io.IOException;
import hero.item.Armor;
import hero.item.Weapon;
import hero.item.Equipment;
import hero.item.dictionary.GoodsContents;
import hero.npc.function.system.auction.AuctionType;
import yoyo.tools.YOYOOutputStream;
import hero.npc.function.system.auction.AuctionGoods;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class UI_AuctionGoodsList {

    private static Logger log;

    static {
        UI_AuctionGoodsList.log = Logger.getLogger((Class) UI_AuctionGoodsList.class);
    }

    public static byte[] getBytes(final short _pageNum, final ArrayList<AuctionGoods> _auctionGoodsList, final String[] _menuList) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeShort(_pageNum);
            output.writeByte(_auctionGoodsList.size());
            for (final AuctionGoods auctionGoods : _auctionGoodsList) {
                output.writeInt(auctionGoods.getAuctionID());
                if (auctionGoods.getAuctionType() == AuctionType.MATERIAL || auctionGoods.getAuctionType() == AuctionType.MEDICAMENT || auctionGoods.getAuctionType() == AuctionType.SPECIAL) {
                    Goods goods = GoodsContents.getGoods(auctionGoods.getGoodsID());
                    if (goods == null) {
                        UI_AuctionGoodsList.log.info((Object) ("\u836f\u6c34\u4e3a\u7a7a\uff1a" + auctionGoods.getGoodsID()));
                    }
                    output.writeShort(goods.getIconID());
                    output.writeUTF(goods.getName());
                    output.writeShort(auctionGoods.getNum());
                    output.writeInt(auctionGoods.getPrice());
                    output.writeByte(3);
                    output.writeUTF(goods.getDescription());
                    output.writeByte(goods.getTrait().value());
                    output.writeShort(goods.getNeedLevel());
                } else {
                    Equipment e = (Equipment) auctionGoods.getInstance().getArchetype();
                    output.writeShort(e.getIconID());
                    StringBuffer name = new StringBuffer();
                    name.append(e.getName());
                    int level = auctionGoods.getInstance().getGeneralEnhance().getLevel();
                    if (level > 0) {
                        name.append("+");
                        name.append(level);
                    }
                    int flash = auctionGoods.getInstance().getGeneralEnhance().getFlash();
                    if (flash > 0) {
                        name.append("(\u95ea");
                        name.append(flash);
                        name.append(")");
                    }
                    output.writeUTF(name.toString());
                    output.writeShort(1);
                    output.writeInt(auctionGoods.getPrice());
                    if (e instanceof Weapon) {
                        output.writeByte(1);
                        output.writeBytes(e.getFixPropertyBytes());
                        output.writeByte(0);
                        output.writeByte(auctionGoods.getInstance().existSeal());
                        output.writeShort(auctionGoods.getInstance().getCurrentDurabilityPoint());
                        output.writeInt(e.getRetrievePrice());
                        output.writeUTF(auctionGoods.getInstance().getGeneralEnhance().getUpEndString());
                        output.writeShort(auctionGoods.getInstance().getGeneralEnhance().getFlashView()[0]);
                        output.writeShort(auctionGoods.getInstance().getGeneralEnhance().getFlashView()[1]);
                        output.writeByte(auctionGoods.getInstance().getGeneralEnhance().detail.length);
                        for (int j = 0; j < auctionGoods.getInstance().getGeneralEnhance().detail.length; ++j) {
                            if (auctionGoods.getInstance().getGeneralEnhance().detail[j][0] == 1) {
                                output.writeByte(auctionGoods.getInstance().getGeneralEnhance().detail[j][1] + 1);
                            } else {
                                output.writeByte(0);
                            }
                        }
                    } else {
                        if (!(e instanceof Armor)) {
                            continue;
                        }
                        output.writeByte(2);
                        output.writeBytes(e.getFixPropertyBytes());
                        output.writeByte(0);
                        output.writeByte(e.existSeal());
                        output.writeShort(auctionGoods.getInstance().getCurrentDurabilityPoint());
                        output.writeInt(e.getRetrievePrice());
                        output.writeUTF(auctionGoods.getInstance().getGeneralEnhance().getUpEndString());
                        output.writeShort(auctionGoods.getInstance().getGeneralEnhance().getArmorFlashView()[0]);
                        output.writeShort(auctionGoods.getInstance().getGeneralEnhance().getArmorFlashView()[1]);
                        output.writeByte(auctionGoods.getInstance().getGeneralEnhance().detail.length);
                        for (int j = 0; j < auctionGoods.getInstance().getGeneralEnhance().detail.length; ++j) {
                            if (auctionGoods.getInstance().getGeneralEnhance().detail[j][0] == 1) {
                                output.writeByte(auctionGoods.getInstance().getGeneralEnhance().detail[j][1] + 1);
                            } else {
                                output.writeByte(0);
                            }
                        }
                    }
                }
            }
            output.writeByte(_menuList.length);
            for (final String menu : _menuList) {
                output.writeUTF(menu);
                output.writeByte(0);
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

    public static EUIType getType() {
        return EUIType.AUCTION_GOODS_LIST;
    }
}
