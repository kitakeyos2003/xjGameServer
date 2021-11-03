// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.others;

import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.AnimalWalkNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.service.NotPlayerServiceImpl;
import hero.npc.service.NpcConfig;
import hero.npc.dict.AnimalImageDict;
import hero.npc.dict.AnimalDataDict;
import java.util.Random;

public class Animal extends ME2OtherGameObject {

    private static final Random RANDOM_BUILDER;
    private static byte TRACK_TIMES;
    private short orgX;
    private short orgY;
    private byte direction;
    private byte fastestWalkRange;
    private short animationID;
    private byte[] image;

    static {
        RANDOM_BUILDER = new Random();
        Animal.TRACK_TIMES = 4;
    }

    public Animal(final AnimalDataDict.AnimalData _data) {
        super(_data.modelID, _data.imageID);
        this.fastestWalkRange = _data.fastestWalkRange;
        this.image = AnimalImageDict.getInstance().getAnimalImageBytes(this.getImageID());
        this.direction = this.getRandomDirection();
        this.animationID = _data.animationID;
    }

    public byte getDirection() {
        return this.direction;
    }

    public void setDirection(final byte _direction) {
        this.direction = _direction;
    }

    public void setOrgX(final int _x) {
        this.orgX = (short) _x;
    }

    public short getOrgX() {
        return this.orgX;
    }

    public void setOrgY(final int _y) {
        this.orgY = (short) _y;
    }

    public short getOrgY() {
        return this.orgY;
    }

    public short getAnimationID() {
        return this.animationID;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void go(final byte _direction) {
        switch (_direction) {
            case 1: {
                this.setCellY((short) (this.getCellY() - 1));
                this.setDirection(_direction);
                break;
            }
            case 2: {
                this.setCellY((short) (this.getCellY() + 1));
                this.setDirection(_direction);
                break;
            }
            case 3: {
                this.setCellX((short) (this.getCellX() - 1));
                this.setDirection(_direction);
                break;
            }
            case 4: {
                this.setCellX((short) (this.getCellX() + 1));
                this.setDirection(_direction);
                break;
            }
        }
    }

    public void goAlone(final byte[] _movePath) {
        for (final byte direction : _movePath) {
            this.go(direction);
        }
    }

    public void walk() {
        byte[] moveActions = new byte[NotPlayerServiceImpl.getInstance().getConfig().ANIMAL_WALK_GRID_NUM_PER_TIME];
        this.direction = this.getRandomDirection();
        byte newDirection = 0;
        for (byte step = 0; step < NotPlayerServiceImpl.getInstance().getConfig().ANIMAL_WALK_GRID_NUM_PER_TIME; ++step) {
            newDirection = this.trackNext(true, 1);
            if (newDirection <= 0) {
                break;
            }
            this.go(newDirection);
            moveActions[step] = newDirection;
        }
        MapSynchronousInfoBroadcast.getInstance().put(this.where, new AnimalWalkNotify(this.getID(), moveActions, this.animationID, (byte) this.getCellX(), (byte) this.getCellY()), false, 0);
    }

    private byte trackNext(final boolean _first, int _trackTimes) {
        if (_first) {
            if (!this.passable(this.direction)) {
                return this.trackNext(false, ++_trackTimes);
            }
            return this.direction;
        } else {
            if (_trackTimes > Animal.TRACK_TIMES) {
                return 0;
            }
            switch (this.direction) {
                case 1: {
                    this.direction = 4;
                    if (!this.passable((byte) 4)) {
                        return this.trackNext(false, ++_trackTimes);
                    }
                    return 4;
                }
                case 2: {
                    this.direction = 3;
                    if (!this.passable((byte) 3)) {
                        return this.trackNext(false, ++_trackTimes);
                    }
                    return 3;
                }
                case 3: {
                    this.direction = 1;
                    if (!this.passable((byte) 1)) {
                        return this.trackNext(false, ++_trackTimes);
                    }
                    return 1;
                }
                case 4: {
                    this.direction = 2;
                    if (!this.passable((byte) 2)) {
                        return this.trackNext(false, ++_trackTimes);
                    }
                    return 2;
                }
                default: {
                    return 0;
                }
            }
        }
    }

    private boolean passable(final byte _direction) {
        switch (_direction) {
            case 1: {
                if (this.inMoveRange(this.getCellX(), this.getCellY() - 1)) {
                    return this.where.isRoad(this.getCellX(), this.getCellY() - 1);
                }
                break;
            }
            case 2: {
                if (this.inMoveRange(this.getCellX(), this.getCellY() + 1)) {
                    return this.where.isRoad(this.getCellX(), this.getCellY() + 1);
                }
                break;
            }
            case 3: {
                if (this.inMoveRange(this.getCellX() - 1, this.getCellY())) {
                    return this.where.isRoad(this.getCellX() - 1, this.getCellY());
                }
                break;
            }
            case 4: {
                if (this.inMoveRange(this.getCellX() + 1, this.getCellY())) {
                    return this.where.isRoad(this.getCellX() + 1, this.getCellY());
                }
                break;
            }
        }
        return false;
    }

    private boolean inMoveRange(final int _targetX, final int _targetY) {
        boolean inDistance = (this.orgX - _targetX) * 16 * ((this.orgX - _targetX) * 16) + (this.orgY - _targetY) * 16 * ((this.orgY - _targetY) * 16) <= this.fastestWalkRange * 16 * (this.fastestWalkRange * 16);
        return inDistance;
    }

    private byte getRandomDirection() {
        return (byte) (Animal.RANDOM_BUILDER.nextInt(4) + 1);
    }
}
