// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class MemberRankChangeNotify extends AbsResponseMessage {

    private byte rankValue;

    public MemberRankChangeNotify(final byte _rankValue) {
        this.rankValue = _rankValue;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.rankValue);
    }
}
