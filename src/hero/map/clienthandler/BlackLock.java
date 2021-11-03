// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.clienthandler;

import hero.map.Map;
import hero.player.HeroPlayer;
import hero.map.message.ResponseMapGameObjectList;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.message.ResponseMapBottomData;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.define.EClan;
import hero.map.service.MapServiceImpl;
import hero.map.service.MapConfig;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class BlackLock extends AbsClientProcess {

    private static Logger log;

    static {
        BlackLock.log = Logger.getLogger((Class) BlackLock.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        short where = this.yis.readShort();
        Map currentMap = player.where();
        Map targetMap = null;
        if (currentMap.getID() != where) {
            BlackLock.log.info((Object) "warn:\u5ba2\u6237\u7aef\u8bb0\u5f55\u81ea\u5df1\u6240\u5728\u5730\u56fe\u548c\u670d\u52a1\u5668\u8bb0\u5f55\u73a9\u5bb6\u6240\u5728\u5730\u56fe\u4e0d\u7b26:");
            BlackLock.log.info((Object) ("client in map=" + where));
            BlackLock.log.info((Object) ("server in map=" + currentMap.getID()));
        }
        if (MapServiceImpl.getInstance().getConfig().use_default_map) {
            if (player.getClan() == EClan.LONG_SHAN) {
                targetMap = MapServiceImpl.getInstance().getNormalMapByID(MapServiceImpl.getInstance().getConfig().break_lock_default_long_map);
            } else {
                targetMap = MapServiceImpl.getInstance().getNormalMapByID(MapServiceImpl.getInstance().getConfig().break_lock_default_mo_map);
            }
        } else {
            if (player.getClan() == EClan.LONG_SHAN) {
                targetMap = MapServiceImpl.getInstance().getNormalMapByID(currentMap.getTargetMapIDAfterDie());
            }
            if (player.getClan() == EClan.HE_MU_DU) {
                short mapid = currentMap.getMozuTargetMapIDAfterDie();
                BlackLock.log.debug((Object) ("curr mapid=" + mapid + "\uff0c\u9b54\u65cfmapid=" + mapid));
                targetMap = MapServiceImpl.getInstance().getNormalMapByID(mapid);
            }
        }
        if (targetMap != null) {
            player.setCellX(currentMap.getBornX());
            player.setCellY(currentMap.getBornY());
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapBottomData(player, currentMap, currentMap));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapGameObjectList(player.getLoginInfo().clientType, currentMap));
            player.gotoMap(currentMap);
        }
    }
}
