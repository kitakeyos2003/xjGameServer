// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.clienthandler;

import hero.player.HeroPlayer;
import hero.task.service.TaskServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class OptionTaskGear extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            int gearID = this.yis.readInt();
            TaskServiceImpl.getInstance().openGear(player, gearID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
