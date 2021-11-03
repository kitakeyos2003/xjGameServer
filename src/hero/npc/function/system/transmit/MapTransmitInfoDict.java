// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system.transmit;

import hero.map.Map;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import hero.share.service.LogWriter;
import java.io.File;
import hero.map.service.MapServiceImpl;
import hero.map.service.MapConfig;
import java.util.ArrayList;
import java.util.HashMap;

public class MapTransmitInfoDict {

    private static MapTransmitInfoDict instance;
    private HashMap<String, ArrayList<TransmitTargetMapInfo>> dictionary;
    private static final String TIP_OF_INVAILD_PATH = "\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a";

    public static MapTransmitInfoDict getInstance() {
        if (MapTransmitInfoDict.instance == null) {
            MapTransmitInfoDict.instance = new MapTransmitInfoDict();
        }
        return MapTransmitInfoDict.instance;
    }

    private MapTransmitInfoDict() {
        this.dictionary = new HashMap<String, ArrayList<TransmitTargetMapInfo>>();
        this.load(MapServiceImpl.getInstance().getConfig().getTransmitMapListPath());
    }

    public ArrayList<TransmitTargetMapInfo> getTargetMapInfoList(final String _npcModelID) {
        return this.dictionary.get(_npcModelID.toLowerCase());
    }

    private void load(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
            return;
        }
        try {
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
                            String npcModelID = subE.elementTextTrim("id");
                            ArrayList<TransmitTargetMapInfo> targetMapInfoList = new ArrayList<TransmitTargetMapInfo>();
                            for (int i = 1; i <= 5; ++i) {
                                String sDestMapID = subE.elementTextTrim("destMap" + i + "ID");
                                if (sDestMapID != null) {
                                    Map destMap = MapServiceImpl.getInstance().getNormalMapByID(Short.parseShort(sDestMapID));
                                    if (destMap != null) {
                                        int freight = Integer.parseInt(subE.elementTextTrim("destMap" + i + "Price"));
                                        short needLevel = Short.parseShort(subE.elementTextTrim("destMap" + i + "NeedLevel"));
                                        byte destMapX = Byte.parseByte(subE.elementTextTrim("destMap" + i + "X"));
                                        byte destMapY = Byte.parseByte(subE.elementTextTrim("destMap" + i + "Y"));
                                        targetMapInfoList.add(new TransmitTargetMapInfo(destMap.getID(), destMap.getName(), destMap.getArea().getName(), needLevel, destMapX, destMapY, freight));
                                    }
                                }
                            }
                            this.dictionary.put(npcModelID, targetMapInfoList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
