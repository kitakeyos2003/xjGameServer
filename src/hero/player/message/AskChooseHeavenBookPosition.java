// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class AskChooseHeavenBookPosition extends AbsResponseMessage {

    private HeroPlayer player;
    private int bookID;

    public AskChooseHeavenBookPosition(final HeroPlayer _player, final int bookID) {
        this.player = _player;
        this.bookID = bookID;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.bookID);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
