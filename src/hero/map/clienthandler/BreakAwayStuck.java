// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.clienthandler;

import hero.map.Map;
import hero.player.HeroPlayer;
import hero.group.service.GroupServiceImpl;
import hero.map.message.PlayerRefreshNotify;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseMapBottomData;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.message.DisappearNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class BreakAwayStuck extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        Map map = player.where();
        player.setCellX(map.getBornX());
        player.setCellY(map.getBornY());
        MapSynchronousInfoBroadcast.getInstance().put(map, new DisappearNotify(player.getObjectType().value(), player.getID(), player.getHp(), player.getBaseProperty().getHpMax(), player.getMp(), player.getBaseProperty().getMpMax()), false, 0);
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapBottomData(player, map, map));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapGameObjectList(player.getLoginInfo().clientType, map));
        MapSynchronousInfoBroadcast.getInstance().put(map, new PlayerRefreshNotify(player), true, player.getID());
        GroupServiceImpl.getInstance().groupMemberListHpMpNotify(player);
    }
}
