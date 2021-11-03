// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.core.process;

import yoyo.tools.YOYOInputStream;
import yoyo.core.packet.ContextData;

public abstract class AbsClientProcess implements Runnable {

    protected ContextData contextData;
    protected YOYOInputStream yis;
    private String ip;

    public AbsClientProcess() {
        this.ip = "";
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(final String ip) {
        this.ip = ip;
    }

    public int getMessageType() {
        return this.contextData.messageType;
    }

    public void init(final ContextData data) {
        this.contextData = data;
        this.yis = new YOYOInputStream(data.context);
    }

    @Override
    public void run() {
        try {
            this.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void read() throws Exception;
}
