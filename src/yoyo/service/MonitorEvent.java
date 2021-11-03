// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service;

import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import yoyo.core.event.AbsEvent;

public class MonitorEvent extends AbsEvent<Map<String, String>> {

    public MonitorEvent(final String src) {
        this.src = src;
        this.context = new HashMap();
    }

    public void put(final String key, final String value) {
        ((Map) this.context).put(key, value);
    }

    public void putAll(final Map<String, String> map) {
        ((Map) this.context).putAll(map);
    }

    @Override
    public String toString() {
        Set<Map.Entry<String, String>> set = ((Map) this.context).entrySet();
        StringBuffer buffer = new StringBuffer();
        buffer.append("\n" + this.src + ":\n");
        for (final Map.Entry entry : set) {
            buffer.append(entry.getKey());
            buffer.append(":");
            buffer.append(entry.getValue());
            buffer.append("\n");
        }
        return buffer.toString();
    }
}
