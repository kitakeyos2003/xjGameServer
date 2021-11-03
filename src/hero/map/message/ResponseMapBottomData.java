// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import hero.map.service.MapMusicDict;
import hero.map.service.WeatherManager;
import hero.map.service.MapTileDict;
import hero.map.service.MapServiceImpl;
import hero.player.HeroPlayer;
import hero.map.Map;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseMapBottomData extends AbsResponseMessage {

    private static Logger log;
    private Map map;
    private Map lastMap;
    private HeroPlayer player;

    static {
        ResponseMapBottomData.log = Logger.getLogger((Class) ResponseMapBottomData.class);
    }

    public ResponseMapBottomData(final HeroPlayer _player, final Map _map, final Map _lastMap) {
        this.player = _player;
        this.map = _map;
        this.lastMap = _lastMap;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeShort(this.map.getID());
        this.yos.writeByte(MapServiceImpl.getInstance().getPlayerMapWorldType(this.map.getID(), this.player));
        this.yos.writeShort(this.map.getMiniImageID());
        if (3 != this.player.getLoginInfo().clientType) {
            if (this.lastMap == null || this.map.getTileID() != this.lastMap.getTileID()) {
                this.yos.writeByte(1);
                char[] tileChars = MapTileDict.getInstance().getMapTileChars(this.map.getTileID());
                this.yos.writeShort(tileChars.length);
                this.yos.writeChars(tileChars);
            } else {
                this.yos.writeByte(0);
            }
        } else {
            this.yos.writeShort(this.map.getTileID());
        }
        int[] weatherDesc = WeatherManager.getInstance().getWeather(this.map.getWeather());
        this.yos.writeByte(weatherDesc[0]);
        this.yos.writeByte(weatherDesc[1]);
        this.yos.writeUTF(this.map.getName());
        this.yos.writeByte(this.map.getWidth());
        this.yos.writeByte(this.map.getHeight());
        this.yos.writeByte(this.map.getPKMark());
        this.yos.writeByte(this.map.isModifiable() ? 1 : 0);
        this.yos.writeByte(this.player.getCellX());
        this.yos.writeByte(this.player.getCellY());
        byte[] bottomCanvasData = this.map.bottomCanvasData;
        int len = bottomCanvasData.length;
        this.yos.writeShort(len);
        for (int i = 0; i < len; ++i) {
            this.yos.writeByte(bottomCanvasData[i]);
            this.yos.writeByte(this.map.transformData1[i]);
        }
        this.yos.writeByte(this.map.doorList.length);
        for (int i = 0; i < this.map.doorList.length; ++i) {
            this.yos.writeByte(this.map.doorList[i].x);
            this.yos.writeByte(this.map.doorList[i].y);
            this.yos.writeByte(this.map.doorList[i].direction);
            this.yos.writeShort(this.map.doorList[i].targetMapID);
            this.yos.writeUTF(this.map.doorList[i].targetMapName);
            this.yos.writeByte(this.map.doorList[i].visible);
        }
        this.yos.writeByte((byte) this.map.internalTransportList.length);
        for (int i = 0; i < this.map.internalTransportList.length; ++i) {
            this.yos.writeByte(this.map.internalTransportList[i][0]);
            this.yos.writeByte(this.map.internalTransportList[i][1]);
            this.yos.writeByte(this.map.internalTransportList[i][2]);
            this.yos.writeByte(this.map.internalTransportList[i][3]);
        }
        this.yos.writeByte((byte) this.map.popMessageList.length);
        for (int i = 0; i < this.map.popMessageList.length; ++i) {
            this.yos.writeByte(this.map.popMessageList[i].x);
            this.yos.writeByte(this.map.popMessageList[i].y);
            this.yos.writeUTF(this.map.popMessageList[i].msgContent);
        }
        if (this.map.cartoonList != null) {
            this.yos.writeByte(this.map.cartoonList.length);
            for (int i = 0; i < this.map.cartoonList.length; ++i) {
                this.yos.writeByte(this.map.cartoonList[i].firstTileIndex);
                this.yos.writeByte(this.map.cartoonList[i].followTileIndexList.length);
            }
        } else {
            this.yos.writeByte(0);
        }
        this.yos.writeShort(this.map.unpassData.length / 2);
        if (this.map.unpassData.length > 0) {
            this.yos.writeBytes(this.map.unpassData);
        }
        this.yos.writeShort((this.map.getArea() == null) ? 0 : this.map.getArea().getID());
        this.yos.writeUTF((this.map.getArea() == null) ? "" : this.map.getArea().getName());
        byte musicId = MapMusicDict.getInstance().getMapMusicID(this.map.getID());
        this.yos.writeByte(musicId);
        ResponseMapBottomData.log.info((Object) ("output size = " + String.valueOf(this.yos.size())));
    }
}
