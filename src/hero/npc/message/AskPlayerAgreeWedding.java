// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class AskPlayerAgreeWedding extends AbsResponseMessage {

    private HeroPlayer player;
    private HeroPlayer otherPlayer;
    private String content;
    private byte type;

    public AskPlayerAgreeWedding(final HeroPlayer player, final HeroPlayer otherPlayer, final String content, final byte type) {
        this.player = player;
        this.otherPlayer = otherPlayer;
        this.content = content;
        this.type = type;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.player.getUserID());
        this.yos.writeInt(this.otherPlayer.getUserID());
        this.yos.writeByte(this.type);
        this.yos.writeUTF(this.content);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
