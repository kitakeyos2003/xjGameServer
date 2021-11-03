// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import java.util.Iterator;
import hero.map.WorldMap;
import java.util.List;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseWorldMaps extends AbsResponseMessage {

    private List<WorldMap> worldMapList;
    private WorldMap maxWorldMap;
    private byte flag;
    private String tip;

    public ResponseWorldMaps(final List<WorldMap> worldMapList, final WorldMap maxWorldMap, final byte flag, final String tip) {
        this.worldMapList = worldMapList;
        this.maxWorldMap = maxWorldMap;
        this.flag = flag;
        this.tip = tip;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.flag);
        if (this.flag == 0) {
            this.yos.writeUTF(this.tip);
        }
        if (this.flag == 1) {
            this.yos.writeUTF(this.maxWorldMap.name);
            this.yos.writeByte(this.maxWorldMap.type);
            this.yos.writeShort(this.maxWorldMap.png);
            this.yos.writeShort(this.maxWorldMap.anu);
            this.yos.writeShort(this.maxWorldMap.width);
            this.yos.writeShort(this.maxWorldMap.height);
            if (this.worldMapList != null) {
                this.yos.writeByte(this.worldMapList.size());
                for (final WorldMap worldMap : this.worldMapList) {
                    this.yos.writeShort(worldMap.cellX);
                    this.yos.writeShort(worldMap.cellY);
                    this.yos.writeShort(worldMap.mapID);
                    this.yos.writeUTF(worldMap.name);
                    this.yos.writeShort(worldMap.bornX);
                    this.yos.writeShort(worldMap.bornY);
                }
            } else {
                this.yos.writeByte(0);
            }
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
