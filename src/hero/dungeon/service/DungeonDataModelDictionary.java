// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon.service;

import org.dom4j.Document;
import hero.map.service.MapModelDataDict;
import hero.share.service.LogWriter;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import hero.map.MapModelData;
import java.util.Iterator;
import hero.dungeon.DungeonDataModel;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class DungeonDataModelDictionary {

    private static Logger log;
    private FastMap<Integer, DungeonDataModel> dungeonDataTable;
    private static DungeonDataModelDictionary instance;

    static {
        DungeonDataModelDictionary.log = Logger.getLogger((Class) DungeonDataModelDictionary.class);
    }

    private DungeonDataModelDictionary() {
        this.dungeonDataTable = (FastMap<Integer, DungeonDataModel>) new FastMap();
    }

    public static DungeonDataModelDictionary getInsatnce() {
        if (DungeonDataModelDictionary.instance == null) {
            DungeonDataModelDictionary.instance = new DungeonDataModelDictionary();
        }
        return DungeonDataModelDictionary.instance;
    }

    public DungeonDataModel get(final int _dungeonID) {
        return (DungeonDataModel) this.dungeonDataTable.get(_dungeonID);
    }

    public DungeonDataModel getDungeonDataModelByMapid(final short mapID) {
        for (final DungeonDataModel m : this.dungeonDataTable.values()) {
            MapModelData[] mapModelList;
            for (int length = (mapModelList = m.mapModelList).length, i = 0; i < length; ++i) {
                MapModelData d = mapModelList[i];
                if (d.id == mapID) {
                    return m;
                }
            }
        }
        return null;
    }

    protected void loadDungeonModelData(final String _filePath) {
        try {
            File dataPath = new File(_filePath);
            File[] dataFileList = dataPath.listFiles();
            File[] array;
            for (int length = (array = dataFileList).length, j = 0; j < length; ++j) {
                File dataFile = array[j];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            DungeonDataModel dungeonDataModel = new DungeonDataModel();
                            dungeonDataModel.id = Integer.parseInt(subE.elementTextTrim("id"));
                            try {
                                dungeonDataModel.name = subE.elementTextTrim("name");
                                dungeonDataModel.level = Short.parseShort(subE.elementTextTrim("level"));
                                dungeonDataModel.playerNumberLimit = Byte.parseByte(subE.elementTextTrim("limitPlayerNumber"));
                                dungeonDataModel.entranceMapID = Short.parseShort(subE.elementTextTrim("entranceMapID"));
                                int number = Integer.parseInt(subE.elementTextTrim("number"));
                                dungeonDataModel.mapModelList = new MapModelData[number];
                                if (number > 0 && number <= 20) {
                                    short mapID = 0;
                                    for (int i = 1; i <= number; ++i) {
                                        mapID = Short.parseShort(subE.elementTextTrim("map" + i + "ID"));
                                        if (mapID <= 0) {
                                            LogWriter.println("\u526f\u672c\u5730\u56fe\u7f16\u53f7\u9519\u8bef\uff0c\u526f\u672c\u7f16\u53f7:" + dungeonDataModel.id);
                                            return;
                                        }
                                        dungeonDataModel.mapModelList[i - 1] = MapModelDataDict.getInstance().getMapModelData(mapID);
                                    }
                                    this.dungeonDataTable.put(dungeonDataModel.id, dungeonDataModel);
                                } else {
                                    LogWriter.println("\u526f\u672c\u5730\u56fe\u6570\u91cf\u9519\u8bef\uff0c\u526f\u672c\u7f16\u53f7:" + dungeonDataModel.id);
                                }
                            } catch (Exception ex) {
                                LogWriter.println("\u52a0\u8f7d\u526f\u672c\u6570\u636e\u51fa\u9519\uff0c\u526f\u672c\u7f16\u53f7:" + dungeonDataModel.id);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
    }
}
