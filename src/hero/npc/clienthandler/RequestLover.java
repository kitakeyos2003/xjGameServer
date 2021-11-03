// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.clienthandler;

import hero.player.HeroPlayer;
import hero.npc.function.system.MarryNPC;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RequestLover extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte type = this.yis.readByte();
        String otherName = this.yis.readUTF();
        if (type == 1) {
            MarryNPC.propose(player, otherName);
        }
        if (type == 2) {
            MarryNPC.breakUp(player, otherName);
        }
    }
}
