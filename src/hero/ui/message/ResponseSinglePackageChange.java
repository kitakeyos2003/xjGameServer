// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseSinglePackageChange extends AbsResponseMessage {

    private byte uiType;
    private short[] change;

    public ResponseSinglePackageChange(final byte _uiType, final short[] _change) {
        this.uiType = _uiType;
        this.change = _change;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.uiType);
        this.yos.writeByte(this.change[0]);
        this.yos.writeShort(this.change[1]);
    }
}
