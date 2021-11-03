// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.service;

import java.util.Iterator;
import org.dom4j.Document;
import hero.share.service.LogWriter;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.Map;
import java.util.Set;
import hero.map.Area;
import java.util.HashMap;

public class AreaDict {

    private HashMap<Integer, Area> dictionary;
    private static AreaDict instance;

    private AreaDict() {
        this.dictionary = new HashMap<Integer, Area>();
    }

    public static AreaDict getInstance() {
        if (AreaDict.instance == null) {
            AreaDict.instance = new AreaDict();
        }
        return AreaDict.instance;
    }

    public Set<Map.Entry<Integer, Area>> getAreaSet() {
        return this.dictionary.entrySet();
    }

    public Area getAreaByID(final int _areaID) {
        return this.dictionary.get(_areaID);
    }

    protected void init(final String _dataPath) {
        try {
            File fileList = new File(_dataPath);
            File[] dataFileList = fileList.listFiles();
            if (dataFileList.length > 0) {
                this.dictionary.clear();
            }
            File[] array;
            for (int length = (array = dataFileList).length, i = 0; i < length; ++i) {
                File dataFile = array[i];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> elementIterator = (Iterator<Element>) rootE.elementIterator();
                    while (elementIterator.hasNext()) {
                        Element subE = elementIterator.next();
                        if (subE != null) {
                            int areaID = Integer.parseInt(subE.elementTextTrim("areaID"));
                            try {
                                String name = subE.elementTextTrim("areaName");
                                Area area = new Area(areaID, name, AreaImageDict.getInstance().getImageBytes(areaID));
                                if (this.dictionary.get(areaID) == null) {
                                    this.dictionary.put(area.getID(), area);
                                } else {
                                    LogWriter.println("\u91cd\u590d\u7684\u5730\u56fe\u533a\u57df\u6570\u636e\uff0c\u7f16\u53f7\uff1a" + areaID);
                                }
                            } catch (Exception e2) {
                                LogWriter.println("\u52a0\u8f7d\u5730\u56fe\u533a\u57df\u6570\u636e\u9519\u8bef\uff0c\u7f16\u53f7\uff1a" + areaID);
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
