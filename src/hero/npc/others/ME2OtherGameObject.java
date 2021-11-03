// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.others;

import hero.share.service.IDManager;
import hero.map.Map;

public abstract class ME2OtherGameObject {

    private int id;
    private String modelID;
    protected Map where;
    private short cellX;
    private short cellY;
    private short imageID;

    public ME2OtherGameObject(final String _modelID, final short _imageID) {
        this.id = IDManager.buildObjectID();
        this.modelID = _modelID;
        this.imageID = _imageID;
    }

    public ME2OtherGameObject(final String _modelID) {
        this.id = IDManager.buildObjectID();
        this.modelID = _modelID;
    }

    public int getID() {
        return this.id;
    }

    public String getModelID() {
        return this.modelID;
    }

    public Map where() {
        return this.where;
    }

    public void live(final Map _map) {
        this.where = _map;
    }

    public short getCellX() {
        return this.cellX;
    }

    public void setCellX(final short _x) {
        this.cellX = _x;
    }

    public short getCellY() {
        return this.cellY;
    }

    public void setCellY(final short _y) {
        this.cellY = _y;
    }

    public short getImageID() {
        return this.imageID;
    }
}
