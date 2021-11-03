// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.clienthandler;

import hero.map.Area;
import hero.map.Map;
import hero.player.HeroPlayer;
import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.message.ResponseAreaImage;
import yoyo.core.queue.ResponseMessageQueue;
import hero.map.service.AreaDict;
import hero.map.service.MapRelationDict;
import hero.map.service.MiniMapImageDict;
import hero.map.service.MapServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RequestMicroMap extends AbsClientProcess {

    private static final byte TYPE_OF_MICRO = 0;
    private static final byte TYPE_OF_AREA = 1;

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            byte imageType = this.yis.readByte();
            short mapID = this.yis.readShort();
            if (imageType == 0) {
                Map map = MapServiceImpl.getInstance().getNormalMapByID(mapID);
                if (map != null) {
                    MiniMapImageDict.getInstance().getImageBytes(map.getMiniImageID());
                }
            } else if (1 == imageType) {
                Map map = MapServiceImpl.getInstance().getNormalMapByID(mapID);
                Area area;
                if (map != null) {
                    area = map.getArea();
                } else {
                    short[] relation = MapRelationDict.getInstance().getRelationByMapID(mapID);
                    area = AreaDict.getInstance().getAreaByID(relation[4]);
                }
                if (area != null) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseAreaImage(player.getLoginInfo().clientType, area));
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
