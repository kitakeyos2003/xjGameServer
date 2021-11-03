// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseIndexNoticeContent extends AbsResponseMessage {

    private String content;

    public ResponseIndexNoticeContent(final String content) {
        this.content = content;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.content);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
