// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.session;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class SessionConfig extends AbsConfig {

    public int checkInterval;
    public int maxUnActiveTime;

    public SessionConfig() {
        this.checkInterval = 5000;
        this.maxUnActiveTime = 45000;
    }

    @Override
    public void init(final Element element) throws Exception {
        String sCheckInterval = element.valueOf("//checkinterval");
        if (!StringUtils.isBlank(sCheckInterval)) {
            this.checkInterval = Integer.valueOf(sCheckInterval);
        }
        String sMaxUnActiveTime = element.valueOf("//maxunactivetime");
        if (!StringUtils.isBlank(sMaxUnActiveTime)) {
            this.maxUnActiveTime = Integer.valueOf(sMaxUnActiveTime);
        }
    }
}
