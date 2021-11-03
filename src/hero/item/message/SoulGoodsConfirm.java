// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class SoulGoodsConfirm extends AbsResponseMessage {

    private byte operateType;
    private int locationOfBag;
    public static final byte TYPE_OF_MARK = 1;
    public static final byte TYPE_OF_CHANNEL = 2;

    public SoulGoodsConfirm(final byte _operateType, final int _locationOfBag) {
        this.operateType = _operateType;
        this.locationOfBag = _locationOfBag;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.operateType);
        this.yos.writeByte(this.locationOfBag);
    }
}
