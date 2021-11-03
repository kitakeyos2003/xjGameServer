// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.store.message;

import java.io.IOException;
import hero.item.SpecialGoods;
import hero.item.Weapon;
import hero.item.detail.EGoodsType;
import hero.micro.store.PersionalStore;
import yoyo.core.packet.AbsResponseMessage;

public class OtherStoreGoodsList extends AbsResponseMessage {

    private PersionalStore store;

    public OtherStoreGoodsList(final PersionalStore _store) {
        this.store = _store;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.store.goodsNumber);
        this.yos.writeUTF(this.store.name);
        PersionalStore.GoodsForSale[] goodsList = this.store.goodsList;
        for (int i = 0; i < 16; ++i) {
            PersionalStore.GoodsForSale goods = goodsList[i];
            if (goods != null) {
                this.yos.writeByte(goods.goodsType);
                this.yos.writeByte(i);
                if (goods.goodsType == EGoodsType.EQUIPMENT.value()) {
                    this.yos.writeInt(goods.equipment.getInstanceID());
                    this.yos.writeShort(goods.equipment.getArchetype().getIconID());
                    StringBuffer name = new StringBuffer();
                    name.append(goods.equipment.getArchetype().getName());
                    int level = goods.equipment.getGeneralEnhance().getLevel();
                    if (level > 0) {
                        name.append("+");
                        name.append(level);
                    }
                    int flash = goods.equipment.getGeneralEnhance().getFlash();
                    if (flash > 0) {
                        name.append("(\u95ea");
                        name.append(flash);
                        name.append(")");
                    }
                    this.yos.writeUTF(name.toString());
                    if (goods.equipment.getArchetype() instanceof Weapon) {
                        this.yos.writeByte(1);
                        this.yos.writeBytes(goods.equipment.getArchetype().getFixPropertyBytes());
                        this.yos.writeByte(goods.equipment.isBind());
                        this.yos.writeByte(goods.equipment.existSeal());
                        this.yos.writeShort(goods.equipment.getCurrentDurabilityPoint());
                        this.yos.writeInt(goods.equipment.getArchetype().getRetrievePrice());
                        this.yos.writeUTF(goods.equipment.getGeneralEnhance().getUpEndString());
                        this.yos.writeShort(goods.equipment.getGeneralEnhance().getFlashView()[0]);
                        this.yos.writeShort(goods.equipment.getGeneralEnhance().getFlashView()[1]);
                        this.yos.writeByte(goods.equipment.getGeneralEnhance().detail.length);
                        for (int j = 0; j < goods.equipment.getGeneralEnhance().detail.length; ++j) {
                            if (goods.equipment.getGeneralEnhance().detail[j][0] == 1) {
                                this.yos.writeByte(goods.equipment.getGeneralEnhance().detail[j][1] + 1);
                            } else {
                                this.yos.writeByte(0);
                            }
                        }
                    } else {
                        this.yos.writeByte(2);
                        this.yos.writeBytes(goods.equipment.getArchetype().getFixPropertyBytes());
                        this.yos.writeByte(goods.equipment.isBind());
                        this.yos.writeByte(goods.equipment.existSeal());
                        this.yos.writeShort(goods.equipment.getCurrentDurabilityPoint());
                        this.yos.writeInt(goods.equipment.getArchetype().getRetrievePrice());
                        this.yos.writeUTF(goods.equipment.getGeneralEnhance().getUpEndString());
                        this.yos.writeShort(goods.equipment.getGeneralEnhance().getArmorFlashView()[0]);
                        this.yos.writeShort(goods.equipment.getGeneralEnhance().getArmorFlashView()[1]);
                        this.yos.writeByte(goods.equipment.getGeneralEnhance().detail.length);
                        for (int j = 0; j < goods.equipment.getGeneralEnhance().detail.length; ++j) {
                            if (goods.equipment.getGeneralEnhance().detail[j][0] == 1) {
                                this.yos.writeByte(goods.equipment.getGeneralEnhance().detail[j][1] + 1);
                            } else {
                                this.yos.writeByte(0);
                            }
                        }
                    }
                } else {
                    this.yos.writeInt(goods.singleGoods.getID());
                    this.yos.writeShort(goods.singleGoods.getIconID());
                    this.yos.writeUTF(goods.singleGoods.getName());
                    this.yos.writeByte(goods.singleGoods.getTrait().value());
                    this.yos.writeShort(goods.number);
                    this.yos.writeByte(goods.number);
                    this.yos.writeInt(goods.singleGoods.getRetrievePrice());
                    this.yos.writeShort(goods.singleGoods.getNeedLevel());
                    if (goods.singleGoods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                        if (((SpecialGoods) goods.singleGoods).canBeSell()) {
                            this.yos.writeUTF(String.valueOf(goods.singleGoods.getDescription()) + "\n" + "\u51fa\u552e\u4ef7\u683c\uff1a" + goods.singleGoods.getRetrievePrice());
                        } else {
                            this.yos.writeUTF(String.valueOf(goods.singleGoods.getDescription()) + "\n" + "\u4e0d\u53ef\u51fa\u552e");
                        }
                    } else {
                        this.yos.writeUTF(String.valueOf(goods.singleGoods.getDescription()) + "\n" + "\u51fa\u552e\u4ef7\u683c\uff1a" + goods.singleGoods.getRetrievePrice());
                    }
                    this.yos.writeByte(goods.singleGoods.exchangeable() ? 1 : 0);
                }
                this.yos.writeInt(goods.salePrice);
            }
        }
    }
}
