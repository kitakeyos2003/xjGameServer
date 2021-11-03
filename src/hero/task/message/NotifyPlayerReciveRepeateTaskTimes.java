// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.message;

import java.io.IOException;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class NotifyPlayerReciveRepeateTaskTimes extends AbsResponseMessage {

    private HeroPlayer player;

    public NotifyPlayerReciveRepeateTaskTimes(final HeroPlayer player) {
        this.player = player;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.player.receivedRepeateTaskTimes);
        this.yos.writeInt(this.player.getCanReceiveRepeateTaskTimes());
    }
}
