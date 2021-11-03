// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.fuwu;

import org.apache.mina.transport.socket.SocketAcceptor;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import yoyo.service.base.network.wrap.YOYOCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.core.service.IoHandler;
import yoyo.service.base.network.NetworkConfig;

public class SocketServer extends AbsYOYOServer {

    @Override
    public void start(final NetworkConfig.ConfigInfo config) throws Exception {
        this.port = config.getPort();
        this.ioHandler = (IoHandler) Class.forName(config.getProcess()).newInstance();
        this.acceptor = (SocketAcceptor) new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
        this.acceptor.getFilterChain().addLast("protocol", (IoFilter) new ProtocolCodecFilter((ProtocolCodecFactory) new YOYOCodecFactory(config)));
        this.acceptor.setHandler(this.ioHandler);
        this.acceptor.getSessionConfig().setSoLinger(-1);
        this.acceptor.bind((SocketAddress) new InetSocketAddress(this.port));
        System.out.println("SocketServer listen on port " + this.port);
    }

    @Override
    public String getServerName() {
        return "SocketServer";
    }
}
