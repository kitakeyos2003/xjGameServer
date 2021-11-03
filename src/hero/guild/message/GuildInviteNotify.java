// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GuildInviteNotify extends AbsResponseMessage {

    private int invitorUserID;
    private String invitorName;
    private int guildID;
    private String guildName;

    public GuildInviteNotify(final int _invitorUserID, final String _invitorName, final int _guildID, final String _guildName) {
        this.invitorUserID = _invitorUserID;
        this.invitorName = _invitorName;
        this.guildID = _guildID;
        this.guildName = _guildName;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.invitorUserID);
        this.yos.writeUTF(this.invitorName);
        this.yos.writeInt(this.guildID);
        this.yos.writeUTF(this.guildName);
    }
}
