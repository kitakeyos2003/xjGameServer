// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ResultFeeTip extends AbsResponseMessage {

    private String tip;

    public ResultFeeTip(final String _tip) {
        this.tip = _tip;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.tip);
    }
}
