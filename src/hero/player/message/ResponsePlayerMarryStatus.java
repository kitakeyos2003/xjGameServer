// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class ResponsePlayerMarryStatus extends AbsResponseMessage {

    private HeroPlayer player;

    public ResponsePlayerMarryStatus(final HeroPlayer _player) {
        this.player = _player;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.player.getUserID());
        this.yos.writeUTF(this.player.spouse);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
