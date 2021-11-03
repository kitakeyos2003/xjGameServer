// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class UpgradeBagAnswer extends AbsResponseMessage {

    public static final byte SEND_TO_CHARGE = 0;
    private String tip;
    private byte type;

    public UpgradeBagAnswer(final String _tip, final int _type) {
        this.tip = _tip;
        this.type = (byte) _type;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.type);
        this.yos.writeUTF(this.tip);
    }
}
