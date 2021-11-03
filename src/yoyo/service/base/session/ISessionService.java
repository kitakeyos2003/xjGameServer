// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.session;

import yoyo.service.base.IService;

public interface ISessionService extends IService {

    int createSession(final int p0, final int p1);

    void initSession(final Session p0);

    int getIndexByUserID(final int p0);

    int getIndexBySessionID(final int p0);

    void freeSession(final Session p0);

    void freeSessionByAccountID(final int p0);
}
