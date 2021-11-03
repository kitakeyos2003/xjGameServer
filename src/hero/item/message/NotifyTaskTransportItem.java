// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class NotifyTaskTransportItem extends AbsResponseMessage {

    private int objectID;
    private int locationOfBag;

    public NotifyTaskTransportItem(final int _objectID, final int _locationOfBag) {
        this.objectID = _objectID;
        this.locationOfBag = _locationOfBag;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.objectID);
        this.yos.writeByte(this.locationOfBag);
    }
}
