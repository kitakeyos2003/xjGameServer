// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.service;

import hero.map.MapModelData;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import hero.map.WorldMap;
import java.util.List;
import org.apache.log4j.Logger;

public class WorldMapDict {

    private static Logger log;
    private static WorldMapDict instance;
    private List<WorldMap> shenLongJieMapsList;
    private List<WorldMap> moLongJieMapsList;
    private List<WorldMap> xianJieMapsList;
    private List<WorldMap> worldMapList;
    public Map<Byte, List<WorldMap>> worldMaps;
    private static final byte TYPE_SHEN_LONG_JIE_MAP = 1;
    private static final byte TYPE_MO_LONG_JIE_MAP = 2;
    private static final byte TYPE_XIAN_JIE_MAP = 3;
    public static final byte TYPE_WORLD_MAP = 4;

    static {
        WorldMapDict.log = Logger.getLogger((Class) WorldMapDict.class);
    }

    private WorldMapDict() {
        this.shenLongJieMapsList = new ArrayList<WorldMap>();
        this.moLongJieMapsList = new ArrayList<WorldMap>();
        this.xianJieMapsList = new ArrayList<WorldMap>();
        this.worldMapList = new ArrayList<WorldMap>();
        this.worldMaps = new HashMap<Byte, List<WorldMap>>();
    }

    public static WorldMapDict getInstance() {
        if (WorldMapDict.instance == null) {
            WorldMapDict.instance = new WorldMapDict();
        }
        return WorldMapDict.instance;
    }

    public void load(final MapConfig config) {
        try {
            File fileList = new File(config.world_maps);
            File[] dataFileList = fileList.listFiles();
            File[] array;
            for (int length = (array = dataFileList).length, i = 0; i < length; ++i) {
                File file = array[i];
                if (file.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(file);
                    Element root = document.getRootElement();
                    Iterator<Element> eit = (Iterator<Element>) root.elementIterator();
                    while (eit.hasNext()) {
                        Element e = eit.next();
                        short mapID = Short.parseShort(e.elementTextTrim("mapID"));
                        short cellX = Short.parseShort(e.elementTextTrim("cellX"));
                        short cellY = Short.parseShort(e.elementTextTrim("cellY"));
                        String name = e.elementTextTrim("name");
                        String desc = e.elementTextTrim("desc");
                        short png = Short.parseShort(e.elementTextTrim("png"));
                        short anu = Short.parseShort(e.elementTextTrim("anu"));
                        short width = Short.parseShort(e.elementTextTrim("width"));
                        short height = Short.parseShort(e.elementTextTrim("height"));
                        WorldMap worldMap = new WorldMap();
                        worldMap.mapID = mapID;
                        worldMap.cellX = cellX;
                        worldMap.cellY = cellY;
                        worldMap.name = name;
                        worldMap.desc = desc;
                        worldMap.type = 4;
                        worldMap.anu = anu;
                        worldMap.png = png;
                        worldMap.width = width;
                        worldMap.height = height;
                        this.worldMapList.add(worldMap);
                        WorldMapDict.log.debug(("load world map[4][" + name + "] end."));
                    }
                    WorldMapDict.log.debug(("world map size = " + this.worldMapList.size()));
                    this.worldMaps.put((byte) 4, this.worldMapList);
                }
            }
            fileList = new File(config.world_maps_shen_long_jie);
            dataFileList = fileList.listFiles();
            File[] array2;
            for (int length2 = (array2 = dataFileList).length, j = 0; j < length2; ++j) {
                File file = array2[j];
                if (file.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(file);
                    Element root = document.getRootElement();
                    Iterator<Element> eit = (Iterator<Element>) root.elementIterator();
                    short dungeons = 0;
                    while (eit.hasNext()) {
                        Element e2 = eit.next();
                        short mapID = Short.parseShort(e2.elementTextTrim("mapID"));
                        short cellX = Short.parseShort(e2.elementTextTrim("cellX"));
                        short cellY = Short.parseShort(e2.elementTextTrim("cellY"));
                        String name2 = e2.elementTextTrim("name");
                        String desc2 = e2.elementTextTrim("desc");
                        String ds = e2.elementTextTrim("dungeon_entry_mapID");
                        if (ds != null && ds.trim().length() > 0) {
                            dungeons = Short.parseShort(ds);
                        }
                        WorldMap worldMap2 = new WorldMap();
                        worldMap2.mapID = mapID;
                        worldMap2.cellX = cellX;
                        worldMap2.cellY = cellY;
                        worldMap2.name = name2;
                        worldMap2.desc = desc2;
                        worldMap2.dungeonEntryMapID = dungeons;
                        worldMap2.type = 1;
                        this.shenLongJieMapsList.add(worldMap2);
                        WorldMapDict.log.debug(("load world map[1][" + name2 + "] end."));
                    }
                    WorldMapDict.log.debug(("shen long map list size = " + this.shenLongJieMapsList.size()));
                    this.worldMaps.put((byte) 1, this.shenLongJieMapsList);
                }
            }
            fileList = new File(config.world_maps_mo_long_jie);
            dataFileList = fileList.listFiles();
            File[] array3;
            for (int length3 = (array3 = dataFileList).length, k = 0; k < length3; ++k) {
                File file = array3[k];
                if (file.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(file);
                    Element root = document.getRootElement();
                    Iterator<Element> eit = (Iterator<Element>) root.elementIterator();
                    short dungeons = 0;
                    while (eit.hasNext()) {
                        Element e2 = eit.next();
                        short mapID = Short.parseShort(e2.elementTextTrim("mapID"));
                        short cellX = Short.parseShort(e2.elementTextTrim("cellX"));
                        short cellY = Short.parseShort(e2.elementTextTrim("cellY"));
                        String name2 = e2.elementTextTrim("name");
                        String desc2 = e2.elementTextTrim("desc");
                        String ds = e2.elementTextTrim("dungeon_entry_mapID");
                        if (ds != null && ds.trim().length() > 0) {
                            dungeons = Short.parseShort(ds);
                        }
                        WorldMap worldMap2 = new WorldMap();
                        worldMap2.mapID = mapID;
                        worldMap2.cellX = cellX;
                        worldMap2.cellY = cellY;
                        worldMap2.name = name2;
                        worldMap2.desc = desc2;
                        worldMap2.dungeonEntryMapID = dungeons;
                        worldMap2.type = 2;
                        this.moLongJieMapsList.add(worldMap2);
                        WorldMapDict.log.debug(("load world map[2][" + name2 + "] end."));
                    }
                    WorldMapDict.log.debug(("world mo long map list size = " + this.moLongJieMapsList.size()));
                    this.worldMaps.put((byte) 2, this.moLongJieMapsList);
                }
            }
            fileList = new File(config.world_maps_xian_jie);
            dataFileList = fileList.listFiles();
            File[] array4;
            for (int length4 = (array4 = dataFileList).length, l = 0; l < length4; ++l) {
                File file = array4[l];
                if (file.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(file);
                    Element root = document.getRootElement();
                    Iterator<Element> eit = (Iterator<Element>) root.elementIterator();
                    short dungeons = 0;
                    while (eit.hasNext()) {
                        Element e2 = eit.next();
                        short mapID = Short.parseShort(e2.elementTextTrim("mapID"));
                        short cellX = Short.parseShort(e2.elementTextTrim("cellX"));
                        short cellY = Short.parseShort(e2.elementTextTrim("cellY"));
                        String name2 = e2.elementTextTrim("name");
                        String desc2 = e2.elementTextTrim("desc");
                        String ds = e2.elementTextTrim("dungeon_entry_mapID");
                        if (ds != null && ds.trim().length() > 0) {
                            dungeons = Short.parseShort(ds);
                        }
                        WorldMap worldMap2 = new WorldMap();
                        worldMap2.mapID = mapID;
                        worldMap2.cellX = cellX;
                        worldMap2.cellY = cellY;
                        worldMap2.name = name2;
                        worldMap2.desc = desc2;
                        worldMap2.dungeonEntryMapID = dungeons;
                        worldMap2.type = 3;
                        this.xianJieMapsList.add(worldMap2);
                        WorldMapDict.log.debug(("load world map[3][" + name2 + "] end."));
                    }
                    WorldMapDict.log.debug(("xian jie map list size = " + this.xianJieMapsList.size()));
                    this.worldMaps.put((byte) 3, this.xianJieMapsList);
                }
            }
            for (final List<WorldMap> mapList : this.worldMaps.values()) {
                for (final WorldMap worldMap3 : mapList) {
                    MapModelData map = MapModelDataDict.getInstance().getMapModelData(worldMap3.mapID);
                    if (map != null) {
                        worldMap3.bornX = map.bornX;
                        worldMap3.bornY = map.bornY;
                    }
                }
            }
        } catch (Exception e3) {
            WorldMapDict.log.error("load world maps error : ", (Throwable) e3);
        }
    }

    public List<WorldMap> getWorldMapListByType(final byte type) {
        return this.worldMaps.get(type);
    }

    public WorldMap getMaxWorldMapByName(final String name) {
        for (final WorldMap worldMap : this.worldMapList) {
            if (worldMap.name.equals(name)) {
                return worldMap;
            }
        }
        return null;
    }

    public short getIncludeDungeonMapID(final short dungeonEntryMapID) {
        for (final List<WorldMap> mapList : this.worldMaps.values()) {
            for (final WorldMap worldMap : mapList) {
                if (worldMap.dungeonEntryMapID == dungeonEntryMapID) {
                    return worldMap.mapID;
                }
            }
        }
        return 0;
    }

    public byte getTypeWorldMapByMapID(final short mapID) {
        for (final List<WorldMap> mapList : this.worldMaps.values()) {
            for (final WorldMap worldMap : mapList) {
                if (worldMap.mapID == mapID) {
                    return worldMap.type;
                }
            }
        }
        return 0;
    }

    public String getMapDesc(final short mapID) {
        for (final List<WorldMap> mapList : this.worldMaps.values()) {
            for (final WorldMap worldMap : mapList) {
                if (worldMap.mapID == mapID) {
                    return worldMap.desc;
                }
            }
        }
        return null;
    }
}
