// 
// Decompiled by Procyon v0.5.36
// 
package hero.novice.clienthandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class ExitNoviceWizard extends AbsClientProcess {

    private static Logger log;

    static {
        ExitNoviceWizard.log = Logger.getLogger((Class) ExitNoviceWizard.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte tag = this.yis.readByte();
    }
}
