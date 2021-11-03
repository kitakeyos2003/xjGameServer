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

public class DungeonManagerDict {

    private FastMap<String, Integer> dictionary;
    private static DungeonManagerDict instance;

    private DungeonManagerDict() {
        this.dictionary = (FastMap<String, Integer>) new FastMap();
    }

    public static DungeonManagerDict getInstance() {
        if (DungeonManagerDict.instance == null) {
            DungeonManagerDict.instance = new DungeonManagerDict();
        }
        return DungeonManagerDict.instance;
    }

    public int getDungeonID(final String _npcModelID) {
        return (int) this.dictionary.get(_npcModelID);
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
                            try {
                                String npcModelID = subE.elementTextTrim("npcModelID").toLowerCase();
                                int dungeonID = Integer.parseInt(subE.elementTextTrim("dungeonID"));
                                this.dictionary.put(npcModelID, dungeonID);
                            } catch (Exception e2) {
                                LogWriter.println("\u52a0\u8f7d\u526f\u672c\u4f20\u9001\u7ba1\u7406\u6570\u636e\u9519\u8bef");
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
