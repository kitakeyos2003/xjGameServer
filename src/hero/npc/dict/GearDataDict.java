// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.dict;

import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class GearDataDict {

    private static Logger log;
    private FastMap<String, GearData> gearDataDict;
    private static GearDataDict instance;

    static {
        GearDataDict.log = Logger.getLogger((Class) GearDataDict.class);
    }

    private GearDataDict() {
        this.gearDataDict = (FastMap<String, GearData>) new FastMap();
    }

    public static GearDataDict getInstance() {
        if (GearDataDict.instance == null) {
            GearDataDict.instance = new GearDataDict();
        }
        return GearDataDict.instance;
    }

    public GearData getGearData(final String _gearModelID) {
        return (GearData) this.gearDataDict.get(_gearModelID);
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
                            GearData gearData = new GearData();
                            try {
                                gearData.modelID = subE.elementTextTrim("id").toLowerCase();
                                gearData.name = subE.elementTextTrim("name");
                                gearData.taskID = Integer.parseInt(subE.elementTextTrim("taskID"));
                                gearData.optionDesc = subE.elementTextTrim("optionDesc");
                                gearData.imageID = Short.parseShort(subE.elementTextTrim("imageID"));
                                gearData.description = subE.elementTextTrim("description");
                                if (!this.gearDataDict.containsKey(gearData.modelID)) {
                                    this.gearDataDict.put(gearData.modelID, gearData);
                                } else {
                                    GearDataDict.log.debug(("\u91cd\u590d\u7684\u4efb\u52a1\u673a\u5173\u6570\u636e\uff0c\u7f16\u53f7:" + gearData.modelID));
                                }
                            } catch (Exception e2) {
                                GearDataDict.log.error(("\u52a0\u8f7d\u4efb\u52a1\u673a\u5173\u6570\u636e\u9519\u8bef\uff0c\u7f16\u53f7:" + gearData.modelID));
                            }
                        }
                    }
                    GearDataDict.log.debug(("gear data dict size = " + this.gearDataDict.size()));
                }
            }
        } catch (Exception e) {
            GearDataDict.log.error("\u52a0\u8f7d\u4efb\u52a1\u673a\u5173 error\uff1a", (Throwable) e);
        }
    }

    public static class GearData {

        public String modelID;
        public String name;
        public String optionDesc;
        public int taskID;
        public short imageID;
        public String description;
    }
}
