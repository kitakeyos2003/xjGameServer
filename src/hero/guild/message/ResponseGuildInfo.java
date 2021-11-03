// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild.message;

import java.io.IOException;
import hero.guild.Guild;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseGuildInfo extends AbsResponseMessage {

    private Guild guild;

    public ResponseGuildInfo(final Guild _guild) {
        this.guild = _guild;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.guild.getName());
        this.yos.writeUTF(this.guild.getPresident().name);
        this.yos.writeByte(this.guild.getGuildLevel());
        this.yos.writeShort(this.guild.getMemberList().size());
    }
}
