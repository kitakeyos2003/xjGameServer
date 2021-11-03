// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.store.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseBagGoodsList extends AbsResponseMessage {

    private byte[] goodsDataList;

    public ResponseBagGoodsList(final byte[] _goodsDataList) {
        this.goodsDataList = _goodsDataList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeBytes(this.goodsDataList);
    }
}
