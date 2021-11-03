// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import hero.item.Goods;
import hero.item.detail.EGoodsType;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class HotKeySumByMedicament extends AbsResponseMessage {

    private HeroPlayer player;
    private byte page;
    private byte index;
    private int goodsID;
    private short count;
    private byte group;
    private boolean isUpdate;

    public boolean haveRelation(final int _goodsID) {
        boolean result = false;
        if (this.isUpdate) {
            result = true;
        }
        return result;
    }

    public HotKeySumByMedicament(final HeroPlayer _player, final int _changeGoodsID) {
        this.player = _player;
        this.page = 1;
        this.isUpdate = false;
        int[][] shortKey = this.player.getShortcutKeyList();
        for (int j = 0; j < shortKey.length; ++j) {
            if (shortKey[j][0] > 0 && shortKey[j][0] == 3) {
                Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(shortKey[j][1]);
                if (goods.getGoodsType() == EGoodsType.MEDICAMENT || goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                    if (_changeGoodsID == shortKey[j][1]) {
                        this.isUpdate = true;
                    }
                    ++this.group;
                }
            }
        }
    }

    public HotKeySumByMedicament(final HeroPlayer _player) {
        this.player = _player;
        this.page = 1;
        int[][] shortKey = this.player.getShortcutKeyList();
        for (int j = 0; j < shortKey.length; ++j) {
            if (shortKey[j][0] > 0 && shortKey[j][0] == 3) {
                Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(shortKey[j][1]);
                if (goods.getGoodsType() == EGoodsType.MEDICAMENT || goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                    ++this.group;
                }
            }
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        int[][] shortKey = this.player.getShortcutKeyList();
        this.yos.writeByte(this.group);
        if (this.group > 0) {
            for (int j = 0; j < shortKey.length; ++j) {
                if (shortKey[j][0] > 0 && shortKey[j][0] == 3) {
                    Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(shortKey[j][1]);
                    if (goods.getGoodsType() == EGoodsType.MEDICAMENT) {
                        this.count = (short) this.player.getInventory().getMedicamentBag().getGoodsNumber(shortKey[j][1]);
                        this.index = (byte) j;
                        this.goodsID = shortKey[j][1];
                        this.yos.writeByte(this.page);
                        this.yos.writeByte(this.index);
                        this.yos.writeInt(this.goodsID);
                        this.yos.writeShort(this.count);
                    } else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                        this.count = (short) this.player.getInventory().getSpecialGoodsBag().getGoodsNumber(shortKey[j][1]);
                        this.index = (byte) j;
                        this.goodsID = shortKey[j][1];
                        this.yos.writeByte(this.page);
                        this.yos.writeByte(this.index);
                        this.yos.writeInt(this.goodsID);
                        this.yos.writeShort(this.count);
                    }
                }
            }
        }
    }
}
