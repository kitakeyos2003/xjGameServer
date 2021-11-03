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

public class NpcHelloContentDict {

    private FastMap<Integer, String> contentDict;
    private static NpcHelloContentDict instance;

    private NpcHelloContentDict() {
        this.contentDict = (FastMap<Integer, String>) new FastMap();
    }

    public static NpcHelloContentDict getInstance() {
        if (NpcHelloContentDict.instance == null) {
            NpcHelloContentDict.instance = new NpcHelloContentDict();
        }
        return NpcHelloContentDict.instance;
    }

    public String getHelloContent(final int _contentID) {
        return (String) this.contentDict.get(_contentID);
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
                            int contentID = Integer.parseInt(subE.elementTextTrim("id"));
                            try {
                                if (!this.contentDict.containsKey(contentID)) {
                                    this.contentDict.put(contentID, subE.elementTextTrim("content"));
                                } else {
                                    LogWriter.println("\u91cd\u590d\u7684NPC\u5bf9\u8bdd\u6570\u636e\uff0c\u7f16\u53f7:" + contentID);
                                }
                            } catch (Exception e2) {
                                LogWriter.println("\u52a0\u8f7dNPC\u5bf9\u8bdd\u6570\u636e\u9519\u8bef\uff0c\u7f16\u53f7:" + contentID);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
    }

    public static void main(final String[] args) {
        getInstance().load(String.valueOf(System.getProperty("user.dir")) + "/" + "res/data/npc/hello/");
        System.out.println(getInstance().getHelloContent(1));
    }
}
