// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ErrorResponse extends AbsResponseMessage {

    private String context;

    public ErrorResponse(final String context) {
        this.context = context;
    }

    @Override
    public int getPriority() {
        return Priority.REAL_TIME.getValue();
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.context);
    }
}
