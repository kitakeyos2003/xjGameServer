// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service;

import yoyo.service.base.AbsConfig;
import yoyo.service.base.session.Session;
import org.apache.commons.lang.StringUtils;
import yoyo.core.event.AbsEvent;
import java.lang.reflect.Method;
import javolution.util.FastList;
import java.util.Collections;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Element;
import java.util.ArrayList;
import org.dom4j.Document;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import org.dom4j.io.SAXReader;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.packet.ContextData;
import javolution.util.FastMap;
import org.slf4j.LoggerFactory;
import yoyo.service.base.AbsService;
import java.util.Map;
import org.slf4j.Logger;

public class ServiceManager {

    private final Logger logger;
    private static ServiceManager instance;
    private Map<Integer, AbsService> serviceMap;
    private AbsService[] sortServices;
    protected Map<String, Integer> messagMap;

    private ServiceManager() {
        this.logger = LoggerFactory.getLogger((Class) this.getClass());
        this.messagMap = (Map<String, Integer>) new FastMap();
        this.serviceMap = (Map<Integer, AbsService>) new FastMap();
    }

    public static ServiceManager getInstance() {
        if (ServiceManager.instance == null) {
            ServiceManager.instance = new ServiceManager();
        }
        return ServiceManager.instance;
    }

    public int getMsgIdByName(final String name) throws Exception {
        Integer i = this.messagMap.get(name);
        if (i == null) {
            throw new Exception("Can not find Message by name " + name);
        }
        return i;
    }

    public AbsClientProcess getClientProcess(final ContextData data) throws Exception {
        if (data == null) {
            return new ErrorProcess("ContextData null");
        }
        AbsService service = this.serviceMap.get(data.serviceID);
        if (service == null) {
            return new ErrorProcess("can not find service by id:" + data.serviceID);
        }
        AbsClientProcess process = service.getClientProcess(data);
        if (process != null) {
            process.init(data);
            return process;
        }
        return new ErrorProcess("can not find ClientProcess by id:" + data.messageType);
    }

    public void load() throws Exception {
        this.serviceMap.clear();
        String path = String.valueOf(YOYOSystem.HOME) + "conf/server.xml";
        this.loadBasic(path);
        List<SortService> srvOrders = this.getLoadOrder();
        String service_dir = String.valueOf(YOYOSystem.HOME) + "service/";
        for (final SortService order : srvOrders) {
            SAXReader reader = new SAXReader();
            Document dom = reader.read(new File(String.valueOf(service_dir) + order.xml));
            this.parse(dom.getRootElement());
        }
        this.logger.info("All Service Size " + this.serviceMap.size());
        this.sort();
    }

    private List<SortService> getLoadOrder() throws Exception {
        String path = String.valueOf(YOYOSystem.HOME) + "conf/loadorder.xml";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(path));
        Element root = document.getRootElement();
        Iterator<Element> it = (Iterator<Element>) root.elementIterator("service");
        List<SortService> list = new ArrayList<SortService>();
        while (it.hasNext()) {
            Element e = it.next();
            SortService order = new SortService();
            order.id = NumberUtils.createInteger(e.attributeValue("id"));
            order.index = NumberUtils.createInteger(e.attributeValue("index"));
            order.className = e.attributeValue("class");
            order.xml = e.getTextTrim();
            list.add(order);
        }
        Collections.sort(list);
        return list;
    }

    private void sort() {
        FastList<AbsService> list = (FastList<AbsService>) new FastList();
        Iterator it = this.serviceMap.values().iterator();
        while (list.size() < this.serviceMap.size()) {
            while (it.hasNext()) {
                AbsService service = (AbsService) it.next();
                if (list.contains(service)) {
                    continue;
                }
                int[] ids = service.getSessionDps();
                if (ids != null) {
                    int[] array;
                    for (int length = (array = ids).length, i = 0; i < length; ++i) {
                        int id = array[i];
                        AbsService stmp = this.serviceMap.get(id);
                        if (!list.contains(stmp)) {
                            list.addLast(stmp);
                        }
                    }
                }
                list.addLast(service);
            }
        }
        list.toArray((Object[]) (this.sortServices = new AbsService[this.serviceMap.size()]));
    }

    private void loadBasic(final String path) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(path));
        Element root = document.getRootElement();
        Element eLog = root.element("logservice");
        this.parse(eLog);
        Element eDB = root.element("dbservice");
        this.parse(eDB);
        Element eNet = root.element("networkservice");
        this.parse(eNet);
        Element eSsn = root.element("sessionservice");
        this.parse(eSsn);
    }

    private AbsService loadClass(final String className) throws ClassNotFoundException, NoSuchMethodException, Exception {
        Class c = this.getClass().getClassLoader().loadClass(className);
        Method getinstance = c.getMethod("getInstance", (Class[]) null);
        AbsService impl = (AbsService) getinstance.invoke(null, (Object[]) null);
        return impl;
    }

    private void loadMessages(final Element element, final int srvId) {
        Element eMsges = element.element("messages");
        if (eMsges == null) {
            return;
        }
        Iterator it = eMsges.elementIterator();
        while (it.hasNext()) {
            Element eMsg = (Element) it.next();
            String className = eMsg.getTextTrim();
            String sId = eMsg.attributeValue("id");
            int id = NumberUtils.createInteger(sId);
            if (srvId != (id >> 8 & 0xFF)) {
                throw new RuntimeException("Message " + className + " is not correct!");
            }
            this.logger.info("load message " + className + " id:" + id);
            this.messagMap.put(className, id);
        }
    }

    public void onEvent(final AbsEvent event) throws Exception {
    }

    public MonitorEvent[] monitor() {
        List<MonitorEvent> list = new ArrayList<MonitorEvent>();
        if (this.sortServices == null) {
            return null;
        }
        AbsService[] sortServices;
        for (int length = (sortServices = this.sortServices).length, i = 0; i < length; ++i) {
            AbsService srvc = sortServices[i];
            list.add((MonitorEvent) srvc.montior());
        }
        MonitorEvent[] rt = new MonitorEvent[list.size()];
        list.toArray(rt);
        return rt;
    }

    private boolean parse(final Element root) throws Exception {
        Element eService = root.element("service");
        if (eService == null) {
            throw new NullPointerException(String.valueOf(root.getName()) + " not define <service> tag");
        }
        String sId = eService.elementTextTrim("id");
        if (StringUtils.isBlank(sId)) {
            throw new RuntimeException("can not find <id> tag");
        }
        int id = NumberUtils.createInteger(sId);
        if (this.serviceMap.containsKey(id)) {
            return true;
        }
        String sName = eService.elementTextTrim("name");
        if (StringUtils.isBlank(sName)) {
            throw new RuntimeException("can not find <name> tag");
        }
        Element eDepends = eService.element("dependencies");
        if (eDepends != null) {
            Iterator it = eDepends.elementIterator("load");
            while (it.hasNext()) {
                Element e = (Element) it.next();
                int dpId = NumberUtils.createInteger(e.attributeValue("id"));
                if (!this.serviceMap.containsKey(dpId)) {
                    return false;
                }
            }
        }
        String sClassName = eService.elementTextTrim("class");
        if (StringUtils.isBlank(sClassName)) {
            throw new RuntimeException("can not find <class> tag");
        }
        AbsService service;
        try {
            service = this.loadClass(sClassName);
        } catch (ClassNotFoundException e2) {
            throw new RuntimeException(String.valueOf(sClassName) + " not extends AbsService");
        } catch (NoSuchMethodException e3) {
            throw new RuntimeException(String.valueOf(sClassName) + "not define getInstance() method");
        }
        if (this.check(id, sName)) {
            this.loadMessages(root, id);
            service.initService(root);
            this.serviceMap.put(id, service);
            return true;
        }
        throw new RuntimeException(String.valueOf(root.getName()) + " id=" + sId + " or name=" + sName + " regisgted");
    }

    public void createSession(final Session session) {
        if (session != null) {
            if (this.sortServices == null) {
                this.logger.error("sortServices null");
                return;
            }
            for (int i = 0; i < this.sortServices.length; ++i) {
                this.sortServices[i].createSession(session);
            }
        }
    }

    public void freeSession(final Session session) {
        if (this.sortServices == null) {
            this.logger.error("sortServices null");
            return;
        }
        if (session != null) {
            for (int i = this.sortServices.length - 1; i >= 0; --i) {
                try {
                    this.sortServices[i].sessionFree(session);
                } catch (Exception e) {
                    this.logger.error("service free session error \uff1a" + this.sortServices[i].getName());
                    e.printStackTrace();
                }
            }
        } else {
            this.logger.error("free empty session");
        }
    }

    public void clean(final int userID) {
        for (int i = this.sortServices.length - 1; i >= 0; --i) {
            try {
                this.sortServices[i].clean(userID);
            } catch (Exception e) {
                this.logger.error("service free session error \uff1a" + this.sortServices[i].getName());
                e.printStackTrace();
            }
        }
    }

    public void dbUpdate(final int userID) {
        for (final AbsService<AbsConfig> s : this.serviceMap.values()) {
            s.dbUpdate(userID);
        }
    }

    private boolean check(final int id, final String name) {
        for (final AbsService s : this.serviceMap.values()) {
            if (s.getID() == id || s.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    private class SortService implements Comparable<SortService> {

        int id;
        int index;
        String className;
        String xml;

        @Override
        public int compareTo(final SortService obj) {
            if (this.index < obj.index) {
                return -1;
            }
            if (this.index > obj.index) {
                return 1;
            }
            return 0;
        }
    }
}
