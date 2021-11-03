// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseExchangeGoodsList extends AbsResponseMessage {

    private byte goodsType;
    private byte[] data;

    public ResponseExchangeGoodsList(final byte _goodsType, final byte[] _data) {
        this.goodsType = _goodsType;
        this.data = _data;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.goodsType);
        this.yos.writeBytes(this.data);
    }
}
