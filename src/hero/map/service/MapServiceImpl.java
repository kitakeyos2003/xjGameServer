// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.service;

import hero.dungeon.Dungeon;
import hero.dungeon.service.DungeonServiceImpl;
import hero.share.EObjectType;
import hero.share.service.ME2ObjectList;
import hero.npc.others.GroundTaskGoods;
import hero.npc.others.Box;
import hero.npc.others.TaskGear;
import hero.npc.others.DoorPlate;
import hero.npc.others.RoadInstructPlate;
import hero.npc.others.Animal;
import hero.npc.Monster;
import hero.npc.Npc;
import hero.map.detail.OtherObjectData;
import hero.map.detail.Door;
import hero.npc.service.NotPlayerServiceImpl;
import hero.map.EMapWeather;
import hero.map.EMapType;
import java.util.ArrayList;
import hero.map.MapModelData;
import hero.share.service.LogWriter;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Iterator;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.message.DisappearNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.share.ME2GameObject;
import hero.player.service.PlayerServiceImpl;
import yoyo.service.base.session.Session;
import hero.npc.function.system.transmit.MapTransmitInfoDict;
import hero.item.dictionary.PetEquipmentDict;
import hero.map.Map;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class MapServiceImpl extends AbsServiceAdaptor<MapConfig> implements IMapService {

    private static Logger log;
    private static MapServiceImpl instance;
    private FastMap<Short, byte[]> mapElementImageTable;
    private FastMap<Short, Map> mapDictionary;
    public static final short[] canStroeMapIDs;

    static {
        MapServiceImpl.log = Logger.getLogger((Class) MapServiceImpl.class);
        canStroeMapIDs = new short[]{401, 118, 408, 120, 7, 65, 28, 86, 35, 93, 51, 109, 117, 125, 138, 149};
    }

    private MapServiceImpl() {
        this.config = new MapConfig();
        this.mapElementImageTable = (FastMap<Short, byte[]>) new FastMap();
        this.mapDictionary = (FastMap<Short, Map>) new FastMap();
    }

    public static MapServiceImpl getInstance() {
        if (MapServiceImpl.instance == null) {
            MapServiceImpl.instance = new MapServiceImpl();
        }
        return MapServiceImpl.instance;
    }

    @Override
    protected void start() {
        MapMusicDict.getInstance().init(((MapConfig) this.config).map_music_config_path);
        MapRelationDict.getInstance().init(((MapConfig) this.config).getMapRelationDataPath());
        AreaImageDict.getInstance().init(((MapConfig) this.config).getAreaImagePath());
        MiniMapImageDict.getInstance().init(((MapConfig) this.config).getMicroMapImagePath());
        MapTileDict.getInstance().init(((MapConfig) this.config).getMapTileImagePath());
        AreaDict.getInstance().init(((MapConfig) this.config).getAreaDataPath());
        MapModelDataDict.getInstance().init(((MapConfig) this.config).getMapModelFilePath());
        PetEquipmentDict.getInstance().load(((MapConfig) this.config).pet_equip_data_path);
        this.loadNormalMap();
        this.loadMapElementImage(((MapConfig) this.config).getMapElementImagePath());
        MapTransmitInfoDict.getInstance();
        WorldMapDict.getInstance().load((MapConfig) this.config);
    }

    @Override
    public void createSession(final Session _session) {
    }

    @Override
    public void sessionFree(final Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
        if (player != null) {
            player.where().getPlayerList().remove(player);
            MapSynchronousInfoBroadcast.getInstance().put(player.where(), new DisappearNotify(player.getObjectType().value(), player.getID(), player.getHp(), player.getBaseProperty().getHpMax(), player.getMp(), player.getBaseProperty().getMpMax()), true, player.getID());
        }
    }

    @Override
    public void clean(final int _userID) {
    }

    public boolean canStroe(final short mapID) {
        for (int i = 0; i < MapServiceImpl.canStroeMapIDs.length; ++i) {
            if (mapID == MapServiceImpl.canStroeMapIDs[i]) {
                return true;
            }
        }
        return false;
    }

    public String getWorldNameByType(final byte type) {
        return ((MapConfig) this.config).world_names[type - 1];
    }

    public Map getNormalMapByID(final short _mapID) {
        return (Map) this.mapDictionary.get(_mapID);
    }

    public Map getNormalMapByName(final String _mapName) {
        for (final Map map : this.mapDictionary.values()) {
            if (map.getName().equals(_mapName)) {
                return map;
            }
        }
        return null;
    }

    public byte[] getElementImageBytes(final short _elementImageID) {
        return (byte[]) this.mapElementImageTable.get(_elementImageID);
    }

    private void loadMapElementImage(final String _imageFilePath) {
        File[] imageFiles = new File(_imageFilePath).listFiles();
        for (int i = 0; i < imageFiles.length; ++i) {
            short imageID = -1;
            if (imageFiles[i].getName().toLowerCase().endsWith(".png")) {
                String imageFileName = imageFiles[i].getName();
                imageID = Short.parseShort(imageFileName.substring(0, imageFileName.length() - 4));
                this.mapElementImageTable.put(imageID, this.getImageBytes(imageFiles[i]));
            }
        }
        imageFiles = null;
    }

    private byte[] getImageBytes(final File _imageFile) {
        byte[] rtnValue = null;
        try {
            DataInputStream dis = null;
            dis = new DataInputStream(new FileInputStream(_imageFile));
            int imgFileByteSize = dis.available();
            rtnValue = new byte[imgFileByteSize];
            int pos = 0;
            while ((pos = dis.read(rtnValue, pos, imgFileByteSize - pos)) != -1) {
            }
            dis.close();
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
        return rtnValue;
    }

    private void loadNormalMap() {
        ArrayList<MapModelData> mapModelDataList = MapModelDataDict.getInstance().getMapModelDataList();
        for (final MapModelData mapModelData : mapModelDataList) {
            MapServiceImpl.log.debug(("start build map id = " + mapModelData.id));
            Map map = this.buildMap(mapModelData);
            if (map == null) {
                MapServiceImpl.log.debug(("\u52a0\u8f7d\u5730\u56fe\u6570\u636e\u5931\u8d25\uff1a" + mapModelData.id + "   " + mapModelData.name));
            } else {
                MapServiceImpl.log.debug(("\u52a0\u8f7d\u5730\u56fe\u6570\u636e\u6210\u529f\uff1a" + mapModelData.id + "   " + mapModelData.name));
            }
            if (map != null) {
                this.mapDictionary.put(map.getID(), map);
            }
        }
        for (final Map map2 : this.mapDictionary.values()) {
            MapServiceImpl.log.debug(("#### mapid=" + map2.getID() + ",name[" + map2.getName() + "] transformData2 size=" + map2.transformData2.length));
        }
    }

    private Map buildMap(final MapModelData _mapModelData) {
        Map map = new Map(_mapModelData.id, _mapModelData.name);
        try {
            map.setBornX(_mapModelData.bornX);
            map.setBornY(_mapModelData.bornY);
            map.setHeight(_mapModelData.height);
            map.setWidth(_mapModelData.width);
            map.setModifiable(_mapModelData.modifiable);
            map.setPKMark(_mapModelData.pkMark);
            map.setTileID(_mapModelData.tileImageID);
            map.setMapType(EMapType.getMapType(_mapModelData.mapTypeValue));
            map.setMonsterModelIDAbout(_mapModelData.monsterModelIDAbout);
            map.setWeather(EMapWeather.get(_mapModelData.mapWeatherValue));
            short[] relation = MapRelationDict.getInstance().getRelationByMapID(map.getID());
            if (relation != null) {
                map.setMiniImageID(relation[1]);
                map.setTargetMapIDAfterDie(relation[2]);
                map.setTargetMapIDAfterUseGoods(relation[3]);
                MapServiceImpl.log.debug(("setMozuTargetMapIDAfterDie=" + relation[8] + ",setMozuTargetMapIDAfterUseGoods=" + relation[9]));
                map.setMozuTargetMapIDAfterDie(relation[8]);
                map.setMozuTargetMapIDAfterUseGoods(relation[9]);
                if (relation[4] != 0) {
                    if (relation[5] != 0) {
                        AreaDict.getInstance().getAreaByID(relation[4]).add(map, true, relation[6], relation[7]);
                    } else {
                        AreaDict.getInstance().getAreaByID(relation[4]).add(map, false, 0, 0);
                    }
                }
            } else {
                LogWriter.println("\u627e\u4e0d\u5230\u5730\u56fe\u5173\u7cfb:" + map.getName());
            }
            map.bottomCanvasData = _mapModelData.bottomCanvasData;
            map.resourceCanvasMap = _mapModelData.resourceCanvasMap;
            map.elementCanvasData = _mapModelData.elementCanvasData;
            map.elementImageIDList = _mapModelData.elementImageIDList;
            map.fixedNpcImageIDList = _mapModelData.fixedNpcImageIDList;
            map.fixedMonsterImageIDList = _mapModelData.fixedMonsterImageIDList;
            map.groundTaskGoodsImageIDList = _mapModelData.groundTaskGoodsImageIDList;
            map.taskGearImageIDList = _mapModelData.taskGearImageIDList;
            map.cartoonList = _mapModelData.cartoonList;
            map.doorList = _mapModelData.externalPortList;
            map.transformData1 = _mapModelData.transformData1;
            map.resourceTransformData = _mapModelData.resourceTransformData;
            map.transformData2 = _mapModelData.transformData2;
            map.decoraterList = _mapModelData.decorateObjectList;
            MapServiceImpl.log.debug(("build mapid=" + map.getID() + ",[" + map.getName() + "],map.transformData2=" + map.transformData2.length + ",_mapModelData.transformData2=" + _mapModelData.transformData2));
            MapServiceImpl.log.debug(("map door List size = " + map.doorList.length));
            Door[] doorList;
            for (int length = (doorList = map.doorList).length, i = 0; i < length; ++i) {
                Door switchPoint = doorList[i];
                MapServiceImpl.log.debug(("switchPoint mapID=" + switchPoint.targetMapID));
                switchPoint.targetMapName = MapModelDataDict.getInstance().getMapModelData(switchPoint.targetMapID).name;
            }
            map.internalTransportList = _mapModelData.internalPorts;
            map.popMessageList = _mapModelData.popMessageList;
            map.unpassData = _mapModelData.unpassData;
            map.unpassMarkArray = _mapModelData.unpassMarkArray;
            if (_mapModelData.notPlayerObjectList.length > 0) {
                if (_mapModelData.notPlayerObjectList.length > 500) {
                    MapServiceImpl.log.warn(("\u52a0\u8f7dnotPlayerObjectList.length=" + _mapModelData.notPlayerObjectList.length));
                }
                OtherObjectData[] notPlayerObjectList;
                for (int length2 = (notPlayerObjectList = _mapModelData.notPlayerObjectList).length, j = 0; j < length2; ++j) {
                    OtherObjectData npcData = notPlayerObjectList[j];
                    MapServiceImpl.log.debug(("npcData.modelID  = " + npcData.modelID));
                    if (npcData.modelID.startsWith("n")) {
                        Npc npc = NotPlayerServiceImpl.getInstance().buildNpcInstance(npcData.modelID);
                        if (npc != null) {
                            npc.live(map);
                            npc.setOrgMap(map);
                            npc.setOrgX(npcData.x);
                            npc.setOrgY(npcData.y);
                            npc.setCellX(npcData.x);
                            npc.setCellY(npcData.y);
                            npc.active();
                            map.getNpcList().add(npc);
                        } else {
                            LogWriter.println("\u672a\u80fd\u52a0\u8f7dNPC\uff1a" + npcData.modelID + "---" + map.getName());
                            MapServiceImpl.log.debug(("\u672a\u80fd\u52a0\u8f7dNPC\uff1a" + npcData.modelID + "---" + map.getName()));
                        }
                    } else if (npcData.modelID.startsWith("m")) {
                        MapServiceImpl.log.debug("\u5f00\u59cb\u52a0\u8f7d\u602a\u7269");
                        Monster monster = NotPlayerServiceImpl.getInstance().buildMonsterInstance(npcData.modelID);
                        if (monster != null) {
                            monster.live(map);
                            monster.setOrgMap(map);
                            monster.setOrgX(npcData.x);
                            monster.setOrgY(npcData.y);
                            monster.setCellX(npcData.x);
                            monster.setCellY(npcData.y);
                            monster.setOrgZ(npcData.z);
                            monster.setCellZ(npcData.z);
                            monster.active(npcData.movePath);
                            map.getMonsterList().add(monster);
                            MapServiceImpl.log.debug(("\u5df2\u52a0\u8f7d\u602a\u7269 : = " + npcData.modelID));
                        } else {
                            LogWriter.println("\u672a\u80fd\u52a0\u8f7d\u602a\u7269\uff1a" + npcData.modelID + "---" + map.getName());
                            MapServiceImpl.log.debug(("\u672a\u80fd\u52a0\u8f7d\u602a\u7269\uff1a" + npcData.modelID + "---" + map.getName()));
                        }
                    } else if (npcData.modelID.startsWith("a")) {
                        MapServiceImpl.log.debug("\u5f00\u59cb\u52a0\u8f7d\u5c0f\u52a8\u7269....");
                        Animal animal = NotPlayerServiceImpl.getInstance().buildAnimalInstance(npcData.modelID);
                        if (animal != null) {
                            animal.live(map);
                            animal.setOrgX(npcData.x);
                            animal.setOrgY(npcData.y);
                            animal.setCellX(npcData.x);
                            animal.setCellY(npcData.y);
                            map.getAnimalList().add(animal);
                            MapServiceImpl.log.debug(("\u5df2\u52a0\u8f7d\u5c0f\u52a8\u7269 id= " + npcData.modelID));
                        } else {
                            LogWriter.println("\u672a\u80fd\u52a0\u8f7d\u5c0f\u52a8\u7269\uff1a" + npcData.modelID + "---" + map.getName());
                            MapServiceImpl.log.debug(("\u672a\u80fd\u52a0\u8f7d\u5c0f\u52a8\u7269\uff1a" + npcData.modelID + "---" + map.getName()));
                        }
                    } else if (npcData.modelID.startsWith("r")) {
                        RoadInstructPlate roadPlate = NotPlayerServiceImpl.getInstance().buildRoadPlate(npcData.modelID);
                        if (roadPlate != null) {
                            roadPlate.setCellX(npcData.x);
                            roadPlate.setCellY(npcData.y);
                            map.getRoadPlateList().add(roadPlate);
                        } else {
                            LogWriter.println("\u672a\u80fd\u52a0\u8f7d\u8def\u724c\uff1a" + npcData.modelID + "---" + map.getName());
                        }
                    } else if (npcData.modelID.startsWith("d")) {
                        DoorPlate doorPlate = NotPlayerServiceImpl.getInstance().buildDoorPlate(npcData.modelID);
                        if (doorPlate != null) {
                            doorPlate.setCellX(npcData.x);
                            doorPlate.setCellY(npcData.y);
                            map.getDoorPlateList().add(doorPlate);
                        } else {
                            MapServiceImpl.log.error(("\u672a\u80fd\u52a0\u8f7d\u5ba4\u5185\u95e8\u724c\uff1a" + npcData.modelID + "---" + map.getName()));
                        }
                    } else if (npcData.modelID.startsWith("g")) {
                        TaskGear gear = NotPlayerServiceImpl.getInstance().buildGearInstance(npcData.modelID);
                        MapServiceImpl.log.debug(("gear = " + gear));
                        if (gear != null) {
                            gear.setCellX(npcData.x);
                            gear.setCellY(npcData.y);
                            map.getTaskGearList().add(gear);
                        } else {
                            MapServiceImpl.log.error(("\u672a\u80fd\u52a0\u8f7d\u4efb\u52a1\u673a\u5173\uff1a" + npcData.modelID + "---" + map.getName()));
                        }
                    } else if (npcData.modelID.startsWith("b")) {
                        Box box = map.existBox(npcData.modelID);
                        if (box == null) {
                            box = NotPlayerServiceImpl.getInstance().buildBoxInstance(npcData.modelID);
                            if (box != null) {
                                box.addRandomLocation(npcData.x, npcData.y);
                                box.live(map);
                                map.getBoxList().add(box);
                            } else {
                                LogWriter.println("\u672a\u80fd\u52a0\u8f7d\u5b9d\u7bb1\uff1a" + npcData.modelID + "---" + map.getName());
                            }
                        } else {
                            box.addRandomLocation(npcData.x, npcData.y);
                        }
                    } else if (npcData.modelID.startsWith("t")) {
                        GroundTaskGoods taskGoods = NotPlayerServiceImpl.getInstance().buildGroundTaskGood(npcData.modelID);
                        if (taskGoods != null) {
                            taskGoods.setCellX(npcData.x);
                            taskGoods.setCellY(npcData.y);
                            taskGoods.live(map);
                            map.getGroundTaskGoodsList().add(taskGoods);
                        } else {
                            LogWriter.println("\u672a\u80fd\u52a0\u8f7d\u5730\u56fe\u4e0a\u7684\u4efb\u52a1\u7269\u54c1\uff1a" + npcData.modelID + "---" + map.getName());
                        }
                    }
                }
            }
            map.refreshBox();
            return map;
        } catch (Exception e) {
            MapServiceImpl.log.error("build map error : ", (Throwable) e);
            return null;
        }
    }

    public Map buildDungeonMap(final byte _pattern, final MapModelData _mapModelData, final String _dungeonName, final boolean _monsterAboutExsits) {
        Map map = new Map(_mapModelData.id, String.valueOf(_dungeonName) + " . " + _mapModelData.name);
        try {
            map.setBornX(_mapModelData.bornX);
            map.setBornY(_mapModelData.bornY);
            map.setHeight(_mapModelData.height);
            map.setWidth(_mapModelData.width);
            map.setModifiable(_mapModelData.modifiable);
            map.setPKMark(_mapModelData.pkMark);
            map.setTileID(_mapModelData.tileImageID);
            map.setMapType(EMapType.getMapType(_mapModelData.mapTypeValue));
            map.setMonsterModelIDAbout(_mapModelData.monsterModelIDAbout);
            map.setWeather(EMapWeather.get(_mapModelData.mapWeatherValue));
            short[] relation = MapRelationDict.getInstance().getRelationByMapID(map.getID());
            if (relation != null) {
                map.setMiniImageID(relation[1]);
                map.setTargetMapIDAfterDie(relation[2]);
                map.setTargetMapIDAfterUseGoods(relation[3]);
                map.setMozuTargetMapIDAfterDie(relation[8]);
                map.setMozuTargetMapIDAfterUseGoods(relation[9]);
                if (relation[4] != 0) {
                    if (relation[5] != 0) {
                        AreaDict.getInstance().getAreaByID(relation[4]).add(map, true, relation[6], relation[7]);
                    } else {
                        AreaDict.getInstance().getAreaByID(relation[4]).add(map, false, 0, 0);
                    }
                }
            }
            map.bottomCanvasData = _mapModelData.bottomCanvasData;
            map.resourceCanvasMap = _mapModelData.resourceCanvasMap;
            map.elementCanvasData = _mapModelData.elementCanvasData;
            map.elementImageIDList = _mapModelData.elementImageIDList;
            map.fixedNpcImageIDList = _mapModelData.fixedNpcImageIDList;
            map.fixedMonsterImageIDList = _mapModelData.fixedMonsterImageIDList;
            map.groundTaskGoodsImageIDList = _mapModelData.groundTaskGoodsImageIDList;
            map.taskGearImageIDList = _mapModelData.taskGearImageIDList;
            map.cartoonList = _mapModelData.cartoonList;
            map.doorList = _mapModelData.externalPortList.clone();
            map.internalTransportList = _mapModelData.internalPorts;
            map.popMessageList = _mapModelData.popMessageList;
            map.unpassData = _mapModelData.unpassData;
            map.unpassMarkArray = _mapModelData.unpassMarkArray;
            map.transformData1 = _mapModelData.transformData1;
            map.transformData2 = _mapModelData.transformData2;
            map.decoraterList = _mapModelData.decorateObjectList;
            map.resourceTransformData = _mapModelData.resourceTransformData;
            if (_mapModelData.notPlayerObjectList.length > 0) {
                OtherObjectData[] notPlayerObjectList;
                for (int length = (notPlayerObjectList = _mapModelData.notPlayerObjectList).length, i = 0; i < length; ++i) {
                    OtherObjectData npcData = notPlayerObjectList[i];
                    if (npcData.modelID.startsWith("n")) {
                        Npc npc = NotPlayerServiceImpl.getInstance().buildNpcInstance(npcData.modelID);
                        if (npc != null) {
                            npc.live(map);
                            npc.setOrgMap(map);
                            npc.setOrgX(npcData.x);
                            npc.setOrgY(npcData.y);
                            npc.setCellX(npcData.x);
                            npc.setCellY(npcData.y);
                            npc.active();
                            map.getNpcList().add(npc);
                        } else {
                            LogWriter.println("\u672a\u80fd\u52a0\u8f7dNPC\uff1a" + npcData.modelID + "---" + map.getName());
                        }
                    } else if (npcData.modelID.startsWith("m")) {
                        if ((map.getMonsterModelIDAbout() == null && _monsterAboutExsits) || (map.getMonsterModelIDAbout() != null && _monsterAboutExsits)) {
                            Monster monster = null;
                            if (2 == _pattern) {
                                monster = NotPlayerServiceImpl.getInstance().buildMonsterInstance(String.valueOf(npcData.modelID) + "h");
                            } else {
                                monster = NotPlayerServiceImpl.getInstance().buildMonsterInstance(npcData.modelID);
                            }
                            if (monster != null) {
                                monster.live(map);
                                monster.setOrgMap(map);
                                monster.setOrgX(npcData.x);
                                monster.setOrgY(npcData.y);
                                monster.setCellX(npcData.x);
                                monster.setCellY(npcData.y);
                                monster.active(npcData.movePath);
                                map.getMonsterList().add(monster);
                            } else {
                                LogWriter.println("\u672a\u80fd\u52a0\u8f7d\u602a\u7269\uff1a" + npcData.modelID + "---" + map.getName());
                            }
                        }
                    } else if (npcData.modelID.startsWith("a")) {
                        Animal animal = NotPlayerServiceImpl.getInstance().buildAnimalInstance(npcData.modelID);
                        if (animal != null) {
                            animal.live(map);
                            animal.setOrgX(npcData.x);
                            animal.setOrgY(npcData.y);
                            animal.setCellX(npcData.x);
                            animal.setCellY(npcData.y);
                            map.getAnimalList().add(animal);
                        } else {
                            LogWriter.println("\u672a\u80fd\u52a0\u8f7d\u5c0f\u52a8\u7269\uff1a" + npcData.modelID + "---" + map.getName());
                        }
                    } else if (npcData.modelID.startsWith("r")) {
                        RoadInstructPlate roadPlate = NotPlayerServiceImpl.getInstance().buildRoadPlate(npcData.modelID);
                        if (roadPlate != null) {
                            roadPlate.setCellX(npcData.x);
                            roadPlate.setCellY(npcData.y);
                            map.getRoadPlateList().add(roadPlate);
                        } else {
                            LogWriter.println("\u672a\u80fd\u52a0\u8f7d\u8def\u724c\uff1a" + npcData.modelID + "---" + map.getName());
                        }
                    } else if (npcData.modelID.startsWith("d")) {
                        DoorPlate doorPlate = NotPlayerServiceImpl.getInstance().buildDoorPlate(npcData.modelID);
                        if (doorPlate != null) {
                            doorPlate.setCellX(npcData.x);
                            doorPlate.setCellY(npcData.y);
                            map.getDoorPlateList().add(doorPlate);
                        } else {
                            LogWriter.println("\u672a\u80fd\u52a0\u8f7d\u5ba4\u5185\u95e8\u724c\uff1a" + npcData.modelID + "---" + map.getName());
                        }
                    } else if (npcData.modelID.startsWith("g")) {
                        TaskGear gear = NotPlayerServiceImpl.getInstance().buildGearInstance(npcData.modelID);
                        if (gear != null) {
                            gear.setCellX(npcData.x);
                            gear.setCellY(npcData.y);
                            map.getTaskGearList().add(gear);
                        } else {
                            LogWriter.println("\u672a\u80fd\u52a0\u8f7d\u4efb\u52a1\u673a\u5173\uff1a" + npcData.modelID + "---" + map.getName());
                        }
                    } else if (npcData.modelID.startsWith("b")) {
                        if ((map.getMonsterModelIDAbout() == null && _monsterAboutExsits) || (map.getMonsterModelIDAbout() != null && _monsterAboutExsits)) {
                            Box box = map.existBox(npcData.modelID);
                            if (box == null) {
                                box = NotPlayerServiceImpl.getInstance().buildBoxInstance(npcData.modelID);
                                if (box != null) {
                                    box.addRandomLocation(npcData.x, npcData.y);
                                    box.live(map);
                                    map.getBoxList().add(box);
                                } else {
                                    LogWriter.println("\u672a\u80fd\u52a0\u8f7d\u5b9d\u7bb1\uff1a" + npcData.modelID + "---" + map.getName());
                                }
                            } else {
                                box.addRandomLocation(npcData.x, npcData.y);
                            }
                        }
                    } else if (npcData.modelID.startsWith("t")) {
                        GroundTaskGoods taskGoods = NotPlayerServiceImpl.getInstance().buildGroundTaskGood(npcData.modelID);
                        if (taskGoods != null) {
                            taskGoods.setCellX(npcData.x);
                            taskGoods.setCellY(npcData.y);
                            taskGoods.live(map);
                            map.getGroundTaskGoodsList().add(taskGoods);
                        } else {
                            LogWriter.println("\u672a\u80fd\u52a0\u8f7d\u5730\u56fe\u4e0a\u7684\u4efb\u52a1\u7269\u54c1\uff1a" + npcData.modelID + "---" + map.getName());
                        }
                    }
                }
            }
            Door[] doorList;
            for (int length2 = (doorList = map.doorList).length, j = 0; j < length2; ++j) {
                Door door = doorList[j];
                door.targetMapName = MapModelDataDict.getInstance().getMapModelData(door.targetMapID).name;
                if (door.monsterIDAbout != null) {
                    for (final ME2GameObject monster2 : map.getMonsterList()) {
                        if (-1 != ((Monster) monster2).getModelID().indexOf(door.monsterIDAbout)) {
                            door.visible = false;
                            break;
                        }
                    }
                }
            }
            map.refreshBox();
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public final ArrayList<HeroPlayer> getAllPlayerListInCircle(final Map _map, final int _cellX, final int _cellY, final int _radius) {
        if (_map == null) {
            return null;
        }
        ME2ObjectList mapPlayerList = _map.getPlayerList();
        ArrayList<HeroPlayer> rangePlayerList = new ArrayList<HeroPlayer>();
        for (final ME2GameObject player : mapPlayerList) {
            boolean radius = (_cellX - player.getCellX()) * (_cellX - player.getCellX()) + (_cellY - player.getCellY()) * (_cellY - player.getCellY()) <= _radius * _radius;
            if (player.isEnable() && !player.isDead() && radius) {
                int i;
                for (i = 0; i < rangePlayerList.size(); ++i) {
                    boolean isrange = (_cellX - player.getCellX()) * (_cellX - player.getCellX()) + (_cellY - player.getCellY()) * (_cellY - player.getCellY()) <= (_cellX - rangePlayerList.get(i).getCellX()) * (_cellX - rangePlayerList.get(i).getCellX()) + (_cellY - rangePlayerList.get(i).getCellY()) * (_cellY - rangePlayerList.get(i).getCellY());
                    if (isrange) {
                        break;
                    }
                }
                rangePlayerList.add(i, (HeroPlayer) player);
            }
        }
        return rangePlayerList;
    }

    public final ArrayList<HeroPlayer> getMonsterValidateTargetListInCircle(final Map _map, final Monster _monster, final int _radius) {
        if (_map.getPlayerList().size() == 0) {
            return null;
        }
        ArrayList<HeroPlayer> rangePlayerList = new ArrayList<HeroPlayer>();
        for (int j = 0; j < _map.getPlayerList().size(); ++j) {
            ME2GameObject player;
            try {
                player = _map.getPlayerList().get(j);
            } catch (Exception e) {
                return null;
            }
            boolean radius = (_monster.getCellX() - player.getCellX()) * (_monster.getCellX() - player.getCellX()) + (_monster.getCellY() - player.getCellY()) * (_monster.getCellY() - player.getCellY()) <= _radius * _radius;
            if (player.isEnable() && !player.isDead() && player.isVisible() && player.getClan() != _monster.getClan() && radius) {
                int i;
                for (i = 0; i < rangePlayerList.size(); ++i) {
                    boolean isrange = (_monster.getCellX() - player.getCellX()) * (_monster.getCellX() - player.getCellX()) + (_monster.getCellY() - player.getCellY()) * (_monster.getCellY() - player.getCellY()) <= (_monster.getCellX() - rangePlayerList.get(i).getCellX()) * (_monster.getCellX() - rangePlayerList.get(i).getCellX()) + (_monster.getCellY() - rangePlayerList.get(i).getCellY()) * (_monster.getCellY() - rangePlayerList.get(i).getCellY());
                    if (isrange) {
                        break;
                    }
                }
                rangePlayerList.add(i, (HeroPlayer) player);
            }
        }
        return (rangePlayerList.size() == 0) ? null : rangePlayerList;
    }

    public final ArrayList<ME2GameObject> getFriendsObjectListInForeRange(final Map _map, final int _width, final int _length, final ME2GameObject _host, final int _numsLimit) {
        ArrayList<ME2GameObject> rangeObjectList = new ArrayList<ME2GameObject>();
        int xRadio = _width / 2;
        int leftX = 0;
        int rightX = 0;
        int upY = 0;
        int downY = 0;
        switch (_host.getDirection()) {
            case 1: {
                leftX = _host.getCellX() - xRadio;
                rightX = _host.getCellX() + xRadio;
                upY = _host.getCellY() - _length;
                downY = _host.getCellY() - 1;
                break;
            }
            case 2: {
                leftX = _host.getCellX() - xRadio;
                rightX = _host.getCellX() + xRadio;
                upY = _host.getCellY() + 1;
                downY = _host.getCellY() + _length;
                break;
            }
            case 3: {
                leftX = _host.getCellX() - _length;
                rightX = _host.getCellX() - 1;
                upY = _host.getCellY() - xRadio;
                downY = _host.getCellY() + xRadio;
                break;
            }
            case 4: {
                leftX = _host.getCellX() + 1;
                rightX = _host.getCellX() + _length;
                upY = _host.getCellY() - xRadio;
                downY = _host.getCellY() + xRadio;
                break;
            }
            default: {
                return null;
            }
        }
        if (_host.getObjectType() == EObjectType.PLAYER) {
            HeroPlayer player = null;
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            for (int i = 0; i < mapPlayerList.size(); ++i) {
                player = (HeroPlayer) mapPlayerList.get(i);
                if (player.isEnable() && !player.isDead() && _host.getClan() == player.getClan() && player.getCellX() >= leftX && player.getCellX() <= rightX && player.getCellY() >= upY && player.getCellY() <= downY) {
                    rangeObjectList.add(player);
                    if (_numsLimit != -1 && rangeObjectList.size() == _numsLimit) {
                        return rangeObjectList;
                    }
                }
            }
        } else if (_host.getObjectType() == EObjectType.MONSTER) {
            ME2ObjectList mapMonsterList = _map.getMonsterList();
            Monster monster = null;
            for (int i = 0; i < mapMonsterList.size(); ++i) {
                monster = (Monster) mapMonsterList.get(i);
                if (_host.getID() != monster.getID() && monster.getCellX() >= leftX && monster.getCellX() <= rightX && monster.getCellY() >= upY && monster.getCellY() <= downY) {
                    rangeObjectList.add(monster);
                    if (_numsLimit != -1 && rangeObjectList.size() == _numsLimit) {
                        return rangeObjectList;
                    }
                }
            }
        }
        return rangeObjectList;
    }

    public final ArrayList<HeroPlayer> getFriendsPlayerListInForeRange(final HeroPlayer _player, final int _width, final int _length, final int _numsLimit) {
        ArrayList<HeroPlayer> rangePlayerList = new ArrayList<HeroPlayer>();
        int xRadio = _width / 2;
        int leftX = 0;
        int rightX = 0;
        int upY = 0;
        int downY = 0;
        switch (_player.getDirection()) {
            case 1: {
                leftX = _player.getCellX() - xRadio;
                rightX = _player.getCellX() + xRadio;
                upY = _player.getCellY() - _length;
                downY = _player.getCellY() - 1;
                break;
            }
            case 2: {
                leftX = _player.getCellX() - xRadio;
                rightX = _player.getCellX() + xRadio;
                upY = _player.getCellY() + 1;
                downY = _player.getCellY() + _length;
                break;
            }
            case 3: {
                leftX = _player.getCellX() - _length;
                rightX = _player.getCellX() - 1;
                upY = _player.getCellY() - xRadio;
                downY = _player.getCellY() + xRadio;
                break;
            }
            case 4: {
                leftX = _player.getCellX() + 1;
                rightX = _player.getCellX() + _length;
                upY = _player.getCellY() - xRadio;
                downY = _player.getCellY() + xRadio;
                break;
            }
            default: {
                return null;
            }
        }
        HeroPlayer player = null;
        ME2ObjectList mapPlayerList = _player.where().getPlayerList();
        for (int i = 0; i < mapPlayerList.size(); ++i) {
            player = (HeroPlayer) mapPlayerList.get(i);
            if (player.isEnable() && !player.isDead() && _player.getClan() == player.getClan() && player.getCellX() >= leftX && player.getCellX() <= rightX && player.getCellY() >= upY && player.getCellY() <= downY) {
                rangePlayerList.add(player);
                if (_numsLimit != -1 && rangePlayerList.size() == _numsLimit) {
                    return rangePlayerList;
                }
            }
        }
        return rangePlayerList;
    }

    public final ArrayList<ME2GameObject> getAttackableObjectListInForeRange(final Map _map, final int _width, final int _length, final ME2GameObject _attacker, final int _numsLimit) {
        ArrayList<ME2GameObject> rangeObjectList = new ArrayList<ME2GameObject>();
        int xRadio = _width / 2;
        int leftX = 0;
        int rightX = 0;
        int upY = 0;
        int downY = 0;
        switch (_attacker.getDirection()) {
            case 1: {
                leftX = _attacker.getCellX() - xRadio;
                rightX = _attacker.getCellX() + xRadio;
                upY = _attacker.getCellY() - _length;
                downY = _attacker.getCellY() + 1;
                break;
            }
            case 2: {
                leftX = _attacker.getCellX() - xRadio;
                rightX = _attacker.getCellX() + xRadio;
                upY = _attacker.getCellY() + 1;
                downY = _attacker.getCellY() + _length;
                break;
            }
            case 3: {
                leftX = _attacker.getCellX() - _length;
                rightX = _attacker.getCellX() - 1;
                upY = _attacker.getCellY() - xRadio;
                downY = _attacker.getCellY() + xRadio;
                break;
            }
            case 4: {
                leftX = _attacker.getCellX() + 1;
                rightX = _attacker.getCellX() + _length;
                upY = _attacker.getCellY() - xRadio;
                downY = _attacker.getCellY() + xRadio;
                break;
            }
            default: {
                return null;
            }
        }
        if (_attacker.getObjectType() == EObjectType.PLAYER) {
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            HeroPlayer player = null;
            for (int i = 0; i < mapPlayerList.size(); ++i) {
                player = (HeroPlayer) mapPlayerList.get(i);
                if (player.isEnable() && !player.isDead() && (_attacker.getClan() != player.getClan() || ((HeroPlayer) _attacker).getDuelTargetUserID() == player.getUserID()) && player.getCellX() >= leftX && player.getCellX() <= rightX && player.getCellY() >= upY && player.getCellY() <= downY) {
                    rangeObjectList.add(player);
                    if (_numsLimit != -1 && rangeObjectList.size() == _numsLimit) {
                        return rangeObjectList;
                    }
                }
            }
            ME2ObjectList mapMonsterList = _map.getMonsterList();
            Monster monster = null;
            for (int j = 0; j < mapMonsterList.size(); ++j) {
                monster = (Monster) mapMonsterList.get(j);
                if (monster.isVisible() && _attacker.getClan() != monster.getClan() && monster.getCellX() >= leftX && monster.getCellX() <= rightX && monster.getCellY() >= upY && monster.getCellY() <= downY) {
                    rangeObjectList.add(monster);
                    if (_numsLimit != -1 && rangeObjectList.size() == _numsLimit) {
                        return rangeObjectList;
                    }
                }
            }
        } else if (_attacker.getObjectType() == EObjectType.MONSTER) {
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            HeroPlayer player = null;
            for (int i = 0; i < mapPlayerList.size(); ++i) {
                player = (HeroPlayer) mapPlayerList.get(i);
                if (player.isEnable() && !player.isDead() && _attacker.getClan() != player.getClan() && player.getCellX() >= leftX && player.getCellX() <= rightX && player.getCellY() >= upY && player.getCellY() >= downY) {
                    rangeObjectList.add(player);
                    if (_numsLimit != -1 && rangeObjectList.size() == _numsLimit) {
                        return rangeObjectList;
                    }
                }
            }
        }
        return rangeObjectList;
    }

    public final ArrayList<ME2GameObject> getAttackableObjectListInRange(final Map _map, final int _cellX, final int _cellY, final int _range, final ME2GameObject _attacker, final int _numsLimit) {
        ArrayList<ME2GameObject> rangeObjectList = new ArrayList<ME2GameObject>();
        if (_attacker.getObjectType() == EObjectType.PLAYER) {
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            HeroPlayer player = null;
            for (int i = 0; i < mapPlayerList.size(); ++i) {
                player = (HeroPlayer) mapPlayerList.get(i);
                if (player.isEnable() && !player.isDead() && (_attacker.getClan() != player.getClan() || ((HeroPlayer) _attacker).getDuelTargetUserID() == player.getUserID()) && Math.abs(player.getCellX() - _cellX) <= _range && Math.abs(player.getCellY() - _cellY) <= _range) {
                    rangeObjectList.add(player);
                    if (_numsLimit != -1 && rangeObjectList.size() == _numsLimit) {
                        return rangeObjectList;
                    }
                }
            }
            ME2ObjectList mapMonsterList = _map.getMonsterList();
            Monster monster = null;
            for (int j = 0; j < mapMonsterList.size(); ++j) {
                monster = (Monster) mapMonsterList.get(j);
                if (monster.isVisible() && _attacker.getClan() != monster.getClan() && Math.abs(monster.getCellX() - _cellX) <= _range && Math.abs(monster.getCellY() - _cellY) <= _range) {
                    rangeObjectList.add(monster);
                    if (_numsLimit != -1 && rangeObjectList.size() == _numsLimit) {
                        return rangeObjectList;
                    }
                }
            }
        } else if (_attacker.getObjectType() == EObjectType.MONSTER) {
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            HeroPlayer player = null;
            for (int i = 0; i < mapPlayerList.size(); ++i) {
                player = (HeroPlayer) mapPlayerList.get(i);
                if (player.isEnable() && !player.isDead() && _attacker.getClan() != player.getClan() && Math.abs(player.getCellX() - _cellX) <= _range && Math.abs(player.getCellY() - _cellY) <= _range) {
                    rangeObjectList.add(player);
                    if (_numsLimit != -1 && rangeObjectList.size() == _numsLimit) {
                        return rangeObjectList;
                    }
                }
            }
        }
        return rangeObjectList;
    }

    public final ArrayList<ME2GameObject> getFriendsObjectInRange(final Map _map, final int _cellX, final int _cellY, final int _range, final ME2GameObject _host, final int _numsLimit) {
        ArrayList<ME2GameObject> rangeObjectList = new ArrayList<ME2GameObject>();
        if (_host.getObjectType() == EObjectType.PLAYER) {
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            HeroPlayer player = null;
            for (int i = 0; i < mapPlayerList.size(); ++i) {
                player = (HeroPlayer) mapPlayerList.get(i);
                if (player.isEnable() && !player.isDead() && _host.getClan() == player.getClan() && Math.abs(player.getCellX() - _cellX) <= _range && Math.abs(player.getCellY() - _cellY) <= _range) {
                    rangeObjectList.add(player);
                    if (_numsLimit != -1 && rangeObjectList.size() == _numsLimit) {
                        return rangeObjectList;
                    }
                }
            }
        } else if (_host.getObjectType() == EObjectType.MONSTER) {
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            HeroPlayer player = null;
            for (int i = 0; i < mapPlayerList.size(); ++i) {
                player = (HeroPlayer) mapPlayerList.get(i);
                if (player.isEnable() && !player.isDead() && Math.abs(player.getCellX() - _cellX) <= _range && Math.abs(player.getCellY() - _cellY) <= _range) {
                    rangeObjectList.add(player);
                    if (_numsLimit != -1 && rangeObjectList.size() == _numsLimit) {
                        return rangeObjectList;
                    }
                }
            }
        }
        return rangeObjectList;
    }

    public final ArrayList<HeroPlayer> getFriendsPlayerInRange(final HeroPlayer _player, final int _cellX, final int _cellY, final int _range, final int _numsLimit) {
        ArrayList<HeroPlayer> rangePlayerList = new ArrayList<HeroPlayer>();
        ME2ObjectList mapPlayerList = _player.where().getPlayerList();
        HeroPlayer player = null;
        for (int i = 0; i < mapPlayerList.size(); ++i) {
            player = (HeroPlayer) mapPlayerList.get(i);
            if (player.isEnable() && !player.isDead() && _player.getClan() == player.getClan() && Math.abs(player.getCellX() - _cellX) <= _range && Math.abs(player.getCellY() - _cellY) <= _range) {
                rangePlayerList.add(player);
                if (_numsLimit != -1 && rangePlayerList.size() == _numsLimit) {
                    return rangePlayerList;
                }
            }
        }
        return rangePlayerList;
    }

    public final ArrayList<HeroPlayer> getGroupPlayerInRange(final HeroPlayer _player, final int _cellX, final int _cellY, final int _range, final int _numsLimit) {
        if (_player.getObjectType() == EObjectType.PLAYER && _player.getGroupID() > 0) {
            Map map = _player.where();
            ArrayList<HeroPlayer> playerList = new ArrayList<HeroPlayer>();
            ME2ObjectList mapPlayerList = map.getPlayerList();
            HeroPlayer player = null;
            for (int i = 0; i < mapPlayerList.size(); ++i) {
                player = (HeroPlayer) mapPlayerList.get(i);
                if (player.getGroupID() == _player.getGroupID() && _player.getClan() == player.getClan() && player.isEnable() && !player.isDead() && Math.abs(player.getCellX() - _cellX) <= _range && Math.abs(player.getCellY() - _cellY) <= _range) {
                    playerList.add(player);
                    if (_numsLimit != -1 && playerList.size() == _numsLimit) {
                        break;
                    }
                }
            }
            return playerList;
        }
        return null;
    }

    public final ArrayList<HeroPlayer> getGroupPlayerListInForeRange(final HeroPlayer _player, final int _width, final int _length, final int _numsLimit) {
        ArrayList<HeroPlayer> playerList = new ArrayList<HeroPlayer>();
        int xRadio = _width / 2;
        int leftX = 0;
        int rightX = 0;
        int upY = 0;
        int downY = 0;
        switch (_player.getDirection()) {
            case 1: {
                leftX = _player.getCellX() - xRadio;
                rightX = _player.getCellX() + xRadio;
                upY = _player.getCellY() - _length;
                downY = _player.getCellY() - 1;
                break;
            }
            case 2: {
                leftX = _player.getCellX() - xRadio;
                rightX = _player.getCellX() + xRadio;
                upY = _player.getCellY() + 1;
                downY = _player.getCellY() + _length;
                break;
            }
            case 3: {
                leftX = _player.getCellX() - _length;
                rightX = _player.getCellX() - 1;
                upY = _player.getCellY() - xRadio;
                downY = _player.getCellY() + xRadio;
                break;
            }
            case 4: {
                leftX = _player.getCellX() + 1;
                rightX = _player.getCellX() + _length;
                upY = _player.getCellY() - xRadio;
                downY = _player.getCellY() + xRadio;
                break;
            }
            default: {
                return null;
            }
        }
        HeroPlayer player = null;
        ME2ObjectList mapPlayerList = _player.where().getPlayerList();
        for (int i = 0; i < mapPlayerList.size(); ++i) {
            player = (HeroPlayer) mapPlayerList.get(i);
            if (player.getGroupID() == _player.getGroupID() && _player.getClan() == player.getClan() && player.isEnable() && !player.isDead() && player.getCellX() >= leftX && player.getCellX() <= rightX && player.getCellY() >= upY && player.getCellY() <= downY) {
                playerList.add(player);
                if (_numsLimit != -1 && playerList.size() == _numsLimit) {
                    break;
                }
            }
        }
        return playerList;
    }

    public byte getPlayerMapWorldType(final HeroPlayer player) {
        short mapID = player.where().getID();
        Map map = this.getNormalMapByID(mapID);
        if (map.getMapType() == EMapType.DUNGEON) {
            return 0;
        }
        byte worldType = WorldMapDict.getInstance().getTypeWorldMapByMapID(mapID);
        return worldType;
    }

    public byte getPlayerMapWorldType(short mapID, final HeroPlayer player) {
        Map map = this.getNormalMapByID(mapID);
        if (map.getMapType() == EMapType.DUNGEON) {
            Dungeon dungeon = DungeonServiceImpl.getInstance().getWhereDungeon(player.getUserID());
            if (dungeon != null) {
                mapID = WorldMapDict.getInstance().getIncludeDungeonMapID(dungeon.getEntranceMap().getID());
                MapServiceImpl.log.debug(("\u83b7\u53d6\u73a9\u5bb6\u8981\u53bb\u7684\u90a3\u4e00\u754c\uff0c\u5165\u53e3\u5730\u56feid=" + mapID));
            }
        }
        byte worldType = WorldMapDict.getInstance().getTypeWorldMapByMapID(mapID);
        return worldType;
    }
}
