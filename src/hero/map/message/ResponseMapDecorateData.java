// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import hero.map.detail.OtherObjectData;
import hero.map.Map;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseMapDecorateData extends AbsResponseMessage {

    private static Logger log;
    private Map map;
    private short clientType;

    static {
        ResponseMapDecorateData.log = Logger.getLogger((Class) ResponseMapDecorateData.class);
    }

    public ResponseMapDecorateData(final Map map, final short clientType) {
        this.map = map;
        this.clientType = clientType;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        ResponseMapDecorateData.log.debug((Object) "\u8fdb\u5165\u88c5\u9970\u5c42\u6570\u636e\u900f\u4f20");
        this.yos.writeShort(this.map.getID());
        ResponseMapDecorateData.log.debug((Object) ("map.getID() : " + this.map.getID()));
        this.yos.writeByte(this.map.decoraterList.length);
        ResponseMapDecorateData.log.debug((Object) ("map.decoraterList.length :" + this.map.decoraterList.length));
        int i = 0;
        OtherObjectData[] decoraterList;
        for (int length = (decoraterList = this.map.decoraterList).length, j = 0; j < length; ++j) {
            OtherObjectData decoraters = decoraterList[j];
            this.yos.writeShort(decoraters.pngId);
            ResponseMapDecorateData.log.debug((Object) ("pngId: " + decoraters.pngId));
            this.yos.writeShort(decoraters.animationID);
            ResponseMapDecorateData.log.debug((Object) ("decoraters.animationID:" + decoraters.animationID));
            this.yos.writeByte(decoraters.x);
            ResponseMapDecorateData.log.debug((Object) ("decoraters.x:" + decoraters.x));
            this.yos.writeByte(decoraters.y);
            ResponseMapDecorateData.log.debug((Object) ("decoraters.y:" + decoraters.y));
            this.yos.writeByte(decoraters.z);
            ResponseMapDecorateData.log.debug((Object) ("decoraters.z" + decoraters.z));
            ResponseMapDecorateData.log.debug((Object) ("decoraters[" + i + "] = " + decoraters.decorater));
            ++i;
            if (decoraters.decorater != null) {
                this.yos.writeByte(decoraters.decorater[0].decorateId);
            } else {
                ResponseMapDecorateData.log.info((Object) ("warn:\u7b56\u5212\u672a\u586b\u5199\u52a8\u753bID,mapid=" + this.map.getID() + ";mapname=" + this.map.getName()));
                this.yos.writeByte(0);
            }
        }
    }
}
