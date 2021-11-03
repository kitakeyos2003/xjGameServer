// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.clienthandler;

import hero.npc.Npc;
import hero.effect.service.EffectServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseMapBottomData;
import hero.dungeon.Dungeon;
import hero.map.Map;
import hero.player.HeroPlayer;
import hero.log.service.LogServiceImpl;
import hero.dungeon.service.DungeonServiceImpl;
import hero.map.EMapType;
import hero.map.service.MapServiceImpl;
import hero.map.message.SwitchMapFailNotify;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class SwitchMap extends AbsClientProcess {

    private static Logger log;

    static {
        SwitchMap.log = Logger.getLogger((Class) SwitchMap.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        if (!player.isEnable() || player.isDead()) {
            return;
        }
        if (player.isSelling()) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6446\u644a\u72b6\u6001\u4e2d\u4e0d\u80fd\u8df3\u8f6c\u5730\u56fe"));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SwitchMapFailNotify("\u6446\u644a\u72b6\u6001\u4e2d\u4e0d\u80fd\u8df3\u8f6c\u5730\u56fe"));
            return;
        }
        SwitchMap.log.debug((Object) ("switch map start ... playe current map id = " + player.where().getID()));
        short targetMapID = this.yis.readShort();
        short switchX = this.yis.readShort();
        short switchY = this.yis.readShort();
        SwitchMap.log.debug((Object) ("switch target mapID = " + targetMapID));
        Map currentMap = player.where();
        Map targetMap = null;
        targetMap = MapServiceImpl.getInstance().getNormalMapByID(targetMapID);
        if (targetMap.getMapType() == EMapType.DUNGEON) {
            Dungeon dungeon = DungeonServiceImpl.getInstance().getWhereDungeon(player.getUserID());
            if (dungeon != null) {
                targetMap = dungeon.getMap(targetMapID);
            }
        }
        if (targetMap == null) {
            SwitchMap.log.debug((Object) ("\u4e0d\u5b58\u5728\u7684\u5730\u56fe\uff0cID:" + targetMapID));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SwitchMapFailNotify("\u4e0d\u5b58\u5728\u7684\u5730\u56fe\uff0cID:" + targetMapID));
            return;
        }
        if (currentMap.getID() == targetMap.getID()) {
            SwitchMap.log.debug((Object) ("\u5f53\u524d\u5730\u56fe\u548c\u76ee\u6807\u5730\u56feID\u76f8\u540c,\u5f53\u524d\u5730\u56fe\uff1a" + currentMap.getName() + ",\u76ee\u6807\u5730\u56fe\uff1a" + targetMap.getName()));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SwitchMapFailNotify("\u5f53\u524d\u5730\u56fe\u548c\u76ee\u6807\u5730\u56feID\u76f8\u540c,\u5f53\u524d\u5730\u56fe\uff1a" + currentMap.getName() + ",\u76ee\u6807\u5730\u56fe\uff1a" + targetMap.getName()));
            return;
        }
        this.switchMap(player, currentMap, targetMap, switchX, switchY);
        LogServiceImpl.getInstance().switchMapLog(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), currentMap.getID(), currentMap.getName(), targetMap.getID(), targetMap.getName(), currentMap.getMapType().name(), targetMap.getMapType().name());
        SwitchMap.log.info((Object) "switch map end ...");
    }

    private void switchMap(final HeroPlayer player, final Map currentMap, final Map targetMap, final short switchX, final short switchY) {
        short[] targetMapBornPort = currentMap.getTargetMapPoint(targetMap.getID(), switchX, switchY);
        if (targetMapBornPort != null) {
            player.setCellX(targetMapBornPort[0]);
            player.setCellY(targetMapBornPort[1]);
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapBottomData(player, targetMap, currentMap));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapGameObjectList(player.getLoginInfo().clientType, targetMap));
            SwitchMap.log.debug((Object) ("next player gotoMap id = " + targetMap.getID()));
            player.gotoMap(targetMap);
            EffectServiceImpl.getInstance().sendEffectList(player, targetMap);
            if (EMapType.DUNGEON == currentMap.getMapType() && EMapType.GENERIC == targetMap.getMapType()) {
                DungeonServiceImpl.getInstance().playerLeftDungeon(player);
            }
            Npc escortNpc = player.getEscortTarget();
            if (escortNpc != null) {
                escortNpc.setCellX(player.getCellX());
                escortNpc.setCellY(player.getCellY());
                escortNpc.gotoMap(targetMap);
            }
            return;
        }
        SwitchMap.log.debug((Object) ("\u4e0d\u80fd\u83b7\u53d6\u76ee\u6807\u5730\u56fe\u51fa\u751f\u70b9\u4fe1\u606f\uff0c\u5f53\u524d\u5730\u56fe: " + currentMap.getID() + ",\u76ee\u6807\u5730\u56fe: " + targetMap.getID()));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SwitchMapFailNotify("\u4e0d\u80fd\u83b7\u53d6\u76ee\u6807\u5730\u56fe\u51fa\u751f\u70b9\u4fe1\u606f\uff0c\u5f53\u524d\u5730\u56fe: " + currentMap.getID() + ",\u76ee\u6807\u5730\u56fe: " + targetMap.getID()));
    }
}
