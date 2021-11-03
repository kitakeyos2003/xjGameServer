// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.tools.log;

import yoyo.service.MonitorEvent;
import yoyo.service.ServiceManager;
import java.util.TimerTask;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.packet.ContextData;
import yoyo.core.event.AbsEvent;
import org.apache.log4j.Logger;
import java.util.Timer;
import yoyo.service.base.IService;
import yoyo.service.base.AbsServiceAdaptor;

public class SystemLogServiceImpl extends AbsServiceAdaptor<SystemLogManager> implements IService {

    private static SystemLogServiceImpl instance;
    private Timer timer;

    private SystemLogServiceImpl() {
        this.config = new SystemLogManager();
        this.timer = new Timer();
    }

    public static SystemLogServiceImpl getInstance() {
        if (SystemLogServiceImpl.instance == null) {
            SystemLogServiceImpl.instance = new SystemLogServiceImpl();
        }
        return SystemLogServiceImpl.instance;
    }

    public Logger getLoggerByName(final String name) {
        if (name == null || name == "") {
            return ((SystemLogManager) this.config).getLoggerByName("root");
        }
        return ((SystemLogManager) this.config).getLoggerByName(name);
    }

    @Override
    public AbsEvent montior() {
        return null;
    }

    @Override
    public AbsClientProcess getClientProcess(final ContextData data) {
        return null;
    }

    @Override
    public void onEvent(final AbsEvent event) {
        String logname = ((SystemLogEvent) event).getLogName();
        Logger logger = this.getLoggerByName(logname);
        logger.debug(event.getContext());
    }

    @Override
    protected void start() {
        this.timer.schedule(new MonitorTask(), 60000L, 60000L);
    }

    private class MonitorTask extends TimerTask {

        @Override
        public void run() {
            Logger logger = SystemLogServiceImpl.this.getLoggerByName("monitor");
            logger.info((Object) "<--------------");
            MonitorEvent[] events = ServiceManager.getInstance().monitor();
            if (events != null) {
                MonitorEvent[] array;
                for (int length = (array = events).length, i = 0; i < length; ++i) {
                    MonitorEvent evt = array[i];
                    if (evt != null) {
                        logger.info((Object) evt.toString());
                    }
                }
            }
            logger.info((Object) "-------------->");
        }
    }
}
