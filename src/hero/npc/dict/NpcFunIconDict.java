// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.dict;

import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class NpcFunIconDict {

    private static Logger log;
    private FastMap<Integer, Short[]> npcFunIconDict;
    private static NpcFunIconDict instance;

    static {
        NpcFunIconDict.log = Logger.getLogger((Class) NpcFunIconDict.class);
    }

    private NpcFunIconDict() {
        this.npcFunIconDict = (FastMap<Integer, Short[]>) new FastMap();
    }

    public static NpcFunIconDict getInstance() {
        if (NpcFunIconDict.instance == null) {
            NpcFunIconDict.instance = new NpcFunIconDict();
        }
        return NpcFunIconDict.instance;
    }

    public Short[] getNpcFunIcon(final int funID) {
        return (Short[]) this.npcFunIconDict.get(funID);
    }

    public void load(final String filePath) {
        try {
            File fileDir = new File(filePath);
            File[] fileList = fileDir.listFiles();
            File[] array;
            for (int length = (array = fileList).length, i = 0; i < length; ++i) {
                File file = array[i];
                if (file.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(file);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        int funID = Integer.parseInt(subE.elementTextTrim("typeID"));
                        short iconID2;
                        short iconID = iconID2 = Short.parseShort(subE.elementTextTrim("iconID"));
                        String data = subE.elementTextTrim("iconID2");
                        if (data != null) {
                            iconID2 = Short.parseShort(data);
                        }
                        this.npcFunIconDict.put(funID, new Short[]{iconID, iconID2});
                    }
                }
            }
            NpcFunIconDict.log.debug(("npc fun icon dict size = " + this.npcFunIconDict.size()));
        } catch (DocumentException e) {
            NpcFunIconDict.log.error("load npc fun icon error : ", (Throwable) e);
            e.printStackTrace();
        }
    }
}
