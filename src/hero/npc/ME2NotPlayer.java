// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc;

import hero.npc.service.NpcConfig;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.map.Map;
import hero.share.ME2GameObject;

public abstract class ME2NotPlayer extends ME2GameObject {

    private String modelID;
    private short imageID;
    private short animationID;
    private Map orgMap;
    private short orgX;
    private short orgY;
    private short orgZ;
    private long refreshTime;
    protected boolean isCalled;
    private int existsTime;

    public ME2NotPlayer() {
        this.refreshTime = System.currentTimeMillis();
    }

    public Map getOrgMap() {
        return this.orgMap;
    }

    public void setOrgMap(final Map _orgMap) {
        this.orgMap = _orgMap;
    }

    public short getOrgX() {
        return this.orgX;
    }

    public void setOrgX(final short _orgX) {
        this.orgX = _orgX;
    }

    public short getOrgY() {
        return this.orgY;
    }

    public void setOrgY(final short _orgY) {
        this.orgY = _orgY;
    }

    public short getOrgZ() {
        return this.orgZ;
    }

    public void setOrgZ(final short _orgZ) {
        this.orgZ = _orgZ;
    }

    public boolean isCalled() {
        return this.isCalled;
    }

    public long getRefreshTime() {
        return this.refreshTime;
    }

    public int getExistsTime() {
        return this.existsTime;
    }

    public void setExistsTime(final int _time) {
        this.isCalled = true;
        this.existsTime = _time;
    }

    public void setImageID(final short _imageID) {
        this.imageID = _imageID;
    }

    public short getImageID() {
        return this.imageID;
    }

    public short getAnimationID() {
        return this.animationID;
    }

    public void setAnimationID(final short _animationID) {
        this.animationID = _animationID;
    }

    public void goAlone(final byte[] _movePath, final HeroPlayer _attackTarget) {
        for (final byte direction : _movePath) {
            this.go(direction);
        }
        NotPlayerServiceImpl.getInstance().broadcastNotPlayerWalkPath(this, _movePath, _attackTarget);
    }

    private boolean inMoveRange(final int _targetX, final int _targetY) {
        int monster_move_most_fast_grid = NotPlayerServiceImpl.getInstance().getConfig().MONSTER_MOVE_MOST_FAST_GRID * 16;
        boolean inDistance = (this.orgX - _targetX) * 16 * ((this.orgX - _targetX) * 16) + (this.orgY - _targetY) * 16 * ((this.orgY - _targetY) * 16) <= monster_move_most_fast_grid * monster_move_most_fast_grid;
        return inDistance;
    }

    public boolean passable(final byte _direction) {
        switch (_direction) {
            case 1: {
                if (this.inMoveRange(this.getCellX(), this.getCellY() - 1)) {
                    return this.where().isRoad(this.getCellX(), this.getCellY() - 1);
                }
                break;
            }
            case 2: {
                if (this.inMoveRange(this.getCellX(), this.getCellY() + 1)) {
                    return this.where().isRoad(this.getCellX(), this.getCellY() + 1);
                }
                break;
            }
            case 3: {
                if (this.inMoveRange(this.getCellX() - 1, this.getCellY())) {
                    return this.where().isRoad(this.getCellX() - 1, this.getCellY());
                }
                break;
            }
            case 4: {
                if (this.inMoveRange(this.getCellX() + 1, this.getCellY())) {
                    return this.where().isRoad(this.getCellX() + 1, this.getCellY());
                }
                break;
            }
        }
        return false;
    }

    public void setModelID(final String _modelID) {
        this.modelID = _modelID;
    }

    public String getModelID() {
        return this.modelID;
    }

    public abstract void destroy();
}
