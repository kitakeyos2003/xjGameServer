// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import hero.share.exchange.ExchangePlayer;
import yoyo.core.packet.AbsResponseMessage;

public class ExchangeLockedGoodsList extends AbsResponseMessage {

    private ExchangePlayer eplayer;

    public ExchangeLockedGoodsList(final ExchangePlayer _eplayer) {
        this.eplayer = _eplayer;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.eplayer.nickname);
        this.yos.writeInt(this.eplayer.money);
        int size = this.eplayer.goodsID.length;
        this.yos.writeInt(size);
        for (int i = 0; i < size; ++i) {
            this.yos.writeShort(this.eplayer.gridIndex[i]);
            this.yos.writeInt(this.eplayer.goodsID[i]);
            this.yos.writeShort(this.eplayer.goodsNum[i]);
            this.yos.writeByte(this.eplayer.goodsType[i]);
        }
    }
}
