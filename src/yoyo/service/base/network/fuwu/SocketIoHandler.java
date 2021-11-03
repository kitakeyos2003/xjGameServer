// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.fuwu;

import org.apache.mina.core.session.IdleStatus;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.packet.ResponseData;
import yoyo.service.base.session.Session;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.core.threadpool.TaskThreadPool;
import yoyo.service.Priority;
import java.net.InetSocketAddress;
import yoyo.service.ServiceManager;
import yoyo.service.PriorityManager;
import yoyo.service.base.session.SessionServiceImpl;
import yoyo.core.packet.ContextData;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;

public class SocketIoHandler extends IoHandlerAdapter {

    private final Logger logger;

    public SocketIoHandler() {
        this.logger = LoggerFactory.getLogger((Class) this.getClass());
    }

    public void messageReceived(final IoSession ioSession, final Object message) throws Exception {
        ContextData[] cds = (ContextData[]) message;
        int sessionID = cds[0].sessionID;
        Session session = SessionServiceImpl.getInstance().getSession(sessionID);
        ResponseData rd = null;
        if (session != null) {
            session.refreshTime = System.currentTimeMillis();
            int len = cds.length;
            ContextData[] array;
            for (int length = (array = cds).length, i = 0; i < length; ++i) {
                ContextData cd = array[i];
                Priority priority = PriorityManager.getInstance().getPriorityByMsgId(cd.messageID);
                AbsClientProcess ch = ServiceManager.getInstance().getClientProcess(cd);
                try {
                    InetSocketAddress address = (InetSocketAddress) ioSession.getRemoteAddress();
                    if (address != null) {
                        String ip = address.getAddress().getHostAddress();
                        ch.setIp(ip);
                    }
                } catch (Exception e2) {
                    this.logger.error("get IP error,accountID=" + session.accountID + ",nickname=" + session.nickName + ",userID=" + session.userID);
                }
                if (priority == Priority.REAL_TIME) {
                    ch.run();
                } else {
                    TaskThreadPool.getInstance().addTask(ch);
                }
            }
            rd = ResponseMessageQueue.getInstance().get(session.index);
            if (rd != null) {
                if (rd.isErrorMessage()) {
                    this.logger.warn("sessionID = " + sessionID + "; nickname=" + session.nickName + "\u7684\u7528\u6237\u88ab\u901a\u77e5\u6389\u7ebf,\u4f46\u4ed6\u7684session\u5e76\u4e0d\u4e3aNULL");
                }
            } else {
                this.logger.error("\u4ece\u6d88\u606f\u5bf9\u8c61get\u51fa\u6765\u7684\u6d88\u606f\u4e3anull,\u5e94\u8be5\u5c3d\u5feb\u6392\u9664\u8fd9\u6837\u7684\u60c5\u51b5:" + session.index);
            }
        } else {
            this.logger.info("session is null sessionid=" + String.valueOf(sessionID));
            try {
                rd = ResponseMessageQueue.getInstance().getErrorData();
                String remoteIP = ((InetSocketAddress) ioSession.getRemoteAddress()).getAddress().getHostAddress();
                this.logger.warn("!!!to yoyo-->\u901a\u8fc7sessionID\u83b7\u5f97session\u4e3anull. sessionID=" + sessionID + "\u4ed6\u7684IP:" + remoteIP);
                ioSession.closeOnFlush();
            } catch (Exception e) {
                this.logger.error("messageReceived error:", (Throwable) e);
                e.printStackTrace();
            }
        }
        rd.setSessionID(sessionID);
        ioSession.write((Object) rd);
    }

    public void exceptionCaught(final IoSession session, final Throwable ex) throws Exception {
        session.close();
    }

    public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception {
        session.close();
    }

    public void sessionClosed(final IoSession session) throws Exception {
        session.close();
    }

    public void sessionCreated(final IoSession session) throws Exception {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 120);
    }
}
