// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.teach.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ClearConfirmDialog extends AbsResponseMessage {

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(0);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
