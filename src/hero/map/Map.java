// 
// Decompiled by Procyon v0.5.36
// 
package hero.map;

import hero.npc.Monster;
import hero.npc.Npc;
import hero.player.HeroPlayer;
import java.util.Iterator;
import hero.map.service.WeatherManager;
import hero.map.detail.OtherObjectData;
import hero.map.detail.Cartoon;
import hero.map.detail.PopMessage;
import hero.map.detail.Door;
import hero.item.legacy.MonsterLegacyBox;
import hero.npc.others.GroundTaskGoods;
import hero.npc.others.Box;
import hero.npc.others.DoorPlate;
import hero.npc.others.RoadInstructPlate;
import hero.npc.others.TaskGear;
import hero.npc.others.Animal;
import java.util.ArrayList;
import hero.share.service.ME2ObjectList;
import hero.dungeon.Dungeon;

public class Map {

    private short id;
    private String name;
    private short tileID;
    private short width;
    private short height;
    private short bornX;
    private short bornY;
    private EMapType mapType;
    private Dungeon dungeon;
    private EMapWeather weather;
    private String monsterModelIDAbout;
    private int microMapID;
    private short targetMapIDAfterDie;
    private short targetMapIDAfterUseGoods;
    private short mozuTargetMapIDAfterDie;
    private short mozuTargetMapIDAfterUseGoods;
    private boolean modifiable;
    private byte pkMark;
    private Area area;
    private ME2ObjectList playerList;
    private ME2ObjectList monsterList;
    private ME2ObjectList npcList;
    private ArrayList<Animal> animalList;
    private ArrayList<TaskGear> gearList;
    private ArrayList<RoadInstructPlate> roadPlateList;
    private ArrayList<DoorPlate> doorPlateList;
    private ArrayList<Box> boxList;
    private ArrayList<GroundTaskGoods> groundTaskGoodsList;
    private ArrayList<MonsterLegacyBox> legacyBoxList;
    public ArrayList<Short> fixedNpcImageIDList;
    public ArrayList<Short> fixedMonsterImageIDList;
    public ArrayList<Short> groundTaskGoodsImageIDList;
    public ArrayList<Short> taskGearImageIDList;
    public byte[] bottomCanvasData;
    public byte[] resourceCanvasMap;
    public short[] elementCanvasData;
    public ArrayList<Short> elementImageIDList;
    public byte[][] unpassMarkArray;
    public byte[] unpassData;
    public Door[] doorList;
    public byte[][] internalTransportList;
    public PopMessage[] popMessageList;
    public Cartoon[] cartoonList;
    public byte[] transformData1;
    public byte[] transformData2;
    public byte[] resourceTransformData;
    public Cartoon[] cartoonList2;
    public int animNum;
    public OtherObjectData[] decoraterList;
    public boolean canStore;
    public static final int TYAPE_ALLOW_PK_AND_ALLOW_FIGHT = 1;
    public static final int TYPE_NOT_PK_AND_ALLOW_FIGHT = 2;
    public static final int TYPE_ALLOW_PK_AND_NOT_FIGHT = 3;
    public static final int TYPE_NOT_PK_AND_NOT_FIGHT = 4;
    public static final int WORLD_MAP_ID_UPPER_LIMIT = 5000;
    public static final boolean IS_NEW_MAP = true;

    public Map(final short _id, final String _name) {
        this.id = _id;
        this.name = _name;
        this.npcList = new ME2ObjectList();
        this.monsterList = new ME2ObjectList();
        this.playerList = new ME2ObjectList();
        this.animalList = new ArrayList<Animal>();
        this.gearList = new ArrayList<TaskGear>();
        this.roadPlateList = new ArrayList<RoadInstructPlate>();
        this.doorPlateList = new ArrayList<DoorPlate>();
        this.boxList = new ArrayList<Box>();
        this.groundTaskGoodsList = new ArrayList<GroundTaskGoods>();
        this.legacyBoxList = new ArrayList<MonsterLegacyBox>();
    }

    public short getID() {
        return this.id;
    }

    public void setID(final int _id) {
        this.id = (short) _id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String _name) {
        this.name = _name;
    }

    public EMapType getMapType() {
        return this.mapType;
    }

    public void setMapType(final EMapType _mapType) {
        this.mapType = _mapType;
    }

    public ArrayList<Door> openTheDoor(final String _monsterModelID) {
        ArrayList<Door> openedDoorList = new ArrayList<Door>();
        Door[] doorList;
        for (int length = (doorList = this.doorList).length, i = 0; i < length; ++i) {
            Door point = doorList[i];
            if (point.monsterIDAbout.equals(_monsterModelID)) {
                openedDoorList.add(point);
            }
        }
        return openedDoorList;
    }

    public short getTileID() {
        return this.tileID;
    }

    public void setTileID(final int _tileID) {
        this.tileID = (short) _tileID;
    }

    public void setTargetMapIDAfterDie(final short _mapID) {
        this.targetMapIDAfterDie = _mapID;
    }

    public short getTargetMapIDAfterDie() {
        return this.targetMapIDAfterDie;
    }

    public short getMozuTargetMapIDAfterDie() {
        return this.mozuTargetMapIDAfterDie;
    }

    public void setMozuTargetMapIDAfterDie(final short mozuTargetMapIDAfterDie) {
        this.mozuTargetMapIDAfterDie = mozuTargetMapIDAfterDie;
    }

    public short getMozuTargetMapIDAfterUseGoods() {
        return this.mozuTargetMapIDAfterUseGoods;
    }

    public void setMozuTargetMapIDAfterUseGoods(final short mozuTargetMapIDAfterUseGoods) {
        this.mozuTargetMapIDAfterUseGoods = mozuTargetMapIDAfterUseGoods;
    }

    public void setTargetMapIDAfterUseGoods(final short _mapID) {
        this.targetMapIDAfterUseGoods = _mapID;
    }

    public short getTargetMapIDAfterUseGoods() {
        return this.targetMapIDAfterUseGoods;
    }

    public short getWidth() {
        return this.width;
    }

    public void setWidth(final int _width) {
        this.width = (short) _width;
    }

    public short getHeight() {
        return this.height;
    }

    public void setHeight(final int _height) {
        this.height = (short) _height;
    }

    public int getMiniImageID() {
        return this.microMapID;
    }

    public void setMiniImageID(final int _microMapID) {
        this.microMapID = _microMapID;
    }

    public EMapWeather getWeather() {
        return this.weather;
    }

    public void setWeather(final EMapWeather _weather) {
        this.weather = _weather;
        if (EMapWeather.NONE != _weather) {
            WeatherManager.getInstance().add(this);
        }
    }

    public short getBornX() {
        return this.bornX;
    }

    public void setBornX(final short _bornX) {
        this.bornX = _bornX;
    }

    public short getBornY() {
        return this.bornY;
    }

    public void setBornY(final short _bornY) {
        this.bornY = _bornY;
    }

    public void setMonsterModelIDAbout(final String _monsterModelID) {
        this.monsterModelIDAbout = _monsterModelID;
    }

    public String getMonsterModelIDAbout() {
        return this.monsterModelIDAbout;
    }

    public void changeBottomCanvasData(final short x, final short y, final short newTileId) {
        if (this.modifiable) {
            this.bottomCanvasData[y * this.width + x] = (byte) newTileId;
        }
    }

    public void changeElementData(final short x, final short y, final short newTileId) {
        if (this.modifiable) {
            this.elementCanvasData[y * this.width + x] = (byte) newTileId;
        }
    }

    public boolean isModifiable() {
        return this.modifiable;
    }

    public void setModifiable(final boolean _yesOrNo) {
        this.modifiable = _yesOrNo;
    }

    public ME2ObjectList getNpcList() {
        return this.npcList;
    }

    public ME2ObjectList getMonsterList() {
        return this.monsterList;
    }

    public ME2ObjectList getPlayerList() {
        return this.playerList;
    }

    public ArrayList<Animal> getAnimalList() {
        return this.animalList;
    }

    public ArrayList<TaskGear> getTaskGearList() {
        return this.gearList;
    }

    public ArrayList<RoadInstructPlate> getRoadPlateList() {
        return this.roadPlateList;
    }

    public ArrayList<DoorPlate> getDoorPlateList() {
        return this.doorPlateList;
    }

    public ArrayList<Box> getBoxList() {
        return this.boxList;
    }

    public Box existBox(final String _boxModelID) {
        for (final Box box : this.boxList) {
            if (box.getModelID().equals(_boxModelID)) {
                return box;
            }
        }
        return null;
    }

    public void refreshBox() {
        for (final Box box : this.boxList) {
            box.rebirth(true);
        }
    }

    public ArrayList<GroundTaskGoods> getGroundTaskGoodsList() {
        return this.groundTaskGoodsList;
    }

    public final HeroPlayer getPlayer(final int _objectID) {
        return (HeroPlayer) this.playerList.getObject(_objectID);
    }

    public final Npc getNpc(final int _objectID) {
        return (Npc) this.npcList.getObject(_objectID);
    }

    public final Monster getMonster(final int _objectID) {
        return (Monster) this.monsterList.getObject(_objectID);
    }

    public final boolean isDxternaPort(final short _x, final short _y) {
        for (int i = 0; i < this.doorList.length; ++i) {
            if (this.doorList[i].x == _x && this.doorList[i].y == _y) {
                return true;
            }
        }
        return false;
    }

    public short[] getExternalPort(final short _x, final short _y) {
        for (int i = 0; i < this.doorList.length; ++i) {
            if (this.doorList[i].x == _x && this.doorList[i].y == _y) {
                short[] p = {this.doorList[i].direction, this.doorList[i].targetMapID, this.doorList[i].targetMapX, this.doorList[i].targetMapY};
                return p;
            }
        }
        return null;
    }

    public short[] getTargetMapPoint(final int _targetMapID, final short _x, final short _y) {
        for (int i = 0; i < this.doorList.length; ++i) {
            if (this.doorList[i].x == _x && this.doorList[i].y == _y && this.doorList[i].targetMapID == _targetMapID) {
                short[] p = {this.doorList[i].targetMapX, this.doorList[i].targetMapY};
                return p;
            }
        }
        return null;
    }

    public ArrayList<MonsterLegacyBox> getLegacyBoxList() {
        return this.legacyBoxList;
    }

    public boolean isLegalPoint(final int _x, final int _y) {
        return _x >= 0 && _y >= 0 && _x < this.width && _y < this.height;
    }

    public void setArea(final Area _area) {
        this.area = _area;
    }

    public Area getArea() {
        return this.area;
    }

    public byte getPKMark() {
        return this.pkMark;
    }

    public void setPKMark(final byte _pkMark) {
        this.pkMark = _pkMark;
    }

    public boolean isRoad(final int _cellX, final int _cellY) {
        return this.isLegalPoint(_cellX, _cellY) && this.unpassMarkArray[_cellY][_cellX] == 0;
    }

    public boolean attackIsRoad(final int _cellX, final int _cellY, final int _targetX, final int _targetY) {
        boolean result = false;
        int x = _cellX;
        int y = _cellY;
        if (x == _targetX && y == _targetY) {
            result = true;
        } else if (x == _targetX) {
            while (y != _targetY) {
                if (y > _targetY) {
                    --y;
                } else {
                    ++y;
                }
                if (this.isLegalPoint(_cellX, _cellY) && this.isLegalPoint(x, y)) {
                    result = (this.unpassMarkArray[x][y] == 0);
                    if (!result) {
                        break;
                    }
                    continue;
                }
            }
        } else if (y == _targetY) {
            while (x != _targetX) {
                if (x > _targetX) {
                    --x;
                } else {
                    ++x;
                }
                if (this.isLegalPoint(_cellX, _cellY) && this.isLegalPoint(x, y)) {
                    result = (this.unpassMarkArray[x][y] == 0);
                    if (!result) {
                        break;
                    }
                    continue;
                }
            }
        }
        return result;
    }

    public void destroy() {
        for (int i = 0; i < this.monsterList.size(); ++i) {
            Monster monster = (Monster) this.monsterList.get(i);
            monster.destroy();
        }
        for (int j = 0; j < this.npcList.size(); ++j) {
            Npc npc = (Npc) this.npcList.get(j);
            npc.destroy();
        }
    }

    public void setDungeon(final Dungeon _dungeon) {
        this.dungeon = _dungeon;
    }

    public Dungeon getDungeon() {
        return this.dungeon;
    }
}
