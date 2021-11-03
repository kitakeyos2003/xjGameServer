// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base;

import org.apache.commons.lang.StringUtils;
import yoyo.service.base.session.Session;
import yoyo.core.event.AbsEvent;
import yoyo.core.packet.ContextData;
import java.util.Iterator;
import java.util.List;
import yoyo.core.process.AbsClientProcess;
import yoyo.service.ServiceManager;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Element;
import javolution.util.FastMap;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.slf4j.Logger;

public abstract class AbsService<T extends AbsConfig> {

    private final Logger logger;
    protected T config;
    protected AbsPolicy policy;
    protected Map<Integer, Class> cpClassMap;
    private ServiceInfo info;

    public AbsService() {
        this.logger = LoggerFactory.getLogger((Class) this.getClass());
        this.cpClassMap = (Map<Integer, Class>) new FastMap();
    }

    public T getConfig() {
        return this.config;
    }

    public void initService(final Element element) throws Exception {
        this.defaultConfig(element);
        if (this.config != null) {
            this.config.init(element);
        }
        this.start();
        this.logger.info(this.info.toString());
    }

    private void defaultConfig(final Element element) throws Exception {
        Element eService = element.element("service");
        this.info = new ServiceInfo();
        this.info.id = NumberUtils.createInteger(eService.elementTextTrim("id"));
        this.info.name = eService.elementTextTrim("name");
        this.info.version = eService.elementTextTrim("version");
        this.info.author = eService.elementTextTrim("author");
        this.info.description = eService.elementTextTrim("description");
        List list = eService.selectNodes("//dependencies/ssn");
        if (list != null && list.size() > 0) {
            this.info.ssnDp = new int[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                Element eSsn = (Element) list.get(i);
                this.info.ssnDp[i] = NumberUtils.createInteger(eSsn.attributeValue("id"));
            }
        }
        Element eChs = element.element("clientprocesses");
        if (eChs == null) {
            return;
        }
        Iterator it = eChs.elementIterator();
        this.cpClassMap = (Map<Integer, Class>) new FastMap();
        while (it.hasNext()) {
            Element eCH = (Element) it.next();
            String className = eCH.getTextTrim();
            String sId = eCH.attributeValue("id");
            ClassLoader loader = ServiceManager.class.getClassLoader();
            int id = NumberUtils.createInteger(sId);
            if (this.info.id != (id >> 8 & 0xFF)) {
                throw new RuntimeException("clientProcess id " + className + " is not correct!");
            }
            id &= 0xFF;
            Class c = loader.loadClass(className);
            if (c.getSuperclass() != AbsClientProcess.class) {
                throw new RuntimeException(String.valueOf(className) + "not extends ClientProcess");
            }
            this.cpClassMap.put(id, c);
        }
    }

    public String getName() {
        return this.info.name;
    }

    public int getID() {
        return this.info.id;
    }

    public int[] getSessionDps() {
        return this.info.ssnDp;
    }

    public AbsClientProcess getClientProcess(final ContextData data) throws InstantiationException, IllegalAccessException {
        Class c = this.cpClassMap.get(data.messageType);
        if (c != null) {
            return (AbsClientProcess) c.newInstance();
        }
        return null;
    }

    protected abstract void start();

    public abstract void onEvent(final AbsEvent p0);

    public abstract void createSession(final Session p0);

    public abstract void sessionFree(final Session p0);

    public abstract void dbUpdate(final int p0);

    public abstract AbsEvent montior();

    public abstract void clean(final int p0);

    private class ServiceInfo {

        int id;
        String name;
        String version;
        String description;
        String author;
        int[] ssnDp;

        @Override
        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("id:" + this.id + "\n");
            buf.append("name:" + this.name + "\n");
            if (!StringUtils.isBlank(this.version)) {
                buf.append("version:" + this.version + "\n");
            }
            if (!StringUtils.isBlank(this.author)) {
                buf.append("author:" + this.author + "\n");
            }
            if (!StringUtils.isBlank(this.description)) {
                buf.append("description:" + this.description + "\n");
            }
            return buf.toString();
        }
    }
}
