// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class NotifyListItemMessage extends AbsResponseMessage {

    private byte step;
    private boolean isIndex;
    private int itemID;

    public NotifyListItemMessage(final byte _step, final boolean _isIndex, final int _itemID) {
        this.step = _step;
        this.isIndex = _isIndex;
        this.itemID = _itemID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.step);
        this.yos.writeByte(this.isIndex ? 0 : 1);
        this.yos.writeInt(this.itemID);
    }
}
