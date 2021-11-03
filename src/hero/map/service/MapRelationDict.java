// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.service;

import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import hero.share.service.LogWriter;
import java.io.File;
import java.util.HashMap;

public class MapRelationDict {

    private HashMap<Short, short[]> dictionary;
    private static MapRelationDict instance;

    private MapRelationDict() {
        this.dictionary = new HashMap<Short, short[]>();
    }

    public static MapRelationDict getInstance() {
        if (MapRelationDict.instance == null) {
            MapRelationDict.instance = new MapRelationDict();
        }
        return MapRelationDict.instance;
    }

    public short[] getRelationByMapID(final short _mapID) {
        return this.dictionary.get(_mapID);
    }

    public void init(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
            return;
        }
        try {
            File[] dataFileList = dataPath.listFiles();
            if (dataFileList.length > 0) {
                this.dictionary.clear();
            }
            File[] array;
            for (int length = (array = dataFileList).length, i = 0; i < length; ++i) {
                File dataFile = array[i];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            short[] relationData = new short[10];
                            short mapID = Short.parseShort(subE.elementTextTrim("mapID"));
                            relationData[0] = mapID;
                            short miniImageID = Short.parseShort(subE.elementTextTrim("miniImageID"));
                            relationData[1] = miniImageID;
                            short targetMapIDAfterDie = Short.parseShort(subE.elementTextTrim("reviveReturnMapID"));
                            relationData[2] = targetMapIDAfterDie;
                            short targetMapIDAfterUseGoods = Short.parseShort(subE.elementTextTrim("toolReturnMapID"));
                            relationData[3] = targetMapIDAfterUseGoods;
                            short areaID = Short.parseShort(subE.elementTextTrim("areaID"));
                            relationData[4] = areaID;
                            if (areaID != 0 && subE.elementTextTrim("existsPoint").equals("\u662f")) {
                                relationData[5] = 1;
                                short imageX = Short.parseShort(subE.elementTextTrim("imageX"));
                                relationData[6] = imageX;
                                short imageY = Short.parseShort(subE.elementTextTrim("imageY"));
                                relationData[7] = imageY;
                            }
                            String data = subE.elementTextTrim("mozu_reviveReturnMapID");
                            if (data != null) {
                                short moZuTargetMapIDAfterDie = Short.parseShort(data);
                                relationData[8] = moZuTargetMapIDAfterDie;
                            }
                            data = subE.elementTextTrim("mozu_toolReturnMapID");
                            if (data != null) {
                                short mozuTargetMapIDAfterUseGoods = Short.parseShort(data);
                                relationData[9] = mozuTargetMapIDAfterUseGoods;
                            }
                            this.dictionary.put(mapID, relationData);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
