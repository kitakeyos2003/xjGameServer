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
import javolution.util.FastMap;

public class MapMusicDict {

    private FastMap<Short, Byte> dictionary;
    private static MapMusicDict instance;

    private MapMusicDict() {
        this.dictionary = (FastMap<Short, Byte>) new FastMap();
    }

    public static MapMusicDict getInstance() {
        if (MapMusicDict.instance == null) {
            MapMusicDict.instance = new MapMusicDict();
        }
        return MapMusicDict.instance;
    }

    public byte getMapMusicID(final short _mapID) {
        Byte musicID = (Byte) this.dictionary.get(_mapID);
        if (musicID == null) {
            return 0;
        }
        return musicID;
    }

    public void init(final String _configPath) {
        File fileDir;
        try {
            fileDir = new File(_configPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u5730\u56fe\u97f3\u4e50\u914d\u7f6e\u76ee\u5f55\uff1a" + _configPath);
            return;
        }
        try {
            File[] fileList = fileDir.listFiles();
            if (fileList.length <= 0) {
                return;
            }
            this.dictionary.clear();
            File[] array;
            for (int length = (array = fileList).length, i = 0; i < length; ++i) {
                File dataFile = array[i];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            short mapID = Short.parseShort(subE.elementTextTrim("mapID"));
                            byte musicID = Byte.parseByte(subE.elementTextTrim("musicID"));
                            this.dictionary.put(mapID, musicID);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
        }
    }
}
