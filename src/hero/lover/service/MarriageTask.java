// 
// Decompiled by Procyon v0.5.36
// 
package hero.lover.service;

import hero.npc.function.system.MarryNPC;
import java.util.TimerTask;

public class MarriageTask extends TimerTask {

    private short clan;

    public MarriageTask(final short _clan) {
        this.clan = _clan;
    }

    @Override
    public void run() {
        LoverServiceImpl.getInstance().canMarry = true;
        MarryNPC.removeAllPlayer(this.clan);
    }
}
