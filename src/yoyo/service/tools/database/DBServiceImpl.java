// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.tools.database;

import java.sql.DriverManager;
import java.sql.Connection;
import org.logicalcobwebs.proxool.admin.SnapshotIF;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import yoyo.service.MonitorEvent;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.packet.ContextData;
import yoyo.core.event.AbsEvent;
import yoyo.service.base.IService;
import yoyo.service.base.AbsServiceAdaptor;

public class DBServiceImpl extends AbsServiceAdaptor<DBConfig> implements IService {

    private static DBServiceImpl instance;

    static {
        DBServiceImpl.instance = null;
    }

    private DBServiceImpl() {
        this.config = new DBConfig();
    }

    public static DBServiceImpl getInstance() {
        if (DBServiceImpl.instance == null) {
            DBServiceImpl.instance = new DBServiceImpl();
        }
        return DBServiceImpl.instance;
    }

    @Override
    public void onEvent(final AbsEvent event) {
    }

    @Override
    public AbsClientProcess getClientProcess(final ContextData data) {
        return null;
    }

    @Override
    public AbsEvent montior() {
        MonitorEvent event = new MonitorEvent(this.getName());
        SnapshotIF snapShot = null;
        try {
            snapShot = ProxoolFacade.getSnapshot(((DBConfig) this.config).dbPoolName);
        } catch (ProxoolException e) {
            e.printStackTrace();
        }
        event.put("Active Connection Count", String.valueOf(snapShot.getActiveConnectionCount()));
        event.put("Avaliable Connection Count", String.valueOf(snapShot.getAvailableConnectionCount()));
        event.put("Connection Count", String.valueOf(snapShot.getConnectionCount()));
        event.put("Maxximum Connection Count", String.valueOf(snapShot.getMaximumConnectionCount()));
        event.put("Offline Connection Count", String.valueOf(snapShot.getOfflineConnectionCount()));
        event.put("Refuse Count", String.valueOf(snapShot.getRefusedCount()));
        event.put("Served Count", String.valueOf(snapShot.getServedCount()));
        return event;
    }

    @Override
    protected void start() {
    }

    public final Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(((DBConfig) this.config).proxpoolName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return con;
    }
}
