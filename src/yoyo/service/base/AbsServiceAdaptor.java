// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base;

import yoyo.service.base.session.Session;
import yoyo.service.MonitorEvent;
import yoyo.core.event.AbsEvent;

public abstract class AbsServiceAdaptor<T extends AbsConfig> extends AbsService<T> {

    @Override
    public AbsEvent montior() {
        MonitorEvent event = new MonitorEvent(this.getName());
        return event;
    }

    @Override
    public void onEvent(final AbsEvent event) {
    }

    @Override
    protected void start() {
    }

    @Override
    public void createSession(final Session seesion) {
    }

    @Override
    public void sessionFree(final Session seesion) {
    }

    @Override
    public void dbUpdate(final int userID) {
    }

    @Override
    public void clean(final int userID) {
    }
}
