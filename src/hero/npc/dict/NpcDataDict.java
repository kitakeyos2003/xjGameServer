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
import org.apache.log4j.Logger;

public class NpcDataDict {

    private static Logger log;
    private FastMap<String, NpcData> npcDataDict;
    private static NpcDataDict instance;

    static {
        NpcDataDict.log = Logger.getLogger((Class) NpcDataDict.class);
    }

    private NpcDataDict() {
        this.npcDataDict = (FastMap<String, NpcData>) new FastMap();
    }

    public static NpcDataDict getInstance() {
        if (NpcDataDict.instance == null) {
            NpcDataDict.instance = new NpcDataDict();
        }
        return NpcDataDict.instance;
    }

    public NpcData getNpcData(final String _npcModelID) {
        return (NpcData) this.npcDataDict.get(_npcModelID);
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
                            NpcData npcData = new NpcData();
                            try {
                                npcData.modelID = subE.elementTextTrim("id").toLowerCase();
                                String data = subE.elementTextTrim("name");
                                if (data == null) {
                                    npcData.name = "";
                                } else {
                                    npcData.name = data;
                                }
                                data = subE.elementTextTrim("title");
                                if (data == null) {
                                    npcData.title = "";
                                } else {
                                    npcData.title = data;
                                }
                                npcData.exsitTime = subE.elementTextTrim("exsitTime");
                                npcData.helloContent = NpcHelloContentDict.getInstance().getHelloContent(Integer.parseInt(subE.elementTextTrim("helloContentID")));
                                npcData.screamContent = subE.elementTextTrim("screamContent");
                                npcData.imageID = subE.elementTextTrim("imageID");
                                npcData.animationID = subE.elementTextTrim("animationID");
                                npcData.imageType = subE.elementTextTrim("imageType");
                                npcData.clanDesc = subE.elementTextTrim("clan");
                                npcData.function1 = subE.elementTextTrim("function1");
                                npcData.function2 = subE.elementTextTrim("function2");
                                npcData.function3 = subE.elementTextTrim("function3");
                                npcData.function4 = subE.elementTextTrim("function4");
                                npcData.function5 = subE.elementTextTrim("function5");
                                npcData.skillEducateVocation = subE.elementTextTrim("skillTrainerVocation");
                                npcData.skillEducateFeature = subE.elementTextTrim("feature");
                                if (!this.npcDataDict.containsKey(npcData.modelID)) {
                                    this.npcDataDict.put(npcData.modelID, npcData);
                                } else {
                                    NpcDataDict.log.error(("\u91cd\u590d\u7684NPC\u6570\u636e\uff0c\u7f16\u53f7:" + npcData.modelID));
                                }
                            } catch (Exception e) {
                                NpcDataDict.log.error(("\u52a0\u8f7dNPC\u6570\u636e\u9519\u8bef\uff0c\u7f16\u53f7:" + npcData.modelID), (Throwable) e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e2) {
            LogWriter.error(this, e2);
        }
    }

    public static void main(final String[] args) {
        NpcHelloContentDict.getInstance().load(String.valueOf(System.getProperty("user.dir")) + "/" + "res/data/npc/hello/");
        getInstance().load(String.valueOf(System.getProperty("user.dir")) + "/" + "res/data/npc");
        NpcData data = getInstance().getNpcData("N1");
        System.out.println(data.name);
        System.out.println(data.function1);
        System.out.println(data.helloContent);
        System.out.println(data.imageID);
        System.out.println(data.imageType);
        System.out.println(data.skillEducateVocation);
    }

    public static class NpcData {

        public String modelID;
        public String name;
        public String clanDesc;
        public String title;
        public String exsitTime;
        public String helloContent;
        public String screamContent;
        public String imageID;
        public String imageType;
        public String animationID;
        public String function1;
        public String function2;
        public String function3;
        public String function4;
        public String function5;
        public String skillEducateVocation;
        public String skillEducateFeature;
        public byte[][] movePath;
    }
}
