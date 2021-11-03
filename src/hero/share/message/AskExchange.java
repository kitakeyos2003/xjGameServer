// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class AskExchange extends AbsResponseMessage {

    private HeroPlayer player;
    private HeroPlayer other;

    public AskExchange(final HeroPlayer player, final HeroPlayer other) {
        this.player = player;
        this.other = other;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.player.getUserID());
        this.yos.writeInt(this.other.getUserID());
        this.yos.writeUTF(String.valueOf(this.player.getName()) + " \u8bf7\u6c42\u548c\u4f60\u4ea4\u6613\uff0c\u662f\u5426\u540c\u610f\uff1f");
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
