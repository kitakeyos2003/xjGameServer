// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseMapDesc extends AbsResponseMessage {

    private String desc;
    private short mapID;

    public ResponseMapDesc(final String desc, final short mapID) {
        this.desc = desc;
        this.mapID = mapID;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeShort(this.mapID);
        this.yos.writeUTF(this.desc);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
