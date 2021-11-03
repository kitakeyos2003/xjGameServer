// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.tools;

import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.lang.reflect.Field;
import java.beans.Introspector;
import java.util.Iterator;
import java.lang.reflect.Array;
import org.dom4j.Element;

public class BeanCreator {

    public static Object createBean(final Element element, final Object upper, final String type) {
        try {
            if (isBaseType(type)) {
                return createBaseType(element, type);
            }
            if (type.contains("[]")) {
                return createArrayObject(element, upper, type);
            }
            return createObject(element, upper, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static boolean isBaseType(final String type) {
        return "int".equals(type) || "double".equals(type) || "boolean".equals(type) || "String".equals(type) || "float".equals(type) || "short".equals(type) || "byte".equals(type);
    }

    private static Class getClass(final String type) throws Exception {
        if ("int".equals(type)) {
            return Integer.TYPE;
        }
        if ("double".equals(type)) {
            return Double.TYPE;
        }
        if ("boolean".equals(type)) {
            return Boolean.TYPE;
        }
        if ("String".equals(type)) {
            return String.class;
        }
        if ("float".equals(type)) {
            return Float.TYPE;
        }
        if ("short".equals(type)) {
            return Short.TYPE;
        }
        if ("byte".equals(type)) {
            return Byte.TYPE;
        }
        return Class.forName(type);
    }

    private static Object createBaseType(final Element element, final String type) {
        String ret = element.getTextTrim();
        if ("int".equals(type)) {
            return Integer.parseInt(ret);
        }
        if ("double".equals(type)) {
            return Double.parseDouble(ret);
        }
        if ("boolean".equals(type)) {
            return Boolean.parseBoolean(ret);
        }
        if ("float".equals(type)) {
            return Float.parseFloat(ret);
        }
        if ("short".equals(type)) {
            return Short.parseShort(ret);
        }
        if ("byte".equals(type)) {
            return Byte.parseByte(ret);
        }
        return ret;
    }

    private static Object createArrayObject(final Element element, final Object upper, final String type) throws Exception {
        String componentType = type.split("\\[]")[0];
        Object array = Array.newInstance(getClass(componentType), element.elements().size());
        Iterator it = element.elementIterator();
        int i = 0;
        while (it.hasNext()) {
            Element subElement = (Element) it.next();
            Array.set(array, i++, createBean(subElement, upper, subElement.attributeValue("type")));
        }
        return array;
    }

    private static Object createObject(final Element element, final Object upper, final String type) throws Exception {
        Class objc = null;
        Object obj = null;
        if (type.contains("$")) {
            Class[] cs = upper.getClass().getClasses();
            Class[] array;
            for (int length = (array = cs).length, i = 0; i < length; ++i) {
                Class c = array[i];
                if (c.getName().equals(type)) {
                    obj = c.getConstructor(upper.getClass()).newInstance(upper);
                    objc = c;
                }
            }
        } else if (upper != null) {
            objc = upper.getClass().getClassLoader().loadClass(type);
        } else {
            objc = Class.forName(type);
        }
        if (objc.isEnum()) {
            Object[] objs = objc.getEnumConstants();
            String value = element.getTextTrim();
            int eId = Integer.parseInt(value);
            Object[] array2;
            for (int length2 = (array2 = objs).length, j = 0; j < length2; ++j) {
                Object b = array2[j];
                int id = (int) objc.getMethod("ordinal", (Class[]) null).invoke(b, (Object[]) null);
                if (id == eId) {
                    return b;
                }
            }
            return null;
        }
        obj = objc.newInstance();
        Iterator it = element.elementIterator();
        while (it.hasNext()) {
            Element subElement = (Element) it.next();
            Object value2 = createBean(subElement, obj, subElement.attributeValue("type"));
            try {
                Field field = objc.getField(subElement.getName());
                field.set(obj, value2);
            } catch (NoSuchFieldException e) {
                BeanInfo bInfo = Introspector.getBeanInfo(objc, Object.class);
                boolean isField = false;
                PropertyDescriptor[] propertyDescriptors;
                for (int length3 = (propertyDescriptors = bInfo.getPropertyDescriptors()).length, k = 0; k < length3; ++k) {
                    PropertyDescriptor property = propertyDescriptors[k];
                    if (property.getName().equalsIgnoreCase(subElement.getName())) {
                        property.getWriteMethod().invoke(obj, value2);
                        isField = true;
                        break;
                    }
                }
                if (!isField) {
                    throw new NoSuchFieldException(e.toString());
                }
                continue;
            }
        }
        return obj;
    }
}
