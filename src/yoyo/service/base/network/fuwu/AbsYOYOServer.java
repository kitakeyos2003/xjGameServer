// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.fuwu;

import yoyo.service.base.network.NetworkConfig;
import yoyo.service.MonitorEvent;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.transport.socket.SocketAcceptor;

public abstract class AbsYOYOServer {

    public static final byte SOCKET = 1;
    public static final byte HTTP = 2;
    protected int port;
    protected SocketAcceptor acceptor;
    protected IoHandler ioHandler;

    public void monitor(final MonitorEvent event) {
        StringBuffer sb = new StringBuffer();
        sb.append("ReadMessages:" + this.acceptor.getReadMessages() + "\n");
        sb.append("WritternMessage:" + this.acceptor.getWrittenMessages() + "\n");
        sb.append("ManagedSessionCount:" + this.acceptor.getManagedSessionCount() + "\n");
        sb.append("ReadBytesThroughput:" + this.acceptor.getReadBytesThroughput() + "byte/s" + "\n");
        sb.append("WrittenBytesThroughput:" + this.acceptor.getWrittenBytesThroughput() + "byte/s" + "\n");
        event.put(this.getServerName(), sb.toString());
    }

    public abstract void start(final NetworkConfig.ConfigInfo p0) throws Exception;

    public abstract String getServerName();
}
