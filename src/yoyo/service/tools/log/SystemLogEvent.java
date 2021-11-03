// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.tools.log;

import yoyo.core.event.AbsEvent;

public class SystemLogEvent extends AbsEvent {

    private String logName;

    public SystemLogEvent() {
        this.setDest("logservice");
    }

    public String getLogName() {
        return this.logName;
    }

    public void setLogName(final String logName) {
        this.logName = logName;
    }
}
