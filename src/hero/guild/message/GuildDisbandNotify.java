// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GuildDisbandNotify extends AbsResponseMessage {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
    }
}
