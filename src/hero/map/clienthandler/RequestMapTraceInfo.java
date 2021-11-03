// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.clienthandler;

import hero.map.Map;
import hero.player.HeroPlayer;
import hero.task.service.TaskServiceImpl;
import hero.item.service.GoodsServiceImpl;
import hero.map.message.ResponseBoxList;
import hero.map.message.ResponseAnimalInfoList;
import hero.map.message.ResponseMapDecorateData;
import hero.map.message.ResponseMapElementList;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.message.ResponseSceneElement;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class RequestMapTraceInfo extends AbsClientProcess {

    private static Logger log;

    static {
        RequestMapTraceInfo.log = Logger.getLogger((Class) RequestMapTraceInfo.class);
    }

    @Override
    public void read() throws Exception {
        RequestMapTraceInfo.log.info((Object) "@@@@@@@ RequestMapTraceInfo ...........");
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        Map where = player.where();
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseSceneElement(player.getLoginInfo().clientType, where));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapElementList(player.getLoginInfo().clientType, where));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapDecorateData(where, player.getLoginInfo().clientType));
        if (where.getAnimalList().size() > 0) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseAnimalInfoList(where));
        }
        if (where.getBoxList().size() > 0) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseBoxList(where.getBoxList()));
        }
        GoodsServiceImpl.getInstance().sendLegacyBoxList(where, player);
        TaskServiceImpl.getInstance().notifyMapNpcTaskMark(player, where);
        TaskServiceImpl.getInstance().notifyMapGearOperateMark(player, where);
        TaskServiceImpl.getInstance().notifyGroundTaskGoodsOperateMark(player, where);
    }
}
