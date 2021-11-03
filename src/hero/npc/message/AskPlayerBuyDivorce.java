// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class AskPlayerBuyDivorce extends AbsResponseMessage {

    private byte type;
    private String content;

    public AskPlayerBuyDivorce(final byte _type, final String _content) {
        this.content = _content;
        this.type = _type;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.type);
        this.yos.writeUTF(this.content);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
