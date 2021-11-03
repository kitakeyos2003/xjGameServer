// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseInlayHeavenBook extends AbsResponseMessage {

    private int bookID;
    private byte position;
    private boolean success;
    private boolean allSame;

    public ResponseInlayHeavenBook(final int bookID, final byte position, final boolean success, final boolean allSame) {
        this.bookID = bookID;
        this.position = position;
        this.success = success;
        this.allSame = allSame;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.bookID);
        this.yos.writeByte(this.position);
        this.yos.writeByte(this.success);
        this.yos.writeByte(this.allSame);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
