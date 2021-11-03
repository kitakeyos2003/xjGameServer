// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseInitializeItemData extends AbsResponseMessage {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        int[] perforateMoney = GoodsServiceImpl.getInstance().getConfig().perforate_money_list;
        this.yos.writeByte(perforateMoney.length);
        for (int i = 0; i < perforateMoney.length; ++i) {
            this.yos.writeInt(perforateMoney[i]);
        }
        int[] enhanceMoney = GoodsServiceImpl.getInstance().getConfig().enhance_money_list;
        this.yos.writeByte(enhanceMoney.length);
        for (int j = 0; j < enhanceMoney.length; ++j) {
            this.yos.writeInt(enhanceMoney[j]);
        }
        int[] wreckMoney = GoodsServiceImpl.getInstance().getConfig().wreck_money_list;
        this.yos.writeByte(wreckMoney.length);
        for (int k = 0; k < wreckMoney.length; ++k) {
            this.yos.writeInt(wreckMoney[k]);
        }
        this.yos.writeUTF(GoodsServiceImpl.getInstance().getConfig().describe_string);
    }
}
