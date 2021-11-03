// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system.transmit;

public class TransmitTargetMapInfo {

    private short mapID;
    private String mapName;
    private short needLevel;
    private byte mapX;
    private byte mapY;
    private int freight;
    private String freightDescription;
    private static final String DESC_HEADER = "\u8d39\u7528\uff1a";
    private static final String DESC_ENDER = "\u91d1";
    private static final String NAME_CONNECTOR = "--";

    public TransmitTargetMapInfo(final short _mapID, final String _mapName, final String _areaName, final short _needLevel, final byte _mapX, final byte _mapY, final int _freight) {
        this.mapID = _mapID;
        this.mapName = _mapName;
        this.needLevel = _needLevel;
        this.mapX = _mapX;
        this.mapY = _mapY;
        this.freight = _freight;
        this.freightDescription = "\u8d39\u7528\uff1a" + _freight + "\u91d1";
    }

    public short getMapID() {
        return this.mapID;
    }

    public String getMapName() {
        return this.mapName;
    }

    public short getNeedLevel() {
        return this.needLevel;
    }

    public byte getMapX() {
        return this.mapX;
    }

    public byte getMapY() {
        return this.mapY;
    }

    public int getFreight() {
        return this.freight;
    }

    public String getDescription() {
        return this.freightDescription;
    }
}
