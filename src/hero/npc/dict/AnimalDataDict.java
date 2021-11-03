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

public class AnimalDataDict {

    private static Logger log;
    private FastMap<String, AnimalData> animalDataDict;
    private static AnimalDataDict instance;

    static {
        AnimalDataDict.log = Logger.getLogger((Class) AnimalDataDict.class);
    }

    private AnimalDataDict() {
        this.animalDataDict = (FastMap<String, AnimalData>) new FastMap();
    }

    public static AnimalDataDict getInstance() {
        if (AnimalDataDict.instance == null) {
            AnimalDataDict.instance = new AnimalDataDict();
        }
        return AnimalDataDict.instance;
    }

    public AnimalData getAnimalData(final String _animalModelID) {
        return (AnimalData) this.animalDataDict.get(_animalModelID);
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
                            AnimalData animalData = new AnimalData();
                            try {
                                animalData.modelID = subE.elementTextTrim("id").toLowerCase();
                                animalData.fastestWalkRange = Byte.parseByte(subE.elementTextTrim("range"));
                                animalData.imageID = Short.parseShort(subE.elementTextTrim("imageID"));
                                animalData.animationID = Byte.parseByte(subE.elementTextTrim("animationID"));
                                if (!this.animalDataDict.containsKey(animalData.modelID)) {
                                    this.animalDataDict.put(animalData.modelID, animalData);
                                } else {
                                    AnimalDataDict.log.debug(("\u91cd\u590d\u7684\u5c0f\u52a8\u7269\u6570\u636e\uff0c\u7f16\u53f7:" + animalData.modelID));
                                }
                            } catch (Exception e) {
                                AnimalDataDict.log.error(("\u52a0\u8f7d\u5c0f\u52a8\u7269\u6570\u636e\u9519\u8bef\uff0c\u7f16\u53f7:" + animalData.modelID), (Throwable) e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e2) {
            AnimalDataDict.log.error("\u52a0\u8f7d\u5c0f\u52a8\u7269\u6570\u636e error: ", (Throwable) e2);
        }
    }

    public static class AnimalData {

        public String modelID;
        public byte fastestWalkRange;
        public short animationID;
        public short imageID;
    }
}
