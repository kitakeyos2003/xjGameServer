// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.clienthandler;

import hero.npc.Npc;
import hero.player.HeroPlayer;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class InteractiveRequest extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        try {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            int npcID = this.yis.readInt();
            Npc npc = NotPlayerServiceImpl.getInstance().getNpc(npcID);
            if (npc != null) {
                npc.listen(player, this.yis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
