// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.player;

public interface IPlayerDAO {

    IPlayer load(final int p0);

    byte[] listRole(final int[] p0);

    byte[] createRole(final int p0, final short p1, final int p2, final String[] p3);

    int deleteRole(final int p0);

    void updateDB(final IPlayer p0);
}
