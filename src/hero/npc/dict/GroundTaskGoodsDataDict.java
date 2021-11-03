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

public class GroundTaskGoodsDataDict {

    private FastMap<String, GroundTaskGoodsData> taskGoodsDataDict;
    private static GroundTaskGoodsDataDict instance;

    private GroundTaskGoodsDataDict() {
        this.taskGoodsDataDict = (FastMap<String, GroundTaskGoodsData>) new FastMap();
    }

    public static GroundTaskGoodsDataDict getInstance() {
        if (GroundTaskGoodsDataDict.instance == null) {
            GroundTaskGoodsDataDict.instance = new GroundTaskGoodsDataDict();
        }
        return GroundTaskGoodsDataDict.instance;
    }

    public GroundTaskGoodsData getTaskGoodsData(final String _taskGoodsModelID) {
        return (GroundTaskGoodsData) this.taskGoodsDataDict.get(_taskGoodsModelID);
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
                            GroundTaskGoodsData taskGoodsData = new GroundTaskGoodsData();
                            try {
                                taskGoodsData.modelID = subE.elementTextTrim("id").toLowerCase();
                                taskGoodsData.name = subE.elementTextTrim("name");
                                taskGoodsData.taskID = Integer.parseInt(subE.elementTextTrim("taskID"));
                                taskGoodsData.taskToolID = Integer.parseInt(subE.elementTextTrim("taskGoodsID"));
                                taskGoodsData.imageID = Short.parseShort(subE.elementTextTrim("imageID"));
                                if (!this.taskGoodsDataDict.containsKey(taskGoodsData.modelID)) {
                                    this.taskGoodsDataDict.put(taskGoodsData.modelID, taskGoodsData);
                                } else {
                                    LogWriter.println("\u91cd\u590d\u7684\u5730\u4e0a\u4efb\u52a1\u7269\u54c1\u6570\u636e\uff0c\u7f16\u53f7:" + taskGoodsData.modelID);
                                }
                            } catch (Exception e2) {
                                LogWriter.println("\u52a0\u8f7d\u5730\u4e0a\u4efb\u52a1\u7269\u54c1\u6570\u636e\u9519\u8bef\uff0c\u7f16\u53f7:" + taskGoodsData.modelID);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
    }

    public static class GroundTaskGoodsData {

        public String modelID;
        public String name;
        public int taskID;
        public int taskToolID;
        public short imageID;
    }
}
