// 
// Decompiled by Procyon v0.5.36
// 
package hero.duel.service;

import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class PvpServerConfig extends AbsConfig {

    public byte duel_time_alert_interval;
    public byte duel_count_down;
    public short duel_sum_time;

    @Override
    public void init(final Element _xmlNode) throws Exception {
        Element element = _xmlNode.element("duelconfig");
        this.duel_time_alert_interval = Byte.parseByte(element.elementTextTrim("duel_time_alert_interval"));
        this.duel_count_down = Byte.parseByte(element.elementTextTrim("duel_count_down"));
        this.duel_sum_time = Short.valueOf(element.elementTextTrim("duel_sum_time"));
    }
}
