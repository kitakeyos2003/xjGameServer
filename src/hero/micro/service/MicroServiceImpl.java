// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.service;

import hero.micro.teach.MasterApprentice;
import hero.micro.store.PersionalStore;
import hero.player.HeroPlayer;
import hero.micro.teach.TeachService;
import hero.micro.store.StoreService;
import hero.player.service.PlayerServiceImpl;
import yoyo.service.base.session.Session;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class MicroServiceImpl extends AbsServiceAdaptor<MicroConfig> {

    private static Logger log;
    private static MicroServiceImpl instance;

    static {
        MicroServiceImpl.log = Logger.getLogger((Class) MicroServiceImpl.class);
    }

    private MicroServiceImpl() {
    }

    public static MicroServiceImpl getInstance() {
        if (MicroServiceImpl.instance == null) {
            MicroServiceImpl.instance = new MicroServiceImpl();
        }
        return MicroServiceImpl.instance;
    }

    @Override
    public void createSession(final Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
        StoreService.login(player);
        TeachService.login(player);
    }

    @Override
    public void sessionFree(final Session _session) {
        TeachService.logout(_session.userID);
    }

    @Override
    public void clean(final int _userID) {
        StoreService.clear(_userID);
        TeachService.clear(_userID);
    }

    public void start() {
    }

    public PersionalStore getStore(final int _userID) {
        return StoreService.get(_userID);
    }

    public MasterApprentice getMasterApprentice(final int _userID) {
        return TeachService.get(_userID);
    }

    public String getMasterName(final HeroPlayer _user) {
        String name = "";
        MasterApprentice m = null;
        if (_user.isEnable()) {
            m = TeachService.get(_user.getUserID());
        } else {
            m = TeachService.getOffLineMasterApprentice(_user.getName());
        }
        if (m != null && m.masterName != null && !m.masterName.equals(_user.getName())) {
            name = m.masterName;
        }
        return name;
    }

    public String getApprenticeNameList(final HeroPlayer _user) {
        String name = "";
        MasterApprentice m = null;
        if (_user.isEnable()) {
            m = TeachService.get(_user.getUserID());
        } else {
            m = TeachService.getOffLineMasterApprentice(_user.getName());
        }
        if (m != null && m.apprenticeList != null && m.apprenticeList.length > 0) {
            for (int i = 0; i < m.apprenticeList.length; ++i) {
                if (m.apprenticeList[i] != null) {
                    name = String.valueOf(name) + m.apprenticeList[i].name + ", ";
                    if (name.equals(_user.getName())) {
                        name = "";
                        break;
                    }
                }
            }
            if (name.length() > 2) {
                name = name.substring(0, name.length() - 2);
            }
        }
        return name;
    }
}
