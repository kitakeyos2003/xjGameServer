// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.clienthandler;

import hero.player.HeroPlayer;
import hero.skill.service.SkillServiceImpl;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class UpSkill extends AbsClientProcess {

    private static Logger log;

    static {
        UpSkill.log = Logger.getLogger((Class) UpSkill.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        if (player == null) {
            UpSkill.log.info((Object) "error: UpSkill.java -- \u73a9\u5bb6\u5bf9\u8c61\u4e3anull");
            return;
        }
        byte what = this.yis.readByte();
        if (what == 1) {
            int skillId = this.yis.readInt();
            SkillServiceImpl.getInstance().learnSkill(player, skillId);
        } else {
            SkillServiceImpl.getInstance().forgetSkill(player);
        }
    }
}
