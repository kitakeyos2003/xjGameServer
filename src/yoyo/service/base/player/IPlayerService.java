// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.player;

import yoyo.service.base.IService;

public interface IPlayerService extends IService {

    IPlayer getPlayerByUserID(final int p0);

    IPlayer getPlayerBySessionID(final int p0);

    IPlayer getPlayerByName(final String p0);

    byte[] listRole(final int[] p0);

    byte[] createRole(final int p0, final short p1, final int p2, final String[] p3);

    int deleteRole(final int p0);

    IPlayerDAO getDAO();
}
