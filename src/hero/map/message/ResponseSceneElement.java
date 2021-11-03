// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import java.util.Iterator;
import hero.map.service.MapServiceImpl;
import hero.map.Map;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseSceneElement extends AbsResponseMessage {

    private static Logger log;
    private Map map;
    private short clientType;

    static {
        ResponseSceneElement.log = Logger.getLogger((Class) ResponseSceneElement.class);
    }

    public ResponseSceneElement(final short _clientType, final Map _map) {
        this.clientType = _clientType;
        this.map = _map;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.map.elementImageIDList.size());
        ResponseSceneElement.log.info((Object) ("map.elementImageIDList:" + this.map.elementImageIDList.size()));
        for (final short imageID : this.map.elementImageIDList) {
            if (this.map.getID() == 120) {
                ResponseSceneElement.log.info((Object) ("120 map \u56fe\u7247ID:" + imageID));
            }
            this.yos.writeShort(imageID);
            if (3 != this.clientType) {
                byte[] imageBytes = MapServiceImpl.getInstance().getElementImageBytes(imageID);
                this.yos.writeShort(imageBytes.length);
                this.yos.writeBytes(imageBytes);
            }
        }
        short[] elementCanvasData = this.map.elementCanvasData;
        ResponseSceneElement.log.debug((Object) ("mapid=" + this.map.getID() + ",name=" + this.map.getName() + "\uff0cmap.elementCanvasData=" + this.map.elementCanvasData));
        this.yos.writeShort(elementCanvasData.length / 3);
        for (int i = 0; i < elementCanvasData.length; ++i) {
            this.yos.writeShort(elementCanvasData[i]);
        }
        ResponseSceneElement.log.debug((Object) ("mapid=" + this.map.getID() + ",name=" + this.map.getName() + ",map.transformData2=" + this.map.transformData2));
        if (this.map.transformData2 != null) {
            this.yos.writeShort(this.map.transformData2.length);
            this.yos.writeBytes(this.map.transformData2);
        }
    }
}
