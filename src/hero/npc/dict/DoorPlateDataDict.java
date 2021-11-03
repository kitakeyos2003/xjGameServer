// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.dict;

import java.util.Iterator;
import org.dom4j.Document;
import hero.share.service.LogWriter;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import javolution.util.FastMap;

public class DoorPlateDataDict {

    private FastMap<String, DoorPlateData> doorPlateDataDict;
    private static DoorPlateDataDict instance;

    private DoorPlateDataDict() {
        this.doorPlateDataDict = (FastMap<String, DoorPlateData>) new FastMap();
    }

    public static DoorPlateDataDict getInstance() {
        if (DoorPlateDataDict.instance == null) {
            DoorPlateDataDict.instance = new DoorPlateDataDict();
        }
        return DoorPlateDataDict.instance;
    }

    public DoorPlateData getDoorPlateData(final String _doorPlateModelID) {
        return (DoorPlateData) this.doorPlateDataDict.get(_doorPlateModelID);
    }

    public void load(final String _dataPath) {
        try {
            File dataPath = new File(_dataPath);
            File[] dataFileList = dataPath.listFiles();
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
                            DoorPlateData doorPlateData = new DoorPlateData();
                            try {
                                doorPlateData.modelID = subE.elementTextTrim("id").toLowerCase();
                                doorPlateData.tip = subE.elementTextTrim("tip");
                                if (!this.doorPlateDataDict.containsKey(doorPlateData.modelID)) {
                                    this.doorPlateDataDict.put(doorPlateData.modelID, doorPlateData);
                                } else {
                                    LogWriter.println("\u91cd\u590d\u7684\u5ba4\u5185\u95e8\u724c\u6570\u636e\uff0c\u7f16\u53f7:" + doorPlateData.modelID);
                                }
                            } catch (Exception e2) {
                                LogWriter.println("\u52a0\u8f7d\u8def\u5ba4\u5185\u95e8\u724c\u636e\u9519\u8bef\uff0c\u7f16\u53f7:" + doorPlateData.modelID);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
    }

    public static class DoorPlateData {

        public String modelID;
        public String tip;
    }
}
