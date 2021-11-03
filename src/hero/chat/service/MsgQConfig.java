// 
// Decompiled by Procyon v0.5.36
// 
package hero.chat.service;

import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class MsgQConfig extends AbsConfig {

    private int maxPri;
    private int maxWorld;
    private int maxItem;
    private long timeOut;

    public MsgQConfig() {
        this.maxPri = 2000;
        this.maxWorld = 300;
        this.maxItem = 2000;
        this.timeOut = 5000L;
    }

    public int getMaxPri() {
        return this.maxPri;
    }

    public int getMaxWorld() {
        return this.maxWorld;
    }

    public int getMaxItem() {
        return this.maxItem;
    }

    public long getTimeOut() {
        return this.timeOut;
    }

    @Override
    public void init(final Element root) throws Exception {
        String sMaxPri = root.valueOf("//chatservice/maxPri");
        if (sMaxPri != null && sMaxPri.equals("")) {
            this.maxPri = Integer.parseInt(sMaxPri);
        }
        String sMaxWorld = root.valueOf("//chatservice/maxWorld");
        if (sMaxWorld != null && sMaxWorld.equals("")) {
            this.maxWorld = Integer.parseInt(sMaxWorld);
        }
        String sMaxItem = root.valueOf("//chatservice/maxItem");
        if (sMaxItem != null && sMaxItem.equals("")) {
            this.maxItem = Integer.parseInt(sMaxItem);
        }
        String sTimeOut = root.valueOf("//chatservice/timeOut");
        if (sTimeOut != null && sTimeOut.equals("")) {
            this.timeOut = Integer.parseInt(sTimeOut);
        }
    }
}
