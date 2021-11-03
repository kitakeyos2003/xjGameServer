// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network;

import yoyo.service.MonitorEvent;
import yoyo.core.event.AbsEvent;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.packet.ContextData;
import yoyo.service.base.network.fuwu.AbsYOYOServer;
import yoyo.service.base.AbsServiceAdaptor;

public class NetworkServiceImpl extends AbsServiceAdaptor<NetworkConfig> {

    private static NetworkServiceImpl instance;
    private AbsYOYOServer[] servers;

    private NetworkServiceImpl() {
        this.config = new NetworkConfig();
    }

    public static NetworkServiceImpl getInstance() {
        if (NetworkServiceImpl.instance == null) {
            NetworkServiceImpl.instance = new NetworkServiceImpl();
        }
        return NetworkServiceImpl.instance;
    }

    @Override
    protected void start() {
        try {
            this.servers = new AbsYOYOServer[((NetworkConfig) this.config).getServerCount()];
            for (int i = 0; i < this.servers.length; ++i) {
                NetworkConfig.ConfigInfo conf = ((NetworkConfig) this.config).configs[i];
                (this.servers[i] = (AbsYOYOServer) Class.forName(conf.getServer()).newInstance()).start(conf);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (ClassNotFoundException e3) {
            e3.printStackTrace();
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }

    @Override
    public AbsClientProcess getClientProcess(final ContextData data) {
        return null;
    }

    @Override
    public AbsEvent montior() {
        MonitorEvent event = new MonitorEvent(this.getName());
        AbsYOYOServer[] servers;
        for (int length = (servers = this.servers).length, i = 0; i < length; ++i) {
            AbsYOYOServer server = servers[i];
            server.monitor(event);
        }
        return event;
    }

    @Override
    public void onEvent(final AbsEvent event) {
    }
}
