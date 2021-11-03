// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.clienthandler;

import hero.player.HeroPlayer;
import hero.dungeon.service.DungeonServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class AttendMarryReply extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int dungeonID = this.yis.readInt();
        short mapID = this.yis.readShort();
        byte reply = this.yis.readByte();
        if (reply == 1) {
            DungeonServiceImpl.getInstance().gotoMarryDungeonMap(dungeonID, player, mapID);
        }
    }
}
