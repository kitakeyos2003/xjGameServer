// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.service;

import java.util.Map;
import java.io.IOException;
import hero.map.detail.Cartoon;
import hero.map.detail.PopMessage;
import hero.map.detail.Door;
import hero.npc.dict.GearDataDict;
import hero.npc.dict.GroundTaskGoodsDataDict;
import hero.npc.dict.MonsterDataDict;
import hero.npc.dict.NpcDataDict;
import hero.map.Decorater;
import hero.map.detail.OtherObjectData;
import java.util.HashMap;
import hero.share.service.DataConvertor;
import java.io.FileInputStream;
import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import hero.share.service.LogWriter;
import hero.map.MapModelData;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class MapModelDataDict {

    private static Logger log;
    private FastMap<Short, MapModelData> dictionary;
    private static MapModelDataDict instance;

    static {
        MapModelDataDict.log = Logger.getLogger((Class) MapModelDataDict.class);
    }

    public static MapModelDataDict getInstance() {
        if (MapModelDataDict.instance == null) {
            MapModelDataDict.instance = new MapModelDataDict();
        }
        return MapModelDataDict.instance;
    }

    private MapModelDataDict() {
        this.dictionary = (FastMap<Short, MapModelData>) new FastMap();
    }

    public MapModelData getMapModelData(final short _mapID) {
        MapModelData mapModelData = (MapModelData) this.dictionary.get(_mapID);
        if (mapModelData == null) {
            LogWriter.println("\u65e0\u6cd5\u627e\u5230\u5730\u56fe\u6a21\u677f\uff0c\u7f16\u53f7\uff1a" + _mapID);
        }
        return mapModelData;
    }

    public ArrayList<MapModelData> getMapModelDataList() {
        Iterator<MapModelData> iterator = this.dictionary.values().iterator();
        ArrayList<MapModelData> mapModelDataList = new ArrayList<MapModelData>();
        while (iterator.hasNext()) {
            mapModelDataList.add(iterator.next());
        }
        return mapModelDataList;
    }

    protected void init(final String _dataPath) {
        File fileList;
        try {
            fileList = new File(_dataPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u5730\u56fe\u6a21\u677f\u6587\u4ef6\u76ee\u5f55\uff1a" + _dataPath);
            return;
        }
        try {
            File[] mapList = fileList.listFiles();
            if (mapList.length > 0) {
                this.dictionary.clear();
            }
            File[] array;
            for (int length = (array = mapList).length, i = 0; i < length; ++i) {
                File dataFile = array[i];
                if (dataFile.getName().endsWith(".map")) {
                    MapModelDataDict.log.debug(("== mapFile name = " + dataFile.getName()));
                    MapModelData mapData = this.loadMapFile(dataFile);
                    MapModelData existMapData = (MapModelData) this.dictionary.get(mapData.id);
                    if (existMapData == null) {
                        this.dictionary.put(mapData.id, mapData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MapModelData loadMapFile(final File _mapFile) {
        FileInputStream fis = null;
        MapModelData mapData = new MapModelData();
        byte[] twoBytes = new byte[2];
        ArrayList<Integer> movePathList = new ArrayList<Integer>();
        try {
            fis = new FileInputStream(_mapFile);
            fis.read();
            fis.read();
            fis.read();
            fis.read(twoBytes, 0, 2);
            mapData.id = DataConvertor.bytes2Short(twoBytes);
            if (mapData.id != Short.parseShort(_mapFile.getName().substring(0, _mapFile.getName().indexOf(".map")))) {
                MapModelDataDict.log.error("\u5730\u56fe\u540d\u79f0 \u548c \u5730\u56fe ID \u4e0d\u5339\u914d\uff0c\u627e\u7b56\u5212\u53bb\uff01");
                return null;
            }
            MapModelDataDict.log.debug(("\u5f00\u59cb\u8bfb\u53d6.map\u6587\u4ef6: id=" + mapData.id));
            fis.read(twoBytes, 0, 2);
            mapData.tileImageID = DataConvertor.bytes2Short(twoBytes);
            fis.read();
            fis.read();
            fis.read(twoBytes, 0, 2);
            mapData.pkMark = twoBytes[0];
            mapData.modifiable = (twoBytes[1] == 1);
            mapData.mapTypeValue = fis.read();
            byte[] monsterIDAbout = new byte[fis.read()];
            if (monsterIDAbout.length > 0) {
                fis.read(monsterIDAbout);
                mapData.monsterModelIDAbout = DataConvertor.bytes2String(monsterIDAbout);
            }
            int length = fis.read();
            byte[] mapName = new byte[length];
            fis.read(mapName);
            mapData.name = DataConvertor.bytes2String(mapName);
            mapData.mapWeatherValue = fis.read();
            int width = fis.read();
            int height = fis.read();
            mapData.width = (short) width;
            mapData.height = (short) height;
            mapData.animNum = fis.read();
            int npcNum = fis.read();
            fis.read(mapData.bottomCanvasData = new byte[height * width], 0, height * width);
            fis.read(mapData.transformData1 = new byte[height * width], 0, height * width);
            short[] temp = new short[height * width];
            mapData.transformData2 = new byte[height * width];
            int upTileLayerSize = fis.read();
            for (int z = 0; z < width * height; ++z) {
                temp[z] = -1;
                mapData.transformData2[z] = 0;
            }
            for (int tileIndex = 0; tileIndex < upTileLayerSize; ++tileIndex) {
                int row = fis.read();
                int column = fis.read();
                short tileId = (short) fis.read();
                temp[row * width + column] = tileId;
            }
            for (int tileIndex = 0; tileIndex < upTileLayerSize; ++tileIndex) {
                int row = fis.read();
                int column = fis.read();
                byte tileTransform = (byte) fis.read();
                mapData.transformData2[row * width + column] = tileTransform;
            }
            MapModelDataDict.log.debug(("mapid=" + mapData.id + ",name[" + mapData.name + "] transformData2 size=" + mapData.transformData2.length));
            mapData.elementImageIDList = new ArrayList<Short>();
            ArrayList<Short> elementList = new ArrayList<Short>();
            Map<Short, String> hashMap = new HashMap<Short, String>();
            for (short h = 0; h < height; ++h) {
                for (short w = 0; w < width; ++w) {
                    short elementImageID = temp[h * width + w];
                    if (elementImageID > -1) {
                        elementList.add(w);
                        elementList.add(h);
                        elementList.add(elementImageID);
                        if (!mapData.elementImageIDList.contains(elementImageID)) {
                            mapData.elementImageIDList.add(elementImageID);
                        }
                    }
                }
            }
            mapData.elementCanvasData = new short[elementList.size()];
            for (short l = 0; l < mapData.elementCanvasData.length; ++l) {
                mapData.elementCanvasData[l] = elementList.get(l);
            }
            mapData.bornX = (short) fis.read();
            mapData.bornY = (short) fis.read();
            int npcModelIDBytesLength = 0;
            int npcNameBytesLength = 0;
            mapData.decorateObjectList = new OtherObjectData[mapData.animNum];
            MapModelDataDict.log.debug(("mapData animNum = " + mapData.animNum));
            for (short i = 0; i < mapData.animNum; ++i) {
                mapData.decorateObjectList[i] = new OtherObjectData();
                fis.read(twoBytes, 0, 2);
                mapData.decorateObjectList[i].pngId = DataConvertor.bytes2Short(twoBytes);
                npcModelIDBytesLength = fis.read();
                byte[] decModelIDBytes = new byte[npcModelIDBytesLength];
                fis.read(decModelIDBytes, 0, npcModelIDBytesLength);
                String npcModelID = new String(decModelIDBytes, 0, npcModelIDBytesLength).toLowerCase();
                npcNameBytesLength = fis.read();
                if (npcNameBytesLength > 0) {
                    byte[] npcNameBytes = new byte[npcNameBytesLength];
                    fis.read(npcNameBytes, 0, npcNameBytesLength);
                }
                mapData.decorateObjectList[i].animationID = Short.valueOf(npcModelID);
                mapData.decorateObjectList[i].x = (byte) fis.read();
                mapData.decorateObjectList[i].y = (byte) fis.read();
                mapData.decorateObjectList[i].z = (byte) fis.read();
                MapModelDataDict.log.debug(("decorateObjectList[" + i + "].animationID = " + mapData.decorateObjectList[i].animationID));
                int movePathNodeNums = fis.read();
                MapModelDataDict.log.debug(("movePathNodeNums = " + movePathNodeNums));
                if (movePathNodeNums > 0) {
                    mapData.decorateObjectList[i].decorater = new Decorater[movePathNodeNums];
                    for (int n = 0; n < movePathNodeNums; ++n) {
                        fis.read(twoBytes, 0, 2);
                        mapData.decorateObjectList[i].decorater[n] = new Decorater();
                        mapData.decorateObjectList[i].decorater[n].decorateId = DataConvertor.bytes2Short(twoBytes);
                        MapModelDataDict.log.debug(("mapData.decorateObjectList[" + n + "].decorater[" + n + "].decorateId = " + mapData.decorateObjectList[i].decorater[n].decorateId));
                        mapData.decorateObjectList[i].decorater[n].x = (byte) fis.read();
                        mapData.decorateObjectList[i].decorater[n].y = (byte) fis.read();
                        mapData.decorateObjectList[i].decorater[n].z = (byte) fis.read();
                    }
                }
            }
            npcModelIDBytesLength = 0;
            npcNameBytesLength = 0;
            mapData.notPlayerObjectList = new OtherObjectData[npcNum];
            for (short i = 0; i < npcNum; ++i) {
                mapData.notPlayerObjectList[i] = new OtherObjectData();
                fis.read(twoBytes, 0, 2);
                npcModelIDBytesLength = fis.read();
                byte[] npcModelIDBytes = new byte[npcModelIDBytesLength];
                fis.read(npcModelIDBytes, 0, npcModelIDBytesLength);
                String npcModelID = new String(npcModelIDBytes, 0, npcModelIDBytesLength).toLowerCase();
                MapModelDataDict.log.debug(("npc modeID = " + npcModelID));
                if (npcModelID.startsWith("n")) {
                    mapData.notPlayerObjectList[i].animationID = DataConvertor.bytes2Short(twoBytes);
                    if (mapData.fixedNpcImageIDList == null) {
                        mapData.fixedNpcImageIDList = new ArrayList<Short>();
                    }
                    short imageID = Short.parseShort(NpcDataDict.getInstance().getNpcData(npcModelID).imageID);
                    if (!mapData.fixedNpcImageIDList.contains(imageID)) {
                        mapData.fixedNpcImageIDList.add(imageID);
                    }
                } else if (npcModelID.startsWith("m")) {
                    if (mapData.fixedMonsterImageIDList == null) {
                        mapData.fixedMonsterImageIDList = new ArrayList<Short>();
                    }
                    MonsterDataDict.MonsterData monster = MonsterDataDict.getInstance().getMonsterData(npcModelID);
                    MapModelDataDict.log.debug(("map id=" + mapData.id + " -- moster = " + monster.modelID));
                    String imgId = monster.imageID;
                    short imageID = Short.parseShort(imgId);
                    if (!mapData.fixedMonsterImageIDList.contains(imageID)) {
                        mapData.fixedMonsterImageIDList.add(imageID);
                    }
                } else if (npcModelID.startsWith("t")) {
                    if (mapData.groundTaskGoodsImageIDList == null) {
                        mapData.groundTaskGoodsImageIDList = new ArrayList<Short>();
                    }
                    GroundTaskGoodsDataDict.GroundTaskGoodsData gData = GroundTaskGoodsDataDict.getInstance().getTaskGoodsData(npcModelID);
                    short imageID;
                    if (gData != null) {
                        imageID = gData.imageID;
                    } else {
                        imageID = -1;
                        MapModelDataDict.log.warn(("\u52a0\u8f7d\u4efb\u52a1\u673a\u5173\u56fe\u7247\u5931\u8d25npcModelID:" + npcModelID));
                    }
                    if (!mapData.groundTaskGoodsImageIDList.contains(imageID)) {
                        mapData.groundTaskGoodsImageIDList.add(imageID);
                    }
                } else if (npcModelID.startsWith("g")) {
                    if (mapData.taskGearImageIDList == null) {
                        mapData.taskGearImageIDList = new ArrayList<Short>();
                    }
                    short imageID = GearDataDict.getInstance().getGearData(npcModelID).imageID;
                    if (!mapData.taskGearImageIDList.contains(imageID)) {
                        mapData.taskGearImageIDList.add(imageID);
                    }
                }
                npcNameBytesLength = fis.read();
                if (npcNameBytesLength > 0) {
                    byte[] npcNameBytes = new byte[npcNameBytesLength];
                    fis.read(npcNameBytes, 0, npcNameBytesLength);
                }
                mapData.notPlayerObjectList[i].modelID = npcModelID;
                mapData.notPlayerObjectList[i].x = (byte) fis.read();
                mapData.notPlayerObjectList[i].y = (byte) fis.read();
                mapData.notPlayerObjectList[i].z = (byte) fis.read();
                int movePathNodeNums = fis.read();
                if (movePathNodeNums > 0) {
                    movePathList.clear();
                    int x = mapData.notPlayerObjectList[i].x;
                    int y = mapData.notPlayerObjectList[i].y;
                    movePathList.add(x);
                    movePathList.add(y);
                    while (movePathNodeNums > 0) {
                        fis.read(twoBytes, 0, 2);
                        int nodeX = fis.read();
                        int nodeY = fis.read();
                        fis.read();
                        if (nodeX == x) {
                            if (y > nodeY) {
                                while (y > nodeY) {
                                    movePathList.add(x);
                                    movePathList.add(--y);
                                }
                            } else {
                                while (y < nodeY) {
                                    movePathList.add(x);
                                    movePathList.add(++y);
                                }
                            }
                        } else {
                            if (nodeY != y) {
                                LogWriter.println(".map\u6587\u4ef6->\u602a\u7269\u884c\u8d70\u8def\u5f84\u8282\u70b9\u6570\u636e\u9519\u8bef\uff1a" + mapData.name + "---" + mapData.notPlayerObjectList[i].modelID);
                                return null;
                            }
                            if (x > nodeX) {
                                while (x > nodeX) {
                                    movePathList.add(--x);
                                    movePathList.add(y);
                                }
                            } else {
                                while (x < nodeX) {
                                    movePathList.add(++x);
                                    movePathList.add(y);
                                }
                            }
                        }
                        x = nodeX;
                        y = nodeY;
                        --movePathNodeNums;
                    }
                    int pathLength = movePathList.size() / 2;
                    mapData.notPlayerObjectList[i].movePath = new byte[pathLength][2];
                    for (int j = 0; j < pathLength; ++j) {
                        mapData.notPlayerObjectList[i].movePath[j][0] = movePathList.get(j * 2).byteValue();
                        mapData.notPlayerObjectList[i].movePath[j][1] = movePathList.get(j * 2 + 1).byteValue();
                    }
                }
            }
            byte[] npcNameBytes = null;
            fis.read(mapData.resourceCanvasMap = new byte[height * width], 0, height * width);
            fis.read(mapData.resourceTransformData = new byte[height * width], 0, height * width);
            short portsNum = (byte) fis.read();
            mapData.externalPortList = new Door[portsNum];
            byte[] portsInfo = new byte[2];
            for (short i = 0; i < portsNum; ++i) {
                mapData.externalPortList[i] = new Door();
                mapData.externalPortList[i].x = (short) fis.read();
                mapData.externalPortList[i].y = (short) fis.read();
                mapData.externalPortList[i].direction = (byte) fis.read();
                fis.read(portsInfo, 0, 2);
                mapData.externalPortList[i].targetMapID = DataConvertor.bytes2Short(portsInfo);
                MapModelDataDict.log.debug(("@ mapData.externalPortList[" + i + "] targetMapID = " + mapData.externalPortList[i].targetMapID));
                mapData.externalPortList[i].targetMapX = (short) fis.read();
                mapData.externalPortList[i].targetMapY = (short) fis.read();
                mapData.externalPortList[i].visible = true;
                npcModelIDBytesLength = fis.read();
                if (npcModelIDBytesLength > 0) {
                    byte[] npcModelIDBytes = new byte[npcModelIDBytesLength];
                    fis.read(npcModelIDBytes, 0, npcModelIDBytesLength);
                    String monsterModelID = new String(npcModelIDBytes, 0, npcModelIDBytesLength, "UTF-16BE").toLowerCase();
                    mapData.externalPortList[i].monsterIDAbout = monsterModelID;
                }
            }
            MapModelDataDict.log.debug("@ mapData.externalPortList end..");
            int msgNum = fis.read();
            short msgLen = 0;
            byte[] tempMsg = null;
            mapData.popMessageList = new PopMessage[msgNum];
            for (short i = 0; i < msgNum; ++i) {
                mapData.popMessageList[i] = new PopMessage();
                mapData.popMessageList[i].x = (byte) fis.read();
                mapData.popMessageList[i].y = (byte) fis.read();
                fis.read(portsInfo, 0, 2);
                msgLen = DataConvertor.bytes2Short(portsInfo);
                tempMsg = new byte[msgLen];
                fis.read(tempMsg, 0, msgLen);
                mapData.popMessageList[i].msgContent = DataConvertor.bytes2String(tempMsg, msgLen);
            }
            portsInfo = null;
            int internalPortNum = fis.read();
            mapData.internalPorts = new byte[internalPortNum][4];
            for (int n2 = 0; n2 < internalPortNum; ++n2) {
                mapData.internalPorts[n2][0] = (byte) fis.read();
                mapData.internalPorts[n2][1] = (byte) fis.read();
                mapData.internalPorts[n2][2] = (byte) fis.read();
                mapData.internalPorts[n2][3] = (byte) fis.read();
            }
            fis.read(twoBytes, 0, 2);
            short unpassNums = DataConvertor.bytes2Short(twoBytes);
            mapData.unpassData = new byte[unpassNums * 2];
            mapData.unpassMarkArray = new byte[height][width];
            for (int n3 = 0; n3 < mapData.unpassData.length; n3 += 2) {
                mapData.unpassData[n3] = (byte) fis.read();
                mapData.unpassData[n3 + 1] = (byte) fis.read();
                mapData.unpassMarkArray[mapData.unpassData[n3]][mapData.unpassData[n3 + 1]] = 1;
            }
            int cartoonNum = fis.read();
            if (cartoonNum > 0) {
                mapData.cartoonList = new Cartoon[cartoonNum];
                for (short i = 0; i < cartoonNum; ++i) {
                    mapData.cartoonList[i] = new Cartoon();
                    mapData.cartoonList[i].x = (byte) fis.read();
                    mapData.cartoonList[i].y = (byte) fis.read();
                    mapData.cartoonList[i].firstTileIndex = (byte) fis.read();
                    int followFrameCount = fis.read();
                    fis.read(mapData.cartoonList[i].followTileIndexList = new byte[followFrameCount]);
                }
            }
            cartoonNum = fis.read();
            if (cartoonNum > 0) {
                mapData.cartoonList2 = new Cartoon[cartoonNum];
                for (short i = 0; i < cartoonNum; ++i) {
                    mapData.cartoonList2[i] = new Cartoon();
                    mapData.cartoonList2[i].x = (byte) fis.read();
                    mapData.cartoonList2[i].y = (byte) fis.read();
                    mapData.cartoonList2[i].firstTileIndex = (byte) fis.read();
                    int followFrameCount = fis.read();
                    fis.read(mapData.cartoonList2[i].followTileIndexList = new byte[followFrameCount]);
                }
            }
            MapModelDataDict.log.debug((".map\u6587\u4ef6\u8bfb\u53d6\u6210\u529f:" + mapData.id + " [" + mapData.name + "]"));
        } catch (Exception ex) {
            ex.printStackTrace();
            LogWriter.println("error:.map\u6587\u4ef6\u8bfb\u53d6\u5931\u8d25\uff0c\u7f16\u53f7\uff1a" + mapData.id);
            MapModelDataDict.log.error(("error:.map\u6587\u4ef6\u8bfb\u53d6\u5931\u8d25\uff0c\u7f16\u53f7\uff1a" + mapData.id));
            LogWriter.error(this, ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
            } catch (IOException ex2) {
            }
        }
        try {
            if (fis != null) {
                fis.close();
                fis = null;
            }
        } catch (IOException ex3) {
        }
        return mapData;
    }
}
