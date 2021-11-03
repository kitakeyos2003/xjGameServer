// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class NotifyPopEnhanceUI extends AbsResponseMessage {

    private int crystalID;
    private int crystalLocationOfBag;

    public NotifyPopEnhanceUI(final int _crystalID, final int _location) {
        this.crystalID = _crystalID;
        this.crystalLocationOfBag = _location;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.crystalID);
        this.yos.writeByte(this.crystalLocationOfBag);
    }
}
