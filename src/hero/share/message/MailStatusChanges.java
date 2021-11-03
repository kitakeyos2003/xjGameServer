// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class MailStatusChanges extends AbsResponseMessage {

    private byte type;
    private boolean existsMark;
    public static byte TYPE_OF_LETTER;
    public static byte TYPE_OF_POST_BOX;

    static {
        MailStatusChanges.TYPE_OF_LETTER = 1;
        MailStatusChanges.TYPE_OF_POST_BOX = 2;
    }

    public MailStatusChanges(final byte _type, final boolean _existsMark) {
        this.type = _type;
        this.existsMark = _existsMark;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.type);
        this.yos.writeByte(this.existsMark);
    }
}
