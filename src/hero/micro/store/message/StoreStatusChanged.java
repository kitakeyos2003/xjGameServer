// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.store.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class StoreStatusChanged extends AbsResponseMessage {

    private int storeOwnerID;
    private boolean statusAtNow;
    private String storeName;

    public StoreStatusChanged(final int _storeOwnerID, final boolean _statusAtNow, final String _storeName) {
        this.storeOwnerID = _storeOwnerID;
        this.statusAtNow = _statusAtNow;
        this.storeName = _storeName;
    }

    public StoreStatusChanged(final int _storeOwnerID, final boolean _statusAtNow) {
        this.storeOwnerID = _storeOwnerID;
        this.statusAtNow = _statusAtNow;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.storeOwnerID);
        this.yos.writeByte(this.statusAtNow);
        if (this.statusAtNow) {
            this.yos.writeUTF(this.storeName);
        }
    }
}
