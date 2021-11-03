// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.dict;

import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import hero.share.service.LogWriter;
import java.io.File;
import java.util.HashMap;

public class SoulInfoDict {

    private static SoulInfoDict instance;
    private HashMap<Integer, SoulInfo> souls;

    private SoulInfoDict() {
        this.souls = new HashMap<Integer, SoulInfo>();
    }

    public void loadSoulInfos(final String path) {
        File dataPath;
        try {
            dataPath = new File(path);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + path);
            return;
        }
        try {
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
                            int id = Integer.parseInt(subE.elementTextTrim("id"));
                            String name = subE.elementTextTrim("name");
                            short icon = Short.parseShort(subE.elementTextTrim("icon"));
                            String des = subE.elementTextTrim("des");
                            this.souls.put(id, new SoulInfo(id, name, icon, des));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SoulInfoDict getInstance() {
        if (SoulInfoDict.instance == null) {
            SoulInfoDict.instance = new SoulInfoDict();
        }
        return SoulInfoDict.instance;
    }

    public SoulInfo getSoulInfoByID(final int _soulID) {
        return this.souls.get(_soulID);
    }
}
