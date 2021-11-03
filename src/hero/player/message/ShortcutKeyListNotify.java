// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import hero.item.Goods;
import hero.share.cd.CDUnit;
import hero.item.Medicament;
import hero.item.detail.EGoodsType;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ShortcutKeyListNotify extends AbsResponseMessage {

    private static Logger log;
    private HeroPlayer player;

    static {
        ShortcutKeyListNotify.log = Logger.getLogger((Class) ShortcutKeyListNotify.class);
    }

    public ShortcutKeyListNotify(final HeroPlayer _player) {
        this.player = _player;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        int[][] shortKey = this.player.getShortcutKeyList();
        for (int j = 0; j < shortKey.length; ++j) {
            this.yos.writeByte(shortKey[j][0]);
            if (shortKey[j][0] > 0) {
                switch (shortKey[j][0]) {
                    case 2: {
                        this.yos.writeInt(shortKey[j][1]);
                        break;
                    }
                    case 1: {
                        this.yos.writeInt(shortKey[j][1]);
                        break;
                    }
                    case 3: {
                        ShortcutKeyListNotify.log.debug((Object) ("shortKey[" + j + "][1]=" + shortKey[j][1]));
                        Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(shortKey[j][1]);
                        ShortcutKeyListNotify.log.debug((Object) ("shortKey goods = " + goods));
                        this.yos.writeInt(goods.getID());
                        this.yos.writeUTF(goods.getName());
                        this.yos.writeShort(goods.getIconID());
                        if (goods.getGoodsType() != EGoodsType.MEDICAMENT) {
                            this.yos.writeInt(0);
                            break;
                        }
                        Medicament medicament = (Medicament) goods;
                        CDUnit cd = this.player.userCDMap.get(medicament.getPublicCdVariable());
                        this.yos.writeInt(medicament.getMaxCdTime());
                        if (medicament.getMaxCdTime() != 0) {
                            if (cd == null) {
                                this.yos.writeInt(0);
                            } else {
                                this.yos.writeInt(cd.getTimeBySec());
                            }
                            this.yos.writeShort(medicament.getPublicCdVariable());
                            break;
                        }
                        break;
                    }
                }
            }
        }
    }
}
