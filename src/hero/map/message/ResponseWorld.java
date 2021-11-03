// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import hero.map.WorldMap;
import hero.map.service.WorldMapDict;
import hero.map.service.MapConfig;
import hero.map.service.MapServiceImpl;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseWorld extends AbsResponseMessage {

    private byte typeID;

    public ResponseWorld(final byte typeID) {
        this.typeID = typeID;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.typeID);
        this.yos.writeUTF(MapServiceImpl.getInstance().getWorldNameByType((byte) 3));
        String[] infos = MapServiceImpl.getInstance().getConfig().world_map_png_anu;
        this.yos.writeShort(Short.parseShort(infos[0]));
        this.yos.writeShort(Short.parseShort(infos[1]));
        this.yos.writeShort(Short.parseShort(infos[2]));
        this.yos.writeShort(Short.parseShort(infos[3]));
        List<WorldMap> mapList = WorldMapDict.getInstance().getWorldMapListByType((byte) 4);
        this.yos.writeByte(mapList.size());
        for (final WorldMap worldMap : mapList) {
            this.yos.writeByte(worldMap.type);
            this.yos.writeUTF(worldMap.name);
            this.yos.writeShort(worldMap.cellX);
            this.yos.writeShort(worldMap.cellY);
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
