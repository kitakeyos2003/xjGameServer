// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.clienthandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class GmQuestionHandler extends AbsClientProcess {

    protected int getPriority() {
        return 0;
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte type = this.yis.readByte();
        String content = this.yis.readUTF();
    }
}
