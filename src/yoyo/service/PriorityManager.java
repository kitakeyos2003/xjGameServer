// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service;

import java.util.Iterator;
import org.dom4j.Document;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import javolution.util.FastMap;
import java.util.Map;

public class PriorityManager {

    private Map<Integer, Priority> priorityMap;
    private static PriorityManager instance;

    private PriorityManager() {
        this.priorityMap = (Map<Integer, Priority>) new FastMap();
    }

    public static PriorityManager getInstance() {
        if (PriorityManager.instance == null) {
            PriorityManager.instance = new PriorityManager();
        }
        return PriorityManager.instance;
    }

    public void load() {
        String pathXml = String.valueOf(YOYOSystem.HOME) + "conf/priority.xml";
        try {
            this.parse(pathXml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parse(final String path) throws Exception {
        File file = new File(path);
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element root = document.getRootElement();
        Iterator<Element> it = (Iterator<Element>) root.elementIterator("clienthandler");
        while (it.hasNext()) {
            Element eCh = it.next();
            int mID = NumberUtils.createInteger(eCh.attributeValue("id"));
            String sPriority = eCh.attributeValue("priority");
            Priority priority = Priority.REAL_TIME;
            if (!StringUtils.isBlank(sPriority)) {
                priority = Priority.getPriority(NumberUtils.createInteger(sPriority));
            }
            this.priorityMap.put(mID, priority);
        }
    }

    public Priority getPriorityByMsgId(final int msgId) {
        Priority pro = this.priorityMap.get(msgId);
        if (pro != null) {
            return pro;
        }
        return Priority.REAL_TIME;
    }
}
