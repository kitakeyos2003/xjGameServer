// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GuildChangeNotify extends AbsResponseMessage {

    private int changerObjectID;
    private byte changeReason;
    private String guildName;
    private static final byte CHANGER_OF_JOIN = 1;
    private static final byte CHANGER_OF_LEFT = 2;

    public GuildChangeNotify(final int _objectID, final String _guildName) {
        this.changerObjectID = _objectID;
        this.changeReason = 1;
        this.guildName = _guildName;
    }

    public GuildChangeNotify(final int _objectID) {
        this.changerObjectID = _objectID;
        this.changeReason = 2;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.changerObjectID);
        this.yos.writeByte(this.changeReason);
        if (1 == this.changeReason) {
            this.yos.writeUTF(this.guildName);
        }
    }
}
