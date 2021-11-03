// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class FullScreenTip extends AbsResponseMessage {

    private String title;
    private String content;

    public FullScreenTip(final String _title, final String _content) {
        this.title = _title;
        this.content = _content;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.title);
        this.yos.writeUTF(this.content);
    }
}
