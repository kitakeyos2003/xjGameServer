// 
// Decompiled by Procyon v0.5.36
// 
package hero.login;

import yoyo.service.base.session.SessionServiceImpl;
import hero.player.service.PlayerServiceImpl;
import hero.share.service.LogWriter;
import java.rmi.Remote;
import java.rmi.Naming;
import hero.login.rmi.YOYOLoginRMIImpl;
import java.rmi.registry.LocateRegistry;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class LoginServiceImpl extends AbsServiceAdaptor<LoginConfig> {

    private static Logger log;
    private static LoginServiceImpl instance;

    static {
        LoginServiceImpl.log = Logger.getLogger((Class) LoginServiceImpl.class);
    }

    private LoginServiceImpl() {
        this.config = new LoginConfig();
    }

    public static LoginServiceImpl getInstance() {
        if (LoginServiceImpl.instance == null) {
            LoginServiceImpl.instance = new LoginServiceImpl();
        }
        return LoginServiceImpl.instance;
    }

    @Override
    protected void start() {
        try {
            LocateRegistry.createRegistry(((LoginConfig) this.config).rmi_port);
            Naming.rebind(((LoginConfig) this.config).rmi_url, new YOYOLoginRMIImpl());
            YOYOLoginRMIImpl objImpl = new YOYOLoginRMIImpl();
            LogWriter.println("RMI\u6ce8\u518c\u6210\u529f");
        } catch (Exception e) {
            e.printStackTrace();
            LogWriter.error(this, e);
        }
    }

    public byte[] createRole(final int _accountID, final short _serverID, final int _userID, final String[] _paras) {
        return PlayerServiceImpl.getInstance().createRole(_accountID, _serverID, _userID, _paras);
    }

    public byte[] listDefaultRole() {
        return PlayerServiceImpl.getInstance().listDefaultRole();
    }

    public byte[] listRole(final int[] _userIDArray) {
        return PlayerServiceImpl.getInstance().listRole(_userIDArray);
    }

    public int deleteRole(final int _userID) {
        return PlayerServiceImpl.getInstance().deleteRole(_userID);
    }

    public boolean resetPlayersStatus(final int _accountID) {
        System.out.println("\u5f00\u59cb\u5f3a\u884c\u628a\u8d26\u53f7ID=" + _accountID + "\u4e0a\u7684\u5728\u7ebf\u89d2\u8272\u8e22\u4e0b\u7ebf");
        SessionServiceImpl.getInstance().freeSessionByAccountID(_accountID);
        return true;
    }

    public int login(final int _userID, final int _accountID) {
        int session = 0;
        try {
            LoginServiceImpl.log.info((Object) ("\u73a9\u5bb6\u767b\u9646:" + _userID));
            LoginServiceImpl.log.info((Object) ("_accountID:" + _accountID));
            session = SessionServiceImpl.getInstance().createSession(_userID, _accountID);
        } catch (Exception e) {
            LoginServiceImpl.log.info((Object) "\u51fa\u73b0\u4e25\u91cd\u95ee\u9898,\u521b\u5efasessionid\u5931\u8d25");
            e.printStackTrace();
        }
        LoginServiceImpl.log.info((Object) ("\u8fd4\u56desessionid:" + session));
        return session;
    }

    public int getOnlinePlayerNumber() {
        return PlayerServiceImpl.getInstance().getPlayerNumber();
    }
}
