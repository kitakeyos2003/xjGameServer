// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.service;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import yoyo.service.base.session.Session;
import yoyo.service.base.AbsServiceAdaptor;

public class BbsServiceImpl extends AbsServiceAdaptor<BbsConfig> {

    private static BbsServiceImpl instance;

    public static BbsServiceImpl getInstance() {
        if (BbsServiceImpl.instance == null) {
            BbsServiceImpl.instance = new BbsServiceImpl();
        }
        return BbsServiceImpl.instance;
    }

    @Override
    public void dbUpdate(final int _userID) {
    }

    @Override
    public void createSession(final Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
    }

    @Override
    public void sessionFree(final Session _session) {
    }

    @Override
    public void clean(final int _userID) {
    }

    @Override
    protected void start() {
    }
}
