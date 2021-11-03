// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.dictionary;

import java.util.Iterator;
import org.dom4j.Document;
import hero.share.service.DataConvertor;
import hero.item.detail.EGoodsTrait;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import hero.share.service.LogWriter;
import java.io.File;
import hero.item.TaskTool;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class TaskGoodsDict {

    private static Logger log;
    private FastMap<Integer, TaskTool> dictionary;
    private static TaskGoodsDict instance;

    static {
        TaskGoodsDict.log = Logger.getLogger((Class) TaskGoodsDict.class);
    }

    private TaskGoodsDict() {
        this.dictionary = (FastMap<Integer, TaskTool>) new FastMap();
    }

    public static TaskGoodsDict getInstance() {
        if (TaskGoodsDict.instance == null) {
            TaskGoodsDict.instance = new TaskGoodsDict();
        }
        return TaskGoodsDict.instance;
    }

    public TaskTool getTaskTool(final int _taskGoodsID) {
        return (TaskTool) this.dictionary.get(_taskGoodsID);
    }

    public void load(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
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
                            TaskGoodsDict.log.debug(("start load task tool id = " + subE.elementTextTrim("id")));
                            TaskTool goods = new TaskTool(Short.parseShort(subE.elementTextTrim("stackNums")), subE.elementTextTrim("isShare").equals("\u662f"), subE.elementTextTrim("useable").equals("\u662f"));
                            goods.setID(Integer.parseInt(subE.elementTextTrim("id")));
                            goods.setName(subE.elementTextTrim("name"));
                            goods.setTrait(EGoodsTrait.getTrait((subE.elementTextTrim("trait") == null) ? EGoodsTrait.BING_ZHI.getDesc() : subE.elementTextTrim("trait")));
                            if (goods.useable()) {
                                if (subE.elementTextTrim("disappear").equals("\u662f")) {
                                    goods.disappearAfterUse();
                                }
                                if (subE.elementTextTrim("limitLocation").equals("\u662f")) {
                                    goods.limitLocation();
                                    goods.setLocationOfUse(Integer.parseInt(subE.elementTextTrim("limitMapID")), Short.parseShort(subE.elementTextTrim("limitMapX")), Short.parseShort(subE.elementTextTrim("limitMapY")), Short.parseShort(subE.elementTextTrim("range")));
                                }
                                String data = subE.elementTextTrim("getGoodsID");
                                if (data != null) {
                                    goods.setGetGoodsAfterUse(Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("targetNpcID");
                                if (data != null) {
                                    goods.setTargetNpcModelID(data);
                                    data = subE.elementTextTrim("hpPercent");
                                    if (data != null) {
                                        goods.setTargetTraceHpPercent(DataConvertor.percentElementsString2Float(data));
                                    }
                                    if (subE.elementTextTrim("targetNpcDisappear").equals("\u662f")) {
                                        goods.targetDisappearAfterUse();
                                    }
                                }
                                data = subE.elementTextTrim("refreshNpcID");
                                if (data != null) {
                                    goods.setRefreshNpcModelIDAfterUse(data);
                                    goods.setRefreshNpcNumsAfterUse(Short.parseShort(subE.elementTextTrim("refreshNpcNums")));
                                }
                            }
                            goods.setIconID(Short.parseShort(subE.elementTextTrim("icon")));
                            goods.initDescription();
                            goods.appendDescription(subE.elementTextTrim("description"));
                            this.dictionary.put(goods.getID(), goods);
                            TaskGoodsDict.log.debug(("\u4efb\u52a1\u9053\u5177\u52a0\u8f7d\u6210\u529f: " + goods.getID()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
