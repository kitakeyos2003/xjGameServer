// 
// Decompiled by Procyon v0.5.36
// 
package hero.group.message;

import java.io.IOException;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class OthersHpMpNotify extends AbsResponseMessage {

    private HeroPlayer player;

    public OthersHpMpNotify(final HeroPlayer player) {
        this.player = player;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.player.getUserID());
        this.yos.writeInt(this.player.getHp());
        this.yos.writeInt(this.player.getBaseProperty().getHpMax());
        this.yos.writeInt(this.player.getMp());
        this.yos.writeInt(this.player.getBaseProperty().getMpMax());
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
