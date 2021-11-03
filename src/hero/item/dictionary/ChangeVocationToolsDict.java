// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.dictionary;

import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import hero.item.TaskTool;
import hero.share.EVocation;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class ChangeVocationToolsDict {

    private static Logger log;
    private FastMap<EVocation, TaskTool> changeVocationGoodsDict;
    private static ChangeVocationToolsDict instance;

    static {
        ChangeVocationToolsDict.log = Logger.getLogger((Class) ChangeVocationToolsDict.class);
    }

    private ChangeVocationToolsDict() {
        this.changeVocationGoodsDict = (FastMap<EVocation, TaskTool>) new FastMap();
    }

    public static ChangeVocationToolsDict getInstance() {
        if (ChangeVocationToolsDict.instance == null) {
            ChangeVocationToolsDict.instance = new ChangeVocationToolsDict();
        }
        return ChangeVocationToolsDict.instance;
    }

    public TaskTool getToolByVocation(final EVocation _vocation) {
        return (TaskTool) this.changeVocationGoodsDict.get(_vocation);
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
                                EVocation vocation = EVocation.getVocationByDesc(subE.elementTextTrim("vocation"));
                                ChangeVocationToolsDict.log.debug(("change vocation = " + vocation));
                                if (vocation != null) {
                                    TaskTool tool = (TaskTool) GoodsContents.getGoods(Integer.parseInt(subE.elementTextTrim("toolID")));
                                    ChangeVocationToolsDict.log.debug(("change vocation tool = " + tool));
                                    if (tool != null) {
                                        if (this.changeVocationGoodsDict.get(vocation) == null) {
                                            this.changeVocationGoodsDict.put(vocation, tool);
                                        } else {
                                            ChangeVocationToolsDict.log.debug(("\u8f6c\u804c\u9053\u5177\u8868\uff0d\u91cd\u590d\u7684\u804c\u4e1a\u63cf\u8ff0\u6570\u636e:" + vocation.getDesc()));
                                        }
                                    } else {
                                        ChangeVocationToolsDict.log.debug(("\u8f6c\u804c\u9053\u5177\u8868\uff0d\u4e0d\u5b58\u5728\u7684\u9053\u5177\uff0c\u7f16\u53f7:" + vocation.getDesc()));
                                    }
                                } else {
                                    ChangeVocationToolsDict.log.debug(("\u8f6c\u804c\u9053\u5177\u8868\uff0d\u4e0d\u5b58\u5728\u7684\u804c\u4e1a\u63cf\u8ff0:" + vocation.getDesc()));
                                }
                            } catch (Exception e) {
                                ChangeVocationToolsDict.log.debug(("\u52a0\u8f7d\u8f6c\u804c\u9053\u5177\u6570\u636e\u9519\u8bef\uff0c\u7f16\u53f7:" + subE.elementTextTrim("vocation")), (Throwable) e);
                            }
                        }
                    }
                }
            }
            ChangeVocationToolsDict.log.debug(("changeVocationGoodsDict size = " + this.changeVocationGoodsDict.size()));
        } catch (Exception e2) {
            ChangeVocationToolsDict.log.error("\u52a0\u8f7d\u8f6c\u804c\u7269\u54c1 error: ", (Throwable) e2);
        }
    }
}
