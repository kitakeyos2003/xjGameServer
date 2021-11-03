// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseQueryResult extends AbsResponseMessage {

    private String result;

    public ResponseQueryResult(final String result) {
        this.result = result;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.result);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
