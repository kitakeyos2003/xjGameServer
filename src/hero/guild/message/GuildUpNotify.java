// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GuildUpNotify extends AbsResponseMessage {

    private int guildLevel;
    private int guildSize;

    public GuildUpNotify(final int _guildLevel, final int _guildSize) {
        this.guildLevel = _guildLevel;
        this.guildSize = _guildSize;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.guildLevel);
        this.yos.writeShort(this.guildSize);
    }
}
