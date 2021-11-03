// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.net;

import hero.share.service.LogWriter;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import hero.charge.net.handler.ChargeHttpHandler;
import hero.charge.service.ChargeServiceImpl;
import hero.charge.service.ChargeConfig;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.transport.socket.SocketAcceptor;

public class ServerSocket {

    private static ServerSocket instance;
    protected SocketAcceptor acceptor;
    protected IoHandler handler;

    public static ServerSocket getInstance() {
        if (ServerSocket.instance == null) {
            ServerSocket.instance = new ServerSocket();
        }
        return ServerSocket.instance;
    }

    public void start() {
        try {
            int port = ChargeServiceImpl.getInstance().getConfig().port_callback;
            this.handler = (IoHandler) new ChargeHttpHandler();
            this.acceptor = (SocketAcceptor) new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
            this.acceptor.getFilterChain().addLast("protocol", (IoFilter) new ProtocolCodecFilter((ProtocolCodecFactory) new ChargeCodeFactory()));
            this.acceptor.setHandler(this.handler);
            this.acceptor.getSessionConfig().setSoLinger(-1);
            this.acceptor.bind((SocketAddress) new InetSocketAddress(port));
            LogWriter.println("Charge callback httpserver is listenig at port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
