// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.player;

import yoyo.service.base.session.Session;

public interface IPlayer {

    void init();

    int getMsgQueueIndex();

    void setSession(final Session p0);

    int getSessionID();

    void free();
}
