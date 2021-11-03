// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.tools;

import java.util.Iterator;
import yoyo.core.process.AbsClientProcess;
import yoyo.service.ServiceManager;
import javolution.util.FastMap;
import java.util.Map;
import org.dom4j.Element;

public class CPTool {

    public static Map<Integer, Class> loadCPMap(final Element element) throws Exception {
        Element root = element.element("clientprocesses");
        Iterator it = root.elementIterator();
        Map<Integer, Class> map = (Map<Integer, Class>) new FastMap();
        while (it.hasNext()) {
            Element eCH = (Element) it.next();
            String className = eCH.getTextTrim();
            String sId = eCH.attributeValue("id");
            ClassLoader loader = ServiceManager.class.getClassLoader();
            int id = Integer.valueOf(sId);
            Class c = loader.loadClass(className);
            if (c.getSuperclass() != AbsClientProcess.class) {
                throw new RuntimeException(String.valueOf(className) + " not extedns ClientProcess");
            }
            map.put(id, c);
        }
        return map;
    }

    public static Class loadCP(final Element element) throws Exception {
        String className = element.valueOf("//clientprocesses/clientprocess");
        if (className == null || className.equals("")) {
            throw new Exception(String.valueOf(element.getName()) + " not define ClientProcess");
        }
        ClassLoader loader = ServiceManager.class.getClassLoader();
        Class c = loader.loadClass(className);
        if (c.getSuperclass() == AbsClientProcess.class) {
            return c;
        }
        throw new RuntimeException(String.valueOf(className) + " not extedns ClientProcess");
    }
}
