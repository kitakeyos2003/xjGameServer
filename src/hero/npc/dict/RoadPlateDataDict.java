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

public class RoadPlateDataDict {

    private FastMap<String, RoadPlateData> roadPlateDataDict;
    private static RoadPlateDataDict instance;

    private RoadPlateDataDict() {
        this.roadPlateDataDict = (FastMap<String, RoadPlateData>) new FastMap();
    }

    public static RoadPlateDataDict getInstance() {
        if (RoadPlateDataDict.instance == null) {
            RoadPlateDataDict.instance = new RoadPlateDataDict();
        }
        return RoadPlateDataDict.instance;
    }

    public RoadPlateData getRoadPlateData(final String _roadPlateModelID) {
        return (RoadPlateData) this.roadPlateDataDict.get(_roadPlateModelID);
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
                            RoadPlateData roadPlateData = new RoadPlateData();
                            try {
                                roadPlateData.modelID = subE.elementTextTrim("id").toLowerCase();
                                roadPlateData.instructContent = subE.elementTextTrim("content");
                                if (!this.roadPlateDataDict.containsKey(roadPlateData.modelID)) {
                                    this.roadPlateDataDict.put(roadPlateData.modelID, roadPlateData);
                                } else {
                                    LogWriter.println("\u91cd\u590d\u7684\u8def\u724c\u6570\u636e\uff0c\u7f16\u53f7:" + roadPlateData.modelID);
                                }
                            } catch (Exception e2) {
                                LogWriter.println("\u52a0\u8f7d\u8def\u724c\u6570\u636e\u9519\u8bef\uff0c\u7f16\u53f7:" + roadPlateData.modelID);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
    }

    public static class RoadPlateData {

        public String modelID;
        public String instructContent;
    }
}
