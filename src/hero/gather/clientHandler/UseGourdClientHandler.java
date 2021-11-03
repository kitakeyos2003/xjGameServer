// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.clientHandler;

import hero.npc.Monster;
import hero.player.HeroPlayer;
import java.io.IOException;
import hero.gather.service.GatherServerImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class UseGourdClientHandler extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        try {
            int _monsterID = this.yis.readInt();
            HeroPlayer _player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            if (_player != null) {
                Monster _monster = _player.where().getMonster(_monsterID);
                if (_monster != null) {
                    GatherServerImpl.getInstance().useGourd(_player, _monster);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
