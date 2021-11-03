// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon.clientHandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class PlayerReplayDungeonPattern extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        short mapID = this.yis.readShort();
        short targetX = this.yis.readShort();
        short targetY = this.yis.readShort();
        byte replay = this.yis.readByte();
    }
}
